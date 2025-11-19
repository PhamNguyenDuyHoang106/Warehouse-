<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="en">
  <head>
    <title>Internal Material Management System</title>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <style>
      body {
        background-color: #f8f9fa;
        font-family: "Roboto", sans-serif;
      }

      .section-card {
        background: white;
        border: 2px solid #dead6f;
        border-radius: 15px;
        margin-bottom: 30px;
        box-shadow: 0 4px 12px rgba(222, 173, 111, 0.15);
      }

      .section-header {
        background: linear-gradient(135deg, #dead6f 0%, #cfa856 100%);
        color: white;
        border-radius: 13px 13px 0 0;
        padding: 20px;
      }

      .section-body {
        padding: 30px;
        background: white;
        border-radius: 0 0 13px 13px;
      }

      .alert-custom {
        border: none;
        border-radius: 12px;
        padding: 20px;
        margin-bottom: 20px;
      }

      .alert-warning-custom {
        background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
        border-left: 4px solid #ffc107;
      }

      .alert-danger-custom {
        background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
        border-left: 4px solid #dc3545;
      }

      .alert-info-custom {
        background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
        border-left: 4px solid #17a2b8;
      }

      .alert-primary-custom {
        background: linear-gradient(135deg, #cce7ff 0%, #b3d9ff 100%);
        border-left: 4px solid #007bff;
      }

      .quick-action-btn {
        background: white;
        border: 2px solid #dead6f;
        border-radius: 12px;
        padding: 20px;
        text-align: center;
        transition: all 0.3s ease;
        text-decoration: none;
        color: #333;
        min-height: 120px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
      }

      .quick-action-btn:hover {
        background: #dead6f;
        color: white;
        transform: translateY(-3px);
        text-decoration: none;
      }

      .quick-action-btn i {
        font-size: 2.5rem;
        margin-bottom: 10px;
      }

      .activity-card {
        background: #f8f9fa;
        border-radius: 10px;
        padding: 20px;
        text-align: center;
        border: 1px solid #dee2e6;
      }

      .activity-card i {
        font-size: 2rem;
        margin-bottom: 10px;
      }

      .activity-card h4 {
        font-weight: 700;
        margin-bottom: 5px;
      }

      .activity-card small {
        color: #6c757d;
      }
    </style>
  </head>
  <body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

    <!-- Main Content -->
    <div class="container-fluid my-4">
      <div class="row">
        <!-- Main Content Area -->
        <div class="col-12">
          <!-- ACTIVITY SUMMARY SECTION -->
          <div class="section-card">
            <div class="section-header">
              <h4 class="mb-0">
                <i class="fas fa-clock me-2"></i>Activity Summary
              </h4>
            </div>
            <div class="section-body">
              <div class="row text-center">
                <div class="col-md-3 mb-3">
                  <div class="activity-card">
                    <i class="fas fa-arrow-down" style="color: #28a745"></i>
                    <h4 class="mb-1">${totalImported}</h4>
                    <small class="text-muted">Total Imported</small>
                  </div>
                </div>
                <div class="col-md-3 mb-3">
                  <div class="activity-card">
                    <i class="fas fa-arrow-up" style="color: #fd7e14"></i>
                    <h4 class="mb-1">${totalExported}</h4>
                    <small class="text-muted">Total Exported</small>
                  </div>
                </div>
                <div class="col-md-3 mb-3">
                  <div class="activity-card">
                    <i class="fas fa-boxes" style="color: #007bff"></i>
                    <h4 class="mb-1">${materialCount}</h4>
                    <small class="text-muted">Total Materials</small>
                  </div>
                </div>
                <div class="col-md-3 mb-3">
                  <div class="activity-card">
                    <i class="fas fa-warehouse" style="color: #6f42c1"></i>
                    <h4 class="mb-1">${totalStock}</h4>
                    <small class="text-muted">Current Stock</small>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- ANALYTICS & REPORTS SECTION -->
          <div class="section-card">
            <div class="section-header">
              <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0">
                  <i class="fas fa-chart-bar me-2"></i>Analytics & Reports
                </h4>
                <button id="toggleReportsBtn" class="btn btn-outline-light btn-sm" style="border-radius: 20px">
                  <i class="fas fa-eye-slash me-1"></i> Hide Reports
                </button>
              </div>
            </div>
            <div id="reportsSection" class="section-body">
              <!-- Charts Row -->
              <div class="row">
                <div class="col-md-6 mb-4">
                  <div class="card" style="border: 1px solid #dead6f; border-radius: 12px">
                    <div class="card-header" style="background: #f8f9fa; border-bottom: 1px solid #dead6f;">
                      <h6 class="mb-0" style="color: #dead6f; font-weight: 600">
                        <i class="fas fa-chart-pie me-2"></i>Inventory Overview
                      </h6>
                    </div>
                    <div class="card-body">
                      <canvas id="pieChart" style="max-height: 300px"></canvas>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-4">
                  <div class="card" style="border: 1px solid #dead6f; border-radius: 12px">
                    <div class="card-header" style="background: #f8f9fa; border-bottom: 1px solid #dead6f;">
                      <h6 class="mb-0" style="color: #dead6f; font-weight: 600">
                        <i class="fas fa-chart-line me-2"></i>Stock Level Distribution
                      </h6>
                    </div>
                    <div class="card-body">
                      <canvas id="barChart" style="max-height: 300px"></canvas>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- CRITICAL ALERTS SECTION -->
          <div class="section-card">
            <div class="section-header">
              <h4 class="mb-0">
                <i class="fas fa-exclamation-triangle me-2"></i>Critical Alerts
              </h4>
            </div>
            <div class="section-body">
              <div class="row">
                <!-- Low Stock Alert -->
                <c:if test="${lowStockCount > 0}">
                  <div class="col-md-6 mb-3">
                    <div class="alert-custom alert-warning-custom">
                      <div class="d-flex align-items-center">
                        <i class="fas fa-exclamation-triangle fa-2x me-3" style="color: #856404"></i>
                        <div>
                          <h5 class="alert-heading mb-1">Low Stock</h5>
                          <p class="mb-2">${lowStockCount} materials are low on stock</p>
                          <a href="StaticInventory?stockFilter=low" class="btn btn-warning btn-sm">
                            <i class="fas fa-eye me-1"></i>View Details
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </c:if>

                <!-- Out of Stock Alert -->
                <c:if test="${outOfStockCount > 0}">
                  <div class="col-md-6 mb-3">
                    <div class="alert-custom alert-danger-custom">
                      <div class="d-flex align-items-center">
                        <i class="fas fa-times-circle fa-2x me-3" style="color: #721c24"></i>
                        <div>
                          <h5 class="alert-heading mb-1">Out of Stock</h5>
                          <p class="mb-2">${outOfStockCount} materials are out of stock</p>
                          <a href="StaticInventory?stockFilter=zero" class="btn btn-danger btn-sm">
                            <i class="fas fa-eye me-1"></i>View Details
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </c:if>

                <!-- Inactive Materials Alert (Admin/Director/Warehouse Staff) -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 2 || sessionScope.user.roleId == 3}">
                  <c:if test="${damagedMaterialsCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom" style="background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%); border-left: 4px solid #ffc107;">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-exclamation-triangle fa-2x me-3" style="color: #856404"></i>
                          <div>
                            <h5 class="alert-heading mb-1">Inactive Materials</h5>
                            <p class="mb-2">${damagedMaterialsCount} materials are inactive</p>
                            <a href="StaticInventory?status=inactive" class="btn btn-warning btn-sm">
                              <i class="fas fa-eye me-1"></i>View Details
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>
                </c:if>

                <!-- Pending Requests Alerts (Admin/Director/Warehouse Staff) -->
                <c:if test="${sessionScope.user.roleId == 1 || sessionScope.user.roleId == 2 || sessionScope.user.roleId == 3}">
                  <c:if test="${pendingExportRequestCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom alert-info-custom">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-file-signature fa-2x me-3" style="color: #0c5460"></i>
                          <div>
                            <h5 class="alert-heading mb-1">Pending Export Requests</h5>
                            <p class="mb-2">${pendingExportRequestCount} requests waiting for approval</p>
                            <a href="ExportRequestList" class="btn btn-info btn-sm">
                              <i class="fas fa-eye me-1"></i>Review Requests
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>

                  <c:if test="${pendingPurchaseRequestCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom alert-primary-custom">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-shopping-cart fa-2x me-3" style="color: #004085"></i>
                          <div>
                            <h5 class="alert-heading mb-1">Pending Purchase Requests</h5>
                            <p class="mb-2">${pendingPurchaseRequestCount} requests waiting for approval</p>
                            <a href="ListPurchaseRequests" class="btn btn-primary btn-sm">
                              <i class="fas fa-eye me-1"></i>Review Requests
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>

                  <c:if test="${pendingRepairRequestCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom" style="background: linear-gradient(135deg, #e2e3e5 0%, #d6d8db 100%); border-left: 4px solid #6c757d;">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-tools fa-2x me-3" style="color: #495057"></i>
                          <div>
                            <h5 class="alert-heading mb-1">Pending Repair Requests</h5>
                            <p class="mb-2">${pendingRepairRequestCount} requests waiting for approval</p>
                            <a href="repairrequestlist" class="btn btn-secondary btn-sm">
                              <i class="fas fa-eye me-1"></i>Review Requests
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>
                </c:if>

                <!-- Employee Specific Alerts -->
                <c:if test="${sessionScope.user.roleId == 4}">
                  <!-- My Pending Requests Alert -->
                  <c:if test="${myPendingRequestCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom alert-info-custom">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-clock fa-2x me-3" style="color: #17a2b8"></i>
                          <div>
                            <h5 class="alert-heading mb-1">My Pending Requests</h5>
                            <p class="mb-2">You have ${myPendingRequestCount} requests waiting for approval</p>
                            <a href="ViewRequests" class="btn btn-info btn-sm">
                              <i class="fas fa-eye me-1"></i>View My Requests
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>

                  <!-- My Approved Requests Alert -->
                  <c:if test="${myApprovedRequestCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom alert-success-custom">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-check-circle fa-2x me-3" style="color: #28a745"></i>
                          <div>
                            <h5 class="alert-heading mb-1">My Approved Requests</h5>
                            <p class="mb-2">${myApprovedRequestCount} of your requests have been approved</p>
                            <a href="ViewRequests" class="btn btn-success btn-sm">
                              <i class="fas fa-eye me-1"></i>View Details
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>

                  <!-- Available Materials Alert -->
                  <c:if test="${availableMaterialsCount > 0}">
                    <div class="col-md-6 mb-3">
                      <div class="alert-custom alert-primary-custom">
                        <div class="d-flex align-items-center">
                          <i class="fas fa-boxes fa-2x me-3" style="color: #007bff"></i>
                          <div>
                            <h5 class="alert-heading mb-1">Available Materials</h5>
                            <p class="mb-2">${availableMaterialsCount} materials are available for request</p>
                            <a href="StaticInventory" class="btn btn-primary btn-sm">
                              <i class="fas fa-search me-1"></i>Browse Materials
                            </a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </c:if>
                </c:if>
              </div>
            </div>
          </div>

          <!-- QUICK ACTIONS SECTION -->
          <div class="section-card">
            <div class="section-header">
              <h4 class="mb-0">
                <i class="fas fa-bolt me-2"></i>Quick Actions
              </h4>
            </div>
            <div class="section-body">
              <div class="row">
                <!-- Admin - All Operations -->
                <c:if test="${sessionScope.user.roleId == 1}">
                  <div class="col-md-2 mb-3">
                    <a href="ImportMaterial" class="quick-action-btn">
                      <i class="fas fa-plus-circle" style="color: #28a745"></i>
                      <h6 class="mb-1">Import Materials</h6>
                      <small>Add new stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="ExportMaterial" class="quick-action-btn">
                      <i class="fas fa-minus-circle" style="color: #ffc107"></i>
                      <h6 class="mb-1">Export Materials</h6>
                      <small>Remove stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="addmaterial" class="quick-action-btn">
                      <i class="fas fa-box" style="color: #007bff"></i>
                      <h6 class="mb-1">Add New Material</h6>
                      <small>Create new item</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="StaticInventory" class="quick-action-btn">
                      <i class="fas fa-clipboard-list" style="color: #17a2b8"></i>
                      <h6 class="mb-1">Inventory Report</h6>
                      <small>View all stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="UserList" class="quick-action-btn">
                      <i class="fas fa-users" style="color: #343a40"></i>
                      <h6 class="mb-1">User Management</h6>
                      <small>Manage users</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="ExportRequestList" class="quick-action-btn">
                      <i class="fas fa-eye" style="color: #17a2b8"></i>
                      <h6 class="mb-1">View Requests</h6>
                      <small>View all requests</small>
                    </a>
                  </div>
                </c:if>

                <!-- Warehouse Staff Operations -->
                <c:if test="${sessionScope.user.roleId == 3}">
                  <div class="col-md-2 mb-3">
                    <a href="ImportMaterial" class="quick-action-btn">
                      <i class="fas fa-plus-circle" style="color: #28a745"></i>
                      <h6 class="mb-1">Import Materials</h6>
                      <small>Add new stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="ExportMaterial" class="quick-action-btn">
                      <i class="fas fa-minus-circle" style="color: #ffc107"></i>
                      <h6 class="mb-1">Export Materials</h6>
                      <small>Remove stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="AddMaterial" class="quick-action-btn">
                      <i class="fas fa-box" style="color: #007bff"></i>
                      <h6 class="mb-1">Add New Material</h6>
                      <small>Create new item</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="StaticInventory" class="quick-action-btn">
                      <i class="fas fa-clipboard-list" style="color: #17a2b8"></i>
                      <h6 class="mb-1">Inventory Report</h6>
                      <small>View all stock</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="CreateExportRequest" class="quick-action-btn">
                      <i class="fas fa-plus" style="color: #20c997"></i>
                      <h6 class="mb-1">Create Request</h6>
                      <small>Create export request</small>
                    </a>
                  </div>
                  <div class="col-md-2 mb-3">
                    <a href="ExportRequestList" class="quick-action-btn">
                      <i class="fas fa-eye" style="color: #17a2b8"></i>
                      <h6 class="mb-1">View Requests</h6>
                      <small>View all requests</small>
                    </a>
                  </div>
                </c:if>

                <!-- Employee Actions -->
                <c:if test="${sessionScope.user.roleId == 4}">
                  <div class="col-md-4 mb-3">
                    <a href="CreateExportRequest" class="quick-action-btn">
                      <i class="fas fa-plus" style="color: #20c997"></i>
                      <h6 class="mb-1">Create Request</h6>
                      <small>Create export request</small>
                    </a>
                  </div>
                  <div class="col-md-4 mb-3">
                    <a href="ViewRequests" class="quick-action-btn">
                      <i class="fas fa-eye" style="color: #17a2b8"></i>
                      <h6 class="mb-1">View Requests</h6>
                      <small>View my requests</small>
                    </a>
                  </div>
                  <div class="col-md-4 mb-3">
                    <a href="StaticInventory" class="quick-action-btn">
                      <i class="fas fa-search" style="color: #6f42c1"></i>
                      <h6 class="mb-1">Browse Materials</h6>
                      <small>Search available items</small>
                    </a>
                  </div>
                </c:if>

                <!-- Director Actions -->
                <c:if test="${sessionScope.user.roleId == 2}">
                  <div class="col-md-4 mb-3">
                    <a href="ExportRequestList" class="quick-action-btn">
                      <i class="fas fa-eye" style="color: #17a2b8"></i>
                      <h6 class="mb-1">View Requests</h6>
                      <small>Monitor & approve requests</small>
                    </a>
                  </div>
                  <div class="col-md-4 mb-3">
                    <a href="StaticInventory" class="quick-action-btn">
                      <i class="fas fa-chart-bar" style="color: #6f42c1"></i>
                      <h6 class="mb-1">Inventory Overview</h6>
                      <small>View stock reports</small>
                    </a>
                  </div>
                </c:if>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <jsp:include page="Footer.jsp" />

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Reports Toggle Script -->
    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const toggleBtn = document.getElementById("toggleReportsBtn");
        const reportsSection = document.getElementById("reportsSection");
        let chartsRendered = false;

        // Render charts immediately when page loads
        if (reportsSection) {
          renderCharts();
          chartsRendered = true;
        }

        if (toggleBtn && reportsSection) {
          toggleBtn.addEventListener("click", function () {
            const isVisible = reportsSection.style.display !== "none";

            if (isVisible) {
              reportsSection.style.display = "none";
              toggleBtn.innerHTML = '<i class="fas fa-eye me-1"></i> Show Reports';
            } else {
              reportsSection.style.display = "block";
              toggleBtn.innerHTML = '<i class="fas fa-eye-slash me-1"></i> Hide Reports';
            }
          });
        }

        function renderCharts() {
          // Pie Chart - Inventory Overview
          const pieCtx = document.getElementById("pieChart");
          if (pieCtx) {
            const totalImported = parseInt("${totalImported}", 10) || 0;
            const totalExported = parseInt("${totalExported}", 10) || 0;
            const totalStock = parseInt("${totalStock}", 10) || 0;

            new Chart(pieCtx, {
              type: "pie",
              data: {
                labels: ["Current Stock", "Total Imported", "Total Exported"],
                datasets: [
                  {
                    data: [totalStock, totalImported, totalExported],
                    backgroundColor: [
                      "rgba(40, 167, 69, 0.8)",
                      "rgba(0, 123, 255, 0.8)",
                      "rgba(253, 126, 20, 0.8)",
                    ],
                    borderColor: [
                      "rgba(40, 167, 69, 1)",
                      "rgba(0, 123, 255, 1)",
                      "rgba(253, 126, 20, 1)",
                    ],
                    borderWidth: 2,
                  },
                ],
              },
              options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                  legend: {
                    position: "bottom",
                    labels: {
                      padding: 20,
                      usePointStyle: true,
                      font: { size: 12 },
                    },
                  },
                  tooltip: {
                    callbacks: {
                      label: function (context) {
                        const value = context.parsed;
                        const total = context.dataset.data.reduce(
                          (a, b) => a + b,
                          0
                        );
                        const percent =
                          total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                        return (
                          context.label + ": " + value + " (" + percent + "%)"
                        );
                      },
                    },
                  },
                },
              },
            });
          }

          // Bar Chart - Stock Level Distribution
          const barCtx = document.getElementById("barChart");
          if (barCtx) {
            const lowStockCount = parseInt("${lowStockCount}", 10) || 0;
            const outOfStockCount = parseInt("${outOfStockCount}", 10) || 0;
            const totalMaterials = parseInt("${materialCount}", 10) || 0;
            const normalStockCount =
              totalMaterials - lowStockCount - outOfStockCount;

            new Chart(barCtx, {
              type: "bar",
              data: {
                labels: ["Normal Stock", "Low Stock", "Out of Stock"],
                datasets: [
                  {
                    label: "Number of Materials",
                    data: [normalStockCount, lowStockCount, outOfStockCount],
                    backgroundColor: [
                      "rgba(40, 167, 69, 0.8)",
                      "rgba(255, 193, 7, 0.8)",
                      "rgba(220, 53, 69, 0.8)",
                    ],
                    borderColor: [
                      "rgba(40, 167, 69, 1)",
                      "rgba(255, 193, 7, 1)",
                      "rgba(220, 53, 69, 1)",
                    ],
                    borderWidth: 2,
                    borderRadius: 8,
                  },
                ],
              },
              options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 },
                  },
                },
                plugins: {
                  legend: { display: false },
                  tooltip: {
                    callbacks: {
                      label: function (context) {
                        return context.parsed.y + " materials";
                      },
                    },
                  },
                },
              },
            });
          }
        }
      });
    </script>
  </body>
</html>