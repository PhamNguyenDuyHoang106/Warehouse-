package dal;

import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.DBContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseRequestDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(PurchaseRequestDAO.class.getName());

    private static final String BASE_SELECT =
            "SELECT pr.pr_id, " +
            "       pr.pr_code, " +
            "       pr.request_by, " +
            "       pr.department_id, " +
            "       pr.request_date, " +
            "       pr.expected_date, " +
            "       pr.total_amount, " +
            "       pr.status, " +
            "       pr.approved_by, " +
            "       pr.approved_at, " +
            "       pr.reason, " +
            "       pr.created_at, " +
            "       pr.updated_at, " +
            "       pr.deleted_at, " +
            "       u.full_name AS requester_name, " +
            "       d.department_name " +
            "  FROM Purchase_Requests pr " +
            "  LEFT JOIN Users u ON pr.request_by = u.user_id " +
            "  LEFT JOIN Departments d ON pr.department_id = d.department_id ";

    private PurchaseRequest mapRequest(ResultSet rs) throws SQLException {
        PurchaseRequest pr = new PurchaseRequest();
        pr.setId(rs.getInt("pr_id"));
        pr.setCode(rs.getString("pr_code"));
        pr.setRequestBy(rs.getInt("request_by"));
        pr.setDepartmentId((Integer) rs.getObject("department_id"));
        Date requestDate = rs.getDate("request_date");
        pr.setRequestDate(requestDate);
        pr.setExpectedDate(rs.getDate("expected_date"));
        pr.setTotalAmount(rs.getBigDecimal("total_amount"));
        pr.setStatus(rs.getString("status"));
        pr.setApprovedBy((Integer) rs.getObject("approved_by"));
        Timestamp approvedAt = rs.getTimestamp("approved_at");
        pr.setApprovedAt(approvedAt);
        pr.setReason(rs.getString("reason"));
        pr.setCreatedAt(rs.getTimestamp("created_at"));
        pr.setUpdatedAt(rs.getTimestamp("updated_at"));
        pr.setDeletedAt(rs.getTimestamp("deleted_at"));
        pr.setRequesterName(rs.getString("requester_name"));
        pr.setDepartmentName(rs.getString("department_name"));
        return pr;
    }

    public PurchaseRequest getPurchaseRequestById(int id) {
        String sql = BASE_SELECT + " WHERE pr.deleted_at IS NULL AND pr.pr_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseRequest request = mapRequest(rs);
                    PurchaseRequestDetailDAO detailDAO = new PurchaseRequestDetailDAO();
                    request.setDetails(detailDAO.paginationOfDetails(id, 1, Integer.MAX_VALUE));
                    return request;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting purchase request by ID: " + id, ex);
        }
        return null;
    }

    public boolean updatePurchaseRequestStatus(int requestId, String newStatus, Integer approverId, String decisionNote) {
        if (newStatus == null) {
            return false;
        }
        String normalizedStatus = newStatus.trim().toLowerCase();
        if (!("approved".equals(normalizedStatus) || "rejected".equals(normalizedStatus) || "cancelled".equals(normalizedStatus))) {
            LOGGER.log(Level.WARNING, "Unsupported purchase request status: {0}", normalizedStatus);
            return false;
        }

        String sql = "UPDATE Purchase_Requests " +
                     "   SET status = ?, " +
                     "       approved_by = ?, " +
                     "       approved_at = CASE WHEN ? THEN CURRENT_TIMESTAMP ELSE approved_at END, " +
                     "       updated_at = CURRENT_TIMESTAMP " +
                     " WHERE pr_id = ? AND deleted_at IS NULL";

        boolean shouldUpdateApprovedAt = "approved".equals(normalizedStatus) || "rejected".equals(normalizedStatus);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, normalizedStatus);
            if (approverId != null) {
                ps.setInt(2, approverId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setBoolean(3, shouldUpdateApprovedAt);
            ps.setInt(4, requestId);
            int rows = ps.executeUpdate();

            if (rows > 0 && decisionNote != null && !decisionNote.trim().isEmpty()) {
                appendDecisionNote(requestId, decisionNote.trim());
            }
            return rows > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating purchase request status for ID: " + requestId, ex);
            return false;
        }
    }

    private void appendDecisionNote(int requestId, String note) {
        String sql = "UPDATE Purchase_Requests " +
                     "   SET reason = CASE " +
                     "                    WHEN reason IS NULL OR reason = '' THEN ? " +
                     "                    ELSE CONCAT(reason, '\n--- Decision Note ---\n', ?) " +
                     "                END, " +
                     "       updated_at = CURRENT_TIMESTAMP " +
                     " WHERE pr_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, note);
            ps.setString(2, note);
            ps.setInt(3, requestId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to append decision note for PR " + requestId, ex);
        }
    }

    public boolean createPurchaseRequestWithDetails(PurchaseRequest request, List<PurchaseRequestDetail> details) {
        String insertRequestSQL = "INSERT INTO Purchase_Requests (" +
                "pr_code, request_by, department_id, request_date, expected_date, total_amount, status, reason" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String insertDetailSQL = "INSERT INTO Purchase_Request_Details (" +
                "pr_id, material_id, unit_id, quantity, unit_price_est, note" +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        // Retry logic to handle race conditions in code generation
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                connection.setAutoCommit(false);
                
                // Generate code within transaction to prevent race conditions
                String requestCode = generateNextRequestCode();
                request.setCode(requestCode);

                int generatedId;
                try (PreparedStatement psRequest = connection.prepareStatement(insertRequestSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psRequest.setString(1, request.getCode());
                    psRequest.setInt(2, request.getRequestBy());
                    // department_id is NOT NULL in schema V12
                    if (request.getDepartmentId() != null) {
                        psRequest.setInt(3, request.getDepartmentId());
                    } else {
                        LOGGER.log(Level.SEVERE, "Department ID is required but was null for Purchase Request: " + request.getCode());
                        throw new SQLException("Department ID is required for Purchase Request");
                    }
                    Date requestDate = request.getRequestDate() != null ? request.getRequestDate() : new Date(System.currentTimeMillis());
                    psRequest.setDate(4, requestDate);
                    if (request.getExpectedDate() != null) {
                        psRequest.setDate(5, request.getExpectedDate());
                    } else {
                        psRequest.setNull(5, Types.DATE);
                    }
                    BigDecimal totalAmount = request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO;
                    psRequest.setBigDecimal(6, totalAmount);
                    psRequest.setString(7, request.getStatus() != null ? request.getStatus() : "submitted");
                    // reason is TEXT and can be NULL in schema V12
                    if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
                        psRequest.setString(8, request.getReason().trim());
                    } else {
                        psRequest.setNull(8, Types.VARCHAR);
                    }

                    int affectedRows = psRequest.executeUpdate();
                    if (affectedRows == 0) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            connection.rollback();
                            return false;
                        }
                        generatedId = generatedKeys.getInt(1);
                    }
                }

                if (details != null && !details.isEmpty()) {
                    try (PreparedStatement psDetail = connection.prepareStatement(insertDetailSQL)) {
                        for (PurchaseRequestDetail detail : details) {
                            psDetail.setInt(1, generatedId);
                            psDetail.setInt(2, detail.getMaterialId());
                            if (detail.getUnitId() != null) {
                                psDetail.setInt(3, detail.getUnitId());
                            } else {
                                psDetail.setNull(3, Types.INTEGER);
                            }
                            psDetail.setBigDecimal(4, detail.getQuantity());
                            psDetail.setBigDecimal(5, detail.getUnitPriceEstimate());
                            // total_est is a GENERATED COLUMN, don't insert value
                            if (detail.getNote() != null && !detail.getNote().trim().isEmpty()) {
                                psDetail.setString(6, detail.getNote().trim());
                            } else {
                                psDetail.setNull(6, Types.VARCHAR);
                            }
                            psDetail.addBatch();
                        }
                        psDetail.executeBatch();
                    }

                    recalculateTotalAmount(generatedId);
                }

                connection.commit();
                return true;
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.WARNING, "Error during rollback", rollbackEx);
                }
                
                // Check if it's a duplicate key error for pr_code
                // MySQL error code 1062 = Duplicate entry
                boolean isDuplicateKey = (e.getErrorCode() == 1062) 
                    || (e instanceof java.sql.SQLIntegrityConstraintViolationException)
                    || (e.getMessage() != null && e.getMessage().contains("Duplicate entry") && e.getMessage().contains("pr_code"));
                
                if (isDuplicateKey) {
                    retryCount++;
                    LOGGER.log(Level.WARNING, "Duplicate pr_code detected (error code: " + e.getErrorCode() + "), retrying... (attempt " + retryCount + "/" + maxRetries + ")");
                    if (retryCount < maxRetries) {
                        try {
                            Thread.sleep(100 * retryCount); // Exponential backoff: 100ms, 200ms, 300ms
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            LOGGER.log(Level.SEVERE, "Thread interrupted during retry", ie);
                            return false;
                        }
                        continue; // Retry
                    } else {
                        LOGGER.log(Level.SEVERE, "Failed to generate unique pr_code after " + maxRetries + " attempts", e);
                        // Don't throw, just return false after max retries
                        return false;
                    }
                } else {
                    // Other SQL errors, don't retry
                    LOGGER.log(Level.SEVERE, "Error creating purchase request with details. Error code: " + e.getErrorCode() + ", Message: " + e.getMessage(), e);
                    return false;
                }
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error resetting auto-commit", e);
                }
            }
        }
        
        return false; // Should not reach here
    }

    private void recalculateTotalAmount(int requestId) {
        String sql = "UPDATE Purchase_Requests pr "
                + "   SET total_amount = (SELECT COALESCE(SUM(total_est), 0) "
                + "                        FROM Purchase_Request_Details d "
                + "                       WHERE d.pr_id = pr.pr_id) "
                + " WHERE pr.pr_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to recalculate total amount for purchase request " + requestId, ex);
        }
    }

    public List<PurchaseRequest> getApprovedPurchaseRequests() {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE pr.deleted_at IS NULL AND pr.status = 'approved' ORDER BY pr.request_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseRequest request = mapRequest(rs);
                list.add(request);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting approved purchase requests.", ex);
        }
        return list;
    }

    public Map<Integer, String> getApprovedPurchaseRequestsWithPOStatus() {
        Map<Integer, String> map = new HashMap<>();
        String sql = "SELECT pr.pr_id, "
                + "       (SELECT po.status "
                + "          FROM Purchase_Orders po "
                + "         WHERE po.deleted_at IS NULL "
                + "           AND po.pr_id = pr.pr_id "
                + "         ORDER BY po.po_id DESC "
                + "         LIMIT 1) AS po_status "
                + "  FROM Purchase_Requests pr "
                + " WHERE pr.deleted_at IS NULL AND pr.status = 'approved'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("pr_id"), rs.getString("po_status"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting PO status for approved purchase requests.", ex);
        }
        return map;
    }

    public String generateNextRequestCode() throws SQLException {
        String prefix = "PR";
        // Use a more robust approach: get MAX value and increment
        // This is safer than SELECT FOR UPDATE which might not work in all isolation levels
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(pr_code, 3) AS UNSIGNED)), 0) AS max_num " +
                     "FROM Purchase_Requests WHERE pr_code LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int maxNum = rs.getInt("max_num");
                    int next = maxNum + 1;
                    return String.format("%s%04d", prefix, next);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error generating next request code.", ex);
            throw ex; // Re-throw to allow retry logic
        }
        return prefix + "0001";
    }

    public void debugAllRequestCodes() {
        String sql = "SELECT pr_code FROM Purchase_Requests ORDER BY pr_code";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> codes = new ArrayList<>();
            while (rs.next()) {
                codes.add(rs.getString("pr_code"));
            }
            LOGGER.log(Level.INFO, "Total purchase request codes: {0}", codes.size());
            LOGGER.log(Level.INFO, "Codes: {0}", codes);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error debugging request codes.", ex);
        }
    }

    public List<PurchaseRequest> searchPurchaseRequest(String keyword, String status, String startDate, String endDate,
                                                        int pageIndex, int pageSize, String sortOption) {
        List<PurchaseRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT)
                .append(" WHERE pr.deleted_at IS NULL ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (pr.pr_code LIKE ? OR pr.pr_id LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND pr.status = ?");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND pr.request_date >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND pr.request_date <= ?");
            params.add(Date.valueOf(endDate));
        }

        String orderBy = "pr.request_date";
        String direction = "DESC";
        if ("date_asc".equalsIgnoreCase(sortOption)) {
            direction = "ASC";
        } else if ("code_asc".equalsIgnoreCase(sortOption)) {
            orderBy = "pr.pr_code";
            direction = "ASC";
        } else if ("code_desc".equalsIgnoreCase(sortOption)) {
            orderBy = "pr.pr_code";
            direction = "DESC";
        }

        sql.append(" ORDER BY ").append(orderBy).append(' ').append(direction)
           .append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(Math.max(0, (pageIndex - 1) * pageSize));

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRequest(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error searching purchase requests.", ex);
        }
        return list;
    }

    public int countPurchaseRequest(String keyword, String status, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Purchase_Requests pr WHERE pr.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (pr.pr_code LIKE ? OR pr.pr_id LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND pr.status = ?");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND pr.request_date >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND pr.request_date <= ?");
            params.add(Date.valueOf(endDate));
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error counting purchase requests.", ex);
        }
        return 0;
    }
}
