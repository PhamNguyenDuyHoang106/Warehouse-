package dal;

import entity.Category;
import entity.DBContext;
import entity.Material;
import entity.Unit;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(MaterialDAO.class.getName());

    private static final String BASE_SELECT =
        "SELECT " +
            "m.material_id, " +
            "m.material_code, " +
            "m.material_name, " +
            "m.url, " +
            "m.barcode, " +
            "m.status, " +
            "m.category_id, " +
            "m.default_unit_id, " +
            "m.purchase_unit_id, " +
            "m.sales_unit_id, " +
            "m.min_stock, " +
            "m.max_stock, " +
            "m.weight_per_unit, " +
            "m.volume_per_unit, " +
            "m.shelf_life_days, " +
            "m.is_serialized, " +
            "m.is_batch_controlled, " +
            "m.created_at, " +
            "m.updated_at, " +
            "m.deleted_at, " +
            "c.category_code, " +
            "c.category_name, " +
            "c.parent_id, " +
            "c.level_depth, " +
            "du.unit_code AS default_unit_code, " +
            "du.unit_name AS default_unit_name, " +
            "du.symbol AS default_unit_symbol, " +
            "pu.unit_id AS purchase_unit_real_id, " +
            "pu.unit_code AS purchase_unit_code, " +
            "pu.unit_name AS purchase_unit_name, " +
            "pu.symbol AS purchase_unit_symbol, " +
            "su.unit_id AS sales_unit_real_id, " +
            "su.unit_code AS sales_unit_code, " +
            "su.unit_name AS sales_unit_name, " +
            "su.symbol AS sales_unit_symbol, " +
            "inv.stock_on_hand, " +
            "inv.reserved_stock, " +
            "inv.available_stock " +
        "FROM Materials m " +
        "LEFT JOIN Categories c ON c.category_id = m.category_id " +
        "LEFT JOIN Units du ON du.unit_id = m.default_unit_id " +
        "LEFT JOIN Units pu ON pu.unit_id = m.purchase_unit_id " +
        "LEFT JOIN Units su ON su.unit_id = m.sales_unit_id " +
        "LEFT JOIN ( " +
            "SELECT material_id, " +
                   "SUM(stock) AS stock_on_hand, " +
                   "SUM(reserved_stock) AS reserved_stock, " +
                   "SUM(stock - reserved_stock) AS available_stock " +
            "FROM Inventory " +
            "GROUP BY material_id " +
        ") inv ON inv.material_id = m.material_id ";

    private Material mapMaterial(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialId(rs.getInt("material_id"));
        material.setMaterialCode(rs.getString("material_code"));
        material.setMaterialName(rs.getString("material_name"));
        material.setUrl(rs.getString("url"));
        material.setBarcode(rs.getString("barcode"));
        String status = rs.getString("status");
        material.setStatus(status);
        material.setMaterialStatus(status);

        material.setMinStock(safeDecimal(rs, "min_stock"));
        material.setMaxStock(safeDecimal(rs, "max_stock"));
        material.setWeightPerUnit(safeDecimal(rs, "weight_per_unit"));
        material.setVolumePerUnit(safeDecimal(rs, "volume_per_unit"));
        material.setShelfLifeDays(safeInteger(rs, "shelf_life_days"));
        material.setSerialized(rs.getBoolean("is_serialized"));
        material.setBatchControlled(rs.getBoolean("is_batch_controlled"));
        material.setStockOnHand(safeDecimal(rs, "stock_on_hand"));
        material.setReservedStock(safeDecimal(rs, "reserved_stock"));
        material.setAvailableStock(safeDecimal(rs, "available_stock"));
        // average_cost column không tồn tại trong database, để null
        material.setAverageCost(null);

        material.setCreatedAt(rs.getTimestamp("created_at"));
        material.setUpdatedAt(rs.getTimestamp("updated_at"));
        material.setDeletedAt(rs.getTimestamp("deleted_at"));

        if (rs.getObject("category_id") != null) {
            Category category = new Category();
            category.setCategory_id(rs.getInt("category_id"));
            category.setCategory_name(rs.getString("category_name"));
            category.setCode(rs.getString("category_code"));
            category.setParent_id((Integer) rs.getObject("parent_id"));
            category.setLevelDepth(safeInteger(rs, "level_depth"));
            // category.setPathLtree(rs.getString("path_ltree")); // Removed - column doesn't exist
            material.setCategory(category);
        }

        if (rs.getObject("default_unit_id") != null) {
            Unit defaultUnit = new Unit();
            defaultUnit.setId(rs.getInt("default_unit_id"));
            defaultUnit.setUnitCode(rs.getString("default_unit_code"));
            defaultUnit.setUnitName(rs.getString("default_unit_name"));
            defaultUnit.setSymbol(rs.getString("default_unit_symbol"));
            material.setDefaultUnit(defaultUnit);
        }

        if (rs.getObject("purchase_unit_real_id") != null) {
            Unit purchaseUnit = new Unit();
            purchaseUnit.setId(rs.getInt("purchase_unit_real_id"));
            purchaseUnit.setUnitCode(rs.getString("purchase_unit_code"));
            purchaseUnit.setUnitName(rs.getString("purchase_unit_name"));
            purchaseUnit.setSymbol(rs.getString("purchase_unit_symbol"));
            material.setPurchaseUnit(purchaseUnit);
        }

        if (rs.getObject("sales_unit_real_id") != null) {
            Unit salesUnit = new Unit();
            salesUnit.setId(rs.getInt("sales_unit_real_id"));
            salesUnit.setUnitCode(rs.getString("sales_unit_code"));
            salesUnit.setUnitName(rs.getString("sales_unit_name"));
            salesUnit.setSymbol(rs.getString("sales_unit_symbol"));
            material.setSalesUnit(salesUnit);
        }

        return material;
    }

    private BigDecimal safeDecimal(ResultSet rs, String column) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        return value != null ? value : BigDecimal.ZERO;
    }

    private Integer safeInteger(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return value == null ? null : rs.getInt(column);
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
            for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            int index = i + 1;
            if (param == null) {
                ps.setNull(index, Types.NULL);
            } else if (param instanceof Integer) {
                ps.setInt(index, (Integer) param);
            } else if (param instanceof Long) {
                ps.setLong(index, (Long) param);
            } else if (param instanceof BigDecimal) {
                ps.setBigDecimal(index, (BigDecimal) param);
            } else if (param instanceof Double) {
                ps.setDouble(index, (Double) param);
            } else if (param instanceof Boolean) {
                ps.setBoolean(index, (Boolean) param);
            } else if (param instanceof Timestamp) {
                ps.setTimestamp(index, (Timestamp) param);
            } else {
                ps.setObject(index, param);
            }
        }
    }

    private List<Material> fetchMaterials(String whereClause, List<Object> params, String orderClause) {
        List<Material> materials = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" ").append(whereClause);
        }
        if (orderClause != null && !orderClause.trim().isEmpty()) {
            sql.append(" ").append(orderClause);
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (params != null && !params.isEmpty()) {
                setParameters(ps, params);
            }
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                    materials.add(mapMaterial(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching materials", ex);
        }
        return materials;
    }

    private String resolveSortOption(String sortOption) {
        if (sortOption == null || sortOption.trim().isEmpty()) {
            return "ORDER BY m.material_code ASC";
        }
        if ("name_asc".equals(sortOption)) {
            return "ORDER BY m.material_name ASC";
        } else if ("name_desc".equals(sortOption)) {
            return "ORDER BY m.material_name DESC";
        } else if ("code_desc".equals(sortOption)) {
            return "ORDER BY m.material_code DESC";
        } else if ("updated_desc".equals(sortOption)) {
            return "ORDER BY m.updated_at DESC";
        } else if ("updated_asc".equals(sortOption)) {
            return "ORDER BY m.updated_at ASC";
        } else {
            return "ORDER BY m.material_code ASC";
        }
    }

    public List<Material> searchMaterials(String keyword, String status, int pageIndex, int pageSize, String sortOption) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }
        StringBuilder where = new StringBuilder("WHERE m.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim() + "%";
            where.append(" AND (m.material_code LIKE ? OR m.material_name LIKE ? OR m.barcode LIKE ?)");
            params.add(like);
            params.add(like);
            params.add(like);
        }

        if (status != null && !status.trim().isEmpty()) {
            where.append(" AND m.status = ?");
            params.add(status.trim());
        }

        String orderClause = resolveSortOption(sortOption);
        StringBuilder sql = new StringBuilder(BASE_SELECT)
                .append(" ")
                .append(where)
                .append(" ")
                .append(orderClause)
                .append(" LIMIT ? OFFSET ?");

        params.add(pageSize);
        params.add((pageIndex - 1) * pageSize);

        List<Material> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapMaterial(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error searching materials", ex);
        }
        return results;
    }

    public int countMaterials(String keyword, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Materials m WHERE m.deleted_at IS NULL");
            List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.trim() + "%";
            sql.append(" AND (m.material_code LIKE ? OR m.material_name LIKE ? OR m.barcode LIKE ?)");
            params.add(like);
            params.add(like);
            params.add(like);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND m.status = ?");
            params.add(status.trim());
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error counting materials", ex);
        }
        return 0;
    }

    public boolean deleteMaterial(int materialId) {
        try (PreparedStatement stockStmt = connection.prepareStatement(
                "SELECT COALESCE(SUM(stock), 0) AS stock_sum FROM Inventory WHERE material_id = ?")) {
            stockStmt.setInt(1, materialId);
            try (ResultSet rs = stockStmt.executeQuery()) {
            if (rs.next()) {
                    BigDecimal stock = rs.getBigDecimal("stock_sum");
                    if (stock != null && stock.compareTo(BigDecimal.ZERO) > 0) {
                    return false;
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking stock before delete", ex);
            return false;
        }

        try (PreparedStatement deleteStmt = connection.prepareStatement(
                "UPDATE Materials SET deleted_at = CURRENT_TIMESTAMP WHERE material_id = ? AND deleted_at IS NULL")) {
            deleteStmt.setInt(1, materialId);
            return deleteStmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error soft deleting material", ex);
            return false;
        }
    }

    public Material getInformation(int materialId) {
        List<Object> params = new ArrayList<>();
        params.add(materialId);
        List<Material> materials = fetchMaterials("WHERE m.material_id = ?", params, "LIMIT 1");
        return materials.isEmpty() ? null : materials.get(0);
    }

    public Material getMaterialWithRacks(int materialId) {
        return getInformation(materialId);
    }

    public void updateMaterial(Material material) {
        String sql = "UPDATE Materials SET " +
            "material_code = ?, " +
            "material_name = ?, " +
            "url = ?, " +
            "barcode = ?, " +
            "status = ?, " +
            "category_id = ?, " +
            "default_unit_id = ?, " +
            "purchase_unit_id = ?, " +
            "sales_unit_id = ?, " +
            "min_stock = ?, " +
            "max_stock = ?, " +
            "weight_per_unit = ?, " +
            "volume_per_unit = ?, " +
            "shelf_life_days = ?, " +
            "is_serialized = ?, " +
            "is_batch_controlled = ?, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE material_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, material.getMaterialCode());
            ps.setString(2, material.getMaterialName());
            ps.setString(3, material.getUrl());
            ps.setString(4, material.getBarcode());
            ps.setString(5, material.getMaterialStatus() != null ? material.getMaterialStatus() : "active");

            if (material.getCategory() != null) {
                ps.setInt(6, material.getCategory().getCategory_id());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            if (material.getDefaultUnit() != null) {
                ps.setInt(7, material.getDefaultUnit().getId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            if (material.getPurchaseUnit() != null) {
                ps.setInt(8, material.getPurchaseUnit().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            if (material.getSalesUnit() != null) {
                ps.setInt(9, material.getSalesUnit().getId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            ps.setBigDecimal(10, material.getMinStock() != null ? material.getMinStock() : BigDecimal.ZERO);
            ps.setBigDecimal(11, material.getMaxStock() != null ? material.getMaxStock() : BigDecimal.ZERO);
            ps.setBigDecimal(12, material.getWeightPerUnit() != null ? material.getWeightPerUnit() : BigDecimal.ZERO);
            ps.setBigDecimal(13, material.getVolumePerUnit() != null ? material.getVolumePerUnit() : BigDecimal.ZERO);

            if (material.getShelfLifeDays() != null) {
                ps.setInt(14, material.getShelfLifeDays());
            } else {
                ps.setNull(14, Types.INTEGER);
            }

            ps.setBoolean(15, material.isSerialized());
            ps.setBoolean(16, material.isBatchControlled());

            ps.setInt(17, material.getMaterialId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating material", ex);
        }
    }

    public void addMaterial(Material material) {
            String sql = "INSERT INTO Materials (" +
                "material_code, material_name, url, barcode, category_id, " +
                "default_unit_id, purchase_unit_id, sales_unit_id, " +
                "min_stock, max_stock, weight_per_unit, volume_per_unit, " +
                "shelf_life_days, is_serialized, is_batch_controlled, " +
                "status" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, material.getMaterialCode());
            ps.setString(2, material.getMaterialName());
            ps.setString(3, material.getUrl());
            ps.setString(4, material.getBarcode());

            if (material.getCategory() != null) {
                ps.setInt(5, material.getCategory().getCategory_id());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (material.getDefaultUnit() != null) {
                ps.setInt(6, material.getDefaultUnit().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            if (material.getPurchaseUnit() != null) {
                ps.setInt(7, material.getPurchaseUnit().getId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            if (material.getSalesUnit() != null) {
                ps.setInt(8, material.getSalesUnit().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setBigDecimal(9, material.getMinStock() != null ? material.getMinStock() : BigDecimal.ZERO);
            ps.setBigDecimal(10, material.getMaxStock() != null ? material.getMaxStock() : BigDecimal.ZERO);
            ps.setBigDecimal(11, material.getWeightPerUnit() != null ? material.getWeightPerUnit() : BigDecimal.ZERO);
            ps.setBigDecimal(12, material.getVolumePerUnit() != null ? material.getVolumePerUnit() : BigDecimal.ZERO);

            if (material.getShelfLifeDays() != null) {
                ps.setInt(13, material.getShelfLifeDays());
            } else {
                ps.setNull(13, Types.INTEGER);
            }

            ps.setBoolean(14, material.isSerialized());
            ps.setBoolean(15, material.isBatchControlled());
            ps.setString(16, material.getMaterialStatus() != null ? material.getMaterialStatus() : "active");

            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding material", ex);
        }
    }

    public Material getProductById(int materialId) {
        return getInformation(materialId);
    }

    public List<Material> getAllProducts() {
        return fetchMaterials("WHERE m.deleted_at IS NULL", new ArrayList<>(), "ORDER BY m.material_code ASC");
    }

    public List<Material> getAllProductsIncludingDisabled() {
        return fetchMaterials("", new ArrayList<>(), "ORDER BY m.material_code ASC");
    }

    public List<Material> searchProductsByName(String keyword) {
        StringBuilder where = new StringBuilder("WHERE m.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            where.append(" AND m.material_name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        return fetchMaterials(where.toString(), params, "ORDER BY m.material_name ASC");
    }

    public List<Material> searchProductsByCode(String materialCode) {
        StringBuilder where = new StringBuilder("WHERE m.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        if (materialCode != null && !materialCode.trim().isEmpty()) {
            where.append(" AND m.material_code LIKE ?");
            params.add("%" + materialCode.trim() + "%");
        }
        return fetchMaterials(where.toString(), params, "ORDER BY m.material_code ASC");
    }

    public List<Material> searchMaterialsByCategoriesID(int categoryId) {
        List<Object> params = new ArrayList<>();
        params.add(categoryId);
        return fetchMaterials("WHERE m.deleted_at IS NULL AND m.category_id = ?", params,
                "ORDER BY m.material_name ASC");
    }

    public List<Material> sortMaterialsByName() {
        return fetchMaterials("WHERE m.deleted_at IS NULL", new ArrayList<>(), "ORDER BY m.material_name ASC");
    }

    public List<Material> getMaterials() {
        return fetchMaterials("WHERE m.deleted_at IS NULL", new ArrayList<>(), "ORDER BY m.material_id DESC");
    }

    public boolean isMaterialCodeExists(String materialCode) {
        String sql = "SELECT 1 FROM Materials WHERE material_code = ? AND deleted_at IS NULL LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialCode);
            try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking material code existence", ex);
        }
        return false;
    }

    public int getMaterialIdByName(String name) {
        String sql = "SELECT material_id FROM Materials WHERE material_name = ? AND deleted_at IS NULL LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("material_id");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting material id by name", ex);
        }
        return -1;
    }

    public Map<Integer, String> getMaterialImages(List<Integer> materialIds) {
        Map<Integer, String> imageMap = new HashMap<>();
        if (materialIds == null) {
            return imageMap;
        }
        for (Integer id : materialIds) {
            imageMap.put(id, "images/material/default-material.png");
        }
        return imageMap;
    }

    public int getMaxMaterialNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(material_code, 4) AS SIGNED)), 0) AS max_num FROM Materials WHERE material_code LIKE 'MAT%'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_num");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting max material number", ex);
        }
        return 0;
    }

    @Deprecated
    public List<Material> getAllMaterials() {
        List<Material> basics = new ArrayList<>();
        for (Material material : getAllProducts()) {
                Material simple = new Material();
            simple.setMaterialId(material.getMaterialId());
            simple.setMaterialName(material.getMaterialName());
            simple.setMaterialCode(material.getMaterialCode());
            basics.add(simple);
        }
        return basics;
    }

    public List<Material> searchMaterialsByPrice(Double minPrice, Double maxPrice, int pageIndex, int pageSize, String sortOption) {
        return searchMaterials(null, null, pageIndex, pageSize, sortOption);
    }

    public boolean isMaterialNameAndStatusExists(String materialName, String materialStatus) {
        String sql = "SELECT 1 FROM Materials WHERE material_name = ? AND status = ? AND deleted_at IS NULL LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialName);
            ps.setString(2, materialStatus);
            try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking material name/status", ex);
        }
        return false;
    }

    public Material getInformationByNameAndStatus(String name, String status) {
        List<Object> params = new ArrayList<>();
        params.add(name);
        params.add(status);
        List<Material> materials = fetchMaterials(
                "WHERE m.deleted_at IS NULL AND m.material_name = ? AND m.status = ?", params, "LIMIT 1");
        return materials.isEmpty() ? null : materials.get(0);
    }

    public Material getMaterialByName(String materialName) {
        List<Object> params = new ArrayList<>();
        params.add(materialName);
        List<Material> materials = fetchMaterials(
                "WHERE m.deleted_at IS NULL AND m.material_name = ?", params, "LIMIT 1");
        return materials.isEmpty() ? null : materials.get(0);
    }

    public int getAvailableStock(int materialId) {
        String sql = "SELECT COALESCE(SUM(available_stock), 0) AS available_stock FROM Inventory WHERE material_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                    return rs.getBigDecimal("available_stock").intValue();
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting available stock", ex);
        }
        return 0;
    }
}

