<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Purchase Request Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
        }
        
        /* Filter Card - Card trắng ở trên */
        .filter-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 24px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }
        
        .filter-card .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            font-size: 14px;
        }
        
        .filter-card .form-control,
        .filter-card .form-select {
            height: 42px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
        }
        
        .filter-card .form-control:focus,
        .filter-card .form-select:focus {
            border-color: #0056b3;
            box-shadow: 0 0 0 0.2rem rgba(0, 86, 179, 0.15);
        }
        
        .btn-search {
            background: linear-gradient(135deg, #0056b3 0%, #007bff 100%);
            color: white;
            border: none;
            height: 42px;
            padding: 0 24px;
            border-radius: 6px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .btn-search:hover {
            background: linear-gradient(135deg, #004085 0%, #0056b3 100%);
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(0, 86, 179, 0.3);
        }
        
        /* Create Button - Nút lớn màu xanh */
        .btn-create {
            background: linear-gradient(135deg, #0056b3 0%, #007bff 100%);
            color: white;
            border: none;
            padding: 14px 32px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 16px;
            margin-bottom: 20px;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(0, 86, 179, 0.25);
        }
        
        .btn-create:hover {
            background: linear-gradient(135deg, #004085 0%, #0056b3 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(0, 86, 179, 0.35);
            color: white;
        }
        
        .btn-create i {
            font-size: 18px;
        }
        
        /* Table Card */
        .table-card {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            overflow: hidden;
        }
        
        .table-card table {
            margin-bottom: 0;
        }
        
        .table-card thead th {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: white;
            font-weight: 600;
            padding: 16px;
            border: none;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .table-card tbody td {
            padding: 16px;
            vertical-align: middle;
            border-bottom: 1px solid #e9ecef;
            font-size: 14px;
        }
        
        .table-card tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .table-card tbody tr:last-child td {
            border-bottom: none;
        }
        
        /* Status Badges */
        .status-badge {
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
            text-align: center;
            min-width: 90px;
        }
        
        .status-pending {
            background-color: #ffc107;
            color: #000;
        }
        
        .status-approved {
            background-color: #28a745;
            color: #fff;
        }
        
        .status-rejected {
            background-color: #dc3545;
            color: #fff;
        }
        
        .status-submitted {
            background-color: #6c757d;
            color: #fff;
        }
        
        .status-cancelled {
            background-color: #6f42c1;
            color: #fff;
        }
        
        /* Action Buttons */
        .btn-action {
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            border: none;
            cursor: pointer;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }
        
        .btn-detail {
            background-color: #fff7e6;
            color: #b8860b;
            border: 1px solid #ffe58f;
        }
        
        .btn-detail:hover {
            background-color: #ffe58f;
            color: #856404;
        }
        
        .btn-approve {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #81c784;
        }
        
        .btn-approve:hover {
            background-color: #81c784;
            color: #1b5e20;
        }
        
        .btn-reject {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #ef9a9a;
        }
        
        .btn-reject:hover {
            background-color: #ef9a9a;
            color: #b71c1c;
        }
        
        .btn-delete {
            background-color: #ffebee;
            color: #d32f2f;
            border: 1px solid #ef9a9a;
        }
        
        .btn-delete:hover {
            background-color: #ef9a9a;
            color: #b71c1c;
        }
        
        /* Pagination */
        .pagination .page-item.active .page-link {
            background: linear-gradient(135deg, #0056b3 0%, #007bff 100%);
            border-color: #0056b3;
            color: #fff;
        }
        
        .pagination .page-link {
            color: #0056b3;
            border-color: #dee2e6;
        }
        
        .pagination .page-link:hover {
            background-color: #e9ecef;
            border-color: #dee2e6;
        }
        
        /* Alert Messages */
        .alert {
            border-radius: 8px;
            border: none;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 64px;
            margin-bottom: 16px;
            opacity: 0.5;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

    <!-- Main Content Wrapper - Bao sidebar và body content -->
    <div class="main-content-wrapper">
      <!-- Sidebar - Nằm trong wrapper -->
      <div class="sidebar-wrapper-inner">
        <jsp:include page="Sidebar.jsp" />
      </div>
      
      <!-- Main Content Body - Nằm trong wrapper, bên cạnh sidebar -->
      <div class="main-content-body">
        <div class="container-fluid my-4" style="padding-left: 30px; padding-right: 30px;">
          <div class="row">
            <div class="col-12 px-md-4">
                <c:set var="roleId" value="${sessionScope.user.roleId}" />
                <c:set var="hasViewPurchaseRequestListPermission" value="${rolePermissionDAO.hasPermission(roleId, 'DS yêu cầu mua')}" scope="request" />
                <c:set var="hasDeletePurchaseRequestPermission" value="${rolePermissionDAO.hasPermission(roleId, 'Xóa PR')}" scope="request" />
                <c:set var="hasHandleRequestPermission" value="${rolePermissionDAO.hasPermission(roleId, 'Duyệt PR') || roleId == 1}" scope="request" />

                <c:if test="${empty sessionScope.user}">
                    <div class="alert alert-danger">Please log in to view purchase requests.</div>
                    <div class="text-center mt-3">
                        <a href="Login.jsp" class="btn btn-outline-secondary btn-lg rounded-1">Log In</a>
                    </div>
                </c:if>
                <c:if test="${not empty sessionScope.user}">
                    <c:if test="${!hasViewPurchaseRequestListPermission}">
                        <div class="alert alert-danger">You do not have permission to view purchase requests.</div>
                        <div class="text-center mt-3">
                            <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Dashboard</a>
                        </div>
                    </c:if>
                    <c:if test="${hasViewPurchaseRequestListPermission}">
                        <!-- Success/Error Messages -->
                        <c:if test="${not empty param.success}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check-circle me-2"></i>${param.success}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty param.error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i>${param.error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>
                        
                        <!-- Filter Card -->
                        <div class="filter-card">
                            <form method="GET" action="ListPurchaseRequests" class="row g-3">
                                <div class="col-md-3">
                                    <label class="form-label">
                                        <i class="fas fa-barcode me-1"></i>Mã phiếu (Request Code)
                                    </label>
                                    <input type="text" class="form-control" name="keyword" 
                                           placeholder="Nhập mã phiếu..." 
                                           value="${keyword != null ? keyword : ''}">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">
                                        <i class="fas fa-info-circle me-1"></i>Tình trạng (Status)
                                    </label>
                                    <select class="form-select" name="status">
                                        <option value="">Tất cả</option>
                                        <option value="submitted" ${status == 'submitted' ? 'selected' : ''}>Submitted</option>
                                        <option value="approved" ${status == 'approved' ? 'selected' : ''}>Approved</option>
                                        <option value="rejected" ${status == 'rejected' ? 'selected' : ''}>Rejected</option>
                                        <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">
                                        <i class="fas fa-sort me-1"></i>Sắp xếp (Sort)
                                    </label>
                                    <select class="form-select" name="sort">
                                        <option value="" ${sortOption == null || sortOption == '' ? 'selected' : ''}>Mới nhất</option>
                                        <option value="date_asc" ${sortOption == 'date_asc' ? 'selected' : ''}>Cũ nhất</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">
                                        <i class="fas fa-calendar me-1"></i>Từ ngày (From Date)
                                    </label>
                                    <input type="date" class="form-control" name="startDate" 
                                           value="${startDate != null ? startDate : ''}">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">
                                        <i class="fas fa-calendar me-1"></i>Đến ngày (To Date)
                                    </label>
                                    <input type="date" class="form-control" name="endDate" 
                                           value="${endDate != null ? endDate : ''}">
                                </div>
                                <div class="col-md-1 d-flex align-items-end gap-2">
                                    <button type="submit" class="btn btn-search flex-grow-1">
                                        <i class="fas fa-search"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/ListPurchaseRequests" class="btn btn-outline-secondary" style="height: 42px; padding: 0 16px; display: flex; align-items: center;" title="Reset">
                                        <i class="fas fa-redo"></i>
                                    </a>
                                </div>
                            </form>
                        </div>
                        
                        <!-- Create Button -->
                        <c:if test="${hasCreatePurchaseRequestPermission}">
                            <div class="d-flex justify-content-end">
                                <a href="CreatePurchaseRequest" class="btn-create">
                                    <i class="fas fa-plus"></i>
                                    Tạo yêu cầu mua hàng
                                </a>
                            </div>
                        </c:if>
                        
                        <!-- Table Card -->
                        <c:if test="${not empty purchaseRequests}">
                            <div class="table-card">
                                <div class="table-responsive">
                                    <table class="table mb-0">
                                        <thead>
                                            <tr>
                                                <th>STT</th>
                                                <th>Mã phiếu</th>
                                                <th>Người yêu cầu</th>
                                                <th>Ngày yêu cầu</th>
                                                <th>Lý do</th>
                                                <th>Tình trạng</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${purchaseRequests}" var="request" varStatus="loop">
                                                <tr>
                                                    <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                                    <td><strong>${request.requestCode}</strong></td>
                                                    <td>${userIdToName[request.requestBy]}</td>
                                                    <td>${request.requestDate}</td>
                                                    <td>${request.reason}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${fn:toLowerCase(request.status) == 'approved'}">
                                                                <span class="status-badge status-approved">Đã duyệt</span>
                                                            </c:when>
                                                            <c:when test="${fn:toLowerCase(request.status) == 'rejected'}">
                                                                <span class="status-badge status-rejected">Từ chối</span>
                                                            </c:when>
                                                            <c:when test="${fn:toLowerCase(request.status) == 'submitted'}">
                                                                <span class="status-badge status-submitted">Đã gửi</span>
                                                            </c:when>
                                                            <c:when test="${fn:toLowerCase(request.status) == 'cancelled'}">
                                                                <span class="status-badge status-cancelled">Đã hủy</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="status-badge status-pending">${fn:toUpperCase(request.status)}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="d-flex gap-2">
                                                            <a href="PurchaseRequestDetail?id=${request.purchaseRequestId}" 
                                                               class="btn-action btn-detail" title="Xem chi tiết">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <c:if test="${fn:toLowerCase(request.status) == 'submitted' && hasHandleRequestPermission}">
                                                                <button type="button" class="btn-action btn-approve" 
                                                                        onclick="setModalAction('approved', '${request.purchaseRequestId}')" 
                                                                        title="Duyệt">
                                                                    <i class="fas fa-check"></i>
                                                                </button>
                                                                <button type="button" class="btn-action btn-reject" 
                                                                        onclick="setModalAction('rejected', '${request.purchaseRequestId}')" 
                                                                        title="Từ chối">
                                                                    <i class="fas fa-times"></i>
                                                                </button>
                                                            </c:if>
                                                            <c:if test="${fn:toLowerCase(request.status) == 'submitted' && request.requestBy == sessionScope.user.userId && hasDeletePurchaseRequestPermission}">
                                                                <button onclick="deleteRequest(${request.purchaseRequestId})" 
                                                                        class="btn-action btn-delete" 
                                                                        title="Xóa">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <!-- Pagination -->
                            <c:if test="${totalPages > 0}">
                                <nav aria-label="Page navigation" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" 
                                               href="ListPurchaseRequests?page=${currentPage - 1}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">Previous</a>
                                        </li>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" 
                                                   href="ListPurchaseRequests?page=${i}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" 
                                               href="ListPurchaseRequests?page=${currentPage + 1}&status=${status}&keyword=${keyword}&sort=${sortOption}&startDate=${startDate}&endDate=${endDate}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </c:if>
                        
                        <c:if test="${empty purchaseRequests}">
                            <div class="table-card">
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <h5>Không có dữ liệu</h5>
                                    <p>Không tìm thấy yêu cầu mua hàng nào.</p>
                                </div>
                            </div>
                        </c:if>
                    </c:if>
                </c:if>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->

<!-- Status Update Modal -->
<div class="modal fade" id="updateStatusModal" tabindex="-1" aria-labelledby="updateStatusModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="PurchaseRequestDetail" method="post" id="updateStatusForm">
                <div class="modal-header">
                    <h5 class="modal-title" id="updateStatusModalLabel">Cập nhật trạng thái yêu cầu mua hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="id" id="modalRequestId" value="" />
                    <input type="hidden" id="modalStatus" name="modalStatus" value="" />
                    <div class="mb-3">
                        <label for="modalReason" class="form-label" id="reasonLabel">Ghi chú quyết định <span style="color:red">*</span></label>
                        <textarea class="form-control" id="modalReason" name="reason" rows="3" placeholder="Nhập lý do..." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" id="updateStatusBtn" class="btn btn-primary">Xác nhận</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function deleteRequest(id) {
        if (confirm('Bạn có chắc chắn muốn xóa yêu cầu này?')) {
            window.location.href = 'DeletePurchaseRequest?id=' + id;
        }
    }
    
    function setModalAction(status, requestId) {
        document.getElementById('modalRequestId').value = requestId;
        document.getElementById('modalStatus').value = status;
        document.getElementById('reasonLabel').innerText = status === 'approved' ? 'Ghi chú duyệt *' : 'Ghi chú từ chối *';
        document.getElementById('modalReason').value = '';
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
        modal.show();
    }
    
    document.getElementById('updateStatusForm').addEventListener('submit', function(e) {
        if (!document.getElementById('modalReason').value.trim()) {
            e.preventDefault();
            alert('Vui lòng nhập lý do');
            document.getElementById('modalReason').focus();
        }
    });
</script>
</body>
</html>
