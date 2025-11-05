package controller;

import dal.ImportDAO;
import dal.ImportDetailDAO;
import dal.MaterialDAO;
import dal.PurchaseOrderDAO;
import dal.WarehouseDAO;
import dal.WarehouseRackDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Material;
import entity.PurchaseOrder;
import entity.PurchaseOrderDetail;
import entity.User;
import entity.Warehouse;
import entity.WarehouseRack;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ImportValidator;
import utils.PermissionHelper;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ImportMaterialServlet.class.getName());

    /**
     * Handle AJAX request to get racks by warehouse ID
     */
    private void handleGetRacksByWarehouse(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String warehouseIdStr = request.getParameter("warehouseId");
        if (warehouseIdStr == null || warehouseIdStr.trim().isEmpty()) {
            response.getWriter().write("{\"racks\":[]}");
            return;
        }
        
        WarehouseRackDAO rackDAO = null;
        try {
            rackDAO = new WarehouseRackDAO();
            Integer warehouseId = Integer.parseInt(warehouseIdStr);
            List<WarehouseRack> racks = rackDAO.getAvailableRacksByWarehouseId(warehouseId);
            
            // Build JSON response
            java.io.PrintWriter out = response.getWriter();
            out.print("{\"racks\":[");
            
            if (racks != null && !racks.isEmpty()) {
                for (int i = 0; i < racks.size(); i++) {
                    WarehouseRack rack = racks.get(i);
                    if (i > 0) out.print(",");
                    out.print("{");
                    out.print("\"rackId\":" + rack.getRackId() + ",");
                    out.print("\"rackCode\":\"" + escapeJson(rack.getRackCode() != null ? rack.getRackCode() : "") + "\",");
                    out.print("\"rackName\":\"" + escapeJson(rack.getRackName() != null ? rack.getRackName() : "") + "\",");
                    out.print("\"capacityVolume\":" + (rack.getCapacityVolume() != null ? rack.getCapacityVolume() : "0") + ",");
                    out.print("\"capacityWeight\":" + (rack.getCapacityWeight() != null ? rack.getCapacityWeight() : "0") + ",");
                    out.print("\"currentVolume\":" + (rack.getCurrentVolume() != null ? rack.getCurrentVolume() : "0") + ",");
                    out.print("\"currentWeight\":" + (rack.getCurrentWeight() != null ? rack.getCurrentWeight() : "0") + ",");
                    double volumePercent = 0;
                    if (rack.getCapacityVolume() != null && rack.getCapacityVolume().doubleValue() > 0) {
                        double currentVol = rack.getCurrentVolume() != null ? rack.getCurrentVolume().doubleValue() : 0;
                        volumePercent = (currentVol / rack.getCapacityVolume().doubleValue()) * 100;
                    }
                    out.print("\"volumePercent\":" + volumePercent);
                    out.print("}");
                }
            }
            
            out.print("]}");
            out.flush();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting racks by warehouse", e);
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            if (rackDAO != null) rackDAO.close();
        }
    }

    /**
     * Handle AJAX request to get Purchase Order details
     */
    private void handleGetPODetails(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String poIdStr = request.getParameter("poId");
        if (poIdStr == null || poIdStr.trim().isEmpty()) {
            response.getWriter().write("{\"error\": \"PO ID is required\"}");
            return;
        }
        
        PurchaseOrderDAO purchaseOrderDAO = null;
        try {
            purchaseOrderDAO = new PurchaseOrderDAO();
            int poId = Integer.parseInt(poIdStr);
            PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
            
            if (po == null) {
                response.getWriter().write("{\"error\": \"Purchase Order not found\"}");
                return;
            }
            
            if (!"sent_to_supplier".equalsIgnoreCase(po.getStatus())) {
                response.getWriter().write("{\"error\": \"Purchase Order status is not 'sent_to_supplier'\"}");
                return;
            }
            
            // Get PO details
            List<PurchaseOrderDetail> details = purchaseOrderDAO.getPurchaseOrderDetails(poId);
            
            // Extract supplier ID from first detail (all details should have same supplier)
            Integer supplierId = null;
            if (details != null && !details.isEmpty()) {
                supplierId = details.get(0).getSupplierId();
            }
            
            // Build JSON response
            java.io.PrintWriter out = response.getWriter();
            out.print("{");
            out.print("\"poId\":" + po.getPoId() + ",");
            out.print("\"poCode\":\"" + (po.getPoCode() != null ? po.getPoCode() : "") + "\",");
            out.print("\"supplierId\":" + (supplierId != null ? supplierId : "null") + ",");
            out.print("\"details\":[");
            
            if (details != null && !details.isEmpty()) {
                for (int i = 0; i < details.size(); i++) {
                    PurchaseOrderDetail detail = details.get(i);
                    if (i > 0) out.print(",");
                    out.print("{");
                    out.print("\"materialId\":" + detail.getMaterialId() + ",");
                    out.print("\"materialName\":\"" + escapeJson(detail.getMaterialName() != null ? detail.getMaterialName() : "") + "\",");
                    out.print("\"materialImageUrl\":\"" + escapeJson(detail.getMaterialImageUrl() != null ? detail.getMaterialImageUrl() : "") + "\",");
                    out.print("\"quantity\":" + detail.getQuantity() + ",");
                    out.print("\"unitPrice\":" + detail.getUnitPrice() + ",");
                    out.print("\"categoryId\":" + detail.getCategoryId() + ",");
                    out.print("\"categoryName\":\"" + escapeJson(detail.getCategoryName() != null ? detail.getCategoryName() : "") + "\"");
                    out.print("}");
                }
            }
            
            out.print("]}");
            out.flush();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting PO details", e);
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            if (purchaseOrderDAO != null) purchaseOrderDAO.close();
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        // Handle AJAX request for PO details
        if ("getPODetails".equals(action)) {
            handleGetPODetails(request, response);
            return;
        }
        
        // Handle AJAX request for racks by warehouse
        if ("getRacksByWarehouse".equals(action)) {
            handleGetRacksByWarehouse(request, response);
            return;
        }
        
        // Handle normal GET request to show import form
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Check permission: Admin has full access, others need CREATE_IMPORT permission
        if (!PermissionHelper.hasPermission(user, "CREATE_IMPORT")) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        ImportDAO importDAO = null;
        MaterialDAO materialDAO = null;
        PurchaseOrderDAO purchaseOrderDAO = null;
        WarehouseDAO warehouseDAO = null;

        try {
            importDAO = new ImportDAO();
            materialDAO = new MaterialDAO();
            purchaseOrderDAO = new PurchaseOrderDAO();
            warehouseDAO = new WarehouseDAO();

            // Generate next import code
            String nextImportCode = importDAO.generateNextImportCode();
            request.setAttribute("nextImportCode", nextImportCode);

            // Get all materials for autocomplete
            List<Material> materials = materialDAO.getAllProducts();
            request.setAttribute("materials", materials);

            // Get Purchase Orders with status 'sent_to_supplier' for dropdown
            List<PurchaseOrder> purchaseOrders = purchaseOrderDAO.getPurchaseOrdersByStatus("sent_to_supplier");
            request.setAttribute("purchaseOrders", purchaseOrders);

            // Get all warehouses for dropdown
            List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
            request.setAttribute("warehouses", warehouses);

            request.getRequestDispatcher("ImportMaterialForm.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for ImportMaterialServlet", e);
            request.setAttribute("error", "Error loading import form: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (importDAO != null) importDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (purchaseOrderDAO != null) purchaseOrderDAO.close();
            if (warehouseDAO != null) warehouseDAO.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Check permission: Admin has full access, others need CREATE_IMPORT permission
        if (!PermissionHelper.hasPermission(user, "CREATE_IMPORT")) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Read form data
        String importCode = request.getParameter("importCode");
        String poIdStr = request.getParameter("poId"); // Purchase Order ID instead of supplier
        String supplierIdStr = request.getParameter("supplierId"); // Will be extracted from PO if poId is provided
        String importDateStr = request.getParameter("importDate");
        String note = request.getParameter("note");

        String[] materialIds = request.getParameterValues("materialId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] unitPrices = request.getParameterValues("unitPrice[]");
        String[] rackIds = request.getParameterValues("rackId[]");

        // Log received parameters
        LOGGER.log(Level.INFO, "Import form submitted - Code: {0}, PO ID: {1}, Supplier ID: {2}, Date: {3}, Materials: {4}",
                new Object[]{importCode, poIdStr, supplierIdStr, importDateStr, 
                    materialIds != null ? materialIds.length : 0});

        Map<String, String> errors = new HashMap<>();

        // Validate import code
        String importCodeError = ImportValidator.validateImportCode(importCode);
        if (importCodeError != null) {
            errors.put("importCode", importCodeError);
        }

        // Validate supplier (optional)
        String supplierError = ImportValidator.validateSupplierId(supplierIdStr);
        if (supplierError != null) {
            errors.put("supplierId", supplierError);
        }

        // Validate import details
        Map<String, String> detailErrors = ImportValidator.validateImportDetails(
                materialIds, quantities, unitPrices, rackIds);
        errors.putAll(detailErrors);

        // Check for duplicate materials
        if (materialIds != null && materialIds.length > 0) {
            Map<String, String> duplicateErrors = ImportValidator.checkDuplicateMaterials(materialIds);
            errors.putAll(duplicateErrors);
        }

        // If there are errors, go back to form
        if (!errors.isEmpty()) {
            LOGGER.log(Level.WARNING, "Validation errors found: {0}", errors);
            request.setAttribute("errors", errors);
            request.setAttribute("submittedImportCode", importCode);
            request.setAttribute("submittedPoId", poIdStr);
            request.setAttribute("submittedSupplierId", supplierIdStr);
            request.setAttribute("submittedImportDate", importDateStr);
            request.setAttribute("submittedNote", note);
            request.setAttribute("submittedMaterialIds", materialIds);
            request.setAttribute("submittedQuantities", quantities);
            request.setAttribute("submittedUnitPrices", unitPrices);
            request.setAttribute("submittedRackIds", rackIds);
            doGet(request, response);
            return;
        }

        LOGGER.log(Level.INFO, "Validation passed, proceeding to create import");

        // Process import
        ImportDAO importDAO = null;
        ImportDetailDAO detailDAO = null;

        try {
            importDAO = new ImportDAO();
            detailDAO = new ImportDetailDAO();

            // Extract supplier from PO if poId is provided
            PurchaseOrderDAO purchaseOrderDAO = null;
            if (poIdStr != null && !poIdStr.trim().isEmpty()) {
                try {
                    purchaseOrderDAO = new PurchaseOrderDAO();
                    int poId = Integer.parseInt(poIdStr);
                    PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
                    
                    if (po != null && "sent_to_supplier".equalsIgnoreCase(po.getStatus())) {
                        // Get supplier from first PO detail
                        List<PurchaseOrderDetail> poDetails = purchaseOrderDAO.getPurchaseOrderDetails(poId);
                        if (poDetails != null && !poDetails.isEmpty() && poDetails.get(0).getSupplierId() != null) {
                            supplierIdStr = String.valueOf(poDetails.get(0).getSupplierId());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error extracting supplier from PO: " + e.getMessage(), e);
                } finally {
                    if (purchaseOrderDAO != null) purchaseOrderDAO.close();
                }
            }

            // Create Import record
            Import importObj = new Import();
            importObj.setImportCode(importCode);
            importObj.setImportedBy(user.getUserId());

            if (supplierIdStr != null && !supplierIdStr.trim().isEmpty()) {
                importObj.setSupplierId(Integer.parseInt(supplierIdStr));
            }

            // Parse import date or use current time
            if (importDateStr != null && !importDateStr.trim().isEmpty()) {
                importObj.setImportDate(LocalDateTime.parse(importDateStr + "T00:00:00"));
            } else {
                importObj.setImportDate(LocalDateTime.now());
            }

            importObj.setNote(note);

            int importId = importDAO.createImport(importObj);
            LOGGER.log(Level.INFO, "Import created with ID: {0}", importId);

            if (importId > 0) {
                LOGGER.log(Level.INFO, "Creating {0} import details", materialIds.length);
                // Create Import_Details
                boolean allDetailsAdded = true;
                for (int i = 0; i < materialIds.length; i++) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportId(importId);
                    detail.setMaterialId(Integer.parseInt(materialIds[i]));

                    // Rack ID is optional
                    if (rackIds != null && i < rackIds.length && 
                        rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                        detail.setRackId(Integer.parseInt(rackIds[i]));
                    }

                    detail.setQuantity(new BigDecimal(quantities[i]));
                    
                    // Unit price is optional - default to 0 if not provided
                    if (unitPrices != null && i < unitPrices.length && 
                        unitPrices[i] != null && !unitPrices[i].trim().isEmpty()) {
                        detail.setUnitPrice(new BigDecimal(unitPrices[i]));
                    } else {
                        detail.setUnitPrice(BigDecimal.ZERO);
                    }
                    
                    detail.setStatus("imported");

                    boolean added = detailDAO.addImportDetail(detail);
                    if (!added) {
                        allDetailsAdded = false;
                        LOGGER.log(Level.WARNING, "Failed to add import detail for material ID: " + materialIds[i]);
                    }
                }

                if (allDetailsAdded) {
                    response.sendRedirect("ImportList?success=Import created successfully");
                } else {
                    response.sendRedirect("ImportList?warning=Import created but some details failed");
                }
            } else {
                request.setAttribute("error", "Failed to create import record");
                doGet(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing import", e);
            request.setAttribute("error", "Error processing import: " + e.getMessage());
            request.setAttribute("submittedImportCode", importCode);
            request.setAttribute("submittedPoId", poIdStr);
            request.setAttribute("submittedSupplierId", supplierIdStr);
            request.setAttribute("submittedImportDate", importDateStr);
            request.setAttribute("submittedNote", note);
            request.setAttribute("submittedMaterialIds", materialIds);
            request.setAttribute("submittedQuantities", quantities);
            request.setAttribute("submittedUnitPrices", unitPrices);
            request.setAttribute("submittedRackIds", rackIds);
            doGet(request, response);
        } finally {
            if (importDAO != null) importDAO.close();
            if (detailDAO != null) detailDAO.close();
        }
    }
}
