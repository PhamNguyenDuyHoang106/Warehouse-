package utils;

import dal.MaterialDAO;
import dal.WarehouseRackDAO;
import entity.Material;
import entity.WarehouseRack;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ImportValidator {

    /**
     * Validate import data including materials, quantities, unit prices, and racks
     * 
     * @param materialIds Array of material IDs
     * @param quantities Array of quantities
     * @param unitPrices Array of unit prices
     * @param rackIds Array of rack IDs (can contain null/empty for optional racks)
     * @return Map of field errors (empty if no errors)
     */
    public static Map<String, String> validateImportDetails(
            String[] materialIds, 
            String[] quantities, 
            String[] unitPrices, 
            String[] rackIds) {
        
        Map<String, String> errors = new HashMap<>();
        
        // Check if arrays are provided
        if (materialIds == null || materialIds.length == 0) {
            errors.put("materials", "At least one material is required");
            return errors;
        }
        
        if (quantities == null || quantities.length != materialIds.length) {
            errors.put("quantities", "Quantities array size mismatch");
            return errors;
        }
        
        // Unit prices can be null/empty - will default to 0
        // Just ensure array exists if provided
        if (unitPrices != null && unitPrices.length != materialIds.length) {
            errors.put("unitPrices", "Unit prices array size mismatch");
            return errors;
        }
        
        // Validate each item
        MaterialDAO materialDAO = null;
        WarehouseRackDAO rackDAO = null;
        
        try {
            materialDAO = new MaterialDAO();
            rackDAO = new WarehouseRackDAO();
            
            for (int i = 0; i < materialIds.length; i++) {
                String prefix = "item_" + i + "_";
                
                // Validate Material ID
                try {
                    int materialId = Integer.parseInt(materialIds[i]);
                    if (materialId <= 0) {
                        errors.put(prefix + "materialId", "Invalid material ID");
                    } else {
                        Material material = materialDAO.getProductById(materialId);
                        if (material == null) {
                            errors.put(prefix + "materialId", "Material not found");
                        }
                    }
                } catch (NumberFormatException e) {
                    errors.put(prefix + "materialId", "Invalid material ID format");
                }
                
                // Validate Quantity
                try {
                    BigDecimal quantity = new BigDecimal(quantities[i]);
                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put(prefix + "quantity", "Quantity must be greater than 0");
                    }
                    if (quantity.scale() > 2) {
                        errors.put(prefix + "quantity", "Maximum 2 decimal places allowed");
                    }
                } catch (NumberFormatException e) {
                    errors.put(prefix + "quantity", "Invalid quantity format");
                }
                
                // Validate Unit Price (optional - defaults to 0)
                if (unitPrices != null && i < unitPrices.length && 
                    unitPrices[i] != null && !unitPrices[i].trim().isEmpty()) {
                    try {
                        BigDecimal unitPrice = new BigDecimal(unitPrices[i]);
                        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                            errors.put(prefix + "unitPrice", "Unit price cannot be negative");
                        }
                        if (unitPrice.scale() > 2) {
                            errors.put(prefix + "unitPrice", "Maximum 2 decimal places allowed");
                        }
                    } catch (NumberFormatException e) {
                        errors.put(prefix + "unitPrice", "Invalid unit price format");
                    }
                }
                
                // Validate Rack ID (optional but must be valid if provided)
                if (rackIds != null && i < rackIds.length && 
                    rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                    try {
                        int rackId = Integer.parseInt(rackIds[i]);
                        if (rackId > 0) {
                            WarehouseRack rack = rackDAO.getRackById(rackId);
                            if (rack == null) {
                                errors.put(prefix + "rackId", "Rack not found");
                            } else {
                                // Check if rack is available/active (handle both string and enum)
                                Object statusObj = rack.getStatus();
                                String statusStr = statusObj != null ? statusObj.toString() : "";
                                if (!statusStr.equalsIgnoreCase("active") && 
                                    !statusStr.equalsIgnoreCase("available") &&
                                    !statusStr.equals("AVAILABLE")) {
                                    errors.put(prefix + "rackId", "Rack is not available");
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        errors.put(prefix + "rackId", "Invalid rack ID format");
                    }
                }
            }
        } finally {
            if (materialDAO != null) {
                materialDAO.close();
            }
            if (rackDAO != null) {
                rackDAO.close();
            }
        }
        
        return errors;
    }

    /**
     * Validate import code format
     */
    public static String validateImportCode(String importCode) {
        if (importCode == null || importCode.trim().isEmpty()) {
            return "Import code is required";
        }
        if (!importCode.matches("^IMP\\d{4}$")) {
            return "Import code must follow format: IMP0001";
        }
        return null;
    }

    /**
     * Validate supplier ID (optional but must be valid if provided)
     */
    public static String validateSupplierId(String supplierIdStr) {
        if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
            return null; // Supplier is optional
        }
        
        try {
            int supplierId = Integer.parseInt(supplierIdStr);
            if (supplierId <= 0) {
                return "Invalid supplier ID";
            }
        } catch (NumberFormatException e) {
            return "Invalid supplier ID format";
        }
        return null;
    }

    /**
     * Check for duplicate materials in the import
     */
    public static Map<String, String> checkDuplicateMaterials(String[] materialIds) {
        Map<String, String> errors = new HashMap<>();
        Map<String, Integer> materialCount = new HashMap<>();
        
        for (int i = 0; i < materialIds.length; i++) {
            String materialId = materialIds[i];
            if (materialCount.containsKey(materialId)) {
                errors.put("item_" + i + "_materialId", 
                          "Duplicate material detected (same material at position " + 
                          (materialCount.get(materialId) + 1) + ")");
            } else {
                materialCount.put(materialId, i);
            }
        }
        
        return errors;
    }
}
