/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequestDetail {

    private int detailId;
    private int repairRequestId;
    private int materialId;
    private BigDecimal quantity;
    private String damageDescription;
    private Double repairCost;
    private int supplierId;
    private String supplierName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String materialName;
    private String materialCode;
    private String unitName;
    private Material material;

    public RepairRequestDetail() {
    }

    public RepairRequestDetail(int detailId, int repairRequestId, int materialId, java.math.BigDecimal quantity, String damageDescription, Double repairCost, Timestamp createdAt, Timestamp updatedAt) {
        this.detailId = detailId;
        this.repairRequestId = repairRequestId;
        this.materialId = materialId;
        this.quantity = quantity;
        this.damageDescription = damageDescription;
        this.repairCost = repairCost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getDetailId() {
        return detailId;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getRepairRequestId() {
        return repairRequestId;
    }

    public void setRepairRequestId(int repairRequestId) {
        this.repairRequestId = repairRequestId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public Double getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(Double repairCost) {
        this.repairCost = repairCost;
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

    @Override
    public String toString() {
        return "RepairRequestDetail{"
                + "detailId=" + detailId
                + ", repairRequestId=" + repairRequestId
                + ", materialId=" + materialId
                + ", quantity=" + quantity
                + ", damageDescription='" + damageDescription + '\''
                + ", repairCost=" + repairCost
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }

}
