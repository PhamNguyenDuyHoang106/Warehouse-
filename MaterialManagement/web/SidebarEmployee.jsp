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
        padding: 8px 0;
        width: 230px;
        height: 100vh;
    }

    #sidebarMenu .nav-link {
        display: flex;
        align-items: center;
        padding: 14px 20px;
        border-radius: 6px;
        color: #333;
        font-weight: 500;
        font-size: 0.95rem;
        text-align: left;
        width: 100%;
        transition: all 0.3s ease;
    }

    #sidebarMenu .nav-link:hover,
    #sidebarMenu .nav-link.active {
        background-color: #8B4513;
        color: #ffffff !important;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }

    #sidebarMenu .nav-link i {
        margin-right: 12px;
        color: inherit;
        transition: color 0.3s ease;
        width: 20px;
        text-align: center;
    }

    #sidebarMenu .nav-link:hover i,
    #sidebarMenu .nav-link.active i {
        color: #ffffff;
    }

    #sidebarMenu .sidebar-heading {
        padding: 8px 20px;
        margin: 0 8px;
        color: #333;
        font-weight: 600;
        font-size: 1rem;
        text-transform: uppercase;
    }

    .menu-list {
        padding-left: 0;
        margin-bottom: 0;
    }

    .nav-item {
        width: 100%;
    }

    @media (max-width: 768px) {
        #sidebarMenu {
            position: fixed;
            z-index: 1040;
            height: 100%;
            top: 0;
            left: 0;
            overflow-y: auto;
        }

        #sidebarMenu .nav-link {
            padding: 14px 20px;
        }
    }
</style>

<div class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse p-0" id="sidebarMenu">
    <div class="position-sticky pt-3">
        <ul class="nav flex-column menu-list list-unstyled">
            <c:if test="${not empty sessionScope.user}">
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
                                            <i class="fas fa-plus-circle me-2"></i> Create Purchase Request
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
