package dal;

import entity.Category;
import entity.DBContext;
import entity.Material;
import entity.Unit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialDAO extends DBContext {

    public List<Material> searchMaterials(String keyword, String status, int pageIndex, int pageSize, String sortOption) {
        if (sortOption == null) sortOption = "";
        List<Material> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT m.*, c.category_name, u.unit_name, wr.rack_name, wr.rack_code, ")
               .append("IFNULL(SUM(i.stock), 0) AS quantity ")
               .append("FROM materials m ")
               .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
               .append("LEFT JOIN units u ON m.unit_id = u.unit_id AND u.disable = 0 ")
               .append("LEFT JOIN Warehouse_Racks wr ON m.rack_id = wr.rack_id ")
               .append("LEFT JOIN inventory i ON m.material_id = i.material_id ")
               .append("WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND m.material_name LIKE ? ");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND m.material_status = ? ");
                params.add(status);
            }
            String sortBy = "m.material_code"; 
            String sortOrder = "ASC";

            switch (sortOption) {
                case "name_asc":
                    sortBy = "m.material_name";
                    sortOrder = "ASC";
                    break;
                case "name_desc":
                    sortBy = "m.material_name";
                    sortOrder = "DESC";
                    break;
                case "code_asc":
                    sortBy = "m.material_code";
                    sortOrder = "ASC";
                    break;
                case "code_desc":
                    sortBy = "m.material_code";
                    sortOrder = "DESC";
                    break;
            }
            sql.append(" GROUP BY m.material_id, c.category_name, u.unit_name, wr.rack_name, wr.rack_code ");
            sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder).append(" ");

            sql.append("LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((pageIndex - 1) * pageSize);

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setQuantity(rs.getBigDecimal("quantity"));

                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                m.setUnit(u);

                // optional default rack info
                if (rs.getObject("rack_id") != null) {
                    entity.WarehouseRack wr = new entity.WarehouseRack();
                    wr.setRackId(rs.getInt("rack_id"));
                    wr.setRackName(rs.getString("rack_name"));
                    wr.setRackCode(rs.getString("rack_code"));
                    m.setRack(wr);
                }

                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countMaterials(String keyword, String status) {
        int count = 0;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM materials m WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                sql.append("AND m.material_name LIKE ? ");
                params.add("%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {
                sql.append("AND m.material_status = ? ");
                params.add(status);
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public boolean deleteMaterial(int materialId) {
        // Kiểm tra quantity stock trước khi xóa
        String checkStockSql = "SELECT IFNULL(i.stock, 0) AS quantity FROM materials m " +
                              "LEFT JOIN inventory i ON m.material_id = i.material_id " +
                              "WHERE m.material_id = ?";
        
        try {
            // Kiểm tra stock quantity
            PreparedStatement checkPs = connection.prepareStatement(checkStockSql);
            checkPs.setInt(1, materialId);
            ResultSet rs = checkPs.executeQuery();
            
            if (rs.next()) {
                java.math.BigDecimal quantity = rs.getBigDecimal("quantity");
                if (quantity != null && quantity.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    // Không thể xóa vì còn stock
                    return false;
                }
            }
            
            // Nếu quantity = 0, tiến hành xóa (set disable = 1)
            String deleteSql = "UPDATE materials SET disable = 1 WHERE material_id = ?";
            PreparedStatement deletePs = connection.prepareStatement(deleteSql);
            deletePs.setInt(1, materialId);
            int affectedRows = deletePs.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Material getInformation(int materialId) {
        Material m = new Material();
        try {
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, "
                + "m.material_status, m.rack_id, wr.rack_name, wr.rack_code, "
                + "c.category_id, c.category_name, c.description AS category_description, "
                + "u.unit_id, u.unit_name, u.symbol, u.description AS unit_description, "
                + "m.created_at, m.updated_at, m.disable, "
                + "IFNULL(SUM(i.stock), 0) AS quantity "
                + "FROM materials m "
                + "LEFT JOIN categories c ON m.category_id = c.category_id "
                + "LEFT JOIN units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Warehouse_Racks wr ON m.rack_id = wr.rack_id "
                + "LEFT JOIN inventory i ON m.material_id = i.material_id "
                + "WHERE m.material_id = ? "
                + "GROUP BY m.material_id, c.category_name, u.unit_name, u.symbol, wr.rack_name, wr.rack_code";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                m.setQuantity(rs.getBigDecimal("quantity"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                c.setDescription(rs.getString("category_description"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                u.setSymbol(rs.getString("symbol"));
                u.setDescription(rs.getString("unit_description"));
                m.setUnit(u);

                if (rs.getObject("rack_id") != null) {
                    entity.WarehouseRack wr = new entity.WarehouseRack();
                    wr.setRackId(rs.getInt("rack_id"));
                    wr.setRackName(rs.getString("rack_name"));
                    wr.setRackCode(rs.getString("rack_code"));
                    m.setRack(wr);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public void updateMaterial(Material m) {
        String sql = "UPDATE Materials SET material_code = ?, material_name = ?, materials_url = ?, "
                + "material_status = ?, category_id = ?, "
                + "unit_id = ?, updated_at = CURRENT_TIMESTAMP, disable = ? WHERE material_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, m.getMaterialCode());
            st.setString(2, m.getMaterialName());
            st.setString(3, m.getMaterialsUrl());
            st.setString(4, m.getMaterialStatus());
            st.setInt(5, m.getCategory().getCategory_id());
            st.setInt(6, m.getUnit().getId());
            st.setBoolean(7, m.isDisable());
            st.setInt(8, m.getMaterialId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMaterial(Material m) {
        try {
            String sql = "INSERT INTO materials ("
                    + "material_code, material_name, materials_url, material_status, "
                    + "category_id, unit_id, disable"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, m.getMaterialCode());
            ps.setString(2, m.getMaterialName());
            ps.setString(3, m.getMaterialsUrl());
            ps.setString(4, m.getMaterialStatus());
            ps.setInt(5, m.getCategory().getCategory_id());
            ps.setInt(6, m.getUnit().getId());
            ps.setBoolean(7, m.isDisable());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Material getProductById(int materialId) {
        Material product = null;
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.created_at, m.updated_at, m.disable, u.unit_id, u.unit_name, c.category_id, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.material_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                product = new Material();
                product.setMaterialId(rs.getInt("material_id"));
                product.setMaterialCode(rs.getString("material_code"));
                product.setMaterialName(rs.getString("material_name"));
                product.setMaterialsUrl(rs.getString("materials_url"));
                product.setMaterialStatus(rs.getString("material_status"));
                product.setCategory(category);
                product.setUnit(unit);
                product.setCreatedAt(rs.getTimestamp("created_at"));
                product.setUpdatedAt(rs.getTimestamp("updated_at"));
                product.setDisable(rs.getBoolean("disable"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<Material> getAllProducts() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.created_at, m.updated_at, m.disable, u.unit_id, u.unit_name, c.category_id, c.category_name, m.rack_id, wr.rack_name, wr.rack_code, IFNULL(SUM(i.stock),0) AS quantity "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "LEFT JOIN Warehouse_Racks wr ON m.rack_id = wr.rack_id "
                + "LEFT JOIN Inventory i ON m.material_id = i.material_id "
                + "WHERE m.disable = 0 "
                + "GROUP BY m.material_id, u.unit_id, u.unit_name, c.category_id, c.category_name, wr.rack_name, wr.rack_code";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setQuantity(rs.getBigDecimal("quantity"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                if (rs.getObject("rack_id") != null) {
                    entity.WarehouseRack wr = new entity.WarehouseRack();
                    wr.setRackId(rs.getInt("rack_id"));
                    wr.setRackName(rs.getString("rack_name"));
                    wr.setRackCode(rs.getString("rack_code"));
                    m.setRack(wr);
                }
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Material> getAllProductsIncludingDisabled() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, m.material_status, m.created_at, m.updated_at, m.disable, u.unit_id, u.unit_name, c.category_id, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }

                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        MaterialDAO dao = new MaterialDAO();
        List <Material> list = dao.getAllProducts();
        for (Material material : list) {
     
        }
    }

    public List<Material> searchProductsByName(String keyword) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.material_name LIKE ? AND m.disable = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Material> searchProductsByCode(String materialCode) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.material_code LIKE ? AND m.disable = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + materialCode + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Material> searchMaterialsByCategoriesID(int categoryId) {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.category_id = ? AND m.disable = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Material> sortMaterialsByName() {
        List<Material> products = new ArrayList<>();
        String sql = "SELECT m.*, u.unit_name, c.category_name "
                + "FROM Materials m "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "LEFT JOIN Categories c ON m.category_id = c.category_id "
                + "WHERE m.disable = 0 "
                + "ORDER BY m.material_name ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );

                Unit unit = null;
                if (rs.getObject("unit_id") != null) {
                    unit = new Unit(
                            rs.getInt("unit_id"),
                            rs.getString("unit_name")
                    );
                }
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCategory(category);
                m.setUnit(unit);
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));
                products.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Material> getMaterials() throws SQLException {
        List<Material> materials = new ArrayList<>();
        try {
            String sql = "SELECT m.*, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS quantity "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN units u ON m.unit_id = u.unit_id "
                    + "LEFT JOIN inventory i ON m.material_id = i.material_id "
                    + "WHERE m.disable = 0 "
                    + "ORDER BY m.material_id DESC";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setMaterialCode(rs.getString("material_code"));
                material.setMaterialName(rs.getString("material_name"));
                material.setMaterialsUrl(rs.getString("materials_url"));
                material.setMaterialStatus(rs.getString("material_status"));
                material.setQuantity(rs.getBigDecimal("quantity"));
                material.setCreatedAt(rs.getTimestamp("created_at"));
                material.setUpdatedAt(rs.getTimestamp("updated_at"));
                material.setDisable(rs.getBoolean("disable"));

                Category category = new Category();
                category.setCategory_id(rs.getInt("category_id"));
                category.setCategory_name(rs.getString("category_name"));
                material.setCategory(category);

                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                material.setUnit(unit);

                materials.add(material);
            }
             
    } catch (SQLException e) {
        e.printStackTrace();
            throw e;
        }
        return materials;
    }

    public boolean isMaterialCodeExists(String materialCode) {
        String sql = "SELECT 1 FROM materials WHERE material_code = ? AND disable = 0 LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialCode);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getMaterialIdByName(String name) {
        String sql = "SELECT material_id FROM materials WHERE material_name = ? AND disable = 0 LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("material_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public java.util.Map<Integer, String> getMaterialImages(List<Integer> materialIds) {
        java.util.Map<Integer, String> imageMap = new java.util.HashMap<>();
        if (materialIds == null || materialIds.isEmpty()) {
            return imageMap;
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT material_id, materials_url FROM materials WHERE material_id IN (");
            
            for (int i = 0; i < materialIds.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
            }
            sql.append(")");

            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < materialIds.size(); i++) {
                ps.setInt(i + 1, materialIds.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int materialId = rs.getInt("material_id");
                String imageUrl = rs.getString("materials_url");
                imageMap.put(materialId, imageUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageMap;
    }

    public int getMaxMaterialNumber() {
        int maxNumber = 0;
        try {
            String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(material_code, 4) AS SIGNED)), 0) AS max_num FROM materials WHERE material_code LIKE 'MAT%'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int num = rs.getInt("max_num");
                if (!rs.wasNull() && num >= 0) {
                    maxNumber = num;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxNumber;
    }

    public List<Material> getAllMaterials() {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT * FROM Materials WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialName(rs.getString("material_name"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Material> searchMaterialsByPrice(Double minPrice, Double maxPrice, int pageIndex, int pageSize, String sortOption) {
        List<Material> list = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT m.*, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS quantity ")
                    .append("FROM materials m ")
                    .append("LEFT JOIN categories c ON m.category_id = c.category_id ")
                    .append("LEFT JOIN units u ON m.unit_id = u.unit_id AND u.disable = 0 ")
                    .append("LEFT JOIN inventory i ON m.material_id = i.material_id ")
                    .append("WHERE m.disable = 0 ");

            List<Object> params = new ArrayList<>();

            String sortBy = "m.material_code";
            String sortOrder = "ASC";

            switch (sortOption) {
                case "name_asc":
                    sortBy = "m.material_name";
                    sortOrder = "ASC";
                    break;
                case "name_desc":
                    sortBy = "m.material_name";
                    sortOrder = "DESC";
                    break;
                case "code_asc":
                    sortBy = "m.material_code";
                    sortOrder = "ASC";
                    break;
                case "code_desc":
                    sortBy = "m.material_code";
                    sortOrder = "DESC";
                    break;
            }
            sql.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder).append(" ");

            sql.append("LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((pageIndex - 1) * pageSize);

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setQuantity(rs.getBigDecimal("quantity"));

                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                m.setUnit(u);

                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isMaterialNameAndStatusExists(String materialName, String materialStatus) {
        String sql = "SELECT 1 FROM materials WHERE material_name = ? AND material_status = ? AND disable = 0 LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, materialName);
            ps.setString(2, materialStatus);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Material getInformationByNameAndStatus(String name, String status) {
        Material m = new Material();
        try {
            String sql = "SELECT m.material_id, m.material_code, m.material_name, m.materials_url, "
                    + "m.material_status, "
                    + "c.category_id, c.category_name, "
                    + "u.unit_id, u.unit_name, "
                    + "m.created_at, m.updated_at, m.disable "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN units u ON m.unit_id = u.unit_id "
                    + "WHERE m.material_name = ? AND m.material_status = ? AND m.disable = 0";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));
                m.setDisable(rs.getBoolean("disable"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                m.setUnit(u);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public Material getMaterialByName(String materialName) {
        try {
            String sql = "SELECT m.*, c.category_name, u.unit_name, IFNULL(i.stock, 0) AS quantity "
                    + "FROM materials m "
                    + "LEFT JOIN categories c ON m.category_id = c.category_id "
                    + "LEFT JOIN units u ON m.unit_id = u.unit_id AND u.disable = 0 "
                    + "LEFT JOIN inventory i ON m.material_id = i.material_id "
                    + "WHERE m.material_name = ? AND m.disable = 0";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, materialName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Material m = new Material();
                m.setMaterialId(rs.getInt("material_id"));
                m.setMaterialCode(rs.getString("material_code"));
                m.setMaterialName(rs.getString("material_name"));
                m.setMaterialsUrl(rs.getString("materials_url"));
                m.setMaterialStatus(rs.getString("material_status"));
                m.setQuantity(rs.getBigDecimal("quantity"));

                m.setCreatedAt(rs.getTimestamp("created_at"));
                m.setUpdatedAt(rs.getTimestamp("updated_at"));

                Category c = new Category();
                c.setCategory_id(rs.getInt("category_id"));
                c.setCategory_name(rs.getString("category_name"));
                m.setCategory(c);

                Unit u = new Unit();
                u.setId(rs.getInt("unit_id"));
                u.setUnitName(rs.getString("unit_name"));
                m.setUnit(u);

                return m;
            }
        } catch (SQLException e) {
            Logger.getLogger(MaterialDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public int getAvailableStock(int materialId) {
        try {
            String sql = "SELECT IFNULL(stock, 0) AS available_stock FROM inventory WHERE material_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("available_stock");
            }
        } catch (SQLException e) {
            Logger.getLogger(MaterialDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return 0;
    }
}
