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
    </style>
</head>
<body>
    <%@ include file="Header.jsp" %>

    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
                <div class="position-sticky pt-4">
                    <ul class="nav flex-column menu-list list-unstyled">
                        <form id="searchForm" action="RolePermission" method="get" class="search-box px-3 pt-3">
                            <div class="input-group custom-search">
                                <input type="text" name="search" class="form-control" placeholder="Search permissions or modules..." value="${searchKeyword}">
                                <input type="hidden" id="selectedModule" name="selectedModule" value="${selectedModule}">
                                <button type="submit" class="btn btn-primary">Search</button>
                            </div>
                        </form>
                        <li class="nav-item mb-2">
                            <a href="RolePermission" class="nav-link text-uppercase secondary-font d-flex align-items-center ${selectedModule == '' ? 'active' : ''}" id="sidebar_all">
                                <i class="fas fa-list fs-4 me-3"></i>All Permissions
                            </a>
                        </li>
                        <c:forEach var="module" items="${modules}">
                            <li class="nav-item mb-2">
                                <a href="javascript:void(0)" class="nav-link text-uppercase secondary-font d-flex align-items-center ${selectedModule == module.moduleId ? 'active' : ''}" id="sidebar_${module.moduleId}" onclick="submitForm('${module.moduleId}')">
                                    <c:choose>
                                        <c:when test="${module.moduleName == 'User Management'}">
                                            <i class="fas fa-users fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Department Management'}">
                                            <i class="fas fa-building fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Unit Management'}">
                                            <i class="fas fa-cubes fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Inventory Management'}">
                                            <i class="fas fa-tachometer-alt fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Material Management'}">
                                            <i class="fas fa-shopping-cart fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Category Management'}">
                                            <i class="fas fa-list fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Supplier Management'}">
                                            <i class="fas fa-truck fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'System Management'}">
                                            <i class="fas fa-cog fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Request Management'}">
                                            <i class="fas fa-file-alt fs-4 me-3"></i>
                                        </c:when>
                                        <c:when test="${module.moduleName == 'Purchase Order Management'}">
                                            <i class="fas fa-shopping-bag fs-4 me-3"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-folder fs-4 me-3"></i>
                                        </c:otherwise>
                                    </c:choose>
                                    ${module.moduleName}
                                </a>
                            </li>
                        </c:forEach>
                        <c:if test="${not empty permissionsByModule[0]}">
                            <li class="nav-item mb-2">
                                <a href="javascript:void(0)" class="nav-link text-uppercase secondary-font d-flex align-items-center ${selectedModule == '0' ? 'active' : ''}" id="sidebar_0" onclick="submitForm('0')">
                                    <i class="fas fa-tools fs-4 me-3"></i>Other
                                </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>

            <div class="col-md-9 col-lg-10 px-md-4 content position-relative">
                <div class="back-button-container">
                    <a href="UserList" class="btn btn-back btn-lg rounded-1">Back to User List</a>
                </div>
                <h2 class="text-primary fw-bold display-6 border-bottom pb-2">🔐 Role Permission Management</h2>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">${successMessage}</div>
                    <% session.removeAttribute("successMessage"); %>
                </c:if>

                <form action="RolePermission" method="post" class="mt-3">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="search" value="${searchKeyword}">
                    <input type="hidden" name="selectedModule" value="${selectedModule}">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${selectedModule == ''}">
                                <div id="module_all" class="tabcontent">
                                    <c:forEach var="module" items="${modules}">
                                        <c:if test="${not empty permissionsByModule[module.moduleId]}">
                                            <h4 class="mt-4">${module.moduleName}</h4>
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
                                                                <td>${perm.permissionName} (${perm.description})</td>
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
                                    <c:if test="${not empty permissionsByModule[0]}">
                                        <h4 class="mt-4">Other</h4>
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
                                                <c:forEach var="perm" items="${permissionsByModule[0]}">
                                                    <c:if test="${perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5 || rolePermissionMap[1][perm.permissionId]}">
                                                        <tr>
                                                            <td>${perm.permissionName} (${perm.description})</td>
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
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="module" items="${modules}">
                                    <div id="module_${module.moduleId}" class="tabcontent">
                                        <c:choose>
                                            <c:when test="${not empty permissionsByModule[module.moduleId]}">
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
                                                                    <td>${perm.permissionName} (${perm.description})</td>
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
                                            </c:when>
                                            <c:otherwise>
                                                <p class="no-permissions text-muted">No permissions available for this module.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                                <c:if test="${not empty permissionsByModule[0]}">
                                    <div id="module_0" class="tabcontent">
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
                                                <c:forEach var="perm" items="${permissionsByModule[0]}">
                                                    <c:if test="${perm.permissionId != 3 && perm.permissionId != 4 && perm.permissionId != 5 || rolePermissionMap[1][perm.permissionId]}">
                                                        <tr>
                                                            <td>${perm.permissionName} (${perm.description})</td>
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
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="d-grid gap-2 mb-3">
                        <button type="submit" class="btn btn-primary btn-lg rounded-1">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <footer class="footer py-4 bg-light mt-auto">
        <div class="container text-center">
            <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
    <script>
        function openModule(moduleId) {
            let tabcontents = document.getElementsByClassName("tabcontent");
            for (let i = 0; i < tabcontents.length; i++) {
                tabcontents[i].style.display = "none";
            }
            let selectedTab = document.getElementById("module_" + moduleId);
            if (selectedTab) {
                selectedTab.style.display = "block";
            }

            let sidebarItems = document.getElementsByClassName("nav-link");
            for (let i = 0; i < sidebarItems.length; i++) {
                sidebarItems[i].classList.remove("active");
                sidebarItems[i].removeAttribute("aria-current");
            }
            let selectedItem = document.getElementById("sidebar_" + (moduleId === "" ? "all" : moduleId));
            if (selectedItem) {
                selectedItem.classList.add("active");
                selectedItem.setAttribute("aria-current", "page");
            }
        }

        function submitForm(moduleId) {
            document.getElementById("selectedModule").value = moduleId;
            document.getElementById("searchForm").submit();
        }

        window.onload = function() {
            let selectedModule = "${selectedModule}";
            if (selectedModule === "" && document.getElementById("module_all")) {
                openModule("all");
            } else if (selectedModule && document.getElementById("module_" + selectedModule)) {
                openModule(selectedModule);
            } else if (document.getElementById("sidebar_0")) {
                openModule("0");
            } else {
                let firstItem = document.getElementsByClassName("nav-link")[0];
                if (firstItem) {
                    let moduleId = firstItem.id.replace("sidebar_", "");
                    openModule(moduleId);
                }
            }
        };
    </script>
</body>
</html>
