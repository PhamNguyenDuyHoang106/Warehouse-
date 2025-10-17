<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Admin Dashboard - Computer Accessories</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- Bootstrap & Custom CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" type="text/css" href="css/vendor.css">
  <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>

  <!-- Header -->
  <header>
    <div class="container py-2">
      <div class="row py-4 pb-0 pb-sm-4 align-items-center">
        <div class="col-sm-4 col-lg-3 text-center text-sm-start">
          <a href="HomePage.jsp">
            <img src="images/logo.png" alt="logo" class="img-fluid" width="300px">
          </a>
        </div>
        <div class="col-sm-8 col-lg-9 d-flex justify-content-end align-items-center">
          <div class="text-end d-none d-xl-block">
            <span class="fs-6 text-muted">Admin</span>
            <h5 class="mb-0">admin@accessories.com</h5>
          </div>
          <a href="logout" class="btn btn-outline-dark btn-lg ms-4">
            Logout
          </a>
        </div>
      </div>
    </div>
  </header>

  <!-- Main content -->
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-3 col-lg-2 bg-light p-0">
        <jsp:include page="Sidebar.jsp" />
      </div>

      
      
    </div> <!-- end row -->
  </div> <!-- end container-fluid -->

  <!-- Footer -->
  <footer class="footer py-4 bg-light mt-auto">
    <div class="container text-center">
      <span class="text-muted">&copy; 2025 Computer Accessories - All Rights Reserved.</span>
    </div>
  </footer>

  <!-- Scripts -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    function autoSubmit(form) {
      form.submit();
    }
  </script>
</body>
</html>
