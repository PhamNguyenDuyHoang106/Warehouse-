package utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Validator for Purchase Order form data
 */
public class PurchaseOrderValidator {
    
    /**
     * Validate purchase order form data
     */
    public static Map<String, String> validatePurchaseOrderFormData(String poCode, String purchaseRequestId, String note) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate PO Code
        if (poCode == null || poCode.trim().isEmpty()) {
            errors.put("poCode", "PO Code is required");
        }
        
        // Validate Purchase Request ID
        if (purchaseRequestId == null || purchaseRequestId.trim().isEmpty()) {
            errors.put("purchaseRequestId", "Purchase Request is required");
        } else {
            try {
                int id = Integer.parseInt(purchaseRequestId);
                if (id <= 0) {
                    errors.put("purchaseRequestId", "Invalid Purchase Request ID");
                }
            } catch (NumberFormatException e) {
                errors.put("purchaseRequestId", "Invalid Purchase Request ID format");
            }
        }
        
        // Note is optional, no validation needed
        
        return errors;
    }
    
    /**
     * Validate purchase order details
     */
    public static Map<String, String> validatePurchaseOrderDetails(String[] materialIds, String[] quantities, 
                                                                  String[] unitPrices, String[] suppliers) {
        Map<String, String> errors = new HashMap<>();
        
        // Check if arrays are null or empty
        if (materialIds == null || materialIds.length == 0) {
            errors.put("materials", "At least one material is required");
            return errors;
        }
        
        // Validate each detail
        for (int i = 0; i < materialIds.length; i++) {
            String materialId = materialIds[i];
            String quantity = quantities != null && i < quantities.length ? quantities[i] : null;
            String unitPrice = unitPrices != null && i < unitPrices.length ? unitPrices[i] : null;
            String supplier = suppliers != null && i < suppliers.length ? suppliers[i] : null;
            
            // Validate Material ID
            if (materialId == null || materialId.trim().isEmpty()) {
                errors.put("materialId_" + i, "Material is required");
            } else {
                try {
                    int id = Integer.parseInt(materialId);
                    if (id <= 0) {
                        errors.put("materialId_" + i, "Invalid Material ID");
                    }
                } catch (NumberFormatException e) {
                    errors.put("materialId_" + i, "Invalid Material ID format");
                }
            }
            
            // Validate Quantity
            if (quantity == null || quantity.trim().isEmpty()) {
                errors.put("quantity_" + i, "Quantity is required");
            } else {
                try {
                    int qty = Integer.parseInt(quantity);
                    if (qty <= 0) {
                        errors.put("quantity_" + i, "Quantity must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    errors.put("quantity_" + i, "Invalid quantity format");
                }
            }
            
            // Validate Unit Price
            if (unitPrice == null || unitPrice.trim().isEmpty()) {
                errors.put("unitPrice_" + i, "Unit price is required");
            } else {
                try {
                    BigDecimal price = new BigDecimal(unitPrice);
                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put("unitPrice_" + i, "Unit price must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    errors.put("unitPrice_" + i, "Invalid unit price format");
                }
            }
            
            // Validate Supplier
            if (supplier == null || supplier.trim().isEmpty()) {
                errors.put("supplier_" + i, "Supplier is required");
            } else {
                try {
                    int supplierId = Integer.parseInt(supplier);
                    if (supplierId <= 0) {
                        errors.put("supplier_" + i, "Invalid Supplier ID");
                    }
                } catch (NumberFormatException e) {
                    errors.put("supplier_" + i, "Invalid Supplier ID format");
                }
            }
        }
        
        return errors;
    }
} 