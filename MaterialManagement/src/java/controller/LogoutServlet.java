package controller;

import dal.SessionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionDAO sessionDAO = null;
        
        try {
            if (session != null) {
                String sessionId = session.getId();
                
                // Delete session from database
                sessionDAO = new SessionDAO();
                sessionDAO.deactivate(sessionId);
                
                // Invalidate HTTP session
                session.invalidate();
            }
        } catch (Exception e) {
            // Log error but continue with logout
        } finally {
            if (sessionDAO != null) {
                try {
                    sessionDAO.close();
                } catch (Exception e) {
                    // Log but don't throw
                }
            }
        }
        
        response.sendRedirect("Login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý đăng xuất người dùng";
    }
}
