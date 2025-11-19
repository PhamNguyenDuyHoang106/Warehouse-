package utils;

import dal.MaterialDAO;
import entity.Material;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MaterialValidator {

    private static final String[] VALID_STATUSES = {"active", "inactive", "discontinued"};
    private static final int MAX_URL_LENGTH = 500;

    private static boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        for (String option : VALID_STATUSES) {
            if (option.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPositiveOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static Map<String, String> validateMaterial(Material material, MaterialDAO materialDAO) {
        Map<String, String> errors = new HashMap<>();

        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Mã vật tư không được để trống.");
        } else if (materialDAO.isMaterialCodeExists(material.getMaterialCode())) {
            errors.put("materialCode", "Mã vật tư đã tồn tại.");
        }

        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Tên vật tư không được để trống.");
        }

        if (!isValidStatus(material.getMaterialStatus())) {
            errors.put("materialStatus", "Trạng thái phải là 'active', 'inactive' hoặc 'discontinued'.");
        }

        if (material.getCategory() == null || material.getCategory().getCategory_id() <= 0) {
            errors.put("category", "Bạn phải chọn nhóm vật tư.");
        }

        if (material.getDefaultUnit() == null || material.getDefaultUnit().getId() <= 0) {
            errors.put("unit", "Bạn phải chọn đơn vị chuẩn.");
        }

        if (!isPositiveOrZero(material.getMinStock())) {
            errors.put("minStock", "Tồn kho tối thiểu phải >= 0.");
        }

        if (!isPositiveOrZero(material.getMaxStock())) {
            errors.put("maxStock", "Tồn kho tối đa phải >= 0.");
        }

        if (material.getMinStock() != null
                && material.getMaxStock() != null
                && material.getMaxStock().compareTo(material.getMinStock()) < 0) {
            errors.put("maxStock", "Tồn kho tối đa phải lớn hơn hoặc bằng tồn kho tối thiểu.");
        }

        if (!isPositiveOrZero(material.getWeightPerUnit())) {
            errors.put("weightPerUnit", "Khối lượng/đơn vị phải >= 0.");
        }

        if (!isPositiveOrZero(material.getVolumePerUnit())) {
            errors.put("volumePerUnit", "Thể tích/đơn vị phải >= 0.");
        }

        if (material.getUrl() != null && material.getUrl().length() > MAX_URL_LENGTH) {
            errors.put("materialsUrl", "Độ dài URL không được vượt quá " + MAX_URL_LENGTH + " ký tự.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialUpdate(Material material) {
        Map<String, String> errors = new HashMap<>();

        if (material.getMaterialId() <= 0) {
            errors.put("materialId", "ID vật tư không hợp lệ.");
        }
        if (material.getMaterialCode() == null || material.getMaterialCode().trim().isEmpty()) {
            errors.put("materialCode", "Mã vật tư không được để trống.");
        }
        if (material.getMaterialName() == null || material.getMaterialName().trim().isEmpty()) {
            errors.put("materialName", "Tên vật tư không được để trống.");
        }
        if (!isValidStatus(material.getMaterialStatus())) {
            errors.put("materialStatus", "Trạng thái phải là 'active', 'inactive' hoặc 'discontinued'.");
        }
        if (material.getCategory() == null || material.getCategory().getCategory_id() <= 0) {
            errors.put("category", "Bạn phải chọn nhóm vật tư.");
        }
        if (material.getDefaultUnit() == null || material.getDefaultUnit().getId() <= 0) {
            errors.put("unit", "Bạn phải chọn đơn vị chuẩn.");
        }

        if (!isPositiveOrZero(material.getMinStock())) {
            errors.put("minStock", "Tồn kho tối thiểu phải >= 0.");
        }

        if (!isPositiveOrZero(material.getMaxStock())) {
            errors.put("maxStock", "Tồn kho tối đa phải >= 0.");
        }

        if (material.getMinStock() != null
                && material.getMaxStock() != null
                && material.getMaxStock().compareTo(material.getMinStock()) < 0) {
            errors.put("maxStock", "Tồn kho tối đa phải lớn hơn hoặc bằng tồn kho tối thiểu.");
        }

        if (material.getUrl() != null && material.getUrl().length() > MAX_URL_LENGTH) {
            errors.put("materialsUrl", "Độ dài URL không được vượt quá " + MAX_URL_LENGTH + " ký tự.");
        }

        return errors;
    }

    public static Map<String, String> validateMaterialFormData(
            String materialCode,
            String materialName,
            String materialStatus,
            String categoryIdStr,
            String unitIdStr,
            String minStockStr,
            String maxStockStr) {

        Map<String, String> errors = new HashMap<>();

        if (materialCode == null || materialCode.trim().isEmpty()) {
            errors.put("materialCode", "Mã vật tư không được để trống.");
        }

        if (materialName == null || materialName.trim().isEmpty()) {
            errors.put("materialName", "Tên vật tư không được để trống.");
        }

        if (!isValidStatus(materialStatus)) {
            errors.put("materialStatus", "Trạng thái phải là 'active', 'inactive' hoặc 'discontinued'.");
        }

        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            errors.put("categoryId", "Bạn phải chọn nhóm vật tư.");
        } else {
            try {
                int categoryId = Integer.parseInt(categoryIdStr);
                if (categoryId <= 0) {
                    errors.put("categoryId", "Nhóm vật tư không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                errors.put("categoryId", "Định dạng nhóm vật tư không hợp lệ.");
            }
        }

        if (unitIdStr == null || unitIdStr.trim().isEmpty()) {
            errors.put("unitId", "Bạn phải chọn đơn vị chuẩn.");
        } else {
            try {
                int unitId = Integer.parseInt(unitIdStr);
                if (unitId <= 0) {
                    errors.put("unitId", "Đơn vị không hợp lệ.");
                }
            } catch (NumberFormatException e) {
                errors.put("unitId", "Định dạng đơn vị không hợp lệ.");
            }
        }

        if (minStockStr != null && !minStockStr.trim().isEmpty()) {
            try {
                BigDecimal minStock = new BigDecimal(minStockStr.trim());
                if (minStock.compareTo(BigDecimal.ZERO) < 0) {
                    errors.put("minStock", "Tồn kho tối thiểu phải >= 0.");
                }
            } catch (NumberFormatException e) {
                errors.put("minStock", "Tồn kho tối thiểu không hợp lệ.");
            }
        }

        if (maxStockStr != null && !maxStockStr.trim().isEmpty()) {
            try {
                BigDecimal maxStock = new BigDecimal(maxStockStr.trim());
                if (maxStock.compareTo(BigDecimal.ZERO) < 0) {
                    errors.put("maxStock", "Tồn kho tối đa phải >= 0.");
                }
            } catch (NumberFormatException e) {
                errors.put("maxStock", "Tồn kho tối đa không hợp lệ.");
            }
        }

        return errors;
    }
}
