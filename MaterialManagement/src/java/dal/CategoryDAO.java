package dal;

import entity.Category;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    private static final String BASE_SELECT =
        "SELECT " +
            "category_id, " +
            "category_code, " +
            "category_name, " +
            "parent_id, " +
            "level_depth, " +
            "description, " +
            "status, " +
            "created_at, " +
            "updated_at, " +
            "deleted_at " +
        "FROM Categories";

    private String lastError;

    public CategoryDAO() {
        super();
        lastError = null;
    }

    public String getLastError() {
        return lastError;
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryCode(rs.getString("category_code"));
        category.setCategoryName(rs.getString("category_name"));
        category.setParentId((Integer) rs.getObject("parent_id"));
        category.setLevelDepth((Integer) rs.getObject("level_depth"));
        // path_ltree column may not exist in database, handle gracefully
        try {
            category.setPathLtree(rs.getString("path_ltree"));
        } catch (SQLException e) {
            category.setPathLtree(null);
        }
        category.setDescription(rs.getString("description"));
        category.setStatus(rs.getString("status"));
        // created_at, updated_at, deleted_at may not exist in all database versions
        try {
            category.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            category.setCreatedAt(null);
        }
        try {
            category.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            category.setUpdatedAt(null);
        }
        try {
            category.setDeletedAt(rs.getTimestamp("deleted_at"));
        } catch (SQLException e) {
            category.setDeletedAt(null);
        }
        return category;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE deleted_at IS NULL ORDER BY category_name ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        } catch (SQLException ex) {
            lastError = "Error in getAllCategories: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) {
        String sql = BASE_SELECT + " WHERE category_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        } catch (SQLException ex) {
            lastError = "Error getCategoryById: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return null;
    }

    public boolean insertCategory(Category category) {
        if (connection == null) {
            lastError = "Database connection is null";
            return false;
        }

        String categoryCode = resolveCategoryCode(category);
        int levelDepth = 1;
        String path = categoryCode;

        if (category.getParentId() != null) {
            Category parent = getCategoryById(category.getParentId());
            if (parent != null) {
                levelDepth = parent.getLevelDepth() != null ? parent.getLevelDepth() + 1 : 2;
                path = parent.getPathLtree() != null
                        ? parent.getPathLtree() + "." + categoryCode
                        : parent.getCategoryCode() + "." + categoryCode;
            }
        }

        String sql = 
            "INSERT INTO Categories (" +
                "category_code, " +
                "category_name, " +
                "parent_id, " +
                "level_depth, " +
                "description, " +
                "status " +
            ") VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoryCode);
            ps.setString(2, category.getCategoryName());
            if (category.getParentId() != null) {
                ps.setInt(3, category.getParentId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, levelDepth);
            ps.setString(5, category.getDescription());
            ps.setString(6, category.getStatus() != null ? category.getStatus() : "active");
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            lastError = "Error adding category: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
            return false;
        }
    }

    public boolean updateCategory(Category category) {
        String categoryCode = resolveCategoryCode(category);
        int levelDepth = 1;
        String path = categoryCode;

        if (category.getParentId() != null) {
            Category parent = getCategoryById(category.getParentId());
            if (parent != null) {
                levelDepth = parent.getLevelDepth() != null ? parent.getLevelDepth() + 1 : 2;
                path = parent.getPathLtree() != null
                        ? parent.getPathLtree() + "." + categoryCode
                        : parent.getCategoryCode() + "." + categoryCode;
            }
        }

        String sql = 
            "UPDATE Categories " +
               "SET category_code = ?, " +
                   "category_name = ?, " +
                   "parent_id = ?, " +
                   "level_depth = ?, " +
                   "description = ?, " +
                   "status = ? " +
             "WHERE category_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoryCode);
            ps.setString(2, category.getCategoryName());
            if (category.getParentId() != null) {
                ps.setInt(3, category.getParentId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, levelDepth);
            ps.setString(5, category.getDescription());
            ps.setString(6, category.getStatus() != null ? category.getStatus() : "active");
            ps.setInt(7, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            lastError = "Error in updateCategory: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "UPDATE Categories SET deleted_at = CURRENT_TIMESTAMP WHERE category_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            lastError = "Error deleteCategory: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
            return false;
        }
    }

    public List<Category> searchCategories(String name, String status, String sortBy, String sortOrder) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" WHERE deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND category_name LIKE ?");
            params.add("%" + name.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            String column;
            if ("status".equalsIgnoreCase(sortBy)) {
                column = "status";
            } else if ("code".equalsIgnoreCase(sortBy)) {
                column = "category_code";
            } else if ("level".equalsIgnoreCase(sortBy)) {
                column = "level_depth";
            } else {
                column = "category_name";
            }
            String order = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
            sql.append(" ORDER BY ").append(column).append(" ").append(order);
        } else {
            sql.append(" ORDER BY category_name ASC");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCategory(rs));
                }
            }
        } catch (SQLException ex) {
            lastError = "Error searchCategories: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return list;
    }

    public int getMaxCategoryNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(category_code, 4) AS SIGNED)), 0) AS max_num FROM Categories WHERE category_code LIKE 'CAT%'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_num");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getMaxCategoryNumber", ex);
        }
        return 0;
    }

    public String getParentCategoryName(Integer parentId) {
        if (parentId == null) {
            return "None";
        }
        String sql = "SELECT category_name FROM Categories WHERE category_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                }
            }
        } catch (SQLException ex) {
            lastError = "Error getParentCategoryName: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return "Unknown";
    }

    public boolean isCategoryNameExists(String categoryName, Integer excludeCategoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Categories WHERE LOWER(category_name) = LOWER(?) AND deleted_at IS NULL");
        if (excludeCategoryId != null) {
            sql.append(" AND category_id != ?");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, categoryName.trim());
            if (excludeCategoryId != null) {
                ps.setInt(2, excludeCategoryId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            lastError = "Error isCategoryNameExists: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return false;
    }

    public boolean isCategoryCodeExists(String categoryCode, Integer excludeCategoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Categories WHERE LOWER(category_code) = LOWER(?) AND deleted_at IS NULL");
        if (excludeCategoryId != null) {
            sql.append(" AND category_id != ?");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, categoryCode.trim());
            if (excludeCategoryId != null) {
                ps.setInt(2, excludeCategoryId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            lastError = "Error isCategoryCodeExists: " + ex.getMessage();
            LOGGER.log(Level.SEVERE, lastError, ex);
        }
        return false;
    }

    public List<Category> getCategoryTree(int maxLevel, int maxChildrenPerNode) {
        List<Category> all = getAllCategories();
        List<Category> tree = new ArrayList<>();
        buildTree(tree, all, null, 1, maxLevel, maxChildrenPerNode);
        return tree;
    }

    private void buildTree(List<Category> tree, List<Category> all, Integer parentId, int level, int maxLevel, int maxChildren) {
        if (level > maxLevel) {
            return;
        }
        int count = 0;
        for (Category category : all) {
            if ((parentId == null && category.getParentId() == null)
                    || (parentId != null && parentId.equals(category.getParentId()))) {
                tree.add(category);
                count++;
                if (count >= maxChildren) {
                    break;
                }
                buildTree(tree, all, category.getCategoryId(), level + 1, maxLevel, maxChildren);
            }
        }
    }

    private String resolveCategoryCode(Category category) {
        if (category.getCategoryCode() != null && !category.getCategoryCode().trim().isEmpty()) {
            return category.getCategoryCode().trim().toUpperCase();
        }
        String name = category.getCategoryName() != null ? category.getCategoryName().trim() : "CAT";
        String base = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (base.isEmpty()) {
            base = "CAT";
        }
        if (base.length() > 8) {
            base = base.substring(0, 8);
        }
        int next = getMaxCategoryNumber() + 1;
        return String.format("%s%03d", base, next);
    }
}
