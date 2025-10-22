package controller;

import dal.ImportDAO;
import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import dal.RolePermissionDAO;
import dal.PurchaseOrderDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Material;
import entity.Supplier;
import entity.User;
import entity.PurchaseOrder;
import entity.PurchaseOrderDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ImportValidator;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ImportMaterialServlet.class.getName());
    private ImportDAO importDAO;
    private SupplierDAO supplierDAO;
    private MaterialDAO materialDAO;
    private InventoryDAO inventoryDAO;
    private RolePermissionDAO rolePermissionDAO;
    private PurchaseOrderDAO purchaseOrderDAO;

    @Override
    public void init() throws ServletException {
        importDAO = new ImportDAO();
        supplierDAO = new SupplierDAO();
        materialDAO = new MaterialDAO();
        inventoryDAO = new InventoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
        purchaseOrderDAO = new PurchaseOrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(currentUser.getRoleId(), "IMPORT_MATERIAL");
        request.setAttribute("hasImportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        safeLoadDataAndForward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "IMPORT_MATERIAL");
        request.setAttribute("hasImportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to import materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        try {
            switch (action) {
                case "add":
                    handleAddMaterial(request, response, session, user);
                    break;
                case "import":
                    handleImport(request, response, session, user);
                    break;
                case "remove":
                    handleRemoveMaterial(request, response, session);
                    break;
                case "update":
                    handleUpdateQuantity(request, response, session);
                    break;
                case "updatePrice":
                    handleUpdatePrice(request, response, session);
                    break;
                case "loadFromPurchaseOrder":
                    handleLoadFromPurchaseOrder(request, response, session, user);
                    break;
                case "reset":
                    handleReset(request, response, session);
                    break;
                default:
                    request.setAttribute("error", "Invalid action.");
                    safeLoadDataAndForward(request, response);
                    break;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid number format provided for action: " + action, e);
            request.setAttribute("error", "Invalid number format provided.");
            safeLoadDataAndForward(request, response);
        }
    }

    private void handleLoadFromPurchaseOrder(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException {
        String purchaseOrderIdStr = request.getParameter("purchaseOrderId");
        String purchaseOrderName = request.getParameter("purchaseOrderName");
        
        if ((purchaseOrderIdStr == null || purchaseOrderIdStr.trim().isEmpty()) && 
            (purchaseOrderName == null || purchaseOrderName.trim().isEmpty())) {
            request.setAttribute("error", "Please enter a purchase order code or request code.");
            safeLoadDataAndForward(request, response);
            return;
        }

        try {
            int purchaseOrderId;
            if (purchaseOrderIdStr != null && !purchaseOrderIdStr.trim().isEmpty()) {
                purchaseOrderId = Integer.parseInt(purchaseOrderIdStr);
            } else {
                // Search by PO code or request code
                purchaseOrderId = purchaseOrderDAO.getPurchaseOrderIdByCodeOrRequest(purchaseOrderName.trim());
                if (purchaseOrderId == 0) {
                    request.setAttribute("error", "Purchase order not found with code: " + purchaseOrderName);
                    safeLoadDataAndForward(request, response);
                    return;
                }
            }
            
            try {
                PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(purchaseOrderId);
                
                if (purchaseOrder == null) {
                    request.setAttribute("error", "Purchase order not found.");
                    safeLoadDataAndForward(request, response);
                    return;
                }

                if (!"sent_to_supplier".equalsIgnoreCase(purchaseOrder.getStatus())) {
                    request.setAttribute("error", "Only purchase orders with status 'sent_to_supplier' can be used for import.");
                    safeLoadDataAndForward(request, response);
                    return;
                }

                // Clear existing temp import
                Integer existingTempImportId = (Integer) session.getAttribute("tempImportId");
                if (existingTempImportId != null && existingTempImportId != 0) {
                    importDAO.deleteImport(existingTempImportId);
                }

                // Create new temp import
                Import imports = new Import();
                imports.setImportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
                imports.setImportDate(LocalDateTime.now());
                imports.setImportedBy(user.getUserId());
                int tempImportId = importDAO.createImport(imports);
                session.setAttribute("tempImportId", tempImportId);

                // Load purchase order details and convert to import details
                List<PurchaseOrderDetail> poDetails = purchaseOrderDAO.getPurchaseOrderDetails(purchaseOrderId);
                List<ImportDetail> importDetails = new ArrayList<>();

                for (PurchaseOrderDetail poDetail : poDetails) {
                    ImportDetail importDetail = new ImportDetail();
                    importDetail.setImportId(tempImportId);
                    importDetail.setMaterialId(poDetail.getMaterialId());
                    importDetail.setQuantity(poDetail.getQuantity());
                    importDetail.setUnitPrice(poDetail.getUnitPrice().doubleValue());
                    importDetail.setStatus("draft");
                    importDetails.add(importDetail);
                }

                if (!importDetails.isEmpty()) {
                    importDAO.createImportDetails(importDetails);
                    request.setAttribute("success", "Materials loaded from purchase order " + purchaseOrder.getPoCode() + " successfully.");
                    session.setAttribute("selectedPurchaseOrderId", purchaseOrderId);
                    session.setAttribute("selectedSupplierId", poDetails.get(0).getSupplierId());
                    // Clear manual input flag when loading from PO
                    session.removeAttribute("usingManualInput");
                } else {
                    request.setAttribute("error", "No materials found in the selected purchase order.");
                }
                
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Database error loading materials from purchase order with ID: " + purchaseOrderId, e);
                request.setAttribute("error", "Database error: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid purchase order ID format provided: " + request.getParameter("purchaseOrderId"), e);
            request.setAttribute("error", "Invalid purchase order ID.");
        }

        safeLoadDataAndForward(request, response);
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        String unitPriceStr = request.getParameter("unitPrice");

        try {
            Map<String, String> errors = utils.ImportValidator.validateAddMaterial(materialIdStr, quantityStr, unitPriceStr, materialDAO);
            if (!errors.isEmpty()) {
                request.setAttribute("formErrors", errors);
                safeLoadDataAndForward(request, response);
                return;
            }

            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            if (tempImportId == null || tempImportId == 0) {
                Import imports = new Import();
                imports.setImportCode("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
                imports.setImportDate(LocalDateTime.now());
                imports.setImportedBy(user.getUserId());
                tempImportId = importDAO.createImport(imports);
                session.setAttribute("tempImportId", tempImportId);
            }

            ImportDetail detail = new ImportDetail();
            detail.setImportId(tempImportId);
            detail.setMaterialId(Integer.parseInt(materialIdStr));
            detail.setQuantity(Integer.parseInt(quantityStr));
            detail.setUnitPrice(Double.parseDouble(unitPriceStr));
            detail.setStatus("draft");

            importDAO.createImportDetails(Collections.singletonList(detail));
            request.setAttribute("success", "Material added to import list successfully.");
            
            // Mark that user is using manual input (hide PO selection)
            session.setAttribute("usingManualInput", true);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error adding material to import list.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
        }
        
        safeLoadDataAndForward(request, response);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        try {
            int materialId = Integer.parseInt(request.getParameter("materialId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");

            if (tempImportId != null && tempImportId != 0) {
                importDAO.removeImportDetail(tempImportId, materialId, quantity);
                request.setAttribute("success", "Material removed from import list successfully.");
                
                // Check if import list is now empty, if so clear manual input flag
                List<ImportDetail> remainingDetails = importDAO.getDraftImportDetails(tempImportId);
                if (remainingDetails.isEmpty()) {
                    session.removeAttribute("usingManualInput");
                }
            } else {
                request.setAttribute("error", "No import list found to remove material from.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error removing material from import list.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
        }
        
        safeLoadDataAndForward(request, response);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        try {
            int materialId = Integer.parseInt(request.getParameter("materialId"));
            int newQuantity = Integer.parseInt(request.getParameter("newQuantity"));
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");

            if (newQuantity <= 0) {
                request.setAttribute("error", "Quantity must be greater than 0.");
                safeLoadDataAndForward(request, response);
                return;
            }

            if (tempImportId != null && tempImportId != 0) {
                ImportDetail detail = importDAO.getImportDetailByMaterial(tempImportId, materialId);
                if (detail != null) {
                    importDAO.updateImportDetailQuantity(detail.getImportDetailId(), newQuantity);
                    request.setAttribute("success", "Quantity updated successfully for material ID: " + materialId);
                } else {
                    request.setAttribute("error", "Material not found in import list.");
                }
            } else {
                request.setAttribute("error", "No import list found to update.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error updating material quantity in import list.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
        }
        
        safeLoadDataAndForward(request, response);
    }

    private void handleUpdatePrice(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        try {
            int materialId = Integer.parseInt(request.getParameter("materialId"));
            double newUnitPrice = Double.parseDouble(request.getParameter("newPrice"));
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");

            if (newUnitPrice <= 0) {
                request.setAttribute("error", "Unit price must be greater than 0.");
                safeLoadDataAndForward(request, response);
                return;
            }

            if (tempImportId != null && tempImportId != 0) {
             
                ImportDetail detail = importDAO.getImportDetailByMaterial(tempImportId, materialId);
                if (detail != null) {
                    importDAO.updateImportDetailPrice(detail.getImportDetailId(), newUnitPrice);
                    request.setAttribute("success", "Unit price updated successfully for material ID: " + materialId);
                } else {
                    request.setAttribute("error", "Material not found in import list.");
                }
            } else {
                request.setAttribute("error", "No import list found to update.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error updating material price in import list.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
        }
        
        safeLoadDataAndForward(request, response);
    }

    private void handleReset(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        try {
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            if (tempImportId != null && tempImportId != 0) {
                importDAO.deleteImport(tempImportId);
            }
            
            // Clear all session attributes
            session.setAttribute("tempImportId", 0);
            session.removeAttribute("selectedPurchaseOrderId");
            session.removeAttribute("selectedSupplierId");
            session.removeAttribute("usingManualInput");
            
            request.setAttribute("success", "Import form has been reset. You can start over.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error resetting import form.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
        }
        
        safeLoadDataAndForward(request, response);
    }

    private void handleImport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user)
            throws ServletException, IOException {
        Integer tempImportId = (Integer) session.getAttribute("tempImportId");
        if (tempImportId == null || tempImportId == 0) {
            request.setAttribute("error", "No materials selected for import.");
            safeLoadDataAndForward(request, response);
            return;
        }

        try {
            List<ImportDetail> details = importDAO.getDraftImportDetails(tempImportId);
            if (details.isEmpty()) {
                request.setAttribute("error", "Import list is empty.");
                safeLoadDataAndForward(request, response);
                return;
            }

            String supplierIdStr = request.getParameter("supplierId");
            String note = request.getParameter("note");
            
            // Get supplier from selected purchase order if not provided
            if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
                Integer selectedPurchaseOrderId = (Integer) session.getAttribute("selectedPurchaseOrderId");
                if (selectedPurchaseOrderId != null) {
                    List<PurchaseOrderDetail> poDetails = purchaseOrderDAO.getPurchaseOrderDetails(selectedPurchaseOrderId);
                    if (!poDetails.isEmpty() && poDetails.get(0).getSupplierId() != null) {
                        supplierIdStr = String.valueOf(poDetails.get(0).getSupplierId());
                    }
                }
            }
            
            // Validate supplier
            if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
                request.setAttribute("error", "Please select a Purchase Order first to set the supplier.");
                safeLoadDataAndForward(request, response);
                return;
            }

            Integer supplierId = Integer.parseInt(supplierIdStr);
            LocalDateTime now = LocalDateTime.now();

            Import imports = new Import();
            imports.setImportId(tempImportId);
            imports.setImportCode(importDAO.generateNextImportCode());
            imports.setImportDate(now);
            imports.setImportedBy(user.getUserId());
            imports.setSupplierId(supplierId);
            imports.setDestination("Warehouse"); // Default destination
            imports.setActualArrival(now);
            imports.setNote(note);

            double totalImportValue = details.stream()
                    .mapToDouble(detail -> detail.getUnitPrice() * detail.getQuantity())
                    .sum();

            importDAO.updateImport(imports);
            importDAO.confirmImport(tempImportId);
            importDAO.updateInventoryByImportId(tempImportId, user.getUserId(), "Warehouse");
            
            // Clear session data
            session.setAttribute("tempImportId", 0);
            session.removeAttribute("selectedPurchaseOrderId");
            session.removeAttribute("selectedSupplierId");
            session.removeAttribute("usingManualInput");
            
            request.setAttribute("success", "Import completed successfully with code: " + imports.getImportCode() + 
                    ". Total value: $" + String.format("%.2f", totalImportValue));
            safeLoadDataAndForward(request, response);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during import process.", e);
            request.setAttribute("error", "Database error: " + e.getMessage());
            safeLoadDataAndForward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid supplier ID format during import process: " + request.getParameter("supplierId"), e);
            request.setAttribute("error", "Invalid supplier ID format.");
            safeLoadDataAndForward(request, response);
        }
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        try {
            // Load basic data
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            List<Material> allMaterials = materialDAO.searchMaterials("", "", 1, Integer.MAX_VALUE, "code_asc");
            List<PurchaseOrder> sentToSupplierOrders = purchaseOrderDAO.getPurchaseOrdersByStatus("sent_to_supplier");
            
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("materials", allMaterials);
            request.setAttribute("sentToSupplierOrders", sentToSupplierOrders);
            
            // Handle temp import data
            Integer tempImportId = (Integer) session.getAttribute("tempImportId");
            if (tempImportId == null) {
                tempImportId = 0;
                session.setAttribute("tempImportId", 0);
            }

            List<ImportDetail> allDetails = (tempImportId != 0) 
                    ? importDAO.getDraftImportDetails(tempImportId) 
                    : new ArrayList<>();
            
            // Pagination
            int pageSize = 3;
            int currentPage = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null) {
                    currentPage = Integer.parseInt(pageParam);
                }
            } catch (NumberFormatException e) {
                // Use default page 1
            }
            
            int totalItems = allDetails.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            int fromIndex = (currentPage - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalItems);
            
            List<ImportDetail> pagedDetails = (fromIndex < toIndex) 
                    ? allDetails.subList(fromIndex, toIndex) 
                    : new ArrayList<>();
            
            request.setAttribute("importDetails", pagedDetails);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);

            // Create material and stock maps
            Map<Integer, Material> materialMap = allMaterials.stream()
                    .collect(HashMap::new, (map, material) -> map.put(material.getMaterialId(), material), HashMap::putAll);
            
            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ImportDetail detail : allDetails) {
                try {
                    int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
                    stockMap.put(detail.getMaterialId(), stock);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error getting stock for material ID: " + detail.getMaterialId() + ". Setting stock to 0.", e);
                    stockMap.put(detail.getMaterialId(), 0);
                }
            }

            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);

            // Set session attributes for JSP
            Integer selectedPurchaseOrderId = (Integer) session.getAttribute("selectedPurchaseOrderId");
            Integer selectedSupplierId = (Integer) session.getAttribute("selectedSupplierId");
            
            if (selectedPurchaseOrderId != null) {
                request.setAttribute("selectedPurchaseOrderId", selectedPurchaseOrderId);
            }
            if (selectedSupplierId != null) {
                request.setAttribute("selectedSupplierId", selectedSupplierId);
            }

            request.getRequestDispatcher("/ImportMaterial.jsp").forward(request, response);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in loadDataAndForward.", e);
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }
    
    private void safeLoadDataAndForward(HttpServletRequest request, HttpServletResponse response) {
        try {
            loadDataAndForward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in safeLoadDataAndForward, forwarding to error page.", e);
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            try {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Critical error: Could not forward to error page.", ex);
            }
        }
    }
}