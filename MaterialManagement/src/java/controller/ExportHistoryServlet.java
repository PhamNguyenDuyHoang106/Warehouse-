package controller;

import dal.ExportDAO;
import dal.MaterialDAO;
import dal.UserDAO;
import entity.Export;
import entity.Material;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Admin
 */
@WebServlet(name = "ExportHistoryServlet", urlPatterns = {"/ExportHistory"})
public class ExportHistoryServlet extends HttpServlet {
    private static final int PAGE_SIZE = 10;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ExportHistoryServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ExportHistoryServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 1. Lấy filter và sort parameters
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String materialName = request.getParameter("materialName");
        String sortByRecipient = request.getParameter("sortByRecipient");
        String sortByExportedBy = request.getParameter("sortByExportedBy");
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) page = 1;
        } catch (Exception e) { page = 1; }

        // 2. Khởi tạo DAO
        ExportDAO exportDAO = new ExportDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        UserDAO userDAO = new UserDAO();

        // 3. Lấy danh sách phiếu xuất, filter nâng cao, phân trang
        List<Export> exportList = exportDAO.getExportHistoryAdvanced(fromDate, toDate, materialName, sortByRecipient, sortByExportedBy, page, PAGE_SIZE);
        int totalRecords = exportDAO.countExportHistoryAdvanced(fromDate, toDate, materialName);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        // 4. Lấy danh sách vật tư cho autocomplete
        List<Material> materialList = materialDAO.getAllMaterials();
        List<User> userList = userDAO.getAllUsers();

        // 5. Đẩy dữ liệu lên request
        request.setAttribute("exportList", exportList);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("materialName", materialName);
        request.setAttribute("sortByRecipient", sortByRecipient);
        request.setAttribute("sortByExportedBy", sortByExportedBy);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("materialList", materialList);
        request.setAttribute("userList", userList);

        // 6. Forward về JSP
        request.getRequestDispatcher("ExportHistory.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}