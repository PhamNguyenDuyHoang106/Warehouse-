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
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
            
            String poStatus = po.getStatus() != null ? po.getStatus().toLowerCase() : "";
            if (!"sent".equals(poStatus) && !"partially_received".equals(poStatus)) {
                response.getWriter().write("{\"error\": \"Purchase Order must be sent to supplier or partially received\"}");
                return;
            }
            
            // Get PO details
            List<PurchaseOrderDetail> details = purchaseOrderDAO.getPurchaseOrderDetails(poId);
            
            Integer supplierId = po.getSupplierId();
            
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
                    BigDecimal orderedQty = detail.getQuantityOrdered() != null ? detail.getQuantityOrdered() : BigDecimal.ZERO;
                    BigDecimal receivedQty = detail.getReceivedQuantity() != null ? detail.getReceivedQuantity() : BigDecimal.ZERO;
                    BigDecimal remainingQty = orderedQty.subtract(receivedQty);
                    if (remainingQty.compareTo(BigDecimal.ZERO) < 0) {
                        remainingQty = BigDecimal.ZERO;
                    }
                    out.print("\"poDetailId\":" + detail.getPoDetailId() + ",");
                    out.print("\"materialId\":" + detail.getMaterialId() + ",");
                    out.print("\"materialName\":\"" + escapeJson(detail.getMaterialName() != null ? detail.getMaterialName() : "") + "\",");
                    out.print("\"materialImageUrl\":\"" + escapeJson(detail.getMaterialImageUrl() != null ? detail.getMaterialImageUrl() : "") + "\",");
                    out.print("\"unitId\":" + (detail.getUnitId() != null ? detail.getUnitId() : "null") + ",");
                    out.print("\"unitName\":\"" + escapeJson(detail.getUnitName() != null ? detail.getUnitName() : "") + "\",");
                    out.print("\"quantity\":" + orderedQty + ",");
                    out.print("\"receivedQuantity\":" + receivedQty + ",");
                    out.print("\"remainingQuantity\":" + remainingQty + ",");
                    out.print("\"unitPrice\":" + (detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO) + ",");
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

        // Admin có toàn quyền - PermissionHelper đã xử lý
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Tạo nhập kho")) {
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

            // Get Purchase Orders eligible for receiving (only sent orders can be imported)
            Map<Integer, PurchaseOrder> purchaseOrderMap = new LinkedHashMap<>();
            for (String status : new String[]{"sent", "partially_received"}) {
                List<PurchaseOrder> pos = purchaseOrderDAO.getPurchaseOrdersByStatus(status);
                for (PurchaseOrder po : pos) {
                    purchaseOrderMap.putIfAbsent(po.getPoId(), po);
                }
            }
            request.setAttribute("purchaseOrders", new ArrayList<>(purchaseOrderMap.values()));

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

        // Admin có toàn quyền - PermissionHelper đã xử lý
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Tạo nhập kho")) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Read form data
        String importCode = request.getParameter("importCode");
        String poIdStr = request.getParameter("poId");
        String warehouseIdStr = request.getParameter("warehouseId");
        String importDateStr = request.getParameter("importDate");
        String note = request.getParameter("note");

        String[] materialIds = request.getParameterValues("materialId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] unitPrices = request.getParameterValues("unitPrice[]");
        String[] rackIds = request.getParameterValues("rackId[]");
        String[] poDetailIds = request.getParameterValues("poDetailId[]");
        String[] unitIds = request.getParameterValues("unitId[]");
        String[] batchCodes = request.getParameterValues("batchCode[]");
        String[] expiryDates = request.getParameterValues("expiryDate[]");

        LOGGER.log(Level.INFO, "Import form submitted - Code: {0}, PO ID: {1}, Warehouse ID: {2}, Date: {3}, Materials: {4}",
                new Object[]{importCode, poIdStr, warehouseIdStr, importDateStr,
                    materialIds != null ? materialIds.length : 0});

        Map<String, String> errors = new HashMap<>();

        // Validate import code
        String importCodeError = ImportValidator.validateImportCode(importCode);
        if (importCodeError != null) {
            errors.put("importCode", importCodeError);
        }

        if (poIdStr == null || poIdStr.trim().isEmpty()) {
            errors.put("poId", "Purchase Order is required");
        }

        if (warehouseIdStr == null || warehouseIdStr.trim().isEmpty()) {
            errors.put("warehouseId", "Warehouse is required");
        }

        if (materialIds == null || materialIds.length == 0) {
            errors.put("materials", "At least one material is required");
        }

        // Check for duplicate materials
        if (materialIds != null && materialIds.length > 0) {
            Map<String, String> duplicateErrors = ImportValidator.checkDuplicateMaterials(materialIds);
            errors.putAll(duplicateErrors);
        }

        if (poDetailIds == null || materialIds == null || poDetailIds.length != materialIds.length) {
            errors.put("poDetails", "Purchase Order detail mapping is required for each material");
        }

        if (!errors.isEmpty()) {
            LOGGER.log(Level.WARNING, "Validation errors found: {0}", errors);
            request.setAttribute("errors", errors);
            preserveFormState(request, importCode, poIdStr, warehouseIdStr, importDateStr, note,
                    materialIds, quantities, unitPrices, rackIds, batchCodes, expiryDates);
            doGet(request, response);
            return;
        }

        LOGGER.log(Level.INFO, "Validation passed, proceeding to create import");

        ImportDAO importDAO = null;
        ImportDetailDAO detailDAO = null;
        MaterialDAO materialDAO = null;
        PurchaseOrderDAO purchaseOrderDAO = null;

        try {
            importDAO = new ImportDAO();
            detailDAO = new ImportDetailDAO();
            materialDAO = new MaterialDAO();
            purchaseOrderDAO = new PurchaseOrderDAO();

            int poId;
            int warehouseId;
            try {
                poId = Integer.parseInt(poIdStr);
            } catch (NumberFormatException e) {
                errors.put("poId", "Invalid Purchase Order ID");
                poId = -1;
            }
            try {
                warehouseId = Integer.parseInt(warehouseIdStr);
            } catch (NumberFormatException e) {
                errors.put("warehouseId", "Invalid warehouse");
                warehouseId = -1;
            }

            PurchaseOrder po = poId > 0 ? purchaseOrderDAO.getPurchaseOrderById(poId) : null;
            if (po == null) {
                errors.put("poId", "Purchase Order not found");
            } else {
                String status = po.getStatus() != null ? po.getStatus().toLowerCase() : "";
                if (!"sent".equals(status) && !"partially_received".equals(status)) {
                    errors.put("poId", "Purchase Order must be sent to supplier or partially received");
                }
            }

            List<PurchaseOrderDetail> poDetails = po != null
                    ? purchaseOrderDAO.getPurchaseOrderDetails(po.getPoId())
                    : new ArrayList<>();
            Map<Integer, PurchaseOrderDetail> poDetailMap = new HashMap<>();
            for (PurchaseOrderDetail detail : poDetails) {
                poDetailMap.put(detail.getPoDetailId(), detail);
            }

            List<ImportDetailPayload> payloads = new ArrayList<>();
            BigDecimal totalQuantity = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (int i = 0; i < materialIds.length; i++) {
                String materialIndexKey = "item_" + i;
                int materialId;
                try {
                    materialId = Integer.parseInt(materialIds[i]);
                } catch (NumberFormatException e) {
                    errors.put(materialIndexKey + "_materialId", "Invalid material selected");
                    continue;
                }

                int poDetailId;
                try {
                    poDetailId = Integer.parseInt(poDetailIds[i]);
                } catch (NumberFormatException e) {
                    errors.put(materialIndexKey + "_poDetailId", "Invalid PO detail");
                    continue;
                }

                PurchaseOrderDetail poDetail = poDetailMap.get(poDetailId);
                if (poDetail == null) {
                    errors.put(materialIndexKey + "_poDetailId", "PO detail not found");
                    continue;
                }

                if (poDetail.getMaterialId() != materialId) {
                    errors.put(materialIndexKey + "_materialId", "Material does not match PO detail");
                    continue;
                }

                BigDecimal quantity;
                try {
                    quantity = new BigDecimal(quantities[i]);
                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put(materialIndexKey + "_quantity", "Quantity must be greater than 0");
                        continue;
                    }
                } catch (Exception e) {
                    errors.put(materialIndexKey + "_quantity", "Invalid quantity");
                    continue;
                }

                BigDecimal orderedQty = poDetail.getQuantityOrdered() != null ? poDetail.getQuantityOrdered() : BigDecimal.ZERO;
                BigDecimal receivedQty = poDetail.getReceivedQuantity() != null ? poDetail.getReceivedQuantity() : BigDecimal.ZERO;
                BigDecimal availableQty = orderedQty.subtract(receivedQty);
                if (availableQty.compareTo(BigDecimal.ZERO) < 0) {
                    availableQty = BigDecimal.ZERO;
                }
                if (quantity.compareTo(availableQty) > 0) {
                    errors.put(materialIndexKey + "_quantity", "Quantity exceeds remaining PO amount (" + availableQty + ")");
                    continue;
                }

                BigDecimal unitCost;
                try {
                    if (unitPrices != null && i < unitPrices.length && unitPrices[i] != null && !unitPrices[i].trim().isEmpty()) {
                        unitCost = new BigDecimal(unitPrices[i]);
                    } else {
                        unitCost = poDetail.getUnitPrice() != null ? poDetail.getUnitPrice() : BigDecimal.ZERO;
                    }
                    if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
                        errors.put(materialIndexKey + "_unitPrice", "Unit cost must be positive");
                        continue;
                    }
                } catch (Exception e) {
                    errors.put(materialIndexKey + "_unitPrice", "Invalid unit cost");
                    continue;
                }

                Integer rackId = null;
                if (rackIds != null && i < rackIds.length && rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                    try {
                        rackId = Integer.parseInt(rackIds[i]);
                    } catch (NumberFormatException e) {
                        errors.put(materialIndexKey + "_rackId", "Invalid rack selected");
                    }
                }

                Integer unitId = null;
                if (unitIds != null && i < unitIds.length && unitIds[i] != null && !unitIds[i].trim().isEmpty()) {
                    try {
                        unitId = Integer.parseInt(unitIds[i]);
                    } catch (NumberFormatException e) {
                        errors.put(materialIndexKey + "_unitId", "Invalid unit");
                    }
                }
                if (unitId == null) {
                    unitId = poDetail.getUnitId();
                }
                if (unitId == null) {
                    Material material = materialDAO.getProductById(materialId);
                    if (material != null && material.getDefaultUnit() != null) {
                        unitId = material.getDefaultUnit().getId();
                    }
                }
                if (unitId == null) {
                    errors.put(materialIndexKey + "_unitId", "Unit is required");
                    continue;
                }

                String batchCode = null;
                if (batchCodes != null && i < batchCodes.length && batchCodes[i] != null && !batchCodes[i].trim().isEmpty()) {
                    batchCode = batchCodes[i].trim();
                }
                if (batchCode == null) {
                    batchCode = generateBatchCode(importCode, materialId, i + 1);
                }

                Date expiryDate = null;
                if (expiryDates != null && i < expiryDates.length && expiryDates[i] != null && !expiryDates[i].trim().isEmpty()) {
                    try {
                        expiryDate = Date.valueOf(expiryDates[i]);
                    } catch (IllegalArgumentException e) {
                        errors.put(materialIndexKey + "_expiryDate", "Invalid expiry date");
                    }
                }

                ImportDetailPayload payload = new ImportDetailPayload();
                payload.materialId = materialId;
                payload.poDetailId = poDetailId;
                payload.unitId = unitId;
                payload.rackId = rackId;
                payload.quantity = quantity;
                payload.unitCost = unitCost;
                payload.batchCode = batchCode;
                payload.expiryDate = expiryDate;

                payloads.add(payload);
                totalQuantity = totalQuantity.add(quantity);
                totalAmount = totalAmount.add(unitCost.multiply(quantity));
            }

            if (payloads.isEmpty()) {
                errors.put("materials", "No valid materials to import");
            }

            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                preserveFormState(request, importCode, poIdStr, warehouseIdStr, importDateStr, note,
                        materialIds, quantities, unitPrices, rackIds, batchCodes, expiryDates);
                doGet(request, response);
                return;
            }

            LocalDate importDate;
            try {
                importDate = (importDateStr != null && !importDateStr.trim().isEmpty())
                        ? LocalDate.parse(importDateStr)
                        : LocalDate.now();
            } catch (Exception e) {
                errors.put("importDate", "Invalid import date");
                request.setAttribute("errors", errors);
                preserveFormState(request, importCode, poIdStr, warehouseIdStr, importDateStr, note,
                        materialIds, quantities, unitPrices, rackIds, batchCodes, expiryDates);
                doGet(request, response);
                return;
            }

            Import importObj = new Import();
            importObj.setImportCode(importCode);
            importObj.setPoId(poId);
            importObj.setWarehouseId(warehouseId);
            importObj.setImportDate(importDate);
            importObj.setReceivedBy(user.getUserId());
            importObj.setCreatedBy(user.getUserId());
            importObj.setStatus("completed");
            importObj.setNote(note);
            importObj.setTotalQuantity(totalQuantity);
            importObj.setTotalAmount(totalAmount);

            int importId = importDAO.createImport(importObj);
            LOGGER.log(Level.INFO, "Import created with ID: {0}", importId);

            if (importId > 0) {
                LOGGER.log(Level.INFO, "Creating {0} import details", payloads.size());
                boolean allDetailsAdded = true;
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                for (ImportDetailPayload payload : payloads) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportId(importId);
                    detail.setPoDetailId(payload.poDetailId);
                    detail.setMaterialId(payload.materialId);
                    detail.setUnitId(payload.unitId);
                    detail.setRackId(payload.rackId);
                    detail.setQuantity(payload.quantity);
                    detail.setUnitCost(payload.unitCost);
                    detail.setBatchCode(payload.batchCode);
                    detail.setExpiryDate(payload.expiryDate);
                    detail.setStatus("imported");
                    detail.setImportedBy(user.getUserId());
                    detail.setImportedAt(now);
                    detail.setCreatedAt(now);

                    boolean added = detailDAO.addImportDetail(detail);
                    if (!added) {
                        allDetailsAdded = false;
                        LOGGER.log(Level.WARNING, "Failed to add import detail for material ID: {0}", payload.materialId);
                    }
                }

                if (allDetailsAdded) {
                    response.sendRedirect("ImportList?success=Import created successfully");
                } else {
                    response.sendRedirect("ImportList?warning=Import created but some details failed");
                }
            } else {
                request.setAttribute("error", "Failed to create import record");
                preserveFormState(request, importCode, poIdStr, warehouseIdStr, importDateStr, note,
                        materialIds, quantities, unitPrices, rackIds, batchCodes, expiryDates);
                doGet(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing import", e);
            request.setAttribute("error", "Error processing import: " + e.getMessage());
            preserveFormState(request, importCode, poIdStr, warehouseIdStr, importDateStr, note,
                    materialIds, quantities, unitPrices, rackIds, batchCodes, expiryDates);
            doGet(request, response);
        } finally {
            if (importDAO != null) importDAO.close();
            if (detailDAO != null) detailDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (purchaseOrderDAO != null) purchaseOrderDAO.close();
        }
    }

    private void preserveFormState(HttpServletRequest request, String importCode, String poId,
                                   String warehouseId, String importDate, String note,
                                   String[] materialIds, String[] quantities, String[] unitPrices,
                                   String[] rackIds, String[] batchCodes, String[] expiryDates) {
        request.setAttribute("submittedImportCode", importCode);
        request.setAttribute("submittedPoId", poId);
        request.setAttribute("submittedWarehouseId", warehouseId);
        request.setAttribute("submittedImportDate", importDate);
        request.setAttribute("submittedNote", note);
        request.setAttribute("submittedMaterialIds", materialIds);
        request.setAttribute("submittedQuantities", quantities);
        request.setAttribute("submittedUnitPrices", unitPrices);
        request.setAttribute("submittedRackIds", rackIds);
        request.setAttribute("submittedBatchCodes", batchCodes);
        request.setAttribute("submittedExpiryDates", expiryDates);
    }

    private String generateBatchCode(String importCode, int materialId, int rowIndex) {
        String prefix = (importCode != null && !importCode.trim().isEmpty()) ? importCode : "IMP";
        return prefix + "-MAT" + materialId + "-" + rowIndex;
    }

    private static class ImportDetailPayload {
        int materialId;
        int poDetailId;
        Integer unitId;
        Integer rackId;
        BigDecimal quantity;
        BigDecimal unitCost;
        String batchCode;
        Date expiryDate;
    }
}
