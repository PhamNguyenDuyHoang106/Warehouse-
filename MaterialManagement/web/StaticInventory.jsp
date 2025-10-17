<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Inventory Statistics - Material Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
      body {
        background-color: #f8f9fa;
        font-family: 'Roboto', sans-serif;
        padding: 20px;
      }
      .content {
        padding-left: 20px;
        font-family: 'Roboto', sans-serif;
      }
      .sidebar {
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
      .sidebar .nav-link {
        color: rgba(255, 255, 255, 0.8);
        padding: 12px 20px;
        margin: 4px 0;
        border-radius: 8px;
        transition: all 0.3s ease;
      }
      .sidebar .nav-link:hover,
      .sidebar .nav-link.active {
        color: white;
        background: rgba(255, 255, 255, 0.1);
        transform: translateX(5px);
      }
      .sidebar .nav-link i {
        width: 20px;
        margin-right: 10px;
      }
      .main-content {
        background: #f8f9fa;
        min-height: 100vh;
      }
      .card {
        border: none;
        border-radius: 15px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;
      }
      .card:hover {
        transform: translateY(-5px);
      }
      .stat-card {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 15px;
        padding: 25px;
        margin-bottom: 20px;
      }
      .stat-card h3 {
        font-size: 2.5rem;
        font-weight: bold;
        margin-bottom: 10px;
      }
      .stat-card p {
        font-size: 1.1rem;
        opacity: 0.9;
      }
      .stat-icon {
        font-size: 3rem;
        opacity: 0.8;
      }
      .table th {
        background: #f8f9fa;
        border-bottom: 2px solid #dee2e6;
        font-weight: 600;
        color: #495057;
      }
      .table td {
        vertical-align: middle;
        border-color: #e9ecef;
      }
      .badge {
        font-size: 1em;
        padding: 8px 16px;
        border-radius: 20px;
        font-weight: bold;
        border: 2px solid #888;
        box-shadow: 0 2px 8px rgba(0,0,0,0.10);
        letter-spacing: 1px;
      }
      .stock-high {
        background-color: #4ade80;
        color: #000;
        border-color: #22c55e;
      }
      .stock-medium {
        background-color: #fde047;
        color: #000;
        border-color: #facc15;
      }
      .stock-low {
        background-color: #fd7e14;
        color: #fff;
        border-color: #b35c00;
      }
      .stock-zero {
        background-color: #dc2626;
        color: #fff;
        border-color: #b91c1c;
      }
      .search-section {
        margin-bottom: 25px;
      }

      .material-name {
        font-weight: 500;
        color: #495057;
      }
      .material-code {
        font-family: 'Courier New', monospace;
        font-weight: 600;
        color: #6c757d;
        background: #f8f9fa;
        padding: 4px 8px;
        border-radius: 4px;
      }
      .pagination {
        justify-content: center;
        margin-top: 20px;
      }
      .pagination .page-link {
        border-radius: 8px;
        margin: 0 2px;
        border: 1px solid #dee2e6;
        color: #DEAD6F;
        background: #fff;
        transition: background 0.2s, color 0.2s;
      }
      .pagination .page-item.active .page-link {
        background: #DEAD6F;
        color: #fff;
        border-color: #DEAD6F;
      }
      .pagination .page-link:hover {
        background: #DEAD6F;
        color: #fff;
        border-color: #DEAD6F;
      }
      .pagination .page-item.disabled .page-link {
        color: #6c757d;
      }

    </style>
  </head>
  <body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
      <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 bg-light p-0">
          <jsp:include page="Sidebar.jsp" />
        </div>
        <div class="col-md-9 col-lg-10 content px-md-4">
          <c:set var="hasViewInventoryPermission" value="${rolePermissionDAO.hasPermission(roleId, 'VIEW_INVENTORY')}" scope="request" />
          <c:if test="${!hasViewInventoryPermission}">
            <div class="alert alert-danger text-center my-5">
                <i class="fas fa-exclamation-triangle fa-2x mb-3"></i>
                <h2>Access Denied</h2>
                <p>You do not have permission to view inventory.</p>
                <a href="home" class="btn btn-primary mt-3">Go to Homepage</a>
            </div>
            <c:remove var="inventoryList" scope="request"/>
          </c:if>
          <c:if test="${hasViewInventoryPermission}">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h2 class="text-primary fw-bold display-6 border-bottom pb-2">
                <i class="fas fa-chart-bar me-2"></i>Inventory Statistics
              </h2>

            </div>
            <c:if test="${not empty error}">
              <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="row search-section">
              <div class="col-md-12">
                <form method="GET" action="StaticInventory" class="d-flex gap-2 align-items-center">
                  <input type="text" name="search" value="${searchTerm}" class="form-control" placeholder="Search by material name, code, or category..." style="width: 220px; height: 50px; border: 2px solid gray" />
                  <select name="filter" class="form-select" style="width: 180px; height: 50px; border: 2px solid gray">
                    <option value="">All Stock Levels</option>
                    <option value="high" ${stockFilter == 'high' ? 'selected' : ''}>High Stock (>50)</option>
                    <option value="medium" ${stockFilter == 'medium' ? 'selected' : ''}>Medium Stock (10-50)</option>
                    <option value="low" ${stockFilter == 'low' ? 'selected' : ''}>Low Stock (1-9)</option>
                    <option value="zero" ${stockFilter == 'zero' ? 'selected' : ''}>Out of Stock (0)</option>
                  </select>
                  <select name="sortStock" class="form-select" style="width: 180px; height: 50px; border: 2px solid gray">
                    <option value="">Sort by Stock</option>
                    <option value="asc" ${sortStock == 'asc' ? 'selected' : ''}>Stock Ascending</option>
                    <option value="desc" ${sortStock == 'desc' ? 'selected' : ''}>Stock Descending</option>
                  </select>
                  <button type="submit" class="btn btn-primary d-flex align-items-center justify-content-center" style="width: 150px; height: 50px;">
                    <i class="fas fa-search me-2"></i> Search
                  </button>
                  <a href="StaticInventory" class="btn btn-secondary d-flex align-items-center justify-content-center" style="width: 120px; height: 50px">Clear</a>
                </form>
              </div>
            </div>
            <div class="table-responsive">
              <table class="table table-bordered table-hover align-middle text-center">
                <thead class="table-light">
                  <tr>
                    <th style="width: 40px;">#</th>
                    <th style="width: 90px;">Code</th>
                    <th style="width: 90px;">Image</th>
                    <th style="width: 180px;">Name</th>
                    <th style="width: 80px;">Unit</th>
                    <th style="width: 120px;">Category</th>
                    <th style="width: 90px;">Stock</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="inv" items="${inventoryList}" varStatus="loop">
                    <tr>
                      <td><strong>${loop.index + 1}</strong></td>
                      <td><span class="material-code">${inv.materialCode}</span></td>
                      <td>
                        <c:choose>
                          <c:when test="${not empty inv.materialsUrl}">
                            <img src="images/material/${inv.materialsUrl}" alt="Material Image" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ccc;" />
                          </c:when>
                          <c:otherwise>
                            <img src="images/material/default.jpg" alt="Material Image" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ccc;" />
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td><div class="material-name">${inv.materialName}</div></td>
                      <td><div class="material-name">${inv.unitName}</div></td>
                      <td><div class="material-name">${inv.categoryName}</div></td>
                      <td>
                        <c:choose>
                          <c:when test="${inv.stock > 50}"><span class="badge stock-high">${inv.stock}</span></c:when>
                          <c:when test="${inv.stock >= 10}"><span class="badge stock-medium">${inv.stock}</span></c:when>
                          <c:when test="${inv.stock > 0}"><span class="badge stock-low">${inv.stock}</span></c:when>
                          <c:otherwise><span class="badge stock-zero">${inv.stock}</span></c:otherwise>
                        </c:choose>
                      </td>
                    </tr>
                  </c:forEach>
                  <c:if test="${empty inventoryList}">
                    <tr>
                      <td colspan="7" class="text-center text-muted">No inventory data available</td>
                    </tr>
                  </c:if>
                </tbody>
              </table>
            </div>
              <nav>
                <ul class="pagination justify-content-center mt-4">
                  <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="StaticInventory?page=${currentPage - 1}&search=${searchTerm}&filter=${stockFilter}&sortStock=${sortStock}">Previous</a>
                  </li>
                  <c:forEach begin="1" end="${totalPages}" var="i">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                      <a class="page-link" href="StaticInventory?page=${i}&search=${searchTerm}&filter=${stockFilter}&sortStock=${sortStock}">${i}</a>
                    </li>
                  </c:forEach>
                  <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="StaticInventory?page=${currentPage + 1}&search=${searchTerm}&filter=${stockFilter}&sortStock=${sortStock}">Next</a>
                  </li>
                </ul>
              </nav>
          </c:if>
        </div>
      </div>
    </div>
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
    <script>

    </script>
  </body>
</html>