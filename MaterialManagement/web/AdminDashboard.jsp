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

          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->


  <!-- Scripts -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    function autoSubmit(form) {
      form.submit();
    }
  </script>
</body>
</html>
