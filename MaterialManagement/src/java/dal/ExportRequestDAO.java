package dal;

import entity.DBContext;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportRequestDAO extends DBContext {
    
    private static final Logger LOGGER = Logger.getLogger(ExportRequestDAO.class.getName());
    
    public ExportRequestDAO() {
        super();
    }
    
    public List<ExportRequest> getAll(String status, String search, String searchDate, int page, int itemsPerPage) {
        try {
            return getAllWithPagination(0, itemsPerPage, search, status, null, null, null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    public List<ExportRequest> getAllWithPagination(int offset, int pageSize, String searchKeyword, String status, String sortByName, String requestDateFrom, String requestDateTo) throws SQLException {
        List<ExportRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, "
                + "COALESCE(d.department_name, 'Unknown') as departmentName, "
                + "COALESCE(a.full_name, 'Unknown') as approverName "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.request_by = u.user_id "
                + "LEFT JOIN Departments d ON er.department_id = d.department_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.deleted_at IS NULL"
        );
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (er.er_code LIKE ? OR u.full_name LIKE ?)");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND er.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND er.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(er.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(er.request_date) <= ?");
        }
        
        if (sortByName != null && !sortByName.isEmpty()) {
            if ("oldest".equalsIgnoreCase(sortByName)) {
                sql.append(" ORDER BY er.created_at ASC, er.er_id ASC");
            }
        } else {
            sql.append(" ORDER BY er.created_at DESC, er.er_id DESC");
        }
        sql.append(" LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
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
                    requests.add(mapResultSetToExportRequest(rs));
                }
            }
        }
        return requests;
    }
    
    public int getTotalCount(String status, String search, String searchDate) {
        try {
            return getTotalExportRequestCount(search, status, null, null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting export requests: " + e.getMessage(), e);
            return 0;
        }
    }
    
    public int getTotalExportRequestCount(String searchKeyword, String status, String requestDateFrom, String requestDateTo) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) "
                + "FROM Export_Requests er "
                + "LEFT JOIN Users u ON er.request_by = u.user_id "
                + "LEFT JOIN Users a ON er.approved_by = a.user_id "
                + "WHERE er.deleted_at IS NULL"
        );
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (er.er_code LIKE ? OR u.full_name LIKE ?)");
        }
        if (status != null && !status.equalsIgnoreCase("all")) {
            sql.append(" AND er.status = ?");
        }
        if (requestDateFrom != null && !requestDateFrom.trim().isEmpty() && requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND er.request_date BETWEEN ? AND ?");
        } else if (requestDateFrom != null && !requestDateFrom.trim().isEmpty()) {
            sql.append(" AND DATE(er.request_date) >= ?");
        } else if (requestDateTo != null && !requestDateTo.trim().isEmpty()) {
            sql.append(" AND DATE(er.request_date) <= ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchKeyword.trim() + "%");
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
    
    public ExportRequest getById(int id) {
        connection = getConnection();
        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, " +
                     "COALESCE(d.department_name, 'Unknown') as departmentName, " +
                     "COALESCE(a.full_name, 'Unknown') as approverName " +
                     "FROM Export_Requests er " +
                     "LEFT JOIN Users u ON er.request_by = u.user_id " +
                     "LEFT JOIN Departments d ON er.department_id = d.department_id " +
                     "LEFT JOIN Users a ON er.approved_by = a.user_id " +
                     "WHERE er.er_id = ? AND er.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ExportRequest request = mapResultSetToExportRequest(rs);
                    // Also set customer_id directly from the table (Customer replaces Recipient)
                    // Wrap in try-catch in case column doesn't exist
                    try {
                        Object customerIdObj = rs.getObject("customer_id");
                        if (customerIdObj != null) {
                            request.setCustomerId(rs.getInt("customer_id"));
                        } else {
                            request.setCustomerId(null);
                        }
                    } catch (SQLException e) {
                        // Column may not exist, try recipient_id for backward compatibility
                        try {
                            Object recipientIdObj = rs.getObject("recipient_id");
                            if (recipientIdObj != null) {
                                request.setCustomerId(rs.getInt("recipient_id"));
                            } else {
                                request.setCustomerId(null);
                            }
                        } catch (SQLException e2) {
                            request.setCustomerId(null);
                        }
                    }
                    return request;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting export request by ID: " + id, e);
        }
        return null;
    }
    
    /**
     * Get all export requests by status (for ExportMaterialServlet)
     */
    public List<ExportRequest> getAllRequestsByStatus(String status) {
        List<ExportRequest> requests = new ArrayList<>();
        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, " +
                     "COALESCE(d.department_name, 'Unknown') as departmentName, " +
                     "COALESCE(a.full_name, 'Unknown') as approverName " +
                     "FROM Export_Requests er " +
                     "LEFT JOIN Users u ON er.request_by = u.user_id " +
                     "LEFT JOIN Departments d ON er.department_id = d.department_id " +
                     "LEFT JOIN Users a ON er.approved_by = a.user_id " +
                     "WHERE er.status = ? AND er.deleted_at IS NULL " +
                     "ORDER BY er.created_at DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToExportRequest(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting export requests by status: " + status, e);
        }
        return requests;
    }
    
    public boolean update(ExportRequest request) {
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE Export_Requests SET "
                + "status = ?, "
                + "approved_by = ?, "
                + (("approved".equals(request.getStatus()) || "rejected".equals(request.getStatus())) ? "approved_at = CURRENT_TIMESTAMP, " : "")
                + "approval_reason = ?, "
                + "rejection_reason = ? "
                + "WHERE er_id = ? AND deleted_at IS NULL";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, request.getStatus());
            ps.setInt(idx++, request.getApprovedBy());
            ps.setString(idx++, request.getApprovalReason());
            ps.setString(idx++, request.getRejectionReason());
            ps.setInt(idx++, request.getExportRequestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating export request: " + e.getMessage(), e);
            return false;
        }
    }
    
    public boolean delete(int id) {
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE Export_Requests SET deleted_at = CURRENT_TIMESTAMP WHERE er_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting export request: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean add(ExportRequest request, List<ExportRequestDetail> details) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Export_Requests (er_code, request_by, status, expected_date, purpose) "
                       + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, request.getRequestCode());
                ps.setInt(2, request.getUserId());
                ps.setString(3, request.getStatus());
                ps.setDate(4, request.getDeliveryDate());
                ps.setString(5, request.getReason());
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating request failed, no rows affected.");
                }
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setExportRequestId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating request failed, no ID obtained.");
                    }
                }
            }
            String detailSql = "INSERT INTO Export_Request_Details (er_id, material_id, rack_id, quantity, unit_price_export) "
                             + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {
                for (ExportRequestDetail detail : details) {
                    ps.setInt(1, request.getExportRequestId());
                    ps.setInt(2, detail.getMaterialId());
                    ps.setObject(3, detail.getRackId());
                    ps.setBigDecimal(4, detail.getQuantity());
                    if (detail.getUnitPriceExport() != null) {
                        ps.setBigDecimal(5, detail.getUnitPriceExport());
                    } else {
                        ps.setNull(5, java.sql.Types.DECIMAL);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction: " + ex.getMessage(), ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Error adding export request: " + e.getMessage(), e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection: " + e.getMessage(), e);
                }
            }
        }
    }

    public int getNextCodeSequence() {
        if (connection == null) {
            return 1;
        }

        String sql = "SELECT COUNT(*) + 1 FROM Export_Requests WHERE YEAR(request_date) = YEAR(CURRENT_DATE)";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next code sequence: " + e.getMessage(), e);
        }
        return 1;
    }

    public List<ExportRequest> getAllSortedByRequestDate(String order) {
        return getAllSorted("request_date", order);
    }

    public List<ExportRequest> getAllSortedByStatus(String order) {
        return getAllSorted("status", order);
    }

    public List<ExportRequest> getAllSortedByRequestCode(String order) {
        return getAllSorted("request_code", order);
    }

    private List<ExportRequest> getAllSorted(String sortColumn, String order) {
        List<ExportRequest> requests = new ArrayList<>();
        if (connection == null) {
            return requests;
        }

        String sql = "SELECT er.*, COALESCE(u.full_name, 'Unknown') as userName, " +
                     "COALESCE(d.department_name, 'Unknown') as departmentName, " +
                     "COALESCE(a.full_name, 'Unknown') as approverName " +
                     "FROM Export_Requests er " +
                     "LEFT JOIN Users u ON er.request_by = u.user_id " +
                     "LEFT JOIN Departments d ON er.department_id = d.department_id " +
                     "LEFT JOIN Users a ON er.approved_by = a.user_id " +
                     "WHERE er.deleted_at IS NULL " +
                     "ORDER BY er." + sortColumn + " " + ("desc".equalsIgnoreCase(order) ? "DESC" : "ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapResultSetToExportRequest(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching export requests sorted by " + sortColumn + ": " + e.getMessage(), e);
        }
        return requests;
    }

    private ExportRequest mapResultSetToExportRequest(ResultSet rs) throws SQLException {
        ExportRequest request = new ExportRequest();
        request.setExportRequestId(rs.getInt("er_id"));
        request.setRequestCode(rs.getString("er_code"));
        request.setRequestDate(rs.getTimestamp("request_date"));
        request.setDeliveryDate(rs.getDate("expected_date"));
        request.setStatus(rs.getString("status"));
        request.setReason(rs.getString("purpose"));
        request.setUserId(rs.getInt("request_by"));
        request.setUserName(rs.getString("userName"));
        
        // Map customer_id and customerName (Customer replaces Recipient)
        try {
            Object customerIdObj = rs.getObject("customer_id");
            if (customerIdObj != null) {
                request.setCustomerId(rs.getInt("customer_id"));
            } else {
                request.setCustomerId(null);
            }
        } catch (SQLException e) {
            // Column may not exist in all queries, try recipient_id for backward compatibility
            try {
                Object recipientIdObj = rs.getObject("recipient_id");
                if (recipientIdObj != null) {
                    request.setCustomerId(rs.getInt("recipient_id"));
                } else {
                    request.setCustomerId(null);
                }
            } catch (SQLException e2) {
                request.setCustomerId(null);
            }
        }
        
        try {
            String customerName = rs.getString("customerName");
            if (customerName == null || customerName.equals("Unknown")) {
                customerName = rs.getString("customer_name"); // Try alternative column name
            }
            request.setCustomerName(customerName != null && !customerName.equals("Unknown") ? customerName : null);
        } catch (SQLException e) {
            // Column may not exist in all queries, try recipientName for backward compatibility
            try {
                String recipientName = rs.getString("recipientName");
                request.setCustomerName(recipientName != null && !recipientName.equals("Unknown") ? recipientName : null);
            } catch (SQLException e2) {
                request.setCustomerName(null);
            }
        }
        
        request.setApprovedBy(rs.getInt("approved_by"));
        request.setApproverName(rs.getString("approverName"));
        request.setApprovedAt(rs.getTimestamp("approved_at"));
        // Schema v11: Export_Requests doesn't have approval_reason or rejection_reason columns
        // request.setApprovalReason(rs.getString("approval_reason"));
        // request.setRejectionReason(rs.getString("rejection_reason"));
        
        // Set other fields
        try {
            request.setUsed(rs.getBoolean("is_used"));
        } catch (SQLException e) {
            // Column may not exist
        }
        try {
            request.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            // Column may not exist
        }
        try {
            request.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            // Column may not exist
        }
        try {
            request.setDeletedAt(rs.getTimestamp("deleted_at"));
        } catch (SQLException e) {
            // Column may not exist
        }
        
        return request;
    }
}