package utils;

import dal.SupplierDAO;
import entity.Supplier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class SupplierValidator {

    private static final String PHONE_REGEX = "^\\+?[0-9\\s-]{7,20}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String TAX_REGEX = "[A-Za-z0-9]{5,20}";

    public static Map<String, String> validateSupplier(Supplier supplier, SupplierDAO supplierDAO) {
        Map<String, String> errors = validateCommonFields(supplier);
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        } else if (supplierDAO.isSupplierCodeExists(supplier.getSupplierCode())) {
            errors.put("supplierCode", "Supplier code already exists, please enter a different one.");
        }
        return errors;
    }

    public static Map<String, String> validateSupplierUpdate(Supplier supplier, SupplierDAO supplierDAO) {
        Map<String, String> errors = new HashMap<>();
        if (supplier.getSupplierId() <= 0) {
            errors.put("supplierId", "Invalid Supplier ID.");
        }
        errors.putAll(validateCommonFields(supplier));
        if (supplier.getSupplierCode() == null || supplier.getSupplierCode().trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        } else if (supplierDAO.isSupplierCodeExists(supplier.getSupplierCode(), supplier.getSupplierId())) {
            errors.put("supplierCode", "Supplier code already exists, please enter a different one.");
        }
        return errors;
    }

    public static Map<String, String> validateSupplierFormData(String supplierCode, String supplierName, String contactPerson,
                                                               String address, String phone, String email, String taxCode,
                                                               String creditLimitStr, String status) {
        Supplier tmp = new Supplier();
        tmp.setSupplierCode(supplierCode);
        tmp.setSupplierName(supplierName);
        tmp.setContactPerson(contactPerson);
        tmp.setAddress(address);
        tmp.setPhone(phone);
        tmp.setEmail(email);
        tmp.setTaxCode(taxCode);
        tmp.setStatus(status);
        Map<String, String> errors = new HashMap<>(validateCommonFields(tmp));

        if (supplierCode == null || supplierCode.trim().isEmpty()) {
            errors.put("supplierCode", "Supplier code cannot be empty.");
        }

        if (creditLimitStr != null && !creditLimitStr.trim().isEmpty()) {
            try {
                tmp.setCreditLimit(new BigDecimal(creditLimitStr.trim()));
                if (tmp.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
                    errors.put("creditLimit", "Credit limit cannot be negative.");
                }
            } catch (NumberFormatException e) {
                errors.put("creditLimit", "Credit limit must be a valid number.");
            }
        }

        if (status == null || status.trim().isEmpty()) {
            errors.put("status", "Status is required.");
        }

        return errors;
    }

    private static Map<String, String> validateCommonFields(Supplier supplier) {
        Map<String, String> errors = new HashMap<>();

        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            errors.put("supplierName", "Supplier name cannot be empty.");
        } else if (supplier.getSupplierName().trim().length() > 255) {
            errors.put("supplierName", "Supplier name cannot exceed 255 characters.");
        }

        if (supplier.getContactPerson() == null || supplier.getContactPerson().trim().isEmpty()) {
            errors.put("contactPerson", "Contact person is required.");
        } else if (supplier.getContactPerson().trim().length() > 150) {
            errors.put("contactPerson", "Contact person cannot exceed 150 characters.");
        }

        if (supplier.getAddress() != null && supplier.getAddress().trim().length() > 500) {
            errors.put("address", "Address cannot exceed 500 characters.");
        }

        if (supplier.getPhone() == null || supplier.getPhone().trim().isEmpty()) {
            errors.put("phone", "Phone is required.");
        } else if (!supplier.getPhone().trim().matches(PHONE_REGEX)) {
            errors.put("phone", "Invalid phone format.");
        }

        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty() && !supplier.getEmail().trim().matches(EMAIL_REGEX)) {
            errors.put("email", "Invalid email format.");
        }

        if (supplier.getTaxCode() != null && !supplier.getTaxCode().trim().isEmpty() && !supplier.getTaxCode().trim().matches(TAX_REGEX)) {
            errors.put("taxCode", "Tax code must be 5-20 alphanumeric characters.");
        }

        BigDecimal creditLimit = supplier.getCreditLimit();
        if (creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) < 0) {
            errors.put("creditLimit", "Credit limit cannot be negative.");
        }

        if (supplier.getStatus() != null) {
            String status = supplier.getStatus().trim().toLowerCase();
            if (!status.equals("active") && !status.equals("inactive")) {
                errors.put("status", "Status must be 'active' or 'inactive'.");
            }
        }

        return errors;
    }
} 