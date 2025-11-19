/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
import java.sql.Timestamp;
import java.math.BigDecimal;

public class PurchaseRequestDetail {

    private int id;
    private int purchaseRequestId;
    private int materialId;
    private Integer unitId;
    private BigDecimal quantity;
    private BigDecimal unitPriceEstimate;
    private BigDecimal totalEstimate;
    private String note;
    private Timestamp createdAt;
    private String materialName;
    private String materialCode;
    private String unitName;

    public PurchaseRequestDetail() {
        this.quantity = BigDecimal.ZERO;
        this.unitPriceEstimate = BigDecimal.ZERO;
        this.totalEstimate = BigDecimal.ZERO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        recalcTotalEstimate();
    }

    public BigDecimal getUnitPriceEstimate() {
        return unitPriceEstimate;
    }

    public void setUnitPriceEstimate(BigDecimal unitPriceEstimate) {
        this.unitPriceEstimate = unitPriceEstimate;
        recalcTotalEstimate();
    }

    public BigDecimal getTotalEstimate() {
        return totalEstimate;
    }

    public void setTotalEstimate(BigDecimal totalEstimate) {
        this.totalEstimate = totalEstimate;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    private void recalcTotalEstimate() {
        if (quantity != null && unitPriceEstimate != null) {
            this.totalEstimate = unitPriceEstimate.multiply(quantity);
        }
    }

    // Compatibility helpers -------------------------------------------------
    public int getPurchaseRequestDetailId() {
        return id;
    }

    public void setPurchaseRequestDetailId(int purchaseRequestDetailId) {
        this.id = purchaseRequestDetailId;
    }

    public BigDecimal getUnit_price_est() {
        return unitPriceEstimate;
    }

    public void setUnit_price_est(BigDecimal value) {
        this.unitPriceEstimate = value;
        recalcTotalEstimate();
    }

    public BigDecimal getTotal_est() {
        return totalEstimate;
    }

    public void setTotal_est(BigDecimal value) {
        this.totalEstimate = value;
    }

    public String getNotes() {
        return note;
    }

    public void setNotes(String notes) {
        this.note = notes;
    }
}
