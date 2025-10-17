package dal;

import entity.Supplier;
import entity.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends DBContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierId(rs.getInt("supplier_id"));
                s.setSupplierCode(rs.getString("supplier_code"));
                s.setSupplierName(rs.getString("supplier_name"));
                s.setContactInfo(rs.getString("contact_info"));
                s.setAddress(rs.getString("address"));
                s.setPhoneNumber(rs.getString("phone_number"));
                s.setEmail(rs.getString("email"));
                s.setDescription(rs.getString("description"));
                s.setTaxId(rs.getString("tax_id"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Supplier getSupplierByID(int id) {
        String sql = "SELECT * FROM Suppliers WHERE supplier_id = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierCode(rs.getString("supplier_code"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (supplier_code, supplier_name, contact_info, address, phone_number, email, description, tax_id, disable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierCode());
            ps.setString(2, supplier.getSupplierName());
            ps.setString(3, supplier.getContactInfo());
            ps.setString(4, supplier.getAddress());
            ps.setString(5, supplier.getPhoneNumber());
            ps.setString(6, supplier.getEmail());
            ps.setString(7, supplier.getDescription());
            ps.setString(8, supplier.getTaxId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET supplier_code=?, supplier_name=?, contact_info=?, address=?, phone_number=?, email=?, description=?, tax_id=? WHERE supplier_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getSupplierCode());
            ps.setString(2, supplier.getSupplierName());
            ps.setString(3, supplier.getContactInfo());
            ps.setString(4, supplier.getAddress());
            ps.setString(5, supplier.getPhoneNumber());
            ps.setString(6, supplier.getEmail());
            ps.setString(7, supplier.getDescription());
            ps.setString(8, supplier.getTaxId());
            ps.setInt(9, supplier.getSupplierId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSupplier(int id) {
        String sql = "UPDATE Suppliers SET disable=1 WHERE supplier_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE disable=0 AND (supplier_name LIKE ? OR contact_info LIKE ? OR phone_number LIKE ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierCode(rs.getString("supplier_code"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public Supplier getSupplierByPhone(String phone) {
        String sql = "SELECT * FROM Suppliers WHERE phone_number = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier s = new Supplier();
                    s.setSupplierId(rs.getInt("supplier_id"));
                    s.setSupplierCode(rs.getString("supplier_code"));
                    s.setSupplierName(rs.getString("supplier_name"));
                    s.setContactInfo(rs.getString("contact_info"));
                    s.setAddress(rs.getString("address"));
                    s.setPhoneNumber(rs.getString("phone_number"));
                    s.setEmail(rs.getString("email"));
                    s.setDescription(rs.getString("description"));
                    s.setTaxId(rs.getString("tax_id"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public String generateNextSupplierCode() {
        String sql = "SELECT supplier_code FROM Suppliers WHERE disable = 0 ORDER BY supplier_code DESC LIMIT 1";
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
            e.printStackTrace();
        }
        return "SUP001"; 
    }

    public boolean isSupplierCodeExists(String supplierCode) {
        String sql = "SELECT COUNT(*) FROM Suppliers WHERE supplier_code = ? AND disable = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplierCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm main để test
    public static void main(String[] args) {
        SupplierDAO dao = new SupplierDAO();
        String nextCode = dao.generateNextSupplierCode();
        System.out.println("Next supplier code: " + nextCode);
        
        List<Supplier> list = dao.getAllSuppliers();
        System.out.println("===== Danh sách nhà cung cấp =====");
        for (Supplier s : list) {
            System.out.println("ID: " + s.getSupplierId());
            System.out.println("Code: " + s.getSupplierCode());
            System.out.println("Tên: " + s.getSupplierName());
            System.out.println("Liên hệ: " + s.getContactInfo());
            System.out.println("Địa chỉ: " + s.getAddress());
            System.out.println("SĐT: " + s.getPhoneNumber());
            System.out.println("Email: " + s.getEmail());
            System.out.println("Mô tả: " + s.getDescription());
            System.out.println("Mã số thuế: " + s.getTaxId());
            System.out.println("-----------------------------------");
        }
    }
}
