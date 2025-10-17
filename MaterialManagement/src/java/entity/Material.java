/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class Material {

    private int materialId;
    private String materialCode;
    private String materialName;
    private String materialsUrl;
    private String materialStatus;

    private Category category;
    private Unit unit;
    private int quantity;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean disable;

    public Material() {
    }

    public Material(int materialId, String materialCode, String materialName, String materialsUrl, String materialStatus,
             Category category, Unit unit,
            Timestamp createdAt, Timestamp updatedAt, boolean disable) {
        this.materialId = materialId;
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.materialsUrl = materialsUrl;
        this.materialStatus = materialStatus;
        this.category = category;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
    }

    // Getters và Setters
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

    public String getMaterialsUrl() {
        return materialsUrl;
    }

    public void setMaterialsUrl(String materialsUrl) {
        this.materialsUrl = materialsUrl;
    }

    public String getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(String materialStatus) {
        this.materialStatus = materialStatus;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public String toString() {
        return "Material{" + "materialId=" + materialId + ", materialCode=" + materialCode + ", materialName=" + materialName + ", materialsUrl=" + materialsUrl + ", materialStatus=" + materialStatus + ", category=" + category + ", unit=" + unit + ", quantity=" + quantity + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", disable=" + disable + '}';
    }

    
}
