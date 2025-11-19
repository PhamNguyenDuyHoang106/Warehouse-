<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Edit Department</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
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
        <jsp:include page="Header.jsp" />

        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="Sidebar.jsp" />
                </div>
                <!-- Page Content -->
                <div class="col-md-9 col-lg-10 px-md-4 py-4">
                    <section id="EditDepartment" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                        <div class="container">
                            <div class="row my-5 py-5">
                                <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                    <h2 class="display-4 fw-normal text-center mb-4">Edit <span class="text-primary">Department</span></h2>

                                    <c:if test="${not empty error}">
                                        <div class="alert alert-danger">${error}</div>
                                    </c:if>

                                    <c:choose>
                                        <c:when test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Sửa phòng ban')}">
                                            <form action="editdepartment" method="post" id="departmentForm">
                                                <input type="hidden" name="id" value="${department.departmentId}">
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="name" class="form-label text-muted">Department Name</label>
                                                        <input type="text" class="form-control" id="name" name="name" value="${department.departmentName}" required>
                                                        <div class="invalid-feedback" id="nameError"></div>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="code" class="form-label text-muted">Department Code</label>
                                                        <input type="text" class="form-control" id="code" name="code" value="${department.departmentCode}" required>
                                                        <div class="invalid-feedback" id="codeError"></div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="phone" class="form-label text-muted">Phone Number</label>
                                                        <input type="text" class="form-control" id="phone" name="phone" value="${department.phoneNumber}">
                                                        <div class="invalid-feedback" id="phoneError"></div>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="email" class="form-label text-muted">Email</label>
                                                        <input type="email" class="form-control" id="email" name="email" value="${department.email}">
                                                        <div class="invalid-feedback" id="emailError"></div>
                                                    </div>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-6 mb-3">
                                                        <label for="location" class="form-label text-muted">Location</label>
                                                        <input type="text" class="form-control" id="location" name="location" value="${department.location}">
                                                        <div class="invalid-feedback" id="locationError"></div>
                                                    </div>
                                                    <div class="col-md-6 mb-3">
                                                        <label for="status" class="form-label text-muted">Status</label>
                                                        <select class="form-select" id="status" name="status" required>
                                                            <option value="active" ${department.status == 'active' ? 'selected' : ''}>Active</option>
                                                            <option value="inactive" ${department.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                                        </select>
                                                        <div class="invalid-feedback" id="statusError"></div>
                                                    </div>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="description" class="form-label text-muted">Description</label>
                                                    <textarea class="form-control" id="description" name="description" rows="3">${department.description}</textarea>
                                                </div>
                                                <div class="d-grid gap-2">
                                                    <button type="submit" class="btn btn-dark btn-lg rounded-1">Update Department</button>
                                                    <a href="depairmentlist" class="btn btn-secondary btn-lg rounded-1">Cancel</a>
                                                </div>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-danger text-center">
                                                <h4><i class="fas fa-exclamation-triangle"></i> Access Denied</h4>
                                                <p>You do not have permission to edit department.</p>
                                                <a href="depairmentlist" class="btn btn-primary">Back to List</a>
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

        <footer class="footer py-4 bg-light mt-auto">
            <div class="container text-center">
                <span class="text-muted">© 2025 Computer Accessories - All Rights Reserved.</span>
            </div>
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script>
            document.getElementById('departmentForm').addEventListener('submit', function (event) {
                let isValid = true;
                const name = document.querySelector('input[name="name"]').value.trim();
                const code = document.querySelector('input[name="code"]').value.trim();
                const phone = document.querySelector('input[name="phone"]').value.trim();
                const email = document.querySelector('input[name="email"]').value.trim();
                const location = document.querySelector('input[name="location"]').value.trim();
                const status = document.querySelector('select[name="status"]').value.trim();

                document.querySelectorAll('.invalid-feedback').forEach(el => el.textContent = '');
                document.querySelectorAll('.form-control, .form-select').forEach(el => el.classList.remove('is-invalid'));

                if (!name) {
                    document.getElementById('nameError').textContent = 'Department name cannot be left blank.';
                    document.querySelector('input[name="name"]').classList.add('is-invalid');
                    isValid = false;
                }

                // Validate code
                if (!code) {
                    document.getElementById('codeError').textContent = 'Department code cannot be left blank.';
                    document.querySelector('input[name="code"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (phone && !/^\d{10,12}$/.test(phone)) {
                    document.getElementById('phoneError').textContent = 'Phone number must contain 10 to 12 digits.';
                    document.querySelector('input[name="phone"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                    document.getElementById('emailError').textContent = 'Invalid email format.';
                    document.querySelector('input[name="email"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (location && location.length > 100) {
                    document.getElementById('locationError').textContent = 'Location must not exceed 100 characters.';
                    document.querySelector('input[name="location"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (!status) {
                    document.getElementById('statusError').textContent = 'Status cannot be left blank.';
                    document.querySelector('select[name="status"]').classList.add('is-invalid');
                    isValid = false;
                }

                if (!isValid) {
                    event.preventDefault();
                }
            });
        </script>
    </body>
</html>