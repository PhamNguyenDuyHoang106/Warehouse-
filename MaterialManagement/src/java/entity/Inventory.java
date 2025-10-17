package entity;

import java.time.LocalDateTime;

public class Inventory {
    private int inventoryId;
    private int materialId;
    private int stock;
    private LocalDateTime lastUpdated;
    private Integer updatedBy;
    private String location;
    private String note;
    
    private String materialName;
    private String materialCode;
    private String categoryName;
    private String unitName;
    private String materialsUrl;

    public Inventory() {}

    public Inventory(int inventoryId, int materialId, int stock, LocalDateTime lastUpdated, Integer updatedBy, String location, String note) {
        this.inventoryId = inventoryId;
        this.materialId = materialId;
        this.stock = stock;
        this.lastUpdated = lastUpdated;
        this.updatedBy = updatedBy;
        this.location = location;
        this.note = note;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getMaterialsUrl() {
        return materialsUrl;
    }
    public void setMaterialsUrl(String materialsUrl) {
        this.materialsUrl = materialsUrl;
    }
} 