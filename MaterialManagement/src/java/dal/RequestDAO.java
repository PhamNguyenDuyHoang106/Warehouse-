package dal;

import entity.DBContext;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import entity.PurchaseOrder;
import entity.PurchaseOrderDetail;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.RepairRequest;
import entity.RepairRequestDetail;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO extends DBContext {

    public List<ExportRequest> getExportRequestsByUser(int userId, int page, int pageSize, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<ExportRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT er.*, u1.full_name AS user_name, u3.full_name AS approver_name "
                + "FROM Export_Requests er "
                + "JOIN Users u1 ON er.request_by = u1.user_id "
                + "LEFT JOIN Users u3 ON er.approved_by = u3.user_id "
                + "LEFT JOIN Export_Request_Details erd ON er.er_id = erd.er_id "
                + "LEFT JOIN Materials m ON erd.material_id = m.material_id "
                + "WHERE er.request_by = ? AND er.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(er.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND er.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND er.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        sql.append("ORDER BY er.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("er_id"));
                request.setRequestCode(rs.getString("er_code"));
                request.setUserId(rs.getInt("request_by"));
                request.setUserName(rs.getString("user_name"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setDeliveryDate(rs.getDate("expected_date"));
                request.setReason(rs.getString("purpose"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approver_name"));
                // Schema v11: Export_Requests doesn't have approval_reason, rejection_reason columns
                // request.setApprovalReason(rs.getString("approval_reason"));
                // request.setApprovedAt(rs.getTimestamp("approved_at"));
                // request.setRejectionReason(rs.getString("rejection_reason"));
                request.setDetails(getExportRequestDetails(request.getExportRequestId()));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public ExportRequest getExportRequestById(int exportRequestId) {
        String sql = "SELECT er.*, u1.full_name AS user_name, u3.full_name AS approver_name "
                + "FROM Export_Requests er "
                + "JOIN Users u1 ON er.request_by = u1.user_id "
                + "LEFT JOIN Users u3 ON er.approved_by = u3.user_id "
                + "WHERE er.er_id = ? AND er.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ExportRequest request = new ExportRequest();
                request.setExportRequestId(rs.getInt("er_id"));
                request.setRequestCode(rs.getString("er_code"));
                request.setUserId(rs.getInt("request_by"));
                request.setUserName(rs.getString("user_name"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setDeliveryDate(rs.getDate("expected_date"));
                request.setReason(rs.getString("purpose"));
                request.setApprovedBy(rs.getInt("approved_by"));
                request.setApproverName(rs.getString("approver_name"));
                // Schema v11: Export_Requests doesn't have approval_reason, rejection_reason columns
                // request.setApprovalReason(rs.getString("approval_reason"));
                // request.setApprovedAt(rs.getTimestamp("approved_at"));
                // request.setRejectionReason(rs.getString("rejection_reason"));
                request.setDetails(getExportRequestDetails(rs.getInt("er_id")));
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ExportRequestDetail> getExportRequestDetails(int exportRequestId) {
        List<ExportRequestDetail> details = new ArrayList<>();
        String sql = "SELECT erd.detail_id, erd.er_id, erd.material_id, erd.unit_id, erd.quantity, erd.unit_price_export, erd.status, erd.created_at, erd.updated_at, "
                + "m.material_code, m.material_name, u.unit_name "
                + "FROM Export_Request_Details erd "
                + "JOIN Materials m ON erd.material_id = m.material_id "
                + "LEFT JOIN Units u ON erd.unit_id = u.unit_id "
                + "WHERE erd.er_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExportRequestDetail detail = new ExportRequestDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setExportRequestId(rs.getInt("er_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setMaterialUnit(rs.getString("unit_name"));
                detail.setUnitName(rs.getString("unit_name"));
                detail.setQuantity(rs.getBigDecimal("quantity"));
                detail.setUnitPriceExport(rs.getBigDecimal("unit_price_export"));
                detail.setStatus(rs.getString("status"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private PurchaseRequest mapPurchaseRequest(ResultSet rs) throws SQLException {
        PurchaseRequest request = new PurchaseRequest();
        request.setId(rs.getInt("pr_id"));
        request.setCode(rs.getString("pr_code"));
        request.setRequestBy(rs.getInt("request_by"));
        request.setDepartmentId((Integer) rs.getObject("department_id"));
        Date requestDate = rs.getDate("request_date");
        if (requestDate != null) {
            request.setRequestDate(requestDate);
        }
        Date expectedDate = rs.getDate("expected_date");
        if (expectedDate != null) {
            request.setExpectedDate(expectedDate);
        }
        request.setTotalAmount(rs.getBigDecimal("total_amount"));
        request.setStatus(rs.getString("status"));
        request.setApprovedBy((Integer) rs.getObject("approved_by"));
        request.setApprovedAt(rs.getTimestamp("approved_at"));
        request.setReason(rs.getString("reason"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setUpdatedAt(rs.getTimestamp("updated_at"));
        request.setDeletedAt(rs.getTimestamp("deleted_at"));
        request.setRequesterName(rs.getString("requester_name"));
        request.setDepartmentName(rs.getString("department_name"));
        return request;
    }

    private List<PurchaseRequestDetail> getPurchaseRequestDetails(int purchaseRequestId) {
        List<PurchaseRequestDetail> details = new ArrayList<>();
        String sql = "SELECT d.pr_detail_id, d.pr_id, d.material_id, d.unit_id, d.quantity, d.unit_price_est, d.total_est, d.note, d.created_at, "
                + "m.material_code, m.material_name, u.unit_name "
                + "FROM Purchase_Request_Details d "
                + "LEFT JOIN Materials m ON d.material_id = m.material_id "
                + "LEFT JOIN Units u ON d.unit_id = u.unit_id "
                + "WHERE d.pr_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setId(rs.getInt("pr_detail_id"));
                detail.setPurchaseRequestId(rs.getInt("pr_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setUnitId((Integer) rs.getObject("unit_id"));
                detail.setUnitName(rs.getString("unit_name"));
                detail.setQuantity(rs.getBigDecimal("quantity"));
                detail.setUnitPriceEstimate(rs.getBigDecimal("unit_price_est"));
                detail.setTotalEstimate(rs.getBigDecimal("total_est"));
                detail.setNote(rs.getString("note"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private List<RepairRequestDetail> getRepairRequestDetails(int repairRequestId) {
        List<RepairRequestDetail> details = new ArrayList<>();
        String sql = "SELECT rrd.rrd_id, rrd.rr_id, rrd.spare_material_id, rrd.unit_id, rrd.quantity_needed, rrd.note, rrd.created_at, rrd.updated_at, "
                + "m.material_code, m.material_name, u.unit_name "
                + "FROM Repair_Request_Details rrd "
                + "LEFT JOIN Materials m ON rrd.spare_material_id = m.material_id "
                + "LEFT JOIN Units u ON rrd.unit_id = u.unit_id "
                + "WHERE rrd.rr_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequestDetail detail = new RepairRequestDetail();
                detail.setDetailId(rs.getInt("rrd_id"));
                detail.setRepairRequestId(rs.getInt("rr_id"));
                detail.setMaterialId(rs.getInt("spare_material_id"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setUnitName(rs.getString("unit_name"));
                detail.setQuantity(rs.getBigDecimal("quantity_needed"));
                detail.setDamageDescription(rs.getString("note"));
                // Schema v11: Repair_Request_Details doesn't have repair_cost column
                // detail.setRepairCost(rs.getObject("repair_cost") != null ? rs.getDouble("repair_cost") : null);
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setUpdatedAt(rs.getTimestamp("updated_at"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public int getExportRequestCountByUser(int userId, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT er.er_id) FROM `Export_Requests` er "
                + "LEFT JOIN `Export_Request_Details` erd ON er.er_id = erd.er_id "
                + "LEFT JOIN `Materials` m ON erd.material_id = m.material_id "
                + "WHERE er.request_by = ? AND er.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(er.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND er.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND er.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PurchaseRequest> getPurchaseRequestsByUser(int userId, int page, int pageSize, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<PurchaseRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT pr.pr_id, pr.pr_code, pr.request_by, pr.department_id, pr.request_date, pr.expected_date, pr.total_amount, pr.status, "
                        + "pr.approved_by, pr.approved_at, pr.reason, pr.created_at, pr.updated_at, pr.deleted_at, "
                        + "u.full_name AS requester_name, d.department_name, u2.full_name AS approver_name "
                        + "FROM Purchase_Requests pr "
                        + "LEFT JOIN Users u ON pr.request_by = u.user_id "
                        + "LEFT JOIN Departments d ON pr.department_id = d.department_id "
                        + "LEFT JOIN Users u2 ON pr.approved_by = u2.user_id "
                        + "LEFT JOIN Purchase_Request_Details prd ON pr.pr_id = prd.pr_id "
                        + "LEFT JOIN Materials m ON prd.material_id = m.material_id "
                        + "WHERE pr.deleted_at IS NULL AND pr.request_by = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND pr.status = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND pr.request_date >= ? ");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append("AND pr.request_date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        sql.append("ORDER BY pr.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(Math.max(0, (page - 1) * pageSize));

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseRequest request = mapPurchaseRequest(rs);
                request.setDetails(getPurchaseRequestDetails(request.getId()));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public PurchaseRequest getPurchaseRequestById(int purchaseRequestId) {
        String sql = "SELECT pr.pr_id, pr.pr_code, pr.request_by, pr.department_id, pr.request_date, pr.expected_date, pr.total_amount, pr.status, "
                + "pr.approved_by, pr.approved_at, pr.reason, pr.created_at, pr.updated_at, pr.deleted_at, u.full_name AS requester_name, d.department_name "
                + "FROM Purchase_Requests pr "
                + "LEFT JOIN Users u ON pr.request_by = u.user_id "
                + "LEFT JOIN Departments d ON pr.department_id = d.department_id "
                + "WHERE pr.pr_id = ? AND pr.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PurchaseRequest request = mapPurchaseRequest(rs);
                request.setDetails(getPurchaseRequestDetails(request.getId()));
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPurchaseRequestCountByUser(int userId, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT pr.pr_id) FROM Purchase_Requests pr "
                        + "LEFT JOIN Purchase_Request_Details prd ON pr.pr_id = prd.pr_id "
                        + "LEFT JOIN Materials m ON prd.material_id = m.material_id "
                        + "WHERE pr.deleted_at IS NULL AND pr.request_by = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND pr.status = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND pr.request_date >= ? ");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append("AND pr.request_date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<RepairRequest> getRepairRequestsByUser(int userId, int page, int pageSize, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<RepairRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT rr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "LEFT JOIN Users u2 ON rr.approved_by = u2.user_id "
                + "LEFT JOIN Repair_Request_Details rrd ON rr.rr_id = rrd.rr_id "
                + "LEFT JOIN Materials m ON rrd.spare_material_id = m.material_id "
                + "WHERE rr.request_by = ? AND rr.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(rr.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND rr.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND rr.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        sql.append("ORDER BY rr.request_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RepairRequest request = new RepairRequest();
                request.setRepairRequestId(rs.getInt("rr_id"));
                request.setRequestCode(rs.getString("rr_code"));
                request.setUserId(rs.getInt("request_by"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("issue_description"));
                request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                // Schema v11: Repair_Requests doesn't have approval_reason, rejection_reason columns
                // request.setApprovalReason(rs.getString("approval_reason"));
                // request.setApprovedAt(rs.getTimestamp("approved_at"));
                // request.setRejectionReason(rs.getString("rejection_reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setUpdatedAt(rs.getTimestamp("updated_at"));
                request.setDisable(false);
                request.setDetails(getRepairRequestDetails(request.getRepairRequestId()));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public RepairRequest getRepairRequestById(int repairRequestId) {
        String sql = "SELECT rr.*, u.full_name AS user_name, u2.full_name AS approver_name "
                + "FROM Repair_Requests rr "
                + "JOIN Users u ON rr.request_by = u.user_id "
                + "LEFT JOIN Users u2 ON rr.approved_by = u2.user_id "
                + "WHERE rr.rr_id = ? AND rr.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RepairRequest request = new RepairRequest();
                request.setRepairRequestId(rs.getInt("rr_id"));
                request.setRequestCode(rs.getString("rr_code"));
                request.setUserId(rs.getInt("request_by"));
                request.setRequestDate(rs.getTimestamp("request_date"));
                request.setStatus(rs.getString("status"));
                request.setReason(rs.getString("issue_description"));
                request.setApprovedBy(rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null);
                // Schema v11: Repair_Requests doesn't have approval_reason, rejection_reason columns
                // request.setApprovalReason(rs.getString("approval_reason"));
                // request.setApprovedAt(rs.getTimestamp("approved_at"));
                // request.setRejectionReason(rs.getString("rejection_reason"));
                request.setCreatedAt(rs.getTimestamp("created_at"));
                request.setUpdatedAt(rs.getTimestamp("updated_at"));
                request.setDisable(false);
                request.setDetails(getRepairRequestDetails(rs.getInt("rr_id")));
                return request;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRepairRequestCountByUser(int userId, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT rr.rr_id) FROM `Repair_Requests` rr "
                + "LEFT JOIN `Repair_Request_Details` rrd ON rr.rr_id = rrd.rr_id "
                + "LEFT JOIN `Materials` m ON rrd.spare_material_id = m.material_id "
                + "WHERE rr.request_by = ? AND rr.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(rr.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND rr.request_date >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND rr.request_date <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PurchaseOrder> getPurchaseOrdersByUser(int userId, int page, int pageSize, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        List<PurchaseOrder> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT po.po_id, po.po_code, po.pr_id, po.supplier_id, s.supplier_name, "
                        + "po.currency_id, c.currency_code, po.order_date, po.expected_delivery_date, po.delivery_address, "
                        + "po.payment_term_id, pt.term_name AS payment_term_name, po.total_amount, po.tax_amount, "
                        + "po.discount_amount, po.note, po.grand_total, po.status, po.confirmed_by, u2.full_name AS confirmed_by_name, "
                        + "po.confirmed_at, po.created_by, u.full_name AS created_by_name, po.created_at, po.updated_at, "
                        + "po.deleted_at, pr.pr_code AS purchase_request_code "
                        + "FROM Purchase_Orders po "
                        + "JOIN Users u ON po.created_by = u.user_id "
                        + "LEFT JOIN Users u2 ON po.confirmed_by = u2.user_id "
                        + "LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id "
                        + "LEFT JOIN Currencies c ON po.currency_id = c.currency_id "
                        + "LEFT JOIN Payment_Terms pt ON po.payment_term_id = pt.term_id "
                        + "LEFT JOIN Purchase_Requests pr ON po.pr_id = pr.pr_id "
                        + "LEFT JOIN Purchase_Order_Details pod ON po.po_id = pod.po_id "
                        + "LEFT JOIN Materials m ON pod.material_id = m.material_id "
                        + "WHERE po.created_by = ? AND po.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(po.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        sql.append("ORDER BY po.created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseOrder order = new PurchaseOrder();
                order.setPoId(rs.getInt("po_id"));
                order.setPoCode(rs.getString("po_code"));
                order.setPurchaseRequestId(rs.getInt("pr_id"));
                order.setPurchaseRequestCode(rs.getString("purchase_request_code"));
                order.setSupplierId((Integer) rs.getObject("supplier_id"));
                order.setSupplierName(rs.getString("supplier_name"));
                order.setCurrencyId((Integer) rs.getObject("currency_id"));
                order.setCurrencyCode(rs.getString("currency_code"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                order.setDeliveryAddress(rs.getString("delivery_address"));
                order.setPaymentTermId((Integer) rs.getObject("payment_term_id"));
                order.setPaymentTermName(rs.getString("payment_term_name"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setTaxAmount(rs.getBigDecimal("tax_amount"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setNote(rs.getString("note"));
                order.setGrandTotal(rs.getBigDecimal("grand_total"));
                order.setStatus(rs.getString("status"));
                order.setConfirmedBy((Integer) rs.getObject("confirmed_by"));
                order.setConfirmedByName(rs.getString("confirmed_by_name"));
                order.setConfirmedAt(rs.getTimestamp("confirmed_at"));
                order.setCreatedBy(rs.getInt("created_by"));
                order.setCreatedByName(rs.getString("created_by_name"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setUpdatedAt(rs.getTimestamp("updated_at"));
                order.setDeletedAt(rs.getTimestamp("deleted_at"));
                List<PurchaseOrderDetail> detailList = getPurchaseOrderDetails(order.getPoId());
                order.setDetails(detailList);
                if (detailList != null) {
                    BigDecimal sum = BigDecimal.ZERO;
                    for (PurchaseOrderDetail d : detailList) {
                        if (d.getLineTotal() != null) {
                            sum = sum.add(d.getLineTotal());
                        }
                    }
                    order.setAggregatedDetailTotal(sum);
                }
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public PurchaseOrder getPurchaseOrderById(int poId) {
        String sql = "SELECT po.po_id, po.po_code, po.pr_id, po.supplier_id, s.supplier_name, po.currency_id, c.currency_code, "
                + "po.order_date, po.expected_delivery_date, po.delivery_address, po.payment_term_id, pt.term_name AS payment_term_name, "
                + "po.total_amount, po.tax_amount, po.discount_amount, po.note, po.grand_total, po.status, po.confirmed_by, u2.full_name AS confirmed_by_name, "
                + "po.confirmed_at, po.created_by, u.full_name AS created_by_name, po.created_at, po.updated_at, po.deleted_at, pr.pr_code AS purchase_request_code "
                + "FROM Purchase_Orders po "
                + "JOIN Users u ON po.created_by = u.user_id "
                + "LEFT JOIN Users u2 ON po.confirmed_by = u2.user_id "
                + "LEFT JOIN Suppliers s ON po.supplier_id = s.supplier_id "
                + "LEFT JOIN Currencies c ON po.currency_id = c.currency_id "
                + "LEFT JOIN Payment_Terms pt ON po.payment_term_id = pt.term_id "
                + "LEFT JOIN Purchase_Requests pr ON po.pr_id = pr.pr_id "
                + "WHERE po.po_id = ? AND po.deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PurchaseOrder order = new PurchaseOrder();
                order.setPoId(rs.getInt("po_id"));
                order.setPoCode(rs.getString("po_code"));
                order.setPurchaseRequestId(rs.getInt("pr_id"));
                order.setPurchaseRequestCode(rs.getString("purchase_request_code"));
                order.setSupplierId((Integer) rs.getObject("supplier_id"));
                order.setSupplierName(rs.getString("supplier_name"));
                order.setCurrencyId((Integer) rs.getObject("currency_id"));
                order.setCurrencyCode(rs.getString("currency_code"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                order.setDeliveryAddress(rs.getString("delivery_address"));
                order.setPaymentTermId((Integer) rs.getObject("payment_term_id"));
                order.setPaymentTermName(rs.getString("payment_term_name"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setTaxAmount(rs.getBigDecimal("tax_amount"));
                order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                order.setNote(rs.getString("note"));
                order.setGrandTotal(rs.getBigDecimal("grand_total"));
                order.setCreatedBy(rs.getInt("created_by"));
                order.setCreatedByName(rs.getString("created_by_name"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setUpdatedAt(rs.getTimestamp("updated_at"));
                order.setStatus(rs.getString("status"));
                order.setConfirmedBy((Integer) rs.getObject("confirmed_by"));
                order.setConfirmedByName(rs.getString("confirmed_by_name"));
                order.setConfirmedAt(rs.getTimestamp("confirmed_at"));
                order.setDeletedAt(rs.getTimestamp("deleted_at"));
                List<PurchaseOrderDetail> detailList = getPurchaseOrderDetails(rs.getInt("po_id"));
                order.setDetails(detailList);
                if (detailList != null) {
                    BigDecimal sum = BigDecimal.ZERO;
                    for (PurchaseOrderDetail d : detailList) {
                        if (d.getLineTotal() != null) {
                            sum = sum.add(d.getLineTotal());
                        }
                    }
                    order.setAggregatedDetailTotal(sum);
                }
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<PurchaseOrderDetail> getPurchaseOrderDetails(int poId) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        String sql = "SELECT pod.po_detail_id, pod.po_id, pod.material_id, pod.unit_id, pod.quantity_ordered, pod.unit_price, "
                + "pod.tax_rate, pod.discount_rate, pod.line_total, pod.received_quantity, pod.note, pod.created_at, "
                + "m.material_name, m.material_code, m.url, u.unit_name "
                + "FROM Purchase_Order_Details pod "
                + "LEFT JOIN Materials m ON pod.material_id = m.material_id "
                + "LEFT JOIN Units u ON pod.unit_id = u.unit_id "
                + "WHERE pod.po_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setPoDetailId(rs.getInt("po_detail_id"));
                detail.setPoId(rs.getInt("po_id"));
                detail.setMaterialId(rs.getInt("material_id"));
                detail.setMaterialName(rs.getString("material_name"));
                detail.setMaterialCode(rs.getString("material_code"));
                detail.setMaterialImageUrl(rs.getString("url"));
                detail.setUnitId((Integer) rs.getObject("unit_id"));
                detail.setUnitName(rs.getString("unit_name"));
                detail.setQuantityOrdered(rs.getBigDecimal("quantity_ordered"));
                detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                detail.setTaxRate(rs.getBigDecimal("tax_rate"));
                detail.setDiscountRate(rs.getBigDecimal("discount_rate"));
                detail.setLineTotal(rs.getBigDecimal("line_total"));
                detail.setReceivedQuantity(rs.getBigDecimal("received_quantity"));
                detail.setNote(rs.getString("note"));
                detail.setCreatedAt(rs.getTimestamp("created_at"));
                detail.setCategoryId(null);
                detail.setCategoryName(null);
                detail.setSupplierId(null);
                detail.setSupplierName(null);
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public int getPurchaseOrderCountByUser(int userId, String status, LocalDate startDate, LocalDate endDate, String materialName, String materialCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT po.po_id) FROM Purchase_Orders po "
                + "LEFT JOIN Purchase_Order_Details pod ON po.po_id = pod.po_id "
                + "LEFT JOIN Materials m ON pod.material_id = m.material_id "
                + "WHERE po.created_by = ? AND po.deleted_at IS NULL "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND LOWER(po.status) = ? ");
            params.add(status.trim().toLowerCase());
        }

        if (startDate != null) {
            sql.append("AND po.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.atStartOfDay()));
        }

        if (endDate != null) {
            sql.append("AND po.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.atTime(23, 59, 59)));
        }

        if (materialName != null && !materialName.trim().isEmpty()) {
            sql.append("AND m.material_name LIKE ? ");
            params.add("%" + materialName.trim() + "%");
        }

        if (materialCode != null && !materialCode.trim().isEmpty()) {
            sql.append("AND m.material_code LIKE ? ");
            params.add("%" + materialCode.trim() + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean cancelPurchaseOrder(int poId, int userId) {
        String sql = "UPDATE Purchase_Orders SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP "
                + "WHERE po_id = ? AND created_by = ? AND status IN ('draft','sent') AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelExportRequest(int exportRequestId, int userId) {
        String sql = "UPDATE Export_Requests SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP "
                + "WHERE er_id = ? AND request_by = ? AND status = 'draft' AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, exportRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelPurchaseRequest(int purchaseRequestId, int userId) {
        String sql = "UPDATE Purchase_Requests SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP "
                + "WHERE pr_id = ? AND request_by = ? AND status = 'submitted' AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, purchaseRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRepairRequest(int repairRequestId, int userId) {
        String sql = "UPDATE Repair_Requests SET status = 'cancelled', updated_at = CURRENT_TIMESTAMP "
                + "WHERE rr_id = ? AND request_by = ? AND status = 'submitted' AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, repairRequestId);
            ps.setInt(2, userId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
