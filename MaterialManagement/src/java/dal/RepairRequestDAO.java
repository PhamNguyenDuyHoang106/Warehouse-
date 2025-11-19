package dal;

import entity.DBContext;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepairRequestDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(RepairRequestDAO.class.getName());

    public boolean createRepairRequest(RepairRequest request, List<RepairRequestDetail> details) throws SQLException {
        String requestSql = "INSERT INTO Repair_Requests (rr_code, request_by, material_id, batch_id, rack_id, department_id, request_date, issue_description, priority, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO Repair_Request_Details "
                + "(rr_id, spare_material_id, quantity_needed, note, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psRequest = connection.prepareStatement(requestSql, Statement.RETURN_GENERATED_KEYS); PreparedStatement psDetail = connection.prepareStatement(detailSql)) {
            // Validate input
            if (request == null || request.getRequestCode() == null || request.getUserId() <= 0) {
                throw new IllegalArgumentException("Invalid repair request data.");
            }

            // Insert into Repair_Requests
            psRequest.setString(1, request.getRequestCode());
            psRequest.setInt(2, request.getUserId());
            if (request.getMaterialId() != null) {
                psRequest.setInt(3, request.getMaterialId());
            } else {
                psRequest.setNull(3, Types.INTEGER);
            }
            if (request.getBatchId() != null) {
                psRequest.setInt(4, request.getBatchId());
            } else {
                psRequest.setNull(4, Types.INTEGER);
            }
            if (request.getRackId() != null) {
                psRequest.setInt(5, request.getRackId());
            } else {
                psRequest.setNull(5, Types.INTEGER);
            }
            if (request.getDepartmentId() != null) {
                psRequest.setInt(6, request.getDepartmentId());
            } else {
                psRequest.setNull(6, Types.INTEGER);
            }
            psRequest.setTimestamp(7, request.getRequestDate());
            psRequest.setString(8, request.getReason()); // issue_description
            psRequest.setString(9, request.getPriority() != null ? request.getPriority() : "medium");
            psRequest.setString(10, request.getStatus());
            psRequest.setTimestamp(11, request.getCreatedAt());
            psRequest.setTimestamp(12, request.getUpdatedAt());
            psRequest.executeUpdate();

            // Get generated repair request ID
            try (ResultSet rs = psRequest.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Failed to retrieve generated repair request ID.");
                }
                int repairRequestId = rs.getInt(1);

                // Insert details into Repair_Request_Details
                for (RepairRequestDetail detail : details) {
                    if (detail == null || detail.getMaterialId() == null || detail.getQuantity() == null || detail.getQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Invalid repair request detail data.");
                    }
                    psDetail.setInt(1, repairRequestId);
                    psDetail.setInt(2, detail.getMaterialId());
                    psDetail.setBigDecimal(3, detail.getQuantity());
                    psDetail.setString(4, detail.getNote() != null ? detail.getNote() : detail.getDamageDescription());
                    psDetail.setTimestamp(5, detail.getCreatedAt());
                    psDetail.setTimestamp(6, detail.getUpdatedAt());
                    psDetail.executeUpdate();
                }
            }
            return true;
        }
    }

    public List<RepairRequest> getRepairRequestsWithPagination(int offset, int pageSize, String searchKeyword, String status, String sortByName, String requestDateFrom, String requestDateTo) throws SQLException {
        List<RepairRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT rr.*, u.full_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "WHERE rr.deleted_at IS NULL AND rr.status != 'cancelled'"
        );
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND rr.issue_description LIKE ?");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND rr.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND rr.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) <= ?");
        }
        if (sortByName != null && !sortByName.isEmpty()) {
            if ("oldest".equalsIgnoreCase(sortByName)) {
                sql.append(" ORDER BY rr.created_at ASC, rr.rr_id ASC");
            } else if ("newest".equalsIgnoreCase(sortByName)) {
                sql.append(" ORDER BY rr.created_at DESC, rr.rr_id DESC");
            }
        } else {
            sql.append(" ORDER BY rr.created_at DESC, rr.rr_id DESC");
        }
        sql.append(" LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }
            if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
            } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest request = new RepairRequest();
                    request.setRepairRequestId(rs.getInt("rr_id"));
                    request.setRequestCode(rs.getString("rr_code"));
                    request.setUserId(rs.getInt("request_by"));
                    request.setFullName(rs.getString("full_name"));
                    request.setRequestDate(rs.getTimestamp("request_date"));
                    request.setStatus(rs.getString("status"));
                    request.setReason(rs.getString("issue_description"));
                    request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                    request.setApprovalReason(rs.getString("approval_reason"));
                    request.setApprovedAt(rs.getTimestamp("approved_at"));
                    request.setRejectionReason(rs.getString("rejection_reason"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setUpdatedAt(rs.getTimestamp("updated_at"));
                    request.setDeletedAt(rs.getTimestamp("deleted_at"));
                    requests.add(request);
                }
            }
        }
        return requests;
    }

    public int getTotalRepairRequestCount(String searchKeyword, String status, String requestDateFrom, String requestDateTo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) "
                + "FROM `Repair_Requests` rr "
                + "JOIN `Users` u ON rr.request_by = u.user_id "
                + "WHERE rr.deleted_at IS NULL AND rr.status != 'cancelled'"
        );
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND rr.issue_description LIKE ?");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND rr.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND rr.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(rr.request_date) <= ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }
            if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateFrom);
            } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
                ps.setString(paramIndex++, requestDateTo + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void updateStatus(int repairRequestId, String action, Integer approvedBy, String reason) throws SQLException {
        if (action == null || (!"approve".equalsIgnoreCase(action) && !"reject".equalsIgnoreCase(action))) {
            throw new IllegalArgumentException("Hành động không hợp lệ: " + action);
        }
        if (approvedBy == null || approvedBy <= 0) {
            throw new IllegalArgumentException("ID người duyệt không hợp lệ.");
        }

        String checkSql = "SELECT status FROM Repair_Requests WHERE rr_id = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, repairRequestId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    String currentStatus = rs.getString("status");
                    if (!"pending".equalsIgnoreCase(currentStatus)) {
                        throw new IllegalStateException("Yêu cầu đã được xử lý. Không thể duyệt hoặc từ chối thêm lần nữa.");
                    }
                } else {
                    throw new SQLException("Không tìm thấy yêu cầu sửa chữa với ID: " + repairRequestId);
                }
            }
        }

        String sql;
        String status;
        if ("approve".equalsIgnoreCase(action)) {
            status = "approved";
            sql = "UPDATE Repair_Requests SET status = ?, approved_by = ?, approved_at = CURRENT_TIMESTAMP, approval_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE rr_id = ?";
        } else {
            status = "rejected";
            sql = "UPDATE Repair_Requests SET status = ?, approved_by = ?, approved_at = CURRENT_TIMESTAMP, rejection_reason = ?, updated_at = CURRENT_TIMESTAMP WHERE rr_id = ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approvedBy);
            ps.setString(3, reason);
            ps.setInt(4, repairRequestId);
            ps.executeUpdate();
        }
    }

    public List<RepairRequest> searchByReason(String reason) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "WHERE rr.deleted_at IS NULL AND rr.status != 'cancelled' AND rr.issue_description LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + (reason == null ? "" : reason) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("rr_id"));
                    req.setRequestCode(rs.getString("rr_code"));
                    req.setUserId(rs.getInt("request_by"));
                    req.setFullName(rs.getString("full_name"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setReason(rs.getString("issue_description"));
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public List<RepairRequest> filterByStatus(String status) throws SQLException {
        List<RepairRequest> list = new ArrayList<>();
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "WHERE rr.deleted_at IS NULL AND rr.status != 'cancelled' AND rr.status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequest req = new RepairRequest();
                    req.setRepairRequestId(rs.getInt("rr_id"));
                    req.setRequestCode(rs.getString("rr_code"));
                    req.setUserId(rs.getInt("request_by"));
                    req.setFullName(rs.getString("full_name"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setReason(rs.getString("issue_description"));
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        }
        return list;
    }

    public RepairRequest getRepairRequestById(int repairRequestId) throws SQLException {
        String sql = "SELECT rr.*, u.full_name FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "WHERE rr.rr_id = ? AND rr.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RepairRequest request = new RepairRequest();
                    request.setRepairRequestId(rs.getInt("rr_id"));
                    request.setRequestCode(rs.getString("rr_code"));
                    request.setUserId(rs.getInt("request_by"));
                    request.setFullName(rs.getString("full_name"));
                    request.setRequestDate(rs.getTimestamp("request_date"));
                    request.setStatus(rs.getString("status"));
                    request.setReason(rs.getString("issue_description"));
                    request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                    request.setApprovalReason(rs.getString("approval_reason"));
                    request.setApprovedAt(rs.getTimestamp("approved_at"));
                    request.setRejectionReason(rs.getString("rejection_reason"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setUpdatedAt(rs.getTimestamp("updated_at"));
                    request.setDeletedAt(rs.getTimestamp("deleted_at"));
                    return request;
                }
            }
        }
        return null;
    }
    public static void main(String[] args) {
    RepairRequestDAO dao = new RepairRequestDAO();
    try {
        // Example: Get repair requests with pagination
        List<RepairRequest> requests = dao.getRepairRequestsWithPagination(0, 5, "equipment", "pending", "asc", "2025-01-01", "2025-12-31");
        for (RepairRequest req : requests) {
            LOGGER.log(Level.INFO, "Request ID: " + req.getRepairRequestId() + ", Code: " + req.getRequestCode() + ", Status: " + req.getStatus());
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error in main method: ", e);
    }
}

    public String generateNextRequestCode() {
        String nextCode = "RR1";
        try {
            String sql = "SELECT rr_code FROM Repair_Requests WHERE deleted_at IS NULL ORDER BY rr_code";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<String> existingCodes = new ArrayList<>();
            while (rs.next()) {
                String code = rs.getString("rr_code");
                if (code != null && code.startsWith("RR")) {
                    existingCodes.add(code);
                }
            }
            
            if (!existingCodes.isEmpty()) {
                // Sort codes numerically
                existingCodes.sort((a, b) -> {
                    try {
                        int numA = Integer.parseInt(a.substring(2));
                        int numB = Integer.parseInt(b.substring(2));
                        return Integer.compare(numA, numB);
                    } catch (NumberFormatException e) {
                        return a.compareTo(b);
                    }
                });
                
                // Get the highest number and increment
                String lastCode = existingCodes.get(existingCodes.size() - 1);
                int lastNumber = Integer.parseInt(lastCode.substring(2));
                nextCode = "RR" + (lastNumber + 1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating next request code: " + e.getMessage(), e);
        }
        return nextCode;
    }
}
