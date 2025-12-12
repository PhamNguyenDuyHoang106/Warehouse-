package dal;

import entity.DBContext;
import entity.ExportRequestDetail;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportRequestDetailDAO extends DBContext {
    private static final Logger LOGGER = Logger.getLogger(ExportRequestDetailDAO.class.getName());
    
    public List<ExportRequestDetail> getByRequestId(int requestId) {
        connection = getConnection();
        List<ExportRequestDetail> details = new ArrayList<>();
        // Schema thực tế sử dụng erd_id (primary key), er_id (foreign key)
        // Query cơ bản với các cột chắc chắn có, các cột optional sẽ được xử lý an toàn
        String sql = "SELECT erd.erd_id, erd.er_id, erd.material_id, erd.unit_id, "
                + "erd.quantity, erd.note, erd.created_at, erd.warehouse_id, erd.unit_price_export, "
                + "m.material_code, m.material_name, m.url, "
                + "COALESCE(u.unit_name, du.unit_name) as material_unit "
                + "FROM Export_Request_Details erd "
                + "JOIN Materials m ON erd.material_id = m.material_id "
                + "LEFT JOIN Units u ON erd.unit_id = u.unit_id "
                + "LEFT JOIN Units du ON m.default_unit_id = du.unit_id "
                + "WHERE erd.er_id = ?";
                
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExportRequestDetail detail = new ExportRequestDetail();
                    detail.setDetailId(rs.getInt("erd_id")); // Sử dụng erd_id thay vì detail_id
                    detail.setExportRequestId(rs.getInt("er_id"));
                    detail.setMaterialId(rs.getInt("material_id"));
                    detail.setMaterialCode(rs.getString("material_code"));
                    detail.setMaterialName(rs.getString("material_name"));
                    detail.setMaterialUnit(rs.getString("material_unit"));
                    detail.setQuantity(rs.getBigDecimal("quantity"));
                    detail.setMaterialImageUrl(rs.getString("url"));
                    detail.setCreatedAt(rs.getTimestamp("created_at"));
                    detail.setWarehouseId((Integer) rs.getObject("warehouse_id"));
                    detail.setUnitPriceExport(rs.getBigDecimal("unit_price_export"));
                    detail.setNote(rs.getString("note"));
                    
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in getByRequestId", e);
        }
        return details;
    }
    
    public boolean addDetails(List<ExportRequestDetail> details) {
        if (details == null || details.isEmpty()) {
            return true;
        }
        
        // Sử dụng đúng tên cột như trong ExportRequestDAO.java: er_id (foreign key)
        String sql = "INSERT INTO Export_Request_Details (er_id, material_id, rack_id, warehouse_id, quantity, unit_price_export, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (ExportRequestDetail detail : details) {
                ps.setInt(1, detail.getExportRequestId());
                ps.setInt(2, detail.getMaterialId());
                ps.setObject(3, detail.getRackId());
                ps.setObject(4, detail.getWarehouseId());
                ps.setBigDecimal(5, detail.getQuantity());
                if (detail.getUnitPriceExport() != null) {
                    ps.setBigDecimal(6, detail.getUnitPriceExport());
                } else {
                    ps.setNull(6, java.sql.Types.DECIMAL);
                }
                if (detail.getNote() != null && !detail.getNote().trim().isEmpty()) {
                    ps.setString(7, detail.getNote());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            return results.length == details.size();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding export request details", e);
            return false;
        }
    }
}