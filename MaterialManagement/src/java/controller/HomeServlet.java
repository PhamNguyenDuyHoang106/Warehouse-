/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.ExportRequestDAO;
import dal.InventoryDAO;
import dal.RoleDAO;
import dal.PurchaseRequestDAO;
import dal.RepairRequestDAO;
import dal.RequestDAO;
import dal.UserDAO;
import entity.Category;
import entity.Material;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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
        
        // Lấy thống kê cơ bản
        MaterialDAO dao = new MaterialDAO();
        int materialCount = dao.countMaterials(null, null);
        request.setAttribute("materialCount", materialCount);



        // Dashboard tổng quan cho tất cả role
        request.setAttribute("materialCount", materialCount);

        // Số yêu cầu xuất kho chờ duyệt
        ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
        int pendingExportRequestCount = exportRequestDAO.getTotalCount("pending", null, null);
        request.setAttribute("pendingExportRequestCount", pendingExportRequestCount);

        // Tổng số người dùng (chỉ cho admin)
        UserDAO userDAO = new UserDAO();
        int totalUserCount = userDAO.getUserCountByFilter(null, null, null, null);
        request.setAttribute("totalUserCount", totalUserCount);

        // Tổng số quyền (chỉ cho admin)
        dal.PermissionDAO permissionDAO = new dal.PermissionDAO();
        int totalPermissionCount = permissionDAO.getAllPermissions().size();
        request.setAttribute("totalPermissionCount", totalPermissionCount);

        // Số yêu cầu mua vật tư chờ duyệt
        PurchaseRequestDAO purchaseRequestDAO = new PurchaseRequestDAO();
        int pendingPurchaseRequestCount = purchaseRequestDAO.countPurchaseRequest(null, "pending", null, null);
        request.setAttribute("pendingPurchaseRequestCount", pendingPurchaseRequestCount);

        // Số yêu cầu sửa chữa chờ duyệt
        RepairRequestDAO repairRequestDAO = new RepairRequestDAO();
        int pendingRepairRequestCount = 0;
        try {
            pendingRepairRequestCount = repairRequestDAO.getTotalRepairRequestCount(null, "Pending", null, null);
            LOGGER.log(Level.INFO, "Pending repair requests count: " + pendingRepairRequestCount);
        } catch (Exception e) {
            pendingRepairRequestCount = 0;
            LOGGER.log(Level.SEVERE, "Failed to get pending repair requests: " + e.getMessage(), e);
        }
        request.setAttribute("pendingRepairRequestCount", pendingRepairRequestCount);

        // Số yêu cầu của user hiện tại
        int myPurchaseRequestCount = 0;
        int myRepairRequestCount = 0;
        int myPendingRequestCount = 0;
        int myApprovedRequestCount = 0;
        int availableMaterialsCount = 0;
        
        if (user != null) {
            RequestDAO requestDAO = new RequestDAO();
            myPurchaseRequestCount = requestDAO.getPurchaseRequestCountByUser(user.getUserId(), null, null, null, null, null);
            myRepairRequestCount = requestDAO.getRepairRequestCountByUser(user.getUserId(), null, null, null, null, null);
            
            // Đếm yêu cầu pending và approved của user
            myPendingRequestCount = requestDAO.getExportRequestCountByUser(user.getUserId(), "pending", null, null, null, null);
            myApprovedRequestCount = requestDAO.getExportRequestCountByUser(user.getUserId(), "approved", null, null, null, null);
            
            // Đếm vật tư có sẵn (có stock > 0)
            availableMaterialsCount = dao.countMaterials(null, "new") + dao.countMaterials(null, "used");
        }
        request.setAttribute("myPurchaseRequestCount", myPurchaseRequestCount);
        request.setAttribute("myRepairRequestCount", myRepairRequestCount);
        request.setAttribute("myPendingRequestCount", myPendingRequestCount);
        request.setAttribute("myApprovedRequestCount", myApprovedRequestCount);
        request.setAttribute("availableMaterialsCount", availableMaterialsCount);

        // Thống kê tồn kho
        InventoryDAO inventoryDAO = new InventoryDAO();
        int totalStock = 0, lowStockCount = 0, outOfStockCount = 0;
        try {
            Map<String, Integer> stats = inventoryDAO.getInventoryStatistics();
            totalStock = stats.getOrDefault("totalStock", 0);
            lowStockCount = stats.getOrDefault("lowStockCount", 0);
            outOfStockCount = stats.getOrDefault("outOfStockCount", 0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting inventory statistics: " + e.getMessage(), e);
        }
        request.setAttribute("totalStock", totalStock);
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("outOfStockCount", outOfStockCount);

        // Đếm vật tư bị hư hỏng
        int damagedMaterialsCount = dao.countMaterials(null, "damaged");
        request.setAttribute("damagedMaterialsCount", damagedMaterialsCount);

        // Thống kê Import/Export cho Reports section
        dal.ImportDAO importDAO = new dal.ImportDAO();
        dal.ExportDAO exportDAO = new dal.ExportDAO();
        int totalImported = 0, totalExported = 0;
        try {
            totalImported = importDAO.getTotalImportedQuantity();
        } catch (Exception e) {
            totalImported = 0;
            LOGGER.log(Level.SEVERE, "Error getting total imported quantity: " + e.getMessage(), e);
        }
        try {
            totalExported = exportDAO.getTotalExportedQuantity();
        } catch (Exception e) {
            totalExported = 0;
            LOGGER.log(Level.SEVERE, "Error getting total exported quantity: " + e.getMessage(), e);
        }
        request.setAttribute("totalImported", totalImported);
        request.setAttribute("totalExported", totalExported);

        // Lấy roleName nếu chưa có
        if (user != null && (user.getRoleName() == null || user.getRoleName().isEmpty())) {
            RoleDAO roleDAO = new RoleDAO();
            entity.Role role = roleDAO.getRoleById(user.getRoleId());
            if (role != null) {
                user.setRoleName(role.getRoleName());
                session.setAttribute("user", user);
            }
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

}
