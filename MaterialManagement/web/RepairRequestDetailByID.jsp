<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.RepairRequestDetail"%>
<%@page import="entity.Material"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="dal.RolePermissionDAO" %>
<%@ page import="entity.User" %>
<%
    RolePermissionDAO rolePermissionDAO = (RolePermissionDAO) request.getAttribute("rolePermissionDAO");
    if (rolePermissionDAO == null) {
        rolePermissionDAO = new RolePermissionDAO();
    }
    User user = (User) session.getAttribute("user");
    boolean canApprove = user != null && (user.getRoleId() == 1 || user.getRoleId() == 2 || rolePermissionDAO.hasPermission(user.getRoleId(), "Duyệt yêu cầu sửa"));
    boolean canReject = user != null && (user.getRoleId() == 1 || user.getRoleId() == 2 || rolePermissionDAO.hasPermission(user.getRoleId(), "Duyệt yêu cầu sửa"));
    request.setAttribute("canApprove", canApprove);
    request.setAttribute("canReject", canReject);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Repair Request Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f8f9fa;
            }
            .container {
                max-width: 1000px;
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
            .table {
                margin-top: 20px;
            }
            .btn {
                margin-right: 10px;
                border: none;
                color: #fff;
            }
            .btn-approve {
                background-color: #198754;
            }
            .btn-approve:hover {
                background-color: #17643a;
            }
            .btn-reject {
                background-color: #dc3545;
            }
            .btn-reject:hover {
                background-color: #b52d3a;
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
            .alert {
                margin-top: 20px;
            }
            .status-tag {
                padding: 5px 10px;
                border-radius: 5px;
                color: #fff;
                background-color: #6c757d;
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
                min-height: 48px;
            }
            .status-approved {
                background-color: #198754;
            }
            .status-rejected {
                background-color: #dc3545;
            }
            .pagination {
                margin-top: 20px;
                justify-content: center;
            }
            .pagination .page-link {
                color: #DEAD6F;
                border: 1px solid #DEAD6F;
                margin: 0 4px;
                border-radius: 6px;
            }
            .pagination .page-link:hover {
                background-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.active .page-link {
                background-color: #DEAD6F;
                border-color: #DEAD6F;
                color: #fff;
            }
            .pagination .page-item.disabled .page-link {
                color: #6c757d;
                border-color: #dee2e6;
                background-color: #f8f9fa;
            }
            .pagination-ellipsis {
                color: #6c757d;
                padding: 0 10px;
                line-height: 38px;
            }
            .action-buttons {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
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
            <h2>Repair Request Details - Request ID: ${requestId}
                <span class="status-tag
                      <c:choose>
                          <c:when test="${status == 'pending'}">status-pending</c:when>
                          <c:when test="${status == 'approved'}">status-approved</c:when>
                          <c:when test="${status == 'rejected'}">status-rejected</c:when>
                          <c:otherwise>status-tag</c:otherwise>
                      </c:choose>">
                    ${status != null ? status : "N/A"}
                </span>
            </h2>
            <c:if test="${not empty details && !details.isEmpty()}">
                <p>Created at: ${details[0].createdAt != null ? details[0].createdAt : "N/A"}</p>
                <p>Updated at: ${details[0].updatedAt != null ? details[0].updatedAt : "N/A"}</p>
            </c:if>
            <c:if test="${empty details}">
                <p>Created at: N/A</p>
                <p>Updated at: N/A</p>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <div class="card">
                <div class="card-header">Request Information</div>
                <div class="card-body">
                    <p><strong>Description of Damage:</strong></p>
                    <p>
                        <c:choose>
                            <c:when test="${not empty details && !details.isEmpty()}">
                                ${details[0].damageDescription != null ? details[0].damageDescription : "N/A"}
                            </c:when>
                            <c:otherwise>N/A</c:otherwise>
                        </c:choose>
                    </p>
                    <p><strong>Supplier Name:</strong> ${supplierName}</p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">Material List</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty details || details.isEmpty()}">
                            <div class="text-muted fst-italic text-center">There are no details for this repair request.</div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table custom-table">
                                    <thead>
                                        <tr>
                                            <th>Images</th>
                                            <th>Material Code</th>
                                            <th>Material Name</th>
                                            <th>Category</th>
                                            <th>Unit</th>
                                            <th>Quantity</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="d" items="${details}">
                                            <c:set var="m" value="${d.material}"/>
                                            <tr>
                                                <td>
                                                    <c:set var="mediaUrl" value="${m.materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${mediaUrl != null && mediaUrl != '' && (fn:startsWith(mediaUrl, 'http://') || fn:startsWith(mediaUrl, 'https://') || fn:startsWith(mediaUrl, '/'))}">
                                                            <img src="${mediaUrl}" 
                                                                 alt="Material Image" 
                                                                 class="img-fluid" 
                                                                 style="width: 100px; height: auto; object-fit: cover;">
                                                        </c:when>
                                                        <c:when test="${mediaUrl != null && mediaUrl != ''}">
                                                            <img src="${pageContext.request.contextPath}/${mediaUrl}" 
                                                                 alt="Material Image" 
                                                                 class="img-fluid" 
                                                                 style="width: 100px; height: auto; object-fit: cover;">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${pageContext.request.contextPath}/images/material/default.jpg" 
                                                                 alt="Material Image" 
                                                                 class="img-fluid" 
                                                                 style="width: 100px; height: auto; object-fit: cover;">
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <br>
                                                    <span style="font-size:12px; color:#888;">
                                                        ${m.rawUrl}
                                                    </span>
                                                </td>
                                                <td>${m != null ? m.materialCode : "N/A"}</td>
                                                <td>${m != null ? m.materialName : "N/A"}</td>
                                                <td>${m != null && m.category != null ? m.category.category_name : "N/A"}</td>
                                                <td>${m != null && m.unit != null ? m.unit.unitName : "N/A"}</td>
                                                <td>${d.quantity}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <nav>
                                    <ul class="pagination">
                                        <!-- Previous Button -->
                                        <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="repairrequestdetailbyID?page=${currentPage-1}&requestId=${requestId}">Previous</a>
                                        </li>
                                        <!-- Page Numbers -->
                                        <c:set var="startPage" value="${currentPage - 2 > 1 ? currentPage - 2 : 1}"/>
                                        <c:set var="endPage" value="${currentPage + 2 < totalPages ? currentPage + 2 : totalPages}"/>
                                        <c:if test="${startPage > 1}">
                                            <li class="page-item"><a class="page-link" href="repairrequestdetailbyID?page=1&requestId=${requestId}">1</a></li>
                                            <li class="page-item disabled"><span class="pagination-ellipsis">...</span></li>
                                            </c:if>
                                            <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link" href="repairrequestdetailbyID?page=${i}&requestId=${requestId}">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <c:if test="${endPage < totalPages}">
                                            <li class="page-item disabled"><span class="pagination-ellipsis">...</span></li>
                                            <li class="page-item"><a class="page-link" href="repairrequestdetailbyID?page=${totalPages}&requestId=${requestId}">${totalPages}</a></li>
                                            </c:if>
                                        <!-- Next Button -->
                                        <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="repairrequestdetailbyID?page=${currentPage+1}&requestId=${requestId}">Next</a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <c:choose>
                <c:when test="${status == 'pending' && (canApprove || canReject)}">
                    <div class="card">
                        <div class="card-header">Actions</div>
                        <div class="card-body">
                            <form action="${canApprove ? 'approve' : 'reject'}" method="post">
                                <input type="hidden" name="requestId" value="${requestId}">
                                <div class="mb-3">
                                    <textarea class="form-control" name="reason" placeholder="Enter reason for action..." required></textarea>
                                </div>
                                <div class="action-buttons">
                                    <c:if test="${canApprove}">
                                        <button class="btn btn-approve" type="submit" formaction="approve" onclick="return confirm('Are you sure you want to approve this request?')"> Approve</button>
                                    </c:if>
                                    <c:if test="${canReject}">
                                        <button class="btn btn-reject" type="submit" formaction="reject" onclick="return confirm('Are you sure you want to reject this request?')">Reject</button>
                                    </c:if>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <c:if test="${!canApprove && !canReject}">
                        <div class="text-muted fst-italic text-center">You only have permission to view the repair request details.</div>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <a href="repairrequestlist" class="btn btn-cancel">Cancel</a>
        </div>
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->
    </body>
</html>