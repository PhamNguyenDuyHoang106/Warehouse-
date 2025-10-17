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

@WebServlet(name = "DeleteUnitServlet", urlPatterns = {"/DeleteUnit"})
public class DeleteUnitServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        dal.RolePermissionDAO rolePermissionDAO = new dal.RolePermissionDAO();
        entity.User user = (session != null) ? (entity.User) session.getAttribute("user") : null;
        if (user == null || !rolePermissionDAO.hasPermission(user.getRoleId(), "DELETE_UNIT")) {
            request.setAttribute("error", "You don't have permission to delete units.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        dal.UnitDAO unitDAO = new dal.UnitDAO();
        int id = Integer.parseInt(request.getParameter("id"));
        unitDAO.deleteUnit(id); // Chỉ xóa unit, không xóa materials
        response.sendRedirect("UnitList");
    }
}
