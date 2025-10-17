package controller;

import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.MaterialDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.Material;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import utils.EmailUtils;
import utils.ExportRequestValidator;

@WebServlet(name = "CreateExportRequestServlet", urlPatterns = {"/CreateExportRequest"})
public class CreateExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;
    private MaterialDAO materialDAO;
    private UserDAO userDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
        materialDAO = new MaterialDAO();
        userDAO = new UserDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_EXPORT_REQUEST");
        request.setAttribute("hasCreateExportRequestPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create export requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        try {
            String requestCode = generateRequestCode();
            request.setAttribute("requestCode", requestCode);
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            request.setAttribute("materials", materials);
            List<User> staffUsers = userDAO.getUsersByRoleId(3);
            request.setAttribute("users", staffUsers);
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("materials", new ArrayList<Material>());
            request.setAttribute("users", new ArrayList<User>());
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_EXPORT_REQUEST");
        request.setAttribute("hasCreateExportRequestPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create export requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        try {
            String requestCode = request.getParameter("requestCode");
            String deliveryDateStr = request.getParameter("deliveryDate");
            String reason = request.getParameter("reason");
            
            // Validate form data using ExportRequestValidator
            Map<String, String> formErrors = ExportRequestValidator.validateExportRequestFormData(reason, deliveryDateStr);
            
            if (!formErrors.isEmpty()) {
                // Set submitted data to repopulate form
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedDeliveryDate", deliveryDateStr);
                request.setAttribute("errors", formErrors);
                
                // Reload form data
                String newRequestCode = generateRequestCode();
                request.setAttribute("requestCode", newRequestCode);
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                request.setAttribute("materials", materials);
                List<User> staffUsers = userDAO.getUsersByRoleId(3);
                request.setAttribute("users", staffUsers);
                
                request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
                return;
            }
            
            Date deliveryDate = Date.valueOf(deliveryDateStr);
            ExportRequest exportRequest = new ExportRequest();
            exportRequest.setRequestCode(requestCode);
            exportRequest.setDeliveryDate(deliveryDate);
            exportRequest.setReason(reason);
            exportRequest.setUserId(user.getUserId());
            exportRequest.setStatus("pending");
            String[] materialNames = request.getParameterValues("materialNames[]");
            String[] quantities = request.getParameterValues("quantities[]");
            
            // Validate material details
            Map<String, String> detailErrors = ExportRequestValidator.validateExportRequestDetails(materialNames, quantities);
            
            if (!detailErrors.isEmpty()) {
                // Set submitted data to repopulate form
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedDeliveryDate", deliveryDateStr);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("errors", detailErrors);
                
                // Reload form data
                String newRequestCode = generateRequestCode();
                request.setAttribute("requestCode", newRequestCode);
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                request.setAttribute("materials", materials);
                List<User> staffUsers = userDAO.getUsersByRoleId(3);
                request.setAttribute("users", staffUsers);
                
                request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
                return;
            }
            
            // Convert material names to IDs
            List<Integer> materialIds = new ArrayList<>();
            for (String materialName : materialNames) {
                if (materialName != null && !materialName.trim().isEmpty()) {
                    Material material = materialDAO.getMaterialByName(materialName.trim());
                    if (material != null) {
                        materialIds.add(material.getMaterialId());
                    }
                }
            }
            Map<Integer, Integer> materialQuantityMap = new HashMap<>();
            List<ExportRequestDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.size(); i++) {
                int materialId = materialIds.get(i);
                int quantity = Integer.parseInt(quantities[i]);
                materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, 0) + quantity);
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                details.add(detail);
            }
            for (Integer materialId : materialQuantityMap.keySet()) {
                int totalQuantity = materialQuantityMap.get(materialId);
                Material material = materialDAO.getInformation(materialId);
                int stock = material.getQuantity();
                if (totalQuantity > stock) {
                    throw new Exception("Not enough stock for material: " + material.getMaterialName());
                }
            }
            boolean success = exportRequestDAO.add(exportRequest, details);
            if (success) {
                try {
                    sendExportRequestNotification(exportRequest, details, user);
                } catch (Exception e) {
                    System.err.println("Error sending export request notification: " + e.getMessage());
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            } else {
                request.setAttribute("error", "Failed to create export request.");
                doGet(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }

    private void sendExportRequestNotification(ExportRequest exportRequest, 
                                            List<ExportRequestDetail> details, 
                                            User creator) {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            List<User> directors = new ArrayList<>();
            
            for (User u : allUsers) {
                if (u.getRoleId() == 2) {
                    directors.add(u);
                }
            }
            
            String subject = "[Notification] New Export Request Created";
            StringBuilder content = new StringBuilder();
            content.append("<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>");
            
            // Email container
            content.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>");
            
            // Header with golden brown theme
            content.append("<div style='background: linear-gradient(135deg, #E9B775 0%, #D4A574 100%); padding: 30px; text-align: center;'>");
            content.append("<h1 style='color: #000000; margin: 0; font-size: 28px; font-weight: bold;'>New Export Request</h1>");
            content.append("<p style='color: #000000; margin: 10px 0 0 0; font-size: 16px;'>A new export request has been submitted and requires your attention</p>");
            content.append("</div>");
            
            // Main content
            content.append("<div style='padding: 40px 30px;'>");
            
            // Request information section
            content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
            content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Request Information</h2>");
            
            content.append("<table style='width: 100%; border-collapse: collapse;'>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold; width: 40%;'>Request Code:</td><td style='padding: 8px 0; color: #333333;'>").append(exportRequest.getRequestCode()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Requested By:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getFullName()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Submitted:</td><td style='padding: 8px 0; color: #333333;'>").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Email:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getEmail()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Phone:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getPhoneNumber()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Delivery Date:</td><td style='padding: 8px 0; color: #333333;'>").append(exportRequest.getDeliveryDate()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Reason:</td><td style='padding: 8px 0; color: #333333;'>").append(exportRequest.getReason()).append("</td></tr>");
            content.append("</table>");
            content.append("</div>");
            
            // Material details section
            content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
            content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Material Details</h2>");
            
            content.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #dee2e6;'>");
            content.append("<thead><tr style='background-color: #E9B775;'>");
            content.append("<th style='padding: 12px; text-align: left; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Material Name</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Quantity</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Category</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Status</th>");
            content.append("</tr></thead>");
            content.append("<tbody>");
            
            for (ExportRequestDetail detail : details) {
                Material material = materialDAO.getProductById(detail.getMaterialId());
                if (material != null) {
                    content.append("<tr style='background-color: #ffffff;'>");
                    content.append("<td style='padding: 12px; border: 1px solid #dee2e6; color: #333333;'>").append(material.getMaterialName()).append("</td>");
                    content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getQuantity()).append("</td>");
                    content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getCategory().getCategory_name()).append("</td>");
                    content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getUnit().getUnitName()).append("</td>");
                    content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getMaterialStatus()).append("</td>");
                    content.append("</tr>");
                }
            }
            content.append("</tbody></table>");
            content.append("</div>");
            
            // Action button
            content.append("<div style='text-align: center; margin-top: 30px;'>");
            content.append("<a href='http://localhost:8080/MaterialManagement/ExportRequestList' style='display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>VIEW IN SYSTEM</a>");
            content.append("</div>");
            
            content.append("</div>");
            
            // Footer
            content.append("<div style='background-color: #E9B775; padding: 20px; text-align: center;'>");
            content.append("<p style='color: #000000; margin: 0; font-size: 14px;'>This is an automated notification from the Material Management System</p>");
            content.append("</div>");
            
            content.append("</div></body></html>");
            
            for (User director : directors) {
                if (director.getEmail() != null && !director.getEmail().trim().isEmpty()) {
                    try {
                        EmailUtils.sendEmail(director.getEmail(), subject, content.toString());
                        System.out.println("Email sent to director: " + director.getEmail());
                    } catch (Exception e) {
                        System.err.println("Error sending email to director " + director.getEmail() + ": " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error sending export request notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateRequestCode() {
        String prefix = "ER";
        String sql = "SELECT request_code FROM Export_Requests WHERE request_code LIKE ? ORDER BY request_code DESC LIMIT 1";
        String likePattern = prefix + "%";
        try (java.sql.Connection conn = exportRequestDAO.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                if (rs.next()) {
                    String lastCode = rs.getString("request_code");
                    String numberPart = lastCode.replace(prefix, "");
                    try {
                        nextSeq = Integer.parseInt(numberPart) + 1;
                    } catch (NumberFormatException ignore) {}
                }
                return prefix + nextSeq;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return prefix + "1";
        }
    }
}