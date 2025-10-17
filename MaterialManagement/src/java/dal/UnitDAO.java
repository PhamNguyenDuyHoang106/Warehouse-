package dal;

import entity.DBContext;
import entity.Unit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO extends DBContext {
    
    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        try {
            String sql = "SELECT * FROM units WHERE disable = 0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                units.add(unit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return units;
    }

    public void addUnit(Unit unit) {
        String sql = "INSERT INTO units (unit_name, symbol, description, disable) VALUES (?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getSymbol());
            ps.setString(3, unit.getDescription());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Unit getUnitById(int id) {
        String sql = "SELECT * FROM units WHERE unit_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                return unit;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateUnit(Unit unit) {
        String sql = "UPDATE units SET unit_name = ?, symbol = ?, description = ? WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getSymbol());
            ps.setString(3, unit.getDescription());
            ps.setInt(4, unit.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUnit(int id) {
        String sql = "UPDATE units SET disable = 1 WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Unit> searchUnitsByNameOrSymbol(String keyword) {
        List<Unit> units = new ArrayList<>();
        try {
            String sql = "SELECT * FROM units WHERE disable = 0 AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                units.add(unit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return units;
    }

    public List<Unit> getUnitsByPage(int offset, int limit, String keyword) {
        List<Unit> units = new ArrayList<>();
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM units WHERE disable = 0");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ?)");
            }
            sql.append(" ORDER BY unit_id ASC LIMIT ? OFFSET ?");
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.toLowerCase() + "%";
                ps.setString(idx++, likeKeyword);
                ps.setString(idx++, likeKeyword);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("unit_id"));
                unit.setUnitName(rs.getString("unit_name"));
                unit.setSymbol(rs.getString("symbol"));
                unit.setDescription(rs.getString("description"));
                units.add(unit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return units;
    }

    public int countUnits(String keyword) {
        int count = 0;
        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM units WHERE disable = 0");
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND (LOWER(unit_name) LIKE ? OR LOWER(symbol) LIKE ?)");
            }
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.toLowerCase() + "%";
                ps.setString(idx++, likeKeyword);
                ps.setString(idx++, likeKeyword);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }

    // Kiểm tra tên đơn vị đã tồn tại chưa (không phân biệt hoa thường, không tính disable)
    public boolean isUnitNameExists(String unitName) {
        String sql = "SELECT 1 FROM units WHERE LOWER(unit_name) = ? AND disable = 0 LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, unitName.toLowerCase());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xoá tất cả vật tư có unit_id này - KHÔNG SỬ DỤNG NỮA
    /*
    public void deleteMaterialsByUnitId(int unitId) {
        String sql = "DELETE FROM materials WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, unitId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    */

    // Xoá mềm tất cả vật tư có unit_id này (disable=1) - KHÔNG SỬ DỤNG NỮA
    /*
    public void disableMaterialsByUnitId(int unitId) {
        String sql = "UPDATE materials SET disable = 1 WHERE unit_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, unitId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    */

    // Xoá mềm vật tư trước, sau đó xoá mềm đơn vị - KHÔNG SỬ DỤNG NỮA
    /*
    public void deleteUnitAndMaterials(int unitId) {
        disableMaterialsByUnitId(unitId);
        deleteUnit(unitId); // deleteUnit đã là disable=1
    }
    */
} 