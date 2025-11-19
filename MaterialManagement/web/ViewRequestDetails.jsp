<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Request Details</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <style>
            body {
                font-family: 'Montserrat', sans-serif;
                background: url('images/background-img.png') no-repeat;
                background-size: cover;
            }
            .detail-container {
                max-width: 800px;
                margin: 50px auto;
                background-color: #fff;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 0 15px rgba(0,0,0,0.1);
            }
            .detail-container h2 {
                font-size: 2.5rem;
                font-weight: 500;
                text-align: center;
                margin-bottom: 20px;
            }
            .detail-container h2 span {
                color: #0d6efd;
            }
            .detail-container h3 {
                font-size: 1.5rem;
                font-weight: 500;
                margin-top: 20px;
                margin-bottom: 15px;
            }
            .detail-container p {
                font-size: 1rem;
                margin: 10px 0;
            }
            .detail-container table {
                width: 100%;
                border-collapse: collapse;
                font-size: 1rem;
                margin-bottom: 20px;
            }
            .detail-container th, .detail-container td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            .detail-container th {
                background-color: #f2f2f2;
                font-weight: 500;
            }
            .detail-container img {
                max-width: 100px;
                max-height: 100px;
                vertical-align: middle;
            }
            .btn {
                font-size: 1rem;
                padding: 10px 20px;
                border-radius: 5px;
            }
            .btn-primary {
                background-color: #0d6efd;
                border-color: #0d6efd;
            }
            .btn-primary:hover {
                background-color: #0b5ed7;
                border-color: #0a58ca;
            }
            .btn-danger {
                background-color: #dc3545;
                border-color: #dc3545;
            }
            .btn-danger:hover {
                background-color: #c82333;
                border-color: #bd2130;
            }
            .btn-secondary {
                background-color: #6c757d;
                border-color: #6c757d;
            }
            .btn-secondary:hover {
                background-color: #5c636a;
                border-color: #565e64;
            }
            .alert {
                font-size: 1rem;
                margin-bottom: 20px;
            }
            .pagination {
                justify-content: center;
                margin-top: 20px;
            }
            .pagination .page-item.active .page-link {
                background-color: #DEAD6F;
                border-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-link {
                color: #DEAD6F;
                border-radius: 0.25rem;
                margin: 0 3px;
                padding: 0.5rem 0.75rem;
            }
            .pagination .page-link:hover {
                background-color: #f8f9fa;
                color: #DEAD6F;
            }
            .pagination .page-item.disabled .page-link {
                color: #6c757d;
                background-color: #f8f9fa;
                border-color: #dee2e6;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="request-details">
            <div class="detail-container">
                <h2 class="display-4 fw-normal text-center mb-4">Request <span class="text-primary">Details</span></h2>

                <c:if test="${not empty message}">
                    <div class="alert ${message.startsWith('Error') ? 'alert-danger' : 'alert-success'}">
                        ${message}
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${requestType == 'PurchaseOrder'}">
                        <p><strong>PO Code:</strong> ${request.poCode}</p>
                        <p><strong>Created Date:</strong> <fmt:formatDate value="${request.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
                        <p><strong>Purchase Request Code:</strong> ${request.purchaseRequestCode}</p>
                        <p><strong>Created By:</strong> ${request.createdByName}</p>
                        <p><strong>Status:</strong> ${request.status}</p>
                        <p><strong>Note:</strong> ${request.note != null ? request.note : "N/A"}</p>
                        <p><strong>Approved By:</strong> ${request.approvedByName != null ? request.approvedByName : "N/A"}</p>
                        <p><strong>Approved At:</strong> <c:choose>
                            <c:when test="${request.approvedAt != null}">
                                <fmt:formatDate value="${request.approvedAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                            </c:when>
                            <c:otherwise>N/A</c:otherwise>
                        </c:choose></p>
                        <p><strong>Rejection Reason:</strong> ${request.rejectionReason != null ? request.rejectionReason : "N/A"}</p>
                        <p><strong>Sent to Supplier At:</strong> <c:choose>
                            <c:when test="${request.sentToSupplierAt != null}">
                                <fmt:formatDate value="${request.sentToSupplierAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                            </c:when>
                            <c:otherwise>N/A</c:otherwise>
                        </c:choose></p>
                    </c:when>
                    <c:otherwise>
                        <p><strong>Request Code:</strong> ${request.requestCode}</p>
                        <p><strong>Request Date:</strong> <fmt:formatDate value="${request.requestDate}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
                        <p><strong>Status:</strong> ${request.status}</p>
                        <p><strong>Reason:</strong> ${request.reason != null ? request.reason : "N/A"}</p>
                        <p><strong>Approval Reason:</strong> ${request.approvalReason != null ? request.approvalReason : "N/A"}</p>
                        <p><strong>Rejection Reason:</strong> ${request.rejectionReason != null ? request.rejectionReason : "N/A"}</p>
                    </c:otherwise>
                </c:choose>

                <c:if test="${requestType == 'Export'}">
                    <p><strong>Approver:</strong> ${request.approverName != null ? request.approverName : "N/A"}</p>
                    <p><strong>Approved At:</strong> <c:choose>
                        <c:when test="${request.approvedAt != null}">
                            <fmt:formatDate value="${request.approvedAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose></p>
                    <p><strong>Delivery Date:</strong> <c:choose>
                        <c:when test="${request.deliveryDate != null}">
                            <fmt:formatDate value="${request.deliveryDate}" pattern="dd/MM/yyyy" />
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose></p>
                </c:if>
                <c:if test="${requestType == 'Purchase'}">
                </c:if>
                <c:if test="${requestType == 'Repair'}">
                </c:if>

                <h3>Details</h3>
                <c:choose>
                    <c:when test="${requestType == 'Export'}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Material Name</th>
                                    <th>Material Code</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Image</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${details}">
                                    <tr>
                                        <td>${detail.materialName}</td>
                                        <td>${detail.materialCode}</td>
                                        <td>${detail.quantity}</td>
                                        <td>${detail.materialUnit}</td>
                                        <td>
                                            <c:forEach var="material" items="${materials}">
                                                <c:if test="${material.materialId == detail.materialId}">
                                                    <c:set var="mediaUrl" value="${material.materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${mediaUrl != null && mediaUrl != '' && (fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/'))}">
                                                            <img src="${mediaUrl}" alt="${detail.materialName}" />
                                                        </c:when>
                                                        <c:when test="${mediaUrl != null && mediaUrl != ''}">
                                                            <img src="${pageContext.request.contextPath}/${mediaUrl}" alt="${detail.materialName}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span>No image available</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:when test="${requestType == 'Purchase'}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Material ID</th>
                                    <th>Material Name</th>
                                    <th>Material Code</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Notes</th>
                                    <th>Image</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${details}">
                                    <tr>
                                        <td>${detail.materialId}</td>
                                        <td>${detail.materialName}</td>
                                        <td>${detail.materialCode}</td>
                                        <td>${detail.quantity}</td>
                                        <td>${detail.unitName}</td>
                                        <td>${detail.notes != null ? detail.notes : "N/A"}</td>
                                        <td>
                                            <c:forEach var="material" items="${materials}">
                                                <c:if test="${material.materialId == detail.materialId}">
                                                    <c:set var="mediaUrl" value="${material.materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${mediaUrl != null && mediaUrl != '' && (fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/'))}">
                                                            <img src="${mediaUrl}" alt="${detail.materialName}" />
                                                        </c:when>
                                                        <c:when test="${mediaUrl != null && mediaUrl != ''}">
                                                            <img src="${pageContext.request.contextPath}/${mediaUrl}" alt="${detail.materialName}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span>No image available</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:when test="${requestType == 'Repair'}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Material ID</th>
                                    <th>Material Name</th>
                                    <th>Material Code</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Damage Description</th>
                                    <th>Repair Cost</th>
                                    <th>Image</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${details}">
                                    <tr>
                                        <td>${detail.materialId}</td>
                                        <td>${detail.materialName}</td>
                                        <td>${detail.materialCode}</td>
                                        <td>${detail.quantity}</td>
                                        <td>${detail.unitName}</td>
                                        <td>${detail.damageDescription != null ? detail.damageDescription : "N/A"}</td>
                                        <td>${detail.repairCost != null ? detail.repairCost : "N/A"}</td>
                                        <td>
                                            <c:forEach var="material" items="${materials}">
                                                <c:if test="${material.materialId == detail.materialId}">
                                                    <c:choose>
                                                        <c:set var="mediaUrl" value="${material.materialsUrl}" />
                                                        <c:choose>
                                                            <c:when test="${mediaUrl != null && mediaUrl != '' && (fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/'))}">
                                                                <img src="${mediaUrl}" alt="${detail.materialName}" />
                                                            </c:when>
                                                            <c:when test="${mediaUrl != null && mediaUrl != ''}">
                                                                <img src="${pageContext.request.contextPath}/${mediaUrl}" alt="${detail.materialName}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span>No image available</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:choose>
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:when test="${requestType == 'PurchaseOrder'}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Material Name</th>
                                    <th>Category</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Unit Price</th>
                                    <th>Total Price</th>
                                    <th>Supplier</th>
                                    <th>Note</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="detail" items="${details}">
                                    <tr>
                                        <td>${detail.materialName}</td>
                                        <td>${detail.categoryName}</td>
                                        <td>${detail.quantity}</td>
                                        <td>${detail.unitName}</td>
                                        <td><fmt:formatNumber value="${detail.unitPrice}" type="currency" currencySymbol="$" /></td>
                                        <td><fmt:formatNumber value="${detail.quantity * detail.unitPrice}" type="currency" currencySymbol="$" /></td>
                                        <td>${detail.supplierName != null ? detail.supplierName : "N/A"}</td>
                                        <td>${detail.note != null ? detail.note : "N/A"}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                </c:choose>

                <c:if test="${totalPages > 1}">
                    <nav class="mt-3">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ViewRequestDetails?type=${requestType.toLowerCase()}&id=${requestType == 'Export' ? request.exportRequestId : (requestType == 'Purchase' ? request.purchaseRequestId : (requestType == 'Repair' ? request.repairRequestId : request.poId))}&page=${currentPage - 1}">Previous</a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/ViewRequestDetails?type=${requestType.toLowerCase()}&id=${requestType == 'Export' ? request.exportRequestId : (requestType == 'Purchase' ? request.purchaseRequestId : (requestType == 'Repair' ? request.repairRequestId : request.poId))}&page=${i}">${i}</a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/ViewRequestDetails?type=${requestType.toLowerCase()}&id=${requestType == 'Export' ? request.exportRequestId : (requestType == 'Purchase' ? request.purchaseRequestId : (requestType == 'Repair' ? request.repairRequestId : request.poId))}&page=${currentPage + 1}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </c:if>

                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/ViewRequests" class="btn btn-secondary">Back to Requests</a>
                    <c:if test="${request.status == 'pending'}">
                        <form action="${pageContext.request.contextPath}/ViewRequestDetails" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="type" value="${requestType.toLowerCase()}">
                            <input type="hidden" name="id" value="${requestType == 'Export' ? request.exportRequestId : (requestType == 'Purchase' ? request.purchaseRequestId : (requestType == 'Repair' ? request.repairRequestId : request.poId))}">
                            <input type="hidden" name="page" value="${currentPage}">
                            <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel Request</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </section>

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
    </body>
</html>