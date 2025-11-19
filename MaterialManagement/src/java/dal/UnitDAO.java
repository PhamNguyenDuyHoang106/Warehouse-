package dal;

import entity.DBContext;
import entity.Unit;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnitDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UnitDAO.class.getName());

    private static final String BASE_SELECT =
        "SELECT " +
            "unit_id, " +
            "unit_code, " +
            "unit_name, " +
            "symbol, " +
            "is_base, " +
            "status " +
        "FROM Units";

    private Unit mapUnit(ResultSet rs) throws SQLException {
        Unit unit = new Unit();
        unit.setId(rs.getInt("unit_id"));
        unit.setUnitCode(rs.getString("unit_code"));
        unit.setUnitName(rs.getString("unit_name"));
        unit.setSymbol(rs.getString("symbol"));
        // Schema v11: Units table doesn't have 'description' column
        // unit.setDescription(rs.getString("description"));
        // Schema v11: No base_unit_id, conversion_rate columns
        // unit.setBaseUnitId((Integer) rs.getObject("base_unit_id"));
        // unit.setConversionRate(rs.getBigDecimal("conversion_rate"));
        unit.setBase(rs.getBoolean("is_base"));
        unit.setStatus(rs.getString("status"));
        // Schema v11: Units table doesn't have created_at, deleted_at columns
        // unit.setCreatedAt(rs.getTimestamp("created_at"));
        // unit.setDeletedAt(rs.getTimestamp("deleted_at"));
        return unit;
    }

    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY unit_name ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                units.add(mapUnit(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading units", ex);
        }
        return units;
    }

    public void addUnit(Unit unit) {
        String sql = 
            "INSERT INTO Units (" +
                "unit_code, " +
                "unit_name, " +
                "symbol, " +
                "is_base, " +
                "status " +
            ") VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resolveUnitCode(unit));
            ps.setString(2, unit.getUnitName());
            ps.setString(3, unit.getSymbol());
            ps.setBoolean(4, unit.isBase());
            ps.setString(5, unit.getStatus() != null ? unit.getStatus() : "active");
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error adding unit", ex);
            throw new RuntimeException("Không thể thêm đơn vị đo. Vui lòng kiểm tra mã/ tên đơn vị.", ex);
        }
    }

    public Unit getUnitById(int id) {
        String sql = BASE_SELECT + " WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUnit(rs);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading unit by id", ex);
        }
        return null;
    }

    public void updateUnit(Unit unit) {
        String sql = 
            "UPDATE Units " +
               "SET unit_code = ?, " +
                   "unit_name = ?, " +
                   "symbol = ?, " +
                   "is_base = ?, " +
                   "status = ? " +
             "WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, resolveUnitCode(unit));
            ps.setString(2, unit.getUnitName());
            ps.setString(3, unit.getSymbol());
            ps.setBoolean(4, unit.isBase());
            ps.setString(5, unit.getStatus() != null ? unit.getStatus() : "active");
            ps.setInt(6, unit.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating unit", ex);
            throw new RuntimeException("Không thể cập nhật đơn vị đo.", ex);
        }
    }

    public void deleteUnit(int id) {
        // Schema v11: Units table doesn't have deleted_at column
        // Soft delete not supported in v11 schema
        String sql = "DELETE FROM Units WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error soft deleting unit", ex);
        }
    }

    public List<Unit> searchUnitsByNameOrSymbol(String keyword) {
        List<Unit> units = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ? OR LOWER(unit_code) LIKE ?)");
        }
        sql.append(" ORDER BY unit_name ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    units.add(mapUnit(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error searching units", ex);
        }
        return units;
    }

    public List<Unit> getUnitsByPage(int offset, int limit, String keyword) {
        List<Unit> units = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ? OR LOWER(unit_code) LIKE ?)");
        }
        sql.append(" ORDER BY unit_name ASC LIMIT ? OFFSET ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    units.add(mapUnit(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error paginating units", ex);
        }
        return units;
    }

    public int countUnits(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Units");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ? OR LOWER(unit_code) LIKE ?)");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error counting units", ex);
        }
        return 0;
    }

    public boolean isUnitNameExists(String unitName) {
        String sql = "SELECT 1 FROM Units WHERE LOWER(unit_name) = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unitName.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking unit name existence", ex);
        }
        return false;
    }

    private String resolveUnitCode(Unit unit) {
        if (unit.getUnitCode() != null && !unit.getUnitCode().trim().isEmpty()) {
            return unit.getUnitCode().trim().toUpperCase();
        }
        String name = unit.getUnitName() != null ? unit.getUnitName().trim() : "UNIT";
        String base = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (base.isEmpty()) {
            base = "UNIT";
        }
        return base.length() > 8 ? base.substring(0, 8) : base;
    }
}
