package controller;

import dal.DepartmentDAO;
import dal.RolePermissionDAO;
import entity.Department;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = {"/depairmentlist"})
public class DepartmentServlet extends HttpServlet {

    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        int roleId = user.getRoleId();
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_DEPARTMENT")) {
            request.setAttribute("error", "You do not have permission to view the department list.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Pagination parameters
        int page = 1;
        int pageSize = 10;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) {
                    page = 1;
                }
            }
            if (request.getParameter("pageSize") != null) {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
                if (pageSize < 1) {
                    pageSize = 10;
                }
            }
        } catch (NumberFormatException e) {
            page = 1;
            pageSize = 10;
        }
        int offset = (page - 1) * pageSize;

        // Filter and sort parameters
        String searchKeyword = request.getParameter("search");
        String sortByName = request.getParameter("sortByName");
        String statusFilter = request.getParameter("statusFilter");

        try {
            // Define statuses for dropdown
            List<String> statuses = Arrays.asList("Active", "Inactive", "Deleted");

            List<Department> departments = departmentDAO.getDepartmentsWithPagination(
                    offset, pageSize, searchKeyword, sortByName, statusFilter
            );
            int totalRecords = departmentDAO.getTotalDepartmentCount(searchKeyword, statusFilter);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // Set attributes for JSP
            request.setAttribute("departments", departments);
            request.setAttribute("statuses", statuses);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("sortByName", sortByName);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("DepartmentList.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("❌ Error when getting department list: " + e.getMessage());
            request.setAttribute("error", "Error when getting department list: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); 
    }

    @Override
    public String getServletInfo() {
        return "Department List Servlet";
    }
}