package dal;

import entity.DBContext;
import entity.Export;
import entity.ExportDetail;
import entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportDAO extends DBContext {

    private int getNextExportNumber() throws SQLException {
        String sql = "SELECT MAX(export_id) AS max_id FROM Exports";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
            return 1;
        }
    }

    private String generateExportCode(int exportId) throws SQLException {
        return String.format("EXP%06d", exportId);
    }

    public int createExport(Export export) throws SQLException {
        String sql = "INSERT INTO Exports (export_code, export_date, exported_by, recipient_user_id, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int exportId = getNextExportNumber();
            stmt.setString(1, generateExportCode(exportId));
            stmt.setObject(2, export.getExportDate());
            stmt.setInt(3, export.getExportedBy());
            stmt.setInt(4, export.getRecipientUserId());
            stmt.setString(5, export.getNote());
            stmt.setObject(6, export.getCreatedAt());
            stmt.setObject(7, export.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating export failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating export failed, no ID obtained.");
                }
            }
        }
    }

    public String getExportCode(int exportId) throws SQLException {
        String sql = "SELECT export_code FROM Exports WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("export_code");
                }
            }
        }
        throw new SQLException("Export code not found for export ID: " + exportId);
    }

    public void addMaterialToExport(ExportDetail detail) throws SQLException {
        if (detail.getQuantity() <= 0) {
            throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
        }

        String sql = "INSERT INTO Export_Details (export_id, material_id, quantity, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, detail.getExportId());
            stmt.setInt(2, detail.getMaterialId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setString(4, "draft");
            stmt.setObject(5, detail.getCreatedAt());
            stmt.setObject(6, detail.getUpdatedAt());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding material to export failed.");
            }
        }
    }

    public void createExportDetails(List<ExportDetail> details) throws SQLException {
        String insertSql = "INSERT INTO Export_Details (export_id, material_id, quantity, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            for (ExportDetail detail : details) {
                if (detail.getQuantity() <= 0) {
                    throw new SQLException("Quantity must be greater than 0 for material ID: " + detail.getMaterialId());
                }

                ExportDetail existingDetail = getExportDetailByMaterial(detail.getExportId(), detail.getMaterialId());
                if (existingDetail != null) {
                    int newQuantity = existingDetail.getQuantity() + detail.getQuantity();
                    updateExportDetailQuantity(existingDetail.getExportDetailId(), newQuantity);
                } else {
                    insertStmt.setInt(1, detail.getExportId());
                    insertStmt.setInt(2, detail.getMaterialId());
                    insertStmt.setInt(3, detail.getQuantity());
                    insertStmt.setString(4, detail.getStatus());
                    insertStmt.setObject(5, detail.getCreatedAt());
                    insertStmt.setObject(6, detail.getUpdatedAt());
                    insertStmt.addBatch();
                }
            }
            int[] affectedRows = insertStmt.executeBatch();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    if (insertStmt.getUpdateCount() > 0) {
                        throw new SQLException("Adding material to export failed.");
                    }
                }
            }
        }
    }

    public ExportDetail getExportDetailByMaterial(int exportId, int materialId) throws SQLException {
        String sql = "SELECT * FROM Export_Details WHERE export_id = ? AND material_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            stmt.setInt(2, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return detail;
                }
            }
        }
        return null;
    }

    public void updateExportDetailQuantity(int exportDetailId, int newQuantity) throws SQLException {
        String sql = "UPDATE Export_Details SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE export_detail_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, exportDetailId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating export detail quantity failed for export detail ID: " + exportDetailId);
            }
        }
    }

    public void confirmExport(int exportId) throws SQLException {
        String sql = "UPDATE Export_Details SET status = 'exported', updated_at = CURRENT_TIMESTAMP WHERE export_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft export details found to confirm for export ID: " + exportId);
            }
        }
    }

    public List<ExportDetail> getDraftExportDetails(int exportId) throws SQLException {
        List<ExportDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM Export_Details WHERE export_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void removeExportDetail(int exportId, int materialId, int quantity) throws SQLException {
        String sql = "DELETE FROM Export_Details WHERE export_id = ? AND material_id = ? AND quantity = ? AND status = 'draft'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantity);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No draft export detail found to remove for export ID: " + exportId);
            }
        }
    }

    public void updateExportDetailsStatus(int exportId, String status) throws SQLException {
        String sql = "UPDATE Export_Details SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export details updated for export ID: " + exportId);
            }
        }
    }

    public void updateExport(Export export) throws SQLException {
        String sql = "UPDATE Exports SET export_date = ?, exported_by = ?, recipient_user_id = ?, note = ?, updated_at = ? WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, export.getExportDate());
            stmt.setInt(2, export.getExportedBy());
            stmt.setInt(3, export.getRecipientUserId());
            stmt.setString(4, export.getNote());
            stmt.setObject(5, export.getUpdatedAt());
            stmt.setInt(6, export.getExportId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export updated for export ID: " + export.getExportId());
            }
        }
    }

    public void updateInventoryByExportId(int exportId, int updatedBy) throws SQLException {
        InventoryDAO inventoryDAO = new InventoryDAO();
        List<ExportDetail> details = getExportDetailsByExportId(exportId);
        inventoryDAO.updateInventory(details, updatedBy);
    }

    public List<ExportDetail> getExportDetailsByExportId(int exportId) throws SQLException {
        List<ExportDetail> details = new ArrayList<>();
        String sql = "SELECT d.*, m.material_name, m.materials_url, u.unit_name "
                + "FROM Export_Details d "
                + "JOIN Materials m ON d.material_id = m.material_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "WHERE d.export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ExportDetail detail = new ExportDetail();
                    detail.setExportDetailId(rs.getInt("export_detail_id"));
                    detail.setExportId(rs.getInt("export_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setStatus(rs.getString("status"));
                    detail.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    detail.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    detail.setMaterialName(rs.getString("material_name"));
                    detail.setMaterialsUrl(rs.getString("materials_url"));
                    detail.setUnitName(rs.getString("unit_name"));
                    details.add(detail);
                }
            }
        }
        return details;
    }

    public void deleteExport(int exportId) throws SQLException {
        String sql = "DELETE FROM Exports WHERE export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No export found to delete for export ID: " + exportId);
            }
        }
    }

    public int getTotalExportedQuantity() throws SQLException {
        String sql = "SELECT SUM(quantity) AS total_exported FROM Export_Details WHERE status = 'exported'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_exported");
            }
        }
        return 0;
    }

    public List<User> getUsersByRoleIds(List<Integer> roleIds) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.user_id, u.full_name, u.department_id, d.department_name "
                + "FROM Users u "
                + "LEFT JOIN Departments d ON u.department_id = d.department_id "
                + "WHERE u.role_id IN (";

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roleIds.size(); i++) {
            placeholders.append("?");
            if (i < roleIds.size() - 1) {
                placeholders.append(",");
            }
        }
        query += placeholders.toString() + ") AND u.status = 'active'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < roleIds.size(); i++) {
                stmt.setInt(i + 1, roleIds.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setDepartmentName(rs.getString("department_name"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    public List<Export> getExportHistory(String fromDate, String toDate, String exportCode, String recipientId, int page, int pageSize) {
        List<Export> list = new ArrayList<>();
        String sql = "SELECT e.*, u1.full_name as exportedByName, u2.full_name as recipientName, "
                + "(SELECT SUM(quantity) FROM Export_Details d WHERE d.export_id = e.export_id AND d.status != 'draft') as totalQuantity, "
                + "0 as totalValue "
                + "FROM Exports e "
                + "LEFT JOIN Users u1 ON e.exported_by = u1.user_id "
                + "LEFT JOIN Users u2 ON e.recipient_user_id = u2.user_id "
                + "WHERE 1=1 AND EXISTS (SELECT 1 FROM Export_Details d WHERE d.export_id = e.export_id AND d.status != 'draft') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND e.export_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND e.export_date <= ? ";
            params.add(toDate + " 23:59:59");
        }
        if (exportCode != null && !exportCode.isEmpty()) {
            sql += "AND e.export_code LIKE ? ";
            params.add("%" + exportCode + "%");
        }
        if (recipientId != null && !recipientId.isEmpty()) {
            sql += "AND e.recipient_user_id = ? ";
            params.add(Integer.parseInt(recipientId));
        }
        sql += "ORDER BY e.export_date DESC LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export exp = new Export();
                exp.setExportId(rs.getInt("export_id"));
                exp.setExportCode(rs.getString("export_code"));
                if (rs.getTimestamp("export_date") != null) {
                    exp.setExportDate(rs.getTimestamp("export_date").toLocalDateTime());
                }
                exp.setExportedBy(rs.getInt("exported_by"));
                exp.setRecipientUserId(rs.getInt("recipient_user_id"));
                exp.setNote(rs.getString("note"));
                exp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                exp.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                exp.setExportedByName(rs.getString("exportedByName"));
                exp.setRecipientName(rs.getString("recipientName"));
                exp.setTotalQuantity(rs.getInt("totalQuantity"));
                exp.setTotalValue(rs.getDouble("totalValue"));
                list.add(exp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countExportHistory(String fromDate, String toDate, String exportCode, String recipientId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Exports WHERE 1=1 AND EXISTS (SELECT 1 FROM Export_Details d WHERE d.export_id = Exports.export_id AND d.status != 'draft') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND export_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND export_date <= ? ";
            params.add(toDate + " 23:59:59");
        }
        if (exportCode != null && !exportCode.isEmpty()) {
            sql += "AND export_code LIKE ? ";
            params.add("%" + exportCode + "%");
        }
        if (recipientId != null && !recipientId.isEmpty()) {
            sql += "AND recipient_user_id = ? ";
            params.add(Integer.parseInt(recipientId));
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Export> getExportHistoryAdvanced(String fromDate, String toDate, String materialName, String sortByRecipient, String sortByExportedBy, int page, int pageSize) {
        List<Export> list = new ArrayList<>();
        String sql = "SELECT DISTINCT e.*, u1.full_name as exportedByName, u2.full_name as recipientName, "
                + "(SELECT SUM(quantity) FROM Export_Details d WHERE d.export_id = e.export_id AND d.status != 'draft') as totalQuantity, "
                + "0 as totalValue "
                + "FROM Exports e "
                + "LEFT JOIN Users u1 ON e.exported_by = u1.user_id "
                + "LEFT JOIN Users u2 ON e.recipient_user_id = u2.user_id "
                + "LEFT JOIN Export_Details edt ON e.export_id = edt.export_id "
                + "LEFT JOIN Materials m ON edt.material_id = m.material_id "
                + "WHERE 1=1 AND EXISTS (SELECT 1 FROM Export_Details d WHERE d.export_id = e.export_id AND d.status != 'draft') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND e.export_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND e.export_date <= ? ";
            params.add(toDate + " 23:59:59");
        }
        if (materialName != null && !materialName.isEmpty()) {
            sql += "AND m.material_name LIKE ? ";
            params.add("%" + materialName + "%");
        }
        String orderByClause = "ORDER BY ";
        boolean hasOrderBy = false;
        if (sortByRecipient != null && !sortByRecipient.isEmpty()) {
            orderByClause += "u2.full_name " + (sortByRecipient.equals("A-Z") ? "ASC" : "DESC");
            hasOrderBy = true;
        }
        if (sortByExportedBy != null && !sortByExportedBy.isEmpty()) {
            if (hasOrderBy) {
                orderByClause += ", ";
            }
            orderByClause += "u1.full_name " + (sortByExportedBy.equals("A-Z") ? "ASC" : "DESC");
            hasOrderBy = true;
        }
        if (!hasOrderBy) {
            orderByClause += "e.export_date DESC";
        }
        sql += orderByClause + " LIMIT ? OFFSET ?";
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Export exp = new Export();
                exp.setExportId(rs.getInt("export_id"));
                exp.setExportCode(rs.getString("export_code"));
                if (rs.getTimestamp("export_date") != null) {
                    exp.setExportDate(rs.getTimestamp("export_date").toLocalDateTime());
                }
                exp.setExportedBy(rs.getInt("exported_by"));
                exp.setRecipientUserId(rs.getInt("recipient_user_id"));
                exp.setNote(rs.getString("note"));
                exp.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                exp.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                exp.setExportedByName(rs.getString("exportedByName"));
                exp.setRecipientName(rs.getString("recipientName"));
                exp.setTotalQuantity(rs.getInt("totalQuantity"));
                exp.setTotalValue(rs.getDouble("totalValue"));
                list.add(exp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countExportHistoryAdvanced(String fromDate, String toDate, String materialName) {
        int count = 0;
        String sql = "SELECT COUNT(DISTINCT e.export_id) FROM Exports e "
                + "LEFT JOIN Users u2 ON e.recipient_user_id = u2.user_id "
                + "LEFT JOIN Export_Details edt ON e.export_id = edt.export_id "
                + "LEFT JOIN Materials m ON edt.material_id = m.material_id WHERE 1=1 "
                + "AND EXISTS (SELECT 1 FROM Export_Details d WHERE d.export_id = e.export_id AND d.status != 'draft') ";
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql += "AND e.export_date >= ? ";
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql += "AND e.export_date <= ? ";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public Export getExportById(int exportId) {
        String sql = "SELECT e.*, u1.full_name AS exportedByName, u2.full_name AS recipientName "
                + "FROM Exports e "
                + "LEFT JOIN Users u1 ON e.exported_by = u1.user_id "
                + "LEFT JOIN Users u2 ON e.recipient_user_id = u2.user_id "
                + "WHERE e.export_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, exportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Export exportData = new Export();
                    exportData.setExportId(rs.getInt("export_id"));
                    exportData.setExportCode(rs.getString("export_code"));
                    if (rs.getTimestamp("export_date") != null) {
                        exportData.setExportDate(rs.getTimestamp("export_date").toLocalDateTime());
                    }
                    exportData.setExportedBy(rs.getInt("exported_by"));
                    exportData.setRecipientUserId(rs.getInt("recipient_user_id"));
                    exportData.setNote(rs.getString("note"));
                    exportData.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    exportData.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    exportData.setExportedByName(rs.getString("exportedByName"));
                    exportData.setRecipientName(rs.getString("recipientName"));
                    return exportData;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}