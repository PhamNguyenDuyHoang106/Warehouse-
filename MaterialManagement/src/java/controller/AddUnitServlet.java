package controller;

import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Unit;
import entity.User;
import utils.UnitValidator;
import utils.PermissionHelper;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AddUnitServlet", urlPatterns = {"/AddUnit"})
public class AddUnitServlet extends HttpServlet {
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
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
        if (!PermissionHelper.hasPermission(user, "Tạo đơn vị")) {
            request.setAttribute("error", "You do not have permission to add new unit.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
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
        if (!PermissionHelper.hasPermission(user, "Tạo đơn vị")) {
            request.setAttribute("error", "You do not have permission to add new unit.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String unitCode = request.getParameter("unitCode");
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String isBaseStr = request.getParameter("isBase");
        String status = request.getParameter("status");
        
        boolean isBase = "true".equalsIgnoreCase(isBaseStr);
        if (status == null || status.trim().isEmpty()) {
            status = "active";
        }
        
        // Validate form data
        Map<String, String> errors = UnitValidator.validateUnitFormData(unitName, symbol);
        
        if (!errors.isEmpty()) {
            // Set errors and form data back to request
            request.setAttribute("errors", errors);
            request.setAttribute("unitCode", unitCode);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("isBase", isBase);
            request.setAttribute("status", status);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
            return;
        }
        
        // Create unit object and validate
        Unit unit = new Unit();
        unit.setUnitCode(unitCode != null && !unitCode.trim().isEmpty() ? unitCode.trim() : null);
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setBase(isBase);
        unit.setStatus(status);
        
        UnitDAO unitDAO = new UnitDAO();
        Map<String, String> validationErrors = UnitValidator.validateUnit(unit, unitDAO);
        
        if (!validationErrors.isEmpty()) {
            request.setAttribute("errors", validationErrors);
            request.setAttribute("unitCode", unitCode);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("isBase", isBase);
            request.setAttribute("status", status);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
            return;
        }
        
        // Add unit to database
        try {
            unitDAO.addUnit(unit);
            response.sendRedirect("UnitList");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to add unit. Please try again.");
            request.setAttribute("unitCode", unitCode);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("isBase", isBase);
            request.setAttribute("status", status);
            request.getRequestDispatcher("AddUnit.jsp").forward(request, response);
        }
    }
} 