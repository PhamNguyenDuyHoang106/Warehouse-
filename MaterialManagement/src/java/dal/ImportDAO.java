package dal;

import entity.DBContext;
import entity.Import;
import entity.ImportDetail;
import entity.Supplier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ImportDAO.class.getName());

    private static final String BASE_SELECT =
            "SELECT i.import_id, i.import_code, i.po_id, i.warehouse_id, i.import_date, " +
            "i.received_by, i.total_quantity, i.total_amount, i.status, i.note, i.created_at, i.created_by, " +
            "po.po_code, po.supplier_id, s.supplier_name, w.warehouse_name, " +
            "creator.full_name AS created_by_name, receiver.full_name AS received_by_name " +
            "FROM Imports i " +
            "LEFT JOIN Purchase_Orders po ON i.po_id = po.po_id " +
            "LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id " +
            "LEFT JOIN Warehouses w ON i.warehouse_id = w.warehouse_id " +
            "LEFT JOIN Users creator ON i.created_by = creator.user_id " +
            "LEFT JOIN Users receiver ON i.received_by = receiver.user_id ";

    /**
     * Create a new import record and return generated ID.
     */
    public int createImport(Import importObj) {
        final String sql = "INSERT INTO Imports " +
                "(import_code, po_id, warehouse_id, import_date, received_by, total_quantity, total_amount, status, note, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, importObj.getImportCode());
            ps.setInt(2, importObj.getPoId());
            ps.setInt(3, importObj.getWarehouseId());

            LocalDate importDate = importObj.getImportDate() != null ? importObj.getImportDate() : LocalDate.now();
            ps.setDate(4, Date.valueOf(importDate));

            ps.setInt(5, importObj.getReceivedBy() != null ? importObj.getReceivedBy() : importObj.getCreatedBy());
            ps.setBigDecimal(6, defaultBigDecimal(importObj.getTotalQuantity()));
            ps.setBigDecimal(7, defaultBigDecimal(importObj.getTotalAmount()));
            ps.setString(8, importObj.getStatus() != null ? importObj.getStatus() : "draft");
            ps.setString(9, importObj.getNote());
            ps.setInt(10, importObj.getCreatedBy());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating import", e);
        }
        return -1;
    }

    public boolean updateImport(Import importObj) {
        final String sql = "UPDATE Imports SET po_id = ?, warehouse_id = ?, import_date = ?, received_by = ?, " +
                "total_quantity = ?, total_amount = ?, status = ?, note = ? WHERE import_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importObj.getPoId());
            ps.setInt(2, importObj.getWarehouseId());
            ps.setDate(3, Date.valueOf(importObj.getImportDate()));
            ps.setInt(4, importObj.getReceivedBy());
            ps.setBigDecimal(5, defaultBigDecimal(importObj.getTotalQuantity()));
            ps.setBigDecimal(6, defaultBigDecimal(importObj.getTotalAmount()));
            ps.setString(7, importObj.getStatus());
            ps.setString(8, importObj.getNote());
            ps.setInt(9, importObj.getImportId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating import", e);
            return false;
        }
    }

    public Import getImportById(int importId) {
        final String sql = BASE_SELECT + "WHERE i.import_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToImport(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import by ID: " + importId, e);
        }
        return null;
    }

    public List<Import> getAllImports(int page, int pageSize) {
        List<Import> imports = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        final String sql = BASE_SELECT + "ORDER BY i.import_date DESC LIMIT ? OFFSET ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imports.add(mapResultSetToImport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting imports", e);
        }
        return imports;
    }

    public List<Import> getImportsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Import> imports = new ArrayList<>();
        final String sql = BASE_SELECT + "WHERE i.import_date BETWEEN ? AND ? ORDER BY i.import_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imports.add(mapResultSetToImport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting imports by date range", e);
        }
        return imports;
    }

    public List<Import> getImportsBySupplier(int supplierId) {
        List<Import> imports = new ArrayList<>();
        final String sql = BASE_SELECT + "WHERE po.supplier_id = ? ORDER BY i.import_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imports.add(mapResultSetToImport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting imports by supplier", e);
        }
        return imports;
    }

    public List<Import> searchImports(String searchQuery) {
        List<Import> imports = new ArrayList<>();
        final String sql = BASE_SELECT +
                "WHERE i.import_code LIKE ? OR s.supplier_name LIKE ? " +
                "ORDER BY i.import_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String keyword = "%" + searchQuery + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imports.add(mapResultSetToImport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching imports", e);
        }
        return imports;
    }

    public int getTotalImportsCount() {
        final String sql = "SELECT COUNT(*) FROM Imports";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting imports", e);
        }
        return 0;
    }

    public int getTotalImportedQuantity() {
        final String sql = "SELECT COALESCE(SUM(total_quantity), 0) AS total FROM Imports";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total").intValue();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total imported quantity", e);
        }
        return 0;
    }

    public List<Import> getImportHistoryAdvanced(String fromDate, String toDate, String materialName,
                                                 String sortSupplier, String sortReceivedBy,
                                                 Integer supplierId, Integer warehouseId,
                                                 String status, String searchKeyword,
                                                 int page, int pageSize) {
        List<Import> imports = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Import_Details id ON i.import_id = id.import_id ");
            sql.append("JOIN Materials m ON id.material_id = m.material_id ");
        }
        sql.append("WHERE 1=1 ");
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND i.import_date >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND i.import_date <= ? ");
        }
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
        }
        if (supplierId != null) {
            sql.append("AND po.supplier_id = ? ");
        }
        if (warehouseId != null) {
            sql.append("AND i.warehouse_id = ? ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (i.import_code LIKE ? OR po.po_code LIKE ? OR s.supplier_name LIKE ?) ");
        }

        if (sortSupplier != null && !sortSupplier.trim().isEmpty()) {
            sql.append("ORDER BY s.supplier_name ").append(sortSupplier.equalsIgnoreCase("desc") ? "DESC" : "ASC");
            sql.append(", i.import_date DESC ");
        } else if (sortReceivedBy != null && !sortReceivedBy.trim().isEmpty()) {
            sql.append("ORDER BY receiver.full_name ").append(sortReceivedBy.equalsIgnoreCase("desc") ? "DESC" : "ASC");
            sql.append(", i.import_date DESC ");
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
            if (supplierId != null) {
                ps.setInt(paramIndex++, supplierId);
            }
            if (warehouseId != null) {
                ps.setInt(paramIndex++, warehouseId);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String pattern = "%" + searchKeyword.trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    imports.add(mapResultSetToImport(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import history", e);
        }
        return imports;
    }

    public int countImportHistoryAdvanced(String fromDate, String toDate, String materialName,
                                          Integer supplierId, Integer warehouseId,
                                          String status, String searchKeyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT i.import_id) AS total FROM Imports i ");
        sql.append("LEFT JOIN Purchase_Orders po ON i.po_id = po.po_id ");
        sql.append("LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id ");
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("JOIN Import_Details id ON i.import_id = id.import_id ");
            sql.append("JOIN Materials m ON id.material_id = m.material_id ");
        }
        sql.append("WHERE 1=1 ");
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND i.import_date >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND i.import_date <= ? ");
        }
        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
        }
        if (supplierId != null) {
            sql.append("AND po.supplier_id = ? ");
        }
        if (warehouseId != null) {
            sql.append("AND i.warehouse_id = ? ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (i.import_code LIKE ? OR po.po_code LIKE ? OR s.supplier_name LIKE ?) ");
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
            if (supplierId != null) {
                ps.setInt(paramIndex++, supplierId);
            }
            if (warehouseId != null) {
                ps.setInt(paramIndex++, warehouseId);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.trim());
            }
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String pattern = "%" + searchKeyword.trim() + "%";
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
                ps.setString(paramIndex++, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting import history", e);
        }
        return 0;
    }

    public List<ImportDetail> getImportDetailsByImportId(int importId) {
        ImportDetailDAO detailDAO = new ImportDetailDAO();
        try {
            return detailDAO.getDetailsByImportId(importId);
        } finally {
            detailDAO.close();
        }
    }

    public String generateNextImportCode() {
        final String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(import_code, 4) AS SIGNED)), 0) + 1 AS next_number " +
                "FROM Imports WHERE import_code LIKE 'IMP%'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return String.format("IMP%04d", rs.getInt("next_number"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating import code", e);
        }
        return "IMP0001";
    }

    private Import mapResultSetToImport(ResultSet rs) throws SQLException {
        Import importObj = new Import();
        importObj.setImportId(rs.getInt("import_id"));
        importObj.setImportCode(rs.getString("import_code"));
        importObj.setPoId(rs.getObject("po_id", Integer.class));
        importObj.setWarehouseId(rs.getObject("warehouse_id", Integer.class));

        Date importDate = rs.getDate("import_date");
        if (importDate != null) {
            importObj.setImportDate(importDate.toLocalDate());
        }

        importObj.setReceivedBy(rs.getObject("received_by", Integer.class));
        importObj.setCreatedBy(rs.getInt("created_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            importObj.setCreatedAt(createdAt.toLocalDateTime());
        } else {
            importObj.setCreatedAt(LocalDateTime.now());
        }

        importObj.setStatus(rs.getString("status"));
        importObj.setNote(rs.getString("note"));
        importObj.setTotalQuantity(rs.getBigDecimal("total_quantity"));
        importObj.setTotalAmount(rs.getBigDecimal("total_amount"));

        importObj.setSupplierId(rs.getObject("supplier_id", Integer.class));
        importObj.setSupplierName(rs.getString("supplier_name"));
        importObj.setWarehouseName(rs.getString("warehouse_name"));
        importObj.setCreatedByName(rs.getString("created_by_name"));
        importObj.setReceivedByName(rs.getString("received_by_name"));
        importObj.setPoCode(rs.getString("po_code"));

        return importObj;
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
    
    /**
     * Get all unique suppliers for an import by checking:
     * 1. Suppliers from Material_Supplier_Prices for materials in Import_Details
     * 2. Fallback to PO's main supplier if no suppliers found from Material_Supplier_Prices
     */
    public List<Supplier> getAllSuppliersForImport(int importId) {
        List<Supplier> suppliers = new ArrayList<>();
        Set<Integer> supplierIds = new LinkedHashSet<>();
        
        String sql = "SELECT DISTINCT " +
                    "COALESCE(msp.supplier_id, po.supplier_id) AS supplier_id, " +
                    "COALESCE(s2.supplier_name, s1.supplier_name) AS supplier_name, " +
                    "COALESCE(s2.supplier_code, s1.supplier_code) AS supplier_code, " +
                    "COALESCE(s2.contact_person, s1.contact_person) AS contact_person, " +
                    "COALESCE(s2.phone, s1.phone) AS phone, " +
                    "COALESCE(s2.email, s1.email) AS email, " +
                    "COALESCE(s2.tax_code, s1.tax_code) AS tax_code, " +
                    "COALESCE(s2.address, s1.address) AS address " +
                    "FROM Import_Details id " +
                    "INNER JOIN Purchase_Order_Details pod ON id.po_detail_id = pod.po_detail_id " +
                    "INNER JOIN Purchase_Orders po ON pod.po_id = po.po_id " +
                    "LEFT JOIN Material_Supplier_Prices msp ON id.material_id = msp.material_id " +
                    "    AND id.unit_id = msp.unit_id " +
                    "    AND msp.is_current = 1 " +
                    "LEFT JOIN Suppliers s1 ON po.supplier_id = s1.supplier_id " +
                    "LEFT JOIN Suppliers s2 ON msp.supplier_id = s2.supplier_id " +
                    "WHERE id.import_id = ? " +
                    "AND (msp.supplier_id IS NOT NULL OR po.supplier_id IS NOT NULL) " +
                    "ORDER BY supplier_name";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer supplierId = rs.getObject("supplier_id", Integer.class);
                    if (supplierId != null && !supplierIds.contains(supplierId)) {
                        supplierIds.add(supplierId);
                        Supplier supplier = new Supplier();
                        supplier.setSupplierId(supplierId);
                        supplier.setSupplierCode(rs.getString("supplier_code"));
                        supplier.setSupplierName(rs.getString("supplier_name"));
                        supplier.setContactPerson(rs.getString("contact_person"));
                        supplier.setPhone(rs.getString("phone"));
                        supplier.setEmail(rs.getString("email"));
                        supplier.setTaxCode(rs.getString("tax_code"));
                        supplier.setAddress(rs.getString("address"));
                        suppliers.add(supplier);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all suppliers for import ID: " + importId, e);
        }
        
        // If no suppliers found from Material_Supplier_Prices, get PO's main supplier
        if (suppliers.isEmpty()) {
            Import importObj = getImportById(importId);
            if (importObj != null && importObj.getSupplierId() != null) {
                SupplierDAO supplierDAO = new SupplierDAO();
                try {
                    Supplier mainSupplier = supplierDAO.getSupplierByID(importObj.getSupplierId());
                    if (mainSupplier != null) {
                        suppliers.add(mainSupplier);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error getting PO main supplier for import ID: " + importId, e);
                } finally {
                    supplierDAO.close();
                }
            }
        }
        
        return suppliers;
    }
    
    /**
     * Get comma-separated supplier names for an import (used in Import List)
     */
    public String getSupplierNamesForImport(int importId) {
        List<Supplier> suppliers = getAllSuppliersForImport(importId);
        if (suppliers.isEmpty()) {
            return "N/A";
        }
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < suppliers.size(); i++) {
            if (i > 0) {
                names.append(", ");
            }
            String name = suppliers.get(i).getSupplierName();
            if (name != null && !name.trim().isEmpty()) {
                names.append(name);
            }
        }
        return names.length() > 0 ? names.toString() : "N/A";
    }
}
