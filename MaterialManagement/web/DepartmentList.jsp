<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Department Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <style>
            body {
                background-color: #f8f9fa;
                padding: 20px;
            }
            .table-responsive {
                margin: 20px 0;
            }
            .content {
                padding-left: 20px;
                font-family: 'Roboto', sans-serif;
            }
            .dashboard-title {
                color: #e2b77a;
                font-weight: bold;
                font-size: 2.2rem;
                margin-bottom: 24px;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .pagination .page-link {
                color: #e2b77a;
                border: 1px solid #e2b77a;
                margin: 0 4px;
                border-radius: 6px;
            }
            .pagination .page-link:hover {
                background-color: #e2b77a;
                color: #fff;
            }
            .pagination .page-item.active .page-link {
                background-color: #e2b77a;
                border-color: #e2b77a;
                color: #fff;
            }
            .pagination .page-item.disabled .page-link {
                color: #6c757d;
                border-color: #dee2e6;
                background-color: #f8f9fa;
            }
            .filter-bar .form-control,
            .filter-bar .form-select {
                height: 60px;
                min-width: 150px;
                border: 2px solid gray;
                border-radius: 6px;
                font-size: 1.1rem;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>
                <!-- Page Content -->
                <div class="col-md-9 col-lg-10 content px-md-4">
                    <c:if test="${empty sessionScope.user}">
                        <div class="alert alert-danger" role="alert">You do not have permission to view the department list.</div>
                    </c:if>
                    <c:if test="${not empty sessionScope.user and sessionScope.user.roleId != 1 and !rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_LIST_DEPARTMENT')}">
                        <div class="alert alert-danger" role="alert">You do not have permission to view the department list.</div>
                    </c:if>
                    <c:if test="${not empty sessionScope.user and (sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'VIEW_LIST_DEPARTMENT'))}">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="dashboard-title mb-0">Department Management</h2>
                            <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'CREATE_DEPARTMENT')}">
                                <a href="adddepartment" class="btn flex-shrink-0" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 260px; font-size: 1.25rem; font-weight: 500; border-radius: 6px; padding: 0 32px; display: inline-flex; align-items: center; justify-content: center;">
                                    <i class="fas fa-plus me-2"></i> Add Department
                                </a>
                            </c:if>
                        </div>
                        <div class="d-flex align-items-center gap-3 mb-4 filter-bar" style="flex-wrap: wrap;">
                            <form class="d-flex align-items-center gap-3 flex-shrink-0" action="depairmentlist" method="get" style="margin-bottom:0;">
                                <input type="hidden" name="page" value="${currentPage != null ? currentPage : 1}">
                                <input type="hidden" name="pageSize" value="${pageSize != null ? pageSize : 10}">
                                <input class="form-control" type="search" name="search" placeholder="Search by Department Code" value="${searchKeyword != null ? searchKeyword : ''}" aria-label="Search" style="min-width: 260px; max-width: 320px;">
                                <select name="sortByName" class="form-select">
                                    <option value="" ${sortByName == null || sortByName == '' ? 'selected' : ''}>Sort by Name</option>
                                    <option value="asc" ${sortByName == 'asc' ? 'selected' : ''}>A-Z</option>
                                    <option value="desc" ${sortByName == 'desc' ? 'selected' : ''}>Z-A</option>
                                </select>
                                <select name="statusFilter" class="form-select">
                                    <option value="" ${statusFilter == null || statusFilter == '' ? 'selected' : ''}>All Statuses</option>
                                    <option value="Active" ${statusFilter == 'Active' ? 'selected' : ''}>Active</option>
                                    <option value="Inactive" ${statusFilter == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                    <option value="Deleted" ${statusFilter == 'Deleted' ? 'selected' : ''}>Deleted</option>
                                </select>
                                <button class="btn" type="submit" style="background-color: #e2b77a; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px;">
                                    <i class="fas fa-search me-2"></i> Search
                                </button>
                                <a href="depairmentlist" class="btn" style="background-color: #6c757d; color: #fff; height: 60px; min-width: 150px; font-size: 1.1rem; font-weight: 500; border-radius: 6px; display: flex; justify-content: center; align-items: center">Clear</a>
                            </form>
                        </div>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>
                        <c:if test="${not empty message}">
                            <div class="alert alert-success" role="alert">${message}</div>
                        </c:if>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover align-middle text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Code</th>
                                        <th>Phone</th>
                                        <th>Email</th>
                                        <th>Location</th>
                                        <th>Status</th>
                                        <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                            <th>Actions</th>
                                        </c:if>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty departments}">
                                            <c:forEach var="d" items="${departments}">
                                                <tr>
                                                    <td>${d.departmentId}</td>
                                                    <td>${d.departmentName}</td>
                                                    <td>${d.departmentCode}</td>
                                                    <td>${d.phoneNumber}</td>
                                                    <td>${d.email}</td>
                                                    <td>${d.location}</td>
                                                    <td>${d.status}</td>
                                                    <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                        <td>
                                                            <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT')}">
                                                                <a href="editdepartment?id=${d.departmentId}" class="btn btn-warning btn-sm me-1" title="Edit" style="width: 45px"><i class="fas fa-edit"></i></a>
                                                            </c:if>
                                                            <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                                <form action="deletedepartment" method="post" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xóa phòng ban này không?');">
                                                                    <input type="hidden" name="id" value="${d.departmentId}" />
                                                                    <button type="submit" class="btn btn-danger btn-sm" title="Delete" style="width: 45px"><i class="fas fa-trash"></i></button>
                                                                </form>
                                                            </c:if>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="7" class="text-center text-muted">No department.</td>
                                                <c:if test="${sessionScope.user.roleId == 1 || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_DEPARTMENT') || rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_DEPARTMENT')}">
                                                    <td></td>
                                                </c:if>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${totalPages > 0}">
                            <nav aria-label="Page navigation">
                                <ul class="pagination">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="depairmentlist?page=${currentPage - 1}&pageSize=${pageSize}&search=${searchKeyword}&sortByName=${sortByName}&statusFilter=${statusFilter}" aria-label="Previous">
                                            <span aria-hidden="true">«</span>
                                        </a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="depairmentlist?page=${i}&pageSize=${pageSize}&search=${searchKeyword}&sortByName=${sortByName}&statusFilter=${statusFilter}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="depairmentlist?page=${currentPage + 1}&pageSize=${pageSize}&search=${searchKeyword}&sortByName=${sortByName}&statusFilter=${statusFilter}" aria-label="Next">
                                            <span aria-hidden="true">»</span>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
                <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>