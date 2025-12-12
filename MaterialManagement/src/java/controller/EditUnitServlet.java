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

@WebServlet(name = "EditUnitServlet", urlPatterns = {"/EditUnit"})
public class EditUnitServlet extends BaseServlet {
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(rolePermissionDAO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Sửa đơn vị")) {
            request.setAttribute("error", "You do not have permission to edit unit.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        UnitDAO unitDAO = null;
        try {
            unitDAO = new UnitDAO();
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Unit unit = unitDAO.getUnitById(id);
                request.setAttribute("unit", unit);
            }
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
        } finally {
            if (unitDAO != null) unitDAO.close();
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
        if (!PermissionHelper.hasPermission(user, "Sửa đơn vị")) {
            request.setAttribute("error", "You do not have permission to edit unit.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String idStr = request.getParameter("id");
        String unitCode = request.getParameter("unitCode");
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String isBaseStr = request.getParameter("isBase");
        String status = request.getParameter("status");
        
        boolean isBase = "true".equalsIgnoreCase(isBaseStr);
        if (status == null || status.trim().isEmpty()) {
            status = "active";
        }
        
        // Validate unit ID
        Map<String, String> idErrors = UnitValidator.validateUnitId(idStr);
        if (!idErrors.isEmpty()) {
            request.setAttribute("error", "Invalid unit ID.");
            response.sendRedirect("UnitList");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        
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
            
            // Get unit data for form
            UnitDAO unitDAO = new UnitDAO();
            Unit unit = unitDAO.getUnitById(id);
            request.setAttribute("unit", unit);
            
            request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
            return;
        }
        
        // Create unit object and validate
        Unit unit = new Unit();
        unit.setId(id);
        unit.setUnitCode(unitCode != null && !unitCode.trim().isEmpty() ? unitCode.trim() : null);
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setBase(isBase);
        unit.setStatus(status);
        
        UnitDAO unitDAO = null;
        try {
            unitDAO = new UnitDAO();
            Map<String, String> validationErrors = UnitValidator.validateUnitUpdate(unit, unitDAO);
            
            if (!validationErrors.isEmpty()) {
                request.setAttribute("errors", validationErrors);
                request.setAttribute("unitCode", unitCode);
                request.setAttribute("unitName", unitName);
                request.setAttribute("symbol", symbol);
                request.setAttribute("isBase", isBase);
                request.setAttribute("status", status);
                request.setAttribute("unit", unit);
                request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
                return;
            }
            
            // Update unit in database
            unitDAO.updateUnit(unit);
            response.sendRedirect("UnitList");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update unit. Please try again.");
            request.setAttribute("unitCode", unitCode);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("isBase", isBase);
            request.setAttribute("status", status);
            request.setAttribute("unit", unit);
            request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
        } finally {
            if (unitDAO != null) unitDAO.close();
        }
    }
}
