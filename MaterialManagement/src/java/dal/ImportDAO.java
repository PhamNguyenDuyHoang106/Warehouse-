package dal;

import entity.DBContext;
import entity.Import;
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

public class ImportDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ImportDAO.class.getName());

    /**
     * Create a new import record and return the generated import_id
     */
    public int createImport(Import importObj) {
        String sql = "INSERT INTO Imports (import_code, import_date, imported_by, supplier_id, actual_arrival, note) VALUES (?, ?, ?, ?, ?, ?)";
        
        java.sql.Connection conn = getConnection();
        if (conn == null) {
            LOGGER.log(Level.SEVERE, "Database connection is null in createImport");
            return -1;
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, importObj.getImportCode());
            ps.setTimestamp(2, importObj.getImportDate() != null ? Timestamp.valueOf(importObj.getImportDate()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, importObj.getImportedBy());
            
            if (importObj.getSupplierId() != null) {
                ps.setInt(4, importObj.getSupplierId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (importObj.getActualArrival() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(importObj.getActualArrival()));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }
            
            ps.setString(6, importObj.getNote());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating import", e);
        }
        return -1;
    }

    /**
     * Get import by ID with joined supplier and user information
     */
    public Import getImportById(int importId) {
        String sql = "SELECT i.*, s.supplier_name, u.full_name as imported_by_name " +
                    "FROM Imports i " +
                    "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                    "LEFT JOIN Users u ON i.imported_by = u.user_id " +
                    "WHERE i.import_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToImport(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import by ID: " + importId, e);
        }
        return null;
    }

    /**
     * Get all imports with pagination
     */
    public List<Import> getAllImports(int page, int pageSize) {
        List<Import> imports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT i.*, s.supplier_name, u.full_name as imported_by_name " +
                    "FROM Imports i " +
                    "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                    "LEFT JOIN Users u ON i.imported_by = u.user_id " +
                    "ORDER BY i.import_date DESC " +
                    "LIMIT ? OFFSET ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Import importObj = mapResultSetToImport(rs);
                imports.add(importObj);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all imports", e);
        }
        return imports;
    }

    /**
     * Get imports by date range
     */
    public List<Import> getImportsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Import> imports = new ArrayList<>();
        
        String sql = "SELECT i.*, s.supplier_name, u.full_name as imported_by_name " +
                    "FROM Imports i " +
                    "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                    "LEFT JOIN Users u ON i.imported_by = u.user_id " +
                    "WHERE DATE(i.import_date) BETWEEN ? AND ? " +
                    "ORDER BY i.import_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Import importObj = mapResultSetToImport(rs);
                imports.add(importObj);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting imports by date range", e);
        }
        return imports;
    }

    /**
     * Get imports by supplier
     */
    public List<Import> getImportsBySupplier(int supplierId) {
        List<Import> imports = new ArrayList<>();
        
        String sql = "SELECT i.*, s.supplier_name, u.full_name as imported_by_name " +
                    "FROM Imports i " +
                    "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                    "LEFT JOIN Users u ON i.imported_by = u.user_id " +
                    "WHERE i.supplier_id = ? " +
                    "ORDER BY i.import_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Import importObj = mapResultSetToImport(rs);
                imports.add(importObj);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting imports by supplier", e);
        }
        return imports;
    }

    /**
     * Search imports by code or supplier name
     */
    public List<Import> searchImports(String searchQuery) {
        List<Import> imports = new ArrayList<>();
        
        String sql = "SELECT i.*, s.supplier_name, u.full_name as imported_by_name " +
                    "FROM Imports i " +
                    "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id " +
                    "LEFT JOIN Users u ON i.imported_by = u.user_id " +
                    "WHERE i.import_code LIKE ? OR s.supplier_name LIKE ? " +
                    "ORDER BY i.import_date DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchQuery + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Import importObj = mapResultSetToImport(rs);
                imports.add(importObj);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching imports", e);
        }
        return imports;
    }

    /**
     * Get total count of imports
     */
    public int getTotalImportsCount() {
        String sql = "SELECT COUNT(*) FROM Imports";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total imports count", e);
        }
        return 0;
    }

    /**
     * Get total imported quantity across all imports (for dashboard)
     */
    public int getTotalImportedQuantity() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) as total FROM Import_Details";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total imported quantity", e);
        }
        return 0;
    }

    /**
     * Update import information
     */
    public boolean updateImport(Import importObj) {
        String sql = "UPDATE Imports SET supplier_id = ?, actual_arrival = ?, note = ?, updated_at = NOW() WHERE import_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (importObj.getSupplierId() != null) {
                ps.setInt(1, importObj.getSupplierId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            if (importObj.getActualArrival() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(importObj.getActualArrival()));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }
            
            ps.setString(3, importObj.getNote());
            ps.setInt(4, importObj.getImportId());
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating import", e);
            return false;
        }
    }

    /**
     * Get import history with advanced filters (for ImportHistoryServlet)
     */
    public List<Import> getImportHistoryAdvanced(String fromDate, String toDate, String materialName, 
                                                  String sortSupplier, String sortImportedBy, int page, int pageSize) {
        List<Import> imports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT i.*, s.supplier_name, u.full_name as imported_by_name ");
        sql.append("FROM Imports i ");
        sql.append("LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id ");
        sql.append("LEFT JOIN Users u ON i.imported_by = u.user_id ");
        
        // Join Import_Details and Materials if filtering by material name
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Import_Details id ON i.import_id = id.import_id ");
            sql.append("JOIN Materials m ON id.material_id = m.material_id ");
        }
        
        sql.append("WHERE 1=1 ");
        
        // Date filters
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND DATE(i.import_date) >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND DATE(i.import_date) <= ? ");
        }
        
        // Material name filter
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
        }
        
        // Sorting
        if (sortSupplier != null && !sortSupplier.trim().isEmpty()) {
            sql.append("ORDER BY s.supplier_name ").append(sortSupplier.equalsIgnoreCase("desc") ? "DESC" : "ASC").append(", i.import_date DESC ");
        } else if (sortImportedBy != null && !sortImportedBy.trim().isEmpty()) {
            sql.append("ORDER BY u.full_name ").append(sortImportedBy.equalsIgnoreCase("desc") ? "DESC" : "ASC").append(", i.import_date DESC ");
        } else {
            sql.append("ORDER BY i.import_date DESC ");
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
                Import importObj = mapResultSetToImport(rs);
                imports.add(importObj);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import history with filters", e);
        }
        return imports;
    }

    /**
     * Count import history records with advanced filters
     */
    public int countImportHistoryAdvanced(String fromDate, String toDate, String materialName, String sortSupplier) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(DISTINCT i.import_id) as total ");
        sql.append("FROM Imports i ");
        
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Import_Details id ON i.import_id = id.import_id ");
            sql.append("JOIN Materials m ON id.material_id = m.material_id ");
        }
        
        sql.append("WHERE 1=1 ");
        
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND DATE(i.import_date) >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND DATE(i.import_date) <= ? ");
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
            LOGGER.log(Level.SEVERE, "Error counting import history", e);
        }
        return 0;
    }

    /**
     * Generate next import code (e.g., IMP0001, IMP0002)
     */
    public String generateNextImportCode() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(import_code, 4) AS SIGNED)), 0) + 1 AS next_number FROM Imports WHERE import_code LIKE 'IMP%'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int nextNumber = rs.getInt("next_number");
                return String.format("IMP%04d", nextNumber);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating import code", e);
        }
        return "IMP0001";
    }

    /**
     * Get import details by import ID
     */
    public List<entity.ImportDetail> getImportDetailsByImportId(int importId) throws SQLException {
        List<entity.ImportDetail> details = new ArrayList<>();
        String sql = "SELECT id.*, m.material_name, m.material_code, m.materials_url, " +
                    "u.unit_name, wr.rack_name, wr.rack_code " +
                    "FROM Import_Details id " +
                    "JOIN Materials m ON id.material_id = m.material_id " +
                    "LEFT JOIN Units u ON m.unit_id = u.unit_id " +
                    "LEFT JOIN Warehouse_Racks wr ON id.rack_id = wr.rack_id " +
                    "WHERE id.import_id = ? " +
                    "ORDER BY id.import_detail_id";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entity.ImportDetail detail = new entity.ImportDetail();
                detail.setImportDetailId(rs.getInt("import_detail_id"));
                detail.setImportId(rs.getInt("import_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setQuantity(rs.getBigDecimal("quantity"));
                detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                
                Integer rackId = rs.getObject("rack_id", Integer.class);
                detail.setRackId(rackId);
                
                // Note field may not exist in all schemas
                try {
                    detail.setNote(rs.getString("note"));
                } catch (SQLException e) {
                    // Column may not exist
                    detail.setNote(null);
                }
                
                // Status field
                try {
                    detail.setStatus(rs.getString("status"));
                } catch (SQLException e) {
                    // Column may not exist
                    detail.setStatus(null);
                }
                
                // Created_at field
                try {
                    if (rs.getTimestamp("created_at") != null) {
                        detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
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
     * Map ResultSet to Import object
     */
    private Import mapResultSetToImport(ResultSet rs) throws SQLException {
        Import importObj = new Import();
        importObj.setImportId(rs.getInt("import_id"));
        importObj.setImportCode(rs.getString("import_code"));
        
        if (rs.getTimestamp("import_date") != null) {
            importObj.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
        }
        
        importObj.setImportedBy(rs.getInt("imported_by"));
        
        int supplierId = rs.getInt("supplier_id");
        if (!rs.wasNull()) {
            importObj.setSupplierId(supplierId);
        }
        
        if (rs.getTimestamp("actual_arrival") != null) {
            importObj.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
        }
        
        importObj.setNote(rs.getString("note"));
        
        if (rs.getTimestamp("created_at") != null) {
            importObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        
        if (rs.getTimestamp("updated_at") != null) {
            importObj.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        // Joined fields
        importObj.setSupplierName(rs.getString("supplier_name"));
        importObj.setImportedByName(rs.getString("imported_by_name"));
        
        return importObj;
    }
}
