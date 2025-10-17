<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
            <div class="card">
                <div class="card-header">Export Information</div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <p><strong>Date:</strong> ${exportData.exportDate}</p>
                            <p><strong>Exported By:</strong> ${exportData.exportedByName}</p>
                            <p><strong>Recipient:</strong> ${exportData.recipientName}</p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>Note:</strong> ${exportData.note}</p>
                        </div>
                    </div>
                </div>
            </div>
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
                                    <th>Quantity</th>
                                    <th>Unit</th>
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
                                        <td>${detail.quantity}</td>
                                        <td>${detail.unitName}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="d-flex gap-2 mb-2 justify-content-center">
                <a href="ExportHistory" class="btn btn-cancel">← Back to Export History</a>
            </div>
        </div>
        <jsp:include page="Footer.jsp" />
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>