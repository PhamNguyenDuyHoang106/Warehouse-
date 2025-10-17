package utils;

import dal.MaterialDAO;
import entity.Material;
import java.util.HashMap;
import java.util.Map;

public class MaterialValidator {

    public static Map<String, String> validateMaterial(Material material, MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();

        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        } else if (materialDAO.isMaterialCodeExists(material.getMaterialCode())) {
            errors.put("materialCode", "Material code already exists.");
        }

        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        if (material.getCategory() == null || material.getCategory().getCategory_id() <= 0) {
            errors.put("category", "Category must be selected.");
        }

        if (material.getUnit() == null || material.getUnit().getId() <= 0) {
            errors.put("unit", "Unit must be selected.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialUpdate(Material material, MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();

        if (material.getMaterialId() <= 0) {
            errors.put("materialId", "Invalid Material ID.");
        }

        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        }

        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        if (material.getMaterialStatus() == null || material.getMaterialStatus().trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        if (material.getCategory() == null || material.getCategory().getCategory_id() <= 0) {
            errors.put("category", "Category must be selected.");
        }

        if (material.getUnit() == null || material.getUnit().getId() <= 0) {
            errors.put("unit", "Unit must be selected.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialFormData(String materialCode, String materialName, String materialStatus, String categoryIdStr, String unitIdStr) {
        Map<String, String> errors = new HashMap<>();

        if (materialCode == null || materialCode.trim().isEmpty()) {
            errors.put("materialCode", "Material code cannot be empty.");
        }

        if (materialName == null || materialName.trim().isEmpty()) {
            errors.put("materialName", "Material name cannot be empty.");
        }

        if (materialStatus == null || materialStatus.trim().isEmpty()) {
            errors.put("materialStatus", "Material status cannot be empty.");
        }

        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            errors.put("categoryId", "Category must be selected.");
        } else {
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                if (categoryId <= 0) {
                    errors.put("categoryId", "Invalid category.");
                }
            } catch (NumberFormatException e) {
                errors.put("categoryId", "Invalid category ID format.");
            }
        }

        if (unitIdStr == null || unitIdStr.trim().isEmpty()) {
            errors.put("unitId", "Unit must be selected.");
        } else {
            try {
                int unitId = Integer.parseInt(unitIdStr);
                if (unitId <= 0) {
                    errors.put("unitId", "Invalid unit.");
                }
            } catch (NumberFormatException e) {
                errors.put("unitId", "Invalid unit ID format.");
            }
        }

        return errors;
    }
} 