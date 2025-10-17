package controller;

import dal.UnitDAO;
import entity.Unit;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dal.RolePermissionDAO;

@WebServlet(name = "DashboardUnitServlet", urlPatterns = {"/UnitList"})
public class DashboardUnitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnitDAO unitDAO = new UnitDAO();
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
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.setAttribute("units", units);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("DashboardUnit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
