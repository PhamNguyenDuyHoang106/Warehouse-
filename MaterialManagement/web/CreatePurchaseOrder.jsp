<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Purchase Order</title>
        <link href="assets/vendor/bootstrap-5.1.3/css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <style>
            .btn-approve {
                background-color: #28a745;
                color: white;
                border: none;
            }
            .btn-reject {
                background-color: #dc3545;
                color: white;
                border: none;
            }
            .btn-cancel {
                background-color: #6c757d;
                color: white;
                border: none;
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
        <jsp:include page="Sidebar.jsp" />
        
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12">
                    <h2 class="mb-4">Create Purchase Order</h2>
                    
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
                    
                    <form action="CreatePurchaseOrder" method="post" id="purchaseOrderForm">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">Purchase Order Information</div>
                                    <div class="card-body">
                                        <div class="mb-3">
                                            <label for="poCode" class="form-label">PO Code <span style="color:#DEAD6F">*</span></label>
                                            <input type="text" class="form-control" id="poCode" name="poCode" 
                                                   value="${submittedPoCode != null ? submittedPoCode : poCode}" readonly>
                                            <c:if test="${not empty errors.poCode}">
                                                <div class="error-message">${errors.poCode}</div>
                                            </c:if>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="purchaseRequestId" class="form-label">Purchase Request <span style="color:#DEAD6F">*</span></label>
                                            <select class="form-select" id="purchaseRequestId" name="purchaseRequestId" onchange="loadPurchaseRequestDetails()">
                                                <option value="">Select Purchase Request</option>
                                                <c:forEach var="request" items="${purchaseRequests}">
                                                    <option value="${request.purchaseRequestId}" 
                                                            ${submittedPurchaseRequestId == request.purchaseRequestId.toString() || selectedPurchaseRequestId == request.purchaseRequestId ? 'selected' : ''}>
                                                        PR-${request.purchaseRequestId} - ${request.title} (${request.status})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty errors.purchaseRequestId}">
                                                <div class="error-message">${errors.purchaseRequestId}</div>
                                            </c:if>
                                        </div>
                                        
                                        <div class="mb-3">
                                            <label for="note" class="form-label">Note</label>
                                            <textarea class="form-control" id="note" name="note" rows="3" 
                                                      placeholder="Enter any additional notes...">${submittedNote}</textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">Materials from Purchase Request</div>
                                    <div class="card-body">
                                        <div id="materialsList">
                                            <c:if test="${not empty purchaseRequestDetailList}">
                                                <table class="table table-sm">
                                                    <thead>
                                                        <tr>
                                                            <th>Material</th>
                                                            <th>Category</th>
                                                            <th>Unit</th>
                                                            <th>Requested Qty</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="detail" items="${purchaseRequestDetailList}" varStatus="status">
                                                            <tr>
                                                                <td>
                                                                    <img src="images/material/${materialImages[detail.materialId]}" 
                                                                         alt="${detail.materialName}" style="width: 30px; height: 30px; object-fit: cover;">
                                                                    ${detail.materialName}
                                                                </td>
                                                                <td>${materialCategories[detail.materialId]}</td>
                                                                <td>${materialUnits[detail.materialId]}</td>
                                                                <td>${detail.quantity}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:if>
                                            <c:if test="${empty purchaseRequestDetailList}">
                                                <p class="text-muted">Select a purchase request to view materials</p>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="card mt-4">
                            <div class="card-header">Purchase Order Details</div>
                            <div class="card-body">
                                <div id="orderDetails">
                                    <c:if test="${not empty purchaseRequestDetailList}">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th>Material</th>
                                                    <th>Category</th>
                                                    <th>Unit</th>
                                                    <th>Quantity <span style="color:#DEAD6F">*</span></th>
                                                    <th>Unit Price <span style="color:#DEAD6F">*</span></th>
                                                    <th>Supplier <span style="color:#DEAD6F">*</span></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="detail" items="${purchaseRequestDetailList}" varStatus="status">
                                                    <tr>
                                                        <td>
                                                            <input type="hidden" name="materialIds[]" value="${detail.materialId}">
                                                            <img src="images/material/${materialImages[detail.materialId]}" 
                                                                 alt="${detail.materialName}" style="width: 40px; height: 40px; object-fit: cover;">
                                                            ${detail.materialName}
                                                        </td>
                                                        <td>${materialCategories[detail.materialId]}</td>
                                                        <td>${materialUnits[detail.materialId]}</td>
                                                        <td>
                                                            <input type="number" class="form-control" name="quantities[]" 
                                                                   value="${submittedQuantities != null && submittedQuantities[status.index] != null ? submittedQuantities[status.index] : detail.quantity}"
                                                                   min="1" required>
                                                            <c:if test="${not empty errors['quantity_' + status.index]}">
                                                                <div class="error-message">${errors['quantity_' + status.index]}</div>
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <input type="number" class="form-control" name="unitPrices[]" 
                                                                   value="${submittedUnitPrices != null && submittedUnitPrices[status.index] != null ? submittedUnitPrices[status.index] : ''}"
                                                                   step="0.01" min="0.01" required>
                                                            <c:if test="${not empty errors['unitPrice_' + status.index]}">
                                                                <div class="error-message">${errors['unitPrice_' + status.index]}</div>
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <select class="form-select" name="suppliers[]" required>
                                                                <option value="">Select Supplier</option>
                                                                <c:forEach var="supplier" items="${suppliers}">
                                                                    <option value="${supplier.supplierId}" 
                                                                            ${submittedSuppliers != null && submittedSuppliers[status.index] == supplier.supplierId.toString() ? 'selected' : ''}>
                                                                        ${supplier.supplierName}
                                                                    </option>
                                                                </c:forEach>
                                                            </select>
                                                            <c:if test="${not empty errors['supplier_' + status.index]}">
                                                                <div class="error-message">${errors['supplier_' + status.index]}</div>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </c:if>
                                    <c:if test="${empty purchaseRequestDetailList}">
                                        <p class="text-muted">Select a purchase request to add order details</p>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mt-4">
                            <button type="submit" class="btn btn-primary">Create Purchase Order</button>
                            <a href="PurchaseOrderList" class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        
        <script src="assets/vendor/bootstrap-5.1.3/js/bootstrap.bundle.min.js"></script>
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