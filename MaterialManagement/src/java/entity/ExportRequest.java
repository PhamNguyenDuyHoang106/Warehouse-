package entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class ExportRequest {
    private int exportRequestId;
    private String requestCode;
    private int userId;
    private String userName;
    private Integer customerId; // Changed from recipientId to customerId - Customer replaces Recipient
    private String customerName;
    private Timestamp requestDate;
    private String status;
    private Date deliveryDate;
    private String reason;
    private int approvedBy;
    private String approverName;
    private String approvalReason;
    private Timestamp approvedAt;
    private String rejectionReason;
    private boolean isUsed;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private List<ExportRequestDetail> details;

    public int getExportRequestId() {
        return exportRequestId;
    }

    public void setExportRequestId(int exportRequestId) {
        this.exportRequestId = exportRequestId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    // Legacy methods for backward compatibility (deprecated)
    @Deprecated
    public Integer getRecipientId() {
        return customerId;
    }

    @Deprecated
    public void setRecipientId(Integer recipientId) {
        this.customerId = recipientId;
    }

    @Deprecated
    public String getRecipientName() {
        return customerName;
    }

    @Deprecated
    public void setRecipientName(String recipientName) {
        this.customerName = recipientName;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public List<ExportRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ExportRequestDetail> details) {
        this.details = details;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
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
    }
    
    // Compatibility method
    public boolean isDisable() {
        return deletedAt != null;
    }

    public void setDisable(boolean disable) {
        this.deletedAt = disable ? new Timestamp(System.currentTimeMillis()) : null;
    }
} 