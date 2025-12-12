package controller;

import dal.MaterialDAO;
import dal.PurchaseOrderDAO;
import dal.PurchaseRequestDAO;
import dal.SupplierDAO;
import dal.PurchaseRequestDetailDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import entity.Material;
import entity.PurchaseRequest;
import entity.Supplier;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.EmailUtils;
import utils.PermissionHelper;
import utils.PurchaseOrderValidator;
import jakarta.mail.MessagingException;

/**
 * Servlet for handling creation of purchase orders.
 */
@WebServlet(name = "CreatePurchaseOrderServlet", urlPatterns = {"/CreatePurchaseOrder"})
public class CreatePurchaseOrderServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CreatePurchaseOrderServlet.class.getName());

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

        PurchaseRequestDAO purchaseRequestDAO = null;
        MaterialDAO materialDAO = null;
        SupplierDAO supplierDAO = null;
        RolePermissionDAO rolePermissionDAO = null;
        PurchaseOrderDAO purchaseOrderDAO = null;
        PurchaseRequestDetailDAO purchaseRequestDetailDAO = null;

        try {
            purchaseRequestDAO = new PurchaseRequestDAO();
            materialDAO = new MaterialDAO();
            supplierDAO = new SupplierDAO();
            rolePermissionDAO = new RolePermissionDAO();

            // Admin (roleId == 1) has full access - check first before permission check
            boolean hasPermission;
            if (currentUser.getRoleId() == 1) {
                hasPermission = true;
            } else {
                hasPermission = PermissionHelper.hasPermission(currentUser, "Tạo PO");
            }
            if (!hasPermission) {
                request.setAttribute("error", "Bạn không có quyền tạo đơn đặt hàng.");
                request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
                return;
            }

            // Generate proper PO code using DAO method
            purchaseOrderDAO = new PurchaseOrderDAO();
            String poCode = purchaseOrderDAO.generateNextPOCode();
            List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
            
            // Get PO status for each Purchase Request (Map: PR ID -> PO Status)
            java.util.Map<Integer, String> poStatusMap = purchaseRequestDAO.getApprovedPurchaseRequestsWithPOStatus();
            
            List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();

            request.setAttribute("poCode", poCode);
            request.setAttribute("purchaseRequests", purchaseRequests);
            request.setAttribute("poStatusMap", poStatusMap); // Map to show PO status in dropdown
            request.setAttribute("materials", materials);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);

            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            if (purchaseRequestIdStr != null && !purchaseRequestIdStr.isEmpty()) {
                int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
                purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
                List<entity.PurchaseRequestDetail> purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, 1000);
                if (purchaseRequestDetailList == null) purchaseRequestDetailList = new ArrayList<>();
                request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
                request.setAttribute("selectedPurchaseRequestId", purchaseRequestId);
                
                java.util.Map<Integer, String> materialImages = new java.util.HashMap<>();
                java.util.Map<Integer, String> materialCategories = new java.util.HashMap<>();
                java.util.Map<Integer, String> materialUnits = new java.util.HashMap<>();
                
                for (entity.PurchaseRequestDetail detail : purchaseRequestDetailList) {
                    Material material = materialDAO.getInformation(detail.getMaterialId());
                    if (material != null) {
                        // Get image
                        String fileName = material.getMaterialsUrl();
                        materialImages.put(detail.getMaterialId(), fileName);
                        
                        // Get category name
                        String categoryName = material.getCategory() != null ? material.getCategory().getCategory_name() : "N/A";
                        materialCategories.put(detail.getMaterialId(), categoryName);
                        
                        // Get unit name
                        String unitName = material.getUnit() != null ? material.getUnit().getUnitName() : "N/A";
                        materialUnits.put(detail.getMaterialId(), unitName);
                    }
                }
                request.setAttribute("materialImages", materialImages);
                request.setAttribute("materialCategories", materialCategories);
                request.setAttribute("materialUnits", materialUnits);
            }

            request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for CreatePurchaseOrderServlet", e);
            request.setAttribute("error", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
        } finally {
            if (purchaseRequestDAO != null) purchaseRequestDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (supplierDAO != null) supplierDAO.close();
            if (rolePermissionDAO != null) rolePermissionDAO.close();
            if (purchaseOrderDAO != null) purchaseOrderDAO.close();
            if (purchaseRequestDetailDAO != null) purchaseRequestDetailDAO.close();
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

        PurchaseOrderDAO purchaseOrderDAO = null;
        PurchaseRequestDAO purchaseRequestDAO = null;
        MaterialDAO materialDAO = null;
        SupplierDAO supplierDAO = null;
        RolePermissionDAO rolePermissionDAO = null;
        UserDAO userDAO = null;
        PurchaseRequestDetailDAO purchaseRequestDetailDAO = null;

        try {
            purchaseOrderDAO = new PurchaseOrderDAO();
            purchaseRequestDAO = new PurchaseRequestDAO();
            materialDAO = new MaterialDAO();
            supplierDAO = new SupplierDAO();
            rolePermissionDAO = new RolePermissionDAO();
            userDAO = new UserDAO();

            // Admin (roleId == 1) has full access - check first before permission check
            boolean hasPermission;
            if (currentUser.getRoleId() == 1) {
                hasPermission = true;
            } else {
                hasPermission = PermissionHelper.hasPermission(currentUser, "Tạo PO");
            }
            if (!hasPermission) {
                request.setAttribute("error", "Bạn không có quyền tạo đơn đặt hàng.");
                request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
                return;
            }
            String poCode = request.getParameter("poCode");
            String purchaseRequestIdStr = request.getParameter("purchaseRequestId");
            String note = request.getParameter("note");
            String[] materialIds = request.getParameterValues("materialIds[]");
            String[] quantities = request.getParameterValues("quantities[]");
            String[] unitPrices = request.getParameterValues("unitPrices[]");
            String[] suppliers = request.getParameterValues("suppliers[]");
            
            Map<String, String> formErrors = PurchaseOrderValidator.validatePurchaseOrderFormData(poCode, purchaseRequestIdStr, note);
            Map<String, String> detailErrors = PurchaseOrderValidator.validatePurchaseOrderDetails(materialIds, quantities, unitPrices, suppliers);
            formErrors.putAll(detailErrors);

            if (!formErrors.isEmpty()) {
                LOGGER.log(Level.WARNING, "Validation errors found during purchase order creation.");
                List<PurchaseRequest> purchaseRequests = purchaseRequestDAO.getApprovedPurchaseRequests();
                List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
                List<Supplier> suppliersList = supplierDAO.getAllSuppliers();
                
                // Generate proper PO code using DAO method
                String newPoCode = purchaseOrderDAO.generateNextPOCode();
                
                // Get PO status for each Purchase Request
                java.util.Map<Integer, String> poStatusMap = purchaseRequestDAO.getApprovedPurchaseRequestsWithPOStatus();
                
                // Preserve form data for retry
                request.setAttribute("poCode", newPoCode);
                request.setAttribute("purchaseRequests", purchaseRequests);
                request.setAttribute("poStatusMap", poStatusMap); // Map to show PO status in dropdown
                request.setAttribute("materials", materials);
                request.setAttribute("suppliers", suppliersList);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                
                // Preserve submitted form data
                request.setAttribute("submittedPoCode", poCode);
                request.setAttribute("submittedPurchaseRequestId", purchaseRequestIdStr);
                request.setAttribute("submittedNote", note);
                request.setAttribute("submittedMaterialIds", materialIds);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedUnitPrices", unitPrices);
                request.setAttribute("submittedSuppliers", suppliers);

                // Load purchase request details if purchase request is selected
                if (purchaseRequestIdStr != null && !purchaseRequestIdStr.isEmpty()) {
                    try {
                        int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
                        purchaseRequestDetailDAO = new PurchaseRequestDetailDAO();
                        List<entity.PurchaseRequestDetail> purchaseRequestDetailList = purchaseRequestDetailDAO.paginationOfDetails(purchaseRequestId, 1, 1000);
                        if (purchaseRequestDetailList == null) purchaseRequestDetailList = new ArrayList<>();
                        request.setAttribute("purchaseRequestDetailList", purchaseRequestDetailList);
                        request.setAttribute("selectedPurchaseRequestId", purchaseRequestId);
                        
                        java.util.Map<Integer, String> materialImages = new java.util.HashMap<>();
                        java.util.Map<Integer, String> materialCategories = new java.util.HashMap<>();
                        java.util.Map<Integer, String> materialUnits = new java.util.HashMap<>();
                        
                        for (entity.PurchaseRequestDetail detail : purchaseRequestDetailList) {
                            Material material = materialDAO.getInformation(detail.getMaterialId());
                            if (material != null) {
                                // Get image
                                String fileName = material.getMaterialsUrl();
                                materialImages.put(detail.getMaterialId(), fileName);
                                
                                // Get category name
                                String categoryName = material.getCategory() != null ? material.getCategory().getCategory_name() : "N/A";
                                materialCategories.put(detail.getMaterialId(), categoryName);
                                
                                // Get unit name
                                String unitName = material.getUnit() != null ? material.getUnit().getUnitName() : "N/A";
                                materialUnits.put(detail.getMaterialId(), unitName);
                            }
                        }
                        request.setAttribute("materialImages", materialImages);
                        request.setAttribute("materialCategories", materialCategories);
                        request.setAttribute("materialUnits", materialUnits);
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid purchaseRequestId format in doPost: " + purchaseRequestIdStr, e);
                        // Ignore if purchase request ID is invalid, material list will be empty
                    }
                } else {
                    // If no purchase request selected, clear the material list
                    request.setAttribute("purchaseRequestDetailList", new ArrayList<>());
                    request.setAttribute("selectedPurchaseRequestId", null);
                }
                
                request.getRequestDispatcher("CreatePurchaseOrder.jsp").forward(request, response);
                return;
            }

            // Parse purchase request ID after validation
            int purchaseRequestId = Integer.parseInt(purchaseRequestIdStr);
            PurchaseRequest purchaseRequest = purchaseRequestDAO.getPurchaseRequestById(purchaseRequestId);
            if (purchaseRequest == null) {
                LOGGER.log(Level.WARNING, "Purchase request with ID " + purchaseRequestId + " not found.");
                throw new Exception("The selected purchase request was not found. Please select a valid one.");
            }
            if (!"approved".equalsIgnoreCase(purchaseRequest.getStatus())) {
                LOGGER.log(Level.WARNING, "Attempted to create PO from unapproved PR: " + purchaseRequest.getRequestCode());
                throw new Exception("Only approved purchase requests can be used to create purchase orders.");
            }

            // Bỏ chức năng phân tách đơn - tạo 1 PO duy nhất với tất cả materials
            // Lấy supplier_id từ material đầu tiên (hoặc có thể để null nếu không bắt buộc)
            List<entity.PurchaseOrderDetail> allDetails = new ArrayList<>();
            Integer firstSupplierId = null;
            
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = Integer.parseInt(materialIds[i]);
                BigDecimal quantity = new BigDecimal(quantities[i]);
                BigDecimal unitPrice = new BigDecimal(unitPrices[i]);
                int supplierId = Integer.parseInt(suppliers[i]);
                
                // Lấy supplier_id từ material đầu tiên
                if (firstSupplierId == null) {
                    firstSupplierId = supplierId;
                }

                Material material = materialDAO.getInformation(materialId);
                if (material == null) {
                    LOGGER.log(Level.WARNING, "Material with ID " + materialId + " not found during PO creation.");
                    throw new Exception("Material with ID " + materialId + " not found. Please check material details.");
                }

                entity.PurchaseOrderDetail detail = new entity.PurchaseOrderDetail();
                detail.setMaterialId(materialId);
                detail.setMaterialName(material.getMaterialName());
                detail.setMaterialCode(material.getMaterialCode());
                detail.setQuantityOrdered(quantity);
                detail.setUnitPrice(unitPrice);
                detail.setTaxRate(BigDecimal.ZERO);
                detail.setDiscountRate(BigDecimal.ZERO);
                detail.setSupplierId(supplierId);

                if (material.getDefaultUnit() != null) {
                    detail.setUnitId(material.getDefaultUnit().getId());
                    detail.setUnitName(material.getDefaultUnit().getUnitName());
                }

                allDetails.add(detail);
            }

            // Tạo 1 PO duy nhất với tất cả materials
            entity.PurchaseOrder purchaseOrder = new entity.PurchaseOrder();
            purchaseOrder.setPoCode(purchaseOrderDAO.generateNextPOCode());
            purchaseOrder.setPurchaseRequestId(purchaseRequestId);
            purchaseOrder.setSupplierId(firstSupplierId); // Supplier từ material đầu tiên
            purchaseOrder.setCurrencyId(1); // default currency
            purchaseOrder.setOrderDate(Date.valueOf(LocalDate.now()));
            purchaseOrder.setExpectedDeliveryDate(purchaseRequest.getExpectedDate());
            purchaseOrder.setDeliveryAddress(null);
            purchaseOrder.setPaymentTermId(null);
            purchaseOrder.setTotalAmount(BigDecimal.ZERO);
            purchaseOrder.setTaxAmount(BigDecimal.ZERO);
            purchaseOrder.setDiscountAmount(BigDecimal.ZERO);
            purchaseOrder.setGrandTotal(BigDecimal.ZERO);
            purchaseOrder.setCreatedBy(currentUser.getUserId());
            // Note: Purchase_Orders table doesn't have 'note' column in V12 schema
            purchaseOrder.setStatus("draft");

            boolean success = purchaseOrderDAO.createPurchaseOrder(purchaseOrder, allDetails);

            if (success) {
                try {
                    sendPurchaseOrderNotification(purchaseOrder, allDetails, purchaseRequest, currentUser);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error sending purchase order notification for PO " + purchaseOrder.getPoCode() + ": " + e.getMessage(), e);
                }
                
                response.sendRedirect(request.getContextPath() + "/PurchaseOrderList");
                return;
            } else {
                LOGGER.log(Level.SEVERE, "Failed to create purchase order.");
                request.setAttribute("error", "Failed to create purchase order. Please try again.");
                doGet(request, response);
                return;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost for CreatePurchaseOrderServlet", e);
            request.setAttribute("error", "Error creating purchase order: " + e.getMessage());
            doGet(request, response);
        } finally {
            if (purchaseOrderDAO != null) purchaseOrderDAO.close();
            if (purchaseRequestDAO != null) purchaseRequestDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (supplierDAO != null) supplierDAO.close();
            if (rolePermissionDAO != null) rolePermissionDAO.close();
            if (userDAO != null) userDAO.close();
            if (purchaseRequestDetailDAO != null) purchaseRequestDetailDAO.close();
        }
    }

    private void sendPurchaseOrderNotification(entity.PurchaseOrder purchaseOrder, 
                                            List<entity.PurchaseOrderDetail> details, 
                                            PurchaseRequest purchaseRequest, 
                                            User creator) {
        UserDAO userDAO = null;
        SupplierDAO supplierDAO = null;
        MaterialDAO materialDAO = null;
        try {
            userDAO = new UserDAO();
            List<User> allUsers = userDAO.getAllUsers();
            List<User> directors = new ArrayList<>();
            
            for (User u : allUsers) {
                if (u.getRoleId() == 2) {
                    directors.add(u);
                }
            }
            
            // Get supplier information from first detail (all details have same supplier)
            supplierDAO = new SupplierDAO();
            Supplier supplier = null;
            if (!details.isEmpty()) {
                supplier = supplierDAO.getSupplierByID(details.get(0).getSupplierId());
            }
            String supplierName = (supplier != null) ? supplier.getSupplierName() : "N/A";
            
            String subject = "[Purchase Order] " + purchaseOrder.getPoCode() + " - " + supplierName;
            StringBuilder content = new StringBuilder();
            content.append("<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>");
            
            // Email container
            content.append("<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>");
            
            // Header with golden brown theme
            content.append("<div style='background: linear-gradient(135deg, #E9B775 0%, #D4A574 100%); padding: 30px; text-align: center;'>");
            content.append("<h1 style='color: #000000; margin: 0; font-size: 28px; font-weight: bold;'>New Purchase Order</h1>");
            content.append("<p style='color: #000000; margin: 10px 0 0 0; font-size: 16px;'>A new purchase order has been submitted and requires your attention</p>");
            content.append("</div>");
            
            // Main content
            content.append("<div style='padding: 40px 30px;'>");
            
            // Request information section
            content.append("<div style='background-color: #f8f9fa; border-radius: 8px; padding: 25px; margin-bottom: 30px;'>");
            content.append("<h2 style='color: #000000; margin: 0 0 20px 0; font-size: 20px; font-weight: bold;'>Request Information</h2>");
            
            content.append("<table style='width: 100%; border-collapse: collapse;'>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold; width: 40%;'>Request Code:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getPoCode()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Requested By:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getFullName()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Submitted:</td><td style='padding: 8px 0; color: #333333;'>").append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Email:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getEmail() != null ? creator.getEmail() : "N/A").append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Phone:</td><td style='padding: 8px 0; color: #333333;'>").append(creator.getPhoneNumber() != null ? creator.getPhoneNumber() : "N/A").append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Purchase Order:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getPoCode()).append("</td></tr>");
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Based on PR:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseRequest.getRequestCode()).append("</td></tr>");
            
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Supplier:</td><td style='padding: 8px 0; color: #333333;'>").append(supplierName).append("</td></tr>");
            
            content.append("<tr><td style='padding: 8px 0; color: #000000; font-weight: bold;'>Reason:</td><td style='padding: 8px 0; color: #333333;'>").append(purchaseOrder.getNote() != null && !purchaseOrder.getNote().trim().isEmpty() ? purchaseOrder.getNote() : "No additional notes").append("</td></tr>");
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
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Unit Price</th>");
            content.append("<th style='padding: 12px; text-align: center; color: #000000; font-weight: bold; border: 1px solid #dee2e6;'>Total Amount</th>");
            content.append("</tr></thead>");
            content.append("<tbody>");
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            materialDAO = new MaterialDAO();
            
            for (entity.PurchaseOrderDetail detail : details) {
                BigDecimal lineTotal = detail.getUnitPrice().multiply(detail.getQuantityOrdered());
                totalAmount = totalAmount.add(lineTotal);
                
                // Get material information for category and unit
                Material material = materialDAO.getInformation(detail.getMaterialId());
                String categoryName = (material != null && material.getCategory() != null) ? material.getCategory().getCategory_name() : "N/A";
                String unitName = (material != null && material.getUnit() != null) ? material.getUnit().getUnitName() : "N/A";
                
                content.append("<tr style='background-color: #ffffff;'>");
                content.append("<td style='padding: 12px; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getMaterialName()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(detail.getQuantityOrdered()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(categoryName).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>").append(unitName).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>$").append(detail.getUnitPrice()).append("</td>");
                content.append("<td style='padding: 12px; text-align: center; border: 1px solid #dee2e6; color: #333333;'>$").append(lineTotal).append("</td>");
                content.append("</tr>");
            }
            content.append("</tbody></table>");
            
            // Total amount
            content.append("<div style='text-align: right; margin-top: 20px; padding: 15px; background-color: #E9B775; border-radius: 5px;'>");
            content.append("<h3 style='color: #000000; margin: 0; font-size: 18px; font-weight: bold;'>Total Amount: $").append(totalAmount).append("</h3>");
            content.append("</div>");
            content.append("</div>");
            
            // Action button
            content.append("<div style='text-align: center; margin-top: 30px;'>");
            content.append("<a href='http://localhost:8080/MaterialManagement/PurchaseOrderList' style='display: inline-block; background-color: #E9B775; color: #FFFFFF !important; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>VIEW IN SYSTEM</a>");
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
                    } catch (Exception e) {
                        // Log individual email failures but continue
                        LOGGER.log(Level.WARNING, "Failed to send email to director: " + director.getEmail() + " - " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            // Log notification errors but don't crash the application
            LOGGER.log(Level.WARNING, "Error in sendPurchaseOrderNotification: " + e.getMessage());
        } finally {
            if (userDAO != null) userDAO.close();
            if (supplierDAO != null) supplierDAO.close();
            if (materialDAO != null) materialDAO.close();
        }
    }

}