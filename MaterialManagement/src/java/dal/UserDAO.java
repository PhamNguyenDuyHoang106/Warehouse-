package dal;

import entity.DBContext;
import entity.Department;
import entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.EmailUtils;
import utils.PasswordHasher;

/**
 * Data Access Object for User entity.
 * Handles all database operations related to users.
 * 
 * @author MaterialManagement Team
 */
public class UserDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    /**
     * Helper method to get connection safely.
     * All DAO methods should use this instead of accessing connection directly.
     * 
     * @return Connection object
     * @throws RuntimeException if connection cannot be established
     */
    private Connection getConnectionSafely() {
        Connection conn = getConnection();
        if (conn == null) {
            throw new RuntimeException("Database connection is null. Cannot perform operation.");
        }
        return conn;
    }

    /**
     * Authenticate user by username and password.
     * Supports both MD5 (legacy) and BCrypt (new) password hashes.
     * Automatically migrates MD5 passwords to BCrypt on successful login.
     * 
     * @param username The username
     * @param password The plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        String sql = "SELECT u.*, r.role_name, d.department_name " +
            "FROM Users u " +
            "LEFT JOIN Roles r ON u.role_id = r.role_id " +
            "LEFT JOIN Departments d ON u.department_id = d.department_id " +
            "WHERE u.username = ? AND u.deleted_at IS NULL";

        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);

                    if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                        recordFailedLogin(user.getUserId(), user.getStatus());
                        LOGGER.log(Level.WARNING, "Login failed for username: {0} - Invalid password", username);
                        return null;
                    }

                    // Migrate password from MD5 to BCrypt if needed
                    String newHash = PasswordHasher.migratePasswordIfNeeded(password, user.getPasswordHash());
                    if (newHash != null) {
                        updatePasswordHash(user.getUserId(), newHash);
                        user.setPasswordHash(newHash);
                        LOGGER.log(Level.INFO, "Migrated password from MD5 to BCrypt for user: {0}", username);
                    }

                    if (user.getStatus() == User.Status.locked) {
                        LOGGER.log(Level.WARNING, "Login blocked for username: {0} - account locked", username);
                        return user;
                    }

                    if (user.getStatus() == User.Status.inactive) {
                        LOGGER.log(Level.WARNING, "Login blocked for username: {0} - account inactive", username);
                        return user;
                    }

                    recordSuccessfulLogin(user.getUserId());
                    user.setLastLogin(LocalDateTime.now());
                    user.setFailedLoginAttempts(0);

                    LOGGER.log(Level.INFO, "User logged in successfully: {0}", username);
                    return user;
                } else {
                    LOGGER.log(Level.WARNING, "Login failed for username: {0} - User not found or deactivated", username);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during login for username: " + username, e);
        }
        return null;
    }
    
    /**
     * Update password hash for a user (used during password migration).
     */
    private void updatePasswordHash(int userId, String newHash) {
        String sql = "UPDATE Users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to update password hash for user ID: " + userId, e);
        }
    }
    
    /**
     * Map ResultSet to User object (reduces code duplication).
     * 
     * @param rs The ResultSet positioned at the user record
     * @return User object with mapped fields
     * @throws SQLException If mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setAvatar(rs.getString("avatar"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        
        Integer deptId = rs.getObject("department_id") != null ? rs.getInt("department_id") : null;
        user.setDepartmentId(deptId);
        user.setDepartmentName(rs.getString("department_name"));
        
        if (rs.getDate("date_of_birth") != null) {
            user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        }
        
        String genderStr = rs.getString("gender");
        if (genderStr != null) {
            try {
                user.setGender(User.Gender.valueOf(genderStr));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid gender value: " + genderStr, e);
            }
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                user.setStatus(User.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Invalid status value: " + statusStr, e);
            }
        }
        
        user.setMfaEnabled(rs.getBoolean("mfa_enabled"));
        user.setMfaSecret(rs.getString("mfa_secret"));
        user.setMfaRecoveryCodes(rs.getString("mfa_recovery_codes"));
        if (rs.getTimestamp("last_login") != null) {
            user.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
        }
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        if (rs.getDate("password_expiry") != null) {
            user.setPasswordExpiry(rs.getDate("password_expiry").toLocalDate());
        }
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        user.setCreatedBy((Integer) rs.getObject("created_by"));
        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        user.setUpdatedBy((Integer) rs.getObject("updated_by"));
        if (rs.getTimestamp("deleted_at") != null) {
            user.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
        }
        
        return user;
    }

    /**
     * Get all users from database.
     * 
     * @return List of all active users
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.department_name " +
            "FROM Users u " +
            "LEFT JOIN Roles r ON u.role_id = r.role_id " +
            "LEFT JOIN Departments d ON u.department_id = d.department_id " +
            "WHERE u.deleted_at IS NULL";

        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
            LOGGER.log(Level.INFO, "Retrieved {0} users successfully", userList.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
        }
        return userList;
    }

    /**
     * Get user by ID.
     * 
     * @param userId The user ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT u.*, r.role_name, d.department_name " +
            "FROM Users u " +
            "LEFT JOIN Roles r ON u.role_id = r.role_id " +
            "LEFT JOIN Departments d ON u.department_id = d.department_id " +
            "WHERE u.user_id = ? AND u.deleted_at IS NULL";
        
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    LOGGER.log(Level.INFO, "Retrieved user: {0}", user.getUsername());
                    return user;
                } else {
                    LOGGER.log(Level.WARNING, "User not found with ID: {0}", userId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by ID: " + userId, e);
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET " +
            "password_hash = ?, " +
            "full_name = ?, " +
            "email = ?, " +
            "phone = ?, " +
            "avatar = ?, " +
            "date_of_birth = ?, " +
            "gender = ?, " +
            "status = ?, " +
            "role_id = ?, " +
            "mfa_enabled = ?, " +
            "mfa_secret = ?, " +
            "mfa_recovery_codes = ?, " +
            "failed_login_attempts = ?, " +
            "password_expiry = ?, " +
            "updated_by = ?, " +
            "updated_at = CURRENT_TIMESTAMP, " +
            "department_id = ? " +
            "WHERE user_id = ? " +
            "AND deleted_at IS NULL";
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            System.out.println("[UPDATE] Cap nhat user voi user_id = " + user.getUserId());
            System.out.println("full_name = " + user.getFullName());
            System.out.println("email = " + user.getEmail());
            System.out.println("phone = " + user.getPhone());
            System.out.println("avatar = " + user.getAvatar());
            System.out.println("date_of_birth = " + (user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "null"));
            System.out.println("gender = " + (user.getGender() != null ? user.getGender().toString() : "null"));
            System.out.println("status = " + (user.getStatus() != null ? user.getStatus().toString() : "null"));
            System.out.println("department_id = " + user.getDepartmentId());

            ps.setString(1, user.getPasswordHash());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAvatar());
            ps.setObject(6, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(7, user.getGender() != null ? user.getGender().toString() : null);
            ps.setString(8, user.getStatus() != null ? user.getStatus().toString() : null);
            ps.setInt(9, user.getRoleId());
            ps.setBoolean(10, user.isMfaEnabled());
            ps.setString(11, user.getMfaSecret());
            ps.setString(12, user.getMfaRecoveryCodes());
            ps.setInt(13, user.getFailedLoginAttempts());
            ps.setObject(14, user.getPasswordExpiry() != null ? java.sql.Date.valueOf(user.getPasswordExpiry()) : null);
            if (user.getUpdatedBy() != null) {
                ps.setInt(15, user.getUpdatedBy());
            } else {
                ps.setNull(15, java.sql.Types.INTEGER);
            }
            ps.setObject(16, user.getDepartmentId());
            ps.setInt(17, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected + " for user_id: " + user.getUserId());
            if (rowsAffected > 0) {
                System.out.println("[OK] Cap nhat thong tin user thanh cong: " + user.getUsername());
                return true;
            } else {
                System.out.println("X Khong tim thay user de cap nhat voi user_id: " + user.getUserId());
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new user.
     * If password is plain text, it will be hashed using BCrypt.
     * 
     * @param user The user object (password should be plain text or already hashed)
     * @return true if user created successfully
     */
    public boolean createUser(User user) {
        String sql = 
            "INSERT INTO Users (" +
                "username, " +
                "password_hash, " +
                "full_name, " +
                "email, " +
                "phone, " +
                "avatar, " +
                "role_id, " +
                "department_id, " +
                "date_of_birth, " +
                "gender, " +
                "status, " +
                "mfa_enabled, " +
                "mfa_secret, " +
                "mfa_recovery_codes, " +
                "last_login, " +
                "failed_login_attempts, " +
                "password_expiry, " +
                "created_by, " +
                "updated_by " +
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            
            // Hash password if it's plain text (not already hashed)
            String passwordToStore = user.getPassword();
            if (passwordToStore != null && !PasswordHasher.isMD5Hash(passwordToStore) 
                && !passwordToStore.startsWith("$2a$") && !passwordToStore.startsWith("$2b$")) {
                // Plain text password - hash it
                passwordToStore = PasswordHasher.hashPassword(passwordToStore);
                LOGGER.log(Level.INFO, "Hashed password for new user: {0}", user.getUsername());
            }
            ps.setString(2, passwordToStore);
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAvatar());
            ps.setInt(7, user.getRoleId());
            ps.setObject(8, user.getDepartmentId());
            ps.setObject(9, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(10, user.getGender() != null ? user.getGender().name() : null);
            ps.setString(11, user.getStatus() != null ? user.getStatus().name() : User.Status.active.name());
            ps.setBoolean(12, user.isMfaEnabled());
            ps.setString(13, user.getMfaSecret());
            ps.setString(14, user.getMfaRecoveryCodes());
            ps.setObject(15, user.getLastLogin() != null ? java.sql.Timestamp.valueOf(user.getLastLogin()) : null);
            ps.setInt(16, user.getFailedLoginAttempts());
            ps.setObject(17, user.getPasswordExpiry() != null ? java.sql.Date.valueOf(user.getPasswordExpiry()) : null);
            if (user.getCreatedBy() != null) {
                ps.setInt(18, user.getCreatedBy());
            } else {
                ps.setNull(18, java.sql.Types.INTEGER);
            }
            if (user.getUpdatedBy() != null) {
                ps.setInt(19, user.getUpdatedBy());
            } else {
                ps.setNull(19, java.sql.Types.INTEGER);
            }

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "User created successfully: {0}", user.getUsername());
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to create user: No rows affected");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user: " + user.getUsername(), e);
            return false;
        }
    }

    public boolean deleteUserById(int id) {
        String sql = "UPDATE Users SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[OK] Đánh dấu xóa user thành công với user_id: " + id);
                return true;
            } else {
                System.out.println("[ERROR] Không tìm thấy user còn hoạt động để xóa với user_id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi deleteUserById: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi kiểm tra username tồn tại: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Error checking if email exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update password by email.
     * Password will be hashed using BCrypt before storing.
     * 
     * @param email The user's email
     * @param plainPassword The plain text password (will be hashed)
     * @return true if password updated successfully
     */
    public boolean updatePasswordByEmail(String email, String plainPassword) {
        String sql = 
            "UPDATE Users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP " +
            "WHERE email = ? AND deleted_at IS NULL";
        
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            // Hash the password before storing
            String hashedPassword = PasswordHasher.hashPassword(plainPassword);
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                LOGGER.log(Level.INFO, "Password updated for email: {0}", email);
            } else {
                LOGGER.log(Level.WARNING, "No user found with email: {0} (or not verified)", email);
            }
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password by email: " + email, e);
            return false;
        }
    }

    public boolean isEmailExist(String email, int excludeUserId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND deleted_at IS NULL AND user_id != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println(exists ? "[ERROR] Email already exists: " + email : "[OK] Email available: " + email);
                return exists;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int userId, User.Status status) {
        String sql = "UPDATE Users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[OK] Cập nhật status thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("[ERROR] Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi updateStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int userId, int roleId) {
        // Check if the role is disabled
        String checkSql = "SELECT status FROM Roles WHERE role_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, roleId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt("status") == 0) {
                System.out.println("[ERROR] Role đang bị vô hiệu hóa: role_id = " + roleId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi kiểm tra role: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE Users SET role_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[OK] Cập nhật role thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("[ERROR] Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi updateRole: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDepartment(int userId, Integer departmentId) {
        String sql = "UPDATE Users SET department_id = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, departmentId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("[OK] Cập nhật department thành công cho user_id: " + userId);
                return true;
            } else {
                System.out.println("[ERROR] Không tìm thấy user để cập nhật với user_id: " + userId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi updateDepartment: " + e.getMessage());
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
                + "WHERE u.deleted_at IS NULL ");

        List<Object> params = new ArrayList<>();

        // Them dieu kien loc user chi thuoc phong ban active hoac khong co phong ban
        sql.append("AND (d.deleted_at IS NULL OR u.department_id IS NULL) ");

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
                userList.add(mapResultSetToUser(rs));
            }
            System.out.println("[OK] Lấy danh sách user phân trang thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi getUsersByPageAndFilter: " + e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }

    public int getUserCountByFilter(String username, String status, Integer roleId, Integer departmentId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Users WHERE deleted_at IS NULL ");
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
            System.out.println("[ERROR] Lỗi getUserCountByFilter: " + e.getMessage());
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
                + "WHERE u.role_id = ? AND u.deleted_at IS NULL";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }
            System.out.println("[OK] Lấy danh sách user theo role_id " + roleId + " thành công, số lượng: " + userList.size());
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi getUsersByRoleId: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public String getAdminEmail() {
        String sql = "SELECT email FROM Users WHERE role_id = 1 AND deleted_at IS NULL LIMIT 1";
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
        String sql = "SELECT * FROM Departments WHERE status = 'active' AND deleted_at IS NULL ORDER BY department_id";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                dept.setDepartmentCode(rs.getString("department_code"));
                dept.setPhoneNumber(rs.getString("phone"));
                dept.setEmail(rs.getString("email"));
                dept.setLocation(rs.getString("location"));
                // Schema v12: Departments table doesn't have 'description' column
                // dept.setDescription(rs.getString("description"));
                dept.setStatus(rs.getString("status") != null ? Department.Status.valueOf(rs.getString("status")) : null);
                dept.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                // Schema v12: Departments table doesn't have 'updated_at' column
                // dept.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                departmentList.add(dept);
            }
            System.out.println("[OK] Lấy danh sách phòng ban active thành công, số lượng: " + departmentList.size());
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi getActiveDepartments: " + e.getMessage());
            e.printStackTrace();
        }
        return departmentList;
    }

    private void recordSuccessfulLogin(int userId) {
        String sql = "UPDATE Users SET last_login = CURRENT_TIMESTAMP, failed_login_attempts = 0 WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to record successful login for user ID: " + userId, e);
        }
    }

    private void recordFailedLogin(int userId, User.Status currentStatus) {
        if (currentStatus == User.Status.locked) {
            return;
        }
        String sql = 
            "UPDATE Users " +
               "SET failed_login_attempts = failed_login_attempts + 1, " +
                   "status = CASE WHEN failed_login_attempts + 1 >= ? THEN 'locked' ELSE status END, " +
                   "updated_at = CURRENT_TIMESTAMP " +
             "WHERE user_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = getConnectionSafely().prepareStatement(sql)) {
            ps.setInt(1, MAX_FAILED_ATTEMPTS);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to record failed login for user ID: " + userId, e);
        }
    }
}