package controller;

import dal.PasswordResetRequestsDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import entity.PasswordResetRequests;
import entity.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.PrintWriter;
import java.util.Random;
import utils.EmailUtils;

@WebServlet(name="PasswordResetRequestsServlet", urlPatterns={"/PasswordResetRequests"})
public class PasswordResetRequestsServlet extends HttpServlet {
    private RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PasswordResetRequestsServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PasswordResetRequestsServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        if (currentUser.getRoleId() != 1) {
            request.setAttribute("error", "You do not have permission to view password reset requests.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        PasswordResetRequestsDAO dao = new PasswordResetRequestsDAO();
        String searchEmail = request.getParameter("searchEmail");
        int page = 1;
        int itemsPerPage = 5;
        String status = request.getParameter("status");
        if (status == null || status.isEmpty()) status = "all";
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            }
            String itemsPerPageStr = request.getParameter("itemsPerPage");
            if (itemsPerPageStr != null && !itemsPerPageStr.isEmpty()) {
                itemsPerPage = Integer.parseInt(itemsPerPageStr);
                if (itemsPerPage < 1) itemsPerPage = 5;
                if (itemsPerPage > 100) itemsPerPage = 100;
            }
        } catch (NumberFormatException e) {
        }
        List<PasswordResetRequests> requests;
        int totalItems;
        if (searchEmail != null && !searchEmail.trim().isEmpty()) {
            requests = dao.getRequestsByFilterPaging(searchEmail.trim(), status, page, itemsPerPage);
            totalItems = dao.countRequestsByFilter(searchEmail.trim(), status);
        } else {
            requests = dao.getRequestsByFilterPaging("", status, page, itemsPerPage);
            totalItems = dao.countRequestsByFilter("", status);
        }
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        request.setAttribute("requests", requests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("status", status);
        request.getRequestDispatcher("PasswordResetRequests.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String status = request.getParameter("status");
        String newPassword = request.getParameter("newPassword");
        int adminId = admin.getUserId();

        PasswordResetRequestsDAO dao = new PasswordResetRequestsDAO();
        UserDAO userDAO = new UserDAO();
        String userEmail = null;
        for (PasswordResetRequests req : dao.getAllRequests()) {
            if (req.getRequestId() == requestId) {
                userEmail = req.getUserEmail();
                break;
            }
        }
        if ("completed".equals(status) && userEmail != null) {
            String randomPassword = generateRandomPassword(8);
            userDAO.updatePasswordByEmail(userEmail, UserDAO.md5(randomPassword));
            String subject = "Your Password Has Been Reset";
            String content = "Your new password is: <b>" + randomPassword + "</b><br>Please log in and change your password immediately.";
            try {
                EmailUtils.sendEmail(userEmail, subject, content);
            } catch (jakarta.mail.MessagingException e) {
                e.printStackTrace(); 
            }
            newPassword = randomPassword;
            request.setAttribute("message", "Password has been reset and emailed to the user.");
            request.setAttribute("newPassword", randomPassword);
        } else if ("rejected".equals(status)) {
            request.setAttribute("message", "Password reset request has been rejected.");
        }
        dao.updateStatus(requestId, status, newPassword, adminId);
        String statusFilter = request.getParameter("statusFilter");
        String searchEmail = request.getParameter("searchEmail");
        String page = request.getParameter("page");
        StringBuilder redirectUrl = new StringBuilder("PasswordResetRequests?");
        if (statusFilter != null && !statusFilter.isEmpty()) {
            redirectUrl.append("status=").append(statusFilter).append("&");
        }
        if (searchEmail != null && !searchEmail.isEmpty()) {
            redirectUrl.append("searchEmail=").append(searchEmail).append("&");
        }
        if (page != null && !page.isEmpty()) {
            redirectUrl.append("page=").append(page).append("&");
        }
        response.sendRedirect(redirectUrl.toString());
    }
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
