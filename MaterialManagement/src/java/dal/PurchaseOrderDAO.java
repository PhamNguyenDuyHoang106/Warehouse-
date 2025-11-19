package dal;

import entity.DBContext;
import entity.PurchaseOrder;
import entity.PurchaseOrderDetail;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseOrderDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderDAO.class.getName());

    private static final String BASE_SELECT =
            "SELECT po.po_id, po.po_code, po.pr_id, po.supplier_id, s.supplier_name, "
                    + "po.currency_id, c.currency_code, po.order_date, po.expected_delivery_date, po.delivery_address, "
                    + "po.payment_term_id, pt.term_name AS payment_term_name, "
                    + "po.total_amount, po.tax_amount, po.discount_amount, po.grand_total, "
                    + "po.status, po.confirmed_by, u2.full_name AS confirmed_by_name, po.confirmed_at, "
                    + "po.created_by, u1.full_name AS created_by_name, po.created_at, po.updated_at, po.deleted_at, "
                    + "pr.pr_code AS purchase_request_code, "
                    + "COALESCE(SUM(pod.line_total), 0) AS aggregated_total, "
                    + "COALESCE(vw.pending_qty, 0) AS pending_qty "
                    + "FROM Purchase_Orders po "
                    + "LEFT JOIN Users u1 ON po.created_by = u1.user_id "
                    + "LEFT JOIN Users u2 ON po.confirmed_by = u2.user_id "
                    + "LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id "
                    + "LEFT JOIN Currencies c ON po.currency_id = c.currency_id "
                    + "LEFT JOIN Payment_Terms pt ON po.payment_term_id = pt.term_id "
                    + "LEFT JOIN Purchase_Requests pr ON po.pr_id = pr.pr_id "
                    + "LEFT JOIN Purchase_Order_Details pod ON po.po_id = pod.po_id "
                    + "LEFT JOIN vw_po_pending_receipt vw ON po.po_code = vw.po_code ";

    public PurchaseOrderDAO() {
        super();
    }

    public List<PurchaseOrder> getPurchaseOrders(int page, int pageSize, String status, String poCode, LocalDate startDate, LocalDate endDate, String sortBy) {
        List<PurchaseOrder> orders = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            return orders;
        }

        StringBuilder sql = new StringBuilder(BASE_SELECT)
                .append("WHERE po.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            String statusLower = status.trim().toLowerCase();
            // Special filter for pending receipt (from vw_po_pending_receipt)
            if ("pending_receipt".equals(statusLower)) {
                sql.append("AND vw.pending_qty > 0 ");
            } else {
                sql.append("AND po.status = ? ");
                params.add(statusLower);
            }
        }

        if (poCode != null && !poCode.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
            params.add("%" + poCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        sql.append("GROUP BY po.po_id ");
        if ("oldest".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY po.created_at ASC, po.po_id ASC ");
        } else {
            sql.append("ORDER BY po.created_at DESC, po.po_id DESC ");
        }
        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(Math.max(0, (page - 1) * pageSize));

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPurchaseOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase orders: " + e.getMessage(), e);
        }
        return orders;
    }

    public int getPurchaseOrderCount(String status, String poCode, LocalDate startDate, LocalDate endDate) {
        int count = 0;
        Connection conn = getConnection();
        if (conn == null) {
            return count;
        }

        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT po.po_id) FROM Purchase_Orders po ");
        // Join with view if needed for pending_receipt filter
        boolean needViewJoin = "pending_receipt".equals(status != null ? status.trim().toLowerCase() : "");
        if (needViewJoin) {
            sql.append("LEFT JOIN vw_po_pending_receipt vw ON po.po_code = vw.po_code ");
        }
        sql.append("WHERE po.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            String statusLower = status.trim().toLowerCase();
            // Special filter for pending receipt (from vw_po_pending_receipt)
            if ("pending_receipt".equals(statusLower)) {
                sql.append("AND vw.pending_qty > 0 ");
            } else {
                sql.append("AND po.status = ? ");
                params.add(statusLower);
            }
        }

        if (poCode != null && !poCode.trim().isEmpty()) {
            sql.append("AND po.po_code LIKE ? ");
            params.add("%" + poCode.trim() + "%");
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting purchase orders: " + e.getMessage(), e);
        }
        return count;
    }

    public PurchaseOrder getPurchaseOrderById(int poId) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }

        String sql = BASE_SELECT + "WHERE po.po_id = ? AND po.deleted_at IS NULL GROUP BY po.po_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder order = mapResultSetToPurchaseOrder(rs);
                    order.setDetails(getPurchaseOrderDetails(poId));
                    return order;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase order by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails(int poId) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            return details;
        }

        String sql = "SELECT pod.po_detail_id, pod.po_id, pod.material_id, m.material_name, m.material_code, m.url, "
                + "pod.unit_id, u.unit_name, pod.quantity_ordered, pod.unit_price, pod.tax_rate, pod.discount_rate, pod.line_total, "
                + "pod.received_quantity, pod.note, pod.created_at "
                + "FROM Purchase_Order_Details pod "
                + "LEFT JOIN Materials m ON pod.material_id = m.material_id "
                + "LEFT JOIN Units u ON pod.unit_id = u.unit_id "
                + "WHERE pod.po_id = ? "
                + "ORDER BY pod.po_detail_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(mapResultSetToPurchaseOrderDetail(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase order details: " + e.getMessage(), e);
        }
        return details;
    }

    public boolean updatePurchaseOrderStatus(int poId, String status, Integer approvedBy, String approvalReason, String rejectionReason) {
        Connection conn = getConnection();
        if (conn == null) {
            LOGGER.log(Level.SEVERE, "Connection is null in updatePurchaseOrderStatus for poId: " + poId);
            return false;
        }

        String normalizedStatus = status != null ? status.trim().toLowerCase() : null;
        
        // Validate status against schema enum values: 'draft','sent','confirmed','partially_received','received','cancelled'
        if (normalizedStatus == null || 
            (!normalizedStatus.equals("draft") && !normalizedStatus.equals("sent") && 
             !normalizedStatus.equals("confirmed") && !normalizedStatus.equals("partially_received") && 
             !normalizedStatus.equals("received") && !normalizedStatus.equals("cancelled"))) {
            LOGGER.log(Level.WARNING, "Invalid status value: {0} for poId: {1}", new Object[]{status, poId});
            return false;
        }
        
        boolean isConfirmedStatus = normalizedStatus != null
                && (normalizedStatus.equals("confirmed") || normalizedStatus.equals("received") || normalizedStatus.equals("partially_received"));

        Integer newConfirmedBy = isConfirmedStatus ? approvedBy : null;
        Timestamp confirmedAt = (isConfirmedStatus && approvedBy != null)
                ? new Timestamp(System.currentTimeMillis())
                : null;
        // Note: Purchase_Orders table doesn't have 'note' column in V12 schema
        // Decision note should be logged in Activity_Log instead
        // Keeping decisionNote for potential Activity_Log logging in the future
        String decisionNote = null;
        if (approvalReason != null && !approvalReason.trim().isEmpty()) {
            decisionNote = approvalReason.trim();
        } else if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
            decisionNote = rejectionReason.trim();
        }
        // TODO: Log decisionNote to Activity_Log table
        String sql = "UPDATE Purchase_Orders SET status = ?, confirmed_by = ?, confirmed_at = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE po_id = ? AND deleted_at IS NULL";

        try {
            // Ensure auto-commit is enabled for this update
            boolean originalAutoCommit = conn.getAutoCommit();
            if (!originalAutoCommit) {
                conn.setAutoCommit(true);
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, normalizedStatus);
                ps.setObject(2, newConfirmedBy);
                ps.setTimestamp(3, confirmedAt);
                ps.setInt(4, poId);
                
                LOGGER.log(Level.INFO, "Executing UPDATE - poId: {0}, status: {1}, confirmed_by: {2}, confirmed_at: {3}", 
                    new Object[]{poId, normalizedStatus, newConfirmedBy, confirmedAt});
                
                int rowsAffected = ps.executeUpdate();
                LOGGER.log(Level.INFO, "UPDATE result - poId: {0}, rowsAffected: {1}", new Object[]{poId, rowsAffected});
                
                if (rowsAffected == 0) {
                    LOGGER.log(Level.WARNING, "No rows affected for poId: {0}. PO may not exist or already deleted.", poId);
                }
                
                return rowsAffected > 0;
            } finally {
                // Restore original auto-commit setting
                if (!originalAutoCommit) {
                    conn.setAutoCommit(false);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating purchase order status - poId: {0}, status: {1}, error: {2}", 
                new Object[]{poId, normalizedStatus, e.getMessage()});
            return false;
        }
    }

    public String generateNextPOCode() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(po_code, 3) AS SIGNED)), 0) AS max_num "
                + "FROM Purchase_Orders WHERE po_code LIKE 'PO%'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int nextNum = 1;
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                if (!rs.wasNull() && maxNum >= 0) {
                    nextNum = maxNum + 1;
                }
            }
            return String.format("PO%03d", nextNum);
        }
    }

    public boolean createPurchaseOrder(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> details) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            if (purchaseOrder.getPoCode() == null || purchaseOrder.getPoCode().isEmpty()) {
                purchaseOrder.setPoCode(generateNextPOCode());
            }

            String insertPOSql = "INSERT INTO Purchase_Orders (po_code, pr_id, supplier_id, currency_id, order_date, expected_delivery_date, "
                    + "delivery_address, payment_term_id, total_amount, tax_amount, discount_amount, status, created_by, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

            try (PreparedStatement ps = conn.prepareStatement(insertPOSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, purchaseOrder.getPoCode());
                ps.setInt(2, purchaseOrder.getPurchaseRequestId());
                ps.setObject(3, purchaseOrder.getSupplierId(), java.sql.Types.INTEGER);
                ps.setObject(4, purchaseOrder.getCurrencyId(), java.sql.Types.INTEGER);
                Date orderDate = purchaseOrder.getOrderDate() != null ? purchaseOrder.getOrderDate() : Date.valueOf(LocalDate.now());
                ps.setDate(5, orderDate);
                ps.setObject(6, purchaseOrder.getExpectedDeliveryDate());
                ps.setString(7, purchaseOrder.getDeliveryAddress());
                ps.setObject(8, purchaseOrder.getPaymentTermId(), java.sql.Types.INTEGER);
                ps.setBigDecimal(9, purchaseOrder.getTotalAmount());
                ps.setBigDecimal(10, purchaseOrder.getTaxAmount());
                ps.setBigDecimal(11, purchaseOrder.getDiscountAmount());
                ps.setString(12, purchaseOrder.getStatus() != null ? purchaseOrder.getStatus().toLowerCase() : "draft");
                ps.setInt(13, purchaseOrder.getCreatedBy());
                // Note: Purchase_Orders table doesn't have 'note' column in V12 schema

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating purchase order failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Creating purchase order failed, no ID obtained.");
                    }
                    int poId = generatedKeys.getInt(1);
                    purchaseOrder.setPoId(poId);

                    String insertDetailSql = "INSERT INTO Purchase_Order_Details (po_id, material_id, unit_id, quantity_ordered, unit_price, tax_rate, discount_rate, note) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement detailPs = conn.prepareStatement(insertDetailSql)) {
                        for (PurchaseOrderDetail detail : details) {
                            detailPs.setInt(1, poId);
                            detailPs.setInt(2, detail.getMaterialId());
                            detailPs.setObject(3, detail.getUnitId(), java.sql.Types.INTEGER);
                            detailPs.setBigDecimal(4, detail.getQuantityOrdered());
                            detailPs.setBigDecimal(5, detail.getUnitPrice());
                            detailPs.setBigDecimal(6, detail.getTaxRate());
                            detailPs.setBigDecimal(7, detail.getDiscountRate());
                            detailPs.setString(8, detail.getNote());
                            detailPs.addBatch();
                        }
                        detailPs.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error rolling back purchase order creation", rollbackEx);
                }
            }
            LOGGER.log(Level.SEVERE, "Error creating purchase order: " + e.getMessage(), e);
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error resetting auto-commit for connection", e);
                }
            }
        }
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        List<PurchaseOrder> orders = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) {
            return orders;
        }

        String statusLower = status != null ? status.trim().toLowerCase() : "";
        String sql;
        if ("pending_receipt".equals(statusLower)) {
            sql = BASE_SELECT + "WHERE po.deleted_at IS NULL AND vw.pending_qty > 0 GROUP BY po.po_id ORDER BY po.created_at DESC";
        } else {
            sql = BASE_SELECT + "WHERE po.deleted_at IS NULL AND po.status = ? GROUP BY po.po_id ORDER BY po.created_at DESC";
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!"pending_receipt".equals(statusLower)) {
                ps.setString(1, statusLower);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToPurchaseOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase orders by status: " + e.getMessage(), e);
        }
        return orders;
    }

    public int getPurchaseOrderIdByCodeOrRequest(String searchTerm) {
        Connection conn = getConnection();
        if (conn == null) {
            return 0;
        }

        String sql = "SELECT po.po_id FROM Purchase_Orders po "
                + "LEFT JOIN Purchase_Requests pr ON po.pr_id = pr.pr_id "
                + "WHERE po.deleted_at IS NULL AND po.status = 'sent' AND (po.po_code = ? OR pr.pr_code = ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchTerm);
            ps.setString(2, searchTerm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("po_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase order ID by code or request: " + e.getMessage(), e);
        }
        return 0;
    }

    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder order = new PurchaseOrder();
        order.setPoId(rs.getInt("po_id"));
        order.setPoCode(rs.getString("po_code"));
        order.setPurchaseRequestId(rs.getInt("pr_id"));
        order.setSupplierId((Integer) rs.getObject("supplier_id"));
        order.setSupplierName(rs.getString("supplier_name"));
        order.setCurrencyId((Integer) rs.getObject("currency_id"));
        order.setCurrencyCode(rs.getString("currency_code"));
        order.setOrderDate(rs.getDate("order_date"));
        order.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setPaymentTermId((Integer) rs.getObject("payment_term_id"));
        order.setPaymentTermName(rs.getString("payment_term_name"));
        order.setCreatedBy(rs.getInt("created_by"));
        order.setCreatedByName(rs.getString("created_by_name"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        order.setStatus(rs.getString("status"));
        // Schema v11: Purchase_Orders doesn't have 'note' column
        // order.setNote(rs.getString("note"));
        order.setConfirmedBy((Integer) rs.getObject("confirmed_by"));
        order.setConfirmedByName(rs.getString("confirmed_by_name"));
        order.setConfirmedAt(rs.getTimestamp("confirmed_at"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setTaxAmount(rs.getBigDecimal("tax_amount"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setGrandTotal(rs.getBigDecimal("grand_total"));
        order.setDeletedAt(rs.getTimestamp("deleted_at"));
        order.setPurchaseRequestCode(rs.getString("purchase_request_code"));
        order.setAggregatedDetailTotal(rs.getBigDecimal("aggregated_total"));
        
        // From vw_po_pending_receipt view
        try {
            BigDecimal pendingQty = rs.getBigDecimal("pending_qty");
            order.setPendingQty(pendingQty != null ? pendingQty : BigDecimal.ZERO);
        } catch (SQLException e) {
            // Column may not exist if view is not joined
            order.setPendingQty(BigDecimal.ZERO);
        }
        
        return order;
    }

    private PurchaseOrderDetail mapResultSetToPurchaseOrderDetail(ResultSet rs) throws SQLException {
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPoDetailId(rs.getInt("po_detail_id"));
        detail.setPoId(rs.getInt("po_id"));
        detail.setMaterialId(rs.getInt("material_id"));
        detail.setMaterialName(rs.getString("material_name"));
        detail.setMaterialCode(rs.getString("material_code"));
        detail.setMaterialImageUrl(rs.getString("url"));
        detail.setUnitId((Integer) rs.getObject("unit_id"));
        detail.setUnitName(rs.getString("unit_name"));
        detail.setQuantityOrdered(rs.getBigDecimal("quantity_ordered"));
        detail.setUnitPrice(rs.getBigDecimal("unit_price"));
        detail.setTaxRate(rs.getBigDecimal("tax_rate"));
        detail.setDiscountRate(rs.getBigDecimal("discount_rate"));
        detail.setLineTotal(rs.getBigDecimal("line_total"));
        detail.setReceivedQuantity(rs.getBigDecimal("received_quantity"));
        detail.setNote(rs.getString("note"));
        detail.setCreatedAt(rs.getTimestamp("created_at"));
        return detail;
    }

    public Connection getConnection() {
        return connection;
    }
} 