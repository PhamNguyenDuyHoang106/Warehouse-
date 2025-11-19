<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="entity.User" %>
<%@ page import="dal.PermissionDAO" %>
<%@ page import="utils.PermissionHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% 
    HttpSession ses = request.getSession(false); 
    User user = (User) ses.getAttribute("user");
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

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-4">
        <ul class="nav flex-column menu-list list-unstyled">
            <c:if test="${not empty sessionScope.user}">
                <!-- ============================================ -->
                <!-- GIÁM ĐỐC - Role 2: Xem báo cáo, duyệt yêu cầu -->
                <!-- ============================================ -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 2}">
                    <c:if test="${sessionScope.user.roleId == 1 
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
                </c:if>
            </c:if>
        </ul>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('#sidebarMenu .nav-link');

        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && currentPath.includes(href.split('/').pop())) {
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
