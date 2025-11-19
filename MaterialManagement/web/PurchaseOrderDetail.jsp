<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Purchase Order Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
        }
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .card {
            border: none;
            margin-bottom: 20px;
        }
        .card-header {
            background-color: #DEAD6F;
            border-bottom: none;
            font-weight: bold;
            color: #fff;
        }
        .table {
            margin-top: 20px;
        }
        .btn {
            margin-right: 10px;
            background-color: #DEAD6F;
            border: none;
            color: #fff;
        }
        .btn:hover {
            background-color: #c79b5a;
            color: #fff;
        }
        .alert {
            margin-top: 20px;
        }
        .total-amount {
            font-size: 1.2em;
            font-weight: bold;
            color: #198754;
        }
        .status-tag {
            padding: 5px 10px;
            border-radius: 5px;
            color: #fff;
        }
        .status-pending { background-color: #6c757d; }
        .status-approved { background-color: #198754; }
        .status-rejected { background-color: #dc3545; }
        .status-sent_to_supplier { background-color: #0d6efd; }
        .status-cancel { background-color: #4b0082; }
        .page-info {
            text-align: center;
            margin: 10px 0;
            font-weight: bold;
        }
        .custom-table thead th {
            background-color: #f9f5f0;
            color: #5c4434;
            font-weight: 600;
        }
        .custom-table tbody tr:hover {
            background-color: #f1f1f1;
        }
        .custom-table th,
        .custom-table td {
            vertical-align: middle;
            min-height: 48px;
        }
        .pagination .page-link {
            color: #5c4434;
            background-color: #f4f1ed;
            border: 1px solid #e2d6c3;
            transition: background-color 0.3s, color 0.3s;
        }
        .pagination .page-link:hover {
            background-color: #e7dbc7;
            color: #5c4434;
        }
        .pagination .page-item.active .page-link {
            background-color: #DEAD6F;
            border-color: #DEAD6F;
            color: #fff;
        }
        .pagination .page-link {
            color: #DEAD6F;
        }
        .pagination .page-link:hover {
            background-color: #DEAD6F;
            border-color: #DEAD6F;
            color: #fff;
        }
        .pagination .page-item.disabled .page-link {
            color: #6c757d;
        }
        .pagination .page-item.disabled .page-link {
            background-color: #eee;
            color: #aaa;
            border-color: #ddd;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>${purchaseOrder.poCode} <span class="status-tag status-${purchaseOrder.status}">${purchaseOrder.status}</span></h2>
    <p>Created at: ${purchaseOrder.createdAt}</p>

    <!-- Card: Created By (Full Info) -->
    <div class="card">
        <div class="card-header">Created By</div>
        <div class="card-body d-flex align-items-center">
            <div style="flex: 1;">
                <p><strong>Full Name:</strong> ${creator.fullName}</p>
                <p><strong>Email:</strong> ${creator.email}</p>
                <p><strong>Phone Number:</strong> ${creator.phoneNumber}</p>
                <p><strong>Department:</strong> ${creator.departmentName}</p>
                <p><strong>Role:</strong> ${creator.roleName}</p>
            </div>
            <div style="flex: 0 0 100px; margin-left: 20px;">
                <img src="images/profiles/${not empty creator.userPicture ? creator.userPicture : 'DefaultUser.jpg'}" alt="${creator.fullName}" class="img-fluid rounded-circle" style="width:100px;height:100px;object-fit:cover;">
            </div>
        </div>
    </div>

    <!-- Card: Order Information -->
    <div class="card">
        <div class="card-header">Order Information</div>
        <div class="card-body">
            <p><strong>Request Code:</strong> ${purchaseOrder.purchaseRequestCode}</p>
            <p><strong>Status:</strong> <span class="status-tag status-${purchaseOrder.status}">${purchaseOrder.status}</span></p>
            <p><strong>Created Date:</strong> ${purchaseOrder.createdAt}</p>
            <c:if test="${not empty purchaseOrder.note}">
                <p><strong>Note:</strong> ${purchaseOrder.note}</p>
            </c:if>
            <c:if test="${not empty purchaseOrder.approvedByName}">
                <p><strong>Approved By:</strong> ${purchaseOrder.approvedByName}</p>
                <p><strong>Approved Date:</strong> ${purchaseOrder.approvedAt}</p>
            </c:if>
            <c:if test="${not empty purchaseOrder.rejectionReason}">
                <p><strong>Rejection Reason:</strong> <span class="text-danger">${purchaseOrder.rejectionReason}</span></p>
            </c:if>
            <c:if test="${not empty purchaseOrder.sentToSupplierAt}">
                <p><strong>Sent to Supplier:</strong> ${purchaseOrder.sentToSupplierAt}</p>
            </c:if>
            <p><strong>Total Amount:</strong> <span class="total-amount">$<fmt:formatNumber value="${purchaseOrder.totalAmount}" type="number" minFractionDigits="2"/></span></p>
        </div>
    </div>

    <!-- Card: Material List -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Material List</span>
        </div>
        <div class="card-body">
            <div class="table-responsive" id="printTableArea">
                <table class="table custom-table">
                    <thead>
                        <tr>
                            <th>Material Name</th>
                            <th>Image</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                            <th>Supplier</th>
                            <th>Note</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="detail" items="${pagedDetails}">
                            <tr>
                                <td><strong>${detail.materialName}</strong></td>
                                <td>
                                    <c:set var="mediaUrl" value="${materialImages[detail.materialId]}" />
                                    <c:choose>
                                        <c:when test="${mediaUrl != null && mediaUrl != '' && (fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/'))}">
                                            <img src="${mediaUrl}" alt="${detail.materialName}" style="width: 100px; height: auto; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/${mediaUrl}" alt="${detail.materialName}" style="width: 100px; height: auto; object-fit: cover;">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${detail.quantity}</td>
                                <td>$<fmt:formatNumber value="${detail.unitPrice}" type="number" minFractionDigits="2"/></td>
                                <td><strong>$<fmt:formatNumber value="${detail.quantity * detail.unitPrice}" type="number" minFractionDigits="2"/></strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty detail.supplierName}">
                                            ${detail.supplierName}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Not specified</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty detail.note}">
                                            ${detail.note}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <c:if test="${not empty pagedDetails}">
                <nav class="mt-3">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${currentPage - 1}">Previous</a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${i}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="PurchaseOrderDetail?id=${purchaseOrder.poId}&page=${currentPage + 1}">Next</a>
                        </li>
                    </ul>
                </nav>
            </c:if>
            <c:if test="${empty pagedDetails}">
                <div class="text-center py-4">
                    <i class="fas fa-inbox fa-2x text-muted mb-3"></i>
                    <p class="text-muted">No details found for this purchase order.</p>
                </div>
            </c:if>
        </div>
    </div>

    <!-- Action Buttons -->
    <!-- Hiển thị nút duyệt khi có permission và status là 'draft' (chưa được duyệt) -->
    <!-- Admin luôn có quyền duyệt -->
    <c:set var="canApprove" value="${hasHandleRequestPermission || sessionScope.user.roleId == 1}" />
    <c:set var="statusLower" value="${fn:toLowerCase(purchaseOrder.status)}" />
    <c:set var="isDraft" value="${statusLower == 'draft'}" />
    <c:if test="${canApprove && isDraft}">
        <div class="d-flex gap-2 mb-2">
            <button type="button" class="btn" style="background-color: #198754; color: #fff; border: none;" onclick="updateStatus('${purchaseOrder.poId}', 'confirmed')">Approve</button>
            <button type="button" class="btn" style="background-color: #dc3545; color: #fff; border: none;" onclick="updateStatus('${purchaseOrder.poId}', 'cancelled')">Reject</button>
            <a href="PurchaseOrderList" class="btn btn-warning">Cancel</a>
        </div>
    </c:if>
    <c:if test="${!(canApprove && isDraft)}">
        <div class="mb-2">
            <a href="PurchaseOrderList" class="btn btn-warning">Cancel</a>
        </div>
    </c:if>
</div>

<!-- Status Update Modal -->
<div class="modal fade" id="statusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Update Purchase Order Status</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="statusForm" method="POST" action="PurchaseOrderDetail">
                <div class="modal-body">
                    <input type="hidden" name="action" value="updateStatus">
                    <input type="hidden" name="poId" id="poId">
                    <input type="hidden" name="status" id="statusHidden">
                    <div class="mb-3" id="approvalReasonDiv" style="display: none;">
                        <label for="approvalReason" class="form-label">Approval Reason</label>
                        <textarea class="form-control" name="approvalReason" id="approvalReason" rows="3" placeholder="Enter approval reason..."></textarea>
                    </div>
                    <div class="mb-3" id="rejectionReasonDiv" style="display: none;">
                        <label for="rejectionReason" class="form-label">Rejection Reason</label>
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function updateStatus(poId, status) {
        document.getElementById('poId').value = poId;
        let statusInput = document.getElementById('statusHidden');
        if (!statusInput) {
            statusInput = document.createElement('input');
            statusInput.type = 'hidden';
            statusInput.name = 'status';
            statusInput.id = 'statusHidden';
            document.getElementById('statusForm').appendChild(statusInput);
        }
        statusInput.value = status;
        if (status === 'approved') {
            document.getElementById('approvalReasonDiv').style.display = 'block';
            document.getElementById('rejectionReasonDiv').style.display = 'none';
            document.getElementById('approvalReason').setAttribute('required', 'required');
            document.getElementById('rejectionReason').removeAttribute('required');
        } else if (status === 'rejected') {
            document.getElementById('approvalReasonDiv').style.display = 'none';
            document.getElementById('rejectionReasonDiv').style.display = 'block';
            document.getElementById('rejectionReason').setAttribute('required', 'required');
            document.getElementById('approvalReason').removeAttribute('required');
        }
        new bootstrap.Modal(document.getElementById('statusModal')).show();
    }
</script>
</body>
</html> 