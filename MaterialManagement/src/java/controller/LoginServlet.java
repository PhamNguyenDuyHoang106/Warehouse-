package controller;

import dal.UserDAO;
import dal.PermissionDAO;
import entity.User;
import entity.Permission;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
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
        
        try {
            userDAO = new UserDAO();
            permissionDAO = new PermissionDAO();
            User user = userDAO.login(username, password); 

        if (user != null) {
            if (user.getStatus() != null && user.getStatus().toString().equalsIgnoreCase("inactive")) {
                request.setAttribute("error", "Your account has been disabled!");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            } else {
                // Clear old session and create new one
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                
                // Debug: Print all session attributes
                System.out.println("DEBUG - Session attributes:");
                java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String name = attributeNames.nextElement();
                    Object value = session.getAttribute(name);
                    System.out.println("DEBUG - " + name + ": " + value);
                }
                
                List<Permission> permissions = permissionDAO.getPermissionsByRole(user.getRoleId());
                List<String> permissionNames = permissions.stream()
                    .map(Permission::getPermissionName)
                    .collect(Collectors.toList());
                session.setAttribute("userPermissions", permissionNames);
                
                String redirectURL = (String) session.getAttribute("redirectURL");
                System.out.println("DEBUG - Found redirectURL: " + redirectURL);
                
                // Clear redirectURL to prevent unwanted redirects
                session.removeAttribute("redirectURL");
                
                // Only redirect if it's a valid servlet URL, not a static file
                if (redirectURL != null && !redirectURL.isEmpty() && 
                    !redirectURL.contains(".css") && !redirectURL.contains(".js") && 
                    !redirectURL.contains(".jpg") && !redirectURL.contains(".png")) {
                    System.out.println("DEBUG - Redirecting to: " + redirectURL);
                    response.sendRedirect(response.encodeRedirectURL(redirectURL));
                } else {
                    System.out.println("DEBUG - Redirecting to: home");
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
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }
}