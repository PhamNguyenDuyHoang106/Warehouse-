<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Admin Dashboard - Computer Accessories</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
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
            .btn-info.btn-action {
                background-color: #DEAD6F;
                border-color: #DEAD6F;
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
        <%@ include file="Header.jsp" %>

        <!-- Main content -->
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>

                <!-- Page Content -->
                <div class="col-md-9 col-lg-10 px-md-4">
                    <c:set var="roleId" value="${sessionScope.user.roleId}" />
                    <c:set var="hasViewListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_LIST_USER')}" scope="request" />

                    <c:if test="${!hasViewListPermission}">
                        <div class="alert alert-danger">You do not have permission to view the user list.</div>
                    </c:if>
                    <c:if test="${hasViewListPermission}">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h2 class="text-primary fw-bold display-6 border-bottom pb-2">👤 User List</h2>
                            <c:if test="${rolePermissionDAO.hasPermission(roleId, 'CREATE_USER')}">
                                <button type="button" class="btn btn-primary" onclick="window.location.href = 'CreateUser'">Create User</button>
                            </c:if>
                        </div>

                        <!-- Filter Form -->
                        <div class="row search-box">
                            <div class="col-md-8">
                                <form action="UserList" method="GET" class="d-flex gap-2 align-items-center">
                                    <input type="text" name="username" class="form-control" 
                                           placeholder="Search by username" 
                                           value="${usernameFilter != null ? usernameFilter : ''}" 
                                           style="width: 180px; height: 50px; border: 2px solid gray" />
                                    <select name="status" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                        <option value="">All Status</option>
                                        <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Active</option>
                                        <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Inactive</option>
                                    </select>
                                    <select name="roleId" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                        <option value="">All Roles</option>
                                        <c:forEach var="role" items="${roleList}">
                                            <c:if test="${role.roleId != 1}">
                                                <option value="${role.roleId}" ${roleIdFilter == role.roleId ? 'selected' : ''}>${role.roleName}</option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                                    <select name="departmentId" class="form-select" style="width: 150px; height: 50px; border: 2px solid gray">
                                        <option value="">All Departments</option>
                                        <c:forEach var="dept" items="${departmentList}">
                                            <option value="${dept.departmentId}" ${departmentIdFilter == dept.departmentId ? 'selected' : ''}>${dept.departmentName}</option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                                        <i class="fas fa-search me-2"></i> Search
                                    </button>
                                    <a href="UserList" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px">Clear</a>
                                </form>
                            </div>
                        </div>

                        <!-- Messages -->
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success">${successMessage}</div>
                            <% session.removeAttribute("successMessage"); %>
                        </c:if>
                        <c:if test="${not empty message}">
                            <div class="alert alert-success">${message}</div>
                            <% session.removeAttribute("message"); %>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                            <% session.removeAttribute("error"); %>
                        </c:if>

                        <!-- User Table -->
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover align-middle text-center">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Full Name</th>
                                        <th>Email</th>
                                        <th>Department</th>
                                        <th>Role</th>
                                        <th>Status</th>
                                        <c:if test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_USER') || rolePermissionDAO.hasPermission(roleId, 'DELETE_USER') || rolePermissionDAO.hasPermission(roleId, 'VIEW_DETAIL_USER')}">
                                            <th>Action</th>
                                        </c:if>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="user" items="${userList}">
                                        <c:if test="${user.roleId != 1}">
                                            <tr>
                                                <td>${user.userId}</td>
                                                <td>${user.username}</td>
                                                <td>${user.fullName}</td>
                                                <td>${user.email}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${user.roleId == 2}">
                                                            No Department
                                                        </c:when>
                                                        <c:when test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_USER') && user.roleId != 2}">
                                                            <form method="post" action="UserList" style="display:inline;">
                                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                                <input type="hidden" name="action" value="updateDepartment"/>
                                                                <input type="hidden" name="usernameFilter" value="${usernameFilter}"/>
                                                                <input type="hidden" name="statusFilter" value="${statusFilter}"/>
                                                                <input type="hidden" name="roleIdFilter" value="${roleIdFilter}"/>
                                                                <input type="hidden" name="departmentIdFilter" value="${departmentIdFilter}"/>
                                                                <input type="hidden" name="page" value="${currentPage}"/>
                                                                <select name="departmentId" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                                    <option value="">No Department</option>
                                                                    <c:forEach var="dept" items="${departmentList}">
                                                                        <option value="${dept.departmentId}" ${user.departmentId == dept.departmentId ? 'selected' : ''}>${dept.departmentName}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${user.departmentId != null}">
                                                                    <c:forEach var="dept" items="${departmentList}">
                                                                        <c:if test="${dept.departmentId == user.departmentId}">
                                                                            ${dept.departmentName}
                                                                        </c:if>
                                                                    </c:forEach>
                                                                </c:when>
                                                                <c:otherwise>No Department</c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_USER')}">
                                                            <form method="post" action="UserList" style="display:inline;">
                                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                                <input type="hidden" name="action" value="updateRole"/>
                                                                <input type="hidden" name="usernameFilter" value="${usernameFilter}"/>
                                                                <input type="hidden" name="statusFilter" value="${statusFilter}"/>
                                                                <input type="hidden" name="roleIdFilter" value="${roleIdFilter}"/>
                                                                <input type="hidden" name="departmentIdFilter" value="${departmentIdFilter}"/>
                                                                <input type="hidden" name="page" value="${currentPage}"/>
                                                                <select name="roleId" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                                    <c:forEach var="role" items="${roleList}">
                                                                        <c:if test="${role.roleId != 1}">
                                                                            <option value="${role.roleId}" ${user.roleId == role.roleId ? 'selected' : ''}>${role.roleName}</option>
                                                                        </c:if>
                                                                    </c:forEach>
                                                                </select>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:forEach var="role" items="${roleList}">
                                                                <c:if test="${role.roleId == user.roleId}">
                                                                    ${role.roleName}
                                                                </c:if>
                                                            </c:forEach>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_USER')}">
                                                            <form method="post" action="UserList" style="display:inline;">
                                                                <input type="hidden" name="userId" value="${user.userId}"/>
                                                                <input type="hidden" name="action" value="updateStatus"/>
                                                                <input type="hidden" name="usernameFilter" value="${usernameFilter}"/>
                                                                <input type="hidden" name="statusFilter" value="${statusFilter}"/>
                                                                <input type="hidden" name="roleIdFilter" value="${roleIdFilter}"/>
                                                                <input type="hidden" name="departmentIdFilter" value="${departmentIdFilter}"/>
                                                                <input type="hidden" name="page" value="${currentPage}"/>
                                                                <select name="status" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                                    <option value="active" ${user.status == 'active' ? 'selected' : ''}>Active</option>
                                                                    <option value="inactive" ${user.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                                                </select>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${user.status == 'active' ? 'Active' : 'Inactive'}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <c:if test="${rolePermissionDAO.hasPermission(roleId, 'UPDATE_USER') || rolePermissionDAO.hasPermission(roleId, 'DELETE_USER') || rolePermissionDAO.hasPermission(roleId, 'VIEW_DETAIL_USER')}">
                                                    <td>
                                                        <div class="d-flex justify-content-center">
                                                            <c:if test="${rolePermissionDAO.hasPermission(roleId, 'VIEW_DETAIL_USER')}">
                                                                <a href="UserDetail?userId=${user.userId}" class="btn btn-info btn-action" title="View Details">
                                                                    <i class="fas fa-eye"></i>
                                                                </a>
                                                            </c:if>
                                                            <c:if test="${rolePermissionDAO.hasPermission(roleId, 'DELETE_USER')}">
                                                                <form method="post" action="UserList" style="display:inline;">
                                                                    <input type="hidden" name="userId" value="${user.userId}"/>
                                                                    <input type="hidden" name="action" value="delete"/>
                                                                    <input type="hidden" name="usernameFilter" value="${usernameFilter}"/>
                                                                    <input type="hidden" name="statusFilter" value="${statusFilter}"/>
                                                                    <input type="hidden" name="roleIdFilter" value="${roleIdFilter}"/>
                                                                    <input type="hidden" name="departmentIdFilter" value="${departmentIdFilter}"/>
                                                                    <input type="hidden" name="page" value="${currentPage}"/>
                                                                    <button type="submit" class="btn btn-danger btn-action" title="Delete User" onclick="return confirm('Are you sure you want to delete this user?');">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <nav class="mt-3">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="UserList?page=${currentPage - 1}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">Previous</a>
                                </li>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="UserList?page=${i}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">${i}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="UserList?page=${currentPage + 1}<c:if test='${not empty usernameFilter}'>&username=${usernameFilter}</c:if><c:if test='${not empty statusFilter}'>&status=${statusFilter}</c:if><c:if test='${roleIdFilter != null}'>&roleId=${roleIdFilter}</c:if><c:if test='${departmentIdFilter != null}'>&departmentId=${departmentIdFilter}</c:if>">Next</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>

        <jsp:include page="Footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
    </body>
</html>