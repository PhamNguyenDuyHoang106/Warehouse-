package controller;

import dal.RoleDAO;
import dal.PermissionDAO;
import dal.RolePermissionDAO;
import dal.ModuleDAO;
import entity.Role;
import entity.Permission;
import entity.Module;
import entity.User;
import utils.PermissionHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RolePermissionServlet", urlPatterns = {"/RolePermission"})
public class RolePermissionServlet extends HttpServlet {

    private RoleDAO roleDAO = new RoleDAO();
    private PermissionDAO permissionDAO = new PermissionDAO();
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    private ModuleDAO moduleDAO = new ModuleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        // Admin có toàn quyền - PermissionHelper đã xử lý
        // Chỉ check permission nếu không phải admin
        if (currentUser.getRoleId() != 1 && !utils.PermissionHelper.hasPermission(currentUser, "Xem danh sách phân quyền")) {
            request.setAttribute("error", "You do not have permission to view permission list.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            String searchKeyword = request.getParameter("search");
            String selectedModule = request.getParameter("selectedModule");
            if (searchKeyword == null) searchKeyword = "";
            if (selectedModule == null) selectedModule = "";

            List<Role> roles = roleDAO.getAllRolesIncludingAdmin();
            System.out.println("✅ Loaded " + roles.size() + " roles");
            
            List<Permission> permissions = searchKeyword.isEmpty() ? 
                permissionDAO.getAllPermissions() : 
                permissionDAO.searchPermissions(searchKeyword);
            System.out.println("✅ Loaded " + permissions.size() + " permissions");
            
            List<Module> modules = moduleDAO.getAllModules();
            System.out.println("✅ Loaded " + modules.size() + " modules");

            // Ánh xạ quyền theo module
            Map<Integer, List<Permission>> permissionsByModule = permissions.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getModuleId() != null ? p.getModuleId() : 0,
                    Collectors.toList()
                ));

            // Lọc modules chỉ chứa quyền khớp với từ khóa (trừ "Other")
            modules = modules.stream()
                .filter(m -> permissionsByModule.containsKey(m.getModuleId()))
                .collect(Collectors.toList());

            // Lọc roles: chỉ hiển thị roles có is_system = 0 (không phải system roles)
            // Admin (role_id = 1) KHÔNG hiển thị vì đã có full quyền
            List<Role> displayRoles = new ArrayList<>();
            for (Role role : roles) {
                // Chỉ hiển thị các role có is_system = 0 (không phải Admin và không phải system roles)
                if (role.getRoleId() != 1 && !role.isIsSystem()) {
                    displayRoles.add(role);
                }
            }
            System.out.println("✅ Filtered roles: " + displayRoles.size() + " roles (excluding Admin and system roles)");

            // Ánh xạ quyền đã gán cho từng vai trò (chỉ cho các role được hiển thị)
            Map<Integer, Map<Integer, Boolean>> rolePermissionMap = new HashMap<>();
            for (Role role : displayRoles) {
                // Load permissions từ database cho từng role
                List<Permission> assignedPermissions = permissionDAO.getPermissionsByRole(role.getRoleId());
                Map<Integer, Boolean> permMap = new HashMap<>();
                for (Permission perm : permissions) {
                    permMap.put(perm.getPermissionId(),
                        assignedPermissions.stream().anyMatch(p -> p.getPermissionId() == perm.getPermissionId()));
                }
                rolePermissionMap.put(role.getRoleId(), permMap);
            }
            System.out.println("✅ Built rolePermissionMap for " + rolePermissionMap.size() + " roles");
            
            request.setAttribute("roles", displayRoles);
            request.setAttribute("modules", modules);
            request.setAttribute("permissionsByModule", permissionsByModule);
            request.setAttribute("rolePermissionMap", rolePermissionMap);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedModule", selectedModule);
            
            System.out.println("✅ Forwarding to RolePermission.jsp with " + displayRoles.size() + " roles, " + modules.size() + " modules, " + permissionsByModule.size() + " permission groups");

            request.getRequestDispatcher("RolePermission.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            System.out.println("❌ Error in doGet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("System error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || !utils.PermissionHelper.hasPermission(currentUser, "Cập nhật phân quyền")) {
            response.sendRedirect("Login.jsp");
            return;
        }

        try {
            String searchKeyword = request.getParameter("search");
            String selectedModule = request.getParameter("selectedModule");
            if (searchKeyword == null) searchKeyword = "";
            if (selectedModule == null) selectedModule = "";

            String action = request.getParameter("action");
            if (!"update".equals(action)) {
                request.setAttribute("errorMessage", "Invalid action!");
                request.setAttribute("searchKeyword", searchKeyword);
                request.setAttribute("selectedModule", selectedModule);
                doGet(request, response);
                return;
            }

            List<Role> roles = roleDAO.getAllRolesIncludingAdmin();
            List<Permission> permissions = permissionDAO.getAllPermissions();

            for (Role role : roles) {
                for (Permission perm : permissions) {
                    String paramName = "permission_" + role.getRoleId() + "_" + perm.getPermissionId();
                    boolean isChecked = request.getParameter(paramName) != null;
                    boolean hasPermission = rolePermissionDAO.hasPermission(role.getRoleId(), perm.getPermissionName());

                    if (isChecked && !hasPermission) {
                        rolePermissionDAO.assignPermissionToRole(role.getRoleId(), perm.getPermissionId());
                    } else if (!isChecked && hasPermission) {
                        rolePermissionDAO.removePermissionFromRole(role.getRoleId(), perm.getPermissionId());
                    }
                }
            }

            request.setAttribute("successMessage", "Permissions updated successfully!");
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedModule", selectedModule);
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            request.setAttribute("searchKeyword", request.getParameter("search") != null ? request.getParameter("search") : "");
            request.setAttribute("selectedModule", request.getParameter("selectedModule") != null ? request.getParameter("selectedModule") : "");
            System.out.println("❌ Error in doPost: " + e.getMessage());
            e.printStackTrace();
            doGet(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing role permissions";
    }
}