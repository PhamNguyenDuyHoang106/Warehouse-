<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
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
              <img
                src="images/material/${m.materialsUrl}"
                alt="${m.materialName}"
                style="width: 200px; height: 200px; object-fit: cover"
                onerror="this.onerror=null;this.src='images/material/default.jpg';"
              />
            </div>
          </div>
          <div class="col-md-8">
            <h2 class="mb-4 material-title">
              ${empty m.materialName ? 'Không có thông tin' : m.materialName}
            </h2>
            <div class="detail-row">
              <div class="detail-label">Material Code</div>
              <div>
                ${empty m.materialCode ? 'Không có thông tin' : m.materialCode}
              </div>
            </div>
            <div class="detail-row">
              <div class="detail-label">Status</div>
              <div>
                <span
                  class="badge ${m.materialStatus == 'new' ? 'bg-success' : (m.materialStatus == 'used' ? 'bg-warning' : 'bg-danger')}"
                >
                  ${m.materialStatus == 'new' ? 'New' : (m.materialStatus ==
                  'used' ? 'Used' : 'Damaged')}
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
                  ${empty m.category.category_name ? 'Không có thông tin' :
                  m.category.category_name}
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-label">Description</div>
                <div>
                  ${empty m.category.description ? 'Không có thông tin' :
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
                  ${empty m.unit.unitName ? 'Không có thông tin' :
                  m.unit.unitName}
                </div>
              </div>
              <div class="detail-row">
                <div class="detail-label">Symbol</div>
                <div>
                  ${empty m.unit.symbol ? 'Không có thông tin' : m.unit.symbol}
                </div>
              </div>
            </div>
          </div>
        </div>
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
