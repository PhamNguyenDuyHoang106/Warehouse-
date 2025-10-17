package controller;

import dal.MaterialDAO;
import entity.Material;
import entity.MaterialDetails;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZoneId;
import java.util.Date;
import java.io.PrintWriter;

@WebServlet(name = "ViewMaterialServlet", urlPatterns = {"/viewmaterial"})
public class ViewMaterialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String materialId = request.getParameter("materialId");
            
            if (materialId == null || materialId.isEmpty()) {
                response.sendRedirect("dashboardmaterial");
                return;
            }
            
            MaterialDAO md = new MaterialDAO();
            int id = Integer.parseInt(materialId);
            
            Material m = md.getInformation(id);

            if (m == null) {
                return;
            }

            // Không cần chuyển đổi sang java.util.Date nữa.

            request.setAttribute("m", m);
            request.getRequestDispatcher("ViewMaterial.jsp").forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

}
