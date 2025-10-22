package dal;

import entity.DBContext;
import entity.Import;
import entity.ImportDetail;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ImportDAO.class.getName());

    public int createImport(Import importData) throws SQLException {
        String sql = "INSERT INTO Imports (import_code, import_date, imported_by, supplier_id, actual_arrival, note) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, importData.getImportCode());
            stmt.setObject(2, importData.getImportDate());
            stmt.setInt(3, importData.getImportedBy());
            if (importData.getSupplierId() != null) {
                stmt.setInt(4, importData.getSupplierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setString(6, importData.getNote());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating import failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating import failed, no ID obtained.");
                }
            }
        }
    }

    public void createImportDetails(List<ImportDetail> details) throws SQLException {
        String insertSql = "INSERT INTO Import_Details (import_id, material_id, quantity, unit_price, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            for (ImportDetail detail : details) {
                if (detail.getQuantity() <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
                }

                ImportDetail existingDetail = getImportDetailByMaterialAndPrice(detail.getImportId(), detail.getMaterialId(), detail.getUnitPrice());
                if (existingDetail != null) {
                    int newQuantity = existingDetail.getQuantity() + detail.getQuantity();
                    updateImportDetailQuantity(existingDetail.getImportDetailId(), newQuantity);
                } else {
                    insertStmt.setInt(1, detail.getImportId());
                    insertStmt.setInt(2, detail.getMaterialId());
                    insertStmt.setInt(3, detail.getQuantity());
                    insertStmt.setDouble(4, detail.getUnitPrice());
                    insertStmt.setString(5, detail.getStatus());
                    insertStmt.setObject(6, detail.getCreatedAt());
                    insertStmt.addBatch();
                }
            }
            int[] affectedRows = insertStmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    if (insertStmt.getUpdateCount() > 0) {
                        throw new SQLException("Adding material to import failed.");
                    }
                }
            }
        }
    }

    public ImportDetail getImportDetailByMaterial(int importId, int materialId) throws SQLException {
        String sql = "SELECT * FROM Import_Details WHERE import_id = ? AND material_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            stmt.setInt(2, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setStatus(rs.getString("status"));
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    return detail;
                }
            }
        }
        return null;
    }

    public void updateImportDetailQuantity(int importDetailId, int newQuantity) throws SQLException {
        String sql = "UPDATE Import_Details SET quantity = ? WHERE import_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, importDetailId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating import detail quantity failed for import detail ID: " + importDetailId);
            }
        }
    }

    public void updateImportDetailPrice(int importDetailId, double newUnitPrice) throws SQLException {
        String sql = "UPDATE Import_Details SET unit_price = ? WHERE import_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newUnitPrice);
            stmt.setInt(2, importDetailId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating import detail price failed for import detail ID: " + importDetailId);
            }
        }
    }

    public void confirmImport(int importId) throws SQLException {
        String sql = "UPDATE Import_Details SET status = 'imported', created_at = CURRENT_TIMESTAMP WHERE import_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft import details found to confirm for import ID: " + importId);
            }
        }
    }

    public List<ImportDetail> getDraftImportDetails(int importId) throws SQLException {
        List<ImportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Import_Details WHERE import_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setStatus(rs.getString("status"));
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void removeImportDetail(int importId, int materialId, int quantity) throws SQLException {
        String sql = "DELETE FROM Import_Details WHERE import_id = ? AND material_id = ? AND quantity = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantity);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft import detail found to remove for import ID: " + importId);
            }
        }
    }

    public void updateImportDetailsStatus(int importId, String status) throws SQLException {
        String sql = "UPDATE Import_Details SET status = ? WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, importId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import details updated for import ID: " + importId);
            }
        }
    }

    public void updateImport(Import importData) throws SQLException {
        String sql = "UPDATE Imports SET import_code = ?, import_date = ?, imported_by = ?, supplier_id = ?, actual_arrival = ?, note = ? WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, importData.getImportCode());
            stmt.setObject(2, importData.getImportDate());
            stmt.setInt(3, importData.getImportedBy());
            if (importData.getSupplierId() != null) {
                stmt.setInt(4, importData.getSupplierId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (importData.getActualArrival() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(importData.getActualArrival()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            stmt.setString(6, importData.getNote());
            stmt.setInt(7, importData.getImportId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import updated for import ID: " + importData.getImportId());
            }
        }
    }

    public void updateInventoryByImportId(int importId, int updatedBy, String location) throws SQLException {
        InventoryDAO inventoryDAO = new InventoryDAO();
        List<ImportDetail> details = getImportDetailsByImportId(importId);
        inventoryDAO.updateInventoryForImport(details, updatedBy, location);
    }

    public List<ImportDetail> getImportDetailsByImportId(int importId) throws SQLException {
        List<ImportDetail> details = new ArrayList<>();
        String sql = "SELECT d.*, m.material_name, m.materials_url, u.unit_name "
                + "FROM Import_Details d "
                + "JOIN Materials m ON d.material_id = m.material_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "WHERE d.import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setStatus(rs.getString("status"));
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    detail.setMaterialName(rs.getString("material_name"));
                    detail.setMaterialsUrl(rs.getString("materials_url"));
                    detail.setUnitName(rs.getString("unit_name"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void deleteImport(int importId) throws SQLException {
        String sql = "DELETE FROM Imports WHERE import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No import found to delete for import ID: " + importId);
            }
        }
    }

    public Import getImportById(int importId) {
        String sql = "SELECT i.*, u.full_name AS importedByName, s.supplier_name AS supplierName "
                + "FROM Imports i "
                + "LEFT JOIN Users u ON i.imported_by = u.user_id "
                + "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id "
                + "WHERE i.import_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Import importData = new Import();
                    importData.setImportId(rs.getInt("import_id"));
                    importData.setImportCode(rs.getString("import_code"));
                    if (rs.getTimestamp("import_date") != null) {
                        importData.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    }
                    importData.setImportedBy(rs.getInt("imported_by"));
                    importData.setSupplierId(rs.getInt("supplier_id"));
                    // Bỏ destination vì database không có
                    if (rs.getTimestamp("actual_arrival") != null) {
                        importData.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
                    }
                    importData.setNote(rs.getString("note"));
                    importData.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    importData.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    importData.setImportedByName(rs.getString("importedByName"));
                    importData.setSupplierName(rs.getString("supplierName"));
                    return importData;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import by ID: " + importId, e);
        }
        return null;
    }

    public List<Import> getImports(LocalDateTime startDate, LocalDateTime endDate, int pageIndex, int pageSize) throws SQLException {
        List<Import> imports = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Imports WHERE 1=1 AND EXISTS (SELECT 1 FROM Import_Details d WHERE d.import_id = Imports.import_id AND d.status = 'imported')");
        List<Object> params = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND import_date >= ?");
            params.add(Timestamp.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append(" AND import_date <= ?");
            params.add(Timestamp.valueOf(endDate));
        }

        sql.append(" ORDER BY import_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageIndex - 1) * pageSize);

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Import importData = new Import();
                    importData.setImportId(rs.getInt("import_id"));
                    importData.setImportCode(rs.getString("import_code"));
                    importData.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                    importData.setImportedBy(rs.getInt("imported_by"));
                    importData.setSupplierId(rs.getInt("supplier_id"));
                    // Bỏ destination vì database không có
                    if (rs.getTimestamp("actual_arrival") != null) {
                        importData.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
                    }
                    importData.setNote(rs.getString("note"));
                    importData.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    importData.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    imports.add(importData);
                }
            }
        }
        return imports;
    }

    public int getTotalImportedQuantity() throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_imported FROM Import_Details WHERE status = 'imported'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_imported");
            }
        }
        return 0;
    }

    public List<Import> getImportHistoryAdvanced(String fromDate, String toDate, String materialName, String sortSupplier, String sortImportedBy, int page, int pageSize) {
        List<Import> list = new ArrayList<>();
        String sql = "SELECT DISTINCT i.*, u.full_name as importedByName, s.supplier_name, "
                + "(SELECT SUM(quantity) FROM Import_Details d WHERE d.import_id = i.import_id AND d.status = 'imported') as totalQuantity, "
                + "(SELECT SUM(quantity * unit_price) FROM Import_Details d WHERE d.import_id = i.import_id AND d.status = 'imported') as totalValue "
                + "FROM Imports i "
                + "LEFT JOIN Users u ON i.imported_by = u.user_id "
                + "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id "
                + "LEFT JOIN Import_Details idt ON i.import_id = idt.import_id "
                + "LEFT JOIN Materials m ON idt.material_id = m.material_id "
                + "WHERE 1=1 AND EXISTS (SELECT 1 FROM Import_Details d WHERE d.import_id = i.import_id AND d.status = 'imported') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND i.import_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND i.import_date <= ? ";
            params.add(toDate + " 23:59:59");
        }
        if (materialName != null && !materialName.isEmpty()) {
            sql += "AND m.material_name LIKE ? ";
            params.add("%" + materialName + "%");
        }
        // Add sorting by supplier and imported by
        StringBuilder orderByClause = new StringBuilder("ORDER BY ");
        boolean hasSorting = false;
        if (sortSupplier != null && !sortSupplier.isEmpty()) {
            if (sortSupplier.equals("A-Z")) {
                orderByClause.append("s.supplier_name ASC");
                hasSorting = true;
            } else if (sortSupplier.equals("Z-A")) {
                orderByClause.append("s.supplier_name DESC");
                hasSorting = true;
            }
        }
        if (sortImportedBy != null && !sortImportedBy.isEmpty()) {
            if (hasSorting) {
                orderByClause.append(", ");
            }
            if (sortImportedBy.equals("A-Z")) {
                orderByClause.append("u.full_name ASC");
            } else if (sortImportedBy.equals("Z-A")) {
                orderByClause.append("u.full_name DESC");
            }
            hasSorting = true;
        }
        if (!hasSorting) {
            orderByClause.append("i.import_date DESC");
        }
        sql += orderByClause.toString() + " LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Import imp = new Import();
                imp.setImportId(rs.getInt("import_id"));
                imp.setImportCode(rs.getString("import_code"));
                if (rs.getTimestamp("import_date") != null) {
                    imp.setImportDate(rs.getTimestamp("import_date").toLocalDateTime());
                }
                imp.setImportedBy(rs.getInt("imported_by"));
                imp.setSupplierId(rs.getInt("supplier_id"));
                // Bỏ destination vì database không có
                if (rs.getTimestamp("actual_arrival") != null) {
                    imp.setActualArrival(rs.getTimestamp("actual_arrival").toLocalDateTime());
                }
                imp.setNote(rs.getString("note"));
                imp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                imp.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                imp.setImportedByName(rs.getString("importedByName"));
                imp.setSupplierName(rs.getString("supplier_name"));
                imp.setTotalQuantity(rs.getInt("totalQuantity"));
                imp.setTotalValue(rs.getDouble("totalValue"));
                list.add(imp);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting import history advanced: ", e);
        }
        return list;
    }

    public int countImportHistoryAdvanced(String fromDate, String toDate, String materialName, String sortSupplier) {
        int count = 0;
        String sql = "SELECT COUNT(DISTINCT i.import_id) FROM Imports i "
                + "LEFT JOIN Suppliers s ON i.supplier_id = s.supplier_id "
                + "LEFT JOIN Import_Details idt ON i.import_id = idt.import_id "
                + "LEFT JOIN Materials m ON idt.material_id = m.material_id WHERE 1=1 "
                + "AND EXISTS (SELECT 1 FROM Import_Details d WHERE d.import_id = i.import_id AND d.status = 'imported') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND i.import_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND i.import_date <= ? ";
            params.add(toDate + " 23:59:59");
        }
        if (materialName != null && !materialName.isEmpty()) {
            sql += "AND m.material_name LIKE ? ";
            params.add("%" + materialName + "%");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting import history advanced: ", e);
        }
        return count;
    }

    public String generateNextImportCode() throws SQLException {
        String sql = "SELECT MAX(import_id) FROM Imports";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return String.format("IMP-%05d", nextId);
        }
    }

    public ImportDetail getImportDetailByMaterialAndPrice(int importId, int materialId, double unitPrice) throws SQLException {
        String sql = "SELECT * FROM Import_Details WHERE import_id = ? AND material_id = ? AND unit_price = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importId);
            stmt.setInt(2, materialId);
            stmt.setDouble(3, unitPrice);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportDetailId(rs.getInt("import_detail_id"));
                    detail.setImportId(rs.getInt("import_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setUnitPrice(rs.getDouble("unit_price"));
                    detail.setStatus(rs.getString("status"));
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    detail.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    return detail;
                }
            }
        }
        return null;
    }
}