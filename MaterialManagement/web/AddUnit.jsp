<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add New Unit</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .card-header-brown {
            background-color: #DEB887;
            color: #fff;
            font-size: 2rem;
            font-weight: bold;
            border-radius: 8px 8px 0 0;
            padding: 18px 32px;
        }
        .unit-card {
            border-radius: 8px;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08);
            background: #fff;
            max-width: 900px;
            margin: 40px auto;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .form-control.is-invalid {
            border-color: #dc3545;
        }
        .error-alert {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        .error-list {
            margin: 0;
            padding-left: 20px;
        }
        .error-list li {
            margin-bottom: 5px;
        }
        .btn-brown {
            background-color: #DEB887 !important;
            color: #fff !important;
            border: none;
        }
        .btn-brown:hover, .btn-brown:focus {
            background-color: #c49b63 !important;
            color: #fff !important;
        }
        .permission-denied {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 20px;
            border-radius: 5px;
            text-align: center;
            margin: 40px auto;
            max-width: 600px;
        }
        .text-coffee {
            color: #E9B775 !important;
        }
    </style>
</head>
<body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-3 col-lg-2 bg-light p-0">
                <jsp:include page="Sidebar.jsp" />
            </div>
            <div class="col-md-9 col-lg-10 px-md-4 py-4">
                <section id="AddUnit" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                    <div class="container">
                        <div class="row my-5 py-5">
                            <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                <c:choose>
                                    <c:when test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Tạo đơn vị')}">
                                        <h2 class="display-4 fw-normal text-center mb-4">Add New <span class="text-coffee">Unit</span></h2>
                                        <c:if test="${not empty errors or not empty error}">
                                            <div class="error-alert">
                                                <h6 class="mb-2"><strong>Please correct the following errors:</strong></h6>
                                                <ul class="error-list">
                                                    <c:if test="${not empty error}">
                                                        <li>${error}</li>
                                                    </c:if>
                                                    <c:forEach var="entry" items="${errors}">
                                                        <li><strong>${entry.key}:</strong> ${entry.value}</li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                        </c:if>
                                        <form action="AddUnit" method="post">
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Unit Code</label>
                                                    <input type="text" class="form-control ${not empty errors.unitCode ? 'is-invalid' : ''}" 
                                                           name="unitCode" value="${param.unitCode != null ? param.unitCode : (unitCode != null ? unitCode : (unit != null ? unit.unitCode : ''))}" placeholder="Auto-generated if empty">
                                                    <c:if test="${not empty errors.unitCode}">
                                                        <div class="error-message">${errors.unitCode}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Unit Name <span class="text-danger">*</span></label>
                                                    <input type="text" class="form-control ${not empty errors.unitName ? 'is-invalid' : ''}" 
                                                           name="unitName" value="${param.unitName != null ? param.unitName : (unitName != null ? unitName : (unit != null ? unit.unitName : ''))}" required>
                                                    <c:if test="${not empty errors.unitName}">
                                                        <div class="error-message">${errors.unitName}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Symbol <span class="text-danger">*</span></label>
                                                    <input type="text" class="form-control ${not empty errors.symbol ? 'is-invalid' : ''}" 
                                                           name="symbol" value="${param.symbol != null ? param.symbol : (symbol != null ? symbol : (unit != null ? unit.symbol : ''))}" required>
                                                    <c:if test="${not empty errors.symbol}">
                                                        <div class="error-message">${errors.symbol}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Is Base Unit</label>
                                                    <select class="form-select" name="isBase">
                                                        <option value="false" ${param.isBase == 'false' ? 'selected' : (isBase != null && !isBase ? 'selected' : (unit != null && !unit.base ? 'selected' : 'selected'))}>No</option>
                                                        <option value="true" ${param.isBase == 'true' ? 'selected' : (isBase != null && isBase ? 'selected' : (unit != null && unit.base ? 'selected' : ''))}>Yes</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6 mb-3">
                                                    <label class="form-label">Status</label>
                                                    <select class="form-select" name="status">
                                                        <option value="active" ${param.status == 'active' ? 'selected' : (status != null && status == 'active' ? 'selected' : (unit != null && unit.status == 'active' ? 'selected' : 'selected'))}>Active</option>
                                                        <option value="inactive" ${param.status == 'inactive' ? 'selected' : (status != null && status == 'inactive' ? 'selected' : (unit != null && unit.status == 'inactive' ? 'selected' : ''))}>Inactive</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                                <button type="submit" class="btn btn-brown px-4">Add Unit</button>
                                                <a href="UnitList" class="btn btn-secondary px-4">Cancel</a>
                                            </div>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="permission-denied">
                                            <h4><i class="fas fa-exclamation-triangle"></i> Access Denied</h4>
                                            <p>You do not have permission to add new unit.</p>
                                            <a href="UnitList" class="btn btn-primary">Back to List</a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>