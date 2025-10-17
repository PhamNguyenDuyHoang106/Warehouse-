package utils;

import dal.MaterialDAO;
import entity.Material;
import entity.ExportRequest;
import entity.ExportRequestDetail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExportRequestValidator {

    public static Map<String, String> validateExportRequest(ExportRequest exportRequest) {
        Map<String, String> errors = new HashMap<>();

        if (exportRequest.getReason() == null || exportRequest.getReason().trim().isEmpty()) {
            errors.put("reason", "Reason for export request cannot be empty.");
        }

        if (exportRequest.getDeliveryDate() == null) {
            errors.put("deliveryDate", "Delivery date cannot be empty.");
        } else {
            // Check if delivery date is not in the past
            LocalDate deliveryDate = exportRequest.getDeliveryDate().toLocalDate();
            LocalDate today = LocalDate.now();
            if (deliveryDate.isBefore(today)) {
                errors.put("deliveryDate", "Delivery date cannot be in the past.");
            }
        }

        if (exportRequest.getUserId() <= 0) {
            errors.put("userId", "Invalid user ID.");
        }

        if (exportRequest.getDetails() == null || exportRequest.getDetails().isEmpty()) {
            errors.put("details", "You need to add at least one material.");
        }

        return errors;
    }

    public static Map<String, String> validateExportRequestFormData(String reason, String deliveryDate) {
        Map<String, String> errors = new HashMap<>();
        
        if (reason == null || reason.trim().isEmpty()) {
            errors.put("reason", "Reason for export request cannot be empty.");
        }
        
        if (deliveryDate == null || deliveryDate.trim().isEmpty()) {
            errors.put("deliveryDate", "Delivery date cannot be empty.");
        } else {
            try {
                LocalDate date = LocalDate.parse(deliveryDate);
                LocalDate today = LocalDate.now();
                if (date.isBefore(today)) {
                    errors.put("deliveryDate", "Delivery date cannot be in the past.");
                }
            } catch (Exception e) {
                errors.put("deliveryDate", "Invalid delivery date format.");
            }
        }
        
        return errors;
    }

    public static Map<String, String> validateExportRequestDetails(String[] materialNames, String[] quantities) {
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
                        } else {
                            // Check if quantity exceeds available stock
                            int availableStock = materialDAO.getAvailableStock(material.getMaterialId());
                            if (quantity > availableStock) {
                                errors.put("quantity_" + i, "Quantity exceeds available stock for material " + trimmedMaterialName + ". Available: " + availableStock);
                            }
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

    public static Map<String, String> validateExportRequestDetail(ExportRequestDetail detail) {
        Map<String, String> errors = new HashMap<>();

        if (detail.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid material ID.");
        }

        if (detail.getQuantity() <= 0) {
            errors.put("quantity", "Quantity must be greater than 0.");
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