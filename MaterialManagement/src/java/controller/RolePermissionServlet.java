package controller;

import dal.RoleDAO;
import dal.PermissionDAO;
import dal.RolePermissionDAO;
import dal.ModuleDAO;
import entity.Role;
import entity.Permission;
import entity.Module;
import entity.User;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RolePermissionServlet", urlPatterns = {"/RolePermission"})
public class RolePermissionServlet extends BaseServlet {

    private static final Logger LOGGER = Logger.getLogger(RolePermissionServlet.class.getName());
    private RoleDAO roleDAO;
    private PermissionDAO permissionDAO;
    private RolePermissionDAO rolePermissionDAO;
    private ModuleDAO moduleDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        roleDAO = new RoleDAO();
        permissionDAO = new PermissionDAO();
        rolePermissionDAO = new RolePermissionDAO();
        moduleDAO = new ModuleDAO();
        registerDAO(roleDAO);
        registerDAO(permissionDAO);
        registerDAO(rolePermissionDAO);
        registerDAO(moduleDAO);
    }

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
            String selectedCategory = request.getParameter("category"); // Changed from selectedModule to category
            if (searchKeyword == null) searchKeyword = "";
            if (selectedCategory == null || selectedCategory.isEmpty()) {
                selectedCategory = null; // null means show category list
            }

            List<Role> roles = roleDAO.getAllRolesIncludingAdmin();
            
            List<Module> allModules = moduleDAO.getAllModules();
            
            // Get permissions - if category selected, filter by category
            List<Permission> allPermissions = permissionDAO.getAllPermissions();
            List<Permission> permissions;
            
            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(selectedCategory);
                    permissions = allPermissions.stream()
                        .filter(p -> p.getModuleId() != null && p.getModuleId() == categoryId)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    permissions = allPermissions;
                }
            } else if (!searchKeyword.isEmpty()) {
                permissions = permissionDAO.searchPermissions(searchKeyword);
            } else {
                permissions = new ArrayList<>();
            }

            // Ánh xạ quyền theo module (only if we have permissions)
            Map<Integer, List<Permission>> permissionsByModule = new HashMap<>();
            if (selectedCategory != null) {
                permissionsByModule = permissions.stream()
                    .collect(Collectors.groupingBy(
                        p -> p.getModuleId() != null ? p.getModuleId() : 0,
                        Collectors.toList()
                    ));
            } else {
                // Group all permissions by module for counting
                Map<Integer, List<Permission>> allPermsByModule = allPermissions.stream()
                    .collect(Collectors.groupingBy(
                        p -> p.getModuleId() != null ? p.getModuleId() : 0,
                        Collectors.toList()
                    ));
                // Add permission counts to modules
                for (Module module : allModules) {
                    List<Permission> modulePerms = allPermsByModule.getOrDefault(module.getModuleId(), new ArrayList<>());
                    permissionsByModule.put(module.getModuleId(), modulePerms);
                }
            }

            // Filter modules to show - if category selected, show only that category
            List<Module> modules = new ArrayList<>();
            if (selectedCategory != null && !selectedCategory.isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(selectedCategory);
                    modules = allModules.stream()
                        .filter(m -> m.getModuleId() == categoryId)
                        .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    modules = allModules;
                }
            } else {
                // Show all modules with permission counts
                modules = allModules;
            }

            // Lọc roles: chỉ hiển thị roles có is_system = 0 (không phải system roles)
            // Admin (role_id = 1) KHÔNG hiển thị vì đã có full quyền
            List<Role> displayRoles = new ArrayList<>();
            for (Role role : roles) {
                // Chỉ hiển thị các role có is_system = 0 (không phải Admin và không phải system roles)
                if (role.getRoleId() != 1 && !role.isIsSystem()) {
                    displayRoles.add(role);
                }
            }

            // Ánh xạ quyền đã gán cho từng vai trò (chỉ cho các role được hiển thị)
            // Luôn build map với tất cả permissions để có thể save được
            Map<Integer, Map<Integer, Boolean>> rolePermissionMap = new HashMap<>();
            
            for (Role role : displayRoles) {
                // Load permissions từ database cho từng role
                List<Permission> assignedPermissions = permissionDAO.getPermissionsByRole(role.getRoleId());
                Map<Integer, Boolean> permMap = new HashMap<>();
                // Build map for all permissions (needed for saving in doPost)
                for (Permission perm : allPermissions) {
                    permMap.put(perm.getPermissionId(),
                        assignedPermissions.stream().anyMatch(p -> p.getPermissionId() == perm.getPermissionId()));
                }
                rolePermissionMap.put(role.getRoleId(), permMap);
            }
            
            request.setAttribute("roles", displayRoles);
            request.setAttribute("modules", modules);
            request.setAttribute("allModules", allModules);
            request.setAttribute("permissionsByModule", permissionsByModule);
            request.setAttribute("rolePermissionMap", rolePermissionMap);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedCategory", selectedCategory != null ? selectedCategory : "");

            request.getRequestDispatcher("RolePermission.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error in doGet", e);
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
            String selectedCategory = request.getParameter("category");
            if (searchKeyword == null) searchKeyword = "";
            if (selectedCategory == null) selectedCategory = "";

            String action = request.getParameter("action");
            if (!"update".equals(action)) {
                request.setAttribute("errorMessage", "Invalid action!");
                request.setAttribute("searchKeyword", searchKeyword);
                request.setAttribute("selectedCategory", selectedCategory);
                doGet(request, response);
                return;
            }

            List<Role> roles = roleDAO.getAllRolesIncludingAdmin();
            List<Permission> permissions = permissionDAO.getAllPermissions();

            // CRITICAL FIX: Chỉ xử lý các role được hiển thị trong form (không bao gồm admin và system roles)
            // Admin (role_id = 1) và system roles không được hiển thị trong form nên không có checkbox
            // Nếu xử lý chúng sẽ vô tình xóa permissions của admin!
            List<Role> displayRoles = new ArrayList<>();
            for (Role role : roles) {
                // Chỉ xử lý các role có is_system = 0 (không phải Admin và không phải system roles)
                if (role.getRoleId() != 1 && !role.isIsSystem()) {
                    displayRoles.add(role);
                }
            }
            
            // CHỈ xử lý permissions cho các role được hiển thị trong form
            for (Role role : displayRoles) {
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
            request.setAttribute("selectedCategory", selectedCategory);
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "System error: " + e.getMessage());
            request.setAttribute("searchKeyword", request.getParameter("search") != null ? request.getParameter("search") : "");
            request.setAttribute("selectedCategory", request.getParameter("category") != null ? request.getParameter("category") : "");
            LOGGER.log(Level.SEVERE, "Error in doPost", e);
            doGet(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing role permissions";
    }
}