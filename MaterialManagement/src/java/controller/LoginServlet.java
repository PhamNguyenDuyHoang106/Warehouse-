package controller;

import dal.UserDAO;
import dal.PermissionDAO;
import dal.SessionDAO;
import entity.User;
import entity.Permission;
import entity.Session;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = null;
        PermissionDAO permissionDAO = null;
        SessionDAO sessionDAO = null;
        
        try {
            userDAO = new UserDAO();
            permissionDAO = new PermissionDAO();
            User user = userDAO.login(username, password); 

        if (user != null) {
            if (user.getStatus() != null && user.getStatus() == User.Status.inactive) {
                request.setAttribute("error", "Tài khoản của bạn đang ở trạng thái inactive. Vui lòng liên hệ quản trị viên.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else if (user.getStatus() != null && user.getStatus() == User.Status.locked) {
                request.setAttribute("error", "Tài khoản đã bị khóa do nhập sai mật khẩu quá nhiều lần. Vui lòng liên hệ quản trị viên để mở khóa.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                // Clear old session and create new one
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                List<Permission> permissions = permissionDAO.getPermissionsByRole(user.getRoleId());
                List<String> permissionNames = permissions.stream()
                    .map(Permission::getPermissionName)
                    .collect(Collectors.toList());
                session.setAttribute("userPermissions", permissionNames);
                
                // Create session in database
                sessionDAO = new SessionDAO();
                String sessionId = session.getId();
                String token = generateToken();
                String tokenHash = hashToken(token);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiresAt = now.plusHours(24); // 24 hours session
                
                Session dbSession = new Session();
                dbSession.setSessionId(sessionId);
                dbSession.setUserId(user.getUserId());
                dbSession.setToken(tokenHash);
                dbSession.setExpiresAt(expiresAt);
                dbSession.setCreatedAt(now);
                dbSession.setIpAddress(getClientIpAddress(request));
                dbSession.setUserAgent(request.getHeader("User-Agent"));
                dbSession.setLastActivity(now);
                
                sessionDAO.create(dbSession);
                session.setAttribute("sessionToken", token); // Store plain token in session for reference
                
                String redirectURL = (String) session.getAttribute("redirectURL");
                session.removeAttribute("redirectURL");
                
                if (redirectURL != null && !redirectURL.isEmpty() && 
                    !redirectURL.contains(".css") && !redirectURL.contains(".js") && 
                    !redirectURL.contains(".jpg") && !redirectURL.contains(".png")) {
                    response.sendRedirect(response.encodeRedirectURL(redirectURL));
                } else {
                    response.sendRedirect(response.encodeRedirectURL("home"));
                }
            }
        } else {
            request.setAttribute("error", "Invalid username or password!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
        } finally {
            if (userDAO != null) {
                try {
                    userDAO.close();
                } catch (Exception e) {
                    // Log but don't throw
                }
            }
            if (permissionDAO != null) {
                try {
                    permissionDAO.close();
                } catch (Exception e) {
                    // Log but don't throw
                }
            }
            if (sessionDAO != null) {
                try {
                    sessionDAO.close();
                } catch (Exception e) {
                    // Log but don't throw
                }
            }
        }
    }
    
    /**
     * Generate secure random token
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") + 
               UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Hash token using SHA-256
     */
    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(token.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return token; // Fallback to plain token if hashing fails
        }
    }
    
    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }
}