<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Material List by Category</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background-color: #f4f4f4;
                padding: 30px;
            }
            h2 {
                text-align: center;
                margin-bottom: 35px;
                color: #d59f39;
                font-weight: bold;
            }
            .table-responsive {
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.08);
                background-color: #fff;
            }
            .table thead {
                background-color: #d59f39 !important;
                color: #fff !important;
            }
            .table thead th {
                background-color: #d59f39 !important;
                color: #fff !important;
                border-bottom: 2px solid #c0872e;
                font-weight: bold;
                font-size: 14px;
                text-align: center;
            }
            .table thead th:first-child {
                border-top-left-radius: 12px;
            }
            .table thead th:last-child {
                border-top-right-radius: 12px;
            }
            .table th, .table td {
                vertical-align: middle;
                padding: 8px;
            }
            .table img {
                width: 100px;
                height: 80px;
                object-fit: cover;
                border-radius: 4px;
                box-shadow: 0 0 4px rgba(0,0,0,0.1);
            }
            .alert {
                max-width: 600px;
                margin: 40px auto;
                background-color: #fff3cd;
                border-color: #ffeeba;
            }
            .back-button {
                text-align: right;
                margin-bottom: 25px;
            }
            .btn-primary {
                background-color: #d59f39;
                border-color: #d59f39;
                border-radius: 30px;
                font-weight: 500;
                padding: 6px 12px;
            }
            .btn-primary:hover {
                background-color: #bb8e2d;
                border-color: #bb8e2d;
            }
            .status-new {
                color: #28a745;
                font-weight: bold;
            }
            .status-used {
                color: #ffc107;
                font-weight: bold;
            }
            .status-damaged {
                color: #dc3545;
                font-weight: bold;
            }
            .table td.text-end {
                padding-right: 16px;
            }
            .table tbody tr:hover {
                background-color: #f8f9fa;
            }
        </style>

    <body>

        <div class="back-button">
            <a href="home" class="btn btn-primary px-4 py-2">🔙 Back to Home</a>
        </div>

        <h2>List of Materials by Category</h2>

        <c:choose>
            <c:when test="${empty materials}">
                <div class="alert alert-warning text-center" role="alert">
                    Not found material.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle mb-0">
                        <thead class="text-center align-middle">
                            <tr>
                                <th style="width: 50px;">ID</th>
                                <th style="width: 120px;">Image</th>
                                <th style="width: 180px;">Material Name</th>
                                <th style="width: 100px;">Code</th>
                                <th style="width: 90px;">Status</th>
                                <th style="width: 80px;">Status</th>
                                <th style="width: 70px;">Unit</th>
                                <th style="width: 120px;">Category</th>
                                <th style="width: 120px;">Created At</th>
                                <th style="width: 120px;">Updated At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="material" items="${materials}" varStatus="loop">
                                <tr>
                                    <td class="text-center">${loop.count}</td>
                                    <td class="text-center">
                                        <img src="images/material/${not empty material.materialsUrl ? material.materialsUrl : 'https://via.placeholder.com/100x80?text=No+Image'}"
                                             alt="Image ${material.materialName}">
                                    </td>
                                    <td>${material.materialName}</td>
                                    <td class="text-center">${material.materialCode}</td>
                                    <td class="text-center">
                                        <span class="badge ${material.materialStatus == 'new' ? 'bg-success' : (material.materialStatus == 'used' ? 'bg-warning' : 'bg-danger')}">
                                            ${material.materialStatus == 'new' ? 'Mới' : (material.materialStatus == 'used' ? 'Đã sử dụng' : 'Hỏng')}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <span class="badge ${material.materialStatus == 'new' ? 'bg-success' : (material.materialStatus == 'used' ? 'bg-warning' : 'bg-danger')}">
                                            ${material.materialStatus == 'new' ? 'New' : (material.materialStatus == 'used' ? 'Used' : 'Damaged')}
                                        </span>
                                    </td>
                                    <td class="text-center">${material.unit != null ? material.unit.unitName : 'N/A'}</td>
                                    <td class="text-center">${material.category != null ? material.category.category_name : 'N/A'}</td>
                                    <td class="text-center">${material.createdAt}</td>
                                    <td class="text-center">${material.updatedAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>

    </body>
</html>
