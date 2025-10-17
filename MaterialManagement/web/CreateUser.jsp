<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="entity.User, entity.Department, java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    User admin = (User) session.getAttribute("user");
    if (admin == null || admin.getRoleId() != 1) {
        response.sendRedirect("Login.jsp");
        return;
    }
    Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
    User enteredUser = (User) request.getAttribute("enteredUser");
    String dateOfBirthStr = (String) request.getAttribute("enteredDateOfBirth");
    String enteredGender = (String) request.getAttribute("enteredGender");
    String enteredRoleId = (String) request.getAttribute("enteredRoleId");
    String enteredDepartmentId = (String) request.getAttribute("enteredDepartmentId");
    String enteredDepartmentName = (String) request.getAttribute("enteredDepartmentName");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Create New User</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
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
            .form-container .img-thumbnail {
                max-height: 150px;
            }
            .ui-autocomplete {
                max-height: 200px;
                overflow-y: auto;
                overflow-x: hidden;
                border: 1px solid #ced4da;
                border-radius: 0.25rem;
                background-color: #fff;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
                z-index: 1000;
            }
            .ui-menu-item {
                padding: 8px 12px;
                font-size: 1rem;
                cursor: pointer;
            }
            .ui-menu-item:hover {
                background-color: #f8f9fa;
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
                    <section id="CreateUser" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                        <div class="container">
                            <div class="row my-5 py-5">
                                <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                    <h2 class="display-4 fw-normal text-center mb-4">Create New <span class="text-primary">User</span></h2>
                                    <%
                                        String message = (String) request.getAttribute("message");
                                        String error = (String) request.getAttribute("error");
                                    %>
                                    <% if (message != null) { %>
                                    <div class="alert alert-success">${fn:escapeXml(message)}</div>
                                    <% } %>
                                    <% if (error != null) { %>
                                    <div class="alert alert-danger">${fn:escapeXml(error)}</div>
                                    <% } %>
                                    <form action="${pageContext.request.contextPath}/CreateUser" method="post" enctype="multipart/form-data" id="createUserForm">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="username" class="form-label text-muted">Username</label>
                                                    <input type="text" class="form-control <% if(errors != null && errors.containsKey("username")) { %>is-invalid<% } %>" 
                                                           name="username" id="username"
                                                           placeholder="Enter Username" maxlength="10"
                                                           value="${fn:escapeXml(enteredUser != null && enteredUser.getUsername() != null ? enteredUser.getUsername() : '')}" required>
                                                    <% if (errors != null && errors.containsKey("username")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("username"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="fullName" class="form-label text-muted">Full Name</label>
                                                    <input type="text" class="form-control <% if(errors != null && errors.containsKey("fullName")) { %>is-invalid<% } %>" 
                                                           name="fullName" id="fullName"
                                                           placeholder="Enter Full Name"
                                                           value="${fn:escapeXml(enteredUser != null && enteredUser.getFullName() != null ? enteredUser.getFullName() : '')}" required>
                                                    <% if (errors != null && errors.containsKey("fullName")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("fullName"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="email" class="form-label text-muted">Email</label>
                                                    <input type="email" class="form-control <% if(errors != null && errors.containsKey("email")) { %>is-invalid<% } %>" 
                                                           name="email" id="email"
                                                           placeholder="Enter Email" maxlength="100"
                                                           value="${fn:escapeXml(enteredUser != null && enteredUser.getEmail() != null ? enteredUser.getEmail() : '')}" required>
                                                    <% if (errors != null && errors.containsKey("email")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("email"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="phoneNumber" class="form-label text-muted">Phone Number</label>
                                                    <input type="text" class="form-control <% if(errors != null && errors.containsKey("phoneNumber")) { %>is-invalid<% } %>" 
                                                           name="phoneNumber" id="phoneNumber"
                                                           placeholder="Enter Phone Number" maxlength="15"
                                                           value="${fn:escapeXml(enteredUser != null && enteredUser.getPhoneNumber() != null ? enteredUser.getPhoneNumber() : '')}" required>
                                                    <% if (errors != null && errors.containsKey("phoneNumber")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("phoneNumber"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="address" class="form-label text-muted">Address</label>
                                                    <input type="text" class="form-control <% if(errors != null && errors.containsKey("address")) { %>is-invalid<% } %>" 
                                                           name="address" id="address"
                                                           placeholder="Enter Address"
                                                           value="${fn:escapeXml(enteredUser != null && enteredUser.getAddress() != null ? enteredUser.getAddress() : '')}" required>
                                                    <% if (errors != null && errors.containsKey("address")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("address"))}</div>
                                                    <% } %>
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="mb-3">
                                                    <label for="userPicture" class="form-label text-muted">User Picture</label>
                                                    <input class="form-control <% if(errors != null && errors.containsKey("userPicture")) { %>is-invalid<% } %>" 
                                                           type="file" name="userPicture" id="userPicture" accept="image/*">
                                                    <img id="previewImage" src="#" alt="Image Preview" class="img-thumbnail mt-3" style="display:none; max-height: 150px;">
                                                    <% if (errors != null && errors.containsKey("userPicture")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("userPicture"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="dateOfBirth" class="form-label text-muted">Date of Birth</label>
                                                    <input type="date" class="form-control <% if(errors != null && errors.containsKey("dateOfBirth")) { %>is-invalid<% } %>" 
                                                           name="dateOfBirth" id="dateOfBirth"
                                                           value="${fn:escapeXml(dateOfBirthStr != null ? dateOfBirthStr : '')}">
                                                    <% if (errors != null && errors.containsKey("dateOfBirth")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("dateOfBirth"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="gender" class="form-label text-muted">Gender</label>
                                                    <select class="form-select <% if(errors != null && errors.containsKey("gender")) { %>is-invalid<% } %>" 
                                                            name="gender" id="gender">
                                                        <option value="" <%= enteredGender == null || enteredGender.isEmpty() ? "selected" : "" %>>Select Gender</option>
                                                        <option value="male" <%= "male".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Male</option>
                                                        <option value="female" <%= "female".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Female</option>
                                                        <option value="other" <%= "other".equalsIgnoreCase(enteredGender) ? "selected" : "" %>>Other</option>
                                                    </select>
                                                    <% if (errors != null && errors.containsKey("gender")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("gender"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="roleId" class="form-label text-muted">Role</label>
                                                    <select class="form-select <% if(errors != null && errors.containsKey("roleId")) { %>is-invalid<% } %>" 
                                                            name="roleId" id="roleId" required>
                                                        <option value="" <%= enteredRoleId == null || enteredRoleId.isEmpty() ? "selected" : "" %>>Select Role</option>
                                                        <option value="2" <%= "2".equals(enteredRoleId) ? "selected" : "" %>>Director</option>
                                                        <option value="3" <%= "3".equals(enteredRoleId) ? "selected" : "" %>>Staff</option>
                                                        <option value="4" <%= "4".equals(enteredRoleId) ? "selected" : "" %>>Employee</option>
                                                    </select>
                                                    <% if (errors != null && errors.containsKey("roleId")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("roleId"))}</div>
                                                    <% } %>
                                                </div>
                                                <div class="mb-3">
                                                    <label for="departmentName" class="form-label text-muted">Department</label>
                                                    <input type="text" id="departmentName" name="departmentName" 
                                                           class="form-control <% if(errors != null && errors.containsKey("departmentId")) { %>is-invalid<% } %>"
                                                           placeholder="Type department name" 
                                                           value="${fn:escapeXml(enteredDepartmentName != null ? enteredDepartmentName : '')}">
                                                    <input type="hidden" name="departmentId" id="departmentId" 
                                                           value="${fn:escapeXml(enteredDepartmentId != null ? enteredDepartmentId : '')}">
                                                    <% if (errors != null && errors.containsKey("departmentId")) { %>
                                                    <div class="invalid-feedback">${fn:escapeXml(errors.get("departmentId"))}</div>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-12">
                                                <div class="d-grid gap-2 mb-3">
                                                    <button type="submit" class="btn btn-dark btn-lg rounded-1">Create User</button>
                                                </div>
                                                <div class="d-grid gap-2 mb-3">
                                                    <a href="UserList" class="btn btn-secondary btn-lg rounded-1">← Back to User List</a>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>

        <jsp:include page="Footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
            document.getElementById("userPicture").addEventListener("change", function () {
                const fileInput = this;
                const previewImage = document.getElementById("previewImage");
                if (fileInput.files && fileInput.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        previewImage.src = e.target.result;
                        previewImage.style.display = "block";
                    };
                    reader.readAsDataURL(fileInput.files[0]);
                } else {
                    previewImage.src = "#";
                    previewImage.style.display = "none";
                }
            });

            // Handle role selection to enable/disable department field
            document.getElementById('roleId').addEventListener('change', function() {
                const departmentNameInput = document.getElementById('departmentName');
                const departmentIdInput = document.getElementById('departmentId');
                if (this.value === '2') { // Director role
                    departmentNameInput.disabled = true;
                    departmentNameInput.value = '';
                    departmentIdInput.value = '';
                    departmentNameInput.classList.remove('is-invalid');
                } else {
                    departmentNameInput.disabled = false;
                }
            });

            document.addEventListener('DOMContentLoaded', function() {
                const roleId = document.getElementById('roleId').value;
                const departmentNameInput = document.getElementById('departmentName');
                const departmentIdInput = document.getElementById('departmentId');
                if (roleId === '2') {
                    departmentNameInput.disabled = true;
                    departmentNameInput.value = '';
                    departmentIdInput.value = '';
                    departmentNameInput.classList.remove('is-invalid');
                }

                const departments = [
                    <c:forEach var="dept" items="${departments}" varStatus="loop">
                    {
                        label: "${fn:escapeXml(dept.departmentName)}",
                        value: "${fn:escapeXml(dept.departmentName)}",
                        id: "${dept.departmentId}"
                    }${loop.last ? '' : ','}
                    </c:forEach>
                ];

                $("#departmentName").autocomplete({
                    source: function(request, response) {
                        const term = request.term.toLowerCase();
                        const matches = departments.filter(dept =>
                            dept.label.toLowerCase().includes(term)
                        );
                        response(matches);
                    },
                    select: function(event, ui) {
                        document.getElementById('departmentId').value = ui.item.id;
                        document.getElementById('departmentName').value = ui.item.value;
                        document.getElementById('departmentName').classList.remove('is-invalid');
                    },
                    change: function(event, ui) {
                        if (!ui.item) {
                            const inputValue = document.getElementById('departmentName').value.toLowerCase().trim();
                            const selectedDept = departments.find(dept =>
                                dept.label.toLowerCase() === inputValue
                            );
                            if (selectedDept) {
                                document.getElementById('departmentId').value = selectedDept.id;
                                document.getElementById('departmentName').value = selectedDept.label;
                                document.getElementById('departmentName').classList.remove('is-invalid');
                            } else {
                                document.getElementById('departmentId').value = '';
                                document.getElementById('departmentName').classList.add('is-invalid');
                            }
                        }
                    },
                    minLength: 1
                });
            });

            document.getElementById('createUserForm').addEventListener('submit', function(event) {
                let errors = [];
                const roleId = document.getElementById('roleId').value;
                const departmentId = document.getElementById('departmentId').value;
                const departmentName = document.getElementById('departmentName').value;

                if (roleId !== '2') {
                    if (!departmentId || !departmentName) {
                        errors.push('Department is required for non-Director roles.');
                        document.getElementById('departmentName').classList.add('is-invalid');
                        document.getElementById('departmentName').nextElementSibling.textContent = 'Department is required.';
                    } else {
                        document.getElementById('departmentName').classList.remove('is-invalid');
                    }
                } else {
                    // Clear department fields for Director
                    document.getElementById('departmentId').value = '';
                    document.getElementById('departmentName').value = '';
                    document.getElementById('departmentName').classList.remove('is-invalid');
                }

                const username = document.getElementById('username').value;
                if (!username) {
                    errors.push('Username is required.');
                    document.getElementById('username').classList.add('is-invalid');
                    document.getElementById('username').nextElementSibling.textContent = 'Username is required.';
                } else if (username.length > 10) {
                    errors.push('Username must not exceed 10 characters.');
                    document.getElementById('username').classList.add('is-invalid');
                    document.getElementById('username').nextElementSibling.textContent = 'Username must not exceed 10 characters.';
                } else {
                    document.getElementById('username').classList.remove('is-invalid');
                }

                const fullName = document.getElementById('fullName').value;
                if (!fullName) {
                    errors.push('Full name is required.');
                    document.getElementById('fullName').classList.add('is-invalid');
                    document.getElementById('fullName').nextElementSibling.textContent = 'Full name is required.';
                } else if (fullName.length > 25) {
                    errors.push('Full name must not exceed 25 characters.');
                    document.getElementById('fullName').classList.add('is-invalid');
                    document.getElementById('fullName').nextElementSibling.textContent = 'Full name must not exceed 25 characters.';
                } else {
                    document.getElementById('fullName').classList.remove('is-invalid');
                }

                const email = document.getElementById('email').value;
                if (!email) {
                    errors.push('Email is required.');
                    document.getElementById('email').classList.add('is-invalid');
                    document.getElementById('email').nextElementSibling.textContent = 'Email is required.';
                } else {
                    document.getElementById('email').classList.remove('is-invalid');
                }

                const phoneNumber = document.getElementById('phoneNumber').value;
                if (!phoneNumber) {
                    errors.push('Phone number is required.');
                    document.getElementById('phoneNumber').classList.add('is-invalid');
                    document.getElementById('phoneNumber').nextElementSibling.textContent = 'Phone number is required.';
                } else {
                    document.getElementById('phoneNumber').classList.remove('is-invalid');
                }

                const address = document.getElementById('address').value;
                if (!address) {
                    errors.push('Address is required.');
                    document.getElementById('address').classList.add('is-invalid');
                    document.getElementById('address').nextElementSibling.textContent = 'Address is required.';
                } else if (address.length > 25) {
                    errors.push('Address must not exceed 25 characters.');
                    document.getElementById('address').classList.add('is-invalid');
                    document.getElementById('address').nextElementSibling.textContent = 'Address must not exceed 25 characters.';
                } else {
                    document.getElementById('address').classList.remove('is-invalid');
                }

                if (!roleId) {
                    errors.push('Role is required.');
                    document.getElementById('roleId').classList.add('is-invalid');
                    document.getElementById('roleId').nextElementSibling.textContent = 'Role is required.';
                } else {
                    document.getElementById('roleId').classList.remove('is-invalid');
                }

                if (errors.length > 0) {
                    event.preventDefault();
                }
            });
        </script>
    </body>
</html>