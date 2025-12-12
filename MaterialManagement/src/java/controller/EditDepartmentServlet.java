package controller;

import dal.DepartmentDAO;
import dal.RolePermissionDAO;
import entity.Department;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/editdepartment"})
public class EditDepartmentServlet extends BaseServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Sửa phòng ban")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            request.setAttribute("error", "Invalid department ID.");
            response.sendRedirect("depairmentlist");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Department dept = departmentDAO.getDepartmentById(id);
            if (dept == null) {
                request.setAttribute("error", "Department does not exist or has been deleted.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            request.setAttribute("department", dept);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid department ID format.");
            response.sendRedirect("depairmentlist");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Sửa phòng ban")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa phòng ban.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String location = request.getParameter("location");
        String description = request.getParameter("description");
        String code = request.getParameter("code");
        String status = request.getParameter("status");

        // Validate department ID
        Map<String, String> errors = new HashMap<>();
        if (idStr == null || idStr.trim().isEmpty()) {
            errors.put("id", "Department ID cannot be blank.");
        } else {
            try {
                Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                errors.put("id", "Invalid department ID.");
            }
        }

        // Validate form data
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Department name cannot be left blank.");
        }
        if (code == null || code.trim().isEmpty()) {
            errors.put("code", "Department code cannot be blank.");
        }
        if (status == null || status.trim().isEmpty()) {
            errors.put("status", "Status cannot be blank.");
        } else if (!status.equals("active") && !status.equals("inactive")) {
            errors.put("status", "Invalid status value.");
        }

        int id = 0;
        Department dept = null;
        if (!errors.isEmpty()) {
            try {
                id = Integer.parseInt(idStr);
                dept = departmentDAO.getDepartmentById(id);
            } catch (NumberFormatException e) {
                // ID is invalid, handled by errors map
            }
            request.setAttribute("errors", errors);
            request.setAttribute("name", name);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("location", location);
            request.setAttribute("description", description);
            request.setAttribute("code", code);
            request.setAttribute("status", status);
            request.setAttribute("department", dept);
            request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
            return;
        }

        id = Integer.parseInt(idStr);
        dept = new Department();
        dept.setDepartmentId(id);
        dept.setDepartmentName(name);
        dept.setDepartmentCode(code);
        dept.setPhoneNumber(phone);
        dept.setEmail(email);
        dept.setLocation(location);
        dept.setDescription(description);
        dept.setStatus(Department.Status.valueOf(status));
        dept.setUpdatedAt(LocalDateTime.now());

        try {
            departmentDAO.updateDepartment(dept);
            response.sendRedirect("depairmentlist");
        } catch (Exception e) {
            errors.put("database", "Lỗi khi cập nhật phòng ban: " + e.getMessage());
            request.setAttribute("errors", errors);
            request.setAttribute("name", name);
            request.setAttribute("phone", phone);
            request.setAttribute("email", email);
            request.setAttribute("location", location);
            request.setAttribute("description", description);
            request.setAttribute("code", code);
            request.setAttribute("status", status);
            request.setAttribute("department", dept);
            request.getRequestDispatcher("EditDepartment.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Edit Department Servlet";
    }
}