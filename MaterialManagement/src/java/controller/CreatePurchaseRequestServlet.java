package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.MaterialDAO;
import entity.Category;
import entity.Material;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PurchaseRequestValidator;

/**
 * Servlet for handling creation of purchase requests.
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequest"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CreatePurchaseRequestServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            List<User> users = userDAO.getAllUsers();
            List<Category> categories = categoryDAO.getAllCategories();
            List<entity.Material> materials = materialDAO.getAllProducts();

            String requestCode = prd.generateNextRequestCode();
            String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());

            request.setAttribute("users", users);
            request.setAttribute("categories", categories);
            request.setAttribute("materials", materials);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("requestDate", requestDate);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);

            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO prd = new PurchaseRequestDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        UserDAO userDAO = new UserDAO();

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "CREATE_PURCHASE_REQUEST");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            String reason = request.getParameter("reason");

            String[] materialNames = request.getParameterValues("materialName[]");
            String[] materialIds = request.getParameterValues("materialId");
            String[] quantities = request.getParameterValues("quantity[]");
            String[] notes = request.getParameterValues("note");
            
            // Debug: Check if reason is being read correctly
           
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                LOGGER.log(Level.FINE, "Parameter: " + paramName + ", Value: " + String.join(", ", paramValues));
            }
            
            

            Map<String, String> formErrors = PurchaseRequestValidator.validatePurchaseRequestFormData(reason);
            Map<String, String> detailErrors = PurchaseRequestValidator.validatePurchaseRequestDetails(materialNames, quantities);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                // Always generate a new request code for retry
                String requestCode = prd.generateNextRequestCode();
                String requestDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date());
                
                // Preserve form data for retry
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate);
                
                // Preserve submitted form data
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            List<PurchaseRequestDetail> purchaseRequestDetails = new ArrayList<>();
            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }
                int quantity = Integer.parseInt(quantities[i]);
                int materialId = 0;
                if (materialIds != null && materialIds.length > i && materialIds[i] != null && !materialIds[i].isEmpty()) {
                    materialId = Integer.parseInt(materialIds[i]);
                }
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setMaterialName(materialName.trim());
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                String note = (notes != null && notes.length > i) ? notes[i] : null;
                detail.setNotes(note != null && !note.trim().isEmpty() ? note.trim() : null);
                purchaseRequestDetails.add(detail);
            }

            String requestCode = prd.generateNextRequestCode();

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setRequestCode(requestCode);
            purchaseRequest.setUserId(currentUser.getUserId());
            purchaseRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            purchaseRequest.setStatus("PENDING");
            
            // Ensure reason is properly set
            String finalReason = reason != null ? reason.trim() : "";
            if (finalReason.isEmpty()) {
                finalReason = "Test reason for debugging";
            }
            purchaseRequest.setReason(finalReason);
            
           
            for (int i = 0; i < purchaseRequestDetails.size(); i++) {
                PurchaseRequestDetail detail = purchaseRequestDetails.get(i);
               
            }

            boolean success = prd.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);
           

            if (success) {
                List<User> allUsers = userDAO.getAllUsers();
                List<User> managers = new ArrayList<>();
                for (User u : allUsers) {
                    if (rolePermissionDAO.hasPermission(u.getRoleId(), "HANDLE_REQUEST")) {
                        managers.add(u);
                    }
                }

                if (!managers.isEmpty()) {
                    String subject = "[Notification] New Purchase Request Created";
                    StringBuilder content = new StringBuilder();
                    content.append("<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>");
                    
                    // Email container
                    content.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>");
                    
                    // Header with golden brown theme
                    content.append("<div style='background: linear-gradient(135deg, #E9B775 0%, #D4A574 100%); padding: 30px; text-align: center;'>");
                    content.append("<h1 style='color: #000000; margin: 0; font-size: 28px; font-weight: bold;'>New Purchase Request</h1>");
                    content.append("<p style='color: #000000; margin: 10px 0 0 0; font-size: 16px;'>A new purchase request has been submitted and requires your approval</p>");
                    content.append("</div>");
                    
                    // Main content
                    content.append("<div style='padding: 40px 30px;'>");
                    
                    // Request information section
                    content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
                    content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Request Information</h2>");
                    
                    content.append("<table style='width: 100%; border-collapse: collapse;'>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold; width: 40%;'>Request Code:</td><td style='padding: 8px 0; color: #333333;'>").append(requestCode).append("</td></tr>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Requested By:</td><td style='padding: 8px 0; color: #333333;'>").append(currentUser.getFullName()).append("</td></tr>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Submitted:</td><td style='padding: 8px 0; color: #333333;'>").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</td></tr>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Email:</td><td style='padding: 8px 0; color: #333333;'>").append(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A").append("</td></tr>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Phone:</td><td style='padding: 8px 0; color: #333333;'>").append(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "N/A").append("</td></tr>");
                    content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Reason:</td><td style='padding: 8px 0; color: #333333;'>").append(reason).append("</td></tr>");
                    content.append("</table>");
                    content.append("</div>");
                    
                    // Material details section
                    content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
                    content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Requested Materials</h2>");
                    
                    content.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #dee2e6;'>");
                    content.append("<thead><tr style='background-color: #E9B775;'>");
                    content.append("<th style='padding: 12px; text-align: left; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Material Name</th>");
                    content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Quantity</th>");
                    content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Category</th>");
                    content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit</th>");
                    content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Status</th>");
                    content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Notes</th>");
                    content.append("</tr></thead>");
                    content.append("<tbody>");
                    
                    for (PurchaseRequestDetail detail : purchaseRequestDetails) {
                        Material material = null;
                        if (detail.getMaterialId() > 0) {
                            material = materialDAO.getProductById(detail.getMaterialId());
                        }
                        
                        content.append("<tr style='background-color: #ffffff;'>");
                        content.append("<td style='padding: 12px; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getMaterialName()).append("</td>");
                        content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getQuantity()).append("</td>");
                        content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material != null && material.getCategory() != null && material.getCategory().getCategory_name() != null ? material.getCategory().getCategory_name() : "N/A").append("</td>");
                        content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material != null && material.getUnit() != null && material.getUnit().getUnitName() != null ? material.getUnit().getUnitName() : "N/A").append("</td>");
                        content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(material != null && material.getMaterialStatus() != null ? material.getMaterialStatus() : "N/A").append("</td>");
                        content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getNotes() != null && !detail.getNotes().trim().isEmpty() ? detail.getNotes() : "-").append("</td>");
                        content.append("</tr>");
                    }
                    content.append("</tbody></table>");
                    content.append("</div>");
                    
                    // Action button
                    content.append("<div style='text-align: center; margin-top: 30px;'>");
                    content.append("<a href='http://localhost:8080/MaterialManagement/PurchaseRequestList' style='display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>VIEW IN SYSTEM</a>");
                    content.append("</div>");
                    
                    content.append("</div>");
                    
                    // Footer
                    content.append("<div style='background-color: #E9B775; padding: 20px; text-align: center;'>");
                    content.append("<p style='color: #000000; margin: 0; font-size: 14px;'>This is an automated notification from the Material Management System</p>");
                    content.append("</div>");
                    
                    content.append("</div></body></html>");
                    content.append("<div class='action-section'>");
                    content.append("<p style='color: #E9B775; margin-bottom: 20px; font-weight: bold; text-shadow: 1px 1px 2px rgba(233, 183, 117, 0.3);'>Please review and take action on this purchase request:</p>");
                    content.append("<a href='").append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath()).append("/ListPurchaseRequests' class='btn'>VIEW IN SYSTEM</a>");
                    content.append("</div>");
                    content.append("</div>");
                    content.append("<div class='footer'>");
                    content.append("<p style='color: #000;'>This is an automated notification from the Material Management System</p>");
                    content.append("<p style='color: #000;'>If you have any questions, please contact the system administrator</p>");
                    content.append("</div>");
                    content.append("</div>");
                    content.append("</body>");
                    content.append("</html>");

                    for (User manager : managers) {
                        if (manager.getEmail() != null && !manager.getEmail().trim().isEmpty()) {
                            try {
                                
                                utils.EmailUtils.sendEmail(manager.getEmail(), subject, content.toString());
                                
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Error sending email to manager: " + manager.getEmail(), e);
                            }
                        }
                    }
                }
                response.sendRedirect("ListPurchaseRequests?success=created");
            } else {
                LOGGER.log(Level.WARNING, "Could not create purchase request. Database operation failed or returned false.");
                request.setAttribute("error", "Could not create purchase request. Please try again.");
                List<Category> categories = categoryDAO.getAllCategories();
                List<entity.Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while processing the request to create purchase request.", e);
            request.setAttribute("error", "An error occurred while processing the request: " + e.getMessage());
            List<Category> categories = categoryDAO.getAllCategories();
            List<entity.Material> materials = materialDAO.getAllProducts();
            request.setAttribute("categories", categories);
            request.setAttribute("materials", materials);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
