<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Import Detail</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f8f9fa;
            }
            .container {
                max-width: 1200px;
                margin: 50px auto;
                padding: 20px;
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            .card {
                border: none;
                margin-bottom: 20px;
            }
            .card-header {
                background-color: #DEAD6F;
                border-bottom: none;
                font-weight: bold;
                color: #fff;
            }
            .custom-table {
                margin-top: 20px;
            }
            .btn {
                margin-right: 10px;
                border: none;
                color: #fff;
            }
            .btn-cancel {
                background-color: #DEAD6F;
                color: #fff;
                border: none;
            }
            .btn-cancel:hover {
                background-color: #c79b5a;
                color: #fff;
            }
            .custom-table thead th {
                background-color: #f9f5f0;
                color: #5c4434;
                font-weight: 600;
            }
            .custom-table tbody tr:hover {
                background-color: #f1f1f1;
            }
            .custom-table th,
            .custom-table td {
                vertical-align: middle;
                padding: 12px 8px;
            }
            .img-cell {
                width: 60px;
                height: 60px;
                text-align: center;
            }
            .material-img {
                width: 60px;
                height: 60px;
                object-fit: cover;
                border-radius: 5px;
                display: block;
                background-color: #eee;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />
        <div class="container">
            <h2 class="fw-bold mb-4 text-center" style="color: #DEAD6F;">Import Detail - ${importData.importCode}</h2>
            
            <!-- Import Information Card -->
            <div class="card">
                <div class="card-header">Import Information</div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <p><strong><i class="fas fa-calendar me-1"></i>Import Date:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importDate}">
                                        <fmt:formatDate value="${importDate}" pattern="dd/MM/yyyy HH:mm" />
                                    </c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong><i class="fas fa-warehouse me-1"></i>Warehouse(s):</strong> 
                                <c:choose>
                                    <c:when test="${not empty warehouseNames}">${warehouseNames}</c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong><i class="fas fa-money-bill me-1"></i>Total Value:</strong> 
                                <c:choose>
                                    <c:when test="${not empty totalValue}">
                                        <fmt:formatNumber value="${totalValue}" type="currency" currencySymbol="VND" />
                                    </c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        <div class="col-md-6">
                            <p><strong><i class="fas fa-sticky-note me-1"></i>Note:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importData.note}">${importData.note}</c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <c:if test="${not empty actualArrival}">
                                <p><strong><i class="fas fa-clock me-1"></i>Actual Arrival:</strong> 
                                    <fmt:formatDate value="${actualArrival}" pattern="dd/MM/yyyy HH:mm" />
                                </p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Supplier Information Card -->
            <c:if test="${not empty supplier}">
                <div class="card">
                    <div class="card-header"><i class="fas fa-truck me-1"></i>Supplier Information</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Supplier Code:</strong> ${supplier.supplierCode}</p>
                                <p><strong>Supplier Name:</strong> ${supplier.supplierName}</p>
                                <p><strong>Contact Info:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.contactInfo}">${supplier.contactInfo}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.phoneNumber}">${supplier.phoneNumber}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.email}">${supplier.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Address:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.address}">${supplier.address}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Tax ID:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.taxId}">${supplier.taxId}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${not empty supplier.description}">
                                    <p><strong>Description:</strong> ${supplier.description}</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Imported By User Information Card -->
            <c:if test="${not empty importedByUser}">
                <div class="card">
                    <div class="card-header"><i class="fas fa-user me-1"></i>Imported By</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Full Name:</strong> ${importedByUser.fullName}</p>
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty importedByUser.email}">${importedByUser.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty importedByUser.phoneNumber}">${importedByUser.phoneNumber}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Department:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty importedByUser.departmentName}">${importedByUser.departmentName}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            <div class="card">
                <div class="card-header">Material List</div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table custom-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Image</th>
                                    <th>Material Name</th>
                                    <th>Warehouse</th>
                                    <th>Rack</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Unit Price</th>
                                    <th>Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${importDetails}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td class="img-cell">
                                            <img 
                                                src="${pageContext.request.contextPath}/images/material/${detail.materialsUrl}" 
                                                alt="${detail.materialName}" 
                                                class="material-img" 
                                                data-fallback="${pageContext.request.contextPath}/images/default-material.png">
                                        </td>
                                        <td>${detail.materialName}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.rackId && not empty rackToWarehouseMap[detail.rackId]}">
                                                    ${rackToWarehouseMap[detail.rackId]}
                                                </c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.rackCode}">${detail.rackCode}</c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatNumber value="${detail.quantity}" maxFractionDigits="2" /></td>
                                        <td>${detail.unitName}</td>
                                        <td><fmt:formatNumber value="${detail.unitPrice}" type="currency" currencySymbol="VND" /></td>
                                        <td><fmt:formatNumber value="${detail.quantity * detail.unitPrice}" type="currency" currencySymbol="VND" /></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="d-flex gap-2 mb-2 justify-content-center">
                <a href="ImportList" class="btn btn-cancel">← Back to Import List</a>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>