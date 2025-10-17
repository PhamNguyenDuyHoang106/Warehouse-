package dal;

import entity.DBContext;
import entity.PasswordResetRequests;
import java.sql.*;
import java.util.*;

public class PasswordResetRequestsDAO extends DBContext {
    public List<PasswordResetRequests> getAllRequests() {
        List<PasswordResetRequests> list = new ArrayList<>();
        String sql = "SELECT * FROM Password_Reset_Requests ORDER BY request_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PasswordResetRequests req = new PasswordResetRequests(
                    rs.getInt("request_id"),
                    rs.getString("user_email"),
                    rs.getTimestamp("request_date"),
                    rs.getString("status"),
                    rs.getString("new_password"),
                    (Integer)rs.getObject("processed_by"),
                    rs.getTimestamp("processed_date")
                );
                list.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertRequest(String email) {
        String sql = "INSERT INTO Password_Reset_Requests (user_email) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int requestId, String status, String newPassword, int adminId) {
        String sql = "UPDATE Password_Reset_Requests SET status=?, new_password=?, processed_by=?, processed_date=NOW() WHERE request_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, newPassword);
            ps.setInt(3, adminId);
            ps.setInt(4, requestId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PasswordResetRequests> getRequestsByFilter(String email) {
        List<PasswordResetRequests> list = new ArrayList<>();
        String sql = "SELECT * FROM Password_Reset_Requests WHERE user_email LIKE ? ORDER BY status, request_id DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + email + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PasswordResetRequests req = new PasswordResetRequests(
                        rs.getInt("request_id"),
                        rs.getString("user_email"),
                        rs.getTimestamp("request_date"),
                        rs.getString("status"),
                        rs.getString("new_password"),
                        (Integer)rs.getObject("processed_by"),
                        rs.getTimestamp("processed_date")
                    );
                    list.add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PasswordResetRequests> getRequestsByFilterPaging(String email, String status, int page, int pageSize) {
        List<PasswordResetRequests> list = new ArrayList<>();
        String sql = "SELECT * FROM Password_Reset_Requests WHERE user_email LIKE ?";
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql += " AND status = ?";
        }
        sql += " ORDER BY request_id DESC LIMIT ? OFFSET ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, "%" + email + "%");
            if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(idx++, status);
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PasswordResetRequests req = new PasswordResetRequests(
                        rs.getInt("request_id"),
                        rs.getString("user_email"),
                        rs.getTimestamp("request_date"),
                        rs.getString("status"),
                        rs.getString("new_password"),
                        (Integer)rs.getObject("processed_by"),
                        rs.getTimestamp("processed_date")
                    );
                    list.add(req);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countRequestsByFilter(String email, String status) {
        String sql = "SELECT COUNT(*) FROM Password_Reset_Requests WHERE user_email LIKE ?";
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql += " AND status = ?";
        }
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, "%" + email + "%");
            if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
                ps.setString(idx++, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
