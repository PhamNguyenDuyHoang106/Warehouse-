package controller;

import dal.ExportRequestDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ExportRequestListServlet", urlPatterns = {"/ExportRequestList"})
public class ExportRequestListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportRequestListServlet.class.getName());
    private final ExportRequestDAO exportRequestDAO = new ExportRequestDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        boolean canView = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_EXPORT_REQUEST_LIST");
        request.setAttribute("canViewExportRequest", canView);
        if (!canView) {
            LOGGER.log(Level.WARNING, "User {0} attempted to access ExportRequestList without permission.", user.getUsername());
            request.setAttribute("error", "You do not have permission to view export requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        // Filter parameters
        String searchKeyword = request.getParameter("search");
        String selectedStatus = request.getParameter("status");
        String sortByName = request.getParameter("sortByName");
        String requestDateFrom = request.getParameter("requestDateFrom");
        String requestDateTo = request.getParameter("requestDateTo");
        if (selectedStatus == null || selectedStatus.isEmpty()) {
            selectedStatus = "all";
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
                LOGGER.log(Level.WARNING, "Invalid page number format: {0}", request.getParameter("page"));
                page = 1;
            }
        }
        int offset = (page - 1) * pageSize;

        try {
            List<ExportRequest> exportRequests = exportRequestDAO.getAllWithPagination(
                    offset, pageSize, searchKeyword, selectedStatus, sortByName, requestDateFrom, requestDateTo
            );
            int totalRecords = exportRequestDAO.getTotalExportRequestCount(
                    searchKeyword, selectedStatus, requestDateFrom, requestDateTo
            );
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            // Set attributes for JSP
            request.setAttribute("exportRequests", exportRequests);
            request.setAttribute("searchKeyword", searchKeyword);
            request.setAttribute("selectedStatus", selectedStatus);
            request.setAttribute("sortByName", sortByName);
            request.setAttribute("requestDateFrom", requestDateFrom);
            request.setAttribute("requestDateTo", requestDateTo);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving export request list: " + e.getMessage(), e);

            request.setAttribute("error", "Error retrieving export request list!");
        }

        request.getRequestDispatcher("ExportRequestList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Export Request List Servlet";
    }
}