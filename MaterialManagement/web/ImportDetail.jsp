<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
        <link rel="stylesheet" type="text/css" href="css/override-style.css">
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
                                        <fmt:formatDate value="${importDate}" pattern="dd/MM/yyyy" />
                                    </c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong><i class="fas fa-warehouse me-1"></i>Warehouse:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importData.warehouseName}">
                                        ${importData.warehouseName}
                                    </c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong><i class="fas fa-file-invoice me-1"></i>PO Code:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importData.poCode}">${importData.poCode}</c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        <div class="col-md-6">
                            <p><strong><i class="fas fa-info-circle me-1"></i>Status:</strong> 
                                <span class="badge bg-secondary text-uppercase">${importData.status}</span>
                            </p>
                            <p><strong><i class="fas fa-money-bill me-1"></i>Total Value:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importData.totalAmount}">
                                        <fmt:formatNumber value="${importData.totalAmount}" type="currency" currencySymbol="₫" />
                                    </c:when>
                                    <c:when test="${not empty totalValue}">
                                        <fmt:formatNumber value="${totalValue}" type="currency" currencySymbol="₫" />
                                    </c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong><i class="fas fa-sticky-note me-1"></i>Note:</strong> 
                                <c:choose>
                                    <c:when test="${not empty importData.note}">${importData.note}</c:when>
                                    <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                </c:choose>
                            </p>
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
                                <p><strong>Contact Person:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.contactPerson}">${supplier.contactPerson}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.phone}">${supplier.phone}</c:when>
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
                                <p><strong>Tax Code:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty supplier.taxCode}">${supplier.taxCode}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- User Information Cards -->
            <div class="row">
                <c:if test="${not empty createdByUser}">
                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header"><i class="fas fa-user-cog me-1"></i>Created By</div>
                            <div class="card-body">
                                <p><strong>Name:</strong> ${createdByUser.fullName}</p>
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty createdByUser.email}">${createdByUser.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty createdByUser.phone}">${createdByUser.phone}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Department:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty createdByUser.departmentName}">${createdByUser.departmentName}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty receivedByUser}">
                    <div class="col-md-6">
                        <div class="card mb-3">
                            <div class="card-header"><i class="fas fa-user-check me-1"></i>Received By</div>
                            <div class="card-body">
                                <p><strong>Name:</strong> ${receivedByUser.fullName}</p>
                                <p><strong>Email:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty receivedByUser.email}">${receivedByUser.email}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Phone:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty receivedByUser.phone}">${receivedByUser.phone}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                                <p><strong>Department:</strong> 
                                    <c:choose>
                                        <c:when test="${not empty receivedByUser.departmentName}">${receivedByUser.departmentName}</c:when>
                                        <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
            <div class="card">
                <div class="card-header">Material List</div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table custom-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Material</th>
                                    <th>Batch Code</th>
                                    <th>Rack / Warehouse</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Unit Cost</th>
                                    <th>Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${importDetails}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td>
                                        <div class="fw-semibold">${detail.materialName}</div>
                                        <small class="text-muted">${detail.materialCode}</small>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.batchCode}">${detail.batchCode}</c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${not empty detail.rackName}">${detail.rackName}</c:when>
                                                    <c:otherwise><span class="text-muted">Unassigned</span></c:otherwise>
                                                </c:choose>
                                            </div>
                                            <small class="text-muted">
                                                <c:choose>
                                                    <c:when test="${not empty detail.rackId && not empty rackToWarehouseMap[detail.rackId]}">
                                                        ${rackToWarehouseMap[detail.rackId]}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:choose>
                                                            <c:when test="${not empty importData.warehouseName}">${importData.warehouseName}</c:when>
                                                            <c:otherwise>N/A</c:otherwise>
                                                        </c:choose>
                                                    </c:otherwise>
                                                </c:choose>
                                            </small>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${detail.quantity}" maxFractionDigits="2" />
                                            <c:if test="${detail.expiryDate != null}">
                                                <br><small class="text-muted">Expiry: <fmt:formatDate value="${detail.expiryDate}" pattern="dd/MM/yyyy"/></small>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty detail.unitName}">${detail.unitName}</c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${detail.unitCost != null}">
                                                    <fmt:formatNumber value="${detail.unitCost}" type="currency" currencySymbol="₫" />
                                                </c:when>
                                                <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${detail.totalCost != null}">
                                                    <fmt:formatNumber value="${detail.totalCost}" type="currency" currencySymbol="₫" />
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatNumber value="${detail.quantity * detail.unitCost}" type="currency" currencySymbol="₫" />
                                                </c:otherwise>
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
                <a href="ImportList" class="btn btn-cancel">← Back to Import List</a>
            </div>
                  </div>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>