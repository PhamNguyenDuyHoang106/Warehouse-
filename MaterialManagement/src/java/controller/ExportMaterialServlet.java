package controller;

import dal.ExportDAO;
import dal.ExportDetailDAO;
import dal.ExportRequestDAO;
import dal.MaterialDAO;
import dal.CustomerDAO;
import dal.VehicleDAO;
import dal.WarehouseDAO;
import dal.WarehouseRackDAO;
import entity.Export;
import entity.ExportRequest;
import entity.Material;
import entity.Customer;
import entity.User;
import entity.Vehicle;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PermissionHelper;

@WebServlet(name = "ExportMaterialServlet", urlPatterns = {"/ExportMaterial"})
public class ExportMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportMaterialServlet.class.getName());

    /**
     * Handle AJAX request to get warehouses by material ID with stock availability
     */
    private void handleGetWarehousesByMaterial(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        
        if (materialIdStr == null || materialIdStr.trim().isEmpty()) {
            response.getWriter().write("{\"warehouses\":[]}");
            return;
        }
        
        WarehouseDAO warehouseDAO = null;
        try {
            warehouseDAO = new WarehouseDAO();
            int materialId = Integer.parseInt(materialIdStr);
            java.math.BigDecimal requiredQuantity = null;
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    requiredQuantity = new java.math.BigDecimal(quantityStr);
                } catch (NumberFormatException e) {
                    // Ignore invalid quantity
                }
            }
            
            List<WarehouseDAO.WarehouseStockInfo> warehouses = warehouseDAO.getWarehousesByMaterial(materialId, requiredQuantity);
            
            // Build JSON response
            java.io.PrintWriter out = response.getWriter();
            out.print("{\"warehouses\":[");
            
            if (warehouses != null && !warehouses.isEmpty()) {
                for (int i = 0; i < warehouses.size(); i++) {
                    WarehouseDAO.WarehouseStockInfo wh = warehouses.get(i);
                    if (i > 0) out.print(",");
                    out.print("{");
                    out.print("\"warehouseId\":" + wh.warehouseId + ",");
                    out.print("\"warehouseCode\":\"" + escapeJson(wh.warehouseCode != null ? wh.warehouseCode : "") + "\",");
                    out.print("\"warehouseName\":\"" + escapeJson(wh.warehouseName != null ? wh.warehouseName : "") + "\",");
                    out.print("\"totalStock\":" + (wh.totalStock != null ? wh.totalStock : "0") + ",");
                    out.print("\"hasSufficientStock\":" + wh.hasSufficientStock);
                    out.print("}");
                }
            }
            
            out.print("]}");
            out.flush();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting warehouses by material", e);
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            if (warehouseDAO != null) warehouseDAO.close();
        }
    }
    
    /**
     * Handle AJAX request to get racks by warehouse ID and material ID with stock info
     */
    private void handleGetRacksByWarehouse(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String warehouseIdStr = request.getParameter("warehouseId");
        String materialIdStr = request.getParameter("materialId");
        
        if (warehouseIdStr == null || warehouseIdStr.trim().isEmpty()) {
            response.getWriter().write("{\"racks\":[]}");
            return;
        }
        
        WarehouseRackDAO rackDAO = null;
        try {
            rackDAO = new WarehouseRackDAO();
            Integer warehouseId = Integer.parseInt(warehouseIdStr);
            
            // If materialId is provided, get racks with stock info for that material
            if (materialIdStr != null && !materialIdStr.trim().isEmpty()) {
                int materialId = Integer.parseInt(materialIdStr);
                List<WarehouseRackDAO.RackStockInfo> racks = rackDAO.getRacksByWarehouseAndMaterial(warehouseId, materialId);
                
                // Build JSON response with stock info
                java.io.PrintWriter out = response.getWriter();
                out.print("{\"racks\":[");
                
                if (racks != null && !racks.isEmpty()) {
                    for (int i = 0; i < racks.size(); i++) {
                        WarehouseRackDAO.RackStockInfo rack = racks.get(i);
                        if (i > 0) out.print(",");
                        out.print("{");
                        out.print("\"rackId\":" + rack.rackId + ",");
                        out.print("\"rackCode\":\"" + escapeJson(rack.rackCode != null ? rack.rackCode : "") + "\",");
                        out.print("\"rackName\":\"" + escapeJson(rack.rackName != null ? rack.rackName : "") + "\",");
                        out.print("\"stock\":" + (rack.stock != null ? rack.stock : "0"));
                        out.print("}");
                    }
                }
                
                out.print("]}");
                out.flush();
            } else {
                // Fallback: get all racks in warehouse (for backward compatibility)
                List<WarehouseRack> racks = rackDAO.getRacksByWarehouseId(warehouseId);
                
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
                        out.print("\"stock\":0");
                        out.print("}");
                    }
                }
                
                out.print("]}");
                out.flush();
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting racks by warehouse", e);
            response.getWriter().write("{\"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            if (rackDAO != null) rackDAO.close();
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
        
        // Handle AJAX request for warehouses by material
        if ("getWarehousesByMaterial".equals(action)) {
            handleGetWarehousesByMaterial(request, response);
            return;
        }
        
        // Handle AJAX request for racks by warehouse
        if ("getRacksByWarehouse".equals(action)) {
            handleGetRacksByWarehouse(request, response);
            return;
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Tạo xuất kho")) {
            request.setAttribute("error", "You do not have permission to export materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        ExportDAO exportDAO = null;
        ExportRequestDAO exportRequestDAO = null;
        MaterialDAO materialDAO = null;
        CustomerDAO customerDAO = null;
        VehicleDAO vehicleDAO = null;
        WarehouseDAO warehouseDAO = null;

        try {
            exportDAO = new ExportDAO();
            exportRequestDAO = new ExportRequestDAO();
            materialDAO = new MaterialDAO();
            customerDAO = new CustomerDAO();
            vehicleDAO = new VehicleDAO();
            warehouseDAO = new WarehouseDAO();

            // Generate next export code
            String nextExportCode = exportDAO.generateNextExportCode();
            request.setAttribute("nextExportCode", nextExportCode);

            // Get approved export requests (that haven't been exported yet)
            List<ExportRequest> approvedRequests = exportRequestDAO.getAllRequestsByStatus("approved");
            request.setAttribute("exportRequests", approvedRequests);

            // Get all materials for autocomplete
            List<Material> materials = materialDAO.getAllProducts();
            request.setAttribute("materials", materials);

            // Get all customers
            List<Customer> customers = customerDAO.getAllCustomers();
            request.setAttribute("customers", customers);

            // Get available vehicles
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicles", vehicles);

            // Get all warehouses for dropdown
            List<Warehouse> warehouses = warehouseDAO.getAllWarehouses();
            request.setAttribute("warehouses", warehouses);

            request.getRequestDispatcher("ExportMaterialForm.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for ExportMaterialServlet", e);
            request.setAttribute("error", "Error loading export form: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (exportDAO != null) exportDAO.close();
            if (exportRequestDAO != null) exportRequestDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (customerDAO != null) customerDAO.close();
            if (vehicleDAO != null) vehicleDAO.close();
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
        if (!PermissionHelper.hasPermission(user, "Tạo xuất kho")) {
            request.setAttribute("error", "You do not have permission to export materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Read form data
        String exportCode = request.getParameter("exportCode");
        String exportRequestIdStr = request.getParameter("exportRequestId");
        String customerIdStr = request.getParameter("customerId");
        String vehicleIdStr = request.getParameter("vehicleId");
        String exportDateStr = request.getParameter("exportDate");
        String note = request.getParameter("note");

        String[] materialIds = request.getParameterValues("materialId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] unitPrices = request.getParameterValues("unitPrice[]");  // V8: REQUIRED for profit calculation
        String[] rackIds = request.getParameterValues("rackId[]");
        String[] notes = request.getParameterValues("materialNote[]");
        String[] exportRequestDetailIds = request.getParameterValues("exportRequestDetailId[]");  // V8: Link to export request details

        // Basic validation
        if (exportCode == null || exportCode.trim().isEmpty()) {
            request.setAttribute("error", "Export code is required");
            doGet(request, response);
            return;
        }

        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Customer is required");
            doGet(request, response);
            return;
        }

        if (materialIds == null || materialIds.length == 0) {
            request.setAttribute("error", "At least one material is required");
            doGet(request, response);
            return;
        }
        
        // V8: Validate unit prices (REQUIRED for profit calculation)
        if (unitPrices == null || unitPrices.length != materialIds.length) {
            request.setAttribute("error", "Unit price (selling price) is required for all materials");
            doGet(request, response);
            return;
        }
        
        for (int i = 0; i < unitPrices.length; i++) {
            if (unitPrices[i] == null || unitPrices[i].trim().isEmpty()) {
                request.setAttribute("error", "Unit price is required for material at position " + (i + 1));
                doGet(request, response);
                return;
            }
        }

        // Process export
        ExportDAO exportDAO = null;
        ExportDetailDAO detailDAO = null;

        try {
            exportDAO = new ExportDAO();
            detailDAO = new ExportDetailDAO();

            // Create Export record
            Export export = new Export();
            export.setExportCode(exportCode);
            export.setExportedBy(user.getUserId());
            // Note: customerId is not directly stored in Exports table in v11
            // It is linked through Export_Requests (erId) or Sales_Orders (soId)

            if (exportRequestIdStr != null && !exportRequestIdStr.trim().isEmpty()) {
                export.setExportRequestId(Integer.parseInt(exportRequestIdStr));
            }

            // Parse export date or use current time
            if (exportDateStr != null && !exportDateStr.trim().isEmpty()) {
                export.setExportDate(LocalDateTime.parse(exportDateStr + "T00:00:00"));
            } else {
                export.setExportDate(LocalDateTime.now());
            }

            export.setNote(note);

            int exportId = exportDAO.createExport(export);

            if (exportId > 0) {
                // V8: Create Export_Details then allocate using SP_Allocate_Export_Detail_V8
                boolean allDetailsAllocated = true;
                StringBuilder errorMessages = new StringBuilder();

                for (int i = 0; i < materialIds.length; i++) {
                    entity.ExportDetail detail = new entity.ExportDetail();
                    detail.setExportId(exportId);
                    detail.setMaterialId(Integer.parseInt(materialIds[i]));
                    detail.setQuantity(new BigDecimal(quantities[i]));
                    
                    // V8: unit_price_export is REQUIRED
                    detail.setUnitPriceExport(new BigDecimal(unitPrices[i]));
                    
                    // export_request_detail_id (optional - link to Export_Request_Details)
                    if (exportRequestDetailIds != null && i < exportRequestDetailIds.length && 
                        exportRequestDetailIds[i] != null && !exportRequestDetailIds[i].trim().isEmpty()) {
                        detail.setExportRequestDetailId(Integer.parseInt(exportRequestDetailIds[i]));
                    }
                    
                    // rack_id (optional)
                    if (rackIds != null && i < rackIds.length && 
                        rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                        detail.setRackId(Integer.parseInt(rackIds[i]));
                    }
                    
                    detail.setStatus("draft");  // Will become 'exported' after allocation
                    detail.setNote((notes != null && i < notes.length) ? notes[i] : null);
                    
                    // Step 1: Add export detail
                    int exportDetailId = detailDAO.addExportDetail(detail);
                    
                    if (exportDetailId > 0) {
                        // Step 2: Allocate using stored procedure (FIFO allocation + profit calculation)
                        boolean allocated = detailDAO.allocateExportDetail(exportDetailId, user.getUserId());
                        
                        if (!allocated) {
                            allDetailsAllocated = false;
                            errorMessages.append("Failed to allocate material ID: ").append(materialIds[i])
                                        .append(" (insufficient inventory or other error). ");
                            LOGGER.log(Level.WARNING, "Failed to allocate export detail ID: " + exportDetailId);
                        }
                    } else {
                        allDetailsAllocated = false;
                        errorMessages.append("Failed to create export detail for material ID: ").append(materialIds[i]).append(". ");
                        LOGGER.log(Level.WARNING, "Failed to add export detail for material ID: " + materialIds[i]);
                    }
                }

                if (allDetailsAllocated) {
                    response.sendRedirect("ExportList?success=Export created and allocated successfully");
                } else {
                    response.sendRedirect("ExportList?warning=Export created but some items failed allocation: " + errorMessages.toString());
                }
            } else {
                request.setAttribute("error", "Failed to create export record");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format in export data", e);
            request.setAttribute("error", "Invalid data format. Please check your inputs.");
            doGet(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing export", e);
            request.setAttribute("error", "Error processing export: " + e.getMessage());
            doGet(request, response);
        } finally {
            if (exportDAO != null) exportDAO.close();
            if (detailDAO != null) detailDAO.close();
        }
    }
}
