package dal;

import entity.DBContext;
import entity.ExportRequestDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportRequestDetailDAO extends DBContext {
    
    public List<ExportRequestDetail> getByRequestId(int requestId) {
        connection = getConnection();
        List<ExportRequestDetail> details = new ArrayList<>();
        String sql = "SELECT erd.detail_id, erd.export_request_id, erd.material_id, "
                + "erd.quantity, erd.created_at, erd.updated_at, "
                + "m.material_code, m.material_name, m.materials_url, u.unit_name as material_unit "
                + "FROM Export_Request_Details erd "
                + "JOIN Materials m ON erd.material_id = m.material_id "
                + "LEFT JOIN Units u ON m.unit_id = u.unit_id "
                + "WHERE erd.export_request_id = ?";
                
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportRequestDetail detail = new ExportRequestDetail();
                    detail.setDetailId(rs.getInt("detail_id"));
                    detail.setExportRequestId(rs.getInt("export_request_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setMaterialCode(rs.getString("material_code"));
                    detail.setMaterialName(rs.getString("material_name"));
                    detail.setMaterialUnit(rs.getString("material_unit"));
                    detail.setQuantity(rs.getInt("quantity"));
                    String rawUrl = rs.getString("materials_url");
                    String imgUrl = rawUrl;
                    if (imgUrl != null && !imgUrl.isEmpty() && !imgUrl.startsWith("/") && !imgUrl.startsWith("http") && !imgUrl.startsWith("images/material/")) {
                        imgUrl = "images/material/" + imgUrl;
                    }
                    detail.setMaterialImageUrl(imgUrl);
                    detail.setCreatedAt(rs.getTimestamp("created_at"));
                    detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in getByRequestId: " + e.getMessage());
            e.printStackTrace();
        }
        return details;
    }
}