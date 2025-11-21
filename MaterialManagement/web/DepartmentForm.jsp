<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Add Department</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="stylesheet" type="text/css" href="css/override-style.css">
        <style>
            body {
                background-color: #f8f9fa;
                padding: 20px;
            }
            .content {
                padding-left: 20px;
                font-family: 'Roboto', sans-serif;
            }
            .form-container .form-control, .form-container .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .form-container .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .form-container .btn {
                font-size: 1rem;
                padding: 0.75rem;
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
                <div class="col-12 px-md-4 py-4">
                    <section id="AddDepartment" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                        <div class="container">
                            <div class="row my-5 py-5">
                                <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                    <h2 class="display-4 fw-normal text-center mb-4">Add New <span class="text-primary">Department</span></h2>

                                    <c:if test="${not empty error or not empty errors}">
                                        <div class="alert alert-danger">
                                            <c:if test="${not empty error}">
                                                ${error}
                                            </c:if>
                                            <c:forEach var="entry" items="${errors}">
                                                <div>${entry.key}: ${entry.value}</div>
                                            </c:forEach>
                                        </div>
                                    </c:if>

                                    <c:choose>
                                        <c:when test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Tạo phòng ban')}">
                                            <form action="${pageContext.request.contextPath}/adddepartment" method="post" id="departmentForm">
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label class="form-label text-muted">Department Name</label>
                                                        <input type="text" class="form-control" name="name" value="${param.name}" required>
                                                        <div class="invalid-feedback" id="nameError"></div>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label class="form-label text-muted">Phone number</label>
                                                        <input type="text" class="form-control" name="phone" value="${param.phone}" required>
                                                        <div class="invalid-feedback" id="phoneError"></div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label class="form-label text-muted">Email</label>
                                                        <input type="email" class="form-control" name="email" value="${param.email}" required>
                                                        <div class="invalid-feedback" id="emailError"></div>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label class="form-label text-muted">Location</label>
                                                        <input type="text" class="form-control" name="location" value="${param.location}" required>
                                                        <div class="invalid-feedback" id="locationError"></div>
                                                    </div>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label text-muted">Description</label>
                                                    <textarea class="form-control" name="description" rows="3">${param.description}</textarea>
                                                </div>
                                                <div class="d-grid gap-2">
                                                    <button type="submit" class="btn btn-dark btn-lg rounded-1">Add Department</button>
                                                    <a href="${pageContext.request.contextPath}/depairmentlist" class="btn btn-secondary btn-lg rounded-1">Cancel</a>
                                                </div>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-danger text-center">
                                                <h4><i class="fas fa-exclamation-triangle"></i> Access Denied</h4>
                                                <p>You do not have permission to add departments.</p>
                                                <a href="${pageContext.request.contextPath}/depairmentlist" class="btn btn-primary">Back to list</a>
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
          </div> <!-- End main-content-body -->
        </div> <!-- End main-content-wrapper -->

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script>
            document.getElementById('departmentForm').addEventListener('submit', function (event) {
                let isValid = true;
                const name = document.querySelector('input[name="name"]').value.trim();
                const phone = document.querySelector('input[name="phone"]').value.trim();
                const email = document.querySelector('input[name="email"]').value.trim();
                const location = document.querySelector('input[name="location"]').value.trim();

                // Reset error messages
                document.querySelectorAll('.invalid-feedback').forEach(el => el.textContent = '');
                document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

                // Validate name
                if (!name) {
                    document.getElementById('nameError').textContent = 'Department name cannot be left blank.';
                    document.querySelector('input[name="name"]').classList.add('is-invalid');
                    isValid = false;
                }

                // Validate phone
                if (!phone) {
                    document.getElementById('phoneError').textContent = 'Phone number cannot be left blank.';
                    document.querySelector('input[name="phone"]').classList.add('is-invalid');
                    isValid = false;
                } else if (!/^\d{10,12}$/.test(phone)) {
                    document.getElementById('phoneError').textContent = 'Phone number must contain 10 to 12 digits.';
                    document.querySelector('input[name="phone"]').classList.add('is-invalid');
                    isValid = false;
                }

                // Validate email
                if (!email) {
                    document.getElementById('emailError').textContent = 'Email cannot be left blank.';
                    document.querySelector('input[name="email"]').classList.add('is-invalid');
                    isValid = false;
                } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    document.getElementById('emailError').textContent = 'Invalid email format.';
                    document.querySelector('input[name="email"]').classList.add('is-invalid');
                    isValid = false;
                }

                // Validate location
                if (!location) {
                    document.getElementById('locationError').textContent = 'Location cannot be left blank.';
                    document.querySelector('input[name="location"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (!isValid) {
                    event.preventDefault();
                }
            });
        </script>
    </body>
</html>