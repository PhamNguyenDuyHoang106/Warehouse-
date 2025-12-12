package dal;

import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RolePermissionDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(RolePermissionDAO.class.getName());

    public void assignPermissionToRole(int roleId, int permissionId) {
        String sql = "INSERT INTO Role_Permissions (role_id, permission_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error assigning permission to role", e);
        }
    }

    public void removePermissionFromRole(int roleId, int permissionId) {
        String sql = "DELETE FROM Role_Permissions WHERE role_id = ? AND permission_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error removing permission from role", e);
        }
    }

    public boolean hasPermission(int roleId, String permissionName) {
        // Check permission_name (this is what exists in database)
        String sql = "SELECT COUNT(*) FROM Role_Permissions rp JOIN Permissions p ON rp.permission_id = p.permission_id WHERE rp.role_id = ? AND p.permission_name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setString(2, permissionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking permission", e);
        }
        return false;
    }
}