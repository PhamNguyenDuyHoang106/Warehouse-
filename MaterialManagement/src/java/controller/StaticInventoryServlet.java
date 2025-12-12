package controller;

import dal.InventoryDAO;
import dal.UserDAO;
import dal.ImportDAO;
import dal.ExportDAO;
import dal.RolePermissionDAO;
import entity.Inventory;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaticInventoryServlet", urlPatterns = {"/StaticInventory"})
public class StaticInventoryServlet extends BaseServlet {
    private InventoryDAO inventoryDAO;
    private UserDAO userDAO;
    private ImportDAO importDAO;
    private ExportDAO exportDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        super.init();
        inventoryDAO = new InventoryDAO();
        userDAO = new UserDAO();
        importDAO = new ImportDAO();
        exportDAO = new ExportDAO();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(inventoryDAO);
        registerDAO(userDAO);
        registerDAO(importDAO);
        registerDAO(exportDAO);
        registerDAO(rolePermissionDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(currentUser, "Báo cáo tồn kho")) {
            request.setAttribute("error", "Bạn không có quyền truy cập dữ liệu kho.");
            request.getRequestDispatcher("/StaticInventory.jsp").forward(request, response);
            return;
        }

        boolean hasViewReportPermission = PermissionHelper.hasPermission(currentUser, "Xuất báo cáo");
        request.setAttribute("hasViewReportPermission", hasViewReportPermission);
        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.setAttribute("roleId", currentUser.getRoleId());

        String searchTerm = request.getParameter("search");
        String stockFilter = request.getParameter("filter");
        String sortStock = request.getParameter("sortStock");
        String pageStr = request.getParameter("page");
        int currentPage = pageStr != null ? Integer.parseInt(pageStr) : 1;
        if (currentPage < 1) currentPage = 1;
        
        try {
            Map<String, Integer> stats = inventoryDAO.getInventoryStatistics();
            int totalStock = stats.getOrDefault("totalStock", 0);
            int lowStockCount = stats.getOrDefault("lowStockCount", 0);
            int outOfStockCount = stats.getOrDefault("outOfStockCount", 0);
            int totalItems = inventoryDAO.getInventoryCount(searchTerm, stockFilter);
            int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            List<Inventory> inventoryList = inventoryDAO.getInventoryWithPagination(searchTerm, stockFilter, sortStock, currentPage, PAGE_SIZE);
            Map<Integer, User> userMap = new HashMap<>();
            for (Inventory inv : inventoryList) {
                if (inv.getUpdatedBy() != null && inv.getUpdatedBy() > 0 && !userMap.containsKey(inv.getUpdatedBy())) {
                    User user = userDAO.getUserById(inv.getUpdatedBy());
                    if (user != null) {
                        userMap.put(inv.getUpdatedBy(), user);
                    }
                }
            }

            int totalImported = 0;
            int totalExported = 0;

            try {
                totalImported = importDAO.getTotalImportedQuantity();
            } catch (Exception ex) {
                totalImported = 0;
                System.err.println("Error getting total imported: " + ex.getMessage());
            }

            try {
                totalExported = exportDAO.getTotalExportedQuantity();
            } catch (Exception ex) {
                totalExported = 0;
                System.err.println("Error getting total exported: " + ex.getMessage());
            }

            request.setAttribute("totalImported", totalImported);
            request.setAttribute("totalExported", totalExported);
            request.setAttribute("totalStock", totalStock);
            request.setAttribute("lowStockCount", lowStockCount);
            request.setAttribute("outOfStockCount", outOfStockCount);
            request.setAttribute("inventoryList", inventoryList);
            request.setAttribute("userMap", userMap);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("pageSize", PAGE_SIZE);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("stockFilter", stockFilter);
            request.setAttribute("sortStock", sortStock);

        } catch (Exception e) {
            request.setAttribute("error", "Error loading inventory data: " + e.getMessage());
            System.err.println("Error in StaticInventoryServlet: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/StaticInventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}