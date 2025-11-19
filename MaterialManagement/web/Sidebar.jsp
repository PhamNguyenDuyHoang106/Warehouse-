<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.User" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="utils.PermissionHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% 
    HttpSession ses = request.getSession(false); 
    User user = null;
    if (ses != null) {
        user = (User) ses.getAttribute("user");
    }
    if (user != null) {
        try {
            PermissionDAO permissionDAO = new PermissionDAO();
            List<String> permissionNames = permissionDAO.getPermissionsByRole(user.getRoleId())
                .stream()
                .map(permission -> permission.getPermissionName())
                .collect(Collectors.toList());
            ses.setAttribute("userPermissions", permissionNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-4">
        <ul class="nav flex-column menu-list list-unstyled">
            <c:if test="${not empty sessionScope.user}">
                
                <!-- ============================================ -->
                <!-- SYSTEM MANAGEMENT - Cho admin và chức năng chung -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#systemMenu" aria-expanded="false" aria-controls="systemMenu">
                            <i class="fas fa-cog fs-4 me-3"></i>
                            🔧 System
                        </a>
                        <div class="collapse" id="systemMenu">
                            <ul class="nav flex-column ms-3">
                                <!-- Admin only -->
                                <c:if test="${sessionScope.user.roleId == 1}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="RolePermission">
                                            <i class="fas fa-key me-2"></i> Permissions
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="UserList">
                                            <i class="fas fa-users me-2"></i> User Management
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="UnitList">
                                            <i class="fas fa-cubes me-2"></i> Units
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="depairmentlist">
                                            <i class="fas fa-building me-2"></i> Departments
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="WarehouseRackList">
                                            <i class="fas fa-layer-group me-2"></i> Warehouse Racks
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách phương tiện')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="VehicleList">
                                            <i class="fas fa-truck me-2"></i> Vehicles
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="InventoryMovement?action=list">
                                            <i class="fas fa-history me-2"></i> Inventory History
                                        </a>
                                    </li>
                                </c:if>
                                
                                <!-- Common system functions -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="Customer?action=list">
                                            <i class="fas fa-user me-2"></i> Customers
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="Supplier?action=list">
                                            <i class="fas fa-truck me-2"></i> Suppliers
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="dashboardmaterial">
                                            <i class="fas fa-shopping-cart me-2"></i> Materials
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS danh mục')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="Category?service=listCategory">
                                            <i class="fas fa-folder me-2"></i> Categories
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="InventoryReport">
                                            <i class="fas fa-warehouse me-2"></i> Inventory Report
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="StaticInventory">
                                            <i class="fas fa-chart-bar me-2"></i> Static Inventory
                                        </a>
                                    </li>
                                </c:if>
                            </ul>
                        </div>
                    </li>
                </c:if>

                <!-- ============================================ -->
                <!-- GIÁM ĐỐC - Role 2: Xem báo cáo, duyệt yêu cầu -->
                <!-- ============================================ -->
                <!-- Admin luôn thấy menu này, các role khác cần có permission -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || sessionScope.user.roleId == 2
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo lợi nhuận')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo công nợ')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
                        <li class="nav-item mb-2">
                            <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#directorMenu" aria-expanded="false" aria-controls="directorMenu">
                                <i class="fas fa-user-tie fs-4 me-3"></i>
                                👔 Director
                            </a>
                            <div class="collapse" id="directorMenu">
                                <ul class="nav flex-column ms-3">
                                    <!-- Reports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo lợi nhuận')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ProfitTracking?type=daily">
                                                <i class="fas fa-chart-line me-2"></i> Profit Report
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo công nợ')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="AccountsReceivable?action=list">
                                                <i class="fas fa-money-bill-wave me-2"></i> Accounts Receivable
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="AccountsPayable?action=list">
                                                <i class="fas fa-money-bill me-2"></i> Accounts Payable
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="InventoryReport">
                                                <i class="fas fa-warehouse me-2"></i> Inventory Report
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Approvals -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ListPurchaseRequests">
                                                <i class="fas fa-check-circle me-2"></i> Approve Purchase Requests
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportRequestList">
                                                <i class="fas fa-check-circle me-2"></i> Approve Export Requests
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="PurchaseOrderList">
                                                <i class="fas fa-check-circle me-2"></i> Approve Purchase Orders
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="SalesOrder?action=list">
                                                <i class="fas fa-check-circle me-2"></i> Approve Sales Orders
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </li>
                </c:if>

                <!-- ============================================ -->
                <!-- KẾ TOÁN - Role 3, 4: Hóa đơn, công nợ, bút toán -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 3 || sessionScope.user.roleId == 4}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải thu')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải trả')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                        <li class="nav-item mb-2">
                            <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#accountingMenu" aria-expanded="false" aria-controls="accountingMenu">
                                <i class="fas fa-calculator fs-4 me-3"></i>
                                💰 Accounting
                            </a>
                            <div class="collapse" id="accountingMenu">
                                <ul class="nav flex-column ms-3">
                                    <!-- Invoices -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Invoice?action=list">
                                                <i class="fas fa-file-invoice me-2"></i> Invoices
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Tax Invoices -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="TaxInvoiceList">
                                                <i class="fas fa-receipt me-2"></i> Tax Invoices
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Currency Management -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="CurrencyList">
                                                <i class="fas fa-coins me-2"></i> Currency Management
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Accounts Receivable/Payable -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải thu')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="AccountsReceivable?action=list">
                                                <i class="fas fa-arrow-down me-2"></i> Accounts Receivable
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải trả')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="AccountsPayable?action=list">
                                                <i class="fas fa-arrow-up me-2"></i> Accounts Payable
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Payments -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Payment?action=list">
                                                <i class="fas fa-credit-card me-2"></i> Payments
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Journals -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Journal?action=list">
                                                <i class="fas fa-book me-2"></i> Journal Entries
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Purchase Orders (for accounting) -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="PurchaseOrderList">
                                                <i class="fas fa-shopping-cart me-2"></i> Purchase Orders
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Accounts Chart -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Account?action=list">
                                                <i class="fas fa-chart-bar me-2"></i> Chart of Accounts
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </li>
                    </c:if>
                </c:if>

                <!-- ============================================ -->
                <!-- MUA HÀNG - Role 5, 6: Yêu cầu mua, đơn đặt hàng -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 5 || sessionScope.user.roleId == 6}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                        <li class="nav-item mb-2">
                            <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#purchaseMenu" aria-expanded="false" aria-controls="purchaseMenu">
                                <i class="fas fa-shopping-cart fs-4 me-3"></i>
                                🛒 Purchasing
                            </a>
                            <div class="collapse" id="purchaseMenu">
                                <ul class="nav flex-column ms-3">
                                    <!-- Purchase Requests -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ListPurchaseRequests">
                                                <i class="fas fa-list me-2"></i> Purchase Requests
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="CreatePurchaseRequest">
                                                <i class="fas fa-plus-circle me-2"></i> Create Purchase Request
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Purchase Orders -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="PurchaseOrderList">
                                                <i class="fas fa-box me-2"></i> Purchase Orders
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PO')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="CreatePurchaseOrder">
                                                <i class="fas fa-plus-circle me-2"></i> Create Purchase Order
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Imports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ImportList">
                                                <i class="fas fa-box-open me-2"></i> Import Slips
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo nhập kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ImportMaterial">
                                                <i class="fas fa-plus-circle me-2"></i> Create Import Slip
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </li>
                    </c:if>
                </c:if>

                <!-- ============================================ -->
                <!-- BÁN HÀNG - Role 7, 8: Báo giá, đơn hàng, xuất kho -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 7 || sessionScope.user.roleId == 8}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS báo giá')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn bán')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                        <li class="nav-item mb-2">
                            <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#salesMenu" aria-expanded="false" aria-controls="salesMenu">
                                <i class="fas fa-briefcase fs-4 me-3"></i>
                                💼 Sales
                            </a>
                            <div class="collapse" id="salesMenu">
                                <ul class="nav flex-column ms-3">
                                    <!-- Quotations -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS báo giá')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Quotation?action=list">
                                                <i class="fas fa-file-alt me-2"></i> Quotations
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo báo giá')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Quotation?action=edit">
                                                <i class="fas fa-plus-circle me-2"></i> Create Quotation
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Sales Orders -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn bán')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="SalesOrder?action=list">
                                                <i class="fas fa-shopping-bag me-2"></i> Sales Orders
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo SO')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="SalesOrder?action=edit">
                                                <i class="fas fa-plus-circle me-2"></i> Create Sales Order
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Exports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportList">
                                                <i class="fas fa-arrow-up me-2"></i> Export Slips
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo xuất kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportMaterial">
                                                <i class="fas fa-plus-circle me-2"></i> Create Export Slip
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Customers -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="Customer?action=list">
                                                <i class="fas fa-user me-2"></i> Customers
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </li>
                    </c:if>
                </c:if>

                <!-- ============================================ -->
                <!-- KHO - Role 9, 10, 11: Nhập/xuất, kiểm kho, sửa chữa -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 9 || sessionScope.user.roleId == 10 || sessionScope.user.roleId == 11}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                        <li class="nav-item mb-2">
                            <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#warehouseMenu" aria-expanded="false" aria-controls="warehouseMenu">
                                <i class="fas fa-warehouse fs-4 me-3"></i>
                                🏭 Warehouse
                            </a>
                            <div class="collapse" id="warehouseMenu">
                                <ul class="nav flex-column ms-3">
                                    <!-- Imports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ImportList">
                                                <i class="fas fa-arrow-down me-2"></i> Import Slips
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo nhập kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ImportMaterial">
                                                <i class="fas fa-plus-circle me-2"></i> Create Import Slip
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Exports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportList">
                                                <i class="fas fa-arrow-up me-2"></i> Export Slips
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo xuất kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportMaterial">
                                                <i class="fas fa-plus-circle me-2"></i> Create Export Slip
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Inventory -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="InventoryReport">
                                                <i class="fas fa-chart-bar me-2"></i> Báo cáo tồn kho
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="InventoryMovement?action=list">
                                                <i class="fas fa-history me-2"></i> Inventory History
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Warehouse Management -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="WarehouseRackList">
                                                <i class="fas fa-layer-group me-2"></i> Warehouse Racks
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- Repair Requests -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="repairrequestlist">
                                                <i class="fas fa-tools me-2"></i> Repair Requests
                                            </a>
                                        </li>
                                    </c:if>
                                    
                                    <!-- History -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ImportDetailHistory">
                                                <i class="fas fa-history me-2"></i> Import History
                                            </a>
                                        </li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <li class="nav-item">
                                            <a class="nav-link d-flex align-items-center ms-4" href="ExportDetailHistory">
                                                <i class="fas fa-history me-2"></i> Export History
                                            </a>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </li>
                    </c:if>
                </c:if>

                <!-- ============================================ -->
                <!-- YÊU CẦU - Cho nhân viên tạo yêu cầu -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu sửa')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#requestMenu" aria-expanded="false" aria-controls="requestMenu">
                            <i class="fas fa-file-alt fs-4 me-3"></i>
                            📝 Requests
                        </a>
                        <div class="collapse" id="requestMenu">
                            <ul class="nav flex-column ms-3">
                                <!-- Export Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu xuất')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="ExportRequestList">
                                            <i class="fas fa-arrow-up me-2"></i> Export Requests
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu xuất')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="CreateExportRequest">
                                            <i class="fas fa-plus-circle me-2"></i> Create Export Request
                                        </a>
                                    </li>
                                </c:if>
                                
                                <!-- Purchase Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="ListPurchaseRequests">
                                            <i class="fas fa-shopping-cart me-2"></i> Purchase Requests
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="CreatePurchaseRequest">
                                            <i class="fas fa-plus-circle me-2"></i> Tạo yêu cầu mua
                                        </a>
                                    </li>
                                </c:if>
                                
                                <!-- Repair Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="repairrequestlist">
                                            <i class="fas fa-tools me-2"></i> Repair Requests
                                        </a>
                                    </li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu sửa')}">
                                    <li class="nav-item">
                                        <a class="nav-link d-flex align-items-center ms-4" href="CreateRepairRequest">
                                            <i class="fas fa-plus-circle me-2"></i> Create Repair Request
                                        </a>
                                    </li>
                                </c:if>
                            </ul>
                        </div>
                    </li>
                </c:if>
            </c:if>
        </ul>
    </div>
</div>

<style>
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
</style>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const currentPage = window.location.pathname.split('/').pop();

        const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href') ? link.getAttribute('href').split('/').pop() : null;
            if (href === currentPage || href === currentPage + '.jsp') {
                link.classList.add('active');
                link.setAttribute('aria-current', 'page');
            } else {
                link.classList.remove('active');
                link.removeAttribute('aria-current');
            }
        });

        navLinks.forEach(link => {
            link.addEventListener('click', function (event) {
                navLinks.forEach(l => {
                    l.classList.remove('active');
                    l.removeAttribute('aria-current');
                });
                this.classList.add('active');
                this.setAttribute('aria-current', 'page');
            });
        });
    });
</script>
