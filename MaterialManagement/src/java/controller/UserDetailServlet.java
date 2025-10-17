package controller;

import dal.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dal.RolePermissionDAO;

import java.io.IOException;

@WebServlet(name = "UserDetailServlet", urlPatterns = {"/UserDetail"})
public class UserDetailServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null || !rolePermissionDAO.hasPermission(currentUser.getRoleId(), "VIEW_DETAIL_USER")) {
            request.setAttribute("error", "You do not have permission to view user details.");
            request.getRequestDispatcher("UserList").forward(request, response);
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Missing or invalid userId.");
            request.getRequestDispatcher("UserList").forward(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);

            if (user != null) {
                request.setAttribute("user", user);
                request.getRequestDispatcher("UserDetail.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "User not found with ID: " + userId);
                request.getRequestDispatcher("UserList.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid user ID: " + userIdStr);
            request.getRequestDispatcher("UserList.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error retrieving details: " + e.getMessage());
            e.printStackTrace();
            request.getRequestDispatcher("UserList.jsp").forward(request, response);
        }
    }
}