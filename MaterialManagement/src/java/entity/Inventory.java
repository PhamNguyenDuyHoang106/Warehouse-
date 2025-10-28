package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Inventory {
    private int inventoryId;
    private int materialId;
    private Integer rackId; // Vị trí kệ trong kho
    private BigDecimal stock; // Thay đổi từ int sang BigDecimal
    private LocalDateTime lastUpdated;
    private Integer updatedBy;
    private String location;
    private String note;
    
    private String materialName;
    private String materialCode;
    private String categoryName;
    private String unitName;
    private String materialsUrl;
    private String rackName;
    private String rackCode;

    public Inventory() {}

    public Inventory(int inventoryId, int materialId, Integer rackId, BigDecimal stock, LocalDateTime lastUpdated, Integer updatedBy, String location, String note) {
        this.inventoryId = inventoryId;
        this.materialId = materialId;
        this.rackId = rackId;
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

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public Integer getRackId() {
        return rackId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
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

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public String getRackCode() {
        return rackCode;
    }

    public void setRackCode(String rackCode) {
        this.rackCode = rackCode;
    }
} 