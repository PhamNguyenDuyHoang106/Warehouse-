package controller;

import dal.PasswordResetRequestsDAO;
import dal.UserDAO;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.PrintWriter;

@WebServlet(name="ForgotPasswordServlet", urlPatterns={"/ForgotPassword"})
public class ForgotPasswordServlet extends HttpServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ForgotPasswordSevlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgotPasswordSevlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setAttribute("step", "email");
        request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String email = request.getParameter("email");
        UserDAO userDAO = new UserDAO();
        if (!userDAO.isEmailExists(email)) {
            request.setAttribute("error", "This email does not exist or has not been verified!");
            request.setAttribute("step", "email");
            request.setAttribute("enteredEmail", email);
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }
        PasswordResetRequestsDAO dao = new PasswordResetRequestsDAO();
        dao.insertRequest(email);
        try {
            utils.EmailUtils.sendAdminNotificationForResetRequest(email);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        request.setAttribute("message", "Your password reset request has been sent to the admin. Please check your email to receive a new password.");
        request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
