package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExportDetail {

    private static final String DEFAULT_MEDIA_URL = "images/material/default-material.png";

    private int exportDetailId;
    private int exportId;
    private int materialId;
    private Integer exportRequestDetailId;  // Link to Export_Request_Details (V8)
    private Integer rackId;  // Warehouse rack location
    private BigDecimal quantity;  // DECIMAL(15,4) in V8
    private String status;  // 'draft' or 'exported' (V8)
    private BigDecimal unitPriceExport;  // Selling price - REQUIRED for profit calculation (V8)
    private BigDecimal totalAmountExport;  // Generated column: unit_price_export * quantity (V8)
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Joined fields for display
    private String materialName;
    private String materialCode;
    private String unitName;
    private String materialsUrl;
    private String rackCode;
    private String rackName;

    public int getExportDetailId() {
        return exportDetailId;
    }

    public String getMaterialsUrl() {
        return resolveMediaUrl(materialsUrl);
    }

    public void setMaterialsUrl(String materialsUrl) {
        this.materialsUrl = materialsUrl != null ? materialsUrl.trim() : null;
    }

    public String getRawMaterialsUrl() {
        return materialsUrl;
    }

    
    public void setExportDetailId(int exportDetailId) {
        this.exportDetailId = exportDetailId;
    }

    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public Integer getRackId() {
        return rackId;
    }

    public void setRackId(Integer rackId) {
        this.rackId = rackId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getRackCode() {
        return rackCode;
    }

    public void setRackCode(String rackCode) {
        this.rackCode = rackCode;
    }

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Integer getExportRequestDetailId() {
        return exportRequestDetailId;
    }

    public void setExportRequestDetailId(Integer exportRequestDetailId) {
        this.exportRequestDetailId = exportRequestDetailId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getUnitPriceExport() {
        return unitPriceExport;
    }

    public void setUnitPriceExport(BigDecimal unitPriceExport) {
        this.unitPriceExport = unitPriceExport;
    }

    public BigDecimal getTotalAmountExport() {
        return totalAmountExport;
    }

    public void setTotalAmountExport(BigDecimal totalAmountExport) {
        this.totalAmountExport = totalAmountExport;
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
}
