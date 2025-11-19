package entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Admin
 */
public class PurchaseRequest {
    private int id;
    private String code;
    private int requestBy;
    private Integer departmentId;
    private Date requestDate;
    private Date expectedDate;
    private BigDecimal totalAmount;
    private String status; // draft, submitted, approved, rejected, cancelled
    private Integer approvedBy;
    private Timestamp approvedAt;
    private String reason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private String requesterName;
    private String departmentName;
    private List<PurchaseRequestDetail> details;

    public PurchaseRequest() {
        this.totalAmount = BigDecimal.ZERO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(int requestBy) {
        this.requestBy = requestBy;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<PurchaseRequestDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseRequestDetail> details) {
        this.details = details;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // Compatibility helpers for legacy code ---------------------------------
    public int getPurchaseRequestId() {
        return id;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.id = purchaseRequestId;
    }

    public String getRequestCode() {
        return code;
    }

    public void setRequestCode(String requestCode) {
        this.code = requestCode;
    }

    public int getUserId() {
        return requestBy;
    }

    public void setUserId(int userId) {
        this.requestBy = userId;
    }

    public Timestamp getRequestDateTime() {
        return requestDate != null ? new Timestamp(requestDate.getTime()) : null;
    }

    public void setRequestDateTime(Timestamp timestamp) {
        this.requestDate = timestamp != null ? new Date(timestamp.getTime()) : null;
    }

    public Double getEstimatedPrice() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.totalAmount = BigDecimal.valueOf(estimatedPrice);
    }
}