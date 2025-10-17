

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
    <meta charset="UTF-8"/>
    <title>Change Password</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <style>
        section.py-5 { background: url('images/background-img.png') no-repeat center center fixed; background-size: contain; }
        .change-password-section .form-control { height: 48px; font-size: 16px; }
        .change-password-section .form-label { font-size: 14px; margin-bottom: 4px; }
        .change-password-section .btn { font-size: 16px; padding: 12px 24px; }
    </style>
    </head>
    <body>
<jsp:include page="Header.jsp" />
<section class="py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 p-4 bg-white rounded shadow change-password-section">
                <jsp:include page="Navigation.jsp" />
                <h2 class="text-center display-5 mb-4">Change Password</h2>
                <c:if test="${not empty message}">
                    <div class="alert alert-success text-center" role="alert">
                        ${message}
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger text-center" role="alert">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${empty message}">
                <form action="ChangePassword" method="post" autocomplete="off">
                    <div class="mb-3">
                        <label for="oldPassword" class="form-label text-muted">Current Password</label>
                        <input type="password" class="form-control" id="oldPassword" name="oldPassword" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label for="newPassword" class="form-label text-muted">New Password</label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="3">
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label text-muted">Confirm New Password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required minlength="3">
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg rounded-1">Change Password</button>
                    </div>
                </form>
                </c:if>
            </div>
        </div>
    </div>
</section>
<script src="js/jquery-1.11.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script src="js/plugins.js"></script>
<script src="js/script.js"></script>
    </body>
</html>
