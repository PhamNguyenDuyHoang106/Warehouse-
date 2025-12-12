package dal;

import entity.DBContext;
import entity.Permission;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PermissionDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(PermissionDAO.class.getName());

    public List<Permission> getAllPermissions() {
        List<Permission> permissionList = new ArrayList<>();
        String sql = "SELECT permission_id, permission_name, description, module_id FROM Permissions";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setPermissionId(rs.getInt("permission_id"));
                permission.setPermissionName(rs.getString("permission_name"));
                permission.setDescription(rs.getString("description"));
                permission.setModuleId(rs.getObject("module_id") != null ? rs.getInt("module_id") : null);
                permissionList.add(permission);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all permissions", e);
        }
        return permissionList;
    }

    public List<Permission> getPermissionsByRole(int roleId) {
        List<Permission> permissionList = new ArrayList<>();
        String sql = "SELECT p.permission_id, p.permission_name, p.description, p.module_id " +
                     "FROM Permissions p JOIN Role_Permissions rp ON p.permission_id = rp.permission_id " +
                     "WHERE rp.role_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("permission_id"));
                    permission.setPermissionName(rs.getString("permission_name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setModuleId(rs.getObject("module_id") != null ? rs.getInt("module_id") : null);
                    permissionList.add(permission);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting permissions by role", e);
        }
        return permissionList;
    }

    public List<Permission> searchPermissions(String keyword) {
        List<Permission> permissionList = new ArrayList<>();
        String sql = "SELECT p.permission_id, p.permission_name, p.description, p.module_id " +
                     "FROM Permissions p LEFT JOIN Modules m ON p.module_id = m.module_id " +
                     "WHERE p.permission_name LIKE ? OR p.description LIKE ? OR m.module_name LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String likePattern = "%" + keyword + "%";
            ps.setString(1, likePattern);
            ps.setString(2, likePattern);
            ps.setString(3, likePattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("permission_id"));
                    permission.setPermissionName(rs.getString("permission_name"));
                    permission.setDescription(rs.getString("description"));
                    permission.setModuleId(rs.getObject("module_id") != null ? rs.getInt("module_id") : null);
                    permissionList.add(permission);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching permissions", e);
        }
        return permissionList;
    }
}