package dal;

import entity.DBContext;
import entity.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {

    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String sql = "SELECT * FROM Roles WHERE disable = 0 AND role_id != 1"; 

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                roleList.add(role);
            }
            System.out.println("✅ Retrieved roles (excluding Admin) successfully, count: " + roleList.size());
        } catch (Exception e) {
            System.out.println("❌ Error in getAllRoles: " + e.getMessage());
            e.printStackTrace();
        }
        return roleList;
    }

    public Role getRoleById(int roleId) {
        String sql = "SELECT * FROM Roles WHERE role_id = ? AND disable = 0 AND role_id != 1"; // Loại bỏ Admin
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    role.setDescription(rs.getString("description"));
                    System.out.println("✅ Retrieved role with ID: " + roleId);
                    return role;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error in getRoleById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}