<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>

<%
User user = (User) session.getAttribute("user");
if (user != null) {
    PermissionDAO permissionDAO = new PermissionDAO();
    List<String> permissionNames = permissionDAO.getPermissionsByRole(user.getRoleId())
        .stream()
        .map(permission -> permission.getPermissionName())
        .collect(Collectors.toList());
    session.setAttribute("userPermissions", permissionNames);
}
%>

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
          integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>

<header>
    <div class="container py-2">
        <div class="row align-items-center">
            <div class="col-12 col-sm-4 text-center text-sm-start mb-3 mb-sm-0">
                <a href="home">
                    <img src="images/AdminLogo.png" alt="logo" class="img-fluid" style="max-width: 180px;">
                </a>
            </div>
            <div class="col-12 col-sm-8 d-flex flex-column flex-sm-row justify-content-sm-end align-items-center gap-3">
                <div class="text-center text-sm-end">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <span class="fs-6 text-muted">${sessionScope.user.fullName}</span><br>
                            <strong>${sessionScope.user.email}</strong>
                        </c:when>
                        <c:otherwise>
                            <span class="fs-6 text-muted">Guest</span><br>
                            <strong>guest@example.com</strong>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="logout" class="btn btn-outline-dark btn-sm">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="Login.jsp" class="btn btn-outline-primary btn-sm">Login</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <hr class="my-2">
    </div>

    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-light main-menu d-flex">
            <a class="navbar-brand d-lg-none" href="#">Menu</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar"
                    aria-controls="offcanvasNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar"
                 aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header justify-content-center">
                    <button type="button" class="btn-close ms-auto" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body d-flex flex-column flex-lg-row align-items-lg-center justify-content-between">

                    <!-- System Management Dropdown -->
                    <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_USER') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT') 
                                  || sessionScope.userPermissions.contains('VIEW_UNIT') 
                                  || sessionScope.userPermissions.contains('VIEW_INVENTORY') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_UNIT')}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>System Management</option>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_INVENTORY')}">
                                  <option value="StaticInventory">Manage Inventory</option>
                              </c:if>                            
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_USER')}">
                                  <option value="UserList">Manage Users</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && sessionScope.user.roleId == 1}">
                                  <option value="PasswordResetRequests">Manager Password</option>
                              </c:if>                                
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT')}">
                                  <option value="depairmentlist">Manage Department</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_UNIT')}">
                                  <option value="Unit">Manage Unit</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_UNIT')}">
                                  <option value="UnitList">Manage Units</option>
                              </c:if>                                
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL')}">
                                  <option value="dashboardmaterial">Manage Materials</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY')}">
                                  <option value="Category">Manage Categories</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER')}">
                                  <option value="Supplier">Manage Suppliers</option>
                              </c:if>                                
                          </select>
                    </c:if>

                    <!-- Stock & History Dropdown -->
                    <c:if test="${sessionScope.userPermissions.contains('EXPORT_MATERIAL') || sessionScope.userPermissions.contains('IMPORT_MATERIAL') || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                        <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                            <option selected disabled>Stock</option>
                            <!-- Export / Import Stock -->
                            <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT')}">
                                <option value="ExportMaterial">Export Material</option>
                            </c:if>
                            <c:if test="${sessionScope.userPermissions.contains('VIEW_EXPORT_LIST')}">
                                <option value="ExportList">Export List</option>
                            </c:if>
                            <c:if test="${sessionScope.userPermissions.contains('CREATE_IMPORT')}">
                                <option value="ImportMaterial">Import Material</option>
                            </c:if>
                            <c:if test="${sessionScope.userPermissions.contains('VIEW_IMPORT_LIST')}">
                                <option value="ImportList">Import List</option>
                            </c:if>
                            <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST') && sessionScope.user.roleId == 3}">
                                <option value="CreateRepairRequest">Repair Request</option>
                            </c:if>
                        </select>
                    </c:if>

                    <!-- Request Dropdown -->
                    <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST') 
                                  || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST') 
                                  || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST') 
                                  || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>Request</option>
                              <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')}">
                                  <option value="CreateExportRequest">Export Request</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')}">
                                  <option value="CreatePurchaseRequest">Purchase Request</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                                  <option value="CreateRepairRequest">Repair Request</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                                  <option value="CreatePurchaseOrder">Purchase Order Request</option>
                              </c:if>
                          </select>
                    </c:if>

                    <!-- Request List Dropdown -->
                    <c:if test="${sessionScope.userPermissions.contains('VIEW_EXPORT_REQUEST_LIST') 
                                  || sessionScope.userPermissions.contains('VIEW_PURCHASE_REQUEST_LIST') 
                                  || sessionScope.userPermissions.contains('VIEW_REPAIR_REQUEST_LIST') 
                                  || sessionScope.userPermissions.contains('VIEW_PURCHASE_ORDER_LIST')}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>Request List</option>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_EXPORT_REQUEST_LIST')}">
                                  <option value="ExportRequestList">Export Request List</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_PURCHASE_REQUEST_LIST')}">
                                  <option value="ListPurchaseRequests">Purchase Request List</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_REPAIR_REQUEST_LIST')}">
                                  <option value="repairrequestlist">Repair Request List</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_PURCHASE_ORDER_LIST')}">
                                  <option value="PurchaseOrderList">Purchase Order List</option>
                              </c:if>
                          </select>
                    </c:if>

                    <ul class="navbar-nav d-flex flex-row flex-wrap gap-3 mb-3 mb-lg-0 menu-list list-unstyled">
                        <li class="nav-item">
                            <a href="home" class="nav-link active">Home</a>
                        </li>
                    </ul>

                    <div class="d-none d-lg-flex align-items-center gap-3 align-items-end">
                        <a href="profile" class="text-dark mx-2 mx-3">
                            <i class="fas fa-user fs-4"></i>
                        </a>
                        <a href="#" class="mx-3 d-lg-none" data-bs-toggle="offcanvas" data-bs-target="#offcanvasSearch"
                           aria-controls="offcanvasSearch">
                            <iconify-icon icon="tabler:search" class="fs-4"></iconify-icon>
                        </a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>

<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js"
        integrity="sha384-7+zCNj/IqJ95wo16oMtfsKbZ9ccEh31eOz1HGyDuCQ6wgnyJNSYdrPa03rtR1zdB"
crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js"
        integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13"
crossorigin="anonymous"></script>
<script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
