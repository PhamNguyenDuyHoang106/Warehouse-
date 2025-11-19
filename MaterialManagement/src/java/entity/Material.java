/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Domain object đại diện bảng Materials (schema v11).
 * Giữ lại một số getter/setter cũ để hạn chế tác động lên các servlet/JSP hiện có.
 */
public class Material {

    private static final String DEFAULT_MEDIA_URL = "images/material/default.jpg";

    private int materialId;
    private String materialCode;
    private String materialName;
    private String barcode;
    private String url; // Link ảnh/datasheet/video (material.url)
    private String status;   // ENUM('active','inactive','discontinued')

    private Category category;
    private Unit defaultUnit;    // default_unit_id
    private Unit purchaseUnit;   // purchase_unit_id
    private Unit salesUnit;      // sales_unit_id
    private WarehouseRack rack;  // Vị trí gợi ý (không bắt buộc)

    private BigDecimal minStock;
    private BigDecimal maxStock;
    private BigDecimal weightPerUnit; // kg
    private BigDecimal volumePerUnit; // m3
    private Integer shelfLifeDays;
    private boolean serialized;
    private boolean batchControlled;

    private BigDecimal stockOnHand;     // Tổng stock (Inventory.stock)
    private BigDecimal reservedStock;   // Tổng reserved_stock
    private BigDecimal availableStock;  // Tổng available_stock
    private BigDecimal averageCost;      // Giá trung bình (từ Materials.average_cost)

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private Integer createdBy;
    private Integer updatedBy;

    private boolean disable; // Giữ để tương thích (true nếu deletedAt != null)

    public Material() {
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url != null ? url.trim() : null;
    }

    /**
     * Getter giữ tên cũ để tránh sửa đổi lớn ở JSP/Servlet.
     */
    public String getMaterialsUrl() {
        return resolveMediaUrl(url);
    }

    public String getRawUrl() {
        return url;
    }

    public void setMaterialsUrl(String materialsUrl) {
        this.url = materialsUrl != null ? materialsUrl.trim() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaterialStatus() {
        return status;
    }

    public void setMaterialStatus(String materialStatus) {
        this.status = materialStatus;
    }

    private String resolveMediaUrl(String value) {
        if (value == null || value.trim().isEmpty()) {
            return DEFAULT_MEDIA_URL;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("/")) {
            return trimmed;
        }
        if (trimmed.startsWith("images/")) {
            return trimmed;
        }
        if (trimmed.startsWith("material/")) {
            return "images/" + trimmed;
        }
        return "images/material/" + trimmed;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Unit getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(Unit defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public Unit getUnit() {
        return defaultUnit;
    }

    public void setUnit(Unit unit) {
        this.defaultUnit = unit;
    }

    public Unit getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(Unit purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    public Unit getSalesUnit() {
        return salesUnit;
    }

    public void setSalesUnit(Unit salesUnit) {
        this.salesUnit = salesUnit;
    }

    public WarehouseRack getRack() {
        return rack;
    }

    public void setRack(WarehouseRack rack) {
        this.rack = rack;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public BigDecimal getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(BigDecimal maxStock) {
        this.maxStock = maxStock;
    }

    public BigDecimal getWeightPerUnit() {
        return weightPerUnit;
    }

    public void setWeightPerUnit(BigDecimal weightPerUnit) {
        this.weightPerUnit = weightPerUnit;
    }

    public BigDecimal getUnitWeight() {
        return weightPerUnit;
    }

    public void setUnitWeight(BigDecimal unitWeight) {
        this.weightPerUnit = unitWeight;
    }

    public BigDecimal getVolumePerUnit() {
        return volumePerUnit;
    }

    public void setVolumePerUnit(BigDecimal volumePerUnit) {
        this.volumePerUnit = volumePerUnit;
    }

    public BigDecimal getUnitVolume() {
        return volumePerUnit;
    }

    public void setUnitVolume(BigDecimal unitVolume) {
        this.volumePerUnit = unitVolume;
    }

    public Integer getShelfLifeDays() {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(Integer shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public boolean isSerialized() {
        return serialized;
    }

    public void setSerialized(boolean serialized) {
        this.serialized = serialized;
    }

    public boolean isBatchControlled() {
        return batchControlled;
    }

    public void setBatchControlled(boolean batchControlled) {
        this.batchControlled = batchControlled;
    }

    public BigDecimal getStockOnHand() {
        return stockOnHand;
    }

    public void setStockOnHand(BigDecimal stockOnHand) {
        this.stockOnHand = stockOnHand;
    }

    public BigDecimal getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(BigDecimal reservedStock) {
        this.reservedStock = reservedStock;
    }

    public BigDecimal getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(BigDecimal availableStock) {
        this.availableStock = availableStock;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public BigDecimal getQuantity() {
        return availableStock;
    }

    public void setQuantity(BigDecimal quantity) {
        this.availableStock = quantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
        this.disable = deletedAt != null;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
        if (disable && this.deletedAt == null) {
            this.deletedAt = new Timestamp(System.currentTimeMillis());
        }
        if (!disable) {
            this.deletedAt = null;
        }
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", materialCode='" + materialCode + '\'' +
                ", materialName='" + materialName + '\'' +
                ", barcode='" + barcode + '\'' +
                ", status='" + status + '\'' +
                ", category=" + category +
                ", defaultUnit=" + defaultUnit +
                ", purchaseUnit=" + purchaseUnit +
                ", salesUnit=" + salesUnit +
                ", minStock=" + minStock +
                ", maxStock=" + maxStock +
                ", weightPerUnit=" + weightPerUnit +
                ", volumePerUnit=" + volumePerUnit +
                ", shelfLifeDays=" + shelfLifeDays +
                ", serialized=" + serialized +
                ", batchControlled=" + batchControlled +
                ", stockOnHand=" + stockOnHand +
                ", reservedStock=" + reservedStock +
                ", availableStock=" + availableStock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
