package dal;

import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RolePermissionDAO extends DBContext {

    public void assignPermissionToRole(int roleId, int permissionId) {
        String sql = "INSERT INTO Role_Permissions (role_id, permission_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
            System.out.println("✅ Gán permission " + permissionId + " cho role " + roleId + " thành công");
        } catch (Exception e) {
            System.out.println("❌ Lỗi assignPermissionToRole: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removePermissionFromRole(int roleId, int permissionId) {
        String sql = "DELETE FROM Role_Permissions WHERE role_id = ? AND permission_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
            System.out.println("✅ Xóa permission " + permissionId + " khỏi role " + roleId + " thành công");
        } catch (Exception e) {
            System.out.println("❌ Lỗi removePermissionFromRole: " + e.getMessage());
            e.printStackTrace();
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
                    boolean hasPerm = rs.getInt(1) > 0;
                    System.out.println("🔍 Checking permission '" + permissionName + "' for role " + roleId + ": " + (hasPerm ? "GRANTED" : "DENIED"));
                    return hasPerm;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi hasPermission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}