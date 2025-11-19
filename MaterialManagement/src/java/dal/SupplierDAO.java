package dal;

import entity.Supplier;
import entity.DBContext;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplierDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(SupplierDAO.class.getName());

    private static final String BASE_SELECT =
            "SELECT s.supplier_id, s.supplier_code, s.supplier_name, s.contact_person, s.phone, s.email, "
                    + "s.tax_code, s.address, s.payment_term_id, pt.term_name, s.credit_limit, s.status, "
                    + "s.created_at, s.deleted_at "
                    + "FROM Suppliers s "
                    + "LEFT JOIN Payment_Terms pt ON s.payment_term_id = pt.term_id ";

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setSupplierCode(rs.getString("supplier_code"));
        supplier.setSupplierName(rs.getString("supplier_name"));
        supplier.setContactPerson(rs.getString("contact_person"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setEmail(rs.getString("email"));
        supplier.setTaxCode(rs.getString("tax_code"));
        supplier.setAddress(rs.getString("address"));
        supplier.setPaymentTermId((Integer) rs.getObject("payment_term_id"));
        supplier.setPaymentTermName(rs.getString("term_name"));
        supplier.setCreditLimit(rs.getBigDecimal("credit_limit"));
        supplier.setStatus(rs.getString("status"));
        supplier.setCreatedAt(rs.getTimestamp("created_at"));
        supplier.setDeletedAt(rs.getTimestamp("deleted_at"));
        return supplier;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE s.deleted_at IS NULL ORDER BY s.supplier_name";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapSupplier(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all suppliers", e);
        }
        return list;
    }

    public Supplier getSupplierByID(int id) {
        String sql = BASE_SELECT + "WHERE s.deleted_at IS NULL AND s.supplier_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting supplier by ID: " + id, e);
        }
        return null;
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (supplier_code, supplier_name, contact_person, phone, email, tax_code, address, payment_term_id, credit_limit, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierCode());
            ps.setString(2, supplier.getSupplierName());
            ps.setString(3, supplier.getContactPerson());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getEmail());
            ps.setString(6, supplier.getTaxCode());
            ps.setString(7, supplier.getAddress());
            if (supplier.getPaymentTermId() != null) {
                ps.setInt(8, supplier.getPaymentTermId());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(9, supplier.getCreditLimit() != null ? supplier.getCreditLimit() : BigDecimal.ZERO);
            ps.setString(10, normalizeStatus(supplier.getStatus()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding supplier", e);
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET supplier_code = ?, supplier_name = ?, contact_person = ?, phone = ?, email = ?, tax_code = ?, "
                + "address = ?, payment_term_id = ?, credit_limit = ?, status = ? "
                + "WHERE supplier_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierCode());
            ps.setString(2, supplier.getSupplierName());
            ps.setString(3, supplier.getContactPerson());
            ps.setString(4, supplier.getPhone());
            ps.setString(5, supplier.getEmail());
            ps.setString(6, supplier.getTaxCode());
            ps.setString(7, supplier.getAddress());
            if (supplier.getPaymentTermId() != null) {
                ps.setInt(8, supplier.getPaymentTermId());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(9, supplier.getCreditLimit() != null ? supplier.getCreditLimit() : BigDecimal.ZERO);
            ps.setString(10, normalizeStatus(supplier.getStatus()));
            ps.setInt(11, supplier.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating supplier: " + supplier.getSupplierId(), e);
            return false;
        }
    }

    public boolean deleteSupplier(int id, Integer userId) {
        String sql = "UPDATE Suppliers SET deleted_at = ? WHERE supplier_id = ? AND deleted_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting supplier: " + id, e);
            return false;
        }
    }

    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE s.deleted_at IS NULL AND (s.supplier_name LIKE ? OR s.contact_person LIKE ? OR s.phone LIKE ? OR s.email LIKE ?) ORDER BY s.supplier_name";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSupplier(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching suppliers with keyword " + keyword, e);
        }
        return list;
    }

    public Supplier getSupplierByPhone(String phone) {
        String sql = BASE_SELECT + "WHERE s.deleted_at IS NULL AND s.phone = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting supplier by phone: " + phone, e);
        }
        return null;
    }

    public String generateNextSupplierCode() {
        String sql = "SELECT supplier_code FROM Suppliers WHERE deleted_at IS NULL ORDER BY supplier_code DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastCode = rs.getString("supplier_code");
                if (lastCode != null && lastCode.matches("SUP\\d+")) {
                    String numberStr = lastCode.substring(3);
                    int nextNumber = Integer.parseInt(numberStr) + 1;
                    return String.format("SUP%03d", nextNumber);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating supplier code", e);
        }
        return "SUP001";
    }

    public boolean isSupplierCodeExists(String supplierCode) {
        return isSupplierCodeExists(supplierCode, null);
    }

    public boolean isSupplierCodeExists(String supplierCode, Integer excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Suppliers WHERE supplier_code = ? AND deleted_at IS NULL");
        if (excludeId != null) {
            sql.append(" AND supplier_id <> ?");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, supplierCode);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking supplier code existence", e);
        }
        return false;
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "active";
        }
        String normalized = status.trim().toLowerCase();
        return normalized.isEmpty() ? "active" : normalized;
    }
}
