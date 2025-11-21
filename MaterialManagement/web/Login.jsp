<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
  <title>Material Management - Login</title>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="format-detection" content="telephone=no">
  <meta name="mobile-web-app-capable" content="yes">
  <meta name="description" content="Login to Material Management System to access inventory and material management features.">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
    
    html, body {
      height: 100%;
      overflow: hidden;
    }
    
    body {
      font-family: 'Inter', sans-serif;
      background: transparent;
      margin: 0;
      padding: 0;
      width: 100%;
      height: 100vh;
      overflow: hidden;
    }
    
    /* PHẦN BO NGOÀI - Có thể thay đổi màu nền ở đây - Tràn toàn màn hình */
    .login-outer-container {
      background: #f3e5f5; /* ⬅️ THAY ĐỔI MÀU NỀN BO NGOÀI Ở ĐÂY */
      width: 100%;
      height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0;
      margin: 0;
    }
    
    
    .login-main-container {
      background: #f3e5f5;
      border-radius: 24px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
      width: 100%;
      max-width: 2000px;
      height: 90vh;
      max-height: 1000px;
      overflow: hidden;
      animation: fadeIn 0.6s ease-out;
    }
    
    .login-wrapper {
      width: 100%;
      height: 100%;
      display: grid;
      grid-template-columns: 60% 40%;
    }
    
    @keyframes fadeIn {
      from {
        opacity: 0;
      }
      to {
        opacity: 1;
      }
    }
    
    /* PHẦN 1: Ảnh - Có thể thay đổi ảnh trong HTML */
    .login-left {
      background: #f8e6d4;
      padding: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      position: relative;
      overflow: hidden;
    }
    
    .login-left::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -20%;
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, rgba(139, 69, 19, 0.1) 0%, transparent 70%);
      border-radius: 50%;
    }
    
    .login-left::after {
      content: '';
      position: absolute;
      bottom: -30%;
      left: -10%;
      width: 300px;
      height: 300px;
      background: radial-gradient(circle, rgba(128, 0, 128, 0.1) 0%, transparent 70%);
      border-radius: 50%;
    }
    
    .illustration-container {
      width: 100%;
      height: 100%;
      position: absolute;
      top: 0;
      left: 0;
      z-index: 0;
    }
    
    .illustration-container img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      object-position: center;
    }
    
    /* Fallback illustration if image not found */
    .illustration-fallback {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, rgba(139, 69, 19, 0.1) 0%, rgba(128, 0, 128, 0.1) 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      position: absolute;
      top: 0;
      left: 0;
      z-index: 0;
    }
    
    .illustration-fallback i {
      font-size: 200px;
      color: rgba(139, 69, 19, 0.3);
    }
    
    .login-left-content {
      display: none;
    }
    
    /* RIGHT SIDE - Login Form */
    .login-right {
      padding: 100px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      width: 100%;
      max-width: 100%;
    }
    
    /* Form Container - Phần 2: Form Login (Màu trắng cố định) */
    .login-form-container {
      background: #ffffff; /* Màu trắng cố định - KHÔNG THAY ĐỔI */
      border-radius: 20px;
      padding: 60px 80px;
      width: 100%;
      max-width: 550px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    }
    
    .login-form-container .logo-container {
      display: flex;
      justify-content: center;
      margin-bottom: 50px;
    }
    
    .logo-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      background: #6b2c91;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 28px;
      font-weight: bold;
    }
    
    .login-form-container h2 {
      font-size: 36px;
      font-weight: 700;
      color: #1f2937;
      margin-bottom: 12px;
    }
    
    .login-form-container .subtitle {
      font-size: 16px;
      color: #6b7280;
      margin-bottom: 40px;
    }
    
    .form-input {
      width: 100%;
      border: 2px solid #e5e7eb;
      border-radius: 12px;
      padding: 20px 24px;
      margin-bottom: 28px;
      font-size: 18px;
      transition: all 0.3s ease;
      background: white;
    }
    
    .form-input:focus {
      outline: none;
      border-color: #9333ea;
      box-shadow: 0 0 0 3px rgba(147, 51, 234, 0.1);
    }
    
    .form-input::placeholder {
      color: #9ca3af;
    }
    
    .form-options {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 17px;
      margin-bottom: 40px;
    }
    
    .remember-me {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #4b5563;
    }
    
    .remember-me input[type="checkbox"] {
      width: 20px;
      height: 20px;
      cursor: pointer;
      accent-color: #9333ea;
    }
    
    .forgot-password {
      color: #9333ea;
      text-decoration: none;
      font-weight: 500;
      font-size: 17px;
      transition: color 0.3s ease;
    }
    
    .forgot-password:hover {
      color: #7c3aed;
      text-decoration: underline;
    }
    
    .login-btn {
      width: 100%;
      background: #6b2c91;
      color: white;
      border: none;
      border-radius: 12px;
      padding: 22px;
      font-size: 20px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .login-btn:hover {
      background: #5a2477;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(107, 44, 145, 0.3);
    }
    
    .login-btn:active {
      transform: translateY(0);
    }
    
    .error-message {
      background: #fee2e2;
      border: 2px solid #fecaca;
      border-radius: 12px;
      padding: 16px;
      color: #991b1b;
      font-size: 16px;
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      gap: 10px;
      animation: shake 0.5s ease-in-out;
    }
    
    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-10px); }
      75% { transform: translateX(10px); }
    }
    
    .error-message i {
      font-size: 20px;
    }
    
    /* Responsive */
    @media (max-width: 768px) {
      .login-outer-container {
        padding: 0;
      }
      
      .login-main-container {
        height: 100vh;
        max-height: none;
        border-radius: 0;
      }
      
      .login-wrapper {
        grid-template-columns: 1fr;
      }
      
      .login-left {
        display: none;
      }
      
      .login-right {
        padding: 60px 40px;
        max-width: 100%;
      }
      
      .login-form-container {
        padding: 50px 60px;
        max-width: 100%;
      }
      
      .login-form-container h2 {
        font-size: 32px;
      }
      
      .login-form-container .subtitle {
        font-size: 16px;
      }
    }
    
    @media (max-width: 576px) {
      .login-outer-container {
        padding: 0;
      }
      
      .login-main-container {
        border-radius: 0;
        height: 100vh;
      }
      
      .login-right {
        padding: 40px 30px;
      }
      
      .login-form-container {
        padding: 40px 30px;
        border-radius: 16px;
      }
      
      .login-form-container h2 {
        font-size: 28px;
      }
      
      .login-form-container .subtitle {
        font-size: 14px;
      }
      
      .form-input {
        padding: 16px 20px;
        font-size: 16px;
      }
      
      .login-btn {
        padding: 18px;
        font-size: 18px;
      }
    }
  </style>
