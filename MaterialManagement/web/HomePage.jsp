<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html lang="en">
  <head>
    <title>Smart Material Dashboard</title>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <style>
      :root {
        --card-radius: 20px;
        --shadow-md: 0 12px 30px rgba(15, 23, 42, 0.12);
        --text-muted: #6b7280;
      }
      
      body {
        background: #f5f6fa;
        font-family: 'Inter', 'Roboto', sans-serif;
        color: #0f172a;
        min-height: 100vh;
      }

      .dashboard-container {
        padding-left: min(4vw, 2.5rem);
        padding-right: min(4vw, 2.5rem);
      }

      .dashboard-topbar {
        background: #fff;
        border-radius: var(--card-radius);
        box-shadow: var(--shadow-md);
        padding: 1.75rem;
        margin-bottom: 1.75rem;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        gap: 1rem;
        align-items: center;
      }

      .dashboard-topbar h2 {
        font-weight: 600;
        margin-bottom: 0.25rem;
      }

      .topbar-actions {
        display: flex;
        align-items: center;
        gap: 1rem;
        flex-wrap: wrap;
        justify-content: flex-end;
      }

      .profile-chip {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.5rem 1rem;
        background: #f8fafc;
        border-radius: 50px;
      }

      .avatar-circle {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        background: linear-gradient(135deg, #2563eb, #4f46e5);
        color: #fff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
      }

      .quick-actions .btn {
        border-radius: 999px;
        padding: 0.5rem 1.2rem;
        font-weight: 500;
      }

      .notifications .btn {
        position: relative;
      }
      
      .notifications .badge {
        position: absolute;
        top: -6px;
        right: -6px;
      }

      .notification-list {
        min-width: 260px;
        max-height: 300px;
        overflow-y: auto;
      }

      .kpi-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
        gap: 1rem;
        margin-bottom: 1.5rem;
      }

      .kpi-card {
        background: #fff;
        border-radius: var(--card-radius);
        padding: 1.25rem;
        box-shadow: var(--shadow-md);
        border-left: 5px solid transparent;
        display: flex;
        gap: 1rem;
        align-items: center;
      }

      .kpi-card .icon-badge {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.25rem;
        background: #eef2ff;
        color: #4338ca;
      }

      .kpi-card h3 {
        margin: 0;
        font-size: 1.8rem;
        font-weight: 700;
      }

      .kpi-card .meta {
        color: var(--text-muted);
        font-size: 0.9rem;
      }

      .accent-primary { border-left-color: #3b82f6; }
      .accent-gold { border-left-color: #f59e0b; }
      .accent-success { border-left-color: #10b981; }
      .accent-warning { border-left-color: #f97316; }
      .accent-info { border-left-color: #0ea5e9; }
      .accent-secondary { border-left-color: #8b5cf6; }

      .chart-card,
      .section-card,
      .capacity-card,
      .director-card {
        background: #fff;
        border-radius: var(--card-radius);
        padding: 1.5rem;
        box-shadow: var(--shadow-md);
        height: 100%;
      }

      .chart-card .card-header,
      .section-card .card-header {
        border-bottom: 1px solid #e2e8f0;
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
      }

      .capacity-card .progress {
        height: 10px;
        border-radius: 20px;
        background: #e2e8f0;
      }

      .capacity-card .progress-bar {
        border-radius: 20px;
      }

      .alerts-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
        gap: 1.25rem;
      }

      .alert-card {
        border-radius: var(--card-radius);
        padding: 1.25rem;
        color: #0f172a;
      }

      .alert-card ul {
        list-style: none;
        padding: 0;
        margin: 1rem 0 0 0;
      }

      .alert-card li {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0;
        border-bottom: 1px dashed rgba(15, 23, 42, 0.1);
      }

      .alert-card li:last-child {
        border-bottom: none;
      }

      .alert-card.warning { background: linear-gradient(135deg, #fff9c4, #ffecb3); }
      .alert-card.danger { background: linear-gradient(135deg, #ffe0e0, #ffcdd2); }
      .alert-card.info { background: linear-gradient(135deg, #e0f2fe, #bae6fd); }

      .requests-card table thead {
        background: #f8fafc;
      }

      .table-dashboard th {
        font-size: 0.85rem;
        color: var(--text-muted);
        text-transform: uppercase;
      }

      .table-dashboard td {
        vertical-align: middle;
      }

      .recent-list {
        max-height: 360px;
        overflow-y: auto;
      }

      .recent-item {
        display: flex;
        justify-content: space-between;
        padding: 0.75rem 0;
        border-bottom: 1px solid #f1f5f9;
      }

      .flow-board .flow-card {
        background: #fff;
        border-radius: var(--card-radius);
        box-shadow: var(--shadow-md);
        padding: 1rem;
        height: 100%;
      }

      .flow-steps {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 0.75rem;
      }

      .flow-step {
        border-radius: 12px;
        padding: 0.85rem;
        background: #f8fafc;
      }

      .flow-step h4 {
        margin: 0;
        font-weight: 700;
      }

      .flow-step .key {
        font-size: 0.75rem;
        text-transform: uppercase;
        color: var(--text-muted);
      }

      .flow-step.success { background: #ecfdf5; }
      .flow-step.warning { background: #fefce8; }
      .flow-step.primary { background: #eff6ff; }
      .flow-step.info { background: #e0f2fe; }

      .director-card h2 {
        font-weight: 700;
        font-size: 2.4rem;
      }

      @media (max-width: 991px) {
        .topbar-actions {
          justify-content: flex-start;
        }
        .flow-steps {
          grid-template-columns: repeat(1, minmax(0, 1fr));
        }
      }
    </style>
  </head>
  <body>
    <jsp:include page="Header.jsp" />

    <div class="main-content-wrapper">
      <div class="sidebar-wrapper-inner">
        <jsp:include page="Sidebar.jsp" />
      </div>
      
      <div class="main-content-body">
        <div class="container-fluid dashboard-container py-4">
          <div class="kpi-grid">
            <c:forEach var="card" items="${topWidgets}">
              <div class="kpi-card ${card.accent}">
                <div class="icon-badge">
                  <i class="${card.icon}"></i>
            </div>
                <div>
                  <p class="mb-1 text-muted text-uppercase" style="letter-spacing: 0.08em;">${card.title}</p>
                  <h3 class="mb-1">
                    <c:choose>
                      <c:when test="${card.format eq 'currency'}">
                        <fmt:formatNumber value="${card.value}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                      </c:when>
                      <c:otherwise>
                        <fmt:formatNumber value="${card.value}" type="number" maxFractionDigits="0" />
                      </c:otherwise>
                    </c:choose>
                  </h3>
                  <span class="meta">${card.meta}</span>
                  </div>
                </div>
            </c:forEach>
                  </div>

          <div class="row g-4 mb-4">
            <div class="col-xl-7">
              <div class="chart-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <div>
                    <h6 class="mb-0">Nhập - Xuất theo tháng</h6>
                    <small class="text-muted">Chỉ tính đơn đã hoàn tất</small>
                </div>
                  </div>
                <canvas id="importExportChart" height="260"></canvas>
                </div>
                  </div>
            <div class="col-xl-5">
              <div class="chart-card">
                <div class="card-header">
                  <h6 class="mb-0">Tồn kho theo loại vật tư</h6>
                </div>
                <canvas id="categoryPieChart" height="260"></canvas>
              </div>
            </div>
          </div>

          <div class="row g-4 mb-4">
            <div class="col-xl-6">
              <div class="chart-card">
                <div class="card-header">
                  <h6 class="mb-0">Giá trị tồn kho 12 tháng</h6>
              </div>
                <canvas id="inventoryValueChart" height="260"></canvas>
            </div>
                    </div>
            <div class="col-xl-6">
              <div class="capacity-card">
                <div class="card-header mb-3">
                  <h6 class="mb-0">Khoảng trống kho</h6>
                  <small class="text-muted">Theo tổng volume & weight</small>
                    </div>
                <div class="mb-4">
                  <div class="d-flex justify-content-between">
                    <span>Volume</span>
                    <strong><fmt:formatNumber value="${warehouseCapacityUsage.volumePercent}" maxFractionDigits="1" />%</strong>
                  </div>
                  <div class="progress my-2">
                    <div class="progress-bar bg-primary" style="width: ${warehouseCapacityUsage.volumePercent}%"></div>
                </div>
                  <small class="text-muted">
                    <fmt:formatNumber value="${warehouseCapacityUsage.usedVolume}" maxFractionDigits="0" /> /
                    <fmt:formatNumber value="${warehouseCapacityUsage.maxVolume}" maxFractionDigits="0" /> m³
                  </small>
                    </div>
                        <div>
                  <div class="d-flex justify-content-between">
                    <span>Weight</span>
                    <strong><fmt:formatNumber value="${warehouseCapacityUsage.weightPercent}" maxFractionDigits="1" />%</strong>
                    </div>
                  <div class="progress my-2">
                    <div class="progress-bar bg-success" style="width: ${warehouseCapacityUsage.weightPercent}%"></div>
                  </div>
                  <small class="text-muted">
                    <fmt:formatNumber value="${warehouseCapacityUsage.usedWeight}" maxFractionDigits="0" /> /
                    <fmt:formatNumber value="${warehouseCapacityUsage.maxWeight}" maxFractionDigits="0" /> kg
                  </small>
                </div>
              </div>
            </div>
          </div>

          <div class="alerts-grid mb-4">
            <div class="alert-card warning">
              <div class="d-flex justify-content-between align-items-center">
                        <div>
                  <h6 class="mb-0">🔥 Sắp hết hàng</h6>
                  <small>Đang thấp hơn mức min-stock</small>
                        </div>
                <span class="badge bg-dark text-white">${fn:length(lowStockAlerts)}</span>
                      </div>
              <c:if test="${empty lowStockAlerts}">
                <p class="text-muted mb-0 mt-3">Không có cảnh báo.</p>
                </c:if>
              <ul>
                <c:forEach var="alert" items="${lowStockAlerts}">
                  <li>
                        <div>
                      <strong>${alert.material}</strong>
                      <div class="text-muted small">${alert.category}</div>
                        </div>
                    <span class="badge bg-warning text-dark">
                      <fmt:formatNumber value="${alert.quantity}" maxFractionDigits="0" /> units
                    </span>
                  </li>
                </c:forEach>
              </ul>
                      </div>
            <div class="alert-card danger">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h6 class="mb-0">⏳ Sắp hết hạn</h6>
                  <small>Trong 30 ngày tới</small>
                    </div>
                <span class="badge bg-dark text-white">${fn:length(expiryAlerts)}</span>
                  </div>
              <c:if test="${empty expiryAlerts}">
                <p class="text-muted mb-0 mt-3">Không có cảnh báo.</p>
                </c:if>
              <ul>
                <c:forEach var="alert" items="${expiryAlerts}">
                  <li>
                          <div>
                      <strong>${alert.material}</strong>
                      <div class="text-muted small">${alert.category}</div>
                          </div>
                    <span class="badge bg-danger">
                      ${alert.detail}
                    </span>
                  </li>
                </c:forEach>
              </ul>
                        </div>
            <div class="alert-card info">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h6 class="mb-0">🐢 Chậm luân chuyển</h6>
                  <small>Không phát sinh > 60 ngày</small>
                      </div>
                <span class="badge bg-dark text-white">${fn:length(slowMovementAlerts)}</span>
                    </div>
              <c:if test="${empty slowMovementAlerts}">
                <p class="text-muted mb-0 mt-3">Không có cảnh báo.</p>
                  </c:if>
              <ul>
                <c:forEach var="alert" items="${slowMovementAlerts}">
                  <li>
                          <div>
                      <strong>${alert.material}</strong>
                      <div class="text-muted small">${alert.category}</div>
                          </div>
                    <span class="badge bg-info text-dark">
                      ${alert.detail}
                    </span>
                  </li>
                </c:forEach>
              </ul>
                        </div>
                      </div>

          <div class="row g-4 mb-4">
            <div class="col-xl-7">
              <div class="section-card requests-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <h6 class="mb-0">Requests đang chờ xử lý</h6>
                  <a href="ViewRequests" class="text-decoration-none small">Xem tất cả</a>
                    </div>
                <div class="table-responsive">
                  <table class="table table-dashboard align-middle mb-0">
                    <thead>
                      <tr>
                        <th>Loại đơn</th>
                        <th>Mã</th>
                        <th>Người tạo</th>
                        <th>Ngày</th>
                        <th>Trạng thái</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:if test="${empty pendingRequests}">
                        <tr>
                          <td colspan="6" class="text-center text-muted py-4">Không có yêu cầu nào.</td>
                        </tr>
                  </c:if>
                      <c:forEach var="req" items="${pendingRequests}">
                        <tr>
                          <td>${req.type}</td>
                          <td>
                            <span class="fw-semibold">${req.code}</span>
                          </td>
                          <td>${req.owner}</td>
                          <td>${req.dateDisplay}</td>
                          <td>
                            <span class="badge text-bg-light text-uppercase">${req.status}</span>
                          </td>
                          <td>
                            <a href="${req.actionUrl}" class="btn btn-sm btn-outline-primary">${req.actionLabel}</a>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                          </div>
                        </div>
                      </div>
            <div class="col-xl-5">
              <div class="section-card recent-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <h6 class="mb-0">Nhập/Xuất gần nhất</h6>
                    </div>
                <div class="recent-list">
                  <c:if test="${empty recentTransactions}">
                    <p class="text-muted">Chưa có giao dịch trong tuần.</p>
                  </c:if>
                  <c:forEach var="txn" items="${recentTransactions}">
                    <div class="recent-item">
                          <div>
                        <p class="mb-0 fw-semibold">${txn.material}</p>
                        <small class="text-muted">${txn.transactionType} • ${txn.dateDisplay}</small>
                          </div>
                      <div class="text-end">
                        <span class="badge bg-light text-dark mb-1">${txn.category}</span>
                        <div class="small text-muted">
                          <fmt:formatNumber value="${txn.quantity}" maxFractionDigits="0" /> units
                        </div>
                        <small class="d-block">${txn.operator}</small>
                      </div>
                    </div>
                  </c:forEach>
                          </div>
                        </div>
                      </div>
                    </div>

          <div class="flow-board mb-4">
            <div class="row g-4">
              <div class="col-lg-4">
                <div class="flow-card">
                  <div class="d-flex justify-content-between mb-2">
                    <h6 class="mb-0">Purchasing Flow</h6>
                          </div>
                  <div class="flow-steps">
                    <c:forEach var="step" items="${purchasingFlowSteps}">
                      <div class="flow-step ${step.theme}">
                        <div class="key">${step.key}</div>
                        <h4>${step.count}</h4>
                        <small>${step.label}</small>
                        </div>
                    </c:forEach>
                      </div>
                    </div>
                          </div>
              <div class="col-lg-4">
                <div class="flow-card">
                  <div class="d-flex justify-content-between mb-2">
                    <h6 class="mb-0">Outbound Flow</h6>
                        </div>
                  <div class="flow-steps">
                    <c:forEach var="step" items="${outboundFlowSteps}">
                      <div class="flow-step ${step.theme}">
                        <div class="key">${step.key}</div>
                        <h4>${step.count}</h4>
                        <small>${step.label}</small>
                      </div>
                    </c:forEach>
                    </div>
              </div>
            </div>
              <div class="col-lg-4">
                <div class="flow-card">
                  <div class="d-flex justify-content-between mb-2">
                    <h6 class="mb-0">Maintenance Flow</h6>
          </div>
                  <div class="flow-steps">
                    <c:forEach var="step" items="${maintenanceFlowSteps}">
                      <div class="flow-step ${step.theme}">
                        <div class="key">${step.key}</div>
                        <h4>${step.count}</h4>
                        <small>${step.label}</small>
            </div>
                    </c:forEach>
                  </div>
                  </div>
                  </div>
                  </div>
                  </div>

          <c:if test="${isDirector || isAdmin}">
            <div class="row g-4 mb-4 align-items-stretch">
              <div class="col-xl-4">
                <div class="director-card h-100">
                  <p class="text-muted text-uppercase mb-1" style="letter-spacing: 0.1em;">Director Insight</p>
                  <h5 class="mb-2">Tổng giá trị tài sản</h5>
                  <h2 class="mb-3">
                    <fmt:formatNumber value="${totalInventoryValue}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
                  </h2>
                  <small class="text-muted">Tính trên tồn kho hiện tại</small>
                  </div>
                  </div>
              <div class="col-xl-8">
                <div class="chart-card h-100">
                  <div class="card-header">
                    <h6 class="mb-0">Lợi nhuận theo tháng</h6>
                  </div>
                  <canvas id="profitTrendChart" height="220"></canvas>
                  </div>
                  </div>
                  </div>
            <div class="row g-4 mb-4">
              <div class="col-12">
                <div class="chart-card">
                  <div class="card-header">
                    <h6 class="mb-0">Giá trị tồn kho theo Category</h6>
                  </div>
                  <canvas id="directorCategoryChart" height="240"></canvas>
                  </div>
                  </div>
                  </div>
                </c:if>
              </div>
            </div>
          </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      const monthlyLabels = [
        <c:forEach var="label" items="${monthlySeries.labels}" varStatus="loop">
          "${label}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const importSeries = [
        <c:forEach var="val" items="${monthlySeries.importSeries}" varStatus="loop">
          ${val}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const exportSeries = [
        <c:forEach var="val" items="${monthlySeries.exportSeries}" varStatus="loop">
          ${val}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];

      const categoryLabels = [
        <c:forEach var="item" items="${categoryDistribution}" varStatus="loop">
          "${item.label}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const categoryValues = [
        <c:forEach var="item" items="${categoryDistribution}" varStatus="loop">
          ${item.stock}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];

      const inventoryValueLabels = [
        <c:forEach var="point" items="${inventoryValueTrend}" varStatus="loop">
          "${point.label}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const inventoryValueSeries = [
        <c:forEach var="point" items="${inventoryValueTrend}" varStatus="loop">
          ${point.value}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];

      const profitLabels = [
        <c:forEach var="point" items="${profitTrend}" varStatus="loop">
          "${point.label}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const profitValues = [
        <c:forEach var="point" items="${profitTrend}" varStatus="loop">
          ${point.value}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];

      const directorCategoryLabels = [
        <c:forEach var="item" items="${directorCategoryValues}" varStatus="loop">
          "${item.label}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];
      const directorCategoryValues = [
        <c:forEach var="item" items="${directorCategoryValues}" varStatus="loop">
          ${item.value}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
      ];

      document.addEventListener("DOMContentLoaded", () => {
        const importExportChart = document.getElementById("importExportChart");
        if (importExportChart) {
          new Chart(importExportChart, {
            type: "bar",
              data: {
              labels: monthlyLabels,
                datasets: [
                  {
                  label: "Import",
                  data: importSeries,
                  backgroundColor: "rgba(37, 99, 235, 0.8)",
                  borderRadius: 8
                },
                {
                  label: "Export",
                  data: exportSeries,
                  backgroundColor: "rgba(249, 115, 22, 0.85)",
                  borderRadius: 8
                }
              ]
              },
              options: {
                responsive: true,
              plugins: { legend: { position: "top" } },
              scales: { y: { beginAtZero: true } }
            }
          });
        }

        const categoryPieChart = document.getElementById("categoryPieChart");
        if (categoryPieChart) {
          new Chart(categoryPieChart, {
            type: "doughnut",
            data: {
              labels: categoryLabels,
              datasets: [{
                data: categoryValues,
                backgroundColor: ["#6366f1", "#f97316", "#14b8a6", "#8b5cf6", "#f43f5e", "#0ea5e9", "#facc15", "#22c55e"],
                borderWidth: 0
              }]
            },
            options: {
              plugins: { legend: { position: "bottom" } }
            }
          });
        }

        const inventoryValueChart = document.getElementById("inventoryValueChart");
        if (inventoryValueChart) {
          new Chart(inventoryValueChart, {
            type: "line",
            data: {
              labels: inventoryValueLabels,
              datasets: [{
                label: "Inventory Value (₫)",
                data: inventoryValueSeries,
                borderColor: "#2563eb",
                backgroundColor: "rgba(37, 99, 235, 0.1)",
                tension: 0.4,
                fill: true
              }]
            },
            options: {
              plugins: { legend: { display: false } },
              scales: { y: { beginAtZero: true } }
            }
          });
        }

        const profitTrendChart = document.getElementById("profitTrendChart");
        if (profitTrendChart) {
          new Chart(profitTrendChart, {
            type: "line",
            data: {
              labels: profitLabels,
              datasets: [{
                label: "VNĐ",
                data: profitValues,
                borderColor: "#0ea5e9",
                backgroundColor: "rgba(14, 165, 233, 0.15)",
                fill: true,
                tension: 0.4
              }]
            },
            options: {
              plugins: { legend: { display: false } },
              scales: { y: { beginAtZero: true } }
            }
          });
        }

        const directorCategoryChart = document.getElementById("directorCategoryChart");
        if (directorCategoryChart) {
          new Chart(directorCategoryChart, {
              type: "bar",
              data: {
              labels: directorCategoryLabels,
              datasets: [{
                label: "Inventory Value (₫)",
                data: directorCategoryValues,
                backgroundColor: "rgba(79, 70, 229, 0.85)",
                borderRadius: 6
              }]
              },
              options: {
              plugins: { legend: { display: false } },
              indexAxis: "y",
                scales: {
                x: { beginAtZero: true }
              }
            }
          });
        }
      });
    </script>
  </body>
</html>

