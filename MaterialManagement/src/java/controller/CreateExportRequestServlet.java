package controller;

import dal.CustomerDAO;
import dal.ExportRequestDAO;
import dal.ExportRequestDetailDAO;
import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.PricingDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import dal.WarehouseDAO;
import dal.WarehouseRackDAO;
import dto.MaterialPriceInfo;
import dto.MaterialWarehouseStock;
import entity.Customer;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.Material;
import entity.User;
import entity.Warehouse;
import entity.WarehouseRack;
import java.util.stream.Collectors;
import utils.PermissionHelper;
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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import utils.EmailUtils;
import utils.ExportRequestValidator;

@WebServlet(name = "CreateExportRequestServlet", urlPatterns = {"/CreateExportRequest"})
public class CreateExportRequestServlet extends BaseServlet {

    private ExportRequestDAO exportRequestDAO;
    private ExportRequestDetailDAO exportRequestDetailDAO;
    private MaterialDAO materialDAO;
    private CustomerDAO customerDAO;
    private WarehouseRackDAO rackDAO;
    private WarehouseDAO warehouseDAO;
    private InventoryDAO inventoryDAO;
    private PricingDAO pricingDAO;
    private UserDAO userDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        exportRequestDAO = new ExportRequestDAO();
        exportRequestDetailDAO = new ExportRequestDetailDAO();
        materialDAO = new MaterialDAO();
        customerDAO = new CustomerDAO();
        rackDAO = new WarehouseRackDAO();
        warehouseDAO = new WarehouseDAO();
        inventoryDAO = new InventoryDAO();
        userDAO = new UserDAO();
        rolePermissionDAO = new RolePermissionDAO();
        pricingDAO = new PricingDAO();
        
        registerDAO(exportRequestDAO);
        registerDAO(exportRequestDetailDAO);
        registerDAO(materialDAO);
        registerDAO(customerDAO);
        registerDAO(rackDAO);
        registerDAO(warehouseDAO);
        registerDAO(inventoryDAO);
        registerDAO(pricingDAO);
        registerDAO(userDAO);
        registerDAO(rolePermissionDAO);
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
        boolean hasPermission = PermissionHelper.hasPermission(user, "Tạo yêu cầu xuất");
        request.setAttribute("hasCreateExportRequestPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create export requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        try {
            String requestCode = generateRequestCode();
            request.setAttribute("requestCode", requestCode);
            loadReferenceData(request);
            request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.setAttribute("materials", new ArrayList<Material>());
            request.setAttribute("customers", new ArrayList<Customer>());
            request.setAttribute("warehouses", new ArrayList<Warehouse>());
            request.setAttribute("racks", new ArrayList<WarehouseRack>());
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
        boolean hasPermission = PermissionHelper.hasPermission(user, "Tạo yêu cầu xuất");
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
            String customerIdStr = request.getParameter("customerId");
            String[] materialNames = request.getParameterValues("materialName[]");
            String[] materialIds = request.getParameterValues("materialId[]");
            String[] quantities = request.getParameterValues("quantity[]");
            String[] unitPriceExports = request.getParameterValues("unitPriceExport[]"); // V8: Giá xuất
            String[] notes = request.getParameterValues("note[]");
            String[] warehouseIds = request.getParameterValues("warehouseId[]");
            
            // Validate form data using ExportRequestValidator
            Map<String, String> formErrors = ExportRequestValidator.validateExportRequestFormData(reason, deliveryDateStr);
            
            // Validate customerId
            if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
                formErrors.put("customerId", "Please select a customer");
            }
            
            if (!formErrors.isEmpty()) {
                // Set submitted data to repopulate form
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedDeliveryDate", deliveryDateStr);
                request.setAttribute("submittedCustomerId", customerIdStr);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedMaterialIds", materialIds);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                request.setAttribute("submittedWarehouses", warehouseIds);
                request.setAttribute("errors", formErrors);
                
                String newRequestCode = generateRequestCode();
                request.setAttribute("requestCode", newRequestCode);
                loadReferenceData(request);
                request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
                return;
            }
            
            Date deliveryDate = Date.valueOf(deliveryDateStr);
            int customerId = Integer.parseInt(customerIdStr);
            
            ExportRequest exportRequest = new ExportRequest();
            exportRequest.setRequestCode(requestCode);
            exportRequest.setDeliveryDate(deliveryDate);
            exportRequest.setReason(reason);
            exportRequest.setUserId(user.getUserId());
            exportRequest.setCustomerId(customerId); // Set customer from form
            exportRequest.setStatus("pending");
            // V8: Validate unit_price_export (REQUIRED for profit calculation)
            if (materialIds == null || unitPriceExports == null || unitPriceExports.length != materialIds.length) {
                request.setAttribute("error", "Unit price (export) is required for all materials");
                doGet(request, response);
                return;
            }
            
            for (int i = 0; i < unitPriceExports.length; i++) {
                if (unitPriceExports[i] == null || unitPriceExports[i].trim().isEmpty()) {
                    request.setAttribute("error", "Unit price (export) is required for material at position " + (i + 1));
                    doGet(request, response);
                    return;
                }
            }

            if (warehouseIds == null || materialIds == null || warehouseIds.length != materialIds.length) {
                request.setAttribute("error", "Please select a warehouse for each material.");
                doGet(request, response);
                return;
            }

            if (quantities == null || quantities.length != materialIds.length) {
                request.setAttribute("error", "Please enter quantity for each material.");
                doGet(request, response);
                return;
            }
            
            // Validate material details
            Map<String, String> detailErrors = ExportRequestValidator.validateExportRequestDetails(materialNames, quantities);
            
