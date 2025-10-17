<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="entity.User" %>
<% 
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Import Material</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <style>
            .material-form {
                border-radius: 12px;
                box-shadow: 0 2px 12px rgba(0,0,0,0.07);
                background: white;
            }
            .material-form .form-control, .material-form .form-select {
                height: 48px;
                font-size: 18px;
                color: black;
                background: white;
                border-radius: 8px;
                border: 1px solid #bdbdbd;
            }
            .material-form .form-label {
                font-size: 16px;
                margin-bottom: 6px;
                font-weight: 500;
                color: black;
            }
            .material-form .btn {
                font-size: 16px;
                padding: 12px;
            }
            .material-form .material-row {
                margin-bottom: 16px;
                border-bottom: 1px solid gray;
                padding-bottom: 16px;
            }
            .material-form .img-thumbnail {
                width: 60px;
                height: 60px;
                object-fit: cover;
                border-radius: 8px;
                border: 1px solid #bdbdbd;
            }
            .material-form .status-badge {
                padding: 10px 22px;
                border-radius: 12px;
                font-size: 16px;
                font-weight: 600;
                background: lightgray;
                color: black;
                border: 1px solid #bdbdbd;
            }
            .material-form .status-new {
                background-color: #198754;
                color: #fff;
            }
            .material-form .status-used {
                background-color: #ffc107;
                color: #212529;
            }
            .material-form .status-refurbished {
                background-color: #0dcaf0;
                color: #fff;
            }
            .material-form .badge.bg-secondary, .material-form .badge.bg-info, .material-form .badge.bg-success {
                font-size: 16px;
                padding: 10px 22px;
                font-weight: 600;
                border-radius: 12px;
                border: 1px solid #bdbdbd;
                background: lightgray !important;
                color: black !important;
            }
            .material-form .badge.bg-info {
                background-color: #0dcaf0 !important;
                color: #fff !important;
            }
            .material-form .badge.bg-secondary {
                background-color: #6c757d !important;
                color: #fff !important;
            }
            .material-form .badge.bg-success {
                background-color: #198754 !important;
                color: #fff !important;
            }
            .material-form .table {
                background: white;
                color: black;
                border: 1px solid #bdbdbd;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 1px 8px rgba(0,0,0,0.04);
            }
            .material-form .table th, .material-form .table td {
                color: black;
                font-size: 16px;
                font-weight: 400;
                border: 1px solid #bdbdbd;
                padding: 10px 12px;
                background: white;
            }
            .material-form .table th {
                font-weight: 700;
                background: lightgray;
                color: black;
                border-top: none;
            }
            .material-form .card, .material-form .card-header, .material-form .card-body {
                border-radius: 12px !important;
                background: white;
                border: none;
            }
            .material-form .card-header {
                background: lightgray !important;
                color: black;
                font-weight: 700;
                border-bottom: 1px solid #bdbdbd;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 16px;
            }
            .condition-bar {
                height: 5px;
                background-color: #e9ecef;
                border-radius: 3px;
                margin-top: 5px;
            }
            .condition-fill {
                height: 100%;
                border-radius: 3px;
                transition: width 0.3s ease;
            }
            .condition-good {
                background-color: #28a745;
            }
            .condition-warning {
                background-color: #ffc107;
            }
            .condition-bad {
                background-color: #dc3545;
            }
            .material-img {
                max-height: 100px;
                max-width: 100px;
                object-fit: cover;
                border-radius: 5px;
            }
            .material-form table {
                border-collapse: collapse;
                width: 100%;
            }
            .material-form th, .material-form td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #dee2e6;
                font-size: 1rem;
            }
            .material-form th {
                background-color: #f8f9fa;
                font-weight: 700;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 1rem;
            }
            .material-form .form-control:focus {
                border-color: #86b7fe;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
                outline: 0;
            }
            .material-form .form-control.is-invalid {
                border-color: #dc3545;
            }
            .material-form .form-control.is-invalid:focus {
                box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
            }
            .invalid-feedback {
                color: #dc3545 !important;
                font-weight: bold;
                font-size: 1rem;
                opacity: 1 !important;
            }
            /* Autocomplete styling */
            .ui-autocomplete {
                max-height: 200px;
                overflow-y: auto;
                overflow-x: hidden;
                border: 1px solid #ced4da;
                border-radius: 0.25rem;
                background-color: #fff;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
                z-index: 1000;
            }
            .ui-menu-item {
                padding: 8px 12px;
                font-size: 1rem;
                cursor: pointer;
            }
            .ui-menu-item:hover {
                background-color: #f8f9fa;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="import-material" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow material-form">
                        <c:if test="${not hasImportMaterialPermission}">
                            <div class="alert alert-danger">You do not have permission to import materials.</div>
                            <div class="text-center mt-3">
                                <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                            </div>
                        </c:if>
                        <c:if test="${hasImportMaterialPermission}">
                            <h2 class="display-4 fw-normal text-center mb-4">
                                <i class="fas fa-box-open me-2"></i>Import <span class="text-primary">Material</span>
                            </h2>
                            <!-- Alert Messages -->
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>
                            <c:if test="${not empty success}">
                                <div class="alert alert-success">${success}</div>
                            </c:if>

                            <!-- Load from Purchase Order Section - Hide when using manual input -->
                            <c:if test="${empty usingManualInput}">
                                <div class="card mb-4">
                                    <div class="card-header">
                                        <h4 class="mb-0"><i class="fas fa-shopping-cart me-2"></i>Load from Purchase Order (Auto-render)</h4>
                                    </div>
                                    <div class="card-body">
                                        <p class="text-muted mb-3">Type purchase order code or request code to automatically load materials for import:</p>
                                        <form action="ImportMaterial" method="post" class="mb-3">
                                            <input type="hidden" name="action" value="loadFromPurchaseOrder">
                                            <div class="row g-3">
                                                <div class="col-md-8">
                                                    <label for="purchaseOrderName" class="form-label">Purchase Order</label>
                                                    <input type="text" id="purchaseOrderName" name="purchaseOrderName" class="form-control" placeholder="Type PO code or request code" autocomplete="off">
                                                    <input type="hidden" name="purchaseOrderId" id="purchaseOrderId">
                                                    <div class="invalid-feedback">Please enter a valid purchase order code or request code.</div>
                                                </div>
                                                <div class="col-md-4">
                                                    <label class="form-label">&nbsp;</label>
                                                    <button type="submit" class="btn btn-primary w-100" disabled id="loadPoBtn">
                                                        <i class="fas fa-download me-2"></i>Load Materials
                                                    </button>
                                                </div>
                                            </div>
                                        </form>
                                        <c:if test="${not empty selectedPurchaseOrderId}">
                                            <div class="alert alert-info">
                                                <i class="fas fa-info-circle me-2"></i>
                                                Materials loaded from Purchase Order. You can now proceed with the import.
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Add Material Form - Only show when no PO is selected -->
                            <c:if test="${empty selectedPurchaseOrderId}">
                                <h3 class="fw-normal mb-3">Add Material to Import List</h3>
                                <form id="addMaterialForm" action="ImportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                                    <input type="hidden" name="action" value="add">
                                    <div class="row g-3">
                                        <div class="col-12">
                                            <label for="materialName" class="form-label text-muted">Material</label>
                                            <input type="text" id="materialName" name="materialName" class="form-control" required placeholder="Type material name or code" autocomplete="off">
                                            <input type="hidden" name="materialId" id="materialId">
                                            <div class="invalid-feedback">Please enter a valid material name or code.</div>
                                            <c:if test="${not empty formErrors['materialId']}">
                                                <div class="text-danger small mt-1">${formErrors['materialId']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="quantity" class="form-label text-muted">Quantity</label>
                                            <input type="number" name="quantity" id="quantity" class="form-control" min="1" required placeholder="Qty">
                                            <div class="invalid-feedback">Please enter a quantity greater than 0.</div>
                                            <c:if test="${not empty formErrors['quantity']}">
                                                <div class="text-danger small mt-1">${formErrors['quantity']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="unitPrice" class="form-label text-muted">Unit Price ($)</label>
                                            <input type="number" name="unitPrice" id="unitPrice" class="form-control" min="0" step="0.01" required placeholder="Unit Price ($)">
                                            <c:if test="${not empty formErrors['unitPrice']}">
                                                <div class="text-danger small mt-1">${formErrors['unitPrice']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="totalValue" class="form-label text-muted">Total Value</label>
                                            <input type="text" id="totalValue" class="form-control" readonly placeholder="Auto">
                                        </div>
                                        <div class="col-12 mt-4">
                                            <button type="submit" class="btn btn-dark btn-lg rounded-1 w-100">
                                                <i class="fas fa-plus-circle me-2"></i>Add to Import List
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </c:if>

                            <!-- Current Import List -->
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h3 class="fw-normal mb-0">Current Import List</h3>
                                <c:if test="${not empty importDetails}">
                                    <form action="ImportMaterial" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="reset">
                                        <button type="submit" class="btn btn-outline-secondary btn-sm">
                                            <i class="fas fa-refresh me-1"></i>Reset Form
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                            <c:if test="${not empty importDetails}">
                                <div class="table-responsive">
                                    <table class="table align-middle text-center">
                                        <thead>
                                                                                         <tr>
                                                 <th style="width: 8%">Image</th>
                                                 <th style="width: 18%">Material</th>
                                                 <th style="width: 12%">Unit</th>
                                                 <th style="width: 15%">Category</th>
                                                 <th style="width: 10%">Quantity</th>
                                                 <th style="width: 15%">Price</th>
                                                 <th style="width: 12%">Value</th>
                                                 <th style="width: 10%">Supplier</th>
                                                 <th style="width: 8%">Stock</th>
                                                 <th style="width: 5%">Action</th>
                                             </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${importDetails}">
                                                <tr>
                                                    <c:set var="imgUrl" value="${materialMap[detail.materialId].materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${empty imgUrl}">
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/default-material.png' />
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(imgUrl, 'http://') || fn:startsWith(imgUrl, 'https://')}">
                                                            <c:set var="finalUrl" value='${imgUrl}' />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/${imgUrl}' />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <td>
                                                        <img src="${finalUrl}" class="material-img" alt="${materialMap[detail.materialId].materialName}">
                                                    </td>
                                                    <td>
                                                        <strong>${materialMap[detail.materialId].materialName}</strong><br>
                                                        <small class="text-muted">${materialMap[detail.materialId].materialCode}</small>
                                                    </td>
                                                    <td>${materialMap[detail.materialId].unit.unitName}</td>
                                                    <td>${materialMap[detail.materialId].category.category_name}</td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="update">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="number" name="newQuantity" value="${detail.quantity}" min="1"
                                                                   class="form-control me-2" style="width: 100px; text-align: center;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Quantity">
                                                                <i class="fas fa-sync-alt"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="updatePrice">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="number" name="newPrice" value="${detail.unitPrice}" min="0" step="0.01"
                                                                   class="form-control me-2" style="width: 80px; text-align: center;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Price">
                                                                <i class="fas fa-dollar-sign"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                                                                         <td><strong>$<fmt:formatNumber value="${detail.unitPrice * detail.quantity}" type="number" minFractionDigits="2"/></strong></td>
                                                     <td>
                                                         <c:if test="${not empty selectedSupplierId}">
                                                             <c:forEach var="supplier" items="${suppliers}">
                                                                 <c:if test="${supplier.supplierId == selectedSupplierId}">
                                                                     <span style="color: #000; font-weight: 600;">${supplier.supplierName}</span>
                                                                 </c:if>
                                                             </c:forEach>
                                                         </c:if>
                                                         <c:if test="${empty selectedSupplierId}">
                                                             <span style="color: #666; font-style: italic;">Not set</span>
                                                         </c:if>
                                                     </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${stockMap[detail.materialId] > 50}">
                                                                <span style="background:#4ade80;color:#000;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #22c55e;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:when test="${stockMap[detail.materialId] >= 10}">
                                                                <span style="background:#fde047;color:#000;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #facc15;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:when test="${stockMap[detail.materialId] > 0}">
                                                                <span style="background:#fd7e14;color:#fff;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #b35c00;">${stockMap[detail.materialId]}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="background:#dc2626;color:#fff;border-radius:20px;padding:6px 18px;font-weight:600;display:inline-block;min-width:40px;text-align:center;border:2px solid #b91c1c;">${stockMap[detail.materialId]}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <form action="ImportMaterial" method="post">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="quantity" value="${detail.quantity}">
                                                            <button type="submit" class="btn btn-outline-danger btn-sm">Remove</button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- Pagination Controls -->
                                <nav aria-label="Page navigation" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <c:if test="${currentPage > 1}">
                                            <li class="page-item">
                                                <a class="page-link" href="ImportMaterial?page=${currentPage - 1}" aria-label="Previous">
                                                    <span aria-hidden="true">&laquo;</span>
                                                </a>
                                            </li>
                                        </c:if>
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                <a class="page-link" href="ImportMaterial?page=${i}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <c:if test="${currentPage < totalPages}">
                                            <li class="page-item">
                                                <a class="page-link" href="ImportMaterial?page=${currentPage + 1}" aria-label="Next">
                                                    <span aria-hidden="true">&raquo;</span>
                                                </a>
                                            </li>
                                        </c:if>
                                    </ul>
                                </nav>
                            </c:if>
                            <c:if test="${empty importDetails}">
                                <div class="text-center py-5">
                                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No materials added to the import list yet.</p>
                                </div>
                            </c:if>

                                                         <h3 class="fw-normal mb-3 mt-5">Confirm Import</h3>
                             <c:if test="${not empty formErrors}">
                                 <div class="alert alert-danger">
                                     <ul class="mb-0">
                                         <c:forEach var="err" items="${formErrors.values()}">
                                             <li>${err}</li>
                                             </c:forEach>
                                     </ul>
                                 </div>
                             </c:if>
                             <form id="confirmImportForm" action="ImportMaterial" method="post" onsubmit="return validateConfirmForm()">
                                 <input type="hidden" name="action" value="import">
                                 <input type="hidden" name="supplierId" id="supplierId" value="${selectedSupplierId}">
                                 <input type="hidden" name="actualArrival" id="actualArrivalField">
                                 <div class="row g-4">
                                     <div class="col-md-12">
                                         <label class="form-label text-muted">Notes</label>
                                         <input type="text" name="note" class="form-control" placeholder="Enter Note" value="${param.note}">
                                         <c:if test="${not empty formErrors['note']}">
                                             <div class="text-danger small mt-1">${formErrors['note']}</div>
                                         </c:if>
                                     </div>
                                     <div class="col-12 mt-4">
                                         <div class="d-grid gap-2">
                                             <button type="submit" class="btn btn-dark btn-lg rounded-1">Confirm Import</button>
                                             <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                                         </div>
                                     </div>
                                 </div>
                             </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script>
                                document.addEventListener('DOMContentLoaded', function () {

                                    const actualArrivalField = document.getElementById('actualArrivalField');
                                    if (actualArrivalField) {
                                        const now = new Date();
                                        const year = now.getFullYear();
                                        const month = String(now.getMonth() + 1).padStart(2, '0');
                                        const day = String(now.getDate()).padStart(2, '0');
                                        const hours = String(now.getHours()).padStart(2, '0');
                                        const minutes = String(now.getMinutes()).padStart(2, '0');
                                        actualArrivalField.value = `${year}-${month}-${day}T${hours}:${minutes}`;
                                    }

                                    const alerts = document.querySelectorAll('.alert');
                                    alerts.forEach(function (alert) {
                                        setTimeout(function () {
                                            if (alert.style.display !== 'none') {
                                                alert.style.transition = 'all 0.5s ease-out';
                                                alert.style.opacity = '0';
                                                alert.style.transform = 'translateY(-10px)';
                                                setTimeout(function () {
                                                    alert.style.display = 'none';
                                                }, 500);
                                            }
                                        }, 5000);

                                        alert.addEventListener('click', function (e) {
                                            if (e.target.classList.contains('btn-close') || e.target.classList.contains('alert')) {
                                                this.style.transition = 'all 0.3s ease-out';
                                                this.style.opacity = '0';
                                                this.style.transform = 'translateY(-10px)';
                                                setTimeout(() => {
                                                    this.style.display = 'none';
                                                }, 300);
                                            }
                                        });
                                    });
                                });

                                function calculateTotal() {
                                    const quantityInput = document.querySelector('input[name="quantity"]');
                                    const unitPriceInput = document.querySelector('input[name="unitPrice"]');
                                    const totalField = document.getElementById('totalValue');
                                    
                                    if (quantityInput && unitPriceInput && totalField) {
                                        const quantity = quantityInput.value;
                                        const unitPrice = unitPriceInput.value;
                                        if (quantity && unitPrice) {
                                            const total = (parseInt(quantity) * parseFloat(unitPrice)).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
                                            totalField.value = '$' + total;
                                        } else {
                                            totalField.value = '';
                                        }
                                    }
                                }
                                
                                // Only add event listeners if the form elements exist
                                const quantityInput = document.querySelector('input[name="quantity"]');
                                const unitPriceInput = document.querySelector('input[name="unitPrice"]');
                                if (quantityInput) {
                                    quantityInput.addEventListener('input', calculateTotal);
                                }
                                if (unitPriceInput) {
                                    unitPriceInput.addEventListener('input', calculateTotal);
                                }

                                function validateAddForm() {
                                    const form = document.getElementById('addMaterialForm');
                                    let isValid = true;
                                    const materialNameInput = form.querySelector('input[name="materialName"]');
                                    const materialIdInput = form.querySelector('input[name="materialId"]');
                                    const quantity = form.querySelector('input[name="quantity"]');
                                    const unitPrice = form.querySelector('input[name="unitPrice"]');

                                    if (!materialNameInput.value.trim()) {
                                        materialNameInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        materialNameInput.classList.remove('is-invalid');
                                    }

                                    if (!materialIdInput.value) {
                                        materialIdInput.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        materialIdInput.classList.remove('is-invalid');
                                    }

                                    if (!quantity.value || quantity.value <= 0) {
                                        quantity.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        quantity.classList.remove('is-invalid');
                                    }
                                    if (!unitPrice.value || unitPrice.value < 0) {
                                        unitPrice.classList.add('is-invalid');
                                        isValid = false;
                                    } else {
                                        unitPrice.classList.remove('is-invalid');
                                    }
                                    return isValid;
                                }

                                function validateConfirmForm() {
                                    const supplierIdInput = document.querySelector('input[name="supplierId"]');
                                    if (!supplierIdInput || !supplierIdInput.value) {
                                        alert('Please select a Purchase Order first to set the supplier.');
                                        return false;
                                    }
                                    return true;
                                }
        </script>
        <script>
            // Material autocomplete - only when form exists
            $(function() {
                var materialNameInput = $("#materialName");
                if (materialNameInput.length > 0) {
                    var materials = [
                        <c:forEach var="material" items="${materials}" varStatus="loop">
                        {
                            label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
                            value: "${fn:escapeXml(material.materialName)}",
                            id: "${material.materialId}",
                            name: "${fn:escapeXml(material.materialName)}",
                            code: "${fn:escapeXml(material.materialCode)}"
                        }${loop.last ? '' : ','}
                        </c:forEach>
                    ];
                    
                    materialNameInput.autocomplete({
                        source: function(request, response) {
                            var term = request.term.toLowerCase();
                            var matches = materials.filter(function(material) {
                                return material.name.toLowerCase().includes(term) || 
                                       material.code.toLowerCase().includes(term);
                            });
                            response(matches);
                        },
                        select: function(event, ui) {
                            $("#materialId").val(ui.item.id);
                            $("#materialName").val(ui.item.name);
                            $("#materialName").removeClass('is-invalid');
                        },
                        change: function(event, ui) {
                            if (!ui.item) {
                                var inputValue = $("#materialName").val().toLowerCase().trim();
                                var selectedMaterial = materials.find(function(material) {
                                    return material.name.toLowerCase() === inputValue || 
                                           material.code.toLowerCase() === inputValue;
                                });
                                if (selectedMaterial) {
                                    $("#materialId").val(selectedMaterial.id);
                                    $("#materialName").val(selectedMaterial.name);
                                    $("#materialName").removeClass('is-invalid');
                                } else {
                                    $("#materialId").val('');
                                    $("#materialName").addClass('is-invalid');
                                }
                            }
                        },
                        minLength: 1
                    });
                }
            });

            // Purchase Order autocomplete
            var purchaseOrdersData = [
                <c:forEach var="po" items="${sentToSupplierOrders}" varStatus="loop">
                {
                    label: "${fn:escapeXml(po.poCode)} - ${fn:escapeXml(po.purchaseRequestCode)} (${fn:escapeXml(po.createdByName)})",
                    value: "${fn:escapeXml(po.poCode)}",
                    id: "${po.poId}",
                    poCode: "${fn:escapeXml(po.poCode)}",
                    requestCode: "${fn:escapeXml(po.purchaseRequestCode)}",
                    createdByName: "${fn:escapeXml(po.createdByName)}"
                }${loop.last ? '' : ','}
                </c:forEach>
            ];
            
            var purchaseOrderNameInput = $("#purchaseOrderName");
            if (purchaseOrderNameInput.length > 0) {
                purchaseOrderNameInput.autocomplete({
                    source: function(request, response) {
                        var term = request.term.toLowerCase();
                        var matches = purchaseOrdersData.filter(function(po) {
                            return po.poCode.toLowerCase().includes(term) || 
                                   po.requestCode.toLowerCase().includes(term) ||
                                   po.createdByName.toLowerCase().includes(term);
                        });
                        response(matches);
                    },
                    select: function(event, ui) {
                        $("#purchaseOrderId").val(ui.item.id);
                        $("#purchaseOrderName").val(ui.item.poCode + " - " + ui.item.requestCode + " (" + ui.item.createdByName + ")");
                        $("#purchaseOrderName").removeClass('is-invalid');
                        $("#loadPoBtn").prop('disabled', false);
                    },
                    change: function(event, ui) {
                        if (!ui.item) {
                            var inputValue = $("#purchaseOrderName").val().toLowerCase().trim();
                            var selectedPO = purchaseOrdersData.find(function(po) {
                                return po.poCode.toLowerCase() === inputValue || 
                                       po.requestCode.toLowerCase() === inputValue;
                            });
                            if (selectedPO) {
                                $("#purchaseOrderId").val(selectedPO.id);
                                $("#purchaseOrderName").val(selectedPO.poCode + " - " + selectedPO.requestCode + " (" + selectedPO.createdByName + ")");
                                $("#purchaseOrderName").removeClass('is-invalid');
                                $("#loadPoBtn").prop('disabled', false);
                            } else {
                                $("#purchaseOrderId").val('');
                                $("#purchaseOrderName").addClass('is-invalid');
                                $("#loadPoBtn").prop('disabled', true);
                            }
                        }
                    },
                    minLength: 1
                });
            }
            
            // Purchase Order selection handling
            $(document).ready(function() {
                var purchaseOrderNameInput = $('#purchaseOrderName');
                var loadPoBtn = $('#loadPoBtn');
                
                purchaseOrderNameInput.on('input', function() {
                    var hasValue = $(this).val().trim() !== '';
                    loadPoBtn.prop('disabled', !hasValue);
                });
            });
        </script>
    </body>
</html>