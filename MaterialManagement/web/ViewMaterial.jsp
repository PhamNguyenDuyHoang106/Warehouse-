<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
  <head>
    <title>View Material Details</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
      rel="stylesheet"
    />
    <style>
      .material-details {
        padding: 30px;
        background: white;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      }
      .material-image {
        width: 200px;
        height: 200px;
        border-radius: 8px;
        overflow: hidden;
        margin-bottom: 20px;
        border: 1px solid #eee;
      }
      .material-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      .detail-row {
        margin-bottom: 15px;
        border-bottom: 1px solid #eee;
        padding-bottom: 15px;
      }
      .detail-label {
        font-weight: 600;
        color: #666;
      }
      .status-badge {
        padding: 6px 15px;
        border-radius: 20px;
        font-size: 0.9em;
        font-weight: 500;
        display: inline-block;
      }
      .status-new {
        background-color: #00c3ff;
        color: white;
      }
      .status-used {
        background-color: #4169e1;
        color: white;
      }
      .status-damaged {
        background-color: #ffd700;
        color: black;
      }
      .condition-bar {
        height: 8px;
        border-radius: 4px;
        margin-top: 8px;
      }
      .info-section {
        background: #f8f9fa;
        padding: 20px;
        border-radius: 8px;
        margin-bottom: 20px;
      }
      .info-section h3 {
        color: #2c3e50;
        font-size: 1.2rem;
        margin-bottom: 15px;
      }
      /* Custom button styles */
      .btn-edit-material {
        background-color: #e9b775;
        color: #fff;
        border: none;
        transition: all 0.2s;
      }
      .btn-edit-material:hover {
        background-color: #fff;
        color: #e9b775;
        border: 1px solid #e9b775;
      }
      .btn-back-dashboard {
        background-color: #222;
        color: #fff;
        border: none;
        margin-right: 10px;
        transition: all 0.2s;
      }
      .btn-back-dashboard:hover {
        background-color: #fff;
        color: #222;
        border: 1px solid #222;
      }
      .material-title {
        color: #e9b775;
        font-weight: bold;
      }
      .action-buttons {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        margin-bottom: 20px;
      }
      .material-header {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 32px;
      }
      .material-header .action-buttons {
        margin-bottom: 0;
        margin-right: 16px;
      }
      .material-title-box {
        background: #e9b775;
        color: #fff;
        font-weight: bold;
        font-size: 2.2rem;
        border-radius: 10px;
        padding: 10px 40px;
        text-align: center;
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    </style>
  </head>
  <body class="bg-light">
    <div class="container py-4">
      <div class="material-header">
        <div class="material-title-box">Material Detail</div>
      </div>
      <div class="material-details">
        <div class="row">
          <div class="col-md-4">
            <div class="material-image">
              <c:set var="mediaUrl" value="${m.materialsUrl}" />
              <c:choose>
                <c:when test="${fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/')}">
                  <img
                    src="${mediaUrl}"
                    alt="${m.materialName}"
                    style="width: 200px; height: 200px; object-fit: cover"
                    onerror="this.onerror=null;this.src='images/material/default.jpg';"
                  />
                </c:when>
                <c:otherwise>
                  <img
                    src="${pageContext.request.contextPath}/${mediaUrl}"
                    alt="${m.materialName}"
                    style="width: 200px; height: 200px; object-fit: cover"
                    onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/material/default.jpg';"
                  />
                </c:otherwise>
              </c:choose>
            </div>
          </div>
          <div class="col-md-8">
            <h2 class="mb-4 material-title">
              ${empty m.materialName ? 'No Information' : m.materialName}
            </h2>
            <div class="detail-row">
              <div class="detail-label">Material Code</div>
              <div>
                ${empty m.materialCode ? 'No Information' : m.materialCode}
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-label">Status</div>
              <div>
                <span
                  class="badge ${m.materialStatus == 'active' ? 'bg-success' : (m.materialStatus == 'inactive' ? 'bg-warning' : 'bg-secondary')}"
                >
                  ${m.materialStatus == 'active' ? 'Active' : (m.materialStatus ==
                  'inactive' ? 'Inactive' : 'Discontinued')}
                </span>
              </div>
            </div>
            <div class="row mb-2">
              <div class="col-sm-4 fw-bold">Last Updated:</div>
              <div class="col-sm-8">
                <fmt:formatDate
                  value="${m.updatedAt}"
                  pattern="yyyy-MM-dd HH:mm:ss"
                />
              </div>
            </div>
            <div class="row mb-2">
              <div class="col-sm-4 fw-bold">Created At:</div>
              <div class="col-sm-8">
                <fmt:formatDate
                  value="${m.createdAt}"
                  pattern="yyyy-MM-dd HH:mm:ss"
                />
              </div>
            </div>
          </div>
        </div>
        <!-- Category Information -->
        <div class="info-section mt-4">
          <h3><i class="fas fa-folder me-2"></i>Category Information</h3>
          <div class="row">
            <div class="col-md-6">
              <div class="detail-row">
                <div class="detail-label">Category Name</div>
                <div>
                  ${empty m.category.category_name ? 'No Information' :
                  m.category.category_name}
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-label">Description</div>
                <div>
                  ${empty m.category.description ? 'No Information' :
                  m.category.description}
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- Unit Information -->
        <div class="info-section">
          <h3><i class="fas fa-ruler me-2"></i>Unit Information</h3>
          <div class="row">
            <div class="col-md-6">
              <div class="detail-row">
                <div class="detail-label">Unit Name</div>
                <div>
                  ${empty m.unit.unitName ? 'No Information' :
                  m.unit.unitName}
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-label">Symbol</div>
                <div>
                  ${empty m.unit.symbol ? 'No Information' : m.unit.symbol}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- V9.1: Material Physical Properties -->
        <div class="info-section">
          <h3><i class="fas fa-cube me-2"></i>Physical Properties (V9.1)</h3>
          <div class="row">
            <div class="col-md-6">
              <div class="detail-row">
                <div class="detail-label">Volume per Unit</div>
                <div>
                  <c:choose>
                    <c:when test="${m.unitVolume != null && m.unitVolume.doubleValue() > 0}">
                      <fmt:formatNumber value="${m.unitVolume}" maxFractionDigits="4"/> m³
                    </c:when>
                    <c:otherwise>
                      <span class="text-muted">Not Updated</span>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="detail-row">
                <div class="detail-label">Weight per Unit</div>
                <div>
                  <c:choose>
                    <c:when test="${m.unitWeight != null && m.unitWeight.doubleValue() > 0}">
                      <fmt:formatNumber value="${m.unitWeight}" maxFractionDigits="4"/> kg
                    </c:when>
                    <c:otherwise>
                      <span class="text-muted">Not Updated</span>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
          </div>
          <c:if test="${m.unitVolume != null && m.unitWeight != null && m.unitVolume.doubleValue() > 0 && m.unitWeight.doubleValue() > 0 && m.quantity != null && m.quantity.doubleValue() > 0}">
            <div class="row mt-3 pt-3 border-top">
              <div class="col-md-6">
                <div class="detail-row">
                  <div class="detail-label">Total Volume (Calculated)</div>
                  <div>
                    <fmt:formatNumber value="${m.unitVolume.doubleValue() * m.quantity.doubleValue()}" maxFractionDigits="2"/> m³
                    <br><small class="text-muted">(Volume per Unit × Total Quantity)</small>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="detail-row">
                  <div class="detail-label">Total Weight (Calculated)</div>
                  <div>
                    <fmt:formatNumber value="${m.unitWeight.doubleValue() * m.quantity.doubleValue()}" maxFractionDigits="2"/> kg
                    <br><small class="text-muted">(Weight per Unit × Total Quantity)</small>
                  </div>
                </div>
              </div>
            </div>
          </c:if>
        </div>

        <!-- Inventory by Racks (V8 - Material can be in multiple racks) -->
        <div class="info-section">
          <h3><i class="fas fa-warehouse me-2"></i>Inventory by Racks</h3>
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="alert alert-info">
                <strong>Total Stock:</strong> 
                <fmt:formatNumber value="${totalStock}" maxFractionDigits="2"/> 
                ${not empty m.unit.unitName ? m.unit.unitName : ''}
                <span class="text-muted ms-2">
                  (Distributed across ${inventoryByRacks.size()} rack(s))
                </span>
              </div>
            </div>
          </div>
          
          <c:choose>
            <c:when test="${empty inventoryByRacks}">
              <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle me-2"></i>
                No inventory records found for this material.
              </div>
            </c:when>
            <c:otherwise>
              <div class="table-responsive">
                <table class="table table-hover table-bordered">
                  <thead class="table-light">
                    <tr>
                      <th><i class="fas fa-warehouse me-1"></i>Warehouse</th>
                      <th><i class="fas fa-barcode me-1"></i>Rack Code</th>
                      <th class="text-center"><i class="fas fa-cubes me-1"></i>Stock</th>
                      <th><i class="fas fa-balance-scale me-1"></i>Unit</th>
                      <th><i class="fas fa-clock me-1"></i>Last Updated</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="inv" items="${inventoryByRacks}">
                      <tr>
                        <td><strong>${not empty inv.warehouseName ? inv.warehouseName : 'N/A'}</strong></td>
                        <td><strong>${not empty inv.rackCode ? inv.rackCode : 'N/A'}</strong></td>
                        <td class="text-center">
                          <span class="badge ${inv.stock >= 100 ? 'bg-success' : (inv.stock >= 10 ? 'bg-warning' : 'bg-danger')}">
                            <fmt:formatNumber value="${inv.stock}" maxFractionDigits="2"/>
                          </span>
                        </td>
                        <td>${not empty inv.unitName ? inv.unitName : 'N/A'}</td>
                        <td>
                          <c:choose>
                            <c:when test="${not empty inv.lastUpdated}">
                              <c:set var="dateStr" value="${inv.lastUpdated.toString()}" />
                              <c:set var="dateFormatted" value="${fn:replace(dateStr, 'T', ' ')}" />
                              <c:choose>
                                <c:when test="${fn:length(dateFormatted) > 16}">
                                  ${fn:substring(dateFormatted, 0, 16)}
                                </c:when>
                                <c:otherwise>
                                  ${dateFormatted}
                                </c:otherwise>
                              </c:choose>
                            </c:when>
                            <c:otherwise>
                              <span class="text-muted">-</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:otherwise>
          </c:choose>
        </div>

        <!-- Material Batches (FIFO Tracking - Different prices per batch) -->
        <c:if test="${not empty batches}">
          <div class="info-section">
            <h3><i class="fas fa-boxes me-2"></i>Material Batches (FIFO Tracking)</h3>
            <p class="text-muted mb-3">
              <i class="fas fa-info-circle me-1"></i>
              This material has multiple batches with different unit costs. Batches are allocated using FIFO (First In First Out) method.
            </p>
            
            <div class="table-responsive">
              <table class="table table-hover table-bordered">
                <thead class="table-light">
                  <tr>
                    <th><i class="fas fa-hashtag me-1"></i>Batch ID</th>
                    <th><i class="fas fa-warehouse me-1"></i>Warehouse</th>
                    <th><i class="fas fa-barcode me-1"></i>Rack</th>
                    <th class="text-center"><i class="fas fa-arrow-down me-1"></i>Quantity In</th>
                    <th class="text-center"><i class="fas fa-arrow-right me-1"></i>Remaining</th>
                    <th class="text-center"><i class="fas fa-dollar-sign me-1"></i>Unit Cost</th>
                    <th class="text-center"><i class="fas fa-dollar-sign me-1"></i>Total Cost</th>
                    <th><i class="fas fa-calendar me-1"></i>Received Date</th>
                    <th><i class="fas fa-info-circle me-1"></i>Status</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="batch" items="${batches}">
                    <tr>
                      <td><strong>#${batch.batchId}</strong></td>
                      <td><strong>${not empty batch.warehouseName ? batch.warehouseName : 'N/A'}</strong></td>
                      <td>${not empty batch.rackCode ? batch.rackCode : 'N/A'}</td>
                      <td class="text-center">
                        <fmt:formatNumber value="${batch.quantityIn}" maxFractionDigits="2"/>
                      </td>
                      <td class="text-center">
                        <span class="badge ${batch.quantityRemaining > 0 ? 'bg-success' : 'bg-secondary'}">
                          <fmt:formatNumber value="${batch.quantityRemaining}" maxFractionDigits="2"/>
                        </span>
                      </td>
                      <td class="text-center">
                        <fmt:formatNumber value="${batch.unitCost}" maxFractionDigits="2" type="currency"/>
                      </td>
                      <td class="text-center">
                        <strong>
                          <fmt:formatNumber value="${batch.quantityIn * batch.unitCost}" maxFractionDigits="2" type="currency"/>
                        </strong>
                      </td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty batch.receivedDate}">
                            <fmt:formatDate value="${batch.receivedDate}" pattern="yyyy-MM-dd HH:mm" />
                          </c:when>
                          <c:otherwise>
                            <span class="text-muted">-</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <span class="badge ${batch.status == 'active' ? 'bg-success' : (batch.status == 'depleted' ? 'bg-secondary' : 'bg-warning')}">
                          ${batch.status}
                        </span>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
                <tfoot class="table-light">
                  <tr>
                    <td colspan="2"><strong>Total</strong></td>
                    <td class="text-center">
                      <strong>
                        <fmt:formatNumber value="${totalBatchQuantityIn}" maxFractionDigits="2"/>
                      </strong>
                    </td>
                    <td class="text-center">
                      <strong>
                        <fmt:formatNumber value="${totalBatchQuantityRemaining}" maxFractionDigits="2"/>
                      </strong>
                    </td>
                    <td colspan="4"></td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </c:if>
        <div
          class="action-buttons"
          style="justify-content: flex-start; margin-top: 32px"
        >
          <a href="dashboardmaterial" class="btn btn-back-dashboard">
            <i class="fas fa-arrow-left"></i> Back to List Material
          </a>
          <a
            href="editmaterial?materialId=${m.materialId}"
            class="btn btn-edit-material"
          >
            <i class="fas fa-edit"></i> Edit Material
          </a>
        </div>
      </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
