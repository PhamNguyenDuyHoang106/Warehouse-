package controller;

import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Unit;
import entity.User;
import utils.UnitValidator;
import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "EditUnitServlet", urlPatterns = {"/EditUnit"})
public class EditUnitServlet extends HttpServlet {
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
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (!rolePermissionDAO.hasPermission(roleId, "UPDATE_UNIT")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa đơn vị.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        UnitDAO unitDAO = new UnitDAO();
        String idStr = request.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            Unit unit = unitDAO.getUnitById(id);
            request.setAttribute("unit", unit);
        }
        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
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
        if (!rolePermissionDAO.hasPermission(roleId, "UPDATE_UNIT")) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa đơn vị.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String idStr = request.getParameter("id");
        String unitName = request.getParameter("unitName");
        String symbol = request.getParameter("symbol");
        String description = request.getParameter("description");
        
        // Validate unit ID
        Map<String, String> idErrors = UnitValidator.validateUnitId(idStr);
        if (!idErrors.isEmpty()) {
            request.setAttribute("error", "Invalid unit ID.");
            response.sendRedirect("UnitList");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        
        // Validate form data
        Map<String, String> errors = UnitValidator.validateUnitFormData(unitName, symbol, description);
        
        if (!errors.isEmpty()) {
            // Set errors and form data back to request
            request.setAttribute("errors", errors);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            
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
        unit.setUnitName(unitName);
        unit.setSymbol(symbol);
        unit.setDescription(description);
        
        UnitDAO unitDAO = new UnitDAO();
        Map<String, String> validationErrors = UnitValidator.validateUnitUpdate(unit, unitDAO);
        
        if (!validationErrors.isEmpty()) {
            request.setAttribute("errors", validationErrors);
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            request.setAttribute("unit", unit);
            request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
            return;
        }
        
        // Update unit in database
        try {
            unitDAO.updateUnit(unit);
            response.sendRedirect("UnitList");
        } catch (Exception e) {
            request.setAttribute("error", "Failed to update unit. Please try again.");
            request.setAttribute("unitName", unitName);
            request.setAttribute("symbol", symbol);
            request.setAttribute("description", description);
            request.setAttribute("unit", unit);
            request.getRequestDispatcher("EditUnit.jsp").forward(request, response);
        }
    }
}
