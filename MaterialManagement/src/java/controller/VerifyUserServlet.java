package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "VerifyUserServlet", value = "/VerifyUser")
public class VerifyUserServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.isEmpty()) {
            request.setAttribute("error", "Token không hợp lệ.");
            request.getRequestDispatcher("/Error.jsp").forward(request, response);
            return;
        }

        boolean verified = userDAO.verifyUser(token);
        if (verified) {
            request.setAttribute("successMessage", "Tài khoản đã được xác thực thành công!");
            // Cập nhật trạng thái người dùng thành active nếu cần
            // Ví dụ: userDAO.updateStatus(userId, User.Status.active);
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/Error.jsp").forward(request, response);
        }
    }
}