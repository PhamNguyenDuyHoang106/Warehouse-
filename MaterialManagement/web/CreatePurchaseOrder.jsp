<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Purchase Order</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        .po-form .form-control, .po-form .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .po-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .po-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .po-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
        .po-form .material-image {
            height: 60px;
            width: 60px;
            object-fit: cover;
            border-radius: 8px;
            background: #f8f9fa;
            box-shadow: 0 1px 4px rgba(0,0,0,0.07);
            border: 1px solid #eee;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="SidebarEmployee.jsp" />
            </div>
            <div class="col-md-9 col-lg-10">
                <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                    <div class="container">
                        <div class="row my-5 py-5">
                            <div class="col-12 bg-white p-4 rounded shadow po-form">
                                <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Purchase Order</span></h2>
                                
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert">
                                        ${error}
                                    </div>
                                </c:if>
                                
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success" role="alert">
                                        ${success}
                                    </div>
                                </c:if>
                                
                                <c:if test="${not empty errors}">
                                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                                        <ul style="margin-bottom: 0;">
                                            <c:forEach var="error" items="${errors}">
                                                <li>${error.value}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>
                                
                                <form action="CreatePurchaseOrder" method="post" id="purchaseOrderForm">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="poCode" class="form-label text-muted">PO Code</label>
                                            <input type="text" class="form-control" id="poCode" name="poCode" 
                                                   value="${submittedPoCode != null ? submittedPoCode : poCode}" readonly>
                                            <c:if test="${not empty errors.poCode}">
                                                <div class="error-message">${errors.poCode}</div>
                                            </c:if>
                                        </div>
                                        
                                        <div class="col-md-6">
                                            <label for="purchaseRequestId" class="form-label text-muted">Purchase Request</label>
                                            <select class="form-select" id="purchaseRequestId" name="purchaseRequestId" onchange="loadPurchaseRequestDetails()">
                                                <option value="">Select Purchase Request</option>
                                                <c:forEach var="request" items="${purchaseRequests}">
                                                    <c:set var="poStatus" value="${poStatusMap[request.purchaseRequestId]}" />
                                                    <option value="${request.purchaseRequestId}" 
                                                            ${submittedPurchaseRequestId == request.purchaseRequestId.toString() || selectedPurchaseRequestId == request.purchaseRequestId ? 'selected' : ''}>
                                                        ${request.requestCode} - ${request.reason}<c:if test="${not empty poStatus}"> (${poStatus})</c:if>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty errors.purchaseRequestId}">
                                                <div class="error-message">${errors.purchaseRequestId}</div>
                                            </c:if>
                                        </div>
                                        
                                        <div class="col-12">
                                            <label for="note" class="form-label text-muted">Note</label>
                                            <textarea class="form-control" id="note" name="note" rows="3" 
                                                      placeholder="Enter any additional notes...">${submittedNote}</textarea>
                                        </div>
                                    </div>
                                    
                                    <c:if test="${not empty purchaseRequestDetailList}">
                                        <h3 class="fw-normal mt-5 mb-3">Purchase Order Details</h3>
                                        <div id="orderDetails">
                                            <c:forEach var="detail" items="${purchaseRequestDetailList}" varStatus="status">
                                                <div class="row material-row align-items-center gy-2">
                                                    <input type="hidden" name="materialIds[]" value="${detail.materialId}">
                                                    
                                                    <div class="col-md-2 text-center">
                                                        <img src="images/material/${materialImages[detail.materialId]}" 
                                                             alt="${detail.materialName}" class="material-image">
                                                    </div>
                                                    
                                                    <div class="col-md-3">
                                                        <label class="form-label text-muted">Material</label>
                                                        <input type="text" class="form-control" value="${detail.materialName}" readonly>
                                                        <small class="text-muted">Category: ${materialCategories[detail.materialId]}</small>
                                                    </div>
                                                    
                                                    <div class="col-md-2">
                                                        <label class="form-label text-muted">Quantity</label>
                                                        <input type="number" class="form-control" name="quantities[]" 
                                                               value="${submittedQuantities != null && submittedQuantities[status.index] != null ? submittedQuantities[status.index] : detail.quantity}"
                                                               min="0.01" step="0.01" required>
                                                        <small class="text-muted">Unit: ${materialUnits[detail.materialId]}</small>
                                                    </div>
                                                    
                                                    <div class="col-md-2">
                                                        <label class="form-label text-muted">Unit Price</label>
                                                        <input type="number" class="form-control" name="unitPrices[]" 
                                                               value="${submittedUnitPrices != null && submittedUnitPrices[status.index] != null ? submittedUnitPrices[status.index] : ''}"
                                                               step="0.01" min="0.01" required placeholder="0.00">
                                                    </div>
                                                    
                                                    <div class="col-md-3">
                                                        <label class="form-label text-muted">Supplier</label>
                                                        <select class="form-select" name="suppliers[]" required>
                                                            <option value="">Select Supplier</option>
                                                            <c:forEach var="supplier" items="${suppliers}">
                                                                <option value="${supplier.supplierId}" 
                                                                        ${submittedSuppliers != null && submittedSuppliers[status.index] == supplier.supplierId.toString() ? 'selected' : ''}>
                                                                    ${supplier.supplierName}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${empty purchaseRequestDetailList}">
                                        <div class="alert alert-info mt-4 text-center">
                                            <p class="mb-0">📦 Please select a purchase request to view and add materials</p>
                                        </div>
                                    </c:if>
                                    
                                    <div class="mt-5 d-grid gap-2">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1" ${empty purchaseRequestDetailList ? 'disabled' : ''}>Create Purchase Order</button>
                                        <a href="PurchaseOrderList" class="btn btn-outline-secondary btn-lg rounded-1">Cancel</a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
    <jsp:include page="Footer.jsp" />
    
    <script src="js/jquery-1.11.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function loadPurchaseRequestDetails() {
            const purchaseRequestId = document.getElementById('purchaseRequestId').value;
            if (purchaseRequestId) {
                window.location.href = 'CreatePurchaseOrder?purchaseRequestId=' + purchaseRequestId;
            }
        }
    </script>
</body>
</html>
