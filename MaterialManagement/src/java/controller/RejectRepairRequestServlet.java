package controller;

import dal.RepairRequestDAO;
import dal.RolePermissionDAO;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RejectRepairRequestServlet", urlPatterns = {"/reject"})
public class RejectRepairRequestServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    private RepairRequestDAO repairRequestDAO = new RepairRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Admin có toàn quyền - PermissionHelper đã xử lý
        // Reject repair request không có permission riêng trong DB, dùng chung với approve
        if (!PermissionHelper.hasPermission(user, "Duyệt yêu cầu sửa")) {
            request.setAttribute("error", "Bạn không có quyền từ chối yêu cầu sửa.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String reason = request.getParameter("reason");

            repairRequestDAO.updateStatus(requestId, "reject", user.getUserId(), reason);
            response.sendRedirect("repairrequestlist");

        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("ErrorRepair.jsp").forward(request, response);
        } catch (SQLException | NumberFormatException ex) {
            Logger.getLogger(RejectRepairRequestServlet.class.getName()).log(Level.SEVERE, null, ex);
            request.setAttribute("error", "Failed to process request. Please try again.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for rejecting repair requests";
    }
}