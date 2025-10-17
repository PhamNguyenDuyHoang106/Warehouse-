package utils;

import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import dal.MaterialDAO;
import entity.Material;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseRequestValidator {

    public static Map<String, String> validatePurchaseRequest(PurchaseRequest purchaseRequest) {
        Map<String, String> errors = new HashMap<>();

        if (purchaseRequest.getReason() == null || purchaseRequest.getReason().trim().isEmpty()) {
            errors.put("reason", "Reason for request cannot be empty.");
        }

        if (purchaseRequest.getUserId() <= 0) {
            errors.put("userId", "Invalid user ID.");
        }

        if (purchaseRequest.getDetails() == null || purchaseRequest.getDetails().isEmpty()) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestFormData(String reason) {
        Map<String, String> errors = new HashMap<>();
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason for request cannot be empty.");
        }
        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetails(String[] materialNames, String[] quantities) {
        Map<String, String> errors = new HashMap<>();

        if (materialNames == null || quantities == null) {
            errors.put("details", "Detail information is invalid.");
            return errors;
        }

        boolean hasValidDetail = false;
        MaterialDAO materialDAO = new MaterialDAO();

        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            String quantityStr = quantities[i];

            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            }

            // Check if material exists in database
            String trimmedMaterialName = materialName.trim();
            Material material = materialDAO.getMaterialByName(trimmedMaterialName);
            if (material == null) {
                errors.put("material_" + i, "Material '" + trimmedMaterialName + "' does not exist in inventory.");
                continue;
            }

            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                errors.put("quantity_" + i, "Quantity cannot be empty for material " + trimmedMaterialName + ".");
            } else {
                String trimmedQuantity = quantityStr.trim();
                if (trimmedQuantity.contains(".") || trimmedQuantity.contains(",")) {
                    errors.put("quantity_" + i, "Quantity must be a whole number (no decimals) for material " + trimmedMaterialName + ".");
                } else {
                    try {
                        int quantity = Integer.parseInt(trimmedQuantity);
                        if (quantity <= 0) {
                            errors.put("quantity_" + i, "Quantity must be greater than 0 for material " + trimmedMaterialName + ".");
                        }
                    } catch (NumberFormatException e) {
                        errors.put("quantity_" + i, "Invalid quantity format for material " + trimmedMaterialName + ".");
                    }
                }
            }

            hasValidDetail = true;
        }

        if (!hasValidDetail) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetail(PurchaseRequestDetail detail) {
        Map<String, String> errors = new HashMap<>();

        if (detail.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid material ID.");
        }

        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
        }

        return errors;
    }

    public static Map<String, String> validatePurchaseRequestDetailFormData(String materialIdStr, String quantityStr) {
        Map<String, String> errors = new HashMap<>();

        if (materialIdStr == null || materialIdStr.trim().isEmpty()) {
             errors.put("materialId", "Material must be selected.");
        } else {
            try {
                int materialId = Integer.parseInt(materialIdStr);
                if (materialId <= 0) {
                     errors.put("materialId", "Invalid material.");
                }
            } catch (NumberFormatException e) {
                errors.put("materialId", "Invalid material ID format.");
            }
        }

        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            errors.put("quantity", "Quantity cannot be empty.");
        } else {
            String trimmedQuantity = quantityStr.trim();
            if (trimmedQuantity.contains(".") || trimmedQuantity.contains(",")) {
                errors.put("quantity", "Quantity must be a whole number (no decimals).");
            } else {
                try {
                    int quantity = Integer.parseInt(trimmedQuantity);
                    if (quantity <= 0) {
                        errors.put("quantity", "Quantity must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("quantity", "Invalid quantity format.");
                }
            }
        }

        return errors;
    }

    // New method to validate material names against database
    public static Map<String, String> validateMaterialNames(String[] materialNames) {
        Map<String, String> errors = new HashMap<>();
        
        if (materialNames == null) {
            return errors;
        }

        MaterialDAO materialDAO = new MaterialDAO();
        
        for (int i = 0; i < materialNames.length; i++) {
            String materialName = materialNames[i];
            
            if (materialName == null || materialName.trim().isEmpty()) {
                continue;
            }

            String trimmedMaterialName = materialName.trim();
            Material material = materialDAO.getMaterialByName(trimmedMaterialName);
            
            if (material == null) {
                errors.put("material_" + i, "Material '" + trimmedMaterialName + "' does not exist in inventory.");
            }
        }

        return errors;
    }
}
