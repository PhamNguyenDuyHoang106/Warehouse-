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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ImportDetailHistoryServlet", urlPatterns = {"/ImportDetailHistory", "/ImportDetail"})
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
        // 1. Get id parameter (from ImportList.jsp) or importId (backward compatibility)
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

        // 2. Initialize DAOs
        ImportDAO importDAO = null;
        SupplierDAO supplierDAO = null;
        UserDAO userDAO = null;
        WarehouseDAO warehouseDAO = null;
        
        try {
            importDAO = new ImportDAO();
            supplierDAO = new SupplierDAO();
            userDAO = new UserDAO();
            warehouseDAO = new WarehouseDAO();

            // 3. Get detail data
            Import importData = importDAO.getImportById(importId);
            if (importData == null) {
                response.sendRedirect("ImportList");
                return;
            }
            
            List<ImportDetail> importDetails = importDAO.getImportDetailsByImportId(importId);
            if (importDetails == null) {
                importDetails = new ArrayList<>();
            }

            // 4. Get additional information
            // Get all suppliers for this import
            List<Supplier> suppliers = importDAO.getAllSuppliersForImport(importId);
            if (suppliers == null || suppliers.isEmpty()) {
                // Fallback to PO's main supplier if no suppliers found
                if (importData.getSupplierId() != null) {
                    try {
                        Supplier mainSupplier = supplierDAO.getSupplierByID(importData.getSupplierId());
                        if (mainSupplier != null) {
                            suppliers = new ArrayList<>();
                            suppliers.add(mainSupplier);
                        }
                    } catch (Exception e) {
                        Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting supplier", e);
                    }
                }
            }
            // Keep backward compatibility - set first supplier as main supplier
            Supplier supplier = (suppliers != null && !suppliers.isEmpty()) ? suppliers.get(0) : null;
            
            // User information
            User createdByUser = null;
            if (importData.getCreatedBy() > 0) {
                try {
                    createdByUser = userDAO.getUserById(importData.getCreatedBy());
                } catch (Exception e) {
                    Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting creator", e);
                }
            }

            User receivedByUser = null;
            if (importData.getReceivedBy() != null) {
                try {
                    receivedByUser = userDAO.getUserById(importData.getReceivedBy());
                } catch (Exception e) {
                    Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting receiver", e);
                }
            }
            
            // Warehouse information
            Warehouse warehouse = null;
            if (importData.getWarehouseId() != null) {
                try {
                    warehouse = warehouseDAO.getWarehouseById(importData.getWarehouseId());
                } catch (Exception e) {
                    Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting warehouse", e);
                }
            }
            
            // Calculate total value
            BigDecimal totalValue = BigDecimal.ZERO;
            if (importDetails != null && !importDetails.isEmpty()) {
                for (ImportDetail detail : importDetails) {
                    if (detail.getQuantity() != null && detail.getUnitCost() != null) {
                        totalValue = totalValue.add(detail.getQuantity().multiply(detail.getUnitCost()));
                    }
                }
            }
            if (totalValue.compareTo(BigDecimal.ZERO) == 0 && importData.getTotalAmount() != null) {
                totalValue = importData.getTotalAmount();
            }

            // 5. Convert LocalDate to Date for JSP formatting
            if (importData.getImportDate() != null) {
                Date importDate = Date.from(importData.getImportDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                request.setAttribute("importDate", importDate);
            }
            
            List<Warehouse> warehouses = new ArrayList<>();
            if (warehouse != null) {
                warehouses.add(warehouse);
            }
            Map<Integer, String> rackToWarehouseMap = new HashMap<>();
            if (importDetails != null) {
                for (ImportDetail detail : importDetails) {
                    if (detail.getRackId() != null) {
                        String name = warehouse != null ? warehouse.getWarehouseName() : importData.getWarehouseName();
                        rackToWarehouseMap.put(detail.getRackId(), name);
                    }
                }
            }
            String warehouseNames = warehouse != null ? warehouse.getWarehouseName() : importData.getWarehouseName();

            // 6. Set data to request
            request.setAttribute("importData", importData);
            request.setAttribute("importDetails", importDetails != null ? importDetails : new ArrayList<>());
            request.setAttribute("supplier", supplier);
            request.setAttribute("suppliers", suppliers != null ? suppliers : new ArrayList<>());
            request.setAttribute("createdByUser", createdByUser);
            request.setAttribute("importedByUser", createdByUser);
            request.setAttribute("receivedByUser", receivedByUser);
            request.setAttribute("warehouse", warehouse);
            request.setAttribute("warehouses", warehouses);
            request.setAttribute("warehouseNames", warehouseNames);
            request.setAttribute("rackToWarehouseMap", rackToWarehouseMap);
            request.setAttribute("totalValue", totalValue);

            // 7. Forward to JSP
            request.getRequestDispatcher("ImportDetail.jsp").forward(request, response);
        } catch (Exception e) {
            Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Error in ImportDetailHistoryServlet", e);
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading import details: " + e.getMessage());
        } finally {
            if (warehouseDAO != null) warehouseDAO.close();
            if (userDAO != null) userDAO.close();
            if (supplierDAO != null) supplierDAO.close();
            if (importDAO != null) importDAO.close();
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
