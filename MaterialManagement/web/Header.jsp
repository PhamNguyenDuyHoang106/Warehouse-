<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="entity.User" %>
<%@ page import="utils.PermissionHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>

<%
User user = null;
if (session != null) {
    user = (User) session.getAttribute("user");
}
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
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        /* Ensure header, body and footer are consistent when zooming - all full width */
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
        
        /* Ensure container-fluid for header and footer matches body content */
        header .container-fluid, footer .container-fluid {
            width: 100%;
            padding-left: calc(var(--bs-gutter-x, 0.75rem) * 1);
            padding-right: calc(var(--bs-gutter-x, 0.75rem) * 1);
            margin-left: auto;
            margin-right: auto;
            box-sizing: border-box;
        }
        
        /* Ensure header menu doesn't overflow */
        header nav.main-menu {
            overflow-x: auto;
            overflow-y: hidden;
            -webkit-overflow-scrolling: touch;
            width: 100%;
        }
        
        /* Ensure dropdown doesn't overflow */
        header .filter-categories {
            max-width: 100%;
            white-space: nowrap;
            box-sizing: border-box;
            min-width: 180px;
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: white;
            font-size: 14px;
        }
        
        /* Responsive for mobile */
        @media (max-width: 991px) {
            header .offcanvas-body {
                overflow-x: hidden;
            }
            
            header .container-fluid, footer .container-fluid {
                padding-left: 15px;
                padding-right: 15px;
            }
        }
        
        /* Ensure row in header and footer has same gutter */
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

                    <c:if test="${not empty sessionScope.user}">
                        <!-- ============================================ -->
                        <!-- SYSTEM MANAGEMENT - Cho admin và chức năng chung -->
                        <!-- ============================================ -->
                        <c:if test="${sessionScope.user.roleId == 1 
                                      || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                            <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                <option value="" selected disabled>🔧 System</option>
                                
                                <!-- Admin only -->
                                <c:if test="${sessionScope.user.roleId == 1}">
                                    <option value="RolePermission">📋 Permissions</option>
                                    <option value="UserList">👥 User Management</option>
                                </c:if>
                                
                                <!-- Common system functions -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                                    <option value="Customer?action=list">👤 Customers</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')}">
                                    <option value="Supplier?action=list">🏢 Suppliers</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')}">
                                    <option value="dashboardmaterial">📦 Materials</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS danh mục')}">
                                    <option value="Category?service=listCategory">📁 Categories</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1}">
                                    <option value="UnitList">📏 Units</option>
                                    <option value="depairmentlist">🏛️ Departments</option>
                                    <option value="WarehouseRackList">🗄️ Warehouse Racks</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách phương tiện')}">
                                    <option value="VehicleList">🚚 Vehicles</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                    <option value="InventoryReport">📊 Inventory Report</option>
                                    <option value="StaticInventory">📊 Static Inventory</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1}">
                                    <option value="InventoryMovement?action=list">📜 Inventory History</option>
                                </c:if>
                            </select>
                        </c:if>

                        <!-- ============================================ -->
                        <!-- GIÁM ĐỐC - Role 2: Xem báo cáo, duyệt yêu cầu -->
                        <!-- ============================================ -->
                        <!-- Admin luôn thấy menu này, các role khác cần có permission -->
                        <c:if test="${sessionScope.user.roleId == 1 
                                      || sessionScope.user.roleId == 2
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Xem báo cáo lợi nhuận')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Xem báo cáo công nợ')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')
                                      || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
                            <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                <option value="" selected disabled>👔 Director</option>
                                
                                <!-- Reports -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo lợi nhuận')}">
                                    <option value="ProfitTracking?type=daily">📈 Profit Report</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo công nợ')}">
                                    <option value="AccountsReceivable?action=list">💰 Accounts Receivable</option>
                                    <option value="AccountsPayable?action=list">💸 Accounts Payable</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                    <option value="InventoryReport">📦 Inventory Report</option>
                                </c:if>
                                
                                <!-- Approvals -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')}">
                                    <option value="ListPurchaseRequests">✅ Approve Purchase Requests</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')}">
                                    <option value="ExportRequestList">✅ Approve Export Requests</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')}">
                                    <option value="PurchaseOrderList">✅ Approve Purchase Orders</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
                                    <option value="SalesOrder?action=list">✅ Approve Sales Orders</option>
                                </c:if>
                            </select>
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
                                <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                    <option value="" selected disabled>💰 Accounting</option>
                                    
                                    <!-- Invoices -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')}">
                                        <option value="Invoice?action=list">🧾 Invoices</option>
                                    </c:if>
                                    
                                    <!-- Tax Invoices -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')}">
                                        <option value="TaxInvoiceList">🧾 Tax Invoices</option>
                                    </c:if>
                                    
                                    <!-- Currency Management -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <option value="CurrencyList">💰 Currency Management</option>
                                    </c:if>
                                    
                                    <!-- Accounts Receivable/Payable -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải thu')}">
                                        <option value="AccountsReceivable?action=list">📥 Accounts Receivable</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải trả')}">
                                        <option value="AccountsPayable?action=list">📤 Accounts Payable</option>
                                    </c:if>
                                    
                                    <!-- Payments -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')}">
                                        <option value="Payment?action=list">💳 Payments</option>
                                    </c:if>
                                    
                                    <!-- Journals -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')}">
                                        <option value="Journal?action=list">📝 Journal Entries</option>
                                    </c:if>
                                    
                                    <!-- Purchase Orders (for accounting) -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                                        <option value="PurchaseOrderList">🛒 Purchase Orders</option>
                                    </c:if>
                                    
                                    <!-- Accounts Chart -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <option value="Account?action=list">📊 Chart of Accounts</option>
                                    </c:if>
                                </select>
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
                                <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                    <option value="" selected disabled>🛒 Purchasing</option>
                                    
                                    <!-- Purchase Requests -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                                        <option value="ListPurchaseRequests">📋 Purchase Requests</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')}">
                                        <option value="CreatePurchaseRequest">➕ Create Purchase Request</option>
                                    </c:if>
                                    
                                    <!-- Purchase Orders -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                                        <option value="PurchaseOrderList">📦 Purchase Orders</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PO')}">
                                        <option value="CreatePurchaseOrder">➕ Create Purchase Order</option>
                                    </c:if>
                                    
                                    <!-- Imports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <option value="ImportList">📥 Import Slips</option>
                            </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo nhập kho')}">
                                        <option value="ImportMaterial">➕ Create Import Slip</option>
                            </c:if>
                        </select>
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
                                <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                    <option value="" selected disabled>💼 Sales</option>
                                    
                                    <!-- Quotations -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS báo giá')}">
                                        <option value="Quotation?action=list">📄 Quotations</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo báo giá')}">
                                        <option value="Quotation?action=edit">➕ Create Quotation</option>
                              </c:if>
                                    
                                    <!-- Sales Orders -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn bán')}">
                                        <option value="SalesOrder?action=list">🛍️ Sales Orders</option>
                              </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo SO')}">
                                        <option value="SalesOrder?action=edit">➕ Create Sales Order</option>
                              </c:if>
                                    
                                    <!-- Exports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <option value="ExportList">📤 Export Slips</option>
                              </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo xuất kho')}">
                                        <option value="ExportMaterial">➕ Create Export Slip</option>
                              </c:if>
                                    
                                    <!-- Customers -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                                        <option value="Customer?action=list">👤 Customers</option>
                              </c:if>
                          </select>
                            </c:if>
                    </c:if>

                        <!-- ============================================ -->
                        <!-- KHO - Role 9, 10, 11: Nhập/xuất, kiểm kho, sửa chữa -->
                        <!-- ============================================ -->
                        <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 9 || sessionScope.user.roleId == 10 || sessionScope.user.roleId == 11}">
                            <c:if test="${sessionScope.user.roleId == 1 
                                          || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')
                                          || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')
                                          || PermissionHelper.hasPermission(sessionScope.user, 'Xem báo cáo tồn kho')
                                          || PermissionHelper.hasPermission(sessionScope.user, 'Xem lịch sử tồn kho')
                                          || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                                <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                    <option value="" selected disabled>🏭 Warehouse</option>
                                    
                                    <!-- Imports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <option value="ImportList">📥 Import Slips</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo nhập kho')}">
                                        <option value="ImportMaterial">➕ Create Import Slip</option>
                                    </c:if>
                                    
                                    <!-- Exports -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <option value="ExportList">📤 Export Slips</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo xuất kho')}">
                                        <option value="ExportMaterial">➕ Create Export Slip</option>
                                    </c:if>
                                    
                                    <!-- Inventory -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                                        <option value="InventoryReport">📊 Inventory Report</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <option value="InventoryMovement?action=list">📜 Inventory History</option>
                                    </c:if>
                                    
                                    <!-- Warehouse Management -->
                                    <c:if test="${sessionScope.user.roleId == 1}">
                                        <option value="WarehouseRackList">🗄️ Warehouse Racks</option>
                                    </c:if>
                                    
                                    <!-- Repair Requests -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                                        <option value="repairrequestlist">🔧 Repair Requests</option>
                                    </c:if>
                                    
                                    <!-- History -->
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                                        <option value="ImportDetailHistory">📜 Import History</option>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                                        <option value="ExportDetailHistory">📜 Export History</option>
                                    </c:if>
                          </select>
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
                            <select class="filter-categories border-0 mb-0 me-3" onchange="if(this.value) location.href = this.value;">
                                <option value="" selected disabled>📝 Requests</option>
                                
                                <!-- Export Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu xuất')}">
                                    <option value="ExportRequestList">📤 Export Requests</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu xuất')}">
                                    <option value="CreateExportRequest">➕ Create Export Request</option>
                                </c:if>
                                
                                <!-- Purchase Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                                    <option value="ListPurchaseRequests">🛒 Purchase Requests</option>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')}">
                                    <option value="CreatePurchaseRequest">➕ Create Purchase Request</option>
                                </c:if>
                                
                                <!-- Repair Requests -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                                    <option value="repairrequestlist">🔧 Repair Requests</option>
                              </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu sửa')}">
                                    <option value="CreateRepairRequest">➕ Create Repair Request</option>
                              </c:if>
                          </select>
                        </c:if>
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
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>
<script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
