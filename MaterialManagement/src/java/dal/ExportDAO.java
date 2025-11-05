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
     * Create a new export record and return the generated export_id
     */
    public int createExport(Export export) {
        String sql = "INSERT INTO Exports (export_code, export_date, exported_by, recipient_id, vehicle_id, export_request_id, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, export.getExportCode());
            ps.setTimestamp(2, export.getExportDate() != null ? Timestamp.valueOf(export.getExportDate()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, export.getExportedBy());
            
            if (export.getRecipientId() != null) {
                ps.setInt(4, export.getRecipientId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (export.getVehicleId() != null) {
                ps.setInt(5, export.getVehicleId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            
            if (export.getExportRequestId() != null) {
                ps.setInt(6, export.getExportRequestId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            
            ps.setString(7, export.getNote());
            
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
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "WHERE e.export_id = ?";
        
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
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
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
     * Get exports by date range
     */
    public List<Export> getExportsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Export> exports = new ArrayList<>();
        
        String sql = "SELECT e.*, " +
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "WHERE DATE(e.export_date) BETWEEN ? AND ? " +
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
     * Get exports by recipient
     */
    public List<Export> getExportsByRecipient(int recipientId) {
        List<Export> exports = new ArrayList<>();
        
        String sql = "SELECT e.*, " +
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "WHERE e.recipient_id = ? " +
                    "ORDER BY e.export_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, recipientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting exports by recipient", e);
        }
        return exports;
    }

    /**
     * Get exports by vehicle
     */
    public List<Export> getExportsByVehicle(int vehicleId) {
        List<Export> exports = new ArrayList<>();
        
        String sql = "SELECT e.*, " +
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "WHERE e.vehicle_id = ? " +
                    "ORDER BY e.export_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export export = mapResultSetToExport(rs);
                exports.add(export);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting exports by vehicle", e);
        }
        return exports;
    }

    /**
     * Search exports by code or recipient name
     */
    public List<Export> searchExports(String searchQuery) {
        List<Export> exports = new ArrayList<>();
        
        String sql = "SELECT e.*, " +
                    "r.recipient_name, r.location as recipient_location, " +
                    "v.license_plate, " +
                    "er.request_code, " +
                    "u.full_name as exported_by_name " +
                    "FROM Exports e " +
                    "LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id " +
                    "LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id " +
                    "LEFT JOIN Users u ON e.exported_by = u.user_id " +
                    "WHERE e.export_code LIKE ? OR r.recipient_name LIKE ? OR v.license_plate LIKE ? " +
                    "ORDER BY e.export_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchQuery + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
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
     * Get total count of exports
     */
    public int getTotalExportsCount() {
        String sql = "SELECT COUNT(*) FROM Exports";
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
        String sql = "UPDATE Exports SET recipient_id = ?, vehicle_id = ?, note = ?, updated_at = NOW() WHERE export_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (export.getRecipientId() != null) {
                ps.setInt(1, export.getRecipientId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            if (export.getVehicleId() != null) {
                ps.setInt(2, export.getVehicleId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            
            ps.setString(3, export.getNote());
            ps.setInt(4, export.getExportId());
            
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
                                                  String sortByRecipient, String sortByExportedBy, int page, int pageSize) {
        List<Export> exports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT e.*, ");
        sql.append("r.recipient_name, r.location as recipient_location, ");
        sql.append("v.license_plate, ");
        sql.append("er.request_code, ");
        sql.append("u.full_name as exported_by_name ");
        sql.append("FROM Exports e ");
        sql.append("LEFT JOIN Recipients r ON e.recipient_id = r.recipient_id ");
        sql.append("LEFT JOIN Vehicles v ON e.vehicle_id = v.vehicle_id ");
        sql.append("LEFT JOIN Export_Requests er ON e.export_request_id = er.export_request_id ");
        sql.append("LEFT JOIN Users u ON e.exported_by = u.user_id ");
        
        // Join Export_Details and Materials if filtering by material name
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Export_Details ed ON e.export_id = ed.export_id ");
            sql.append("JOIN Materials m ON ed.material_id = m.material_id ");
        }
        
        sql.append("WHERE 1=1 ");
        
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
        
        // Sorting
        if (sortByRecipient != null && !sortByRecipient.trim().isEmpty()) {
            sql.append("ORDER BY r.recipient_name ").append(sortByRecipient.equalsIgnoreCase("desc") ? "DESC" : "ASC").append(", e.export_date DESC ");
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
        
        sql.append("WHERE 1=1 ");
        
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
        String sql = "SELECT ed.*, m.material_name, m.material_code, m.materials_url, " +
                    "u.unit_name, wr.rack_name, wr.rack_code " +
                    "FROM Export_Details ed " +
                    "JOIN Materials m ON ed.material_id = m.material_id " +
                    "LEFT JOIN Units u ON m.unit_id = u.unit_id " +
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
                detail.setMaterialsUrl(rs.getString("materials_url"));
                detail.setUnitName(rs.getString("unit_name"));
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
        
        if (rs.getTimestamp("export_date") != null) {
            export.setExportDate(rs.getTimestamp("export_date").toLocalDateTime());
        }
        
        export.setExportedBy(rs.getInt("exported_by"));
        
        int recipientId = rs.getInt("recipient_id");
        if (!rs.wasNull()) {
            export.setRecipientId(recipientId);
        }
        
        int vehicleId = rs.getInt("vehicle_id");
        if (!rs.wasNull()) {
            export.setVehicleId(vehicleId);
        }
        
        int exportRequestId = rs.getInt("export_request_id");
        if (!rs.wasNull()) {
            export.setExportRequestId(exportRequestId);
        }
        
        export.setNote(rs.getString("note"));
        
        if (rs.getTimestamp("created_at") != null) {
            export.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        if (rs.getTimestamp("updated_at") != null) {
            export.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        // Joined fields
        export.setExportedByName(rs.getString("exported_by_name"));
        export.setRecipientName(rs.getString("recipient_name"));
        export.setRecipientLocation(rs.getString("recipient_location"));
        export.setVehicleLicensePlate(rs.getString("license_plate"));
        export.setExportRequestCode(rs.getString("request_code"));
        
        return export;
    }
}
