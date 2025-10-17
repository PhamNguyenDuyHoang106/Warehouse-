package dal;

import entity.DBContext;
import entity.Department;
import entity.Material;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DepartmentDAO extends DBContext {

    public List<Department> getAllDepartments() {
        List<Department> departmentList = new ArrayList<>();
        String sql = "SELECT * FROM Departments";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                dept.setDepartmentCode(rs.getString("department_code"));
                dept.setPhoneNumber(rs.getString("phone_number"));
                dept.setEmail(rs.getString("email"));
                dept.setLocation(rs.getString("location"));
                dept.setDescription(rs.getString("description"));
                dept.setStatus(rs.getString("status") != null ? Department.Status.valueOf(rs.getString("status")) : null);
                dept.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                dept.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                departmentList.add(dept);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return departmentList;
    }

    public List<Department> getDepartmentsWithPagination(int offset, int pageSize, String searchKeyword, String sortByName, String statusFilter) throws SQLException {
        List<Department> departmentList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Departments");
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" WHERE UPPER(department_code) LIKE ?");
        } else {
            sql.append(" WHERE 1=1");
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }
        List<String> orderByClauses = new ArrayList<>();
        if (sortByName != null && (sortByName.equals("asc") || sortByName.equals("desc"))) {
            orderByClauses.add("department_name " + sortByName.toUpperCase());
        }
        if (!orderByClauses.isEmpty()) {
            sql.append(" ORDER BY ").append(String.join(", ", orderByClauses));
        } else {
            sql.append(" ORDER BY department_id");
        }
        sql.append(" LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim().toUpperCase() + "%");
            }
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(paramIndex++, statusFilter.trim());
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department();
                    dept.setDepartmentId(rs.getInt("department_id"));
                    dept.setDepartmentName(rs.getString("department_name"));
                    dept.setDepartmentCode(rs.getString("department_code"));
                    dept.setPhoneNumber(rs.getString("phone_number"));
                    dept.setEmail(rs.getString("email"));
                    dept.setLocation(rs.getString("location"));
                    dept.setDescription(rs.getString("description"));
                    dept.setStatus(rs.getString("status") != null ? Department.Status.valueOf(rs.getString("status")) : null);
                    dept.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    dept.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    departmentList.add(dept);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return departmentList;
    }

    public int getTotalDepartmentCount(String searchKeyword, String statusFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Departments");
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" WHERE UPPER(department_code) LIKE ?");
        } else {
            sql.append(" WHERE 1=1");
        }
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim().toUpperCase() + "%");
            }
            if (statusFilter != null && !statusFilter.trim().isEmpty()) {
                ps.setString(paramIndex++, statusFilter.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getTotalDepartmentCount: " + e.getMessage());
            throw e;
        }
        return 0;
    }

    public List<String> getAllStatuses() {
        return Arrays.asList("Active", "Inactive", "Deleted");
    }

    public List<Department> getDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT department_id, department_name FROM Departments WHERE status = 'active'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                departments.add(dept);
            }
        } catch (SQLException e) {
            throw e;
        }
        return departments;
    }

    public List<Material> getMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT material_id, material_code, material_name, materials_url FROM Materials WHERE disable = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialCode(rs.getString("material_code"));
                material.setMaterialName(rs.getString("material_name"));
                material.setMaterialsUrl(rs.getString("materials_url"));
                materials.add(material);
            }
            System.out.println("✅ Get list of materials successfully, quantity: " + materials.size());
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getMaterials: " + e.getMessage());
            throw e;
        }
        return materials;
    }

    public void addDepartment(Department dept) {
        String sql = "INSERT INTO Departments (department_name, department_code, phone_number, email, location, description, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dept.getDepartmentName());
            ps.setString(2, dept.getDepartmentCode());
            ps.setString(3, dept.getPhoneNumber());
            ps.setString(4, dept.getEmail());
            ps.setString(5, dept.getLocation());
            ps.setString(6, dept.getDescription());
            ps.setString(7, dept.getStatus().toString());
            ps.setTimestamp(8, java.sql.Timestamp.valueOf(dept.getCreatedAt()));
            ps.setTimestamp(9, java.sql.Timestamp.valueOf(dept.getUpdatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDepartment(Department dept) {
        String sql = "UPDATE Departments SET department_name = ?, department_code = ?, phone_number = ?, email = ?, location = ?, description = ?, "
                + "status = ?, updated_at = ? WHERE department_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dept.getDepartmentName());
            ps.setString(2, dept.getDepartmentCode());
            ps.setString(3, dept.getPhoneNumber());
            ps.setString(4, dept.getEmail());
            ps.setString(5, dept.getLocation());
            ps.setString(6, dept.getDescription());
            ps.setString(7, dept.getStatus().toString());
            ps.setTimestamp(8, java.sql.Timestamp.valueOf(dept.getUpdatedAt()));
            ps.setInt(9, dept.getDepartmentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDepartment(int id) {
        String updateUsersSql = "UPDATE Users SET status = 'deleted', updated_at = CURRENT_TIMESTAMP WHERE department_id = ?";
        String deleteDepartmentSql = "DELETE FROM Departments WHERE department_id = ?";

        try {
            connection.setAutoCommit(false); // Start transaction

            // Update user status
            try (PreparedStatement psUsers = connection.prepareStatement(updateUsersSql)) {
                psUsers.setInt(1, id);
                int usersAffected = psUsers.executeUpdate();
                System.out.println("✅ Updated " + usersAffected + " users to 'deleted' status for department_id: " + id);
            }

            // Delete department
            try (PreparedStatement psDept = connection.prepareStatement(deleteDepartmentSql)) {
                psDept.setInt(1, id);
                int deptAffected = psDept.executeUpdate();
                if (deptAffected == 0) {
                    throw new SQLException("No department found with department_id: " + id);
                }
                System.out.println("✅ Deleted department with department_id: " + id);
            }

            connection.commit(); // Commit transaction
            System.out.println("✅ Department and associated users deleted successfully");
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
                System.out.println("🔄 Transaction rolled back due to error: " + e.getMessage());
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error during rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            System.out.println("❌ Error in deleteDepartment: " + e.getMessage());
            throw new RuntimeException("Error deleting department and updating user statuses", e);
        } finally {
            try {
                connection.setAutoCommit(true); // Restore default auto-commit mode
            } catch (SQLException e) {
                System.out.println("❌ Error restoring auto-commit: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Department getDepartmentById(int id) {
        String sql = "SELECT * FROM Departments WHERE department_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department d = new Department();
                    d.setDepartmentId(rs.getInt("department_id"));
                    d.setDepartmentName(rs.getString("department_name"));
                    d.setDepartmentCode(rs.getString("department_code"));
                    d.setPhoneNumber(rs.getString("phone_number"));
                    d.setEmail(rs.getString("email"));
                    d.setLocation(rs.getString("location"));
                    d.setDescription(rs.getString("description"));
                    d.setStatus(Department.Status.valueOf(rs.getString("status")));
                    d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    d.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateUniqueDepartmentCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        String code;
        int maxAttempts = 100;
        int attempt = 0;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            code = sb.toString();
            attempt++;
        } while (isCodeExists(code) && attempt < maxAttempts);

        if (attempt >= maxAttempts) {
            throw new RuntimeException("Unable to generate unique department code after " + maxAttempts + " attempts");
        }

        return code;
    }

    private boolean isCodeExists(String code) {
        String sql = "SELECT COUNT(*) FROM Departments WHERE department_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi isCodeExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        DepartmentDAO dao = new DepartmentDAO();
        List<Department> list = dao.getAllDepartments();
        for (Department department : list) {
            System.out.println(department.toString());
        }
    }
}
