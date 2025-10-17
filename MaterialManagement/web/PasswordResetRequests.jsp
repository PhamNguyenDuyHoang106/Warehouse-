<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
    <meta charset="UTF-8">
    <title>Password Reset Requests</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
         body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .search-box {
            margin-bottom: 20px;
        }
        .table-responsive {
            margin: 20px 0;
        }
        .pagination {
            justify-content: center;
            margin-top: 20px;
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
        .btn-action {
            width: 50px;
            height: 32px;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 2px;
        }
        .status-badge {
            padding: 4px 16px;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            letter-spacing: 0.5px;
            display: inline-block;
        }
        .status-pending {
            background-color: #6c757d;
            color: #fff;
        }
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        .status-rejected {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
    </head>
    <body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>
            <div class="col-md-9 col-lg-10 content px-md-4">
                <c:if test="${sessionScope.user.roleId != 1}">
                    <div class="alert alert-danger mt-4">You do not have permission to view password reset requests.</div>
                </c:if>
                <c:if test="${sessionScope.user.roleId == 1}">
                    <c:if test="${not empty message}">
                        <div id="reset-msg" class="alert alert-success alert-dismissible fade show text-center my-3" role="alert">
                            ${message}
                            <c:if test="${not empty newPassword}">
                                <br/>
                                <strong>New Password: </strong>
                                <span style="font-family:monospace; font-size:1.1em;">${newPassword}</span>
                            </c:if>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <script>
                            setTimeout(function() {
                                var msg = document.getElementById('reset-msg');
                                if (msg) {
                                    var alert = bootstrap.Alert.getOrCreateInstance(msg);
                                    alert.close();
                                }
                            }, 5000);
                        </script>
                    </c:if>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="fas fa-lock"></i> Password Reset Requests</h2>
                    </div>
                    <div class="row search-box">
                        <div class="col-md-8">
                            <form method="get" action="PasswordResetRequests" class="d-flex gap-2">
                                <input type="text" name="searchEmail" class="form-control" placeholder="Search by email" value="${param.searchEmail != null ? param.searchEmail : ''}" style="width: 200px; height: 50px; border: 2px solid gray" />
                                <select name="status" class="form-select" style="width: 150px; height: 50px; border: 2px solid #000">
                                    <option value="all" ${status == 'all' ? 'selected' : ''}>All Statuses</option>
                                    <option value="pending" ${status == 'pending' ? 'selected' : ''}>Pending</option>
                                    <option value="completed" ${status == 'completed' ? 'selected' : ''}>Completed</option>
                                    <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                </select>
                                <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 100px; height: 50px;">
                                    <i class="fas fa-search me-2"></i> Search
                                </button>
                                <a href="PasswordResetRequests" class="btn btn-secondary d-flex align-items-center justify-content-center " style="width: 100px; height: 50px">Clear</a>
                            </form>
                        </div>
                    </div>
                    <c:if test="${not empty requests}">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover align-middle text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th style="width: 60px;">ID</th>
                                        <th style="width: 240px;">User Email</th>
                                        <th style="width: 160px;">Request Date</th>
                                        <th style="width: 120px;">Status</th>
                                        <!-- <th>New Password</th> -->
                                        <th style="width: 160px;">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${requests}">
                                        <tr>
                                            <td style="width: 60px;">${req.requestId}</td>
                                            <td style="width: 240px; word-break: break-all;">${req.userEmail}</td>
                                            <td style="width: 160px;">${req.requestDate}</td>
                                            <td style="width: 120px;"> 
                                                <c:choose>
                                                    <c:when test="${req.status == 'pending'}">
                                                        <span class="status-badge status-pending">Pending</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'processing'}">
                                                        <span class="status-badge status-processing">Processing</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'completed'}">
                                                        <span class="status-badge status-completed">Completed</span>
                                                    </c:when>
                                                    <c:when test="${req.status == 'rejected'}">
                                                        <span class="status-badge status-rejected">Rejected</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${req.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <!-- <td>
                                                <input type="text" name="newPassword" class="form-control form-control-sm w-auto" placeholder="New Password" value="${req.newPassword}" style="min-width:120px;" readonly />
                                            </td> -->
                                            <td style="width: 160px; text-align: center; vertical-align: middle;">
                                                <form method="post" action="PasswordResetRequests" class="d-flex flex-column align-items-center justify-content-center gap-2 mb-0" style="min-width: 120px;">
                                                    <input type="hidden" name="requestId" value="${req.requestId}" />
                                                    <input type="hidden" name="newPassword" value="${req.newPassword}" />
                                                    <input type="hidden" name="statusFilter" value="${param.status}" />
                                                    <input type="hidden" name="searchEmail" value="${param.searchEmail}" />
                                                    <input type="hidden" name="page" value="${currentPage}" />
                                                    <select name="status" class="form-select form-select-sm w-auto d-inline-block mx-auto" style="min-width:80px; border: 2px solid #000; text-align: center;"
                                                        <c:if test="${req.status != 'pending'}">disabled</c:if>>
                                                        <option value="completed" ${req.status == 'completed' ? 'selected' : ''}>Completed</option>
                                                        <option value="rejected" ${req.status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-warning btn-action mx-auto" style="width: 100px;"
                                                        <c:if test="${req.status != 'pending'}">disabled</c:if>>
                                                        <i class="fas fa-save"></i> Update
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <nav class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="PasswordResetRequests?page=${currentPage - 1}&searchEmail=${param.searchEmail}&status=${param.status}">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="PasswordResetRequests?page=${i}&searchEmail=${param.searchEmail}&status=${param.status}">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="PasswordResetRequests?page=${currentPage + 1}&searchEmail=${param.searchEmail}&status=${param.status}">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                    <c:if test="${empty requests}">
                        <div class="text-center py-5">
                            <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                            <h4 class="text-muted">No Password Reset Requests Found</h4>
                            <p class="text-muted">No password reset requests at this time.</p>
                        </div>
                    </c:if>
                </c:if>
            </div>
        </div>
    </div>
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
    </body>
</html>
