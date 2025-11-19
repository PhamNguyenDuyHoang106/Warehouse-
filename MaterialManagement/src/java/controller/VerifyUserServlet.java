package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "VerifyUserServlet", value = "/VerifyUser")
public class VerifyUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("successMessage", "Tính năng xác thực email không còn cần thiết. Vui lòng đăng nhập bằng thông tin được cung cấp.");
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
    }
}