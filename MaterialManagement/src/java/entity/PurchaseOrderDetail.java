package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PurchaseOrderDetail {

    private static final String DEFAULT_MEDIA_URL = "images/material/default-material.png";

    private int poDetailId;
    private int poId;
    private int materialId;
    private String materialName;
    private String materialCode;
    private String materialImageUrl;
    private Integer unitId;
    private String unitName;
    private BigDecimal quantityOrdered = BigDecimal.ZERO;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;
    private BigDecimal discountRate = BigDecimal.ZERO;
    private BigDecimal lineTotal = BigDecimal.ZERO;
    private BigDecimal receivedQuantity = BigDecimal.ZERO;
    private String note;
    private Timestamp createdAt;

    // Legacy fields for compatibility (no longer persisted)
    private Integer categoryId;
    private String categoryName;
    private Integer supplierId;
    private String supplierName;

    public PurchaseOrderDetail() {
    }

    public int getPoDetailId() {
        return poDetailId;
    }

    public void setPoDetailId(int poDetailId) {
        this.poDetailId = poDetailId;
    }

    public int getPoId() {
        return poId;
    }

    public void setPoId(int poId) {
        this.poId = poId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
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

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(BigDecimal quantityOrdered) {
        this.quantityOrdered = quantityOrdered != null ? quantityOrdered : BigDecimal.ZERO;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate != null ? taxRate : BigDecimal.ZERO;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal != null ? lineTotal : BigDecimal.ZERO;
    }

    public BigDecimal getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(BigDecimal receivedQuantity) {
        this.receivedQuantity = receivedQuantity != null ? receivedQuantity : BigDecimal.ZERO;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    // Compatibility helpers --------------------------------------------------
    public BigDecimal getQuantity() {
        return quantityOrdered;
    }

    public void setQuantity(BigDecimal quantity) {
        setQuantityOrdered(quantity);
    }

    public BigDecimal getTotalPrice() {
        if (unitPrice != null && quantityOrdered != null) {
            return unitPrice.multiply(quantityOrdered);
        }
        return BigDecimal.ZERO;
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
