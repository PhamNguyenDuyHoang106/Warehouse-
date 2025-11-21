 package controller;

import dal.RepairRequestDAO;
import entity.RepairRequest;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RepairRequestListServlet", urlPatterns = {"/repairrequestlist"})
public class RepairRequestListServlet extends HttpServlet {

    private RepairRequestDAO repairRequestDAO = new RepairRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        int page = 1;
        int pageSize = 10; // Number of records per page
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offset = (page - 1) * pageSize;

        // Filter parameters
        String searchKeyword = request.getParameter("search");
        String selectedStatus = request.getParameter("status");
        String sortByName = request.getParameter("sortByName");
        String requestDateFrom = request.getParameter("requestDateFrom");
        String requestDateTo = request.getParameter("requestDateTo");
        if (selectedStatus == null || selectedStatus.isEmpty()) {
            selectedStatus = "all";
        }

        try {
            List<RepairRequest> repairRequests = repairRequestDAO.getRepairRequestsWithPagination(
                    offset, pageSize, searchKeyword, selectedStatus, sortByName, requestDateFrom, requestDateTo
            );
            int totalRecords = repairRequestDAO.getTotalRepairRequestCount(
                    searchKeyword, selectedStatus, requestDateFrom, requestDateTo
            );
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // Set attributes for JSP
            request.setAttribute("repairRequests", repairRequests);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedStatus", selectedStatus);
            request.setAttribute("sortByName", sortByName);
            request.setAttribute("requestDateFrom", requestDateFrom);
            request.setAttribute("requestDateTo", requestDateTo);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            
            // Check permission for creating repair request
            boolean hasCreateRepairRequestPermission = PermissionHelper.hasPermission(user, "Tạo yêu cầu sửa");
            request.setAttribute("hasCreateRepairRequestPermission", hasCreateRepairRequestPermission);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error retrieving request list!");
        }

        request.getRequestDispatcher("RepairRequestList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); 
    }

    @Override
    public String getServletInfo() {
        return "Repair Request List Servlet";
    }
}