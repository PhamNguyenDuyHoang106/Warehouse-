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
            <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST') 
                      || sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')}">
                <c:if test="${sessionScope.userPermissions.contains('CREATE_EXPORT_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center ps-4" href="${pageContext.request.contextPath}/CreateExportRequest">
                            <i class="fas fa-plus-square fs-5 me-3"></i>
                            New Export Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_REPAIR_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center ps-4" href="${pageContext.request.contextPath}/repairrequest">
                            <i class="fas fa-tools fs-5 me-3"></i>
                            New Repair Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_REQUEST')}">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center ps-4" href="${pageContext.request.contextPath}/CreatePurchaseRequest">
                            <i class="fas fa-cart-plus fs-5 me-3"></i>
                            New Purchase Request
                        </a>
                    </li>
                </c:if>

                <c:if test="${sessionScope.userPermissions.contains('CREATE_PURCHASE_ORDER')} ps-4">
                    <li class="nav-item mb-2">
                        <a class="nav-link text-uppercase secondary-font d-flex align-items-center" href="${pageContext.request.contextPath}/CreatePurchaseOrder">
                            <i class="fas fa-file-import fs-5 me-3"></i>
                            New Purchase Order
                        </a>
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
            if (currentPath.includes(href.split('/').pop())) {
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