            if (!detailErrors.isEmpty()) {
                // Set submitted data to repopulate form
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedDeliveryDate", deliveryDateStr);
                request.setAttribute("submittedCustomerId", customerIdStr);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedMaterialIds", materialIds);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                request.setAttribute("submittedWarehouses", warehouseIds);
                request.setAttribute("errors", detailErrors);
                
                String newRequestCode = generateRequestCode();
                request.setAttribute("requestCode", newRequestCode);
                loadReferenceData(request);
                request.getRequestDispatcher("CreateExportRequest.jsp").forward(request, response);
                return;
            }
            
            // Validate materialIds
            if (materialIds == null || materialIds.length == 0) {
                throw new Exception("Please select at least one material");
            }
            
            Map<Integer, BigDecimal> materialQuantityMap = new HashMap<>();
            List<Integer> materialIdList = new ArrayList<>();
            List<ExportRequestDetail> details = new ArrayList<>();
            for (int i = 0; i < materialIds.length; i++) {
                int materialId = 0;
                if (materialIds[i] != null && !materialIds[i].trim().isEmpty()) {
                    try {
                        materialId = Integer.parseInt(materialIds[i]);
                    } catch (NumberFormatException e) {
                        throw new Exception("Invalid material ID at row " + (i + 1));
                    }
                }
                if (materialId <= 0) {
                    throw new Exception("Invalid material selected at row " + (i + 1) + ". Please select from the dropdown list.");
                }
                BigDecimal quantity = new BigDecimal(quantities[i]);
                BigDecimal unitPriceExport = new BigDecimal(unitPriceExports[i]); // V8: Giá xuất
                Integer rackId = null; // No rack selection in simplified form
                int warehouseId;
                try {
                    warehouseId = Integer.parseInt(warehouseIds[i]);
                } catch (NumberFormatException ex) {
                    throw new Exception("Please select a warehouse for material at row " + (i + 1));
                }
                if (warehouseId <= 0) {
                    throw new Exception("Please select a warehouse for material at row " + (i + 1));
                }

                BigDecimal availableStock = inventoryDAO.getAvailableStock(materialId, warehouseId);
                if (quantity.compareTo(availableStock) > 0) {
                    throw new Exception("Not enough stock in the selected warehouse for material at row " + (i + 1)
                            + ". Available: " + availableStock);
                }

                materialQuantityMap.put(materialId, materialQuantityMap.getOrDefault(materialId, BigDecimal.ZERO).add(quantity));
                materialIdList.add(materialId);
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setMaterialId(materialId);
                detail.setRackId(rackId);
                detail.setWarehouseId(warehouseId);
                detail.setQuantity(quantity);
                detail.setUnitPriceExport(unitPriceExport); // V8: Set giá xuất
                if (notes != null && i < notes.length) {
                    detail.setNote(notes[i]);
                }
                details.add(detail);
            }
            for (Integer materialId : materialQuantityMap.keySet()) {
                BigDecimal totalQuantity = materialQuantityMap.get(materialId);
                Material material = materialDAO.getInformation(materialId);
                BigDecimal stock = material.getQuantity();
                if (totalQuantity.compareTo(stock) > 0) {
                    throw new Exception("Not enough stock for material: " + material.getMaterialName());
                }
            }

            List<Integer> distinctMaterialIds = materialIdList.stream().distinct().collect(Collectors.toList());
            Map<Integer, MaterialPriceInfo> priceInfoMap = pricingDAO.getPriceInfoForMaterials(distinctMaterialIds);
            for (int i = 0; i < details.size(); i++) {
                ExportRequestDetail detail = details.get(i);
                MaterialPriceInfo priceInfo = priceInfoMap.get(detail.getMaterialId());
                if (priceInfo != null && priceInfo.getMinPrice() != null
                        && detail.getUnitPriceExport().compareTo(priceInfo.getMinPrice()) < 0) {
                    throw new Exception("Unit price is below allowed cost for material at row " + (i + 1));
                }
                if (priceInfo != null && priceInfo.getMaxPrice() != null
                        && detail.getUnitPriceExport().compareTo(priceInfo.getMaxPrice()) > 0) {
                    throw new Exception("Unit price is above allowed range for material at row " + (i + 1));
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
        String sql = "SELECT er_code FROM Export_Requests WHERE er_code LIKE ? ORDER BY er_code DESC LIMIT 1";
        String likePattern = prefix + "%";
        try (java.sql.Connection conn = exportRequestDAO.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, likePattern);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                int nextSeq = 1;
                if (rs.next()) {
                    String lastCode = rs.getString("er_code");
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

    private void loadReferenceData(HttpServletRequest request) {
        List<Material> materials = materialDAO.searchMaterials(null, null, 1, 1000, "name_asc");
        request.setAttribute("materials", materials);
        List<Customer> customers = customerDAO.getAllCustomers();
        request.setAttribute("customers", customers);
        List<WarehouseRack> racks = rackDAO.getAvailableRacks();
        request.setAttribute("racks", racks);
        List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
        request.setAttribute("warehouses", warehouses);

        List<Integer> materialIds = materials.stream()
                .map(Material::getMaterialId)
                .collect(Collectors.toList());

        List<MaterialWarehouseStock> stockList = inventoryDAO.getMaterialWarehouseAvailability(materialIds);
        Map<Integer, List<MaterialWarehouseStock>> availability = stockList.stream()
                .collect(Collectors.groupingBy(MaterialWarehouseStock::getMaterialId));
        request.setAttribute("materialAvailability", availability);

        Map<Integer, MaterialPriceInfo> pricingMap = pricingDAO.getPriceInfoForMaterials(materialIds);
        request.setAttribute("materialPricing", pricingMap);
    }
}