package dal;

import entity.DBContext;
import entity.Department;
import entity.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.util.UUID;
import utils.EmailUtils;

public class UserDAO extends DBContext {

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT u.*, r.role_name, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.username = ? AND u.password = ? AND u.verification_status = 'verified' AND u.status != 'deleted'";

        String md5Password = md5(password);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, md5Password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDepartmentId(rs.getObject("department_id") != null ? rs.getInt("department_id") : null);
                user.setDepartmentName(rs.getString("department_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                user.setVerificationToken(rs.getString("verification_token"));
                user.setVerificationStatus(rs.getString("verification_status"));
                user.setVerificationExpiry(rs.getTimestamp("verification_expiry") != null ? rs.getTimestamp("verification_expiry").toLocalDateTime() : null);
                System.out.println("✅ Đăng nhập thành công: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Sai thông tin đăng nhập, tài khoản chưa được xác thực hoặc bị xóa.");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.status != 'deleted'";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDepartmentId(rs.getObject("department_id") != null ? rs.getInt("department_id") : null);
                user.setDepartmentName(rs.getString("department_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                userList.add(user);
            }
            System.out.println("✅ Lấy danh sách user thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.role_name, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.user_id = ? AND u.status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDepartmentId(rs.getObject("department_id") != null ? rs.getInt("department_id") : null);
                user.setDepartmentName(rs.getString("department_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                System.out.println("✅ Lấy được user: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Không tìm thấy user với user_id: " + userId);
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUserById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET password = ?, full_name = ?, email = ?, phone_number = ?, address = ?, user_picture = ?, date_of_birth = ?, gender = ?, status = ?, department_id = ? WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println("🔄 Cập nhật user với user_id = " + user.getUserId());
            System.out.println("full_name = " + user.getFullName());
            System.out.println("email = " + user.getEmail());
            System.out.println("phone_number = " + user.getPhoneNumber());
            System.out.println("address = " + user.getAddress());
            System.out.println("user_picture = " + user.getUserPicture());
            System.out.println("date_of_birth = " + (user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "null"));
            System.out.println("gender = " + (user.getGender() != null ? user.getGender().toString() : "null"));
            System.out.println("status = " + (user.getStatus() != null ? user.getStatus().toString() : "null"));
            System.out.println("department_id = " + user.getDepartmentId());

            ps.setString(1, user.getPassword());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getUserPicture());
            ps.setObject(7, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(8, user.getGender() != null ? user.getGender().toString() : null);
            ps.setString(9, user.getStatus() != null ? user.getStatus().toString() : null);
            ps.setObject(10, user.getDepartmentId());
            ps.setInt(11, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected + " for user_id: " + user.getUserId());
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật thông tin user thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + user.getUserId());
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (username, password, full_name, email, phone_number, address, user_picture, role_id, department_id, date_of_birth, gender, status, verification_token, verification_status, verification_expiry) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getUserPicture());
            ps.setInt(8, user.getRoleId());
            ps.setObject(9, user.getDepartmentId());
            ps.setObject(10, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(11, user.getGender() != null ? user.getGender().name() : null);
            ps.setString(12, user.getStatus() != null ? user.getStatus().name() : "pending");
            ps.setString(13, user.getVerificationToken());
            ps.setString(14, user.getVerificationStatus() != null ? user.getVerificationStatus() : "pending");
            ps.setObject(15, user.getVerificationExpiry() != null ? java.sql.Timestamp.valueOf(user.getVerificationExpiry()) : null);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Tạo user thành công: " + user.getUsername());
                return true;
            } else {
                System.out.println("❌ Không thể tạo user: Không có dòng nào được chèn");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyUser(String token) {
        String sql = "UPDATE Users SET verification_status = 'verified', status = 'active', created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE verification_token = ? AND verification_status = 'pending' AND verification_expiry > CURRENT_TIMESTAMP";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ User verified successfully with token: " + token);
                return true;
            } else {
                System.out.println("❌ Invalid or expired token: " + token);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error verifying user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUserById(int id) {
        String sql = "UPDATE Users SET status = 'deleted', updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Xóa mềm user thành công với user_id: " + id);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để xóa với user_id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi deleteUserById: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi kiểm tra username tồn tại: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND status != 'deleted' AND verification_status = 'verified'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("❌ Error checking if email exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePasswordByEmail(String email, String md5Password) {
        String sql = "UPDATE Users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE email = ? AND status != 'deleted' AND verification_status = 'verified'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, md5Password);
            ps.setString(2, email);
            int rows = ps.executeUpdate();
            System.out.println("Updated password for email " + email + ": " + rows + " row(s) affected");
            return rows > 0;
        } catch (Exception e) {
            System.out.println("❌ Error updating password by email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExist(String email, int excludeUserId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND status != 'deleted' AND verification_status = 'verified' AND user_id != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println(exists ? "❌ Email already exists: " + email : "✅ Email available: " + email);
                return exists;
            }
        } catch (Exception e) {
            System.out.println("❌ Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int userId, User.Status status) {
        String sql = "UPDATE Users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật status thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int userId, int roleId) {
        // Check if the role is disabled
        String checkSql = "SELECT disable FROM Roles WHERE role_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, roleId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getBoolean("disable")) {
                System.out.println("❌ Role bị vô hiệu hóa: role_id = " + roleId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi kiểm tra role: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE Users SET role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật role thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateRole: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDepartment(int userId, Integer departmentId) {
        String sql = "UPDATE Users SET department_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status != 'deleted'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, departmentId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Cập nhật department thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("❌ Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi updateDepartment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getUsersByPageAndFilter(int page, int pageSize, String username, String status, Integer roleId, Integer departmentId) {
        List<User> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT u.*, r.role_name, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.status != 'deleted' ");

        List<Object> params = new ArrayList<>();

        // Thêm điều kiện lọc user chỉ thuộc phòng ban active hoặc không có phòng ban
        sql.append("AND (d.status = 'active' OR u.department_id IS NULL) ");

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND u.username LIKE ? ");
            params.add("%" + username.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("deleted")) {
            sql.append("AND u.status = ? ");
            params.add(status.trim());
        }

        if (roleId != null) {
            sql.append("AND u.role_id = ? ");
            params.add(roleId);
        }

        if (departmentId != null) {
            sql.append("AND u.department_id = ? ");
            params.add(departmentId);
        }

        sql.append("ORDER BY u.user_id LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDepartmentId(rs.getObject("department_id") != null ? rs.getInt("department_id") : null);
                user.setDepartmentName(rs.getString("department_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                userList.add(user);
            }
            System.out.println("✅ Lấy danh sách user phân trang thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUsersByPageAndFilter: " + e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }

    public int getUserCountByFilter(String username, String status, Integer roleId, Integer departmentId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE status != 'deleted' ");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append("AND username LIKE ? ");
            params.add("%" + username.trim() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("deleted")) {
            sql.append("AND status = ? ");
            params.add(status.trim());
        }

        if (roleId != null) {
            sql.append("AND role_id = ? ");
            params.add(roleId);
        }

        if (departmentId != null) {
            sql.append("AND department_id = ? ");
            params.add(departmentId);
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUserCountByFilter: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<User> getUsersByRoleId(int roleId) {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.role_id "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.role_id = ? AND u.status != 'deleted'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setUserPicture(rs.getString("user_picture"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setDepartmentId(rs.getObject("department_id") != null ? rs.getInt("department_id") : null);
                user.setDepartmentName(rs.getString("department_name"));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setGender(rs.getString("gender") != null ? User.Gender.valueOf(rs.getString("gender")) : null);
                user.setStatus(rs.getString("status") != null ? User.Status.valueOf(rs.getString("status")) : null);
                user.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                userList.add(user);
            }
            System.out.println("✅ Lấy danh sách user theo role_id " + roleId + " thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getUsersByRoleId: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public String getAdminEmail() {
        String sql = "SELECT email FROM Users WHERE role_id = 1 LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Department> getActiveDepartments() {
        List<Department> departmentList = new ArrayList<>();
        String sql = "SELECT * FROM Departments WHERE status = 'active' ORDER BY department_id";

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
            System.out.println("✅ Lấy danh sách phòng ban active thành công, số lượng: " + departmentList.size());
        } catch (Exception e) {
            System.out.println("❌ Lỗi getActiveDepartments: " + e.getMessage());
            e.printStackTrace();
        }
        return departmentList;
    }
}