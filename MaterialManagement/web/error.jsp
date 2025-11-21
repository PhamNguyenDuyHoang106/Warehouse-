<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Access Denied</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap & Fonts -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        .error-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: url('images/background-img.png') no-repeat;
            background-size: cover;
        }
        .error-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            padding: 3rem;
            text-align: center;
            max-width: 500px;
        }
        .error-icon {
            font-size: 4rem;
            color: #dc3545;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <div class="error-icon">⚠️</div>
            <h2 class="text-danger mb-3">Access Denied</h2>
            
            <c:if test="${not empty error}">
                <p class="text-muted mb-4">${error}</p>
            </c:if>
            
            <c:if test="${empty error}">
                <p class="text-muted mb-4">You do not have permission to access this page.</p>
            </c:if>
            
            <div class="d-grid gap-2">
                <a href="http://localhost:8080/MaterialManagement/home" class="btn btn-primary">Go to Homepage</a>
                <a href="javascript:history.back()" class="btn btn-outline-secondary">Go Back</a>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 