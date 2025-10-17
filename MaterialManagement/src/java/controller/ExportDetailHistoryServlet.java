package controller;

import dal.ExportDAO;
import entity.Export;
import entity.ExportDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="ExportDetailHistoryServlet", urlPatterns={"/ExportDetail"})
public class ExportDetailHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String exportIdStr = request.getParameter("exportId");
        if (exportIdStr == null || exportIdStr.trim().isEmpty()) {
            response.sendRedirect("ExportHistory");
            return;
        }
        try {
            int exportId = Integer.parseInt(exportIdStr);
            ExportDAO exportDAO = new ExportDAO();
            Export exportData = exportDAO.getExportById(exportId);
            List<ExportDetail> exportDetails = exportDAO.getExportDetailsByExportId(exportId);

            // Log để kiểm tra
            System.out.println("Export ID: " + exportId);
            for (ExportDetail detail : exportDetails) {
                System.out.println("Material: " + detail.getMaterialName() + ", URL: " + detail.getMaterialsUrl());
            }

            request.setAttribute("exportData", exportData);
            request.setAttribute("exportDetails", exportDetails != null ? exportDetails : new ArrayList<>());
            request.getRequestDispatcher("ExportDetail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("ExportHistory");
        } catch (SQLException e) {
            Logger.getLogger(ExportDetailHistoryServlet.class.getName()).log(Level.SEVERE, null, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported");
    }

    @Override
    public String getServletInfo() {
        return "Servlet to display export details";
    }
}