package entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class PurchaseOrder {
    private int poId;
    private String poCode;
    private int purchaseRequestId;
    private Integer supplierId;
    private String supplierName;
    private Integer currencyId;
    private String currencyCode;
    private Date orderDate;
    private Date expectedDeliveryDate;
    private String deliveryAddress;
    private Integer paymentTermId;
    private String paymentTermName;
    private int createdBy;
    private String createdByName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
    private String note;
    private Integer confirmedBy;
    private String confirmedByName;
    private Timestamp confirmedAt;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private Timestamp deletedAt;
    private String purchaseRequestCode;
    private BigDecimal aggregatedDetailTotal = BigDecimal.ZERO;
    private List<PurchaseOrderDetail> details;
    
    // From vw_po_pending_receipt view
    private BigDecimal pendingQty;

    public PurchaseOrder() {
    }

    public int getPoId() {
        return poId;
    }

    public void setPoId(int poId) {
        this.poId = poId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public int getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(int purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
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

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Integer getPaymentTermId() {
        return paymentTermId;
    }

    public void setPaymentTermId(Integer paymentTermId) {
        this.paymentTermId = paymentTermId;
    }

    public String getPaymentTermName() {
        return paymentTermName;
    }

    public void setPaymentTermName(String paymentTermName) {
        this.paymentTermName = paymentTermName;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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

    public Integer getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(Integer confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public String getConfirmedByName() {
        return confirmedByName;
    }

    public void setConfirmedByName(String confirmedByName) {
        this.confirmedByName = confirmedByName;
    }

    public Timestamp getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Timestamp confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount != null ? taxAmount : BigDecimal.ZERO;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal != null ? grandTotal : BigDecimal.ZERO;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getPurchaseRequestCode() {
        return purchaseRequestCode;
    }

    public void setPurchaseRequestCode(String purchaseRequestCode) {
        this.purchaseRequestCode = purchaseRequestCode;
    }

    public BigDecimal getAggregatedDetailTotal() {
        return aggregatedDetailTotal;
    }

    public void setAggregatedDetailTotal(BigDecimal aggregatedDetailTotal) {
        this.aggregatedDetailTotal = aggregatedDetailTotal != null ? aggregatedDetailTotal : BigDecimal.ZERO;
    }

    public List<PurchaseOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details;
    }

    // Compatibility helpers --------------------------------------------------
    public boolean isDisable() {
        return deletedAt != null;
    }

    public void setDisable(boolean disable) {
        if (!disable) {
            this.deletedAt = null;
        } else if (this.deletedAt == null) {
            this.deletedAt = new Timestamp(System.currentTimeMillis());
        }
    }

    public Integer getApprovedBy() {
        return confirmedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.confirmedBy = approvedBy;
    }

    public String getApprovedByName() {
        return confirmedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.confirmedByName = approvedByName;
    }

    public Timestamp getApprovedAt() {
        return confirmedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.confirmedAt = approvedAt;
    }

    public String getRejectionReason() {
        return note;
    }

    public void setRejectionReason(String rejectionReason) {
        this.note = rejectionReason;
    }

    public Timestamp getSentToSupplierAt() {
        return confirmedAt;
    }

    public void setSentToSupplierAt(Timestamp sentToSupplierAt) {
        this.confirmedAt = sentToSupplierAt;
    }

    public double getTotalAmountLegacy() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    public BigDecimal getPendingQty() {
        return pendingQty;
    }

    public void setPendingQty(BigDecimal pendingQty) {
        this.pendingQty = pendingQty;
    }
} 