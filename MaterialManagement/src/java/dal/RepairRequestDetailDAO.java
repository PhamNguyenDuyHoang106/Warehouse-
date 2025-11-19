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
        String sql = "SELECT rrd.*, m.material_code, m.material_name, m.url, m.status AS material_status, " +
                     "c.category_name, u.unit_name AS unit_name " +
                     "FROM Repair_Request_Details rrd " +
                     "LEFT JOIN Materials m ON rrd.spare_material_id = m.material_id " +
                     "LEFT JOIN Categories c ON m.category_id = c.category_id " +
                     "LEFT JOIN Units u ON rrd.unit_id = u.unit_id " +
                     "WHERE rrd.rr_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairRequestDetail detail = new RepairRequestDetail();
                    detail.setDetailId(rs.getInt("rrd_id"));
                    detail.setRepairRequestId(rs.getInt("rr_id"));
                    Integer spareMaterialId = rs.getObject("spare_material_id") != null ? rs.getInt("spare_material_id") : null;
                    detail.setMaterialId(spareMaterialId);
                    detail.setUnitId(rs.getObject("unit_id") != null ? rs.getInt("unit_id") : null);
                    detail.setQuantity(rs.getBigDecimal("quantity_needed"));
                    detail.setNote(rs.getString("note"));
                    detail.setCreatedAt(rs.getTimestamp("created_at"));

                    // Tạo đối tượng Material (chỉ khi có spare_material_id)
                    if (spareMaterialId != null) {
                        Material material = new Material();
                        material.setMaterialId(detail.getMaterialId());
                        material.setMaterialCode(rs.getString("material_code"));
                        material.setMaterialName(rs.getString("material_name"));
                        material.setUrl(rs.getString("url"));
                        material.setMaterialStatus(rs.getString("material_status"));

                        // Tạo đối tượng Category
                        Category category = new Category();
                        category.setCategory_name(rs.getString("category_name"));
                        material.setCategory(category);

                        // Tạo đối tượng Unit
                        Unit unit = new Unit();
                        unit.setUnitName(rs.getString("unit_name"));
                        material.setUnit(unit);

                        detail.setMaterial(material);
                    }
                    details.add(detail);
                }
            }
        }
        return details;
    }
}