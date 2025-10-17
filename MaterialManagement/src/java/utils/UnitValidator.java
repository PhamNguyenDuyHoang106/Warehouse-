package utils;

import dal.UnitDAO;
import entity.Unit;
import java.util.HashMap;
import java.util.Map;

public class UnitValidator {

    public static Map<String, String> validateUnit(Unit unit, UnitDAO unitDAO) {
        Map<String, String> errors = new HashMap<>();

        if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unit.getUnitName().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        if (unit.getUnitName() != null && !unit.getUnitName().trim().isEmpty()) {
            if (unitDAO.isUnitNameExists(unit.getUnitName())) {
                errors.put("unitName", "Unit name already exists.");
            }
        }

        if (unit.getSymbol() == null || unit.getSymbol().trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!unit.getSymbol().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        if (unit.getDescription() != null && !unit.getDescription().trim().isEmpty()) {
            if (!unit.getDescription().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitUpdate(Unit unit, UnitDAO unitDAO) {
        Map<String, String> errors = new HashMap<>();

        if (unit.getId() <= 0) {
            errors.put("unitId", "Invalid Unit ID.");
        }

        if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unit.getUnitName().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        if (unit.getUnitName() != null && !unit.getUnitName().trim().isEmpty()) {
            if (unitDAO.isUnitNameExists(unit.getUnitName())) {
                Unit existing = unitDAO.getAllUnits().stream()
                    .filter(u -> u.getUnitName().equalsIgnoreCase(unit.getUnitName()))
                    .findFirst().orElse(null);
                if (existing != null && existing.getId() != unit.getId()) {
                    errors.put("unitName", "Unit name already exists.");
                }
            }
        }

        if (unit.getSymbol() == null || unit.getSymbol().trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!unit.getSymbol().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        if (unit.getDescription() != null && !unit.getDescription().trim().isEmpty()) {
            if (!unit.getDescription().matches("^[a-zA-Z0-9\\sÀ-ỹà-ỹ.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitFormData(String unitName, String symbol, String description) {
        Map<String, String> errors = new HashMap<>();

        if (unitName == null || unitName.trim().isEmpty()) {
            errors.put("unitName", "Unit name cannot be empty.");
        } else if (!unitName.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
            errors.put("unitName", "Unit name cannot contain special characters.");
        }

        if (symbol == null || symbol.trim().isEmpty()) {
            errors.put("symbol", "Unit symbol cannot be empty.");
        } else if (!symbol.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
            errors.put("symbol", "Unit symbol cannot contain special characters.");
        }

        if (description != null && !description.trim().isEmpty()) {
            if (!description.matches("^[a-zA-Z0-9\\s\u00c0-\u1ef9\u00e0-\u1ef9.,-]+$")) {
                errors.put("description", "Description cannot contain special characters.");
            }
        }

        return errors;
    }

    public static Map<String, String> validateUnitId(String unitIdStr) {
        Map<String, String> errors = new HashMap<>();

        if (unitIdStr == null || unitIdStr.trim().isEmpty()) {
            errors.put("unitId", "Unit ID cannot be empty.");
        } else {
            try {
                int unitId = Integer.parseInt(unitIdStr);
                if (unitId <= 0) {
                    errors.put("unitId", "Invalid unit ID.");
                }
            } catch (NumberFormatException e) {
                errors.put("unitId", "Invalid unit ID format.");
            }
        }

        return errors;
    }
} 