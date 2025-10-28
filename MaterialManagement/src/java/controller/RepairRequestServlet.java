package controller;

import dal.MaterialDAO;
import dal.RepairRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.SupplierDAO;
import entity.Material;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import entity.User;
import entity.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.EmailUtils;
import utils.RepairRequestValidator;

import java.io.IOException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RepairRequestServlet", urlPatterns = {"/CreateRepairRequest"})
public class RepairRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RepairRequestServlet.class.getName());
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        if (!rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_REPAIR_REQUEST")) {
            request.setAttribute("error", "You do not have permission to access the repair request creation page.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        MaterialDAO materialDAO = null;
        SupplierDAO supplierDAO = null;
        
        try {
            materialDAO = new MaterialDAO();
            // Load ALL materials, not just damaged ones (like PurchaseRequest does)
            List<Material> materialList = materialDAO.getAllProducts();
            
            // Debug log
            LOGGER.log(Level.INFO, "Found " + materialList.size() + " materials for repair request autocomplete");

            supplierDAO = new SupplierDAO();
            List<Supplier> supplierList = supplierDAO.getAllSuppliers();

            // Generate request code and date
            String requestCode = generateRequestCode();
            String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

            request.setAttribute("materialList", materialList);
            request.setAttribute("supplierList", supplierList);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("requestDate", requestDate);

            // Forward đến trang tạo yêu cầu sửa chữa
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        } finally {
            if (materialDAO != null) {
                try {
                    materialDAO.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error closing MaterialDAO connection", e);
                }
            }
            if (supplierDAO != null) {
                try {
                    supplierDAO.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error closing SupplierDAO connection", e);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("Login.jsp");
                return;
            }

            if (!rolePermissionDAO.hasPermission(user.getRoleId(), "CREATE_REPAIR_REQUEST")) {
                request.setAttribute("error", "You do not have permission to create a repair request.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            int userId = user.getUserId();

            String requestCode = generateRequestCode();

            String supplierIdStr = request.getParameter("supplierId");
            String reason = request.getParameter("reason");
            String[] materialNames = request.getParameterValues("materialName[]");
            String[] materialIds = request.getParameterValues("materialId[]");
            String[] quantities = request.getParameterValues("quantity[]");
            String[] damageDescriptions = request.getParameterValues("damageDescription[]");
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // Validate form data
            Map<String, String> formErrors = RepairRequestValidator.validateRepairRequestFormData(reason, supplierIdStr);
            Map<String, String> detailErrors = RepairRequestValidator.validateRepairRequestDetails(materialNames, quantities, damageDescriptions);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                // Preserve form data for retry
                MaterialDAO materialDAO = null;
                SupplierDAO supplierDAO = null;
                
                try {
                    materialDAO = new MaterialDAO();
                    supplierDAO = new SupplierDAO();
                    // Load ALL materials, not just damaged ones
                    List<Material> materialList = materialDAO.getAllProducts();
                    List<Supplier> supplierList = supplierDAO.getAllSuppliers();

                    String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

                    request.setAttribute("materialList", materialList);
                    request.setAttribute("supplierList", supplierList);
                    request.setAttribute("requestCode", requestCode);
                    request.setAttribute("requestDate", requestDate);
                    request.setAttribute("errors", formErrors);
                    request.setAttribute("submittedReason", reason);
                    request.setAttribute("submittedSupplierId", supplierIdStr);
                    request.setAttribute("submittedMaterialNames", materialNames);
                    request.setAttribute("submittedQuantities", quantities);
                    request.setAttribute("submittedDamageDescriptions", damageDescriptions);

                    request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                } finally {
                    if (materialDAO != null) {
                        try {
                            materialDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing MaterialDAO connection", e);
                        }
                    }
                    if (supplierDAO != null) {
                        try {
                            supplierDAO.close();
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Error closing SupplierDAO connection", e);
                        }
                    }
                }
                return;
            }

            // Parse supplierId after validation
            int supplierId = 0;
            try {
                supplierId = Integer.parseInt(supplierIdStr);
            } catch (NumberFormatException e) {
                // This will be caught by validation above
            }

            RepairRequest requestObj = new RepairRequest();
            requestObj.setRequestCode(requestCode);
            requestObj.setUserId(userId);
            requestObj.setRepairPersonPhoneNumber(null); 
            requestObj.setRepairPersonEmail(null); 
            requestObj.setRepairLocation(null); 
            requestObj.setReason(reason);
            requestObj.setRequestDate(now);
            requestObj.setStatus("Pending");
            requestObj.setCreatedAt(now);
            requestObj.setUpdatedAt(now);
            requestObj.setDisable(false);

            List<RepairRequestDetail> detailList = new ArrayList<>();

            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                
                // Get material ID from hidden field
                int materialId = 0;
                if (materialIds != null && materialIds.length > i && materialIds[i] != null && !materialIds[i].isEmpty()) {
                    try {
                        materialId = Integer.parseInt(materialIds[i]);
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid material ID at index " + i);
                    }
                }
                
                // Validate material ID
                if (materialId <= 0) {
                    LOGGER.log(Level.WARNING, "Invalid material ID: " + materialId + " for material: " + materialName);
                    String errorMsg = "Invalid material selected at row " + (i + 1) + ". Please select from the dropdown list.";
                    request.setAttribute("error", errorMsg);
                    
                    // Reload form data
                    MaterialDAO materialDAO = null;
                    SupplierDAO supplierDAO = null;
                    
                    try {
                        materialDAO = new MaterialDAO();
                        supplierDAO = new SupplierDAO();
                        // Load ALL materials, not just damaged ones
                        List<Material> materialList = materialDAO.getAllProducts();
                        List<Supplier> supplierList = supplierDAO.getAllSuppliers();
                        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());
                        
                        request.setAttribute("materialList", materialList);
                        request.setAttribute("supplierList", supplierList);
                        request.setAttribute("requestCode", requestCode);
                        request.setAttribute("requestDate", requestDate);
                        request.setAttribute("submittedReason", reason);
                        request.setAttribute("submittedSupplierId", supplierIdStr);
                        request.setAttribute("submittedMaterialNames", materialNames);
                        request.setAttribute("submittedQuantities", quantities);
                        request.setAttribute("submittedDamageDescriptions", damageDescriptions);
                        
                        request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
                    } finally {
                        if (materialDAO != null) {
                            try {
                                materialDAO.close();
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Error closing MaterialDAO connection", e);
                            }
                        }
                        if (supplierDAO != null) {
                            try {
                                supplierDAO.close();
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Error closing SupplierDAO connection", e);
                            }
                        }
                    }
                    return;
                }
                
                LOGGER.log(Level.FINE, "Processing material ID: " + materialId + " - " + materialName);
                
                BigDecimal quantity = new BigDecimal(quantities[i]);
                String damageDescription = damageDescriptions[i];

                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                detail.setDamageDescription(damageDescription);
                detail.setSupplierId(supplierId); 
                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);

                detailList.add(detail);
            }

            boolean success = new RepairRequestDAO().createRepairRequest(requestObj, detailList);
            LOGGER.log(Level.INFO, "[doPost] Kết quả lưu yêu cầu vào DB: " + success);

            if (success) {
                // Get user and supplier data, then close connections immediately
                UserDAO userDAO = new UserDAO();
                List<User> allUsers = userDAO.getAllUsers();
                userDAO.close();
                
                SupplierDAO supplierDAO = new SupplierDAO();
                Supplier supplier = supplierDAO.getSupplierByID(supplierId);
                supplierDAO.close();
                
                List<User> managers = new ArrayList<>();
                for (User u : allUsers) {
                    if (u.getRoleId() == 2) {
                        managers.add(u);
                    }
                }

                if (!managers.isEmpty()) {
                    String subject = "[Notification] New Repair Request";

                    // Build HTML email content
                    StringBuilder htmlContent = new StringBuilder();
                    htmlContent.append("<!DOCTYPE html>");
                    htmlContent.append("<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>");
                    
                    // Email container
                    htmlContent.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>");
                    
                    // Header with golden brown theme
                    htmlContent.append("<div style='background: linear-gradient(135deg, #E9B775 0%, #D4A574 100%); padding: 30px; text-align: center;'>");
                    htmlContent.append("<h1 style='color: #000000; margin: 0; font-size: 28px; font-weight: bold;'>New Repair Request</h1>");
                    htmlContent.append("<p style='color: #000000; margin: 10px 0 0 0; font-size: 16px;'>A new repair request has been submitted and requires your attention</p>");
                    htmlContent.append("</div>");
                    
                    // Main content
                    htmlContent.append("<div style='padding: 40px 30px;'>");
                    
                    // Request information section
                    htmlContent.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
                    htmlContent.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Request Information</h2>");
                    
                    htmlContent.append("<table style='width: 100%; border-collapse: collapse;'>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold; width: 40%;'>Request Code:</td><td style='padding: 8px 0; color: #333333;'>").append(requestCode).append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Requested By:</td><td style='padding: 8px 0; color: #333333;'>").append(user.getFullName()).append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Submitted:</td><td style='padding: 8px 0; color: #333333;'>").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now)).append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Email:</td><td style='padding: 8px 0; color: #333333;'>").append(user.getEmail() != null ? user.getEmail() : "N/A").append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Phone:</td><td style='padding: 8px 0; color: #333333;'>").append(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A").append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Repairer:</td><td style='padding: 8px 0; color: #333333;'>").append(supplier != null ? supplier.getSupplierName() : "N/A").append("</td></tr>");
                    htmlContent.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Reason:</td><td style='padding: 8px 0; color: #333333;'>").append(reason).append("</td></tr>");
                    htmlContent.append("</table>");
                    htmlContent.append("</div>");
                    
                    // Material details section
                    htmlContent.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
                    htmlContent.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Materials for Repair</h2>");
                    
                    htmlContent.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #dee2e6;'>");
                    htmlContent.append("<thead><tr style='background-color: #E9B775;'>");
                    htmlContent.append("<th style='padding: 12px; text-align: left; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Material Name</th>");
                    htmlContent.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Quantity</th>");
                    htmlContent.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Category</th>");
                    htmlContent.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit</th>");
                    htmlContent.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Status</th>");
                    htmlContent.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Damage Description</th>");
                    htmlContent.append("</tr></thead>");
                    htmlContent.append("<tbody>");
                    
                    MaterialDAO emailMaterialDAO = new MaterialDAO();
                    for (RepairRequestDetail detail : detailList) {
                        Material material = emailMaterialDAO.getProductById(detail.getMaterialId());
                        if (material != null) {
                            htmlContent.append("<tr style='background-color: #ffffff;'>");
                            htmlContent.append("<td style='padding: 12px; border: 1px solid #dee2e6; color: #333333;'>").append(material.getMaterialName()).append("</td>");
                            htmlContent.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getQuantity()).append("</td>");
                            htmlContent.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getCategory() != null ? material.getCategory().getCategory_name() : "N/A").append("</td>");
                            htmlContent.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getUnit() != null ? material.getUnit().getUnitName() : "N/A").append("</td>");
                            htmlContent.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material.getMaterialStatus()).append("</td>");
                            htmlContent.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getDamageDescription()).append("</td>");
                            htmlContent.append("</tr>");
                        }
                    }
                    emailMaterialDAO.close();
                    
                    htmlContent.append("</tbody></table>");
                    htmlContent.append("</div>");
                    
                    // Action button
                    htmlContent.append("<div style='text-align: center; margin-top: 30px;'>");
                    htmlContent.append("<a href='http://localhost:8080/MaterialManagement/repairrequestlist' style='display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>VIEW IN SYSTEM</a>");
                    htmlContent.append("</div>");
                    
                    htmlContent.append("</div>");
                    
                    // Footer
                    htmlContent.append("<div style='background-color: #E9B775; padding: 20px; text-align: center;'>");
                    htmlContent.append("<p style='color: #000000; margin: 0; font-size: 14px;'>This is an automated notification from the Material Management System</p>");
                    htmlContent.append("</div>");
                    
                    htmlContent.append("</div></body></html>");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                EmailUtils.sendEmail(manager.getEmail(), subject, htmlContent.toString());
                            } catch (Exception e) {
                                // Log individual email failures but continue
                                LOGGER.log(Level.WARNING, "Failed to send email to manager: " + manager.getEmail() + " - " + e.getMessage());
                            }
                        } else {
                            LOGGER.log(Level.INFO, "Manager has no valid email: " + manager.getFullName());
                        }
                    }

                    if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                        try {
                            EmailUtils.sendEmail(user.getEmail(), subject, htmlContent.toString());
                        } catch (Exception e) {
                            // Log individual email failures but continue
                            LOGGER.log(Level.WARNING, "Failed to send email to user: " + user.getEmail() + " - " + e.getMessage());
                        }
                    } else {
                        LOGGER.log(Level.INFO, "User has no valid email: " + user.getFullName());
                    }
                }
            }

            response.sendRedirect("repairrequestlist");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when sending repair request: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Error when sending repair request: " + e.getMessage());
            request.getRequestDispatcher("CreateRepairRequest.jsp").forward(request, response);
        }
    }

    private String generateRequestCode() {
        RepairRequestDAO repairRequestDAO = new RepairRequestDAO();
        return repairRequestDAO.generateNextRequestCode();
    }
}
