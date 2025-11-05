package controller;

import dal.ImportDAO;
import dal.SupplierDAO;
import dal.UserDAO;
import dal.WarehouseDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Supplier;
import entity.User;
import entity.Warehouse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ImportDetailHistoryServlet", urlPatterns = {"/ImportDetail"})
public class ImportDetailHistoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (java.io.PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ImportDetailHistoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ImportDetailHistoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy tham số id (từ ImportList.jsp) hoặc importId (backward compatibility)
        String importIdStr = request.getParameter("id");
        if (importIdStr == null) {
            importIdStr = request.getParameter("importId"); // Fallback for backward compatibility
        }
        if (importIdStr == null || importIdStr.trim().isEmpty()) {
            response.sendRedirect("ImportList");
            return;
        }
        int importId;
        try {
            importId = Integer.parseInt(importIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("ImportList");
            return;
        }

        // 2. Khởi tạo DAO
        ImportDAO importDAO = null;
        SupplierDAO supplierDAO = null;
        UserDAO userDAO = null;
        WarehouseDAO warehouseDAO = null;
        
        try {
            importDAO = new ImportDAO();
            supplierDAO = new SupplierDAO();
            userDAO = new UserDAO();
            warehouseDAO = new WarehouseDAO();

            // 3. Lấy dữ liệu chi tiết
            Import importData = importDAO.getImportById(importId);
            if (importData == null) {
                response.sendRedirect("ImportList");
                return;
            }
            
            List<ImportDetail> importDetails = null;
            try {
                importDetails = importDAO.getImportDetailsByImportId(importId);
            } catch (SQLException ex) {
                Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Error getting import details", ex);
                importDetails = new ArrayList<>();
            }

            // 4. Lấy thông tin bổ sung
            // Supplier information
            Supplier supplier = null;
            if (importData.getSupplierId() != null) {
                try {
                    supplier = supplierDAO.getSupplierByID(importData.getSupplierId());
                } catch (Exception e) {
                    Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting supplier", e);
                }
            }
            
            // User information (người import)
            User importedByUser = null;
            try {
                importedByUser = userDAO.getUserById(importData.getImportedBy());
            } catch (Exception e) {
                Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting user", e);
            }
            
            // Calculate total value and get unique warehouses from details
            BigDecimal totalValue = BigDecimal.ZERO;
            Map<Integer, Warehouse> warehousesMap = new HashMap<>();
            Map<Integer, String> rackToWarehouseMap = new HashMap<>(); // rackId -> warehouseName
            List<String> warehouseNames = new ArrayList<>();
            
            if (importDetails != null && !importDetails.isEmpty()) {
                for (ImportDetail detail : importDetails) {
                    // Calculate total value
                    if (detail.getQuantity() != null && detail.getUnitPrice() != null) {
                        totalValue = totalValue.add(detail.getQuantity().multiply(detail.getUnitPrice()));
                    }
                    
                    // Get warehouse from rack if available
                    if (detail.getRackId() != null) {
                        dal.WarehouseRackDAO rackDAO = null;
                        try {
                            // Get warehouse_id from rack using WarehouseRackDAO
                            rackDAO = new dal.WarehouseRackDAO();
                            entity.WarehouseRack rack = rackDAO.getRackById(detail.getRackId());
                            if (rack != null && rack.getWarehouseId() != null) {
                                Integer warehouseId = rack.getWarehouseId();
                                if (!warehousesMap.containsKey(warehouseId)) {
                                    Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
                                    if (warehouse != null) {
                                        warehousesMap.put(warehouseId, warehouse);
                                        warehouseNames.add(warehouse.getWarehouseName());
                                    }
                                }
                                // Map rack to warehouse name
                                if (warehousesMap.containsKey(warehouseId)) {
                                    rackToWarehouseMap.put(detail.getRackId(), warehousesMap.get(warehouseId).getWarehouseName());
                                }
                            }
                        } catch (Exception e) {
                            Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting warehouse for rack: " + detail.getRackId(), e);
                        } finally {
                            // WarehouseRackDAO doesn't have close method
                        }
                    }
                }
            }

            // 5. Convert LocalDateTime to Date for JSP formatting
            if (importData.getImportDate() != null) {
                Date importDate = Date.from(importData.getImportDate().atZone(ZoneId.systemDefault()).toInstant());
                request.setAttribute("importDate", importDate);
            }
            if (importData.getActualArrival() != null) {
                Date actualArrival = Date.from(importData.getActualArrival().atZone(ZoneId.systemDefault()).toInstant());
                request.setAttribute("actualArrival", actualArrival);
            }
            
            // 6. Đẩy dữ liệu lên request
            request.setAttribute("importData", importData);
            request.setAttribute("importDetails", importDetails != null ? importDetails : new ArrayList<>());
            request.setAttribute("supplier", supplier);
            request.setAttribute("importedByUser", importedByUser);
            request.setAttribute("warehouses", new ArrayList<>(warehousesMap.values()));
            request.setAttribute("warehouseNames", warehouseNames.isEmpty() ? "" : String.join(", ", warehouseNames));
            request.setAttribute("rackToWarehouseMap", rackToWarehouseMap);
            request.setAttribute("totalValue", totalValue);

            // 6. Forward về JSP
            request.getRequestDispatcher("ImportDetail.jsp").forward(request, response);
        } catch (Exception e) {
            Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Error in ImportDetailHistoryServlet", e);
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading import details: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
