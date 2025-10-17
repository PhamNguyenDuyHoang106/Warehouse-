<%-- 
    Document   : PurchaseRequestList
    Created on : Jun 10, 2025, 7:21:05 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<!--    abc-->
    <head>
        <meta charset="UTF-8">
        <title>Purchase Request Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <style>
            body {
                font-family: 'Segoe UI', Arial, sans-serif;
                background-color: #faf4ee;
            }
            .container-main {
                max-width: 1200px;
                margin: 30px auto;
                background: #fff;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
            }
            h2 {
                font-size: 1.75rem;
                font-weight: bold;
                margin-bottom: 0.5rem;
                color: #DEAD6F;
            }
            .filter-bar {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                margin: 20px 0;
                align-items: center;
            }
            .filter-bar .form-control,
            .filter-bar .form-select,
            .filter-bar .btn {
                height: 48px;
                min-width: 120px;
            }
            .btn-export, .btn-print {
                background-color: #DEAD6F;
                color: #fff;
                border: none;
                padding: 6px 14px;
                border-radius: 6px;
                font-weight: 500;
                height:50px;
                transition: background 0.2s;
            }
            .btn-export:hover, .btn-print:hover {
                background-color: #cfa856;
                color: #fff;
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
            .status-badge {
                padding: 2px 10px;
                border-radius: 10px;
                font-size: 13px;
                font-weight: 500;
            }
            .status-pending {
                background-color: #6c757d;
                color: #fff;
            }
            .status-approved {
                background-color: #d4edda;
                color: #155724;
            }
            .status-rejected {
                background-color: #f8d7da;
                color: #721c24;
            }
            .btn-detail {
                background-color: #fff7e6;
                color: #b8860b;
                border: 1px solid #ffe58f;
                border-radius: 6px;
                padding: 6px 14px;
                font-weight: 500;
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
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp"/>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="SidebarDirector.jsp"/>
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="container-main">
                        <c:set var="roleId" value="${sessionScope.user.roleId}" />
                        <c:set var="hasViewPurchaseRequestListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_PURCHASE_REQUEST_LIST')}" scope="request" />
                        <c:set var="hasCreatePurchaseRequestPermission" value="${rolePermissionDAO.hasPermission(roleId, 'CREATE_PURCHASE_REQUEST')}" scope="request" />
                        <c:set var="hasDeletePurchaseRequestPermission" value="${rolePermissionDAO.hasPermission(roleId, 'DELETE_PURCHASE_REQUEST')}" scope="request" />

                        <c:if test="${empty sessionScope.user}">
                            <div class="alert alert-danger">Please log in to view purchase requests.</div>
                            <div class="text-center mt-3">
                                <a href="Login.jsp" class="btn btn-outline-secondary btn-lg rounded-1">Log In</a>
                            </div>
                        </c:if>
                        <c:if test="${not empty sessionScope.user}">
                            <c:if test="${!hasViewPurchaseRequestListPermission}">
                                <div class="alert alert-danger">You do not have permission to view purchase requests.</div>
                                <div class="text-center mt-3">
                                    <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Dashboard</a>
                                </div>
                            </c:if>
                            <c:if test="${hasViewPurchaseRequestListPermission}">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <h2 class="fw-bold display-6 border-bottom pb-2 m-0" style="color: #DEAD6F;"><i class="fas fa-file-invoice-dollar"></i> Purchase Request Management</h2>
                                    <div class="d-flex gap-2">
                                        <c:if test="${hasCreatePurchaseRequestPermission}">
                                            <a href="CreatePurchaseRequest" class="btn btn-export">Add Purchase Request</a>
                                        </c:if>
                                    </div>
                                </div>
                                <form class="filter-bar align-items-center" method="GET" action="ListPurchaseRequests" style="gap: 8px; flex-wrap:nowrap;">
                                    <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Search by request code" style="width:200px;">
                                    <select class="form-select" name="status" style="max-width:120px;">
                                        <option value="">All Statuses</option>
                                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                        <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                                    </select>
                                    <select class="form-select" name="sort" style="max-width:150px;">
                                        <option value="" ${sortOption == null || sortOption == '' ? 'selected' : ''}>Newest First</option>
                                        <option value="date_asc" ${sortOption == 'date_asc' ? 'selected' : ''}>Oldest First</option>
                                    </select>
                                    <input type="date" class="form-control" name="startDate" value="${startDate}" placeholder="Start Date" style="width:140px;">
                                    <input type="date" class="form-control" name="endDate" value="${endDate}" placeholder="End Date" style="width:140px;">
                                    <button type="submit" class="btn" style="background-color: #DEAD6F; border-color: #DEAD6F; color:white;">Filter</button>
                                    <a href="${pageContext.request.contextPath}/ListPurchaseRequests" class="btn btn-secondary" style="width: 75px; height: 50px;">Clear</a>
                                </form>
                                <c:if test="${not empty purchaseRequests}">
                                    <div class="table-responsive" id="printTableListArea">
                                        <table class="table custom-table">
                                            <thead>
                                                <tr>
                                                    <th>Request Code</th>
                                                    <th>Requester</th>
                                                    <th>Request Date</th>
                                                    <th>Reason</th>
                                                    <th>Status</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach items="${purchaseRequests}" var="request">
                                                    <tr>
                                                        <td>${request.requestCode}</td>
                                                        <td>${userIdToName[request.userId]}</td>
                                                        <td>${request.requestDate}</td>
                                                        <td>${request.reason}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${fn:toLowerCase(request.status) == 'approved'}">
                                                                    <span class="status-badge status-approved">Approved</span>
                                                                </c:when>
                                                                <c:when test="${fn:toLowerCase(request.status) == 'rejected'}">
                                                                    <span class="status-badge status-rejected">Rejected</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-badge status-pending">Pending</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:if test="${hasViewPurchaseRequestListPermission}">
                                                                <a href="PurchaseRequestDetail?id=${request.purchaseRequestId}" class="btn-detail">
                                                                    <i class="fas fa-eye"></i> Detail
                                                                </a>
                                                            </c:if>
                                                            <c:if test="${request.status == 'PENDING' && request.userId == sessionScope.user.userId && hasDeletePurchaseRequestPermission}">
                                                                <button onclick="deleteRequest(${request.purchaseRequestId})" class="btn btn-danger btn-sm">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <nav class="mt-3">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="ListPurchaseRequests?page=${currentPage - 1}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                            </li>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="ListPurchaseRequests?page=${i}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="ListPurchaseRequests?page=${currentPage + 1}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                            </li>
                                        </ul>
                                    </nav>
                                </c:if>
                                <c:if test="${empty purchaseRequests}">
                                    <div class="alert alert-info mt-4">
                                        No purchase request found.
                                    </div>
                                </c:if>
                            </c:if>
                        </c:if>
                    </div>
                </main>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function deleteRequest(id) {
                if (confirm('Are you sure you want to delete this request?')) {
                    window.location.href = 'DeletePurchaseRequest?id=' + id;
                }
            }
        </script>
    </body>
</html>