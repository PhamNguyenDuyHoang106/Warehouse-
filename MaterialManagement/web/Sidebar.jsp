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

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<style>
    /* Sidebar mặc định - khi không nằm trong wrapper (cho các trang khác) */
    .sidebar {
        width: 300px;
        min-height: 100vh;
        position: fixed;
        left: 0;
        top: 0;
        background: linear-gradient(180deg, #1e3a8a 0%, #3b82f6 15%, #e5e7eb 15%);
        border-right: 1px solid #e4e4e4;
        padding: 0;
        transition: all 0.3s ease;
        z-index: 1000;
        overflow-y: auto;
        overflow-x: hidden;
        box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
    }
    
    .sidebar.collapsed {
        width: 80px;
        /* Khi collapsed, không còn background xanh ở phần header nữa */
        background: #e5e7eb;
    }
    
    .sidebar a {
        text-decoration: none;
    }
    
    .sidebar-toggle {
        position: absolute;
        top: 20px;
        right: 20px;
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.25);
        border: 2px solid rgba(255, 255, 255, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s ease;
        z-index: 10;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    }
    
    /* Khi sidebar collapsed, nút toggle ở giữa và đậm hơn, rõ ràng hơn */
    .sidebar.collapsed .sidebar-toggle {
        position: relative;
        top: 20px;
        left: 50%;
        right: auto;
        transform: translateX(-50%);
        width: 55px;
        height: 55px;
        background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
        border: 3px solid #ffffff;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
    }
    
    .sidebar-toggle:hover {
        background: rgba(255, 255, 255, 0.4);
        border-color: rgba(255, 255, 255, 0.6);
        transform: scale(1.1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }
    
    .sidebar.collapsed .sidebar-toggle:hover {
        background: linear-gradient(135deg, #1e40af 0%, #2563eb 100%);
        border-color: #fff;
        transform: translateX(-50%) scale(1.15);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5);
        top: 20px; /* Giữ nguyên vị trí khi hover */
    }
    
    .sidebar-toggle i {
        font-size: 18px;
        color: #fff;
        transition: transform 0.3s ease;
        font-weight: bold;
    }
    
    .sidebar.collapsed .sidebar-toggle i {
        transform: rotate(180deg);
        font-size: 22px;
        font-weight: 900;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }
    
    .sidebar-header {
        text-align: center;
        padding: 25px 20px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.2);
        margin-bottom: 0;
        transition: all 0.3s ease;
        background: transparent;
        display: flex;
        align-items: center;
        gap: 14px;
        text-align: left;
        position: relative;
    }
    
    .sidebar-header .avatar-container {
        flex-shrink: 0;
        cursor: pointer;
        text-decoration: none;
        display: inline-block;
        transition: all 0.3s ease;
    }
    
    .sidebar-header .avatar-container:hover {
        transform: scale(1.05);
    }
    
    .sidebar-header .avatar-container:hover .sidebar-avatar {
        border-color: rgba(255, 255, 255, 0.8);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
    }
    
    .sidebar-header .user-info {
        flex: 1;
        min-width: 0;
    }
    
    .sidebar.collapsed .sidebar-header {
        padding: 20px 10px;
    }
    
    .sidebar-header img,
    .sidebar-header .sidebar-avatar {
        border-radius: 50%;
        width: 55px;
        height: 55px;
        object-fit: cover;
        border: 3px solid rgba(255, 255, 255, 0.4);
        transition: all 0.3s ease;
        background: #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        display: block;
        /* Xoay thẳng avatar lên - không để nằm ngang */
        transform: rotate(0deg) !important;
        image-orientation: from-image;
        /* Đảm bảo ảnh hiển thị đúng hướng */
        -webkit-transform: rotate(0deg) !important;
        -moz-transform: rotate(0deg) !important;
        -ms-transform: rotate(0deg) !important;
        -o-transform: rotate(0deg) !important;
    }
    
    /* Khi sidebar collapsed, ẩn toàn bộ header (background xanh, avatar, user info) - chỉ giữ nút toggle */
    .sidebar.collapsed .sidebar-header {
        padding: 15px 0;
        border-bottom: none;
        background: transparent !important;
        min-height: auto;
        height: auto;
        justify-content: center;
        align-items: center;
    }
    
    /* Đảm bảo khi collapsed, sidebar không có background xanh */
    .sidebar.collapsed {
        background: #e5e7eb !important;
    }
    
    /* Ẩn avatar và user info khi sidebar collapsed */
    .sidebar.collapsed .sidebar-header .avatar-container,
    .sidebar.collapsed .sidebar-header .user-info {
        display: none !important;
    }
    
    .sidebar.collapsed .sidebar-header img,
    .sidebar.collapsed .sidebar-header .sidebar-avatar {
        display: none !important;
    }
    
    .sidebar-header h6 {
        font-weight: bold;
        margin: 0 0 2px 0;
        color: #fff;
        font-size: 15px;
        transition: all 0.3s ease;
        white-space: nowrap;
        overflow: hidden;
    }
    
    .sidebar.collapsed .sidebar-header .user-info {
        display: none;
    }
    
    .sidebar-header small {
        color: rgba(255, 255, 255, 0.9);
        font-size: 12px;
        display: block;
        transition: all 0.3s ease;
        margin: 0;
    }
    
    .sidebar-header hr {
        display: none;
    }
    
    /* Ẩn divider khi sidebar collapsed */
    .sidebar.collapsed .sidebar-header-divider {
        display: none !important;
    }
    
    .sidebar-menu {
        list-style: none;
        padding: 12px;
        margin: 0;
        background: #e5e7eb;
        min-height: calc(100vh - 200px);
    }
    
    .sidebar .menu-item,
    .sidebar .menu-toggle {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        color: #333;
        cursor: pointer;
        border-radius: 8px;
        font-size: 0.95rem;
        margin-bottom: 6px;
        transition: all 0.3s ease;
        white-space: nowrap;
        background: #ffffff;
        border: 1px solid #e0e0e0;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        pointer-events: auto;
        user-select: none;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
    }
    
    .sidebar.collapsed .menu-item span,
    .sidebar.collapsed .menu-toggle span {
        opacity: 0;
        width: 0;
        overflow: hidden;
    }
    
    .sidebar .menu-item i,
    .sidebar .menu-toggle i {
        width: 24px;
        text-align: center;
        margin-right: 12px;
        font-size: 18px;
        flex-shrink: 0;
    }
    
    .sidebar.collapsed .menu-item i,
    .sidebar.collapsed .menu-toggle i {
        margin-right: 0;
    }
    
    .sidebar .menu-item:hover,
    .sidebar .menu-toggle:hover {
        background: #e6eaff;
        color: #0038d1;
        border-color: #0038d1;
        box-shadow: 0 4px 8px rgba(0, 56, 209, 0.15);
        transform: translateX(2px);
    }
    
    .sidebar .menu-item.active {
        background: linear-gradient(135deg, #0038d1 0%, #0052ff 100%);
        color: #fff;
        border-color: #0038d1;
        box-shadow: 0 4px 12px rgba(0, 56, 209, 0.25);
    }
    
    .sidebar .menu-item.active:hover {
        background: linear-gradient(135deg, #0029a3 0%, #0038d1 100%);
        box-shadow: 0 6px 16px rgba(0, 56, 209, 0.3);
    }
    
    .menu-group {
        margin-bottom: 8px;
    }
    
    .submenu {
        padding-left: 0;
        padding-top: 8px;
        display: none;
        list-style: none;
        margin: 8px 0 0 0;
        transition: all 0.3s ease;
    }
    
    .menu-group.active .submenu {
        display: block !important;
    }
    
    /* Đảm bảo có thể toggle menu group ngay cả khi có active item */
    .menu-group.active .menu-toggle {
        cursor: pointer;
        pointer-events: auto;
    }
    
    .sidebar.collapsed .submenu {
        display: none !important;
    }
    
    .submenu li {
        margin-bottom: 2px;
    }
    
    .submenu li a {
        display: block;
        padding: 10px 16px;
        color: #444;
        font-size: 0.9rem;
        border-radius: 6px;
        transition: all 0.3s ease;
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        margin-bottom: 4px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    }
    
    .submenu li a:hover {
        background: #eef2ff;
        color: #0038d1;
        border-color: #0038d1;
        box-shadow: 0 2px 4px rgba(0, 56, 209, 0.1);
        transform: translateX(2px);
    }
    
    .submenu li a.active {
        background: linear-gradient(135deg, #0038d1 0%, #0052ff 100%);
        color: #fff;
        border-color: #0038d1;
        box-shadow: 0 2px 6px rgba(0, 56, 209, 0.2);
    }
    
    /* Đảm bảo chỉ có một item active tại một thời điểm */
    .submenu li a.active ~ .submenu li a.active,
    .sidebar .submenu li a.active + .submenu li a.active {
        background: #f8f9fa;
        color: #444;
        border-color: #e9ecef;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
    }
    
    .menu-toggle::after {
        content: '\f107';
        font-family: 'Font Awesome 6 Free';
        font-weight: 900;
        margin-left: auto;
        transition: transform 0.3s ease;
        font-size: 12px;
    }
    
    .menu-group.active .menu-toggle::after {
        transform: rotate(180deg);
    }
    
    .sidebar.collapsed .menu-toggle::after {
        display: none;
    }
    
    /* Scrollbar */
    .sidebar::-webkit-scrollbar {
        width: 6px;
    }
    
    .sidebar::-webkit-scrollbar-track {
        background: #f1f1f1;
    }
    
    .sidebar::-webkit-scrollbar-thumb {
        background: #ccc;
        border-radius: 3px;
    }
    
    .sidebar::-webkit-scrollbar-thumb:hover {
        background: #aaa;
    }
    
    /* Main content wrapper - Bao sidebar và body content, nằm dưới header */
    /* Cấu trúc: Header (full width) -> Main Content (Sidebar + Content trong cùng box) */
    .main-content-wrapper {
        position: relative;
        width: 100%;
        max-width: 100%;
        min-height: calc(100vh - 100px);
        display: flex;
        flex-direction: row;
        margin: 0;
        padding: 0;
        transition: all 0.3s ease;
        align-items: stretch;
        box-sizing: border-box;
        clear: both;
    }
    
    /* Sidebar wrapper trong main-content-wrapper */
    .main-content-wrapper .sidebar-wrapper-inner {
        flex-shrink: 0;
        width: 300px;
        position: relative;
        z-index: 1;
        height: 100%;
    }
    
    body.sidebar-collapsed .main-content-wrapper .sidebar-wrapper-inner {
        width: 80px;
    }
    
    /* Sidebar trong wrapper - relative position, nằm cùng box với body */
    /* QUAN TRỌNG: CSS này phải override hoàn toàn CSS của .sidebar */
    .main-content-wrapper .sidebar-wrapper-inner .sidebar,
    .main-content-wrapper .sidebar {
        position: relative !important;
        flex-shrink: 0 !important;
        width: 100% !important;
        min-height: 100% !important;
        height: 100% !important;
        top: auto !important;
        left: auto !important;
        margin: 0 !important;
        padding: 0 !important;
        z-index: 1 !important;
        background: linear-gradient(180deg, #1e3a8a 0%, #3b82f6 15%, #e5e7eb 15%) !important;
        border-right: 1px solid #e4e4e4 !important;
        box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1) !important;
        overflow-y: auto !important;
        overflow-x: hidden !important;
    }
    
    body.sidebar-collapsed .main-content-wrapper .sidebar-wrapper-inner .sidebar,
    body.sidebar-collapsed .main-content-wrapper .sidebar {
        width: 100% !important;
        /* Khi collapsed, không còn background xanh */
        background: #e5e7eb !important;
    }
    
    /* Body content trong wrapper */
    .main-content-wrapper .main-content-body {
        flex: 1 !important;
        margin: 0 !important;
        padding: 0 !important;
        width: calc(100% - 300px) !important;
        transition: all 0.3s ease;
        min-height: 100vh !important;
        overflow-x: hidden !important;
        overflow-y: auto !important;
        box-sizing: border-box !important;
        display: block !important;
        visibility: visible !important;
        opacity: 1 !important;
        position: relative !important;
        z-index: 2 !important;
        background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%) !important;
    }
    
    body.sidebar-collapsed .main-content-wrapper .main-content-body {
        width: calc(100% - 80px) !important;
    }
    
    /* Đảm bảo container-fluid trong main-content-body hiển thị */
    .main-content-wrapper .main-content-body .container-fluid {
        display: block !important;
        visibility: visible !important;
        width: 100% !important;
        max-width: 100% !important;
        margin: 0 !important;
        padding: 30px !important;
        box-sizing: border-box !important;
    }
    
    /* Đảm bảo tất cả các phần tử trong body content hiển thị */
    .main-content-wrapper .main-content-body * {
        visibility: visible !important;
    }
    
    /* Body không cần padding-left nữa vì đã có wrapper */
    body {
        padding: 0;
        margin: 0;
        width: 100%;
        max-width: 100%;
        box-sizing: border-box;
    }
    
    /* Header và Footer luôn full width, không bị ảnh hưởng bởi sidebar */
    header,
    footer {
        width: 100% !important;
        max-width: 100% !important;
        margin-left: 0 !important;
        margin-right: 0 !important;
        padding-left: 0 !important;
        padding-right: 0 !important;
        position: relative;
        z-index: 998;
        box-sizing: border-box;
    }
    
    /* Đảm bảo header và footer không bị ảnh hưởng bởi main-content-wrapper */
    header {
        clear: both;
    }
    
    footer {
        clear: both;
    }
    
    /* Điều chỉnh container-fluid và row để không bị che bởi sidebar */
    .container-fluid {
        transition: margin-left 0.3s ease;
    }
    
    /* Ẩn div col-md-3 chứa sidebar cũ (sidebar đã là fixed) */
    /* Sử dụng class cụ thể thay vì :has() selector để tương thích hơn */
    .container-fluid .row > .col-md-3.col-lg-2.bg-light.sidebar,
    .container-fluid .row > .col-md-3.col-lg-2.d-md-block.sidebar,
    .container-fluid .row > .col-md-3.col-lg-2:not(.sidebar-wrapper-inner) {
        display: none !important;
    }
    
    /* Đảm bảo main content không bị che */
    .container-fluid .row > .col-md-9,
    .container-fluid .row > .col-lg-10,
    .container-fluid .row > .col-12 {
        transition: margin-left 0.3s ease;
        width: 100% !important;
        max-width: 100% !important;
        flex: 0 0 100% !important;
    }
    
    /* Đảm bảo content có padding phù hợp */
    .content {
        padding-left: 30px !important;
        padding-right: 30px !important;
    }
    
    /* Responsive */
    @media (max-width: 768px) {
        .sidebar {
            transform: translateX(-100%);
        }
        
        .sidebar.show {
            transform: translateX(0);
        }
        
        body {
            padding-left: 0 !important;
        }
        
        body.sidebar-collapsed {
            padding-left: 0 !important;
        }
        
        .main-content-wrapper {
            margin-left: 0 !important;
            width: 100% !important;
        }
        
        header {
            margin-left: 0 !important;
        }
    }
</style>

            <c:if test="${not empty sessionScope.user}">
<nav class="sidebar" id="sidebar">
    <button class="sidebar-toggle" onclick="toggleSidebar()">
        <i class="fas fa-chevron-left"></i>
    </button>
    
    <!-- Header -->
    <div class="sidebar-header">
        <a href="profile" class="avatar-container" title="View Profile">
            <c:choose>
                <c:when test="${not empty sessionScope.user.avatar}">
                    <img src="${pageContext.request.contextPath}/images/profiles/${sessionScope.user.avatar}?t=${System.currentTimeMillis()}" 
                         alt="avatar" 
                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/profiles/default.jpg';"
                         class="sidebar-avatar">
                </c:when>
                <c:otherwise>
                    <img src="${pageContext.request.contextPath}/images/profiles/default.jpg" 
                         alt="avatar" 
                         class="sidebar-avatar">
                </c:otherwise>
            </c:choose>
        </a>
        <div class="user-info">
            <h6>${sessionScope.user.fullName}</h6>
            <small>
                <c:choose>
                    <c:when test="${sessionScope.user.roleId == 1}">Administrator</c:when>
                    <c:when test="${sessionScope.user.roleId == 2}">Director</c:when>
                    <c:when test="${sessionScope.user.roleId == 3}">Accountant</c:when>
                    <c:when test="${sessionScope.user.roleId == 4}">Employee</c:when>
                    <c:otherwise>User</c:otherwise>
                </c:choose>
            </small>
        </div>
    </div>
    <hr class="sidebar-header-divider" style="margin: 0; border: none; border-top: 1px solid rgba(255, 255, 255, 0.2);">

    <ul class="sidebar-menu">
        <!-- Dashboard / Overview -->
        <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
            <li>
                <a href="home" class="menu-item">
                    <i class="bi bi-speedometer2"></i>
                    <span>Dashboard</span>
                </a>
            </li>
        </c:if>

        <!-- ==================== SYSTEM ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
            <li class="menu-group">
                <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                    <i class="bi bi-gear"></i>
                    <span>System</span>
                </a>
                <ul class="submenu">
                                <!-- Admin only -->
                                <c:if test="${sessionScope.user.roleId == 1}">
                        <li><a href="RolePermission">Role & Permissions</a></li>
                        <li><a href="UserList">Users</a></li>
                        <li><a href="AccountType?action=list">Account Types</a></li>
                        <li><a href="ExchangeRate?action=list">Exchange Rates</a></li>
                        <li><a href="Session?action=list">Session Management</a></li>
                        <li><a href="APIKey?action=list">API Keys</a></li>
                        <li><a href="RateLimit?action=list">Rate Limits</a></li>
                        <li><a href="UnitList">Units</a></li>
                        <li><a href="depairmentlist">Departments</a></li>
                        <li><a href="WarehouseRackList">Warehouse Racks</a></li>
                        <li><a href="VehicleList">Vehicles</a></li>
                                </c:if>
                                
                    <!-- Common -->
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                        <li><a href="Customer?action=list">Customers</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS NCC')}">
                        <li><a href="Supplier?action=list">Suppliers</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách NVL')}">
                        <li><a href="dashboardmaterial">Materials</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS danh mục')}">
                        <li><a href="Category?service=listCategory">Categories</a></li>
                    </c:if>
                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Danh sách phương tiện')}">
                        <li><a href="VehicleList">Vehicles</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                        <li><a href="InventoryReport">Inventory Report</a></li>
                        <li><a href="StaticInventory">Static Inventory</a></li>
                                </c:if>
                    <c:if test="${sessionScope.user.roleId == 1}">
                        <li><a href="InventoryMovement?action=list">Inventory History</a></li>
                                </c:if>
                            </ul>
                    </li>
                </c:if>

        <!-- ==================== DIRECTOR ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || sessionScope.user.roleId == 2
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo lợi nhuận')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo công nợ')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
            <li class="menu-group">
                <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                    <i class="bi bi-person-badge"></i>
                    <span>Director</span>
                </a>
                <ul class="submenu">
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo lợi nhuận')}">
                        <li><a href="ProfitTracking?type=daily">Profit Report</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo công nợ')}">
                        <li><a href="AccountsReceivable?action=list">Accounts Receivable</a></li>
                        <li><a href="AccountsPayable?action=list">Accounts Payable</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                        <li><a href="InventoryReport">Inventory Report</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PR')}">
                        <li><a href="ListPurchaseRequests">Purchase Requests</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt yêu cầu xuất')}">
                        <li><a href="ExportRequestList">Export Requests</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt PO')}">
                        <li><a href="PurchaseOrderList">Purchase Orders</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Duyệt SO')}">
                        <li><a href="SalesOrder?action=list">Sales Orders</a></li>
                                    </c:if>
                                </ul>
                        </li>
                </c:if>

        <!-- ==================== ACCOUNTING ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 3 || sessionScope.user.roleId == 4}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải thu')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải trả')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                <li class="menu-group">
                    <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                        <i class="bi bi-calculator"></i>
                        <span>Accounting</span>
                    </a>
                    <ul class="submenu">
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS hóa đơn')}">
                            <li><a href="Invoice?action=list">Invoices</a></li>
                            <li><a href="TaxInvoiceList">Tax Invoices</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1}">
                            <li><a href="CurrencyList">Currency Management</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải thu')}">
                            <li><a href="AccountsReceivable?action=list">Accounts Receivable</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS công nợ phải trả')}">
                            <li><a href="AccountsPayable?action=list">Accounts Payable</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo bút toán')}">
                            <li><a href="Payment?action=list">Payments</a></li>
                            <li><a href="Journal?action=list">Journal Entries</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                            <li><a href="PurchaseOrderList">Purchase Orders</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1}">
                            <li><a href="Account?action=list">Chart of Accounts</a></li>
                                    </c:if>
                                </ul>
                        </li>
                    </c:if>
                </c:if>

        <!-- ==================== PURCHASING ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 5 || sessionScope.user.roleId == 6}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                <li class="menu-group">
                    <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                        <i class="bi bi-cart"></i>
                        <span>Purchasing</span>
                    </a>
                    <ul class="submenu">
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                            <li><a href="ListPurchaseRequests">Purchase Requests</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn đặt hàng')}">
                        <li><a href="PurchaseOrderList">Purchase Orders</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                            <li><a href="ImportList">Import</a></li>
                                    </c:if>
                                </ul>
                        </li>
                    </c:if>
                </c:if>

        <!-- ==================== SALES ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 7 || sessionScope.user.roleId == 8}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS báo giá')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn bán')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                <li class="menu-group">
                    <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                        <i class="bi bi-bag"></i>
                        <span>Sales</span>
                    </a>
                    <ul class="submenu">
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS báo giá')}">
                            <li><a href="Quotation?action=list">Quotations</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Tạo báo giá')}">
                            <li><a href="Quotation?action=edit">Create Quotation</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS đơn bán')}">
                            <li><a href="SalesOrder?action=list">Sales Orders</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                            <li><a href="ExportList">Export</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS KH')}">
                            <li><a href="Customer?action=list">Customers</a></li>
                                    </c:if>
                                </ul>
                        </li>
                    </c:if>
                </c:if>

        <!-- ==================== WAREHOUSE ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 9 || sessionScope.user.roleId == 10 || sessionScope.user.roleId == 11}">
                    <c:if test="${sessionScope.user.roleId == 1 
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')
                                  || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                <li class="menu-group">
                    <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                        <i class="bi bi-box-seam"></i>
                        <span>Warehouse</span>
                    </a>
                    <ul class="submenu">
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu nhập')}">
                            <li><a href="ImportList">Import</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS phiếu xuất')}">
                            <li><a href="ExportList">Export</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'Báo cáo tồn kho')}">
                            <li><a href="InventoryReport">Inventory Report</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1}">
                            <li><a href="InventoryMovement?action=list">Inventory History</a></li>
                            <li><a href="WarehouseRackList">Warehouse Racks</a></li>
                            <li><a href="VehicleList">Vehicles</a></li>
                                    </c:if>
                                    <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                            <li><a href="repairrequestlist">Repair Requests</a></li>
                                    </c:if>
                                </ul>
                        </li>
                    </c:if>
                </c:if>

        <!-- ==================== REQUESTS ===================== -->
                <c:if test="${sessionScope.user.roleId == 1 
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu xuất')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo PR')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')
                              || PermissionHelper.hasPermission(sessionScope.user, 'Tạo yêu cầu sửa')
                              || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
            <li class="menu-group">
                <a class="menu-toggle" href="javascript:void(0);" onclick="toggleMenuGroup(this, event); return false;">
                    <i class="bi bi-envelope-paper"></i>
                    <span>Requests</span>
                </a>
                <ul class="submenu">
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu xuất')}">
                        <li><a href="ExportRequestList">Export Requests</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu mua')}">
                        <li><a href="ListPurchaseRequests">Purchase Requests</a></li>
                                </c:if>
                                <c:if test="${sessionScope.user.roleId == 1 || PermissionHelper.hasPermission(sessionScope.user, 'DS yêu cầu sửa')}">
                        <li><a href="repairrequestlist">Repair Requests</a></li>
                                </c:if>
                            </ul>
                    </li>
            </c:if>
        </ul>
