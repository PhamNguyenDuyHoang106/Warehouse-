<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="en">
<head>
  <title>Material Management - Login</title>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="format-detection" content="telephone=no">
  <meta name="mobile-web-app-capable" content="yes">
  <meta name="author" content="">
  <meta name="keywords" content="">
  <meta name="description" content="Login to Material Management System to access inventory and material management features.">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
    integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
  <link rel="stylesheet" type="text/css" href="css/vendor.css">
  <link rel="stylesheet" type="text/css" href="style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
    rel="stylesheet">
</head>
<body>
  <section id="login" style="background: url('images/background-img.png') no-repeat;">
    <div class="container">
      <div class="row my-5 py-5">
        <div class="offset-md-3 col-md-6 my-5">
          <h2 class="display-3 fw-normal text-center">Login to <span class="text-primary">Material Management</span></h2>
          <form action="LoginServlet" method="post">
            <div class="mb-3">
              <input type="text" class="form-control form-control-lg" name="username" id="username"
                placeholder="Enter Your Username" required>
            </div>
            <div class="mb-3">
              <input type="password" class="form-control form-control-lg" name="password" id="password"
                placeholder="Enter Your Password" autocomplete="current-password" required>
            </div>
            <div class="mb-3 d-flex justify-content-between align-items-center">
              <a href="ForgotPassword.jsp" class="text-primary text-decoration-none">Forgot Password?</a>
            </div>
            <p style="color:red;">
              ${error}
            </p>
            <div class="d-grid gap-2">
              <button type="submit" class="btn btn-dark btn-lg rounded-1">Login Now</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </section>

  <script src="js/jquery-1.11.0.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe"
    crossorigin="anonymous"></script>
  <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</body>
</html>