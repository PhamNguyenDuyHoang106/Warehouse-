package dal;

import entity.DBContext;
import entity.RepairRequestDetail;
import entity.Material;
import entity.Category;
import entity.Unit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepairRequestDetailDAO extends DBContext {

    public List<RepairRequestDetail> getRepairRequestDetailsByRequestId(int repairRequestId) throws SQLException {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT rrd.*, m.material_code, m.material_name, m.materials_url, m.material_status, " +
                     "c.category_name, u.unit_name, s.supplier_name " +
                     "FROM Repair_Request_Details rrd " +
                     "JOIN Materials m ON rrd.material_id = m.material_id " +
                     "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                     "LEFT JOIN Units u ON m.unit_id = u.unit_id " +
                     "LEFT JOIN Suppliers s ON rrd.supplier_id = s.supplier_id " +
                     "WHERE rrd.repair_request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequestDetail detail = new RepairRequestDetail();
                    detail.setDetailId(rs.getInt("detail_id"));
                    detail.setRepairRequestId(rs.getInt("repair_request_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setQuantity(rs.getBigDecimal("quantity"));
                    detail.setDamageDescription(rs.getString("damage_description"));
                    detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                    detail.setCreatedAt(rs.getTimestamp("created_at"));
                    detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                    detail.setSupplierId(rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : 0);
                    detail.setSupplierName(rs.getString("supplier_name"));

                    // Tạo đối tượng Material
                    Material material = new Material();
                    material.setMaterialId(rs.getInt("material_id"));
                    material.setMaterialCode(rs.getString("material_code"));
                    material.setMaterialName(rs.getString("material_name"));
                    material.setMaterialsUrl(rs.getString("materials_url"));
                    material.setMaterialStatus(rs.getString("material_status"));
                    // Bỏ setPrice và mọi dòng liên quan đến condition

                    // Tạo đối tượng Category
                    Category category = new Category();
                    category.setCategory_name(rs.getString("category_name"));
                    material.setCategory(category);

                    // Tạo đối tượng Unit
                    Unit unit = new Unit();
                    unit.setUnitName(rs.getString("unit_name"));
                    material.setUnit(unit);

                    detail.setMaterial(material);
                    details.add(detail);
                }
            }
        }
        return details;
    }
}