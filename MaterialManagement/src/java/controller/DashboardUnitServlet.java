package controller;

import dal.UnitDAO;
import entity.Unit;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dal.RolePermissionDAO;
import utils.PermissionHelper;

@WebServlet(name = "DashboardUnitServlet", urlPatterns = {"/UnitList"})
public class DashboardUnitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check user authentication
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        // Admin có toàn quyền - PermissionHelper đã xử lý
        boolean hasPermission = PermissionHelper.hasPermission(user, "DS đơn vị");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to view unit list.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");
        if ("view".equals(action)) {
            viewUnit(request, response, user);
            return;
        }

        UnitDAO unitDAO = null;
        RolePermissionDAO rolePermissionDAO = null;
        try {
            unitDAO = new UnitDAO();
            String keyword = request.getParameter("keyword");
            String pageParam = request.getParameter("page");
            int page = 1;
            int pageSize = 5;
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            int totalUnits = unitDAO.countUnits(keyword);
            int totalPages = (int) Math.ceil((double) totalUnits / pageSize);
            if (page > totalPages && totalPages > 0) page = totalPages;
            int offset = (page - 1) * pageSize;
            List<Unit> units = unitDAO.getUnitsByPage(offset, pageSize, keyword);
            rolePermissionDAO = new RolePermissionDAO();
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.setAttribute("units", units);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("keyword", keyword);
            request.getRequestDispatcher("DashboardUnit.jsp").forward(request, response);
        } finally {
            if (unitDAO != null) unitDAO.close();
            if (rolePermissionDAO != null) rolePermissionDAO.close();
        }
    }

    private void viewUnit(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            UnitDAO unitDAO = null;
            RolePermissionDAO rolePermissionDAO = null;
            try {
                int id = Integer.parseInt(idStr);
                unitDAO = new UnitDAO();
                Unit unit = unitDAO.getUnitById(id);
                if (unit != null) {
                    rolePermissionDAO = new RolePermissionDAO();
                    request.setAttribute("unit", unit);
                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                    request.getRequestDispatcher("/UnitDetail.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Unit not found with ID: " + id);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid unit ID.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } finally {
                if (unitDAO != null) unitDAO.close();
                if (rolePermissionDAO != null) rolePermissionDAO.close();
            }
        } else {
            request.setAttribute("error", "Unit ID is required.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
