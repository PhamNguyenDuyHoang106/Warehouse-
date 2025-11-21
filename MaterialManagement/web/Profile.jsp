<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%@ page import="java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    String genderStr = user.getGender() != null ? user.getGender().toString().toLowerCase() : "";
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <title>User Profile - Waggy</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="description" content="View and update your Waggy account profile.">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="stylesheet" type="text/css" href="css/override-style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <style>
            section.py-5 {
                background: url('images/background-img.png') no-repeat center center fixed;
                background-size: contain;
            }
            .profile-section .form-control, .profile-section .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .profile-section .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .profile-section .btn {
                font-size: 1rem;
                padding: 0.75rem 1.5rem;
            }
            .profile-section .nav-buttons .btn {
                margin: 0 0.5rem;
                border-radius: 0.25rem;
            }
            .profile-section .img-thumbnail {
                max-height: 200px;
                width: 200px;
                object-fit: cover;
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
            <section class="py-5">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-md-10 p-4 bg-white rounded shadow profile-section">
                        <!-- Navigation buttons -->
                        <jsp:include page="Navigation.jsp" />

                        <h2 class="text-center display-5 mb-4">Profile of: <span class="text-primary"><%= user.getUsername() %></span></h2>
                        <!-- Message displays -->
                        <%
                            String message = (String) request.getAttribute("message");
                            String error = (String) request.getAttribute("error");
                            Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                        %>
                        <% if (message != null) { %>
                        <div class="alert alert-success text-center" role="alert">
                            <%= message %>
                        </div>
                        <% } %>
                        <% if (error != null) { %>
                        <div class="alert alert-danger text-center" role="alert">
                            <%= error %>
                        </div>
                        <% } %>
                        <% if (errors != null && !errors.isEmpty()) { %>
                        <div class="alert alert-warning" role="alert">
                            <ul class="mb-0">
                                <% for (Map.Entry<String, String> entry : errors.entrySet()) { %>
                                <li><strong><%= entry.getKey() %>:</strong> <%= entry.getValue() %></li>
                                <% } %>
                            </ul>
                        </div>
                        <% } %>

                        <div class="row">
                            <!-- Avatar -->
                            <div class="col-md-4 text-center">
                                <% if (user.getUserPicture() != null && !user.getUserPicture().isEmpty()) { %>
                                <img id="previewImage" src="images/profiles/<%= user.getUserPicture() %>?t=<%= System.currentTimeMillis() %>"
                                     class="img-thumbnail rounded-circle mb-3" alt="Profile Picture"/>
                                <% } else { %>
                                <img id="previewImage" src="images/profiles/DefaultUser.jpg"
                                     class="img-thumbnail rounded-circle mb-3" alt="Profile Picture"/>
                                <% } %>
                                <div class="mb-3">
                                    <label class="form-label text-muted">Change Profile Picture</label>
                                    <input type="file" class="form-control" name="userPicture" id="userPictureInput"
                                           form="profileForm" accept="image/*" onchange="previewImage(event)">
                                </div>
                                <div class="mb-3">
                                    <a href="ChangePassword.jsp" class="btn btn-outline-primary w-100">Change Password</a>
                                </div>
                            </div>

                            <!-- Profile Form -->
                            <div class="col-md-8">
                                <form id="profileForm" action="profile" method="post" enctype="multipart/form-data">
                                    <div class="mb-3">
                                        <label for="fullName" class="form-label text-muted">Full Name</label>
                                        <input type="text" class="form-control <%= errors != null && errors.containsKey("fullName") ? "is-invalid" : "" %>" id="fullName" name="fullName"
                                               value="<%= user.getFullName() != null ? user.getFullName() : "" %>" required>
                                        <% if (errors != null && errors.containsKey("fullName")) { %>
                                        <div class="invalid-feedback"><%= errors.get("fullName") %></div>
                                        <% } %>
                                    </div>
                                    <div class="mb-3">
                                        <label for="email" class="form-label text-muted">Email</label>
                                        <input type="email" class="form-control <%= errors != null && errors.containsKey("email") ? "is-invalid" : "" %>" id="email" name="email"
                                               value="<%= user.getEmail() != null ? user.getEmail() : "" %>" required>
                                        <% if (errors != null && errors.containsKey("email")) { %>
                                        <div class="invalid-feedback"><%= errors.get("email") %></div>
                                        <% } %>
                                    </div>
                                    <div class="mb-3">
                                        <label for="phoneNumber" class="form-label text-muted">Phone Number</label>
                                        <input type="text" class="form-control <%= errors != null && errors.containsKey("phoneNumber") ? "is-invalid" : "" %>" id="phoneNumber" name="phoneNumber"
                                               value="<%= user.getPhoneNumber() != null ? user.getPhoneNumber() : "" %>" required>
                                        <% if (errors != null && errors.containsKey("phoneNumber")) { %>
                                        <div class="invalid-feedback"><%= errors.get("phoneNumber") %></div>
                                        <% } %>
                                    </div>
                                    <div class="mb-3">
                                        <label for="dateOfBirth" class="form-label text-muted">Date of Birth</label>
                                        <input type="date" class="form-control <%= errors != null && errors.containsKey("dateOfBirth") ? "is-invalid" : "" %>" id="dateOfBirth" name="dateOfBirth"
                                               value="<%= user.getDateOfBirth() != null ? user.getDateOfBirth() : "" %>">
                                        <% if (errors != null && errors.containsKey("dateOfBirth")) { %>
                                        <div class="invalid-feedback"><%= errors.get("dateOfBirth") %></div>
                                        <% } %>
                                    </div>
                                    <div class="mb-3">
                                        <label for="gender" class="form-label text-muted">Gender</label>
                                        <select class="form-select <%= errors != null && errors.containsKey("gender") ? "is-invalid" : "" %>" id="gender" name="gender">
                                            <option value="">Select</option>
                                            <option value="male" <%= "male".equals(genderStr) ? "selected" : "" %>>Male</option>
                                            <option value="female" <%= "female".equals(genderStr) ? "selected" : "" %>>Female</option>
                                            <option value="other" <%= "other".equals(genderStr) ? "selected" : "" %>>Other</option>
                                        </select>
                                        <% if (errors != null && errors.containsKey("gender")) { %>
                                        <div class="invalid-feedback"><%= errors.get("gender") %></div>
                                        <% } %>
                                    </div>
                                    <div class="mb-3">
                                        <label for="department" class="form-label text-muted">Department</label>
                                        <input type="text" class="form-control" id="department"
                                               value="<%= user.getDepartmentName() != null ? user.getDepartmentName() : "-" %>" disabled>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary btn-lg rounded-1">Update Profile</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
          </div> <!-- End main-content-body -->
        </div> <!-- End main-content-wrapper -->

        <!-- Scripts -->
        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
                crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script>
            function previewImage(event) {
                const input = event.target;
                const preview = document.getElementById('previewImage');
                const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
                const maxSize = 5 * 1024 * 1024; // 5MB

                if (input.files && input.files[0]) {
                    const file = input.files[0];
                    if (!allowedTypes.includes(file.type)) {
                        alert('Only JPEG, PNG, or GIF images are allowed.');
                        input.value = '';
                        preview.src = '<%= user.getUserPicture() != null && !user.getUserPicture().isEmpty() ? "images/profiles/" + user.getUserPicture() + "?t=" + System.currentTimeMillis() : "images/profiles/DefaultUser.jpg" %>';
                        return;
                    }
                    if (file.size > maxSize) {
                        alert('Image file size must not exceed 5MB.');
                        input.value = '';
                        preview.src = '<%= user.getUserPicture() != null && !user.getUserPicture().isEmpty() ? "images/profiles/" + user.getUserPicture() + "?t=" + System.currentTimeMillis() : "images/profiles/DefaultUser.jpg" %>';
                        return;
                    }
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        preview.src = e.target.result;
                    };
                    reader.readAsDataURL(file);
                }
            }

            document.getElementById('fullName').addEventListener('input', function () {
                const fullName = this.value;
                const feedback = this.nextElementSibling;
                if (!fullName) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Full name is required.';
                } else if (fullName.length > 100) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Full name must not exceed 100 characters.';
                } else if (fullName.match(/\d/)) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Full name must not contain numbers.';
                } else {
                    this.classList.remove('is-invalid');
                    feedback.textContent = '';
                }
            });

            document.getElementById('email').addEventListener('input', function () {
                const email = this.value;
                const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
                const feedback = this.nextElementSibling;
                if (!email) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Email is required.';
                } else if (!emailRegex.test(email)) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Invalid email format.';
                } else if (email.length > 100) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Email must not exceed 100 characters.';
                } else {
                    this.classList.remove('is-invalid');
                    feedback.textContent = '';
                }
            });

            document.getElementById('phoneNumber').addEventListener('input', function () {
                const phone = this.value;
                const phoneRegex = /^\+?[0-9]{1,20}$/;
                const feedback = this.nextElementSibling;
                if (!phone) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Phone number is required.';
                } else if (!phoneRegex.test(phone)) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Phone number must be 1-20 digits, optionally starting with "+".';
                } else if (phone.length > 20) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Phone number must not exceed 20 characters.';
                } else {
                    this.classList.remove('is-invalid');
                    feedback.textContent = '';
                }
            });

            document.getElementById('dateOfBirth').addEventListener('input', function () {
                const date = this.value;
                const feedback = this.nextElementSibling;
                if (date) {
                    const inputDate = new Date(date);
                    const today = new Date();
                    if (inputDate > today) {
                        this.classList.add('is-invalid');
                        feedback.textContent = 'Date of birth cannot be in the future.';
                    } else {
                        this.classList.remove('is-invalid');
                        feedback.textContent = '';
                    }
                } else {
                    this.classList.remove('is-invalid');
                    feedback.textContent = '';
                }
            });

            document.getElementById('gender').addEventListener('change', function () {
                const gender = this.value;
                const feedback = this.nextElementSibling;
                if (gender && !['male', 'female', 'other'].includes(gender)) {
                    this.classList.add('is-invalid');
                    feedback.textContent = 'Invalid gender.';
                } else {
                    this.classList.remove('is-invalid');
                    feedback.textContent = '';
                }
            });

            document.getElementById('profileForm').addEventListener('submit', function (event) {
                let valid = true;
                const fullName = document.getElementById('fullName').value;
                const email = document.getElementById('email').value;
                const phone = document.getElementById('phoneNumber').value;
                const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
                const phoneRegex = /^\+?[0-9]{1,20}$/;
                const fullNameFeedback = document.getElementById('fullName').nextElementSibling;
                const emailFeedback = document.getElementById('email').nextElementSibling;
                const phoneFeedback = document.getElementById('phoneNumber').nextElementSibling;

                if (!fullName) {
                    valid = false;
                    document.getElementById('fullName').classList.add('is-invalid');
                    fullNameFeedback.textContent = 'Full name is required.';
                } else if (fullName.length > 100) {
                    valid = false;
                    document.getElementById('fullName').classList.add('is-invalid');
                    fullNameFeedback.textContent = 'Full name must not exceed 100 characters.';
                } else if (fullName.match(/\d/)) {
                    valid = false;
                    document.getElementById('fullName').classList.add('is-invalid');
                    fullNameFeedback.textContent = 'Full name must not contain numbers.';
                }

                if (!email) {
                    valid = false;
                    document.getElementById('email').classList.add('is-invalid');
                    emailFeedback.textContent = 'Email is required.';
                } else if (!emailRegex.test(email)) {
                    valid = false;
                    document.getElementById('email').classList.add('is-invalid');
                    emailFeedback.textContent = 'Invalid email format.';
                } else if (email.length > 100) {
                    valid = false;
                    document.getElementById('email').classList.add('is-invalid');
                    emailFeedback.textContent = 'Email must not exceed 100 characters.';
                }

                if (!phone) {
                    valid = false;
                    document.getElementById('phoneNumber').classList.add('is-invalid');
                    phoneFeedback.textContent = 'Phone number is required.';
                } else if (!phoneRegex.test(phone)) {
                    valid = false;
                    document.getElementById('phoneNumber').classList.add('is-invalid');
                    phoneFeedback.textContent = 'Phone number must be 1-20 digits.';
                }

                if (!valid) {
                    event.preventDefault();
                }
            });
        </script>
    </body>
</html>