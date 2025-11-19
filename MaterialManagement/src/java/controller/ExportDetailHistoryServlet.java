package controller;

import dal.ExportDAO;
import dal.ExportPricingDAO;
import dal.CustomerDAO;
import dal.ExportRequestDAO;
import dal.VehicleDAO;
import dal.UserDAO;
import dal.WarehouseDAO;
import entity.Export;
import entity.ExportDetail;
import entity.ExportRequest;
import entity.Customer;
import entity.Vehicle;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="ExportDetailHistoryServlet", urlPatterns={"/ExportDetailHistory", "/ExportDetail"})
public class ExportDetailHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameter id (from ExportList.jsp) or exportId (backward compatibility)
        String exportIdStr = request.getParameter("id");
        if (exportIdStr == null) {
            exportIdStr = request.getParameter("exportId"); // Fallback for backward compatibility
        }
        if (exportIdStr == null || exportIdStr.trim().isEmpty()) {
            response.sendRedirect("ExportList");
            return;
        }
        try {
            int exportId = Integer.parseInt(exportIdStr);
            ExportDAO exportDAO = null;
            ExportPricingDAO pricingDAO = null;
            CustomerDAO customerDAO = null;
            ExportRequestDAO exportRequestDAO = null;
            VehicleDAO vehicleDAO = null;
            UserDAO userDAO = null;
            WarehouseDAO warehouseDAO = null;
            
            try {
                exportDAO = new ExportDAO();
                pricingDAO = new ExportPricingDAO();
                customerDAO = new CustomerDAO();
                exportRequestDAO = new ExportRequestDAO();
                vehicleDAO = new VehicleDAO();
                userDAO = new UserDAO();
                warehouseDAO = new WarehouseDAO();
                
                Export exportData = exportDAO.getExportById(exportId);
                if (exportData == null) {
                    response.sendRedirect("ExportList");
                    return;
                }
                
                List<ExportDetail> exportDetails = null;
                try {
                    exportDetails = exportDAO.getExportDetailsByExportId(exportId);
                } catch (SQLException e) {
                    Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Error getting export details", e);
                    exportDetails = new ArrayList<>();
                }

                // Lấy thông tin bổ sung
                // Customer information (from Export_Requests)
                Customer customer = null;
                if (exportData.getErId() != null) {
                    try {
                        ExportRequest exportRequest = exportRequestDAO.getById(exportData.getErId());
                        if (exportRequest != null && exportRequest.getCustomerId() != null) {
                            customer = customerDAO.getCustomerById(exportRequest.getCustomerId());
                        }
                    } catch (Exception e) {
                        Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting customer", e);
                    }
                }
                
                // Vehicle information
                Vehicle vehicle = null;
                if (exportData.getVehicleId() != null) {
                    try {
                        vehicle = vehicleDAO.getVehicleById(exportData.getVehicleId());
                    } catch (Exception e) {
                        Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting vehicle", e);
                    }
                }
                
                // User information (người export)
                User exportedByUser = null;
                try {
                    exportedByUser = userDAO.getUserById(exportData.getExportedBy());
                } catch (Exception e) {
                    Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting user", e);
                }
                
                // Calculate total value and get unique warehouses from details
                BigDecimal totalValue = BigDecimal.ZERO;
                Map<Integer, Warehouse> warehousesMap = new HashMap<>();
                Map<Integer, String> rackToWarehouseMap = new HashMap<>(); // rackId -> warehouseName
                List<String> warehouseNames = new ArrayList<>();
                
                if (exportDetails != null && !exportDetails.isEmpty()) {
                    for (ExportDetail detail : exportDetails) {
                        // Calculate total value (unit_price_export * quantity)
                        if (detail.getQuantity() != null && detail.getUnitPriceExport() != null) {
                            totalValue = totalValue.add(detail.getQuantity().multiply(detail.getUnitPriceExport()));
                        }
                        
                        // Get warehouse from rack if available
                        if (detail.getRackId() != null) {
                            dal.WarehouseRackDAO rackDAO = null;
                            try {
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
                                Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.WARNING, "Error getting warehouse for rack: " + detail.getRackId(), e);
                            } finally {
                                // WarehouseRackDAO doesn't have close method
                            }
                        }
                    }
                }

                // Convert LocalDateTime to Date for JSP formatting
                if (exportData.getExportDate() != null) {
                    Date exportDate = Date.from(exportData.getExportDate().atZone(ZoneId.systemDefault()).toInstant());
                    request.setAttribute("exportDate", exportDate);
                }
                
                // V8: Get Export_Pricing data for profit calculation (accounting)
                Map<Integer, entity.ExportPricing> pricingMap = new HashMap<>();
                BigDecimal totalRevenue = BigDecimal.ZERO;
                BigDecimal totalCost = BigDecimal.ZERO;
                BigDecimal totalProfit = BigDecimal.ZERO;
                
                if (exportDetails != null && !exportDetails.isEmpty()) {
                    for (ExportDetail detail : exportDetails) {
                        try {
                            entity.ExportPricing pricing = pricingDAO.getPricingByExportDetail(detail.getExportDetailId());
                            if (pricing != null) {
                                pricingMap.put(detail.getExportDetailId(), pricing);
                                if (pricing.getProfit() != null) {
                                    totalProfit = totalProfit.add(pricing.getProfit());
                                }
                                if (pricing.getUnitPrice() != null && pricing.getQuantity() != null) {
                                    totalRevenue = totalRevenue.add(pricing.getUnitPrice().multiply(pricing.getQuantity()));
                                }
                                if (pricing.getUnitCost() != null && pricing.getQuantity() != null) {
                                    totalCost = totalCost.add(pricing.getUnitCost().multiply(pricing.getQuantity()));
                                }
                            }
                        } catch (Exception e) {
                            Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.WARNING, 
                                "Error getting pricing for export detail: " + detail.getExportDetailId(), e);
                        }
                    }
                }
                
                request.setAttribute("exportData", exportData);
                request.setAttribute("exportDetails", exportDetails != null ? exportDetails : new ArrayList<>());
                request.setAttribute("customer", customer);
                request.setAttribute("vehicle", vehicle);
                request.setAttribute("exportedByUser", exportedByUser);
                request.setAttribute("warehouses", new ArrayList<>(warehousesMap.values()));
                request.setAttribute("warehouseNames", warehouseNames.isEmpty() ? "" : String.join(", ", warehouseNames));
                request.setAttribute("rackToWarehouseMap", rackToWarehouseMap);
                request.setAttribute("totalValue", totalValue);
                // V8: Accounting data
                request.setAttribute("pricingMap", pricingMap);
                request.setAttribute("totalRevenue", totalRevenue);
                request.setAttribute("totalCost", totalCost);
                request.setAttribute("totalProfit", totalProfit);
                request.getRequestDispatcher("ExportDetail.jsp").forward(request, response);
            } catch (Exception e) {
                Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Error in ExportDetailHistoryServlet", e);
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading export details: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ExportList");
        } catch (Exception e) {
            Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.SEVERE, "Unexpected error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported");
    }

    @Override
    public String getServletInfo() {
        return "Servlet to display export details";
    }
}