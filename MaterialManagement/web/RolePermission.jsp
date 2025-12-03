<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Role Permission Management - Computer Accessories</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .table-responsive {
            margin: 20px 0;
        }
        .search-box {
            margin-bottom: 15px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .custom-search {
            max-width: 250px;
        }
        .custom-search .form-control {
            font-size: 0.9rem;
            padding: 6px 10px;
        }
        .custom-search .btn {
            padding: 6px 12px;
            font-size: 0.9rem;
        }
        .tabcontent {
            display: none;
        }
        .tabcontent.active {
            display: block;
        }
        .table th, .table td {
            vertical-align: middle;
        }
        #sidebarMenu {
            background: #f8f9fa;
            border-right: 1px solid #dee2e6;
            padding: 10px 0;
        }
        #sidebarMenu .nav-link {
            padding: 12px 20px;
            border-radius: 8px;
            margin: 0 10px;
            color: #333;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        #sidebarMenu .nav-link:hover {
            background-color: #8B4513;
            color: #ffffff !important;
            transform: scale(1.02);
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        #sidebarMenu .nav-link.active {
            background-color: #8B4513;
            color: #ffffff !important;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        #sidebarMenu .nav-link i {
            transition: color 0.3s ease;
        }
        #sidebarMenu .nav-link:hover i,
        #sidebarMenu .nav-link.active i {
            color: #ffffff;
        }
        .back-button-container {
            position: absolute;
            top: 20px;
            right: 20px;
        }
        .back-button-container .btn-back {
            background-color: #Dead6F;
            border-color: #Dead6F;
            color: #ffffff;
        }
        .back-button-container .btn-back:hover {
            background-color: #c7a65f;
            border-color: #c7a65f;
        }
        input[type="checkbox"] {
            -webkit-appearance: none;
            -moz-appearance: none;
            appearance: none;
            width: 20px;
            height: 20px;
            border: 2px solid #dee2e6;
            border-radius: 4px;
            background-color: #fff;
            position: relative;
            cursor: pointer;
        }
        input[type="checkbox"]:checked {
            background-color: #Dead6F;
            border-color: #Dead6F;
        }
        input[type="checkbox"]:checked::after {
            content: '\2713';
            color: #ffffff;
            font-size: 14px;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }
        .package-grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            gap: 20px;
            max-width: 1400px;
        }
        .package-module-link {
            text-decoration: none;
            display: block;
        }
        .package-module {
            background: #ffffff;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            overflow: hidden;
            transition: all 0.3s ease;
            cursor: pointer;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .package-module:hover {
            border-color: #0038d1;
            transform: translateY(-4px);
            box-shadow: 0 6px 16px rgba(0, 56, 209, 0.25);
        }
        .package-module-header {
            background: linear-gradient(135deg, #0038d1 0%, #0052ff 100%);
            padding: 20px;
            text-align: center;
            position: relative;
            min-height: 80px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .package-module-icon {
            font-size: 32px;
            color: #ffffff;
            transition: transform 0.3s ease;
        }
        .package-module:hover .package-module-icon {
            transform: scale(1.15);
        }
        .package-module-body {
            padding: 20px;
            text-align: center;
            flex: 1;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 60px;
        }
        .package-module-name {
            font-size: 1.05rem;
            font-weight: 600;
            color: #333;
            margin: 0;
            line-height: 1.4;
        }
        .package-module:hover .package-module-name {
            color: #0038d1;
        }
    </style>
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
            <div class="col-12 content px-md-4">
                <div class="back-button-container mb-3">
                    <a href="UserList" class="btn btn-secondary btn-lg rounded-1">
                        <i class="fas fa-arrow-left me-2"></i>Back to User List
                    </a>
                </div>
                <h2 class="text-primary fw-bold display-6 border-bottom pb-2 mb-4">🔐 Role Permission Management</h2>
                
                <!-- Search Form (only show when viewing permissions) -->
                <c:if test="${not empty selectedCategory}">
                    <form id="searchForm" action="RolePermission" method="get" class="search-box mb-4">
                        <div class="input-group custom-search">
                            <input type="text" name="search" class="form-control" placeholder="Search permissions..." value="${searchKeyword}">
                            <input type="hidden" name="category" value="${selectedCategory}">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </div>
                    </form>
                </c:if>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">${successMessage}</div>
                    <% session.removeAttribute("successMessage"); %>
                </c:if>

                <c:choose>
                    <%-- Show category list when no category selected --%>
                    <c:when test="${empty selectedCategory}">
                        <div class="package-grid-container">
                            <c:forEach var="module" items="${allModules}">
                                <a href="RolePermission?category=${module.moduleId}" class="package-module-link">
                                    <div class="package-module">
                                        <div class="package-module-header">
                                            <i class="fas fa-box package-module-icon"></i>
                                        </div>
                                        <div class="package-module-body">
                                            <span class="package-module-name">${module.moduleName}</span>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:when>
                    
                    <%-- Show permissions when category selected --%>
                    <c:otherwise>
                        <div class="mb-4">
                            <a href="RolePermission" class="btn btn-secondary mb-3">
                                <i class="fas fa-arrow-left me-2"></i>Back to Categories
                            </a>
                            <c:forEach var="module" items="${modules}">
                                <h3 class="text-primary">${module.moduleName}</h3>
                                <p class="text-muted">${module.description}</p>
                            </c:forEach>
                        </div>
                        
                        <form action="RolePermission" method="post" class="mt-3">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="search" value="${searchKeyword}">
                            <input type="hidden" name="category" value="${selectedCategory}">
                            <div class="table-responsive">
                                <c:forEach var="module" items="${modules}">
                                    <c:if test="${not empty permissionsByModule[module.moduleId]}">
                                        <table class="table table-bordered table-hover align-middle text-center">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Permission \ Role</th>
                                                    <c:forEach var="role" items="${roles}">
                                                        <th>${role.roleName}</th>
                                                    </c:forEach>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="perm" items="${permissionsByModule[module.moduleId]}">
                                                    <c:if test="${perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5 || rolePermissionMap[1][perm.permissionId]}">
                                                        <tr>
                                                            <td class="text-start">${perm.permissionName} (${perm.description})</td>
                                                            <c:forEach var="role" items="${roles}">
                                                                <td>
                                                                    <c:if test="${role.roleId == 1 || (perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5)}">
                                                                        <input type="checkbox" 
                                                                               name="permission_${role.roleId}_${perm.permissionId}" 
                                                                               ${rolePermissionMap[role.roleId][perm.permissionId] ? 'checked' : ''}>
                                                                    </c:if>
                                                                </td>
                                                            </c:forEach>
                                                        </tr>
                                                    </c:if>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:if>
                                </c:forEach>
                                
                                <c:if test="${empty modules || (modules.size() == 1 && permissionsByModule[modules[0].moduleId].isEmpty())}">
                                    <div class="alert alert-info">
                                        <i class="fas fa-info-circle me-2"></i>
                                        No permissions found in this category.
                                    </div>
                                </c:if>
                            </div>
                            <div class="d-grid gap-2 mb-3">
                                <button type="submit" class="btn btn-primary btn-lg rounded-1">
                                    <i class="fas fa-save me-2"></i>Save Changes
                                </button>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
</body>
</html>
