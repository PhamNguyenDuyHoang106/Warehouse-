package entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Export {

    private int exportId;
    private String exportCode;
    private Integer soId;  // FK to Sales_Orders table (v11)
    private Integer erId;  // FK to Export_Requests table (v11)
    private int warehouseId;  // Required FK to Warehouses table (v11)
    private LocalDateTime exportDate;
    private int exportedBy;  // FK to Users table
    private BigDecimal totalQuantity;  // DECIMAL(15,4) in v11
    private String status;  // ENUM in v11
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;  // FK to Users table

    // Joined fields for display
    private String exportedByName;
    private String salesOrderCode;
    private String exportRequestCode;
    private String warehouseName;
    private String customerName;  // From SO
    private BigDecimal totalValue;  // Calculated from Export_Details

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

    public Integer getSoId() {
        return soId;
    }

    public void setSoId(Integer soId) {
        this.soId = soId;
    }

    public Integer getErId() {
        return erId;
    }

    public void setErId(Integer erId) {
        this.erId = erId;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getExportedBy() {
        return exportedBy;
    }

    public void setExportedBy(int exportedBy) {
        this.exportedBy = exportedBy;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getExportedByName() {
        return exportedByName;
    }

    public void setExportedByName(String exportedByName) {
        this.exportedByName = exportedByName;
    }

    public String getSalesOrderCode() {
        return salesOrderCode;
    }

    public void setSalesOrderCode(String salesOrderCode) {
        this.salesOrderCode = salesOrderCode;
    }

    public String getExportRequestCode() {
        return exportRequestCode;
    }

    public void setExportRequestCode(String exportRequestCode) {
        this.exportRequestCode = exportRequestCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    // Compatibility methods for backward compatibility
    public Integer getRecipientId() {
        return null; // Not used in v11
    }

    public void setRecipientId(Integer recipientId) {
        // Not used in v11
    }

    public Integer getVehicleId() {
        return null; // Not used in v11
    }

    public void setVehicleId(Integer vehicleId) {
        // Not used in v11
    }

    public Integer getExportRequestId() {
        return erId; // Map to erId
    }

    public void setExportRequestId(Integer exportRequestId) {
        this.erId = exportRequestId;
    }
}