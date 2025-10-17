<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="entity.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Detail - Computer Accessories</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap & Custom CSS -->
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
        .profile-img {
            max-width: 200px;
            border: 3px solid #007bff;
        }
        .detail-row {
            margin-bottom: 1rem;
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

            <!-- Content -->
            <div class="col-md-9 col-lg-10 px-md-4 py-4">
                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:if test="${user != null}">
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <img src="images/profiles/${empty user.userPicture ? 'default-avatar.png' : user.userPicture}" 
                                 class="img-fluid rounded-circle mb-3 profile-img" 
                                 alt="Profile Picture">
                        </div>
                        <div class="col-md-8">
                            <h3 class="text-primary border-bottom pb-2">👤 User Detail Information</h3>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Username:</div>
                                <div class="col-sm-8">${user.username}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Full Name:</div>
                                <div class="col-sm-8">${user.fullName}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Email:</div>
                                <div class="col-sm-8">${user.email}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Phone Number:</div>
                                <div class="col-sm-8">${user.phoneNumber != null ? user.phoneNumber : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Address:</div>
                                <div class="col-sm-8">${user.address != null ? user.address : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Gender:</div>
                                <div class="col-sm-8">${user.gender != null ? user.gender : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Date of Birth:</div>
                                <div class="col-sm-8">${user.dateOfBirth != null ? user.dateOfBirth : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Role:</div>
                                <div class="col-sm-8">${user.roleName}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Department:</div>
                                <div class="col-sm-8">${user.departmentName != null ? user.departmentName : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Status:</div>
                                <div class="col-sm-8">${user.status}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Created At:</div>
                                <div class="col-sm-8">${user.createdAt != null ? user.createdAt : '-'}</div>
                            </div>
                            <div class="row detail-row">
                                <div class="col-sm-4 fw-bold">Last Updated:</div>
                                <div class="col-sm-8">${user.updatedAt != null ? user.updatedAt : '-'}</div>
                            </div>
                            <a href="UserList" class="btn btn-secondary">← Back to User List</a>
                        </div>
                    </div>
                </c:if>
                <c:if test="${user == null && empty error}">
                    <div class="alert alert-danger">User not found.</div>
                    <a href="UserList" class="btn btn-secondary">← Back to User List</a>
                </c:if>
            </div>
        </div>
    </div>

    <jsp:include page="Footer.jsp" />

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
</body>
</html>