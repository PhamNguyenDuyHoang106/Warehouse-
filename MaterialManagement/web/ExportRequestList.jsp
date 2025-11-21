<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Export Request Management</title>
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
        
        .status-cancel {
            background-color: #6f42c1;
            color: #fff;
        }
        
        .status-exported {
            background-color: #007bff;
            color: #fff;
        }
        
        .status-returned {
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
        
        .btn-edit {
            background-color: #e3f2fd;
            color: #1976d2;
            border: 1px solid #90caf9;
        }
        
        .btn-edit:hover {
            background-color: #90caf9;
            color: #0d47a1;
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
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <c:if test="${canViewExportRequest}">
                    <!-- Filter Card -->
                    <div class="filter-card">
                        <form method="get" action="ExportRequestList" class="row g-3">
                            <div class="col-md-3">
                                <label class="form-label">
                                    <i class="fas fa-barcode me-1"></i>Mã phiếu (Slip ID)
                                </label>
                                <input type="text" name="search" class="form-control" 
                                       placeholder="Nhập mã phiếu..." 
                                       value="${searchKeyword != null ? searchKeyword : ''}">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">
                                    <i class="fas fa-building me-1"></i>Nguồn nhận (Recipient Source)
                                </label>
                                <input type="text" class="form-control" placeholder="Nhập nguồn nhận...">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">
                                    <i class="fas fa-info-circle me-1"></i>Tình trạng (Status)
                                </label>
                                <select name="status" class="form-select">
                                    <option value="all" ${selectedStatus == null || selectedStatus == 'all' ? 'selected' : ''}>Tất cả</option>
                                    <option value="pending" ${selectedStatus == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                    <option value="approved" ${selectedStatus == 'approved' ? 'selected' : ''}>Đã duyệt</option>
                                    <option value="rejected" ${selectedStatus == 'rejected' ? 'selected' : ''}>Từ chối</option>
                                    <option value="cancel" ${selectedStatus == 'cancel' ? 'selected' : ''}>Hủy</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">
                                    <i class="fas fa-calendar me-1"></i>Từ ngày (From Date)
                                </label>
                                <input type="date" name="requestDateFrom" class="form-control" 
                                       value="${requestDateFrom != null ? requestDateFrom : ''}">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">
                                    <i class="fas fa-calendar me-1"></i>Đến ngày (To Date)
                                </label>
                                <input type="date" name="requestDateTo" class="form-control" 
                                       value="${requestDateTo != null ? requestDateTo : ''}">
                            </div>
                            <div class="col-md-12 d-flex justify-content-end gap-2 mt-3">
                                <button type="submit" class="btn btn-search">
                                    <i class="fas fa-search me-2"></i>Tìm kiếm
                                </button>
                                <a href="${pageContext.request.contextPath}/ExportRequestList" class="btn btn-outline-secondary" style="height: 42px; padding: 0 24px; display: flex; align-items: center;">
                                    <i class="fas fa-redo me-2"></i>Xóa bộ lọc
                                </a>
                            </div>
                        </form>
                    </div>
                    
                    <!-- Create Button -->
                    <c:if test="${hasCreateExportRequestPermission}">
                        <div class="d-flex justify-content-end">
                            <a href="CreateExportRequest" class="btn-create">
                                <i class="fas fa-plus"></i>
                                Tạo phiếu xuất kho
                            </a>
                        </div>
                    </c:if>
                    
                    <!-- Table Card -->
                    <c:if test="${not empty exportRequests}">
                        <div class="table-card">
                            <div class="table-responsive">
                                <table class="table mb-0">
                                    <thead>
                                        <tr>
                                            <th>STT</th>
                                            <th>Mã phiếu</th>
                                            <th>Nguồn nhận</th>
                                            <th>Giá trị</th>
                                            <th>Thời gian</th>
                                            <th>Tình trạng</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="request" items="${exportRequests}" varStatus="loop">
                                            <tr>
                                                <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                                <td><strong>${request.requestCode}</strong></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty request.userName}">
                                                            ${request.userName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            N/A
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>50.000.000</td>
                                                <td>${request.requestDate}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${request.status == 'approved'}">
                                                            <span class="status-badge status-approved">Đã duyệt</span>
                                                        </c:when>
                                                        <c:when test="${request.status == 'rejected'}">
                                                            <span class="status-badge status-rejected">Từ chối</span>
                                                        </c:when>
                                                        <c:when test="${request.status == 'cancel'}">
                                                            <span class="status-badge status-cancel">Hủy</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-pending">Chờ duyệt</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="d-flex gap-2">
                                                        <a href="${pageContext.request.contextPath}/ViewExportRequest?id=${request.exportRequestId}" 
                                                           class="btn-action btn-detail" title="Xem chi tiết">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <c:if test="${request.status == 'pending' && hasHandleRequestPermission}">
                                                            <button type="button" class="btn-action btn-approve" 
                                                                    onclick="setModalAction('approved', '${request.exportRequestId}')" 
                                                                    title="Duyệt">
                                                                <i class="fas fa-check"></i>
                                                            </button>
                                                            <button type="button" class="btn-action btn-reject" 
                                                                    onclick="setModalAction('rejected', '${request.exportRequestId}')" 
                                                                    title="Từ chối">
                                                                <i class="fas fa-times"></i>
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
                                           href="ExportRequestList?page=${currentPage - 1}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}" 
                                           aria-label="Previous">
                                            <span aria-hidden="true">« Previous</span>
                                        </a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" 
                                               href="ExportRequestList?page=${i}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}">${i}</a>
                                        </li>
                                    </c:forEach>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" 
                                           href="ExportRequestList?page=${currentPage + 1}&search=${searchKeyword}&status=${selectedStatus}&sortByName=${sortByName}&requestDateFrom=${requestDateFrom}&requestDateTo=${requestDateTo}" 
                                           aria-label="Next">
                                            <span aria-hidden="true">Next »</span>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </c:if>
                    
                    <c:if test="${empty exportRequests}">
                        <div class="table-card">
                            <div class="empty-state">
                                <i class="fas fa-inbox"></i>
                                <h5>Không có dữ liệu</h5>
                                <p>Không tìm thấy phiếu xuất kho nào.</p>
                            </div>
                        </div>
                    </c:if>
                </c:if>
                
                <c:if test="${!canViewExportRequest}">
                    <div class="alert alert-danger mt-4">Bạn không có quyền xem danh sách yêu cầu xuất.</div>
                </c:if>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->

<!-- Status Update Modal -->
<div class="modal fade" id="statusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Cập nhật trạng thái yêu cầu xuất</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form id="statusForm" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="requestId" id="modalRequestId">
                    <div class="mb-3" id="approvalReasonDiv" style="display: none;">
                        <label for="approvalReason" class="form-label">Lý do duyệt</label>
                        <textarea class="form-control" name="approvalReason" id="approvalReason" rows="3" placeholder="Nhập lý do duyệt..."></textarea>
                    </div>
                    <div class="mb-3" id="rejectionReasonDiv" style="display: none;">
                        <label for="rejectionReason" class="form-label">Lý do từ chối <span style="color:red">*</span></label>
                        <textarea class="form-control" name="rejectionReason" id="rejectionReason" rows="3" placeholder="Nhập lý do từ chối..." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function setModalAction(status, requestId) {
        document.getElementById('modalRequestId').value = requestId;
        
        const approvalReasonDiv = document.getElementById('approvalReasonDiv');
        const rejectionReasonDiv = document.getElementById('rejectionReasonDiv');
        const approvalReason = document.getElementById('approvalReason');
        const rejectionReason = document.getElementById('rejectionReason');
        const statusForm = document.getElementById('statusForm');
        
        // Clear previous values
        if (approvalReason) approvalReason.value = '';
        if (rejectionReason) rejectionReason.value = '';
        
        if (status === 'approved') {
            approvalReasonDiv.style.display = 'block';
            rejectionReasonDiv.style.display = 'none';
            if (rejectionReason) rejectionReason.removeAttribute('required');
            if (approvalReason) approvalReason.removeAttribute('required');
            statusForm.action = '${pageContext.request.contextPath}/ApproveExportRequest';
        } else if (status === 'rejected') {
            approvalReasonDiv.style.display = 'none';
            rejectionReasonDiv.style.display = 'block';
            if (rejectionReason) rejectionReason.setAttribute('required', 'required');
            if (approvalReason) approvalReason.removeAttribute('required');
            statusForm.action = '${pageContext.request.contextPath}/RejectExportRequest';
        }
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('statusModal'));
        modal.show();
    }
</script>
</body>
</html>
