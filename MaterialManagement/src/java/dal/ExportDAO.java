package dal;

import entity.DBContext;
import entity.Export;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ExportDAO.class.getName());

    /**
     * Create a new export record and return the generated export_id (Schema v11)
     */
    public int createExport(Export export) {
        String sql = "INSERT INTO Exports (export_code, so_id, er_id, warehouse_id, export_date, exported_by, status, note, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, export.getExportCode());

            if (export.getSoId() != null) {
                ps.setInt(2, export.getSoId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            if (export.getErId() != null) {
                ps.setInt(3, export.getErId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setInt(4, export.getWarehouseId());
            ps.setTimestamp(5, export.getExportDate() != null ? Timestamp.valueOf(export.getExportDate()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, export.getExportedBy());
            ps.setString(7, export.getStatus() != null ? export.getStatus() : "draft");
            ps.setString(8, export.getNote());
            ps.setInt(9, export.getCreatedBy() != null ? export.getCreatedBy() : export.getExportedBy());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating export", e);
        }
        return -1;
    }

    /**
     * Get export by ID with joined information
     */
    public Export getExportById(int exportId) {
        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.export_id = ? AND e.deleted_at IS NULL";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToExport(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting export by ID: " + exportId, e);
        }
        return null;
    }

    /**
     * Get all exports with pagination
     */
    public List<Export> getAllExports(int page, int pageSize) {
        List<Export> exports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.deleted_at IS NULL " +
                    "ORDER BY e.export_date DESC " +
                    "LIMIT ? OFFSET ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all exports", e);
        }
        return exports;
    }

    /**
     * Get exports by date range (Schema v11)
     */
    public List<Export> getExportsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Export> exports = new ArrayList<>();

        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.deleted_at IS NULL AND DATE(e.export_date) BETWEEN ? AND ? " +
                    "ORDER BY e.export_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting exports by date range", e);
        }
        return exports;
    }

    /**
     * Get exports by customer (Schema v11 - replaces recipient)
     */
    public List<Export> getExportsByCustomer(int customerId) {
        List<Export> exports = new ArrayList<>();

        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.deleted_at IS NULL AND so.customer_id = ? " +
                    "ORDER BY e.export_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting exports by customer", e);
        }
        return exports;
    }

    /**
     * Get exports by warehouse (Schema v11 - replaces vehicle)
     * Since vehicles are no longer directly linked to exports in v11,
     * this method now finds exports by warehouse
     */
    public List<Export> getExportsByWarehouse(int warehouseId) {
        List<Export> exports = new ArrayList<>();

        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.deleted_at IS NULL AND e.warehouse_id = ? " +
                    "ORDER BY e.export_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, warehouseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting exports by warehouse", e);
        }
        return exports;
    }

    /**
     * Search exports by code, customer name, sales order code, or export request code (Schema v11)
     */
    public List<Export> searchExports(String searchQuery) {
        List<Export> exports = new ArrayList<>();

        String sql = "SELECT e.*, " +
                    "u.full_name as exported_by_name, " +
                    "w.warehouse_name, " +
                    "so.so_code as sales_order_code, " +
                    "er.er_code as export_request_code, " +
                    "c.customer_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id " +
                    "LEFT JOIN Sales_Orders so ON e.so_id = so.so_id " +
                    "LEFT JOIN Export_Requests er ON e.er_id = er.er_id " +
                    "LEFT JOIN Customers c ON so.customer_id = c.customer_id " +
                    "WHERE e.deleted_at IS NULL AND (" +
                    "e.export_code LIKE ? OR " +
                    "c.customer_name LIKE ? OR " +
                    "so.so_code LIKE ? OR " +
                    "er.er_code LIKE ? OR " +
                    "w.warehouse_name LIKE ?)" +
                    "ORDER BY e.export_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchQuery + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching exports", e);
        }
        return exports;
    }

    /**
     * Get total count of exports (Schema v11)
     */
    public int getTotalExportsCount() {
        String sql = "SELECT COUNT(*) FROM Exports WHERE deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total exports count", e);
        }
        return 0;
    }

    /**
     * Get total exported quantity across all exports (for dashboard)
     */
    public int getTotalExportedQuantity() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) as total FROM Export_Details";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total exported quantity", e);
        }
        return 0;
    }

    /**
     * Update export information
     */
    public boolean updateExport(Export export) {
        String sql = "UPDATE Exports SET so_id = ?, er_id = ?, warehouse_id = ?, status = ?, note = ?, updated_at = CURRENT_TIMESTAMP WHERE export_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (export.getSoId() != null) {
                ps.setInt(1, export.getSoId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            if (export.getErId() != null) {
                ps.setInt(2, export.getErId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setInt(3, export.getWarehouseId());
            ps.setString(4, export.getStatus());
            ps.setString(5, export.getNote());
            ps.setInt(6, export.getExportId());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating export", e);
            return false;
        }
    }

    /**
     * Get export history with advanced filters (for ExportHistoryServlet)
     */
    public List<Export> getExportHistoryAdvanced(String fromDate, String toDate, String materialName, 
                                                  String sortByCustomer, String sortByExportedBy, int page, int pageSize) {
        List<Export> exports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT e.*, ");
        sql.append("u.full_name as exported_by_name, ");
        sql.append("w.warehouse_name, ");
        sql.append("so.so_code as sales_order_code, ");
        sql.append("er.er_code as export_request_code, ");
        sql.append("c.customer_name ");
        sql.append("FROM Exports e ");
        sql.append("LEFT JOIN Users u ON e.exported_by = u.user_id ");
        sql.append("LEFT JOIN Warehouses w ON e.warehouse_id = w.warehouse_id ");
        sql.append("LEFT JOIN Sales_Orders so ON e.so_id = so.so_id ");
        sql.append("LEFT JOIN Export_Requests er ON e.er_id = er.er_id ");
        sql.append("LEFT JOIN Customers c ON so.customer_id = c.customer_id ");
        
        // Join Export_Details and Materials if filtering by material name
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Export_Details ed ON e.export_id = ed.export_id ");
            sql.append("JOIN Materials m ON ed.material_id = m.material_id ");
        }
        
        sql.append("WHERE e.deleted_at IS NULL ");

        // Date filters
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND DATE(e.export_date) >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND DATE(e.export_date) <= ? ");
        }
        
        // Material name filter
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
        }
        
        // Sorting (Schema v11)
        if (sortByCustomer != null && !sortByCustomer.trim().isEmpty()) {
            sql.append("ORDER BY c.customer_name ").append(sortByCustomer.equalsIgnoreCase("desc") ? "DESC" : "ASC").append(", e.export_date DESC ");
        } else if (sortByExportedBy != null && !sortByExportedBy.trim().isEmpty()) {
            sql.append("ORDER BY u.full_name ").append(sortByExportedBy.equalsIgnoreCase("desc") ? "DESC" : "ASC").append(", e.export_date DESC ");
        } else {
            sql.append("ORDER BY e.export_date DESC ");
        }
        
        sql.append("LIMIT ? OFFSET ?");
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setString(paramIndex++, fromDate);
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setString(paramIndex++, toDate);
            }
            if (materialName != null && !materialName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + materialName + "%");
            }
            
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting export history with filters", e);
        }
        return exports;
    }

    /**
     * Count export history records with advanced filters
     */
    public int countExportHistoryAdvanced(String fromDate, String toDate, String materialName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT e.export_id) as total ");
        sql.append("FROM Exports e ");

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Export_Details ed ON e.export_id = ed.export_id ");
            sql.append("JOIN Materials m ON ed.material_id = m.material_id ");
        }

        sql.append("WHERE e.deleted_at IS NULL ");
        
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND DATE(e.export_date) >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND DATE(e.export_date) <= ? ");
        }
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
        }
        
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setString(paramIndex++, fromDate);
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setString(paramIndex++, toDate);
            }
            if (materialName != null && !materialName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + materialName + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting export history", e);
        }
        return 0;
    }

    /**
     * Get export details by export ID
     */
    public List<entity.ExportDetail> getExportDetailsByExportId(int exportId) throws java.sql.SQLException {
        List<entity.ExportDetail> details = new ArrayList<>();
        String sql = "SELECT ed.*, m.material_name, m.material_code, m.url, " +
                    "du.unit_name AS default_unit_name, wr.rack_name, wr.rack_code " +
                    "FROM Export_Details ed " +
                    "JOIN Materials m ON ed.material_id = m.material_id " +
                    "LEFT JOIN Units du ON m.default_unit_id = du.unit_id " +
                    "LEFT JOIN Warehouse_Racks wr ON ed.rack_id = wr.rack_id " +
                    "WHERE ed.export_id = ? " +
                    "ORDER BY ed.export_detail_id";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entity.ExportDetail detail = new entity.ExportDetail();
                detail.setExportDetailId(rs.getInt("export_detail_id"));
                detail.setExportId(rs.getInt("export_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getBigDecimal("quantity"));
                
                Integer rackId = rs.getObject("rack_id", Integer.class);
                detail.setRackId(rackId);
                
                // Optional fields with null check
                try {
                    detail.setNote(rs.getString("note"));
                } catch (SQLException e) {
                    detail.setNote(null);
                }
                
                try {
                    detail.setStatus(rs.getString("status"));
                } catch (SQLException e) {
                    detail.setStatus(null);
                }
                
                try {
                    detail.setUnitPriceExport(rs.getBigDecimal("unit_price_export"));
                } catch (SQLException e) {
                    detail.setUnitPriceExport(null);
                }
                
                try {
                    detail.setTotalAmountExport(rs.getBigDecimal("total_amount_export"));
                } catch (SQLException e) {
                    detail.setTotalAmountExport(null);
                }
                
                try {
                    Integer exportRequestDetailId = rs.getObject("export_request_detail_id", Integer.class);
                    detail.setExportRequestDetailId(exportRequestDetailId);
                } catch (SQLException e) {
                    detail.setExportRequestDetailId(null);
                }
                
                try {
                    if (rs.getTimestamp("created_at") != null) {
                        detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                } catch (SQLException e) {
                    // Column may not exist
                }
                
                try {
                    if (rs.getTimestamp("updated_at") != null) {
                        detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                } catch (SQLException e) {
                    // Column may not exist
                }
                
                // Joined fields
                detail.setMaterialName(rs.getString("material_name"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialsUrl(rs.getString("url"));
                detail.setUnitName(rs.getString("default_unit_name"));
                detail.setRackName(rs.getString("rack_name"));
                detail.setRackCode(rs.getString("rack_code"));
                
                details.add(detail);
            }
        }
        return details;
    }

    /**
     * Generate next export code (e.g., EXP0001, EXP0002)
     */
    public String generateNextExportCode() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(export_code, 4) AS SIGNED)), 0) + 1 AS next_number FROM Exports WHERE export_code LIKE 'EXP%'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int nextNumber = rs.getInt("next_number");
                return String.format("EXP%04d", nextNumber);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating export code", e);
        }
        return "EXP0001";
    }

    /**
     * Map ResultSet to Export object
     */
    private Export mapResultSetToExport(ResultSet rs) throws SQLException {
        Export export = new Export();
        export.setExportId(rs.getInt("export_id"));
        export.setExportCode(rs.getString("export_code"));

        // Schema v11 fields
        int soId = rs.getInt("so_id");
        if (!rs.wasNull()) {
            export.setSoId(soId);
        }

        int erId = rs.getInt("er_id");
        if (!rs.wasNull()) {
            export.setErId(erId);
        }

        export.setWarehouseId(rs.getInt("warehouse_id"));
        export.setExportedBy(rs.getInt("exported_by"));
        export.setTotalQuantity(rs.getBigDecimal("total_quantity"));
        export.setStatus(rs.getString("status"));
        export.setNote(rs.getString("note"));

        if (rs.getTimestamp("export_date") != null) {
            export.setExportDate(rs.getTimestamp("export_date").toLocalDateTime());
        }

        if (rs.getTimestamp("created_at") != null) {
            export.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            export.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        if (rs.getTimestamp("deleted_at") != null) {
            export.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
        }

        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) {
            export.setCreatedBy(createdBy);
        }

        // Joined fields (Schema v11)
        export.setExportedByName(rs.getString("exported_by_name"));
        export.setWarehouseName(rs.getString("warehouse_name"));
        export.setSalesOrderCode(rs.getString("sales_order_code"));
        export.setExportRequestCode(rs.getString("export_request_code"));
        export.setCustomerName(rs.getString("customer_name"));

        return export;
    }
}
