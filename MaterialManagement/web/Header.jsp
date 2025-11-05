<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>

<%
User user = (User) session.getAttribute("user");
if (user != null) {
    // Only query permissions if not already in session to avoid repeated database queries
    List<String> userPermissions = (List<String>) session.getAttribute("userPermissions");
    if (userPermissions == null) {
        PermissionDAO permissionDAO = null;
        try {
            permissionDAO = new PermissionDAO();
            userPermissions = permissionDAO.getPermissionsByRole(user.getRoleId())
                .stream()
                .map(permission -> permission.getPermissionName())
                .collect(Collectors.toList());
            session.setAttribute("userPermissions", userPermissions);
        } catch (Exception e) {
            // Log error but don't break the page
            e.printStackTrace();
        } finally {
            if (permissionDAO != null) {
                try {
                    permissionDAO.close();
                } catch (Exception e) {
                    // Ignore close errors
                }
            }
        }
    }
}
%>

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
          integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        /* Đảm bảo header, body và footer đồng nhất khi zoom - tất cả đều full width */
        * {
            box-sizing: border-box;
        }
        
        html, body {
            width: 100%;
            margin: 0;
            padding: 0;
            overflow-x: hidden;
            max-width: 100vw;
        }
        
        header, footer {
            width: 100%;
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        
        /* Đảm bảo container-fluid cho header và footer giống body content */
        header .container-fluid, footer .container-fluid {
            width: 100%;
            padding-left: calc(var(--bs-gutter-x, 0.75rem) * 1);
            padding-right: calc(var(--bs-gutter-x, 0.75rem) * 1);
            margin-left: auto;
            margin-right: auto;
            box-sizing: border-box;
        }
        
        /* Đảm bảo header menu không bị overflow */
        header nav.main-menu {
            overflow-x: auto;
            overflow-y: hidden;
            -webkit-overflow-scrolling: touch;
            width: 100%;
        }
        
        /* Đảm bảo dropdown không bị overflow */
        header .filter-categories {
            max-width: 100%;
            white-space: nowrap;
            box-sizing: border-box;
        }
        
        /* Responsive cho mobile */
        @media (max-width: 991px) {
            header .offcanvas-body {
                overflow-x: hidden;
            }
            
            header .container-fluid, footer .container-fluid {
                padding-left: 15px;
                padding-right: 15px;
            }
        }
        
        /* Đảm bảo row trong header và footer có cùng gutter */
        header .row, footer .row {
            margin-left: calc(-0.5 * var(--bs-gutter-x, 0.75rem));
            margin-right: calc(-0.5 * var(--bs-gutter-x, 0.75rem));
        }
    </style>
</head>

<header>
    <div class="container-fluid py-2">
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

    <div class="container-fluid">
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

                    <!-- System Management Dropdown - Hệ thống, ai cũng có thể truy cập -->
                    <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 
                                  || sessionScope.userPermissions.contains('VIEW_INVENTORY')
                                  || sessionScope.userPermissions.contains('VIEW_LIST_USER') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_UNIT') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY') 
                                  || sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER')
                                  || sessionScope.userPermissions.contains('VIEW_LIST_RECIPIENT')
                                  || sessionScope.userPermissions.contains('VIEW_LIST_RACK')
                                  || sessionScope.userPermissions.contains('VIEW_LIST_VEHICLE'))}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>System Management</option>
                              <c:if test="${not empty sessionScope.user && sessionScope.user.roleId == 1}">
                                  <option value="RolePermission">Permission</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_INVENTORY'))}">
                                  <option value="InventoryReport">Inventory Report</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_LIST_RACK'))}">
                                  <option value="WarehouseRackList">Warehouse Racks</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_LIST_VEHICLE'))}">
                                  <option value="VehicleList">Vehicles</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_USER')}">
                                  <option value="UserList">Users</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && sessionScope.user.roleId == 1}">
                                  <option value="PasswordResetRequests">Password Reset Requests</option>
                              </c:if>                                
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_DEPARTMENT')}">
                                  <option value="depairmentlist">Department</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_UNIT')}">
                                  <option value="UnitList">Unit</option>
                              </c:if>                                
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_MATERIAL')}">
                                  <option value="dashboardmaterial">Material</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_CATEGORY')}">
                                  <option value="Category">Category</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_SUPPLIER')}">
                                  <option value="Supplier">Supplier</option>
                              </c:if>
                              <c:if test="${sessionScope.userPermissions.contains('VIEW_LIST_RECIPIENT')}">
                                  <option value="Recipient">Recipient</option>
                              </c:if>                                
                          </select>
                    </c:if>

                    <!-- Import/Export Operations Dropdown -->
                    <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_IMPORT') || sessionScope.userPermissions.contains('CREATE_EXPORT') || sessionScope.userPermissions.contains('VIEW_IMPORT_LIST') || sessionScope.userPermissions.contains('VIEW_EXPORT_LIST'))}">
                        <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                            <option selected disabled>Import/Export</option>
                            <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_IMPORT'))}">
                                <option value="ImportMaterial">Import Material</option>
                            </c:if>
                            <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_IMPORT_LIST'))}">
                                <option value="ImportList">Import List</option>
                            </c:if>
                            <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_EXPORT'))}">
                                <option value="ExportMaterial">Export Material</option>
                            </c:if>
                            <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_EXPORT_LIST'))}">
                                <option value="ExportList">Export List</option>
                            </c:if>
                        </select>
                    </c:if>

                    <!-- Employee Requests Dropdown - Các đơn yêu cầu cho nhân viên (không có Purchase Order) -->
                    <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 
                                  || sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')
                                  || sessionScope.userPermissions.contains('VIEW_EXPORT_REQUEST_LIST')
                                  || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')
                                  || sessionScope.userPermissions.contains('VIEW_PURCHASE_REQUEST_LIST')
                                  || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')
                                  || sessionScope.userPermissions.contains('VIEW_REPAIR_REQUEST_LIST'))}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>Employee Requests</option>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST'))}">
                                  <option value="CreateExportRequest">Create Export Request</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_EXPORT_REQUEST_LIST'))}">
                                  <option value="ExportRequestList">Export Requests</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST'))}">
                                  <option value="CreatePurchaseRequest">Create Purchase Request</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_PURCHASE_REQUEST_LIST'))}">
                                  <option value="ListPurchaseRequests">Purchase Requests</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST'))}">
                                  <option value="CreateRepairRequest">Create Repair Request</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_REPAIR_REQUEST_LIST'))}">
                                  <option value="repairrequestlist">Repair Requests</option>
                              </c:if>
                          </select>
                    </c:if>

                    <!-- Purchase Order Dropdown - Dành cho Accounting -->
                    <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER') || sessionScope.userPermissions.contains('VIEW_PURCHASE_ORDER_LIST'))}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>Purchase Order</option>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER'))}">
                                  <option value="CreatePurchaseOrder">Create Purchase Order</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_PURCHASE_ORDER_LIST'))}">
                                  <option value="PurchaseOrderList">Purchase Orders</option>
                              </c:if>
                          </select>
                    </c:if>

                    <!-- History Dropdown -->
                    <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_IMPORT_LIST') || sessionScope.userPermissions.contains('VIEW_EXPORT_LIST'))}">
                          <select class="filter-categories border-0 mb-0 me-5" onchange="location.href = this.value;">
                              <option selected disabled>History</option>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_IMPORT_LIST'))}">
                                  <option value="ImportDetailHistory">Import Details</option>
                              </c:if>
                              <c:if test="${not empty sessionScope.user && (sessionScope.user.roleId == 1 || sessionScope.userPermissions.contains('VIEW_EXPORT_LIST'))}">
                                  <option value="ExportDetailHistory">Export Details</option>
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
