<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Repair Request Management</title>
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
            .btn-filter:hover {
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
                display: inline-block;
                min-width: 90px;
                text-align: center;
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
                padding: 6px auto;
                font-weight: 300;
                width: 100px;
            }
            .error {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 10px;
            }
            .pagination {
                margin-top: 20px;
                justify-content: center;
            }
            .pagination .page-link {
                color: #DEAD6F;
                border: 1px solid #DEAD6F;
                margin: 0 4px;
                border-radius: 6px;
            }
            .pagination .page-link:hover {
                background-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.active .page-link {
                background-color: #DEAD6F;
                border-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.disabled .page-link {
                color: #6c757d;
                border-color: #dee2e6;
                background-color: #f8f9fa;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="SidebarDirector.jsp" />
                <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div class="container-main">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <h2 class="fw-bold display-6 border-bottom pb-2 m-0" style="color: #DEAD6F;">
                                <i class="fas fa-tools"></i> Repair Request Management
                            </h2>
                            <a href="${pageContext.request.contextPath}/repairrequest" class="btn btn-add ms-auto">
                                <i class="fas fa-plus"></i> Add Repair Request
                            </a>
                        </div>
                        <form method="get" action="repairrequestlist" class="filter-bar align-items-center" style="gap: 8px; flex-wrap: nowrap;">
                            <input type="text" name="search" class="form-control" placeholder="Search Reason"
                                   value="${searchKeyword != null ? searchKeyword : ''}" style="width: 230px;">
                            <select name="status" class="form-select" style="max-width: 150px;" onchange="this.form.submit()">
                                <option value="all" ${selectedStatus == null || selectedStatus == 'all' ? 'selected' : ''}>All Status</option>
                                <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>Pending</option>
                                <option value="approved" ${selectedStatus == 'approved' ? 'selected' : ''}>Approved</option>
                                <option value="rejected" ${selectedStatus == 'rejected' ? 'selected' : ''}>Rejected</option>
                            </select>
                            <select name="sortByName" class="form-select" style="max-width: 150px;" onchange="this.form.submit()">
                                <option value="newest" ${sortByName == 'newest' || sortByName == null || sortByName == '' ? 'selected' : ''}>Newest First</option>
                                <option value="oldest" ${sortByName == 'oldest' ? 'selected' : ''}>Oldest First</option>
                            </select>
                            <input type="hidden" name="page" value="${currentPage != null ? currentPage : 1}">
                            <input type="date" name="requestDateFrom" class="form-control" placeholder="From Date"
                                   value="${requestDateFrom != null ? requestDateFrom : ''}" style="width: 230px;">
                            <input type="date" name="requestDateTo" class="form-control" placeholder="To Date"
                                   value="${requestDateTo != null ? requestDateTo : ''}" style="width: 230px;">
                            <button type="submit" class="btn btn-filter" style="background-color: #DEAD6F; border-color: #DEAD6F; color:white;">Filter</button>
                            <a href="${pageContext.request.contextPath}/repairrequestlist" class="btn btn-secondary" style="width: 75px; height: 50px; display: flex; justify-content: center; align-items: center">Clear</a>
                        </form>
                        <c:if test="${not empty error}">
                            <p class="error">${error}</p>
                        </c:if>
                        <div class="table-responsive">
                            <table class="table custom-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Request Code</th>
                                        <th>Full Name</th>
                                        <th>Request Date</th>
                                        <th>Status</th>
                                        <th>Reason</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${repairRequests}">
                                        <c:if test="${r.status ne 'cancel'}">
                                            <tr>
                                                <td>${r.repairRequestId}</td>
                                                <td>${r.requestCode}</td>
                                                <td>${r.fullName != null ? r.fullName : 'Unknown'}</td>
                                                <td>${r.requestDate}</td>
                                                <td>
                                                    <span class="status-badge
                                                          <c:choose>
                                                              <c:when test="${r.status == 'pending'}">status-pending</c:when>
                                                              <c:when test="${r.status == 'approved'}">status-approved</c:when>
                                                              <c:when test="${r.status == 'rejected'}">status-rejected</c:when>
                                                          </c:choose>">
                                                        ${r.status}
                                                    </span>
                                                </td>
                                                <td>${r.reason}</td>
                                                <td>
                                                    <form action="repairrequestdetailbyID" method="get">
                                                        <input type="hidden" name="requestId" value="${r.repairRequestId}" />
                                                        <button type="submit" class="btn-detail">
                                                            <i class="fas fa-eye"></i> Detail
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${totalPages > 0}">
                            <nav aria-label="Page navigation">
                                <ul class="pagination">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="repairrequestlist?page=${currentPage - 1}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}" aria-label="Previous">
                                            <span aria-hidden="true">« Previous</span>
                                        </a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="repairrequestlist?page=${i}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="repairrequestlist?page=${currentPage + 1}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}" aria-label="Next">
                                            <span aria-hidden="true">Next »</span>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </div>
                </main>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>