</nav>

<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('sidebar');
        const body = document.body;
        
        sidebar.classList.toggle('collapsed');
        body.classList.toggle('sidebar-collapsed');
        
        // Save state to localStorage
        localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
    }
    
    function toggleMenuGroup(element, event) {
        // Ngăn chặn event bubbling và default behavior
        if (event) {
            event.preventDefault();
            event.stopPropagation();
        } else if (window.event) {
            // Fallback for IE
            window.event.preventDefault();
            window.event.stopPropagation();
        }
        
        const menuGroup = element.parentElement;
        if (menuGroup && menuGroup.classList.contains('menu-group')) {
            const menuToggle = menuGroup.querySelector('.menu-toggle span');
            const menuGroupName = menuToggle ? menuToggle.textContent.trim() : '';
            
            // Luôn cho phép toggle, ngay cả khi có active item
            const isActive = menuGroup.classList.contains('active');
            menuGroup.classList.toggle('active');
            
            // Lưu state vào localStorage
            if (menuGroupName) {
                localStorage.setItem('menuGroup_' + menuGroupName, menuGroup.classList.contains('active') ? 'open' : 'closed');
            }
            
            // Nếu đang đóng menu group, đảm bảo submenu được ẩn
            if (!menuGroup.classList.contains('active')) {
                const submenu = menuGroup.querySelector('.submenu');
                if (submenu) {
                    submenu.style.display = 'none';
                }
            }
        }
        return false;
    }
    
    // Lưu menu group khi click vào menu item
    document.addEventListener('click', function(e) {
        const menuItem = e.target.closest('.submenu a, .menu-item');
        if (menuItem && menuItem.href) {
            const submenu = menuItem.closest('.submenu');
            if (submenu) {
                const parentGroup = submenu.parentElement;
                if (parentGroup && parentGroup.classList.contains('menu-group')) {
                    const menuToggle = parentGroup.querySelector('.menu-toggle span');
                    const menuGroupName = menuToggle ? menuToggle.textContent.trim() : '';
                    if (menuGroupName) {
                        // Lưu menu group này là menu group cuối cùng được click
                        localStorage.setItem('lastClickedMenuGroup', menuGroupName);
                        // Đảm bảo menu group này được mở
                        localStorage.setItem('menuGroup_' + menuGroupName, 'open');
                    }
                }
            }
        }
    }, true); // Use capture phase to catch clicks early
    
    // Restore sidebar state on page load
    document.addEventListener('DOMContentLoaded', function() {
        const isCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
        if (isCollapsed) {
            document.getElementById('sidebar').classList.add('collapsed');
            document.body.classList.add('sidebar-collapsed');
        }
        
        // Set active menu item based on current page
        const currentPath = window.location.pathname;
        const menuItems = document.querySelectorAll('.sidebar .menu-item, .sidebar .submenu a');
        
        // Tìm item active - ưu tiên match chính xác và match dài hơn trước
        // QUAN TRỌNG: Nếu có nhiều items cùng href, ưu tiên item trong menu group đã được mở (active)
        // Nếu không có menu group nào active, ưu tiên item cuối cùng (index lớn hơn) - menu group xuất hiện sau
        let activeItemFound = false;
        let activeMenuGroup = null;
        let bestMatch = null;
        let bestMatchLength = 0;
        let bestMatchIndex = -1; // Bắt đầu với -1, sau đó chọn index lớn nhất (item cuối cùng)
        let bestMatchInActiveGroup = false; // Đánh dấu item có nằm trong menu group đã active không
        
        // Bước 0: Tìm menu groups đã được mở (từ localStorage hoặc đã có class 'active')
        // Và lấy menu group cuối cùng được click (nếu có)
        const openedMenuGroups = new Set();
        const lastClickedMenuGroupName = localStorage.getItem('lastClickedMenuGroup');
        let lastClickedMenuGroup = null;
        
        document.querySelectorAll('.menu-group').forEach(group => {
            const menuToggle = group.querySelector('.menu-toggle span');
            const menuGroupName = menuToggle ? menuToggle.textContent.trim() : '';
            if (menuGroupName) {
                const savedState = localStorage.getItem('menuGroup_' + menuGroupName);
                if (savedState === 'open' || group.classList.contains('active')) {
                    openedMenuGroups.add(group);
                }
                // Tìm menu group cuối cùng được click
                if (menuGroupName === lastClickedMenuGroupName) {
                    lastClickedMenuGroup = group;
                }
            }
        });
        
        // Bước 1: Tìm tất cả các matches và chọn match tốt nhất
        menuItems.forEach((item, index) => {
            const href = item.getAttribute('href');
            if (href) {
                const hrefPath = href.split('?')[0];
                const currentPathClean = currentPath.split('?')[0];
                const currentPathBase = currentPathClean.split('/').pop() || currentPathClean;
                
                let matchScore = 0;
                let isMatch = false;
                
                // Ưu tiên 1: Exact match
                if (currentPathClean === hrefPath || currentPathBase === hrefPath) {
                    matchScore = 1000;
                    isMatch = true;
                }
                // Ưu tiên 2: Current path chứa href (và href không quá ngắn)
                else if (currentPathClean.includes(hrefPath) && hrefPath.length > 5) {
                    matchScore = hrefPath.length; // Match dài hơn = tốt hơn
                    isMatch = true;
                }
                // Ưu tiên 3: Href chứa current path base
                else if (hrefPath.includes(currentPathBase) && currentPathBase.length > 5) {
                    matchScore = currentPathBase.length;
                    isMatch = true;
                }
                
                if (isMatch) {
                    // Kiểm tra xem item này có nằm trong menu group đã được mở không
                    const submenu = item.closest('.submenu');
                    const parentGroup = submenu ? submenu.parentElement : null;
                    const isInActiveGroup = parentGroup && openedMenuGroups.has(parentGroup);
                    // Kiểm tra xem item này có nằm trong menu group cuối cùng được click không
                    const isInLastClickedGroup = parentGroup === lastClickedMenuGroup;
                    
                    // Tính điểm ưu tiên: last clicked group = 100, active group = 50, index = 1
                    let priority = 0;
                    if (isInLastClickedGroup) priority += 100;
                    if (isInActiveGroup) priority += 50;
                    priority += index; // Index càng lớn càng tốt (item cuối cùng)
                    
                    // Tính điểm ưu tiên cho bestMatch hiện tại
                    let bestPriority = 0;
                    if (bestMatch) {
                        const bestSubmenu = bestMatch.closest('.submenu');
                        const bestParentGroup = bestSubmenu ? bestSubmenu.parentElement : null;
                        if (bestParentGroup === lastClickedMenuGroup) bestPriority += 100;
                        if (bestMatchInActiveGroup) bestPriority += 50;
                        bestPriority += bestMatchIndex;
                    }
                    
                    // Ưu tiên: match score > priority
                    let shouldUpdate = false;
                    if (matchScore > bestMatchLength) {
                        shouldUpdate = true;
                    } else if (matchScore === bestMatchLength && priority > bestPriority) {
                        shouldUpdate = true;
                    }
                    
                    if (shouldUpdate) {
                        bestMatch = item;
                        bestMatchLength = matchScore;
                        bestMatchIndex = index;
                        bestMatchInActiveGroup = isInActiveGroup;
                    }
                }
            }
        });
        
        // Bước 2: Đánh dấu active cho match tốt nhất
        // Xóa tất cả active classes trước để đảm bảo chỉ có một item active
        menuItems.forEach(item => {
            item.classList.remove('active');
        });
        
        if (bestMatch) {
            bestMatch.classList.add('active');
            activeItemFound = true;
            
            // CHỈ mở menu group chứa item active này
            const submenu = bestMatch.closest('.submenu');
            if (submenu) {
                const parentGroup = submenu.parentElement;
                if (parentGroup && parentGroup.classList.contains('menu-group')) {
                    activeMenuGroup = parentGroup;
                    submenu.style.display = 'block';
                    parentGroup.classList.add('active');
                }
            }
        }
        
        // Đảm bảo các menu group khác không tự động mở (trừ menu group chứa active item)
        // QUAN TRỌNG: Khi có active item, chỉ mở menu group chứa active item
        // Đóng TẤT CẢ các menu groups khác có item cùng href với active item
        const allMenuGroups = document.querySelectorAll('.menu-group');
        const activeHref = bestMatch ? bestMatch.getAttribute('href') : null;
        
        // Đóng tất cả menu groups trước, sau đó mới mở active menu group
        allMenuGroups.forEach(group => {
            if (group !== activeMenuGroup) {
                const submenu = group.querySelector('.submenu');
                if (submenu) {
                    // Kiểm tra xem menu group này có chứa item có cùng href với active item không
                    const itemsInGroup = submenu.querySelectorAll('a');
                    let hasSameHrefItem = false;
                    
                    if (activeHref) {
                        itemsInGroup.forEach(item => {
                            if (item.getAttribute('href') === activeHref) {
                                hasSameHrefItem = true;
                            }
                        });
                    }
                    
                    // Nếu menu group này có item cùng href với active item, ĐÓNG NÓ NGAY LẬP TỨC
                    if (hasSameHrefItem) {
                        group.classList.remove('active');
                        submenu.style.display = 'none';
                        // Xóa state từ localStorage để tránh restore lại
                        const menuToggle = group.querySelector('.menu-toggle span');
                        const menuGroupName = menuToggle ? menuToggle.textContent.trim() : '';
                        if (menuGroupName) {
                            localStorage.removeItem('menuGroup_' + menuGroupName);
                        }
                    }
                }
            }
        });
        
        // Restore menu group states from localStorage
        // QUAN TRỌNG: Chỉ restore cho các menu groups không có active item và không có item cùng href với active item
        // KHÔNG restore cho các menu groups đã bị đóng do có item cùng href
        const menuGroups = document.querySelectorAll('.menu-group');
        menuGroups.forEach(group => {
            // Bỏ qua active menu group - nó đã được mở rồi
            if (group === activeMenuGroup) {
                return;
            }
            
            const submenu = group.querySelector('.submenu');
            if (!submenu) {
                return;
            }
            
            // Kiểm tra xem menu group này có item cùng href với active item không
            const itemsInGroup = submenu.querySelectorAll('a');
            let hasSameHrefItem = false;
            
            if (activeHref) {
                itemsInGroup.forEach(item => {
                    if (item.getAttribute('href') === activeHref) {
                        hasSameHrefItem = true;
                    }
                });
            }
            
            // QUAN TRỌNG: Nếu menu group này có item cùng href với active item, KHÔNG restore
            // Nó đã bị đóng ở trên rồi
            if (hasSameHrefItem) {
                return; // Bỏ qua, không restore
            }
            
            // Chỉ restore nếu menu group này không có item cùng href với active item
            const menuToggle = group.querySelector('.menu-toggle span');
            const menuGroupName = menuToggle ? menuToggle.textContent.trim() : '';
            if (menuGroupName) {
                const savedState = localStorage.getItem('menuGroup_' + menuGroupName);
                const hasActiveItem = submenu.querySelector('a.active');
                
                // Chỉ restore nếu không có active item trong menu group này
                // Và menu group này chưa bị đóng (không có class 'active' và submenu đang ẩn)
                if (!hasActiveItem) {
                    // Chỉ restore nếu state trong localStorage là 'open' và menu group hiện tại đang đóng
                    if (savedState === 'open' && !group.classList.contains('active')) {
                        group.classList.add('active');
                        submenu.style.display = 'block';
                    } else if (savedState === 'closed' || !savedState) {
                        // Đảm bảo menu group đóng nếu state là 'closed' hoặc không có state
                        group.classList.remove('active');
                        submenu.style.display = 'none';
                    }
                }
            }
        });
    });
</script>
</c:if>

