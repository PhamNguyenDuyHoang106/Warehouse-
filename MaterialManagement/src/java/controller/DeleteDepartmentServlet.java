package controller;

import dal.DepartmentDAO;
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

@WebServlet(urlPatterns = {"/deletedepartment"})
public class DeleteDepartmentServlet extends BaseServlet {
    private DepartmentDAO departmentDAO;
    private RolePermissionDAO rolePermissionDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        departmentDAO = new DepartmentDAO();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(departmentDAO);
        registerDAO(rolePermissionDAO);
    }

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
        if (!PermissionHelper.hasPermission(user, "Xóa phòng ban")) {
            request.setAttribute("error", "Bạn không có quyền xóa phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int deleteId = Integer.parseInt(request.getParameter("id"));
            departmentDAO.deleteDepartment(deleteId);
            request.setAttribute("message", "Department deleted successfully!");
            response.sendRedirect("depairmentlist");
        } catch (Exception e) {
            request.setAttribute("error", "Error when deleting department: " + e.getMessage());
            request.getRequestDispatcher("depairmentlist").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Delete Department Servlet";
    }
}