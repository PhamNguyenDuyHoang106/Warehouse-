package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ExportRequestDetail {

    private static final String DEFAULT_MEDIA_URL = "images/material/default-material.png";

    private int detailId;
    private int exportRequestId;
    private int materialId;
    private Integer rackId; // Vị trí kệ mong muốn
    private Integer warehouseId;
    private String materialCode;
    private String materialName;
    private String materialUnit;
    private BigDecimal quantity; // Thay đổi từ int sang BigDecimal
    private BigDecimal unitPriceExport; // V8: Giá xuất - REQUIRED cho profit calculation
    private String status;
    private String materialImageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String unitName;
    private String note;

    public ExportRequestDetail() {
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getExportRequestId() {
        return exportRequestId;
    }

    public void setExportRequestId(int exportRequestId) {
        this.exportRequestId = exportRequestId;
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

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Integer getRackId() {
        return rackId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaterialImageUrl() {
        return resolveMediaUrl(materialImageUrl);
    }

    public void setMaterialImageUrl(String materialImageUrl) {
        this.materialImageUrl = materialImageUrl != null ? materialImageUrl.trim() : null;
    }

    public String getRawMaterialImageUrl() {
        return materialImageUrl;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public BigDecimal getUnitPriceExport() {
        return unitPriceExport;
    }

    public void setUnitPriceExport(BigDecimal unitPriceExport) {
        this.unitPriceExport = unitPriceExport;
    }
}
