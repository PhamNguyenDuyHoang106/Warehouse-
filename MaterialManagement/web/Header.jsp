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

<!-- Header Styles - Không dùng thẻ <head> để tránh xung đột khi include -->
<!-- Lưu ý: Font Awesome cần được thêm vào phần <head> của từng trang -->
<!-- CSS này được load sau cùng để override tất cả CSS khác (style.css, vendor.css, etc.) -->
<!-- IMPORTANT: CSS này phải được load sau tất cả CSS khác để đảm bảo override -->
<style id="header-custom-styles">
        :root {
            --mm-primary: #0056b3;
            --mm-primary-light: #007bff;
            --mm-secondary: #E9B775;
            --mm-dark: #1f2937;
            --mm-muted: #6c757d;
            --mm-bg: #f5f7fb;
            --mm-card-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
            --mm-header-height: 160px;
        }

        body {
            font-family: "Segoe UI", "Inter", system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
            background: linear-gradient(145deg, #f8fafc 0%, #eef2ff 45%, #f5f7fb 100%);
            color: #1f2937;
            min-height: 100vh;
            padding-top: var(--mm-header-height, 160px);
        }

        .mm-sticky-header {
            position: fixed !important;
            top: 0 !important;
            left: 0 !important;
            right: 0 !important;
            width: 100% !important;
            z-index: 1300 !important;
            background: rgba(255, 255, 255, 0.95) !important;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08) !important;
            backdrop-filter: blur(10px);
        }

        .main-content-wrapper {
            display: flex;
            gap: 0;
            background: transparent;
        }

        .main-content-body {
            flex: 1;
            min-height: 100vh;
            background: rgba(255, 255, 255, 0.9);
            padding-bottom: 48px;
            margin-top: 0 !important;
        }

        .main-content-body::before {
            content: "";
            display: block;
            height: calc(var(--mm-header-height, 160px) + 20px);
        }
        .main-content-body .container-fluid {
            padding-left: 32px;
            padding-right: 32px;
        }

        .page-headline {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 24px;
        }

        .page-headline h1,
        .page-headline h2 {
            font-size: 1.75rem;
            font-weight: 700;
            color: #111827;
            margin: 0;
        }

        .section-card,
        .filter-card,
        .table-card,
        .info-card,
        .card {
            background: #ffffff;
            border-radius: 16px;
            box-shadow: var(--mm-card-shadow);
            border: 1px solid rgba(15, 23, 42, 0.05);
            padding: 24px;
            margin-bottom: 24px;
        }

        .section-card .section-title,
        .card-title,
        .table-card .section-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-label {
            font-weight: 600;
            color: #374151;
            font-size: 0.9rem;
        }

        .form-control,
        .form-select,
        input[type="text"],
        input[type="date"],
        input[type="number"],
        select,
        textarea {
            border-radius: 10px;
            border: 1px solid #e2e8f0;
            padding: 10px 14px;
            font-size: 0.95rem;
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .form-control:focus,
        .form-select:focus,
        input:focus,
        textarea:focus {
            border-color: var(--mm-primary);
            box-shadow: 0 0 0 3px rgba(0, 86, 179, 0.12);
            outline: none;
        }

        .btn,
        button.btn,
        a.btn {
            border-radius: 10px;
            font-weight: 600;
            padding: 12px 20px;
            transition: transform 0.15s ease, box-shadow 0.15s ease;
        }

        .btn:focus {
            box-shadow: 0 0 0 3px rgba(0, 86, 179, 0.25);
        }

        .btn-primary,
        .btn-gradient,
        .btn-create,
        .btn-add,
        .btn-search {
            background: linear-gradient(135deg, var(--mm-primary) 0%, var(--mm-primary-light) 100%);
            border: none;
            color: #ffffff;
            box-shadow: 0 12px 24px rgba(0, 86, 179, 0.25);
        }

        .btn-primary:hover,
        .btn-gradient:hover,
        .btn-create:hover,
        .btn-add:hover,
        .btn-search:hover {
            transform: translateY(-1px);
            box-shadow: 0 16px 30px rgba(0, 86, 179, 0.3);
            color: #ffffff;
        }

        .btn-outline-secondary {
            border-radius: 10px;
            padding: 12px 20px;
            border: 1px solid #cbd5f5;
            color: var(--mm-primary);
        }

        .btn-outline-secondary:hover {
            background: rgba(0, 86, 179, 0.08);
            color: var(--mm-primary);
        }

        .status-badge,
        .badge {
            border-radius: 999px;
            padding: 6px 16px;
            font-weight: 600;
            font-size: 0.75rem;
            text-transform: uppercase;
            letter-spacing: 0.04em;
        }

        .status-draft,
        .badge-secondary {
            background: #f3f4f6;
            color: #1f2937;
        }

        .status-completed,
        .status-confirmed,
        .badge-success {
            background: rgba(34, 197, 94, 0.15);
            color: #047857;
        }

        .status-pending,
        .status-pending_receipt,
        .status-partially_received {
            background: rgba(251, 191, 36, 0.18);
            color: #b45309;
        }

        .status-shipped,
        .status-delivered,
        .status-sent,
        .status-sent_to_supplier {
            background: rgba(59, 130, 246, 0.15);
            color: #1d4ed8;
        }

        .status-cancelled,
        .status-cancel,
        .status-rejected,
        .badge-danger {
            background: rgba(239, 68, 68, 0.15);
            color: #b91c1c;
        }

        .status-approved {
            background: rgba(16, 185, 129, 0.18);
            color: #047857;
        }

        .table-card {
            padding: 0;
        }

        .table-card table {
            margin: 0;
            border-collapse: separate;
            border-spacing: 0;
        }

        .table-card thead th,
        .table thead th {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: #ffffff;
            font-weight: 600;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            padding: 16px;
            border: none;
        }

        .table-card tbody td,
        .table tbody td {
            padding: 16px;
            border-bottom: 1px solid #eef2ff;
            vertical-align: middle;
            font-size: 0.95rem;
            color: #1f2937;
        }

        .table-card tbody tr:hover,
        .table tbody tr:hover {
            background: #f8fafc;
        }

        .empty-state {
            text-align: center;
            padding: 56px 16px;
            color: var(--mm-muted);
        }

        .empty-state i {
            font-size: 56px;
            margin-bottom: 16px;
            color: #cbd5f5;
        }

        .alert {
            border-radius: 12px;
            border: none;
            padding: 16px 20px;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
        }

        .alert i {
            margin-right: 8px;
        }

        .info-row {
            display: grid;
            grid-template-columns: 150px 1fr;
            gap: 16px;
            padding: 12px 0;
            border-bottom: 1px dashed rgba(15, 23, 42, 0.1);
        }

        .info-row:last-child {
            border-bottom: none;
        }

        .info-label {
            text-transform: uppercase;
            font-size: 0.75rem;
            color: #6b7280;
            letter-spacing: 0.05em;
        }

        .info-value {
            font-weight: 600;
            color: #111827;
        }

        .modal-content {
            border-radius: 24px;
            border: none;
            box-shadow: 0 20px 50px rgba(15, 23, 42, 0.12);
        }

        .modal-header {
            border-bottom: 1px solid rgba(15, 23, 42, 0.08);
            padding: 20px 24px;
        }

        .modal-footer {
            border-top: 1px solid rgba(15, 23, 42, 0.08);
            padding: 20px 24px;
        }

        .material-selection-item {
            border: 1px solid #e5e7eb;
            border-radius: 14px;
            padding: 16px;
            transition: border-color 0.2s ease, transform 0.2s ease;
        }

        .material-selection-item:hover {
            border-color: var(--mm-primary);
            transform: translateY(-2px);
        }

        .material-selection-item.selected {
            border-color: var(--mm-secondary);
            background: rgba(233, 183, 117, 0.1);
        }

        .pagination .page-item .page-link {
            border-radius: 10px;
            padding: 8px 14px;
            border: none;
            color: var(--mm-primary);
            margin: 0 4px;
        }

        .pagination .page-item.active .page-link {
            background: linear-gradient(135deg, var(--mm-primary) 0%, var(--mm-primary-light) 100%);
            color: #fff;
            box-shadow: 0 8px 18px rgba(0, 86, 179, 0.25);
        }

        .table-actions {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .btn-icon {
            width: 40px;
            height: 40px;
            border-radius: 12px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .text-muted-soft {
            color: #94a3b8;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            flex-wrap: wrap;
        }

        .quick-stat-card {
            background: linear-gradient(145deg, rgba(30, 58, 138, 0.08), rgba(37, 99, 235, 0.12));
            border: 1px solid rgba(37, 99, 235, 0.15);
            border-radius: 20px;
            padding: 20px;
            box-shadow: var(--mm-card-shadow);
        }

        .quick-stat-card h3 {
            font-size: 0.95rem;
            text-transform: uppercase;
            color: #1d4ed8;
            letter-spacing: 0.08em;
        }

        .quick-stat-card .stat-value {
            font-size: 2rem;
            font-weight: 700;
            color: #0f172a;
        }

        .quick-stat-card .stat-label {
            font-size: 0.85rem;
            color: #475569;
        }

        .table thead th:first-child,
        .table tbody td:first-child {
            border-top-left-radius: 16px;
            border-bottom-left-radius: 16px;
        }

        .table thead th:last-child,
        .table tbody td:last-child {
            border-top-right-radius: 16px;
            border-bottom-right-radius: 16px;
        }

        /* Modern Header Styles */
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
        
        /* Override tất cả CSS từ style.css, vendor.css, và các file khác với specificity cao nhất */
        html body header,
        body header,
        header {
            width: 100% !important;
            max-width: 100% !important;
            box-sizing: border-box !important;
            margin: 0 !important;
            padding: 0 !important;
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%) !important;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08) !important;
            position: sticky !important;
            top: 0 !important;
            z-index: 999 !important;
            transition: none !important;
            /* Override CSS từ Sidebar.jsp và các file khác */
            clear: none !important;
            /* Đảm bảo header có độ cao cố định - override tất cả CSS khác */
            min-height: auto !important;
            height: auto !important;
            /* Override biến CSS từ style.css nếu có sử dụng */
            --header-height: auto !important;
            --header-height-min: auto !important;
        }
        
        /* Ẩn các dropdown menu trong header vì đã có trong sidebar */
        header .filter-categories,
        header select.filter-categories {
            display: none !important;
        }
        
        /* Override với specificity cao nhất - Đảm bảo container-fluid có chiều cao và màu cố định */
        html body header .container-fluid,
        body header .container-fluid,
        header .container-fluid,
        html body header .container-fluid.py-2,
        body header .container-fluid.py-2,
        header .container-fluid.py-2,
        html body header div.container-fluid.py-2,
        body header div.container-fluid.py-2,
        header div.container-fluid.py-2 {
            width: 100% !important;
            max-width: 100% !important;
            padding-left: 30px !important;
            padding-right: 30px !important;
            margin-left: 0 !important;
            margin-right: 0 !important;
            box-sizing: border-box !important;
            /* Đảm bảo padding nhất quán - override Bootstrap py-2 và tất cả CSS khác */
            padding-top: 12px !important;
            padding-bottom: 12px !important;
            /* Đảm bảo chiều cao cố định - override tất cả CSS từ style.css, vendor.css */
            /* Không set height cố định, để nó tự động dựa trên nội dung, nhưng đảm bảo không bị CSS khác override */
            min-height: auto !important;
            height: auto !important;
            max-height: none !important;
            /* Override màu sắc từ các file CSS khác (style.css có thể set màu #41403E hoặc #212529) */
            background: transparent !important;
            background-color: transparent !important;
            color: inherit !important;
            /* Override tất cả CSS variables có thể ảnh hưởng từ style.css */
            --bs-body-color: inherit !important;
            --bs-body-line-height: normal !important;
            /* Đảm bảo không có CSS từ style.css ảnh hưởng đến line-height */
            line-height: normal !important;
        }
        
        /* Đảm bảo row và các element bên trong container-fluid không ảnh hưởng đến chiều cao */
        html body header .container-fluid.py-2 .row,
        body header .container-fluid.py-2 .row,
        header .container-fluid.py-2 .row {
            margin-top: 0 !important;
            margin-bottom: 0 !important;
            padding-top: 0 !important;
            padding-bottom: 0 !important;
            /* Đảm bảo row không có height cố định */
            min-height: auto !important;
            height: auto !important;
        }
        
        /* Đảm bảo các col bên trong không ảnh hưởng đến chiều cao */
        html body header .container-fluid.py-2 .row > [class*="col-"],
        body header .container-fluid.py-2 .row > [class*="col-"],
        header .container-fluid.py-2 .row > [class*="col-"] {
            margin-top: 0 !important;
            margin-bottom: 0 !important;
            padding-top: 0 !important;
            padding-bottom: 0 !important;
        }
        
        /* Đảm bảo các element p, a, button trong container-fluid không ảnh hưởng đến chiều cao */
        html body header .container-fluid.py-2 p,
        body header .container-fluid.py-2 p,
        header .container-fluid.py-2 p,
        html body header .container-fluid.py-2 a,
        body header .container-fluid.py-2 a,
        header .container-fluid.py-2 a,
        html body header .container-fluid.py-2 button,
        body header .container-fluid.py-2 button,
        header .container-fluid.py-2 button {
            margin-top: 0 !important;
            margin-bottom: 0 !important;
            /* Override line-height từ style.css (--bs-body-line-height: 2) */
            line-height: normal !important;
        }
        
        /* Đảm bảo user-name và user-email không ảnh hưởng đến chiều cao */
        html body header .container-fluid.py-2 .user-name,
        body header .container-fluid.py-2 .user-name,
        header .container-fluid.py-2 .user-name,
        html body header .container-fluid.py-2 .user-email,
        body header .container-fluid.py-2 .user-email,
        header .container-fluid.py-2 .user-email {
            margin: 0 !important;
            padding: 0 !important;
            line-height: normal !important;
        }
        
        /* Đảm bảo phần navigation có padding và height nhất quán - override với specificity cao */
        html body header nav.main-menu,
        body header nav.main-menu,
        header nav.main-menu {
            padding-top: 10px !important;
            padding-bottom: 10px !important;
            min-height: 60px !important;
            height: auto !important;
            margin: 0 !important;
            /* Đảm bảo navigation luôn có cùng độ cao dù có hay không có menu items */
            display: flex !important;
            visibility: visible !important;
            align-items: center !important;
        }
        
        /* Đảm bảo offcanvas-body có padding nhất quán */
        html body header .offcanvas-body,
        body header .offcanvas-body,
        header .offcanvas-body {
            padding-top: 10px !important;
            padding-bottom: 10px !important;
            min-height: 40px !important;
        }
        
        /* Nếu không có menu items, vẫn giữ độ cao */
        header nav.main-menu:empty {
            min-height: 60px !important;
        }
        
        /* Đảm bảo container-fluid chứa nav có padding nhất quán */
        /* Sử dụng selector tương thích thay vì :has() */
        html body header .container-fluid:not(.py-2),
        body header .container-fluid:not(.py-2),
        header .container-fluid:not(.py-2),
        header > .container-fluid:last-child {
            padding-top: 0 !important;
            padding-bottom: 0 !important;
            margin-top: 0 !important;
            margin-bottom: 0 !important;
        }
        
        /* Đảm bảo tất cả select trong nav có cùng style */
        html body header nav.main-menu select,
        body header nav.main-menu select,
        header nav.main-menu select {
            margin: 0 !important;
            vertical-align: middle !important;
        }
        
        /* Override tất cả CSS có thể ảnh hưởng đến header height */
        header * {
            box-sizing: border-box;
        }
        
        /* Đảm bảo hr trong header không ảnh hưởng đến height - override Bootstrap my-2 */
        html body header hr,
        body header hr,
        header hr {
            margin-top: 8px !important;
            margin-bottom: 8px !important;
            border: none !important;
            border-top: 1px solid #e9ecef !important;
            padding: 0 !important;
            height: 1px !important;
        }
        
        /* Override Bootstrap margin classes trong header */
        header .mb-3,
        header .mb-sm-0 {
            margin-bottom: 0 !important;
        }
        
        /* User Info Section */
        header .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 10px 0;
        }
        
        header .user-avatar {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #DEAD6F 0%, #cfa856 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            font-size: 18px;
            box-shadow: 0 2px 8px rgba(222, 173, 111, 0.3);
        }
        
        header .user-details {
            display: flex;
            flex-direction: column;
        }
        
        header .user-name {
            font-weight: 600;
            color: #333;
            font-size: 15px;
            margin: 0;
        }
        
        header .user-email {
            font-size: 13px;
            color: #6c757d;
            margin: 0;
        }
        
        /* Modern Navigation */
        header nav.main-menu {
            overflow-x: auto;
            overflow-y: hidden;
            -webkit-overflow-scrolling: touch;
            width: 100%;
            background: #ffffff;
            border-top: 1px solid #e9ecef;
        }
        
        /* Modern Dropdown Select */
        header .filter-categories {
            max-width: 100%;
            white-space: nowrap;
            box-sizing: border-box;
            min-width: 200px;
            padding: 10px 15px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
            font-size: 14px;
            font-weight: 500;
            color: #495057;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        }
        
        header .filter-categories:hover {
            border-color: #DEAD6F;
            background: linear-gradient(135deg, #ffffff 0%, #fff9f0 100%);
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(222, 173, 111, 0.15);
        }
        
        header .filter-categories:focus {
            outline: none;
            border-color: #DEAD6F;
            box-shadow: 0 0 0 3px rgba(222, 173, 111, 0.1);
        }
        
        /* Navbar Toggler */
        header .navbar-toggler {
            border: 2px solid #DEAD6F;
            border-radius: 8px;
            padding: 8px 12px;
            transition: all 0.3s ease;
        }
        
        header .navbar-toggler:hover {
            background: #DEAD6F;
            color: white;
        }
        
        /* Offcanvas Modern Style */
        header .offcanvas {
            background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
        }
        
        header .offcanvas-header {
            border-bottom: 2px solid #e9ecef;
            padding: 20px;
        }
        
        header .btn-close {
            background: #DEAD6F;
            opacity: 1;
            border-radius: 50%;
            width: 35px;
            height: 35px;
            transition: all 0.3s ease;
        }
        
        header .btn-close:hover {
            background: #cfa856;
            transform: rotate(90deg);
        }
        
        /* Nav Links */
        header .nav-link {
            color: #495057;
            font-weight: 500;
            padding: 10px 20px;
            border-radius: 8px;
            transition: all 0.3s ease;
            position: relative;
        }
        
        header .nav-link:hover {
            color: #DEAD6F;
            background: rgba(222, 173, 111, 0.1);
        }
        
        header .nav-link.active {
            color: #DEAD6F;
            background: rgba(222, 173, 111, 0.15);
            font-weight: 600;
        }
        
        header .nav-link.active::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 60%;
            height: 3px;
            background: #DEAD6F;
            border-radius: 2px;
        }
        
        /* Profile Icon */
        header .profile-icon {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #DEAD6F 0%, #cfa856 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            transition: all 0.3s ease;
            box-shadow: 0 2px 8px rgba(222, 173, 111, 0.3);
        }
        
        header .profile-icon:hover {
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(222, 173, 111, 0.4);
        }
        
        /* Buttons */
        header .btn {
            border-radius: 8px;
            font-weight: 500;
            padding: 8px 20px;
            transition: all 0.3s ease;
        }
        
        header .btn-outline-dark {
            border: 2px solid #495057;
            color: #495057;
        }
        
        header .btn-outline-dark:hover {
            background: #495057;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
        }
        
        /* Responsive */
        @media (max-width: 991px) {
            header .offcanvas-body {
                overflow-x: hidden;
                padding: 20px;
            }
            
            header .container-fluid {
                padding-left: 15px;
                padding-right: 15px;
            }
            
            header .filter-categories {
                width: 100%;
                margin-bottom: 15px;
            }
            
            header .user-info {
                flex-direction: column;
                text-align: center;
                gap: 10px;
            }
        }
        
        /* Đảm bảo row trong header có margin/padding nhất quán */
        html body header .row,
        body header .row,
        header .row {
            margin-left: calc(-0.5 * var(--bs-gutter-x, 0.75rem)) !important;
            margin-right: calc(-0.5 * var(--bs-gutter-x, 0.75rem)) !important;
            margin-top: 0 !important;
            margin-bottom: 0 !important;
            padding: 0 !important;
            /* Đảm bảo row không ảnh hưởng đến chiều cao của container-fluid */
            min-height: auto !important;
            height: auto !important;
        }
        
        /* Logo Animation */
        header img {
            transition: transform 0.3s ease;
        }
        
        header img:hover {
            transform: scale(1.05);
        }

        .header-avatar-container {
            display: inline-block;
            transition: transform 0.2s ease;
        }

        .header-avatar-container:hover {
            transform: scale(1.05);
        }

        .header-avatar {
            border-radius: 50%;
            width: 50px;
            height: 50px;
            object-fit: cover;
            border: 3px solid rgba(0, 86, 179, 0.3);
            transition: border-color 0.2s ease, box-shadow 0.2s ease;
        }

        .header-avatar-container:hover .header-avatar {
            border-color: rgba(0, 86, 179, 0.6);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }

        .header-actions {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .header-alert .btn {
            border-radius: 999px;
            padding: 0.4rem 0.8rem;
        }

        .header-alert .badge {
            position: absolute;
            top: -6px;
            right: -6px;
        }

        .quick-action-group {
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 0.5rem;
            margin-left: 1rem;
        }

        .quick-action-group .btn {
            border-radius: 999px;
            font-weight: 600;
            padding: 0.35rem 0.9rem;
        }

        @media (max-width: 992px) {
            .quick-action-group {
                width: 100%;
                margin-left: 0;
                margin-top: 0.75rem;
            }
        }
    </style>

<header class="mm-sticky-header">
    <c:set var="headerRoleId" value="${sessionScope.user != null ? sessionScope.user.roleId : 0}" />
    <c:set var="headerPendingTotal" value="${empty pendingRequestTotal ? 0 : pendingRequestTotal}" />
    <c:set var="headerNotifications" value="${pendingNotifications}" />
    <div class="container-fluid py-2">
        <div class="d-flex flex-column flex-lg-row align-items-center justify-content-between gap-3">
            <div class="d-flex align-items-center gap-3 header-brand">
                <a href="home">
                    <img src="images/AdminLogo.png" alt="logo" class="img-fluid" style="max-width: 180px;">
                </a>
            </div>
            <div class="d-flex flex-column flex-sm-row flex-wrap align-items-center justify-content-end gap-3 w-100">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="user-info d-flex align-items-center gap-2">
                            <a href="profile" class="header-avatar-container" title="View Profile">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.avatar}">
                                        <img src="${pageContext.request.contextPath}/images/profiles/${sessionScope.user.avatar}?t=${System.currentTimeMillis()}" 
                                             alt="avatar" 
                                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/profiles/default.jpg';"
                                             class="header-avatar">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/images/profiles/default.jpg" 
                                             alt="avatar" 
                                             class="header-avatar">
                                    </c:otherwise>
                                </c:choose>
                            </a>
                            <div class="user-details">
                                <p class="user-name mb-0">${sessionScope.user.fullName}</p>
                                <p class="user-email mb-0">${sessionScope.user.email}</p>
                            </div>
                        </div>
                        <div class="header-actions">
                            <div class="header-alert dropdown">
                                <button class="btn btn-outline-secondary position-relative" type="button" data-bs-toggle="dropdown">
                                    <i class="fa-regular fa-bell"></i>
                                    <c:if test="${headerPendingTotal > 0}">
                                        <span class="badge bg-danger rounded-pill">${headerPendingTotal}</span>
                                    </c:if>
                                </button>
                                <div class="dropdown-menu dropdown-menu-end p-3 header-notify-menu">
                                    <c:choose>
                                        <c:when test="${not empty headerNotifications}">
                                            <c:forEach var="notify" items="${headerNotifications}">
                                                <a class="dropdown-item d-flex flex-column mb-2 rounded-3" href="${notify.actionUrl}">
                                                    <strong>${notify.type}</strong>
                                                    <small class="text-muted">${notify.code} • ${notify.status}</small>
                                                </a>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="dropdown-item-text text-muted">Không có yêu cầu chờ xử lý</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <a href="logout" class="btn btn-outline-dark btn-sm">
                                <i class="fas fa-sign-out-alt me-1"></i>Logout
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="user-info">
                            <div class="user-avatar">
                                <i class="fas fa-user"></i>
                            </div>
                            <div class="user-details">
                                <p class="user-name mb-0">Guest</p>
                                <p class="user-email mb-0">guest@example.com</p>
                            </div>
                        </div>
                        <a href="Login.jsp" class="btn btn-outline-primary btn-sm">
                            <i class="fas fa-sign-in-alt me-1"></i>Login
                        </a>
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

                    <c:if test="${headerRoleId > 0}">
                        <div class="quick-action-group">
                            <c:if test="${headerRoleId == 1 || headerRoleId == 3}">
                                <a href="ImportMaterial" class="btn btn-outline-primary btn-sm">
                                    <i class="fa-solid fa-circle-arrow-down me-1"></i> Nhập kho
                                </a>
                                <a href="ExportMaterial" class="btn btn-outline-primary btn-sm">
                                    <i class="fa-solid fa-circle-arrow-up me-1"></i> Xuất kho
                                </a>
                            </c:if>
                            <c:if test="${headerRoleId == 1 || headerRoleId == 2 || headerRoleId == 4}">
                                <a href="PurchaseRequestForm" class="btn btn-outline-dark btn-sm">
                                    <i class="fa-solid fa-file-circle-plus me-1"></i> Đề nghị mua
                                </a>
                            </c:if>
                            <c:if test="${headerRoleId != 4}">
                                <a href="CreateRepairRequest" class="btn btn-outline-secondary btn-sm">
                                    <i class="fa-solid fa-screwdriver-wrench me-1"></i> Đơn sửa chữa
                                </a>
                            </c:if>
                            <c:if test="${headerRoleId == 4}">
                                <a href="CreateExportRequest" class="btn btn-outline-success btn-sm">
                                    <i class="fa-solid fa-paper-plane me-1"></i> Yêu cầu xuất
                                </a>
                            </c:if>
                        </div>
                    </c:if>

                    <div class="d-none d-lg-flex align-items-center gap-3">
                        <a href="home" class="btn btn-outline-primary btn-sm" title="Home">
                            <i class="fas fa-house me-1"></i> Home
                        </a>
                    </div>
                </div>
            </div>
        </nav>
    </div>
</header>

<script>
    (function () {
        let resizeTimer;
        function syncHeaderHeight() {
            const header = document.querySelector('.mm-sticky-header');
            if (!header) return;
            const height = header.offsetHeight;
            document.documentElement.style.setProperty('--mm-header-height', height + 'px');
        }
        window.addEventListener('load', syncHeaderHeight);
        window.addEventListener('resize', function () {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(syncHeaderHeight, 120);
        });
    })();
</script>

<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>
<script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
