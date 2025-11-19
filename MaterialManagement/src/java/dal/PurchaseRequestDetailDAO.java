package dal;

import entity.PurchaseRequestDetail;
import entity.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDetailDAO extends DBContext {

    private static final String BASE_SELECT =
            "SELECT d.pr_detail_id, d.pr_id, d.material_id, d.unit_id, d.quantity, d.unit_price_est, d.total_est, d.note, d.created_at, "
          + "m.material_code, m.material_name, u.unit_name "
          + "FROM Purchase_Request_Details d "
          + "LEFT JOIN Materials m ON d.material_id = m.material_id "
          + "LEFT JOIN Units u ON d.unit_id = u.unit_id";

    private PurchaseRequestDetail mapDetail(ResultSet rs) throws SQLException {
        PurchaseRequestDetail detail = new PurchaseRequestDetail();
        detail.setId(rs.getInt("pr_detail_id"));
        detail.setPurchaseRequestId(rs.getInt("pr_id"));
        detail.setMaterialId(rs.getInt("material_id"));
        detail.setUnitId((Integer) rs.getObject("unit_id"));
        detail.setQuantity(rs.getBigDecimal("quantity"));
        detail.setUnitPriceEstimate(rs.getBigDecimal("unit_price_est"));
        detail.setTotalEstimate(rs.getBigDecimal("total_est"));
        detail.setNote(rs.getString("note"));
        detail.setCreatedAt(rs.getTimestamp("created_at"));
        detail.setMaterialCode(rs.getString("material_code"));
        detail.setMaterialName(rs.getString("material_name"));
        detail.setUnitName(rs.getString("unit_name"));
        return detail;
    }

    public List<PurchaseRequestDetail> paginationOfDetails(int purchaseRequestId, int page, int pageSize) {
        List<PurchaseRequestDetail> list = new ArrayList<>();
        String sql = BASE_SELECT +
                " WHERE d.pr_id = ?" +
                " ORDER BY d.pr_detail_id" +
                " LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ps.setInt(2, pageSize);
            ps.setInt(3, Math.max(0, (page - 1) * pageSize));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int count(int purchaseRequestId) {
        String sql = "SELECT COUNT(*) FROM Purchase_Request_Details WHERE pr_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
