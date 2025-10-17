package controller;

import dal.ImportDAO;
import entity.Import;
import entity.ImportDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ImportDetailHistoryServlet", urlPatterns = {"/ImportDetail"})
public class ImportDetailHistoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (java.io.PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ImportDetailHistoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ImportDetailHistoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy tham số importId
        String importIdStr = request.getParameter("importId");
        if (importIdStr == null) {
            response.sendRedirect("ImportHistory");
            return;
        }
        int importId;
        try {
            importId = Integer.parseInt(importIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("ImportHistory");
            return;
        }

        // 2. Khởi tạo DAO
        ImportDAO importDAO = new ImportDAO();

        // 3. Lấy dữ liệu chi tiết
        Import importData = importDAO.getImportById(importId);
        List<ImportDetail> importDetails = null;
        try {
            importDetails = importDAO.getImportDetailsByImportId(importId);
        } catch (SQLException ex) {
            Logger.getLogger(ImportDetailHistoryServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 4. Đẩy dữ liệu lên request
        request.setAttribute("importData", importData);
        request.setAttribute("importDetails", importDetails);

        // 5. Forward về JSP
        request.getRequestDispatcher("ImportDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
