package dal;

import entity.DBContext;
import entity.ExportDetail;
import entity.ImportDetail;
import entity.Inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InventoryDAO extends DBContext {

    public void updateInventory(List<ExportDetail> details, int updatedBy) throws SQLException {
        Objects.requireNonNull(details, "Export details list cannot be null");
        if (details.isEmpty()) {
            throw new SQLException("Export details list cannot be empty.");
        }
        if (updatedBy <= 0) {
            throw new SQLException("Invalid user ID for update: " + updatedBy);
        }

        String checkStockSql = "SELECT stock FROM Inventory WHERE material_id = ?";
        String updateStockSql = "UPDATE Inventory SET stock = stock - ?, updated_by = ?, last_updated = CURRENT_TIMESTAMP WHERE material_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateStockSql)) {
            for (ExportDetail detail : details) {
                checkStmt.setInt(1, detail.getMaterialId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("stock");
                        if (currentStock < detail.getQuantity()) {
                            throw new SQLException("Insufficient stock for material ID: " + detail.getMaterialId() +
                                    ". Available: " + currentStock + ", Required: " + detail.getQuantity());
                        }
                        updateStmt.setInt(1, detail.getQuantity());
                        updateStmt.setInt(2, updatedBy);
                        updateStmt.setInt(3, detail.getMaterialId());
                        if (updateStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to update inventory for material ID: " + detail.getMaterialId());
                        }
                    } else {
                        throw new SQLException("Inventory information not found for material ID: " + detail.getMaterialId());
                    }
                }
            }
        }
    }

    public void updateInventoryForImport(List<ImportDetail> details, int updatedBy, String location) throws SQLException {
        Objects.requireNonNull(details, "Import details list cannot be null");
        if (details.isEmpty()) {
            throw new SQLException("Import details list cannot be empty.");
        }
        if (updatedBy <= 0) {
            throw new SQLException("Invalid user ID for update: " + updatedBy);
        }

        String checkStockSql = "SELECT stock FROM Inventory WHERE material_id = ?";
        String updateStockSql = "UPDATE Inventory SET stock = stock + ?, updated_by = ?, last_updated = CURRENT_TIMESTAMP, location = ? WHERE material_id = ?";
        String insertStockSql = "INSERT INTO Inventory (material_id, stock, updated_by, last_updated, location) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStockSql);
             PreparedStatement updateStmt = connection.prepareStatement(updateStockSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertStockSql)) {
            for (ImportDetail detail : details) {
                checkStmt.setInt(1, detail.getMaterialId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        updateStmt.setInt(1, detail.getQuantity());
                        updateStmt.setInt(2, updatedBy);
                        updateStmt.setString(3, location);
                        updateStmt.setInt(4, detail.getMaterialId());
                        if (updateStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to update inventory for material ID: " + detail.getMaterialId());
                        }
                    } else {
                        insertStmt.setInt(1, detail.getMaterialId());
                        insertStmt.setInt(2, detail.getQuantity());
                        insertStmt.setInt(3, updatedBy);
                        insertStmt.setString(4, location);
                        if (insertStmt.executeUpdate() == 0) {
                            throw new SQLException("Failed to create new inventory for material ID: " + detail.getMaterialId());
                        }
                    }
                }
            }
        }
    }

    public int getStockByMaterialId(int materialId) throws SQLException {
        String sql = "SELECT stock FROM Inventory WHERE material_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        return 0;
    }

    public List<Inventory> getAllInventory() throws SQLException {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT m.material_id, m.material_name, m.material_code, m.materials_url, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS stock, i.location, i.note, i.last_updated, i.updated_by, i.inventory_id " +
                    "FROM Materials m " +
                    "LEFT JOIN Inventory i ON m.material_id = i.material_id " +
                    "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                    "LEFT JOIN Units u ON m.unit_id = u.unit_id " +
                    "WHERE m.disable = 0 " +
                    "ORDER BY m.material_code";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setInventoryId(rs.getInt("inventory_id"));
                inventory.setMaterialId(rs.getInt("material_id"));
                inventory.setStock(rs.getInt("stock"));
                inventory.setLocation(rs.getString("location"));
                inventory.setNote(rs.getString("note"));
                if (rs.getTimestamp("last_updated") != null) {
                    inventory.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                }
                inventory.setUpdatedBy(rs.getInt("updated_by"));
                inventory.setMaterialName(rs.getString("material_name"));
                inventory.setMaterialCode(rs.getString("material_code"));
                inventory.setMaterialsUrl(rs.getString("materials_url"));
                inventory.setCategoryName(rs.getString("category_name"));
                inventory.setUnitName(rs.getString("unit_name"));
                inventoryList.add(inventory);
            }
        }
        return inventoryList;
    }
    public Map<String, Integer> getInventoryStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT " +
                    "SUM(IFNULL(i.stock,0)) as total_stock, " +
                    "COUNT(CASE WHEN IFNULL(i.stock,0) > 0 AND IFNULL(i.stock,0) < 10 THEN 1 END) as low_stock_count, " +
                    "COUNT(CASE WHEN IFNULL(i.stock,0) = 0 THEN 1 END) as out_of_stock_count " +
                    "FROM Materials m " +
                    "LEFT JOIN Inventory i ON m.material_id = i.material_id " +
                    "WHERE m.disable = 0";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalStock", rs.getInt("total_stock"));
                stats.put("lowStockCount", rs.getInt("low_stock_count"));
                stats.put("outOfStockCount", rs.getInt("out_of_stock_count"));
            }
        }
        return stats;
    }
    
    public List<Inventory> getInventoryWithPagination(String searchTerm, String stockFilter, String sortStock, int page, int pageSize) throws SQLException {
        List<Inventory> inventoryList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.material_id, m.material_name, m.material_code, m.materials_url, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS stock, i.location, i.note, i.last_updated, i.updated_by, i.inventory_id ");
        sql.append("FROM Materials m ");
        sql.append("LEFT JOIN Inventory i ON m.material_id = i.material_id ");
        sql.append("LEFT JOIN Categories c ON m.category_id = c.category_id ");
        sql.append("LEFT JOIN Units u ON m.unit_id = u.unit_id ");
        sql.append("WHERE m.disable = 0 ");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (LOWER(m.material_name) LIKE ? OR LOWER(m.material_code) LIKE ? OR LOWER(c.category_name) LIKE ?) ");
        }
        if (stockFilter != null && !stockFilter.trim().isEmpty()) {
            switch (stockFilter) {
                case "high":
                    sql.append("AND IFNULL(i.stock, 0) > 50 ");
                    break;
                case "medium":
                    sql.append("AND IFNULL(i.stock, 0) >= 10 AND IFNULL(i.stock, 0) <= 50 ");
                    break;
                case "low":
                    sql.append("AND IFNULL(i.stock, 0) >= 1 AND IFNULL(i.stock, 0) < 10 ");
                    break;
                case "zero":
                    sql.append("AND IFNULL(i.stock, 0) = 0 ");
                    break;
            }
        }
        if (sortStock != null && !sortStock.trim().isEmpty()) {
            if (sortStock.equals("asc")) {
                sql.append("ORDER BY IFNULL(i.stock, 0) ASC, m.material_code ASC ");
            } else if (sortStock.equals("desc")) {
                sql.append("ORDER BY IFNULL(i.stock, 0) DESC, m.material_code ASC ");
            } else {
                sql.append("ORDER BY m.material_code ASC ");
            }
        } else {
            sql.append("ORDER BY m.material_code ASC ");
        }
        sql.append("LIMIT ? OFFSET ?");
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase().trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            stmt.setInt(paramIndex++, pageSize);
            stmt.setInt(paramIndex, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Inventory inventory = new Inventory();
                    inventory.setInventoryId(rs.getInt("inventory_id"));
                    inventory.setMaterialId(rs.getInt("material_id"));
                    inventory.setStock(rs.getInt("stock"));
                    inventory.setLocation(rs.getString("location"));
                    inventory.setNote(rs.getString("note"));
                    if (rs.getTimestamp("last_updated") != null) {
                        inventory.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                    }
                    inventory.setUpdatedBy(rs.getInt("updated_by"));
                    inventory.setMaterialName(rs.getString("material_name"));
                    inventory.setMaterialCode(rs.getString("material_code"));
                    inventory.setMaterialsUrl(rs.getString("materials_url"));
                    inventory.setCategoryName(rs.getString("category_name"));
                    inventory.setUnitName(rs.getString("unit_name"));
                    inventoryList.add(inventory);
                }
            }
        }
        return inventoryList;
    }
    public int getInventoryCount(String searchTerm, String stockFilter) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) as total ");
        sql.append("FROM Materials m ");
        sql.append("LEFT JOIN Inventory i ON m.material_id = i.material_id ");
        sql.append("LEFT JOIN Categories c ON m.category_id = c.category_id ");
        sql.append("WHERE m.disable = 0 ");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (LOWER(m.material_name) LIKE ? OR LOWER(m.material_code) LIKE ? OR LOWER(c.category_name) LIKE ?) ");
        }
        if (stockFilter != null && !stockFilter.trim().isEmpty()) {
            switch (stockFilter) {
                case "high":
                    sql.append("AND IFNULL(i.stock, 0) > 50 ");
                    break;
                case "medium":
                    sql.append("AND IFNULL(i.stock, 0) >= 10 AND IFNULL(i.stock, 0) <= 50 ");
                    break;
                case "low":
                    sql.append("AND IFNULL(i.stock, 0) >= 1 AND IFNULL(i.stock, 0) < 10 ");
                    break;
                case "zero":
                    sql.append("AND IFNULL(i.stock, 0) = 0 ");
                    break;
            }
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm.toLowerCase().trim() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public void updateNote(int materialId, String note) throws SQLException {
        String sql = "UPDATE Inventory SET note = ? WHERE material_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, note);
            stmt.setInt(2, materialId);
            stmt.executeUpdate();
        }
    }
}