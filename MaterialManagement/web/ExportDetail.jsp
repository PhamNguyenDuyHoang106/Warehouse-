<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Export Detail</title>
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
            <h2 class="fw-bold mb-4 text-center" style="color: #DEAD6F;">Export Detail - ${exportData.exportCode}</h2>
            
            <!-- Export Information Card -->
            <div class="card">
                <div class="card-header">Export Information</div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <p><strong><i class="fas fa-calendar me-1"></i>Export Date:</strong> 
                                <c:choose>
                                    <c:when test="${not empty exportDate}">
                                        <fmt:formatDate value="${exportDate}" pattern="dd/MM/yyyy HH:mm" />
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
                                    <c:when test="${not empty exportData.note}">${exportData.note}</c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <c:if test="${not empty exportData.exportRequestCode}">
                                <p><strong><i class="fas fa-file-alt me-1"></i>Export Request:</strong> ${exportData.exportRequestCode}</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Recipient Information Card -->
            <c:if test="${not empty recipient}">
                <div class="card">
                    <div class="card-header"><i class="fas fa-user-tag me-1"></i>Recipient Information</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Recipient Code:</strong> ${recipient.recipientCode}</p>
                                <p><strong>Recipient Name:</strong> ${recipient.recipientName}</p>
                                <p><strong>Contact Person:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty recipient.contactPerson}">${recipient.contactPerson}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty recipient.phoneNumber}">${recipient.phoneNumber}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty recipient.email}">${recipient.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Address:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty recipient.address}">${recipient.address}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Location:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty recipient.location}">${recipient.location}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${not empty recipient.description}">
                                    <p><strong>Description:</strong> ${recipient.description}</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Vehicle Information Card -->
            <c:if test="${not empty vehicle}">
                <div class="card">
                    <div class="card-header"><i class="fas fa-truck me-1"></i>Vehicle Information</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Vehicle Code:</strong> ${vehicle.vehicleCode}</p>
                                <p><strong>License Plate:</strong> ${vehicle.licensePlate}</p>
                                <p><strong>Vehicle Type:</strong> ${vehicle.vehicleType}</p>
                                <p><strong>Driver Name:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty vehicle.driverName}">${vehicle.driverName}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Driver Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty vehicle.driverPhone}">${vehicle.driverPhone}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Volume:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty vehicle.volume}">
                                            <fmt:formatNumber value="${vehicle.volume}" maxFractionDigits="2"/> m³
                                        </c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Weight:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty vehicle.weight}">
                                            <fmt:formatNumber value="${vehicle.weight}" maxFractionDigits="2"/> kg
                                        </c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Status:</strong> ${vehicle.status}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Exported By User Information Card -->
            <c:if test="${not empty exportedByUser}">
                <div class="card">
                    <div class="card-header"><i class="fas fa-user me-1"></i>Exported By</div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>Full Name:</strong> ${exportedByUser.fullName}</p>
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty exportedByUser.email}">${exportedByUser.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                        </div>
                        <div class="col-md-6">
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty exportedByUser.phoneNumber}">${exportedByUser.phoneNumber}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Department:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty exportedByUser.departmentName}">${exportedByUser.departmentName}</c:when>
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
                        <c:if test="${empty exportDetails}">
                            <p>No export details found for this export.</p>
                        </c:if>
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
                                <c:forEach var="detail" items="${exportDetails}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td class="img-cell">
                                            <c:choose>
                                                <c:when test="${not empty detail.materialsUrl}">
                                                    <img src="${pageContext.request.contextPath}/images/material/${detail.materialsUrl}" 
                                                         alt="${detail.materialName}" 
                                                         class="material-img">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/images/default-material.png" 
                                                         alt="No Image" 
                                                         class="material-img">
                                                </c:otherwise>
                                            </c:choose>
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
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.unitPriceExport}">
                                                    <fmt:formatNumber value="${detail.unitPriceExport}" type="currency" currencySymbol="VND" />
                                                </c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.unitPriceExport && not empty detail.quantity}">
                                                    <fmt:formatNumber value="${detail.quantity * detail.unitPriceExport}" type="currency" currencySymbol="VND" />
                                                </c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="d-flex gap-2 mb-2 justify-content-center">
                <a href="ExportList" class="btn btn-cancel">← Back to Export List</a>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>