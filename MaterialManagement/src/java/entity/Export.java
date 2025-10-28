package entity;

import java.time.LocalDateTime;

public class Export {

    private int exportId;
    private String exportCode;
    private LocalDateTime exportDate;
    private int exportedBy;
    private Integer recipientId;  // FK to Recipients table
    private Integer vehicleId;  // FK to Vehicles table
    private Integer exportRequestId;  // FK to Export_Requests table
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields for display
    private String exportedByName;
    private String recipientName;
    private String recipientLocation;
    private String vehicleLicensePlate;
    private String exportRequestCode;
    private int totalQuantity;
    private double totalValue;

    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public String getExportCode() {
        return exportCode;
    }

    public void setExportCode(String exportCode) {
        this.exportCode = exportCode;
    }

    public LocalDateTime getExportDate() {
        return exportDate;
    }

    public void setExportDate(LocalDateTime exportDate) {
        this.exportDate = exportDate;
    }

    public int getExportedBy() {
        return exportedBy;
    }

    public void setExportedBy(int exportedBy) {
        this.exportedBy = exportedBy;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getExportRequestId() {
        return exportRequestId;
    }

    public void setExportRequestId(Integer exportRequestId) {
        this.exportRequestId = exportRequestId;
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

    public String getExportedByName() {
        return exportedByName;
    }

    public void setExportedByName(String exportedByName) {
        this.exportedByName = exportedByName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientLocation() {
        return recipientLocation;
    }

    public void setRecipientLocation(String recipientLocation) {
        this.recipientLocation = recipientLocation;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public String getExportRequestCode() {
        return exportRequestCode;
    }

    public void setExportRequestCode(String exportRequestCode) {
        this.exportRequestCode = exportRequestCode;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}