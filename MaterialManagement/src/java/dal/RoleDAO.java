package dal;

import entity.DBContext;
import entity.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoleDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(RoleDAO.class.getName());

    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String sql = "SELECT * FROM Roles WHERE (status = 'active' OR status = 1) AND role_id != 1"; 

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                role.setIsSystem(rs.getBoolean("is_system"));
                role.setStatus(rs.getString("status"));
                roleList.add(role);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all roles", e);
        }
        return roleList;
    }

    public Role getRoleById(int roleId) {
        String sql = "SELECT * FROM Roles WHERE role_id = ? AND status = 'active' AND role_id != 1"; // Loại bỏ Admin
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    role.setDescription(rs.getString("description"));
                    role.setIsSystem(rs.getBoolean("is_system"));
                    role.setStatus(rs.getString("status"));
                    return role;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting role by ID", e);
        }
        return null;
    }

    public Role getRoleByIdIncludingAdmin(int roleId) {
        String sql = "SELECT * FROM Roles WHERE role_id = ? AND status = 'active'"; // Bao gồm Admin
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    role.setDescription(rs.getString("description"));
                    role.setIsSystem(rs.getBoolean("is_system"));
                    role.setStatus(rs.getString("status"));
                    return role;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting role by ID including admin", e);
        }
        return null;
    }

    public List<Role> getAllRolesIncludingAdmin() {
        List<Role> roleList = new ArrayList<>();
        // Query: lấy tất cả roles, không filter theo status (vì có thể status không tồn tại hoặc có giá trị khác)
        String sql = "SELECT * FROM Roles ORDER BY role_id";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                
                // Handle is_system: có thể là boolean hoặc TINYINT(1)
                try {
                    role.setIsSystem(rs.getBoolean("is_system"));
                } catch (Exception e) {
                    // Nếu không phải boolean, thử getInt
                    int isSystemInt = rs.getInt("is_system");
                    role.setIsSystem(isSystemInt == 1);
                }
                
                // Handle status: có thể là string hoặc không có
                try {
                    String statusStr = rs.getString("status");
                    role.setStatus(statusStr);
                } catch (Exception e) {
                    role.setStatus(null);
                }
                
                roleList.add(role);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting all roles including admin", e);
        }
        return roleList;
    }
}