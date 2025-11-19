package controller;

import dal.UnitDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dal.RolePermissionDAO;
import entity.User;
import utils.PermissionHelper;

@WebServlet(name = "DeleteUnitServlet", urlPatterns = {"/DeleteUnit"})
public class DeleteUnitServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        dal.RolePermissionDAO rolePermissionDAO = new dal.RolePermissionDAO();
        entity.User user = (session != null) ? (entity.User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Xóa đơn vị")) {
            request.setAttribute("error", "Bạn không có quyền xóa đơn vị.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        dal.UnitDAO unitDAO = new dal.UnitDAO();
        int id = Integer.parseInt(request.getParameter("id"));
        unitDAO.deleteUnit(id); // Chỉ xóa unit, không xóa materials
        response.sendRedirect("UnitList");
    }
}
