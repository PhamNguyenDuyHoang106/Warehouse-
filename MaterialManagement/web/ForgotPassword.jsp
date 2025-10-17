
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html lang="en">
<head>
  <title>Waggy - Forgot Password</title>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="format-detection" content="telephone=no">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="author" content="">
  <meta name="keywords" content="">
  <meta name="description" content="Reset your Waggy account password.">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
    crossorigin="anonymous">
  <link rel="stylesheet" type="text/css" href="css/vendor.css">
  <link rel="stylesheet" type="text/css" href="style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
    rel="stylesheet">
</head>
<body>
  <section id="forgot-password" style="background: url('images/background-img.png') no-repeat;">
    <div class="container">
      <div class="row my-5 py-5">
        <div class="offset-md-3 col-md-6 my-5">
          <h2 class="display-3 fw-normal text-center mb-4">Forgot <span class="text-primary">Password</span> <span class="fw-light">for</span> <span class="text-primary">Waggy</span></h2>
          <div class="card p-4 shadow border-0">
            <div class="card-body">
              <% String step = (String) request.getAttribute("step");
                 String message = (String) request.getAttribute("message");
                 String error = (String) request.getAttribute("error"); %>
              <% if (step == null || step.equals("email")) { %>
                <% if (error != null) { %>
                  <div class="alert alert-danger text-center"> <%= error %> </div>
                <% } %>
                <% if (message != null) { %>
                  <div class="alert alert-info text-center"> <%= message %> </div>
                <% } %>
                <form method="post" action="ForgotPassword">
                  <div class="mb-3">
                    <input type="email" class="form-control form-control-lg" id="email" name="email" placeholder="Enter your email" required>
                  </div>
                  <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-dark btn-lg rounded-1">Continue</button>
                  </div>
                </form>
                <div class="text-center mt-3">
                  <a href="Login.jsp" class="btn btn-primary">Back to Login</a>
                </div>
              <% } else if (step.equals("done")) { %>
                <div class="alert alert-success text-center">
                  <%= message != null ? message : "Your request has been sent to the admin. Please wait for the admin to reset your password." %>
                </div>
                <div class="text-center mt-3">
                  <a href="Login.jsp" class="btn btn-primary">Back to Login</a>
                </div>
              <% } %>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <script src="js/jquery-1.11.0.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
  <script src="js/plugins.js"></script>
  <script src="js/script.js"></script>
  <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
</body>
</html>
