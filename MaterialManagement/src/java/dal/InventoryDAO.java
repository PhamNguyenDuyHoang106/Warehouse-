package dal;

import entity.DBContext;
import entity.Inventory;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(InventoryDAO.class.getName());

    // ========================================
    // NEW SCHEMA METHODS (with rack_id support)
    // ========================================
    
    public List<Inventory> getInventoryByMaterialId(int materialId) {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT i.*, m.material_name, m.material_code, c.category_name, u.unit_name, m.materials_url, "
                + "wr.rack_name, wr.rack_code "
                + "FROM Inventory i "
                + "JOIN Materials m ON i.material_id = m.material_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Warehouse_Racks wr ON i.rack_id = wr.rack_id "
                + "WHERE i.material_id = ? AND m.disable = 0 "
                + "ORDER BY wr.rack_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inventory inventory = mapResultSetToInventory(rs);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting inventory by material ID: " + materialId, e);
        }
        return inventoryList;
    }

    public List<Inventory> getInventoryByRackId(int rackId) {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT i.*, m.material_name, m.material_code, c.category_name, u.unit_name, m.materials_url, "
                + "wr.rack_name, wr.rack_code "
                + "FROM Inventory i "
                + "JOIN Materials m ON i.material_id = m.material_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Warehouse_Racks wr ON i.rack_id = wr.rack_id "
                + "WHERE i.rack_id = ? AND m.disable = 0 "
                + "ORDER BY m.material_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rackId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inventory inventory = mapResultSetToInventory(rs);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting inventory by rack ID: " + rackId, e);
        }
        return inventoryList;
    }

    public Inventory getInventoryByMaterialAndRack(int materialId, Integer rackId) {
        String sql = "SELECT i.*, m.material_name, m.material_code, c.category_name, u.unit_name, m.materials_url, "
                + "wr.rack_name, wr.rack_code "
                + "FROM Inventory i "
                + "JOIN Materials m ON i.material_id = m.material_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Warehouse_Racks wr ON i.rack_id = wr.rack_id "
                + "WHERE i.material_id = ? AND (i.rack_id = ? OR (i.rack_id IS NULL AND ? IS NULL)) "
                + "AND m.disable = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setObject(2, rackId);
            ps.setObject(3, rackId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToInventory(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting inventory by material and rack", e);
        }
        return null;
    }

    public boolean updateStock(int materialId, Integer rackId, BigDecimal quantity, int updatedBy) {
        String sql = "UPDATE Inventory SET stock = stock + ?, updated_by = ?, last_updated = CURRENT_TIMESTAMP "
                + "WHERE material_id = ? AND (rack_id = ? OR (rack_id IS NULL AND ? IS NULL))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, quantity);
            ps.setInt(2, updatedBy);
            ps.setInt(3, materialId);
            ps.setObject(4, rackId);
            ps.setObject(5, rackId);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock", e);
            return false;
        }
    }

    public boolean createInventory(int materialId, Integer rackId, BigDecimal stock, int updatedBy) {
        String sql = "INSERT INTO Inventory (material_id, rack_id, stock, updated_by, last_updated) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setObject(2, rackId);
            ps.setBigDecimal(3, stock);
            ps.setInt(4, updatedBy);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating inventory", e);
            return false;
        }
    }

    public boolean upsertInventory(int materialId, Integer rackId, BigDecimal quantity, int updatedBy) {
        // First try to update existing record
        if (updateStock(materialId, rackId, quantity, updatedBy)) {
            return true;
        }
        
        // If no record exists, create new one
        return createInventory(materialId, rackId, quantity, updatedBy);
    }

    public BigDecimal getTotalStockByMaterial(int materialId) {
        String sql = "SELECT COALESCE(SUM(stock), 0) as total_stock FROM Inventory WHERE material_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                return rs.getBigDecimal("total_stock");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total stock by material", e);
        }
        return BigDecimal.ZERO;
    }

    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT i.*, m.material_name, m.material_code, c.category_name, u.unit_name, m.materials_url, "
                + "wr.rack_name, wr.rack_code "
                + "FROM Inventory i "
                + "JOIN Materials m ON i.material_id = m.material_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Warehouse_Racks wr ON i.rack_id = wr.rack_id "
                + "WHERE m.disable = 0 "
                + "ORDER BY m.material_name";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inventory inventory = mapResultSetToInventory(rs);
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all inventory", e);
        }
        return inventoryList;
    }
    public Map<String, Integer> getInventoryStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT " +
                    "COALESCE(SUM(i.stock), 0) as total_stock, " +
                    "COUNT(DISTINCT CASE WHEN i.stock > 0 AND i.stock < 10 THEN i.inventory_id END) as low_stock_count, " +
                    "COUNT(DISTINCT CASE WHEN i.stock = 0 OR i.stock IS NULL THEN m.material_id END) as out_of_stock_count " +
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting inventory statistics", e);
        }
        return stats;
    }
    
    public List<Inventory> getInventoryWithPagination(String searchTerm, String stockFilter, String sortStock, int page, int pageSize) {
        List<Inventory> inventoryList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.material_id, m.material_name, m.material_code, m.materials_url, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS stock, i.last_updated, i.updated_by, i.inventory_id ");
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
                    inventory.setStock(rs.getBigDecimal("stock"));
                    if (rs.getTimestamp("last_updated") != null) {
                        inventory.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
                    }
                    int updatedBy = rs.getInt("updated_by");
                    if (!rs.wasNull()) {
                        inventory.setUpdatedBy(updatedBy);
                    }
                    inventory.setMaterialName(rs.getString("material_name"));
                    inventory.setMaterialCode(rs.getString("material_code"));
                    inventory.setMaterialsUrl(rs.getString("materials_url"));
                    inventory.setCategoryName(rs.getString("category_name"));
                    inventory.setUnitName(rs.getString("unit_name"));
                    inventoryList.add(inventory);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getInventoryWithPagination", e);
        }
        return inventoryList;
    }
    
    public int getInventoryCount(String searchTerm, String stockFilter) {
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getInventoryCount", e);
        }
        return 0;
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(rs.getInt("inventory_id"));
        inventory.setMaterialId(rs.getInt("material_id"));
        
        // Handle nullable rack_id
        Integer rackId = rs.getObject("rack_id", Integer.class);
        inventory.setRackId(rackId);
        
        inventory.setStock(rs.getBigDecimal("stock"));
        
        if (rs.getTimestamp("last_updated") != null) {
            inventory.setLastUpdated(rs.getTimestamp("last_updated").toLocalDateTime());
        }
        
        int updatedBy = rs.getInt("updated_by");
        if (!rs.wasNull()) {
            inventory.setUpdatedBy(updatedBy);
        }
        
        // Additional fields for display
        inventory.setMaterialName(rs.getString("material_name"));
        inventory.setMaterialCode(rs.getString("material_code"));
        inventory.setCategoryName(rs.getString("category_name"));
        inventory.setUnitName(rs.getString("unit_name"));
        inventory.setMaterialsUrl(rs.getString("materials_url"));
        inventory.setRackName(rs.getString("rack_name"));
        inventory.setRackCode(rs.getString("rack_code"));
        
        return inventory;
    }
}