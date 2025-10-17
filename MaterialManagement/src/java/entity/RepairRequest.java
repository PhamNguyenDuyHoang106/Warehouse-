/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nb://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author Nhat Anh
 */
public class RepairRequest {

    private int repairRequestId;
    private String requestCode;
    private int userId;
    private Timestamp requestDate;
    private String repairPersonPhoneNumber;
    private String repairPersonEmail;
    private String repairLocation;
    private String status;
    private String reason;
    private Integer approvedBy; // Nullable theo FOREIGN KEY
    private String approvalReason;
    private Timestamp approvedAt;
    private String rejectionReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean disable;
    private List<RepairRequestDetail> details; // Added field

    private String fullName;

    // Constructor mặc định
    public RepairRequest() {
    }

    // Constructor với tất cả các thuộc tính, including details
    public RepairRequest(int repairRequestId, String requestCode, int userId, Timestamp requestDate,
            String status, String reason, Integer approvedBy, String approvalReason,
            Timestamp approvedAt, String rejectionReason, Timestamp createdAt,
            Timestamp updatedAt, boolean disable, List<RepairRequestDetail> details) {
        this.repairRequestId = repairRequestId;
        this.requestCode = requestCode;
        this.userId = userId;
        this.requestDate = requestDate;
        this.status = status;
        this.reason = reason;
        this.approvedBy = approvedBy;
        this.approvalReason = approvalReason;
        this.approvedAt = approvedAt;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.disable = disable;
        this.details = details;
    }

    public int getRepairRequestId() {
        return repairRequestId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRepairRequestId(int repairRequestId) {
        this.repairRequestId = repairRequestId;
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

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getRepairPersonPhoneNumber() {
        return repairPersonPhoneNumber;
    }

    public void setRepairPersonPhoneNumber(String repairPersonPhoneNumber) {
        this.repairPersonPhoneNumber = repairPersonPhoneNumber;
    }

    public String getRepairPersonEmail() {
        return repairPersonEmail;
    }

    public void setRepairPersonEmail(String repairPersonEmail) {
        this.repairPersonEmail = repairPersonEmail;
    }

    public String getRepairLocation() {
        return repairLocation;
    }

    public void setRepairLocation(String repairLocation) {
        this.repairLocation = repairLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    // Getter and setter for details
    public List<RepairRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RepairRequestDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RepairRequest{"
                + "repairRequestId=" + repairRequestId
                + ", requestCode='" + requestCode + '\''
                + ", userId=" + userId
                + ", requestDate=" + requestDate
                + ", repairPersonPhoneNumber='" + repairPersonPhoneNumber + '\''
                + ", repairPersonEmail='" + repairPersonEmail + '\''
                + ", repairLocation='" + repairLocation + '\''
                + ", status='" + status + '\''
                + ", reason='" + reason + '\''
                + ", approvedBy=" + approvedBy
                + ", approvalReason='" + approvalReason + '\''
                + ", approvedAt=" + approvedAt
                + ", rejectionReason='" + rejectionReason + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", disable=" + disable
                + ", details=" + details
                + '}';
    }
}
