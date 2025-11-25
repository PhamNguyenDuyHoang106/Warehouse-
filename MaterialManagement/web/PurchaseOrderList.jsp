<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Purchase Order Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
</head>
<body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

    <!-- Main Content Wrapper - Bao sidebar và body content -->
    <div class="main-content-wrapper">
      <!-- Sidebar - Nằm trong wrapper -->
      <div class="sidebar-wrapper-inner">
        <jsp:include page="Sidebar.jsp" />
      </div>
      
      <!-- Main Content Body - Nằm trong wrapper, bên cạnh sidebar -->
      <div class="main-content-body">
        <div class="container-fluid my-4" style="padding-left: 30px; padding-right: 30px;">
          <div class="row">
            <div class="col-12 px-md-4">
                <div class="page-headline">
                    <div>
                        <h1><i class="fas fa-file-invoice me-2 text-primary"></i>Purchase Orders</h1>
                        <p class="text-muted-soft mb-0">Quản lý toàn bộ đơn mua với giao diện đồng bộ</p>
                    </div>
                </div>
                
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${param.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <c:if test="${!hasViewPurchaseOrderListPermission}">
                    <div class="alert alert-danger">You do not have permission to view purchase order list.</div>
                    <jsp:include page="HomePage.jsp"/>
                </c:if>
                <c:if test="${hasViewPurchaseOrderListPermission}">
                    <div class="filter-card">
                        <form class="row g-3 align-items-end" method="GET" action="${pageContext.request.contextPath}/PurchaseOrderList">
                            <div class="col-md-3">
                                <label class="form-label">PO Code</label>
                                <input type="text" class="form-control" name="poCode" value="${poCode}" placeholder="Search by PO code">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Status</label>
                                <select class="form-select" name="status">
                                    <option value="">All Statuses</option>
                                    <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                                    <option value="confirmed" ${status == 'confirmed' ? 'selected' : ''}>Confirmed</option>
                                    <option value="sent" ${status == 'sent' ? 'selected' : ''}>Sent</option>
                                    <option value="partially_received" ${status == 'partially_received' ? 'selected' : ''}>Partially Received</option>
                                    <option value="received" ${status == 'received' ? 'selected' : ''}>Received</option>
                                    <option value="pending_receipt" ${status == 'pending_receipt' ? 'selected' : ''}>Pending Receipt</option>
                                    <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Sort</label>
                                <select class="form-select" name="sortBy">
                                    <option value="" ${sortBy == null || sortBy == '' ? 'selected' : ''}>Newest First</option>
                                    <option value="oldest" ${sortBy == 'oldest' ? 'selected' : ''}>Oldest First</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Start Date</label>
                                <input type="date" class="form-control" name="startDate" value="${startDate}">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">End Date</label>
                                <input type="date" class="form-control" name="endDate" value="${endDate}">
                            </div>
                            <div class="col-md-3 d-flex gap-2">
                                <button type="submit" class="btn btn-gradient flex-grow-1">
                                    <i class="fas fa-filter me-2"></i>Filter
                                </button>
                                <a href="${pageContext.request.contextPath}/PurchaseOrderList" class="btn btn-outline-secondary">
                                    Clear
                                </a>
                            </div>
                        </form>
                    </div>
                    
                    <c:if test="${not empty purchaseOrders}">
                        <div class="table-responsive" id="printTableListArea">
                            <table id="purchaseOrderTable" class="table custom-table">
                                <thead>
                                <tr>
                                    <th>PO Code</th>
                                    <th>Request Code</th>
                                    <th>Created Date</th>
                                    <th>Status</th>
                                    <th>Created By</th>
                                    <th>Total Amount</th>
                                    <th class="text-center">Pending Qty</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="po" items="${purchaseOrders}">
                                    <tr>
                                        <td><strong>${po.poCode}</strong></td>
                                        <td>${po.purchaseRequestCode}</td>
                                        <td>${po.createdAt}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${po.status == 'draft'}">
                                                    <span class="status-badge status-draft">Draft</span>
                                                </c:when>
                                                <c:when test="${po.status == 'confirmed'}">
                                                    <span class="status-badge status-approved">Confirmed</span>
                                                </c:when>
                                                <c:when test="${po.status == 'sent'}">
                                                    <span class="status-badge status-sent_to_supplier">Sent</span>
                                                </c:when>
                                                <c:when test="${po.status == 'partially_received'}">
                                                    <span class="status-badge status-pending">Partially Received</span>
                                                </c:when>
                                                <c:when test="${po.status == 'received'}">
                                                    <span class="status-badge status-approved">Received</span>
                                                </c:when>
                                                <c:when test="${po.status == 'cancelled'}">
                                                    <span class="status-badge status-cancelled">Cancelled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-pending">${po.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty po.createdByName}">
                                                    ${po.createdByName}
                                                </c:when>
                                                <c:otherwise>
                                                    Unknown
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <strong>$<fmt:formatNumber value="${po.totalAmount}" type="number" minFractionDigits="2"/></strong>
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${po.pendingQty != null && po.pendingQty.doubleValue() > 0}">
                                                    <span class="badge bg-warning text-dark">
                                                        <fmt:formatNumber value="${po.pendingQty}" maxFractionDigits="2"/>
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">0</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-1">
                                                <a href="PurchaseOrderDetail?id=${po.poId}" class="btn-detail" title="View Details" style="pointer-events:auto;z-index:9999;position:relative;">
                                                    <i class="fas fa-eye"></i> Detail
                                                </a>
                                                <c:set var="statusLower" value="${fn:toLowerCase(po.status)}" />
                                                <c:if test="${statusLower == 'draft' && hasHandleRequestPermission}">
                                                    <button type="button" class="btn btn-success btn-sm" onclick="setModalAction('confirmed', '${po.poId}')" title="Approve">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-danger btn-sm" onclick="setModalAction('cancelled', '${po.poId}')" title="Reject">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${hasSendToSupplierPermission && po.status == 'confirmed'}">
                                                    <button type="button" class="btn btn-detail btn-sm" style="background-color: #0d6efd; color: #fff; border: none;" title="Send to Supplier" onclick="updateStatus('${po.poId}', 'sent')">
                                                        <i class="fas fa-paper-plane"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <nav aria-label="Purchase Order pagination">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="PurchaseOrderList?page=${currentPage - 1}&status=${status}&poCode=${poCode}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}">
                                        Previous
                                    </a>
                                </li>
                                
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="PurchaseOrderList?page=${i}&status=${status}&poCode=${poCode}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                    </li>
                                </c:forEach>
                                
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="PurchaseOrderList?page=${currentPage + 1}&status=${status}&poCode=${poCode}&sortBy=${sortBy}&startDate=${startDate}&endDate=${endDate}">
                                        Next
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                    
                    <c:if test="${empty purchaseOrders}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Purchase Orders Found</h4>
                            <p class="text-muted">No purchase orders match your current filters.</p>
                        </div>
                    </c:if>
                </c:if>
            </div>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->

<div class="modal fade" id="statusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Update Purchase Order Status</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="statusForm" method="POST" action="PurchaseOrderList">
                <div class="modal-body">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="poId" id="poId">
                    <input type="hidden" name="status" id="statusHidden">
                    
                    <div class="mb-3" id="approvalReasonDiv" style="display: none;">
                        <label for="approvalReason" class="form-label">Approval Reason</label>
                        <textarea class="form-control" name="approvalReason" id="approvalReason" rows="3" placeholder="Enter approval reason..."></textarea>
                    </div>
                    
                    <div class="mb-3" id="rejectionReasonDiv" style="display: none;">
                        <label for="rejectionReason" class="form-label">Rejection Reason <span style="color:red">*</span></label>
                        <textarea class="form-control" name="rejectionReason" id="rejectionReason" rows="3" placeholder="Enter rejection reason..." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Update Status</button>
                </div>
            </form>
        </div>
    </div>
</div>

<form id="sendToSupplierForm" method="POST" action="PurchaseOrderList" style="display:none;">
    <input type="hidden" name="action" value="updateStatus">
    <input type="hidden" name="poId" id="sendToSupplierPoId">
    <input type="hidden" name="status" value="sent">
</form>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function setModalAction(status, poId) {
        document.getElementById('poId').value = poId;
        document.getElementById('statusHidden').value = status;
        
        const approvalReasonDiv = document.getElementById('approvalReasonDiv');
        const rejectionReasonDiv = document.getElementById('rejectionReasonDiv');
        const approvalReason = document.getElementById('approvalReason');
        const rejectionReason = document.getElementById('rejectionReason');
        
        // Clear previous values
        if (approvalReason) approvalReason.value = '';
        if (rejectionReason) rejectionReason.value = '';
        
        if (status === 'confirmed') {
            approvalReasonDiv.style.display = 'block';
            rejectionReasonDiv.style.display = 'none';
            if (rejectionReason) rejectionReason.removeAttribute('required');
            if (approvalReason) approvalReason.removeAttribute('required');
        } else if (status === 'cancelled') {
            approvalReasonDiv.style.display = 'none';
            rejectionReasonDiv.style.display = 'block';
            if (rejectionReason) rejectionReason.setAttribute('required', 'required');
            if (approvalReason) approvalReason.removeAttribute('required');
        } else {
            approvalReasonDiv.style.display = 'none';
            rejectionReasonDiv.style.display = 'none';
        }
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('statusModal'));
        modal.show();
    }
    
    function updateStatus(poId, status) {
        if (status === 'sent') {
            if (confirm('Are you sure you want to send this purchase order to the supplier?')) {
                document.getElementById('sendToSupplierPoId').value = poId;
                document.getElementById('sendToSupplierForm').submit();
            }
            return;
        }
    }
</script>
</body>
</html> 