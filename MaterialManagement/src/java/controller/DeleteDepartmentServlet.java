package controller;

import dal.DepartmentDAO;
import dal.RolePermissionDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/deletedepartment"})
public class DeleteDepartmentServlet extends HttpServlet {
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Check DELETE_DEPARTMENT permission
        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "DELETE_DEPARTMENT")) {
            request.setAttribute("error", "You do not have permission to delete the department..");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int deleteId = Integer.parseInt(request.getParameter("id"));
            departmentDAO.deleteDepartment(deleteId);
            request.setAttribute("message", "Department deleted successfully!");
            response.sendRedirect("depairmentlist");
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi xóa phòng ban: " + e.getMessage());
            request.setAttribute("error", "Error when deleting department: " + e.getMessage());
            request.getRequestDispatcher("depairmentlist").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Delete Department Servlet";
    }
}