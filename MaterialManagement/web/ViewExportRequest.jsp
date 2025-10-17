<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Request Details</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
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
            color: #dc3545;
        }
        .status-tag {
            padding: 5px 10px;
            border-radius: 5px;
            color: #fff;
        }
        .status-pending { background-color: #6c757d; }
        .status-approved { background-color: #198754; }
        .status-rejected { background-color: #dc3545; }
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

    /* Custom Pagination Styles */
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
        <h2>${exportRequest.requestCode} <span class="status-tag status-${exportRequest.status}">${exportRequest.status}</span></h2>
        <p>Created at: ${exportRequest.requestDate}</p>

        <div class="card">
            <div class="card-header">Sender</div>
            <div class="card-body d-flex align-items-center">
                <div style="flex: 1;">
                    <p><strong>Full Name:</strong> ${sender.fullName}</p>
                    <p><strong>Email:</strong> ${sender.email}</p>
                    <p><strong>Phone Number:</strong> ${sender.phoneNumber}</p>
                    <p><strong>Address:</strong> Hanoi</p>
                </div>
                <div style="flex: 0 0 100px; margin-left: 20px;">
                    <img src="${senderImg}" alt="${sender.fullName}" class="img-fluid rounded-circle">
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Order Information</div>
            <div class="card-body">
                <p><strong>Delivery Date:</strong> ${exportRequest.deliveryDate}</p>
                <p>Request Reason:</p>
                <p>${exportRequest.reason != null ? exportRequest.reason : "N/A"}</p>
                <c:if test="${exportRequest.status == 'approved' && not empty exportRequest.approvalReason}">
                    <p><strong>Approval Reason:</strong> ${exportRequest.approvalReason}</p>
                    <p><strong>Approved At:</strong> <fmt:formatDate value="${exportRequest.approvedAt}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="Asia/Ho_Chi_Minh"/></p>
                </c:if>
                <c:if test="${exportRequest.status == 'rejected' && not empty exportRequest.rejectionReason}">
                    <p><strong>Rejection Reason:</strong> ${exportRequest.rejectionReason}</p>
                </c:if>
            </div>
        </div>

        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>Material List</span>
            </div>
            <div class="card-body">
                <div class="table-responsive" id="printTableArea">
                    <table class="table custom-table">
                        <thead>
                            <tr>
                                <th>Material Code</th>
                                <th>Material Name</th>
                                <th>Image</th>
                                <th>Quantity</th>
                                <th>Unit</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${details}">
                                <tr>
                                    <td>${detail.materialCode}</td>
                                    <td>${detail.materialName}</td>
                                    <td>
                                        <img src="${not empty detail.materialImageUrl ? detail.materialImageUrl : 'images/placeholder.png'}" 
                                             alt="${detail.materialName}" 
                                             style="width: 100px; height: auto; object-fit: cover;">
                                    </td>
                                    <td>${detail.quantity}</td>
                                    <td>${detail.materialUnit}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <c:if test="${not empty details}">
     
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ViewExportRequest?id=${exportRequest.exportRequestId}&page=${currentPage - 1}&status=${listStatus}&search=${listSearch}&searchRecipient=${listSearchRecipient}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/ViewExportRequest?id=${exportRequest.exportRequestId}&page=${i}&status=${listStatus}&search=${listSearch}&searchRecipient=${listSearchRecipient}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ViewExportRequest?id=${exportRequest.exportRequestId}&page=${currentPage + 1}&status=${listStatus}&search=${listSearch}&searchRecipient=${listSearchRecipient}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>

        <c:if test="${hasHandleRequestPermission && exportRequest.status == 'pending'}">
            <div class="d-flex gap-2 mb-2">
                <button type="button" class="btn" style="background-color: #198754; color: #fff; border: none;" onclick="updateStatus('approved')">Approve</button>
                <button type="button" class="btn" style="background-color: #dc3545; color: #fff; border: none;" onclick="updateStatus('rejected')">Reject</button>
                <a href="${pageContext.request.contextPath}/ExportRequestList?page=${listPage}&status=${listStatus}&search=${listSearch}&searchRecipient=${listSearchRecipient}" class="btn btn-warning">Cancel</a>
            </div>
        </c:if>

        <c:if test="${!(hasHandleRequestPermission && exportRequest.status == 'pending')}">
            <div class="mb-2">
                <a href="${pageContext.request.contextPath}/ExportRequestList?page=${listPage}&status=${listStatus}&search=${listSearch}&searchRecipient=${listSearchRecipient}" class="btn btn-warning">Cancel</a>
            </div>
        </c:if>
    </div>
    <!-- Status Update Modal -->
    <div class="modal fade" id="statusModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Update Export Request Status</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form id="statusForm" method="POST">
                    <div class="modal-body">
                        <input type="hidden" name="requestId" value="${exportRequest.exportRequestId}">
                        <!-- Chỉ hiện 1 ô nhập lý do, không còn select status -->
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
        function updateStatus(status) {
            if (status === 'approved') {
                document.getElementById('approvalReasonDiv').style.display = 'block';
                document.getElementById('rejectionReasonDiv').style.display = 'none';
                document.getElementById('statusForm').action = '${pageContext.request.contextPath}/ApproveExportRequest';
                document.getElementById('approvalReason').setAttribute('required', 'required');
                document.getElementById('rejectionReason').removeAttribute('required');
            } else if (status === 'rejected') {
                document.getElementById('approvalReasonDiv').style.display = 'none';
                document.getElementById('rejectionReasonDiv').style.display = 'block';
                document.getElementById('statusForm').action = '${pageContext.request.contextPath}/RejectExportRequest';
                document.getElementById('rejectionReason').setAttribute('required', 'required');
                document.getElementById('approvalReason').removeAttribute('required');
            }
            new bootstrap.Modal(document.getElementById('statusModal')).show();
        }
    </script>
</body>
</html>