</head>
<body>
  <div class="login-outer-container">
    <div class="login-main-container">
    <div class="login-wrapper">
    <!-- PHẦN 1: Ảnh - Thay đổi ảnh bằng cách sửa đường dẫn src bên dưới -->
    <div class="login-left">
      <div class="illustration-container">
        <img src="images/download.jpg" alt="Illustration" onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';"> <!-- ⬅️ THAY ĐỔI ẢNH Ở ĐÂY: images/download.jpg -->
        <div class="illustration-fallback" style="display: none;">
          <i class="fas fa-laptop-code"></i>
        </div>
      </div>
      <div class="login-left-content">
        <h2>Turn your ideas into reality.</h2>
        <p>Start for free and get attractive offers from the community</p>
      </div>
    </div>

    <!-- RIGHT SIDE - Login Form -->
    <div class="login-right">
      <div class="login-form-container">
        <div class="logo-container">
          <div class="logo-icon">⋮</div>
        </div>

        <h2>Login to your Account</h2>
        <p class="subtitle">See what is going on with your business</p>

        <form action="LoginServlet" method="post" id="loginForm">
        <input
          type="text"
          name="username"
          id="username"
          placeholder="Enter your username"
          class="form-input"
          required
          autocomplete="username"
        />

        <input
          type="password"
          name="password"
          id="password"
          placeholder="••••••••"
          class="form-input"
          required
          autocomplete="current-password"
        />

        <c:if test="${not empty error}">
          <div class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            <span>${error}</span>
          </div>
        </c:if>

        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" name="rememberMe" value="true">
            <span>Remember Me</span>
          </label>
          <a href="#" class="forgot-password" onclick="event.preventDefault(); alert('Forgot password feature coming soon!');">
            Forgot Password?
          </a>
        </div>

        <button type="submit" class="login-btn">
          Login
        </button>
      </form>
      </div>
    </div>
    </div>
  </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    // Form validation
    document.getElementById('loginForm').addEventListener('submit', function(e) {
      const username = document.getElementById('username').value.trim();
      const password = document.getElementById('password').value;
      
      if (!username || !password) {
        e.preventDefault();
        alert('Please fill in all fields');
        return false;
      }
    });
    
    // Add focus effects
    document.querySelectorAll('.form-input').forEach(input => {
      input.addEventListener('focus', function() {
        this.style.borderColor = '#9333ea';
      });
      
      input.addEventListener('blur', function() {
        if (!this.value) {
          this.style.borderColor = '#e5e7eb';
        }
      });
    });
  </script>
</body>
</html>
