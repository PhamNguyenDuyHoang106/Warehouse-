<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="entity.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Sup  plier Dashboard - Computer Accessories</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
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
        .search-box {
            margin-bottom: 20px;
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
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .custom-search {
            max-width: 400px;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

    <!-- Main content -->
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>

            <div class="col-md-9 col-lg-10 content px-md-4">
                <c:set var="roleId" value="${sessionScope.user.roleId}" />
                <c:set var="hasViewListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_LIST_SUPPLIER')}" scope="request" />

                <c:if test="${!hasViewListPermission}">
                    <div class="alert alert-danger">You do not have permission to view the supplier list.</div>
                </c:if>
                <c:if test="${hasViewListPermission}">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h2 class="text-primary fw-bold display-6 border-bottom pb-2"><i class="bi bi-person-fill-up"></i> Supplier List</h2>
                        <c:if test="${rolePermissionDAO.hasPermission(roleId, 'CREATE_SUPPLIER')}">
                            <a href="Supplier?action=edit" class="btn btn-primary">
                                <i class="fas fa-plus me-1"></i> Add New Supplier
                            </a>
                        </c:if>
                    </div>

                    <div class="row search-box">
                        <div class="col-md-8">
                            <form action="Supplier" method="GET" class="d-flex gap-2">
                                <input type="hidden" name="action" value="list" />
                                <input type="text" name="keyword" class="form-control" 
                                       placeholder="Search by name" 
                                       value="${keyword != null ? keyword : ''}" 
                                       style="width: 200px; height: 50px; border: 2px solid gray" />
                                <select name="sortBy" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                    <option value="">Sort By</option>
                                    <option value="name_asc" ${sortBy == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                                    <option value="name_desc" ${sortBy == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
                                </select>
                                <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                    <i class="fas fa-search me-2"></i> Search
                                </button>
                                <a href="Supplier?action=list" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px">Clear</a>
                            </form>
                        </div>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover align-middle text-center">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col" style="width: 90px;">Code</th>
                                    <th scope="col" style="width: 150px;">Name</th>
                                    <th scope="col" style="width: 150px;">Contact</th>
                                    <th scope="col" style="width: 150px;">Address</th>
                                    <th scope="col" style="width: 150px;">Phone</th>
                                    <th scope="col" style="width: 200px;">Email</th>
                                    <th scope="col" style="width: 150px;">Description</th>
                                    <th scope="col">Tax ID</th>
                                    <c:if test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_SUPPLIER') || rolePermissionDAO.hasPermission(roleId, 'DELETE_SUPPLIER')}">
                                        <th scope="col">Actions</th>
                                    </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="s" items="${supplierList}">
                                    <tr>
                                        <td>${s.supplierId}</td>
                                        <td>${s.supplierCode}</td>
                                        <td>${s.supplierName}</td>
                                        <td>${s.contactInfo}</td>
                                        <td>${s.address}</td>
                                        <td>
                                            <a href="tel:${s.phoneNumber}" class="text-decoration-none">
                                                ${s.phoneNumber}
                                            </a>
                                        </td>
                                        <td>
                                            <a href="mailto:${s.email}" class="text-decoration-none">
                                                ${s.email}
                                            </a>
                                        </td>
                                        <td>${s.description}</td>
                                        <td>${s.taxId}</td>
                                        <c:if test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_SUPPLIER') || rolePermissionDAO.hasPermission(roleId, 'DELETE_SUPPLIER')}">
                                            <td>
                                                <div class="d-flex justify-content-center">
                                                    <c:if test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_SUPPLIER')}">
                                                        <a href="Supplier?action=edit&id=${s.supplierId}" class="btn btn-warning btn-action" title="Edit">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                    </c:if>
                                                    <c:if test="${rolePermissionDAO.hasPermission(roleId, 'DELETE_SUPPLIER')}">
                                                        <a href="Supplier?action=delete&id=${s.supplierId}" class="btn btn-danger btn-action" onclick="return confirm('Are you sure you want to delete this supplier?');" title="Delete">
                                                            <i class="fas fa-trash"></i>
                                                        </a>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty supplierList}">
                                    <tr>
                                        <td colspan="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_SUPPLIER') || rolePermissionDAO.hasPermission(roleId, 'DELETE_SUPPLIER') ? 10 : 9}" class="text-center text-muted">No suppliers found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="Supplier?action=list&page=${currentPage - 1}&keyword=${keyword}&sortBy=${sortBy}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="Supplier?action=list&page=${i}&keyword=${keyword}&sortBy=${sortBy}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="Supplier?action=list&page=${currentPage + 1}&keyword=${keyword}&sortBy=${sortBy}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div> 
        </div> 
    </div> 

    <!-- Footer -->
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
</body>
</html>