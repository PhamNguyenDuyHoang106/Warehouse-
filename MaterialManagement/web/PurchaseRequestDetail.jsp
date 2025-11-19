<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Purchase Request Details</title>
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
        border: none;
        color: #fff;
    }
    .btn-approve {
        background-color: #198754;
    }
    .btn-approve:hover {
        background-color: #17643a;
    }
    .btn-reject {
        background-color: #dc3545;
    }
    .btn-reject:hover {
        background-color: #b52d3a;
    }
    .btn-cancel {
        background-color: #DEAD6F;
        color: #fff;
        border: none;
    }
    .btn-cancel:hover {
        background-color: #c79b5a;
        color: #fff;
    }
    .alert {
        margin-top: 20px;
    }
    .status-tag {
        padding: 5px 10px;
        border-radius: 5px;
        color: #fff;
    }
    .status-pending { background-color: #6c757d; }
    .status-approved { background-color: #198754; }
    .status-rejected { background-color: #dc3545; }
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
        background-color: #DEAD6F;
        color: #fff;
        border: none;
        margin: 0 2px;
    }
    .pagination .page-link:hover,
    .pagination .page-item.active .page-link {
        background-color: #c79b5a;
        color: #fff;
        border: none;
    }
    .pagination .page-item.disabled .page-link {
        background-color: #f1e5d0;
        color: #b5a07a;
        border: none;
    }
    </style>
</head>
<body>
    <c:set var="statusLower" value="${fn:toLowerCase(purchaseRequest.status)}"/>
    <c:set var="statusClass"
           value="${statusLower == 'approved' ? 'status-approved' :
                   statusLower == 'rejected' ? 'status-rejected' : 'status-pending'}"/>
    <div class="container">
        <h2>${purchaseRequest.requestCode} <span class="status-tag ${statusClass}">${fn:toUpperCase(purchaseRequest.status)}</span></h2>
        <p>Request date: ${purchaseRequest.requestDate}</p>
        <p>Expected date: ${purchaseRequest.expectedDate != null ? purchaseRequest.expectedDate : 'N/A'}</p>

        <div class="card">
            <div class="card-header">Requester</div>
            <div class="card-body d-flex align-items-center">
                <div style="flex: 1;">
                    <p><strong>Full Name:</strong> ${requester.fullName}</p>
                    <p><strong>Email:</strong> ${requester.email}</p>
                    <p><strong>Phone Number:</strong> ${requester.phone}</p>
                    <p><strong>Department:</strong> ${requester.departmentName}</p>
                    <p><strong>Role:</strong> ${requester.roleName}</p>
                </div>
                <div style="flex: 0 0 100px; margin-left: 20px;">
                    <img src="images/profiles/${not empty requester.userPicture ? requester.userPicture : 'DefaultUser.jpg'}" alt="${requester.fullName}" class="img-fluid rounded-circle" style="width:100px;height:100px;object-fit:cover;">
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Request Information</div>
            <div class="card-body">
                <p>Request Reason:</p>
                <p>${purchaseRequest.reason != null ? purchaseRequest.reason : "N/A"}</p>
                <p><strong>Total Amount:</strong> ${purchaseRequest.totalAmount}</p>
                <c:if test="${purchaseRequest.approvedBy != null}">
                    <p><strong>Approved By:</strong> ${purchaseRequest.approvedBy}</p>
                    <p><strong>Approved At:</strong> ${purchaseRequest.approvedAt}</p>
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
                                <th>Material ID</th>
                                <th>Material Name</th>
                                <th>Image</th>
                                <th>Quantity</th>
                                <th>Unit</th>
                                <th>Note</th>
                                <th>Created</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${purchaseRequestDetailList}">
                                <tr>
                                    <td>${detail.materialId}</td>
                                    <td>${detail.materialName}</td>
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
                                        <br>
                                        <span style="font-size:12px; color:#888;">
                                            ${materialImages[detail.materialId]}
                                        </span>
                                    </td>
                                    <td>${detail.quantity}</td>
                                    <td>${detail.unitName != null ? detail.unitName : 'N/A'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty detail.note}">
                                                ${detail.note}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted fst-italic">No notes</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${detail.createdAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <!-- PHÂN TRANG -->
                <c:if test="${totalPages > 1}">
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${currentPage - 1}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${i}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="PurchaseRequestDetail?id=${purchaseRequest.purchaseRequestId}&page=${currentPage + 1}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <a href="ListPurchaseRequests" class="btn btn-cancel">Back to List</a>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
</body>
</html>