<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Export Request</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
        }
        
        /* Modal-like Form Container */
        .export-form-container {
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            margin: 2rem auto;
            max-width: 1400px;
            overflow: hidden;
            position: relative;
        }
        
        /* Blue Header with Title and Close Button */
        .export-form-header {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: white;
            padding: 20px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 4px solid #0056b3;
        }
        
        .export-form-header h2 {
            margin: 0;
            font-size: 1.75rem;
            font-weight: 600;
        }
        
        .export-form-close {
            background: transparent;
            border: none;
            color: white;
            font-size: 1.5rem;
            cursor: pointer;
            padding: 5px 10px;
            border-radius: 4px;
            transition: background 0.2s;
        }
        
        .export-form-close:hover {
            background: rgba(255, 255, 255, 0.2);
        }
        
        /* Form Body */
        .export-form-body {
            padding: 30px;
        }
        
        /* General Information Section */
        .info-section {
            margin-bottom: 30px;
        }
        
        .info-section-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e9ecef;
        }
        
        .info-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 15px;
        }
        
        .info-item {
            display: flex;
            flex-direction: column;
        }
        
        .info-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 5px;
            font-size: 0.9rem;
        }
        
        .info-value {
            color: #333;
            font-size: 0.95rem;
        }
        
        .export-form .form-control, 
        .export-form .form-select {
            height: 42px;
            font-size: 0.95rem;
            border: 1px solid #ddd;
            border-radius: 6px;
        }
        
        .export-form .form-control:focus,
        .export-form .form-select:focus {
            border-color: #0056b3;
            box-shadow: 0 0 0 0.2rem rgba(0, 86, 179, 0.15);
        }
        
        .export-form .form-label {
            font-size: 0.9rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 0.5rem;
        }
        
        /* Materials Table */
        .materials-table-section {
            margin-top: 30px;
        }
        
        .materials-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }
        
        .materials-table thead {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: white;
        }
        
        .materials-table thead th {
            padding: 16px;
            font-weight: 600;
            font-size: 0.9rem;
            text-align: left;
            border: none;
        }
        
        .materials-table tbody td {
            padding: 12px 16px;
            border-bottom: 1px solid #e9ecef;
            vertical-align: middle;
        }
        
        .materials-table tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .materials-table tbody tr:last-child td {
            border-bottom: none;
        }
        
        .material-image-cell {
            width: 60px;
            height: 60px;
            padding: 8px !important;
        }
        
        .material-image-cell img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #dee2e6;
        }
        
        .material-input-cell input,
        .material-input-cell select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 0.9rem;
        }
        
        .material-input-cell input:focus,
        .material-input-cell select:focus {
            border-color: #0056b3;
            outline: none;
        }
        
        .total-row {
            background: #f8f9fa;
            font-weight: 600;
        }
        
        .total-row td {
            padding: 16px !important;
            font-size: 1.1rem;
        }
        
        /* Action Buttons */
        .export-form-actions {
            margin-top: 30px;
            display: flex;
            justify-content: space-between;
            gap: 15px;
        }
        
        .btn-submit {
            background: linear-gradient(135deg, #0056b3 0%, #007bff 100%);
            color: white;
            border: none;
            padding: 12px 32px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1rem;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(0, 86, 179, 0.25);
        }
        
        .btn-submit:hover {
            background: linear-gradient(135deg, #004085 0%, #0056b3 100%);
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(0, 86, 179, 0.35);
            color: white;
        }
        
        .material-warehouse-cell {
            min-width: 220px;
        }
        
        .warehouse-stock-info {
            font-size: 0.78rem;
            color: #6c757d;
            margin-top: 4px;
        }
        
        .price-hint {
            font-size: 0.78rem;
            color: #6c757d;
            margin-top: 4px;
        }
        
        .price-warning {
            color: #dc3545;
            font-weight: 600;
        }

        .material-warehouse-cell {
            min-width: 220px;
        }

        .warehouse-stock-info {
            font-size: 0.78rem;
            color: #6c757d;
            margin-top: 4px;
        }

        .price-hint {
            font-size: 0.78rem;
            color: #6c757d;
            margin-top: 4px;
        }

        .price-warning {
            color: #dc3545;
            font-weight: 600;
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
            border: none;
            padding: 12px 32px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1rem;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }
        
        .btn-back:hover {
            background: #5a6268;
            color: white;
            transform: translateY(-2px);
        }
        
        .btn-add-material {
            background: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.9rem;
            transition: all 0.2s ease;
        }
        
        .btn-add-material:hover {
            background: #218838;
            color: white;
        }
        
        .btn-select-multiple {
            background: linear-gradient(135deg, #0056b3 0%, #007bff 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 600;
            font-size: 0.9rem;
            transition: all 0.2s ease;
        }
        
        .btn-select-multiple:hover {
            background: linear-gradient(135deg, #004085 0%, #0056b3 100%);
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 86, 179, 0.25);
        }
        
        .btn-remove-material {
            background: #dc3545;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 0.85rem;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .btn-remove-material:hover {
            background: #c82333;
            color: white;
        }
        
        /* Autocomplete Styles */
        .ui-autocomplete {
            background: #fff;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            max-height: 300px;
            overflow-y: auto;
            box-shadow: 0 4px 16px rgba(0,0,0,0.08);
            z-index: 9999 !important;
            font-size: 1rem;
            padding: 4px 0;
        }
        
        .ui-menu-item {
            border-bottom: 1px solid #f0f0f0;
        }
        
        .ui-menu-item:last-child {
            border-bottom: none;
        }
        
        .ui-menu-item-wrapper {
            padding: 0 !important;
            cursor: pointer;
            display: flex !important;
            align-items: center;
            gap: 12px;
            padding: 8px 12px !important;
            transition: all 0.2s ease;
        }
        
        .ui-menu-item-wrapper:hover,
        .ui-menu-item-wrapper.ui-state-active {
            background: #f0f4fa !important;
            color: #0d6efd !important;
            border: none !important;
            margin: 0 !important;
        }
        
        .autocomplete-img {
            width: 45px;
            height: 45px;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #e0e0e0;
            flex-shrink: 0;
        }
        
        .autocomplete-info {
            display: flex;
            flex-direction: column;
            flex-grow: 1;
            min-width: 0;
        }
        
        .autocomplete-name {
            font-weight: 600;
            color: #333;
            font-size: 0.95rem;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .autocomplete-code {
            font-size: 0.8rem;
            color: #6c757d;
        }
        
        .ui-menu-item-wrapper.ui-state-active .autocomplete-name {
            color: #0d6efd;
        }
        
        /* Alert Messages */
        .alert {
            border-radius: 8px;
            border: none;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        
        /* Material Selection Modal */
        .material-selection-modal {
            z-index: 10000;
        }
        
        .material-selection-modal .modal-dialog {
            max-width: 1200px;
        }
        
        .material-selection-modal .modal-header {
            background: linear-gradient(135deg, #1e3a8a 0%, #2563eb 100%);
            color: white;
            border-bottom: none;
        }
        
        .material-selection-modal .modal-title {
            font-weight: 600;
            font-size: 1.25rem;
        }
        
        .material-selection-modal .modal-body {
            max-height: 600px;
            overflow-y: auto;
            padding: 20px;
        }
        
        .material-search-box {
            margin-bottom: 20px;
        }
        
        .material-search-box input {
            height: 45px;
            border-radius: 8px;
            border: 1px solid #ddd;
            padding: 10px 15px;
            font-size: 0.95rem;
        }
        
        .material-selection-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 15px;
        }
        
        .material-selection-item {
            border: 2px solid #e9ecef;
            border-radius: 8px;
            padding: 15px;
            cursor: pointer;
            transition: all 0.2s ease;
            background: white;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .material-selection-item:hover {
            border-color: #0056b3;
            box-shadow: 0 2px 8px rgba(0, 86, 179, 0.15);
        }
        
        .material-selection-item.selected {
            border-color: #0056b3;
            background: #f0f4fa;
        }
        
        .material-selection-item input[type="checkbox"] {
            width: 20px;
            height: 20px;
            cursor: pointer;
            margin: 0;
        }
        
        .material-selection-item img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 6px;
            border: 1px solid #dee2e6;
        }
        
        .material-selection-item-info {
            flex: 1;
            min-width: 0;
        }
        
        .material-selection-item-name {
            font-weight: 600;
            color: #333;
            font-size: 0.95rem;
            margin-bottom: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .material-selection-item-code {
            font-size: 0.85rem;
            color: #6c757d;
        }
        
        .material-selection-item-unit {
            font-size: 0.8rem;
            color: #999;
            margin-top: 2px;
        }
        
        .material-selection-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            border-top: 1px solid #e9ecef;
            background: #f8f9fa;
        }
        
        .selected-count {
            font-weight: 600;
            color: #0056b3;
        }
        
        .material-selection-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn-select-all {
            background: #6c757d;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 6px;
            font-weight: 500;
            font-size: 0.9rem;
        }
        
        .btn-select-all:hover {
            background: #5a6268;
            color: white;
        }
        
        .material-pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 20px;
        }
        
        .material-pagination button {
            padding: 8px 16px;
            border: 1px solid #ddd;
            background: white;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .material-pagination button:hover:not(:disabled) {
            background: #0056b3;
            color: white;
            border-color: #0056b3;
        }
        
        .material-pagination button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .material-pagination .page-info {
            font-weight: 600;
            color: #495057;
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
            <div class="col-12">
                <!-- Modal-like Form Container -->
                <div class="export-form-container">
                    <!-- Header with Title and Close Button -->
                    <div class="export-form-header">
                        <h2><i class="fas fa-file-export me-2"></i>PHIẾU XUẤT KHO</h2>
                        <a href="ExportRequestList" class="export-form-close" title="Đóng">
                            <i class="fas fa-times"></i>
                        </a>
                    </div>
                    
                    <!-- Form Body -->
                    <div class="export-form-body">
                                <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                            </div>
                                </c:if>
                                <c:if test="${not empty success}">
                            <div class="alert alert-success">
                                <i class="fas fa-check-circle me-2"></i>${success}
                            </div>
                                </c:if>
                                <c:if test="${not empty errors}">
                            <div class="alert alert-danger">
                                        <ul style="margin-bottom: 0;">
                                            <c:forEach var="error" items="${errors}">
                                                <li>${error.value}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>

                        <form action="CreateExportRequest" method="post" class="export-form">
                            <!-- General Information Section -->
                            <div class="info-section">
                                <div class="info-section-title">
                                    <i class="fas fa-info-circle me-2"></i>Thông tin chung
                                </div>
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Nguồn xuất (Source)</label>
                                            <select class="form-select" id="customerId" name="customerId" required>
                                                    <option value="">-- Chọn khách hàng --</option>
                                                <c:forEach var="customer" items="${customers}">
                                                    <option value="${customer.customerId}" ${submittedCustomerId == customer.customerId ? 'selected' : ''}>
                                                            ${customer.customerName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty errors.customerId}">
                                                <div class="text-danger small mt-1">${errors.customerId}</div>
                                            </c:if>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Mã nguồn (Source Code)</label>
                                                <input type="text" class="form-control" id="customerCode" readonly>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Số điện thoại (Phone)</label>
                                                <input type="text" class="form-control" id="customerPhone" readonly>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Địa chỉ (Address)</label>
                                                <input type="text" class="form-control" id="customerAddress" readonly>
                                            </div>
                                        </div>
                                        </div>
                                        <div class="col-md-6">
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Mã phiếu (Receipt Code)</label>
                                                <input type="text" class="form-control" id="requestCode" name="requestCode" value="${requestCode}" readonly>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Xuất tại kho (Warehouse)</label>
                                                <input type="text" class="form-control" value="Kho tổng" readonly>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Mã kho (Warehouse Code)</label>
                                                <input type="text" class="form-control" value="KT_5467" readonly>
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Lý do (Reason)</label>
                                                <textarea class="form-control" id="reason" name="reason" rows="2" placeholder="Nhập lý do xuất kho...">${submittedReason}</textarea>
                                            <c:if test="${not empty errors.reason}">
                                                <div class="text-danger small mt-1">${errors.reason}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                        <div class="info-row">
                                            <div class="info-item">
                                                <label class="info-label">Ngày giao (Delivery Date)</label>
                                                <input type="date" class="form-control" id="deliveryDate" name="deliveryDate" value="${submittedDeliveryDate}">
                                                <c:if test="${not empty errors.deliveryDate}">
                                                    <div class="text-danger small mt-1">${errors.deliveryDate}</div>
                                                </c:if>
                                            </div>
                                            </div>
                                            </div>
                                            </div>
                                            </div>
                            
                            <!-- Materials Table Section -->
                            <div class="materials-table-section">
                                <table class="materials-table">
                                    <thead>
                                        <tr>
                                            <th style="width: 50px;">STT</th>
                                            <th style="width: 60px;">Hình ảnh</th>
                                            <th>Tên hàng hóa</th>
                                            <th>Mã hàng</th>
                                            <th>Đơn vị tính</th>
                                            <th>Kho xuất</th>
                                            <th>Đơn giá</th>
                                            <th>Số lượng</th>
                                            <th>Chiết khấu</th>
                                            <th>Thành tiền</th>
                                            <th style="width: 80px;">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody id="materialList">
                                        <!-- Materials will be added here dynamically -->
                                    </tbody>
                                    <tfoot>
                                        <tr class="total-row">
                                            <td colspan="9" class="text-end"><strong>Tổng</strong></td>
                                            <td class="text-end" id="totalAmount"><strong>0</strong></td>
                                            <td></td>
                                        </tr>
                                    </tfoot>
                                </table>
                                
                                <div class="d-flex gap-2 mt-3">
                                    <button type="button" class="btn-add-material" id="addMaterial">
                                        <i class="fas fa-plus me-2"></i>Thêm hàng hóa
                                    </button>
                                    <button type="button" class="btn-select-multiple" id="selectMultipleMaterials">
                                        <i class="fas fa-check-square me-2"></i>Chọn nhiều sản phẩm
                                    </button>
                                            </div>
                                        </div>

                            <!-- Action Buttons -->
                            <div class="export-form-actions">
                                <a href="ExportRequestList" class="btn-back">
                                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                                </a>
                                <button type="submit" class="btn-submit">
                                    <i class="fas fa-save me-2"></i>Lưu phiếu xuất kho
                                </button>
                                    </div>
                        </form>
                    </div>
                </div>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->
    
    <!-- Material Selection Modal -->
    <div class="modal fade material-selection-modal" id="materialSelectionModal" tabindex="-1" aria-labelledby="materialSelectionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="materialSelectionModalLabel">
                        <i class="fas fa-check-square me-2"></i>Chọn nhiều sản phẩm
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- Search Box -->
                    <div class="material-search-box">
                        <input type="text" class="form-control" id="materialSearchInput" placeholder="Tìm kiếm sản phẩm theo tên hoặc mã...">
                                    </div>

                    <!-- Material Grid -->
                    <div class="material-selection-grid" id="materialSelectionGrid">
                        <!-- Materials will be loaded here -->
                                    </div>
                    
                    <!-- Pagination -->
                    <div class="material-pagination" id="materialPagination" style="display: none;">
                        <button type="button" id="prevPageBtn" disabled>
                            <i class="fas fa-chevron-left"></i> Trước
                        </button>
                        <span class="page-info" id="pageInfo">Trang 1 / 1</span>
                        <button type="button" id="nextPageBtn" disabled>
                            Sau <i class="fas fa-chevron-right"></i>
                        </button>
                            </div>
                        </div>
                <div class="material-selection-footer">
                    <div>
                        <span class="selected-count" id="selectedCount">Đã chọn: 0 sản phẩm</span>
                    </div>
                    <div class="material-selection-actions">
                        <button type="button" class="btn-select-all" id="selectAllBtn">
                            <i class="fas fa-check-double me-1"></i>Chọn tất cả
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-1"></i>Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="addSelectedMaterialsBtn" style="background: linear-gradient(135deg, #0056b3 0%, #007bff 100%); border: none;">
                            <i class="fas fa-plus me-1"></i>Thêm đã chọn
                        </button>
            </div>
          </div>
        </div>
        </div>
    </div>
    
    <script src="js/jquery-1.11.0.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const contextPath = '${pageContext.request.contextPath}';

        const warehouses = [
        <c:if test="${not empty warehouses}">
            <c:forEach var="warehouse" items="${warehouses}" varStatus="loop">
                { id: ${warehouse.warehouseId}, code: '${fn:escapeXml(warehouse.warehouseCode)}', name: '${fn:escapeXml(warehouse.warehouseName)}' }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        </c:if>
        ];

        const materialAvailability = {
        <c:if test="${not empty materialAvailability}">
            <c:forEach var="entry" items="${materialAvailability}" varStatus="loop">
                '${entry.key}': [
                    <c:forEach var="stock" items="${entry.value}" varStatus="inner">
                        { warehouseId: ${stock.warehouseId}, warehouseCode: '${fn:escapeXml(stock.warehouseCode)}', warehouseName: '${fn:escapeXml(stock.warehouseName)}', availableStock: ${stock.availableStock} }<c:if test="${!inner.last}">,</c:if>
                    </c:forEach>
                ]<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        </c:if>
        };

        const materialPricing = {
        <c:if test="${not empty materialPricing}">
            <c:forEach var="entry" items="${materialPricing}" varStatus="loop">
                '${entry.key}': {
                    averageCost: ${entry.value.averageCost == null ? 0 : entry.value.averageCost},
                    lastExportPrice: ${entry.value.lastExportPrice == null ? 0 : entry.value.lastExportPrice},
                    suggestedPrice: ${entry.value.suggestedPrice == null ? 0 : entry.value.suggestedPrice},
                    minPrice: ${entry.value.minPrice == null ? 0 : entry.value.minPrice},
                    maxPrice: ${entry.value.maxPrice == null ? 0 : entry.value.maxPrice}
                }<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        </c:if>
        };

        function resolveMediaUrl(url) {
            if (!url || url === 'null') {
                return `${contextPath}/images/material/default.jpg`;
            }
            if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/')) {
                return url;
            }
            return `${contextPath}/${url}`;
        }

        function formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN').format(amount);
        }

        function calculateRowTotal(row) {
            const quantity = parseFloat(row.querySelector('.quantity-input').value) || 0;
            const unitPrice = parseFloat(row.querySelector('.unit-price-input').value) || 0;
            const discount = parseFloat(row.querySelector('.discount-input').value) || 0;
            
            const subtotal = quantity * unitPrice;
            const discountAmount = subtotal * (discount / 100);
            const total = subtotal - discountAmount;
            
            const amountCell = row.querySelector('.material-amount-cell');
            amountCell.textContent = formatCurrency(total);
            
            calculateGrandTotal();
        }

        function calculateGrandTotal() {
            const rows = document.querySelectorAll('.material-row');
            let grandTotal = 0;
            
            rows.forEach(row => {
                const amountText = row.querySelector('.material-amount-cell').textContent.replace(/\./g, '').replace(/,/g, '');
                const amount = parseFloat(amountText) || 0;
                grandTotal += amount;
            });
            
            document.getElementById('totalAmount').innerHTML = '<strong>' + formatCurrency(grandTotal) + '</strong>';
        }

        function formatNumber(value) {
            return new Intl.NumberFormat('vi-VN').format(value || 0);
        }

        function populateWarehouseOptions(row, materialId, selectedWarehouseId) {
            const select = row.querySelector('.warehouse-select');
            const stockInfo = row.querySelector('.warehouse-stock-info');
            if (!select) return;

            select.innerHTML = '<option value=\"\">-- Chọn kho --</option>';
            select.disabled = true;

            if (!materialId) {
                if (stockInfo) {
                    stockInfo.textContent = 'Chưa chọn hàng hóa';
                }
                return;
            }

            const availability = materialAvailability[String(materialId)] || [];
            const sources = availability.length ? availability : warehouses.map(w => ({
                warehouseId: w.id,
                warehouseCode: w.code,
                warehouseName: w.name,
                availableStock: 0
            }));

            sources.forEach(info => {
                const option = document.createElement('option');
                option.value = info.warehouseId;
                option.dataset.available = info.availableStock || 0;
                const labelCode = info.warehouseCode ? (info.warehouseCode + ' - ') : '';
                option.textContent = labelCode + info.warehouseName + ' (Còn: ' + formatNumber(info.availableStock || 0) + ')';
                select.appendChild(option);
            });

            select.disabled = false;
            if (selectedWarehouseId) {
                select.value = selectedWarehouseId;
            }
            updateWarehouseInfo(select);
        }

        function updateWarehouseInfo(select) {
            if (!select) return;
            const infoDiv = select.closest('.material-warehouse-cell').querySelector('.warehouse-stock-info');
            if (!select.value) {
                if (infoDiv) {
                    infoDiv.textContent = 'Chưa chọn kho';
                }
                return;
            }
            const option = select.options[select.selectedIndex];
            const available = option ? option.dataset.available : null;
            if (infoDiv) {
                infoDiv.textContent = available !== null && available !== undefined
                    ? ('Tồn khả dụng: ' + formatNumber(parseFloat(available)))
                    : 'Không có dữ liệu tồn';
            }
        }

        function applyPricing(row, materialId) {
            const pricing = materialPricing[String(materialId)];
            const unitPriceInput = row.querySelector('.unit-price-input');
            if (!pricing || !unitPriceInput) {
                const hint = row.querySelector('.price-hint');
                if (hint) {
                    hint.textContent = pricing ? 'Không có khuyến nghị giá' : 'Chưa có dữ liệu giá';
                    hint.classList.remove('price-warning');
                }
                return;
            }

            const fallbackPrice = pricing.suggestedPrice || pricing.lastExportPrice || pricing.averageCost;
            if ((!unitPriceInput.value || unitPriceInput.dataset.autofilled === 'true') && fallbackPrice) {
                unitPriceInput.value = Number(fallbackPrice).toFixed(2);
                unitPriceInput.dataset.autofilled = 'true';
            }
            validatePrice(row);
        }

        function validatePrice(row) {
            const unitPriceInput = row.querySelector('.unit-price-input');
            const hint = row.querySelector('.price-hint');
            const materialId = row.querySelector('.material-id-input')?.value;
            if (!hint || !materialId) {
                if (hint) hint.textContent = '';
                return;
            }

            const pricing = materialPricing[String(materialId)];
            if (!pricing) {
                hint.textContent = 'Chưa có dữ liệu giá';
                hint.classList.remove('price-warning');
                return;
            }

            const parts = [];
            if (pricing.suggestedPrice && pricing.suggestedPrice > 0) {
                parts.push('Gợi ý: ' + formatCurrency(pricing.suggestedPrice));
            }
            const hasMin = pricing.minPrice && pricing.minPrice > 0;
            const hasMax = pricing.maxPrice && pricing.maxPrice > 0;
            if (hasMin && hasMax) {
                parts.push('Biên: ' + formatCurrency(pricing.minPrice) + ' - ' + formatCurrency(pricing.maxPrice));
            } else if (hasMin) {
                parts.push('Tối thiểu: ' + formatCurrency(pricing.minPrice));
            } else if (hasMax) {
                parts.push('Tối đa: ' + formatCurrency(pricing.maxPrice));
            }
            hint.textContent = parts.join(' | ');

            const value = parseFloat(unitPriceInput.value);
            let outOfRange = false;
            if (!isNaN(value)) {
                if (hasMin && value < pricing.minPrice) {
                    outOfRange = true;
                }
                if (hasMax && value > pricing.maxPrice) {
                    outOfRange = true;
                }
            }
            if (outOfRange) {
                hint.classList.add('price-warning');
            } else {
                hint.classList.remove('price-warning');
            }
        }

        function initializeRow(row) {
            const quantityInput = row.querySelector('.quantity-input');
            const unitPriceInput = row.querySelector('.unit-price-input');
            const discountInput = row.querySelector('.discount-input');
            const warehouseSelect = row.querySelector('.warehouse-select');

            if (quantityInput) {
                quantityInput.addEventListener('input', () => calculateRowTotal(row));
            }
            if (unitPriceInput) {
                unitPriceInput.addEventListener('input', () => {
                    unitPriceInput.dataset.autofilled = 'false';
                    validatePrice(row);
                    calculateRowTotal(row);
                });
            }
            if (discountInput) {
                discountInput.addEventListener('input', () => calculateRowTotal(row));
            }
            if (warehouseSelect) {
                warehouseSelect.addEventListener('change', function () {
                    updateWarehouseInfo(this);
                });
                if (warehouseSelect.value) {
                    updateWarehouseInfo(warehouseSelect);
                }
            }
        }

        function handleMaterialSelected(row, materialId, selectedWarehouseId) {
            populateWarehouseOptions(row, materialId, selectedWarehouseId);
            applyPricing(row, materialId);
            validatePrice(row);
        }

        function resetMaterialRow(row) {
            const nameInput = row.querySelector('.material-name-input');
            const idInput = row.querySelector('.material-id-input');
            const quantityInput = row.querySelector('.quantity-input');
            const unitPriceInput = row.querySelector('.unit-price-input');
            const discountInput = row.querySelector('.discount-input');
            const codeCell = row.querySelector('.material-code-cell');
            const unitCell = row.querySelector('.material-unit-cell');
            const warehouseSelect = row.querySelector('.warehouse-select');
            const warehouseInfo = row.querySelector('.warehouse-stock-info');
            const priceHint = row.querySelector('.price-hint');
            const image = row.querySelector('.material-image');

            if (nameInput) nameInput.value = '';
            if (idInput) idInput.value = '';
            if (quantityInput) quantityInput.value = '';
            if (unitPriceInput) {
                unitPriceInput.value = '';
                unitPriceInput.dataset.autofilled = 'false';
            }
            if (discountInput) discountInput.value = '0';
            if (codeCell) codeCell.textContent = '-';
            if (unitCell) unitCell.textContent = '-';
            if (warehouseSelect) {
                warehouseSelect.innerHTML = '<option value=\"\">-- Chọn kho --</option>';
                warehouseSelect.value = '';
                warehouseSelect.disabled = true;
            }
            if (warehouseInfo) warehouseInfo.textContent = 'Chưa chọn hàng hóa';
            if (priceHint) {
                priceHint.textContent = '';
                priceHint.classList.remove('price-warning');
            }
            if (image) image.src = resolveMediaUrl(null);

            const amountCell = row.querySelector('.material-amount-cell');
            if (amountCell) amountCell.textContent = '0';
        }

        function updateRowNumbers() {
            const rows = document.querySelectorAll('.material-row');
            rows.forEach((row, index) => {
                row.querySelector('td:first-child').textContent = index + 1;
            });
        }

        var materialsData = [
        <c:forEach var="material" items="${materials}" varStatus="status">
            {
                label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
                value: "${fn:escapeXml(material.materialName)}",
                id: "${material.materialId}",
                name: "${fn:escapeXml(material.materialName)}",
                code: "${fn:escapeXml(material.materialCode)}",
                imageUrl: "${fn:escapeXml(material.materialsUrl)}",
                unit: "${material.defaultUnit != null ? fn:escapeXml(material.defaultUnit.unitName) : ''}",
                categoryId: "${material.category.category_id}",
                categoryName: "${material.category.category_name}"
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
        ];

        var customersData = {
        <c:forEach var="customer" items="${customers}" varStatus="status">
            "${customer.customerId}": {
                name: "${fn:escapeXml(customer.customerName)}",
                code: "${fn:escapeXml(customer.customerCode)}",
                phone: "${fn:escapeXml(customer.phone)}",
                address: "${fn:escapeXml(customer.address)}"
            }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
        };
        
        function updateMaterialRowAutocomplete(row) {
            const nameInput = row.querySelector('.material-name-input');
            const idInput = row.querySelector('.material-id-input');
            const img = row.querySelector('.material-image');
            const codeCell = row.querySelector('.material-code-cell');
            const unitCell = row.querySelector('.material-unit-cell');
            
            $(nameInput).autocomplete({
                source: function(request, response) {
                    const term = request.term.toLowerCase();
                    let matches;

                    if (term.length === 0) {
                        matches = materialsData.slice(0, 10);
                    } else {
                        matches = materialsData.filter(material =>
                            material.name.toLowerCase().includes(term) ||
                            material.code.toLowerCase().includes(term)
                        );
                    }
                    response(matches);
                },
                select: function(event, ui) {
                    idInput.value = ui.item.id;
                    nameInput.value = ui.item.name;
                    codeCell.textContent = ui.item.code;
                    unitCell.textContent = ui.item.unit || '-';
                    const imgUrl = ui.item.imageUrl && ui.item.imageUrl !== 'null' ? ui.item.imageUrl : null;
                    img.src = resolveMediaUrl(imgUrl);
                    const parsedId = parseInt(ui.item.id);
                    if (!isNaN(parsedId)) {
                        handleMaterialSelected(row, parsedId);
                    }
                    return false;
                },
                focus: function(event, ui) {
                    event.preventDefault();
                    return false;
                },
                minLength: 0
            }).autocomplete("instance")._renderItem = function(ul, item) {
                let imgUrl = resolveMediaUrl(item.imageUrl && item.imageUrl !== 'null' ? item.imageUrl : null);
                
                return $("<li>")
                    .append(
                        $("<div class='ui-menu-item-wrapper'>")
                            .append($("<img>").attr("src", imgUrl).addClass("autocomplete-img"))
                            .append(
                                $("<div class='autocomplete-info'>")
                                    .append($("<div class='autocomplete-name'>").text(item.name))
                                    .append($("<div class='autocomplete-code'>").text("Code: " + item.code))
                            )
                    )
                    .appendTo(ul);
            };

            $(nameInput).on('focus click', function () {
                $(this).autocomplete('search', '');
            });
        }

        // Handle customer selection
        document.getElementById('customerId').addEventListener('change', function() {
            const customerId = this.value;
            if (customerId && customersData[customerId]) {
                const customer = customersData[customerId];
                document.getElementById('customerCode').value = customer.code || '-';
                document.getElementById('customerPhone').value = customer.phone || '-';
                document.getElementById('customerAddress').value = customer.address || '-';
            } else {
                document.getElementById('customerCode').value = '';
                document.getElementById('customerPhone').value = '';
                document.getElementById('customerAddress').value = '';
            }
        });
        
        document.addEventListener('DOMContentLoaded', function() {
            // Initialize autocomplete for existing rows
            document.querySelectorAll('.material-row').forEach(row => {
                updateMaterialRowAutocomplete(row);
                initializeRow(row);
            });
            
            // Restore submitted data if there were validation errors
            <c:if test="${not empty submittedMaterialNames}">
                const submittedMaterialNames = [
                    <c:forEach var="materialName" items="${submittedMaterialNames}" varStatus="status">
                        "${fn:escapeXml(materialName)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedMaterialIds = [
                    <c:forEach var="materialId" items="${submittedMaterialIds}" varStatus="status">
                        "${materialId}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedQuantities = [
                    <c:forEach var="quantity" items="${submittedQuantities}" varStatus="status">
                        "${fn:escapeXml(quantity)}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                const submittedWarehouses = [
                    <c:forEach var="warehouseId" items="${submittedWarehouses}" varStatus="status">
                        "${warehouseId}"<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ];
                
                const materialList = document.getElementById('materialList');
                materialList.innerHTML = '';
                
                for (let i = 0; i < submittedMaterialNames.length; i++) {
                    const newRow = createMaterialRow();
                    materialList.appendChild(newRow);
                    
                    const nameInput = newRow.querySelector('.material-name-input');
                    const idInput = newRow.querySelector('.material-id-input');
                    const quantityInput = newRow.querySelector('.quantity-input');
                        
                        if (nameInput && submittedMaterialNames[i]) {
                            nameInput.value = submittedMaterialNames[i];
                            setTimeout(() => {
                                $(nameInput).autocomplete('search', submittedMaterialNames[i]);
                            }, 100);
                        }
                    if (idInput && submittedMaterialIds[i]) {
                        idInput.value = submittedMaterialIds[i];
                        }
                        if (quantityInput && submittedQuantities[i]) {
                            quantityInput.value = submittedQuantities[i];
                        }
                    
                    updateMaterialRowAutocomplete(newRow);
                    initializeRow(newRow);
                    
                    const materialIdValue = submittedMaterialIds[i] ? parseInt(submittedMaterialIds[i]) : null;
                    if (!isNaN(materialIdValue)) {
                        populateWarehouseOptions(newRow, materialIdValue, submittedWarehouses[i]);
                        applyPricing(newRow, materialIdValue);
                    }
                    const warehouseSelect = newRow.querySelector('.warehouse-select');
                    if (warehouseSelect && submittedWarehouses[i]) {
                        warehouseSelect.value = submittedWarehouses[i];
                        updateWarehouseInfo(warehouseSelect);
                    }
                }
                
                updateRowNumbers();
            </c:if>
            
            // Initialize customer data if already selected
            const customerSelect = document.getElementById('customerId');
            if (customerSelect.value) {
                customerSelect.dispatchEvent(new Event('change'));
            }
            
            calculateGrandTotal();
        });
        
        // Create a new material row template
        function createMaterialRow() {
            const defaultImageUrl = resolveMediaUrl(null);
            const row = document.createElement('tr');
            row.className = 'material-row';
            row.innerHTML =
                '<td class="text-center">1</td>' +
                '<td class="material-image-cell">' +
                    '<img class="material-image" src="' + defaultImageUrl + '" alt="Material Image">' +
                '</td>' +
                '<td class="material-input-cell">' +
                    '<input type="text" class="form-control material-name-input" name="materialName[]" placeholder="Nhập tên hoặc mã hàng" autocomplete="off">' +
                    '<input type="hidden" name="materialId[]" class="material-id-input">' +
                '</td>' +
                '<td class="material-code-cell">-</td>' +
                '<td class="material-unit-cell">-</td>' +
                '<td class="material-warehouse-cell">' +
                    '<select class="form-select warehouse-select" name="warehouseId[]" required disabled>' +
                        '<option value="">-- Chọn kho --</option>' +
                    '</select>' +
                    '<div class="warehouse-stock-info text-muted">Chưa chọn hàng hóa</div>' +
                '</td>' +
                '<td class="material-input-cell">' +
                    '<input type="number" class="form-control unit-price-input" name="unitPriceExport[]" min="0" step="0.01" placeholder="0" required>' +
                    '<div class="price-hint text-muted"></div>' +
                '</td>' +
                '<td class="material-input-cell">' +
                    '<input type="number" class="form-control quantity-input" name="quantity[]" min="0.01" step="0.01" placeholder="0" required>' +
                '</td>' +
                '<td class="material-input-cell">' +
                    '<input type="number" class="form-control discount-input" name="discount[]" min="0" max="100" step="0.01" placeholder="0" value="0">' +
                '</td>' +
                '<td class="material-amount-cell">0</td>' +
                '<td>' +
                    '<button type="button" class="btn-remove-material remove-material">' +
                        '<i class="fas fa-trash"></i>' +
                    '</button>' +
                '</td>';
            return row;
        }
        
        document.getElementById('addMaterial').addEventListener('click', function () {
            const materialList = document.getElementById('materialList');
            let newRow;
            
            // If no rows exist, create a new one from template
            const firstRow = materialList.querySelector('.material-row');
            if (firstRow) {
                newRow = firstRow.cloneNode(true);
                resetMaterialRow(newRow);
            } else {
                newRow = createMaterialRow();
            }
            
            materialList.appendChild(newRow);
            updateMaterialRowAutocomplete(newRow);
            initializeRow(newRow);
            updateRowNumbers();
        });
        
        document.addEventListener('click', function (e) {
            if (e.target.closest('.remove-material')) {
                    e.target.closest('.material-row').remove();
                updateRowNumbers();
                calculateGrandTotal();
            }
        });
        
        // Material Selection Modal Functions
        let currentMaterialPage = 1;
        let materialsPerPage = 20;
        let filteredMaterials = [];
        let selectedMaterialIds = new Set();
        
        // Get already added material IDs to avoid duplicates
        function getAddedMaterialIds() {
            const ids = new Set();
            document.querySelectorAll('.material-id-input').forEach(input => {
                if (input.value) {
                    const id = parseInt(input.value);
                    if (!isNaN(id)) {
                        ids.add(id);
                    }
                }
            });
            return ids;
        }
        
        // Render materials in modal
        function renderMaterialsInModal(materials, page = 1) {
            const grid = document.getElementById('materialSelectionGrid');
            const addedIds = getAddedMaterialIds();
            const startIndex = (page - 1) * materialsPerPage;
            const endIndex = startIndex + materialsPerPage;
            const pageMaterials = materials.slice(startIndex, endIndex);
            
            console.log('Rendering materials. Page:', page, 'Materials on page:', pageMaterials.length);
            console.log('Sample material data:', pageMaterials[0]);
            
            grid.innerHTML = '';
            
            if (pageMaterials.length === 0) {
                grid.innerHTML = '<div class="col-12 text-center text-muted py-5">Không tìm thấy sản phẩm nào</div>';
                return;
            }
            
            pageMaterials.forEach((material, index) => {
                // Skip invalid materials
                if (!material || !material.id) {
                    console.warn('Skipping invalid material:', material);
                    return;
                }
                
                // Convert ID to number for consistent comparison
                const materialId = parseInt(material.id, 10);
                if (isNaN(materialId)) {
                    console.warn('Skipping material with invalid ID:', material.id);
                    return;
                }
                if (isNaN(materialId)) {
                    console.warn('Invalid material ID:', material.id, material);
                    return;
                }
                
                const availability = materialAvailability[String(materialId)] || [];
                const stockSummary = availability.length
                    ? availability.map(info => {
                        const label = info.warehouseCode ? info.warehouseCode : info.warehouseName;
                        return (label || 'Kho') + ': ' + formatNumber(info.availableStock || 0);
                    }).join(' | ')
                    : 'Chưa có tồn kho';
                const isAdded = addedIds.has(materialId);
                const isSelected = selectedMaterialIds.has(materialId);
                const imgUrl = resolveMediaUrl(material.imageUrl && material.imageUrl !== 'null' ? material.imageUrl : null);
                
                // Get material data - try multiple possible property names
                const materialName = (material.name || material.value || material.label || 'Không có tên').trim();
                const materialCode = (material.code || '').trim() || '-';
                const materialUnit = (material.unit || '').trim() || '-';
                
                // Debug log for first few items
                if (index < 3) {
                    console.log(`Rendering material ${index + 1}:`, {
                        id: materialId,
                        name: materialName,
                        code: materialCode,
                        unit: materialUnit,
                        hasName: !!material.name,
                        hasValue: !!material.value,
                        hasLabel: !!material.label,
                        fullMaterial: material
                    });
                }
                
                const item = document.createElement('div');
                item.className = `material-selection-item ${isSelected ? 'selected' : ''} ${isAdded ? 'opacity-50' : ''}`;
                if (isAdded) {
                    item.style.pointerEvents = 'none';
                    item.title = 'Sản phẩm đã được thêm vào phiếu';
                }
                
                // Create elements using DOM methods instead of innerHTML for better control
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.className = 'material-checkbox';
                checkbox.dataset.materialId = materialId;
                checkbox.id = `material-checkbox-${materialId}-${index}`;
                checkbox.checked = isSelected;
                checkbox.disabled = isAdded;
                
                const img = document.createElement('img');
                img.src = imgUrl;
                img.alt = materialName || 'Material Image';
                
                const infoDiv = document.createElement('div');
                infoDiv.className = 'material-selection-item-info';
                
                const nameDiv = document.createElement('div');
                nameDiv.className = 'material-selection-item-name';
                nameDiv.textContent = materialName || 'Không có tên';
                if (isAdded) {
                    const badge = document.createElement('span');
                    badge.className = 'badge bg-secondary ms-2';
                    badge.style.fontSize = '0.7rem';
                    badge.textContent = 'Đã thêm';
                    nameDiv.appendChild(badge);
                }
                
                const codeDiv = document.createElement('div');
                codeDiv.className = 'material-selection-item-code';
                codeDiv.textContent = 'Mã: ' + (materialCode || '-');
                
                const unitDiv = document.createElement('div');
                unitDiv.className = 'material-selection-item-unit';
                unitDiv.textContent = 'Đơn vị: ' + materialUnit;

                const stockDiv = document.createElement('div');
                stockDiv.className = 'warehouse-stock-info';
                stockDiv.textContent = stockSummary;
                
                infoDiv.appendChild(nameDiv);
                infoDiv.appendChild(codeDiv);
                infoDiv.appendChild(unitDiv);
                infoDiv.appendChild(stockDiv);
                
                item.appendChild(checkbox);
                item.appendChild(img);
                item.appendChild(infoDiv);
                
                // Add event listener to checkbox
                checkbox.addEventListener('change', function() {
                    toggleMaterialSelection(materialId, this.checked);
                });
                
                grid.appendChild(item);
            });
            
            // Update pagination
            const totalPages = Math.ceil(materials.length / materialsPerPage);
            updatePagination(page, totalPages);
        }
        
        // Helper function to escape HTML (not used anymore but kept for reference)
        function escapeHtml(text) {
            if (!text) return '';
            const map = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#039;'
            };
            return String(text).replace(/[&<>"']/g, m => map[m]);
        }
        
        // Update pagination controls
        function updatePagination(currentPage, totalPages) {
            const pagination = document.getElementById('materialPagination');
            const prevBtn = document.getElementById('prevPageBtn');
            const nextBtn = document.getElementById('nextPageBtn');
            const pageInfo = document.getElementById('pageInfo');
            
            if (totalPages <= 1) {
                pagination.style.display = 'none';
            } else {
                pagination.style.display = 'flex';
                prevBtn.disabled = currentPage === 1;
                nextBtn.disabled = currentPage === totalPages;
                pageInfo.textContent = `Trang ${currentPage} / ${totalPages}`;
            }
        }
        
        // Toggle material selection
        function toggleMaterialSelection(materialId, isChecked) {
            // Ensure materialId is a number
            const id = parseInt(materialId);
            
            if (isChecked) {
                selectedMaterialIds.add(id);
            } else {
                selectedMaterialIds.delete(id);
            }
            updateSelectedCount();
            
            // Update visual state - find the exact checkbox that was clicked
            const checkbox = document.querySelector(`[data-material-id="${id}"].material-checkbox`);
            if (checkbox) {
                const item = checkbox.closest('.material-selection-item');
                if (item) {
                    if (isChecked) {
                        item.classList.add('selected');
                    } else {
                        item.classList.remove('selected');
                    }
                }
            }
        }
        
        // Update selected count
        function updateSelectedCount() {
            const count = selectedMaterialIds.size;
            document.getElementById('selectedCount').textContent = `Đã chọn: ${count} sản phẩm`;
        }
        
        // Filter materials
        function filterMaterials(searchTerm) {
            if (!searchTerm || searchTerm.trim() === '') {
                filteredMaterials = materialsData;
            } else {
                const term = searchTerm.toLowerCase().trim();
                filteredMaterials = materialsData.filter(material =>
                    material.name.toLowerCase().includes(term) ||
                    material.code.toLowerCase().includes(term)
                );
            }
            currentMaterialPage = 1;
            renderMaterialsInModal(filteredMaterials, currentMaterialPage);
        }
        
        // Select all visible materials
        function selectAllVisible() {
            const addedIds = getAddedMaterialIds();
            const startIndex = (currentMaterialPage - 1) * materialsPerPage;
            const endIndex = startIndex + materialsPerPage;
            const pageMaterials = filteredMaterials.slice(startIndex, endIndex);
            
            pageMaterials.forEach(material => {
                const materialId = parseInt(material.id);
                if (!addedIds.has(materialId)) {
                    selectedMaterialIds.add(materialId);
                    const checkbox = document.querySelector(`[data-material-id="${materialId}"]`);
                    if (checkbox && !checkbox.disabled) {
                        checkbox.checked = true;
                        checkbox.closest('.material-selection-item').classList.add('selected');
                    }
                }
            });
            updateSelectedCount();
        }
        
        // Add selected materials to table
        function addSelectedMaterialsToTable() {
            const addedIds = getAddedMaterialIds();
            let addedCount = 0;
            
            selectedMaterialIds.forEach(materialId => {
                // Ensure materialId is a number for comparison
                const id = parseInt(materialId);
                if (!addedIds.has(id)) {
                    // Find material by comparing IDs as numbers
                    const material = materialsData.find(m => parseInt(m.id) === id);
                    if (material) {
                        addMaterialRowFromSelection(material);
                        addedCount++;
                    }
                }
            });
            
            if (addedCount > 0) {
                // Close modal and reset
                const modal = bootstrap.Modal.getInstance(document.getElementById('materialSelectionModal'));
                modal.hide();
                selectedMaterialIds.clear();
                updateSelectedCount();
                
                // No alert message needed as requested
            } else {
                // Only show alert if no items were added (error case)
                alert('Không có sản phẩm nào được thêm. Có thể tất cả đã được thêm trước đó.');
            }
        }
        
        // Add material row from selection
        function addMaterialRowFromSelection(material) {
            const materialList = document.getElementById('materialList');
            let newRow;
            
            // If no rows exist, create a new one from template
            const firstRow = materialList.querySelector('.material-row');
            if (firstRow) {
                newRow = firstRow.cloneNode(true);
                resetMaterialRow(newRow);
            } else {
                // Create first row from template
                newRow = createMaterialRow();
            }
            
            // Set values from material
            newRow.querySelector('.material-name-input').value = material.name || '';
            newRow.querySelector('.material-id-input').value = material.id || '';
            newRow.querySelector('.material-code-cell').textContent = material.code || '-';
            newRow.querySelector('.material-unit-cell').textContent = material.unit || '-';
            
            const imgUrl = resolveMediaUrl(material.imageUrl && material.imageUrl !== 'null' ? material.imageUrl : null);
            newRow.querySelector('.material-image').src = imgUrl;
            
            materialList.appendChild(newRow);
            updateMaterialRowAutocomplete(newRow);
            initializeRow(newRow);

            const parsedId = parseInt(material.id);
            if (!isNaN(parsedId)) {
                const availability = materialAvailability[String(parsedId)];
                const defaultWarehouseId = availability && availability.length ? availability[0].warehouseId : null;
                handleMaterialSelected(newRow, parsedId, defaultWarehouseId);
            }

            calculateRowTotal(newRow);
            updateRowNumbers();
        }
        
        // Event listeners for modal
        document.getElementById('selectMultipleMaterials').addEventListener('click', function() {
            // Check if materialsData is loaded
            if (!materialsData || materialsData.length === 0) {
                alert('Không có dữ liệu sản phẩm. Vui lòng tải lại trang.');
                console.error('materialsData is empty:', materialsData);
                console.error('materialsData type:', typeof materialsData);
                return;
            }
            
            console.log('Opening material selection modal. Total materials:', materialsData.length);
            if (materialsData.length > 0) {
                console.log('Sample material (first):', materialsData[0]);
                console.log('Sample material keys:', Object.keys(materialsData[0]));
            }
            
            // Filter out any invalid materials
            const validMaterials = materialsData.filter(m => m && m.id);
            console.log('Valid materials count:', validMaterials.length);
            
            if (validMaterials.length === 0) {
                alert('Không có dữ liệu sản phẩm hợp lệ. Vui lòng tải lại trang.');
                return;
            }
            
            filteredMaterials = [...validMaterials]; // Create a copy
            selectedMaterialIds.clear();
            currentMaterialPage = 1;
            renderMaterialsInModal(filteredMaterials, currentMaterialPage);
            updateSelectedCount();
            
            const modal = new bootstrap.Modal(document.getElementById('materialSelectionModal'));
            modal.show();
        });
        
        document.getElementById('materialSearchInput').addEventListener('input', function(e) {
            filterMaterials(e.target.value);
        });
        
        document.getElementById('selectAllBtn').addEventListener('click', function() {
            selectAllVisible();
        });
        
        document.getElementById('addSelectedMaterialsBtn').addEventListener('click', function() {
            addSelectedMaterialsToTable();
        });
        
        document.getElementById('prevPageBtn').addEventListener('click', function() {
            if (currentMaterialPage > 1) {
                currentMaterialPage--;
                renderMaterialsInModal(filteredMaterials, currentMaterialPage);
            }
        });
        
        document.getElementById('nextPageBtn').addEventListener('click', function() {
            const totalPages = Math.ceil(filteredMaterials.length / materialsPerPage);
            if (currentMaterialPage < totalPages) {
                currentMaterialPage++;
                renderMaterialsInModal(filteredMaterials, currentMaterialPage);
            }
        });
        
        // Reset modal when closed
        document.getElementById('materialSelectionModal').addEventListener('hidden.bs.modal', function() {
            selectedMaterialIds.clear();
            document.getElementById('materialSearchInput').value = '';
            updateSelectedCount();
        });
    </script>
</body>
</html>
