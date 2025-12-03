/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.MaterialDAO;
import dal.ExportRequestDAO;
import dal.InventoryDAO;
import dal.RoleDAO;
import dal.PurchaseRequestDAO;
import dal.RepairRequestDAO;
import dal.RequestDAO;
import dal.UserDAO;
import dal.DashboardDAO;
import entity.User;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nhat Anh
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HomeServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        // Kiểm tra đăng nhập - nếu chưa đăng nhập thì redirect về trang login
        if (user == null) {
            LOGGER.log(Level.INFO, "User not logged in, redirecting to Login.jsp");
            response.sendRedirect("Login.jsp");
            return;
        }
        
        LOGGER.log(Level.INFO, "User logged in: " + user.getUsername() + ", role: " + user.getRoleId());
        int roleId = user.getRoleId();
        request.setAttribute("roleId", roleId);
        request.setAttribute("isAdmin", roleId == 1);
        request.setAttribute("isDirector", roleId == 2);
        request.setAttribute("isWarehouseStaff", roleId == 3);
        request.setAttribute("isEmployee", roleId == 4);
        request.setAttribute("dashboardDate", LocalDate.now());
        
        // Initialize all DAOs
        MaterialDAO dao = null;
        ExportRequestDAO exportRequestDAO = null;
        UserDAO userDAO = null;
        dal.PermissionDAO permissionDAO = null;
        PurchaseRequestDAO purchaseRequestDAO = null;
        RepairRequestDAO repairRequestDAO = null;
        RequestDAO requestDAO = null;
        InventoryDAO inventoryDAO = null;
        DashboardDAO dashboardDAO = null;
        RoleDAO roleDAO = null;
        
        try {
            // Lấy thống kê cơ bản
            dao = new MaterialDAO();
            int materialCount = dao.countMaterials(null, null);
            request.setAttribute("materialCount", materialCount);

            // Dashboard tổng quan cho tất cả role
            request.setAttribute("materialCount", materialCount);

            // Số yêu cầu xuất kho chờ duyệt
            exportRequestDAO = new ExportRequestDAO();
            int pendingExportRequestCount = exportRequestDAO.getTotalCount("pending", null, null);
            request.setAttribute("pendingExportRequestCount", pendingExportRequestCount);

            // Tổng số người dùng (chỉ cho admin)
            userDAO = new UserDAO();
            int totalUserCount = userDAO.getUserCountByFilter(null, null, null, null);
            request.setAttribute("totalUserCount", totalUserCount);

            // Tổng số quyền (chỉ cho admin)
            permissionDAO = new dal.PermissionDAO();
            int totalPermissionCount = permissionDAO.getAllPermissions().size();
            request.setAttribute("totalPermissionCount", totalPermissionCount);

            // Số yêu cầu mua vật tư chờ duyệt
            purchaseRequestDAO = new PurchaseRequestDAO();
            int pendingPurchaseRequestCount = purchaseRequestDAO.countPurchaseRequest(null, "submitted", null, null);
            request.setAttribute("pendingPurchaseRequestCount", pendingPurchaseRequestCount);

            // Số yêu cầu sửa chữa chờ duyệt
            repairRequestDAO = new RepairRequestDAO();
            int pendingRepairRequestCount = 0;
            try {
                pendingRepairRequestCount = repairRequestDAO.getTotalRepairRequestCount(null, "Pending", null, null);
                LOGGER.log(Level.INFO, "Pending repair requests count: " + pendingRepairRequestCount);
            } catch (Exception e) {
                pendingRepairRequestCount = 0;
                LOGGER.log(Level.SEVERE, "Failed to get pending repair requests: " + e.getMessage(), e);
            }
            request.setAttribute("pendingRepairRequestCount", pendingRepairRequestCount);
            int pendingRequestTotal = pendingPurchaseRequestCount + pendingExportRequestCount + pendingRepairRequestCount;
            request.setAttribute("pendingRequestTotal", pendingRequestTotal);

            int repairInProgressCount = 0;
            try {
                repairInProgressCount = repairRequestDAO.getTotalRepairRequestCount(null, "In Progress", null, null);
                LOGGER.log(Level.INFO, "Repair requests in progress: " + repairInProgressCount);
            } catch (Exception e) {
                repairInProgressCount = 0;
                LOGGER.log(Level.WARNING, "Failed to get in-progress repair requests: " + e.getMessage(), e);
            }
            request.setAttribute("repairInProgressCount", repairInProgressCount);

            // Số yêu cầu của user hiện tại
            int myPurchaseRequestCount = 0;
            int myRepairRequestCount = 0;
            int myPendingRequestCount = 0;
            int myApprovedRequestCount = 0;
            int availableMaterialsCount = 0;
            
            if (user != null) {
                requestDAO = new RequestDAO();
                try {
                    myPurchaseRequestCount = requestDAO.getPurchaseRequestCountByUser(user.getUserId(), null, null, null, null, null);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get purchase request count: " + e.getMessage(), e);
                    myPurchaseRequestCount = 0;
                }
                
                try {
                    myRepairRequestCount = requestDAO.getRepairRequestCountByUser(user.getUserId(), null, null, null, null, null);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get repair request count: " + e.getMessage(), e);
                    myRepairRequestCount = 0;
                }
                
                // Đếm yêu cầu pending và approved của user
                try {
                    myPendingRequestCount = requestDAO.getExportRequestCountByUser(user.getUserId(), "pending", null, null, null, null);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get pending export request count: " + e.getMessage(), e);
                    myPendingRequestCount = 0;
                }
                
                try {
                    myApprovedRequestCount = requestDAO.getExportRequestCountByUser(user.getUserId(), "approved", null, null, null, null);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get approved export request count: " + e.getMessage(), e);
                    myApprovedRequestCount = 0;
                }
                
                // Đếm vật tư đang hoạt động (active)
                try {
                    availableMaterialsCount = dao.countMaterials(null, "active");
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to get available materials count: " + e.getMessage(), e);
                    availableMaterialsCount = 0;
                }
            }
            request.setAttribute("myPurchaseRequestCount", myPurchaseRequestCount);
            request.setAttribute("myRepairRequestCount", myRepairRequestCount);
            request.setAttribute("myPendingRequestCount", myPendingRequestCount);
            request.setAttribute("myApprovedRequestCount", myApprovedRequestCount);
            request.setAttribute("availableMaterialsCount", availableMaterialsCount);

            // Thống kê tồn kho
            inventoryDAO = new InventoryDAO();
            Map<String, Integer> stats = inventoryDAO.getInventoryStatistics();
            int totalStock = stats.getOrDefault("totalStock", 0);
            int lowStockCount = stats.getOrDefault("lowStockCount", 0);
            int outOfStockCount = stats.getOrDefault("outOfStockCount", 0);
            request.setAttribute("totalStock", totalStock);
            request.setAttribute("lowStockCount", lowStockCount);
            request.setAttribute("outOfStockCount", outOfStockCount);

            // Đếm vật tư đang tạm ngưng (inactive)
            int damagedMaterialsCount = dao.countMaterials(null, "inactive");
            request.setAttribute("damagedMaterialsCount", damagedMaterialsCount);

            dashboardDAO = new DashboardDAO();
            try {
                BigDecimal totalInventoryValue = dashboardDAO.getTotalInventoryValue();
                DashboardDAO.TransactionStats importTodayStats = dashboardDAO.getDailyImportStats(LocalDate.now());
                DashboardDAO.TransactionStats exportTodayStats = dashboardDAO.getDailyExportStats(LocalDate.now());
                DashboardDAO.MonthlySeries monthlySeries = dashboardDAO.getMonthlyImportExportSeries(12);

                request.setAttribute("totalInventoryValue", totalInventoryValue);
                request.setAttribute("importTodayStats", importTodayStats);
                request.setAttribute("exportTodayStats", exportTodayStats);
                request.setAttribute("monthlySeries", monthlySeries);
                request.setAttribute("categoryDistribution", dashboardDAO.getCategoryStockDistribution(8));
                request.setAttribute("inventoryValueTrend", dashboardDAO.getInventoryValueTrend(12));
                request.setAttribute("lowStockAlerts", dashboardDAO.getLowStockMaterials(4));
                request.setAttribute("expiryAlerts", dashboardDAO.getExpiringBatches(30, 4));
                request.setAttribute("slowMovementAlerts", dashboardDAO.getSlowMovingMaterials(60, 4));
                request.setAttribute("warehouseCapacityUsage", dashboardDAO.getWarehouseCapacityUsage());

                List<Map<String, Object>> pendingRequests = dashboardDAO.getPendingRequests(10);
                List<Map<String, Object>> pendingNotifications = new ArrayList<>();
                if (pendingRequests != null && !pendingRequests.isEmpty()) {
                    int notifyCount = Math.min(4, pendingRequests.size());
                    pendingNotifications = new ArrayList<>(pendingRequests.subList(0, notifyCount));
                }
                request.setAttribute("pendingRequests", pendingRequests);
                request.setAttribute("pendingNotifications", pendingNotifications);
                request.setAttribute("recentTransactions", dashboardDAO.getRecentMaterialMovements(8));
                request.setAttribute("purchasingFlowSteps", dashboardDAO.getPurchasingFlowSteps());
                request.setAttribute("outboundFlowSteps", dashboardDAO.getOutboundFlowSteps());
                request.setAttribute("maintenanceFlowSteps", dashboardDAO.getMaintenanceFlowSteps());
                request.setAttribute("profitTrend", dashboardDAO.getMonthlyProfitTrend(12));
                request.setAttribute("directorCategoryValues", dashboardDAO.getCategoryValueBreakdown(6));

                List<Map<String, Object>> topWidgets = buildTopWidgets(
                        totalStock,
                        totalInventoryValue,
                        importTodayStats,
                        exportTodayStats,
                        pendingPurchaseRequestCount,
                        repairInProgressCount);
                request.setAttribute("topWidgets", topWidgets);
            } finally {
                if (dashboardDAO != null) {
                    dashboardDAO.close();
                }
            }

            // Lấy roleName nếu chưa có
            if (user != null && (user.getRoleName() == null || user.getRoleName().isEmpty())) {
                roleDAO = new RoleDAO();
                entity.Role role = roleDAO.getRoleById(user.getRoleId());
                if (role != null) {
                    user.setRoleName(role.getRoleName());
                    session.setAttribute("user", user);
                }
            }
        } finally {
            // Close all DAOs to prevent connection leaks
            if (dao != null) dao.close();
            if (exportRequestDAO != null) exportRequestDAO.close();
            if (userDAO != null) userDAO.close();
            if (permissionDAO != null) permissionDAO.close();
            if (purchaseRequestDAO != null) purchaseRequestDAO.close();
            if (repairRequestDAO != null) repairRequestDAO.close();
            if (requestDAO != null) requestDAO.close();
            if (inventoryDAO != null) inventoryDAO.close();
            if (roleDAO != null) roleDAO.close();
        }

        // TODO: Lấy thêm các số liệu dashboard khác nếu cần (yêu cầu mua, sửa chữa, ...)

        LOGGER.log(Level.INFO, "Forwarding to HomePage.jsp");
        request.getRequestDispatcher("HomePage.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private List<Map<String, Object>> buildTopWidgets(int totalStock,
            BigDecimal totalInventoryValue,
            DashboardDAO.TransactionStats importStats,
            DashboardDAO.TransactionStats exportStats,
            int pendingPurchaseRequestCount,
            int repairInProgressCount) {
        List<Map<String, Object>> cards = new ArrayList<>();
        DashboardDAO.TransactionStats safeImport = importStats != null ? importStats : DashboardDAO.TransactionStats.empty();
        DashboardDAO.TransactionStats safeExport = exportStats != null ? exportStats : DashboardDAO.TransactionStats.empty();
        BigDecimal safeInventoryValue = totalInventoryValue != null ? totalInventoryValue : BigDecimal.ZERO;

        cards.add(widgetCard(
                "Tồn kho tổng",
                totalStock,
                formatQuantity(BigDecimal.valueOf(totalStock)) + " units trong hệ thống",
                "fa-solid fa-boxes-stacked",
                "accent-primary",
                "number"));

        cards.add(widgetCard(
                "Giá trị tồn kho",
                safeInventoryValue,
                "Dựa trên lô hàng đang hoạt động",
                "fa-solid fa-coins",
                "accent-gold",
                "currency"));

        cards.add(widgetCard(
                "Nhập kho hôm nay",
                safeImport.getOrderCount(),
                composeTransactionMeta(safeImport),
                "fa-solid fa-arrow-down-long",
                "accent-success",
                "number"));

        cards.add(widgetCard(
                "Xuất kho hôm nay",
                safeExport.getOrderCount(),
                composeTransactionMeta(safeExport),
                "fa-solid fa-arrow-up-long",
                "accent-warning",
                "number"));

        cards.add(widgetCard(
                "Đơn đề nghị mua hàng",
                pendingPurchaseRequestCount,
                "Đang chờ duyệt",
                "fa-solid fa-file-circle-plus",
                "accent-info",
                "number"));

        cards.add(widgetCard(
                "Đơn sửa chữa",
                repairInProgressCount,
                "Đang xử lý",
                "fa-solid fa-screwdriver-wrench",
                "accent-secondary",
                "number"));

        return cards;
    }

    private Map<String, Object> widgetCard(String title, Object value, String meta, String icon, String accent, String format) {
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("title", title);
        card.put("value", value);
        card.put("meta", meta);
        card.put("icon", icon);
        card.put("accent", accent);
        card.put("format", format);
        return card;
    }

    private String composeTransactionMeta(DashboardDAO.TransactionStats stats) {
        String units = formatQuantity(stats.getTotalQuantity());
        return stats.getOrderCount() + " đơn · " + units + " units";
    }

    private String formatQuantity(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat.format(value != null ? value : BigDecimal.ZERO);
    }

}
