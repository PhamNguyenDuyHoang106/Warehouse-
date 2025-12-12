package controller;

import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import entity.PurchaseRequest;
import entity.User;
import utils.PermissionHelper;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.HashMap;
@WebServlet(name = "ListPurchaseRequestsServlet", urlPatterns = {"/ListPurchaseRequests"})
public class ListPurchaseRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            session = request.getSession();
            session.setAttribute("redirectURL", request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO prd = null;
        UserDAO userDAO = null;
        RolePermissionDAO rolePermissionDAO = null;

        try {
            prd = new PurchaseRequestDAO();
            userDAO = new UserDAO();
            rolePermissionDAO = new RolePermissionDAO();

            // Admin (roleId == 1) has full access - check first before permission check
            if (currentUser.getRoleId() == 1) {
                // Admin has permission, continue to load the page
            } else {
                // For non-admin users, check permission
                boolean hasPermission = PermissionHelper.hasPermission(currentUser, "DS yêu cầu mua");
                if (!hasPermission) {
                    request.setAttribute("error", "You do not have permission to view purchase requests.");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                    return;
                }
            }
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String sortOption = request.getParameter("sort");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            int pageIndex = 1;
            int pageSize = 10;

            if (request.getParameter("page") != null) {
                try {
                    pageIndex = Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    pageIndex = 1;
                }
            }

            if (sortOption == null || sortOption.isEmpty()) {
                sortOption = "code_desc";
            }

            List<PurchaseRequest> list = prd.searchPurchaseRequest(keyword, status, startDate, endDate, pageIndex, pageSize, sortOption);
            int totalItems = prd.countPurchaseRequest(keyword, status, startDate, endDate);

            list.removeIf(pr -> "cancelled".equalsIgnoreCase(pr.getStatus()));

            HashMap<Integer, String> userIdToName = new HashMap<>();
            for (PurchaseRequest pr : list) {
                int uid = pr.getRequestBy();
                if (!userIdToName.containsKey(uid)) {
                    User requester = userDAO.getUserById(uid);
                    userIdToName.put(uid, requester != null ? requester.getFullName() : "Không xác định");
                }
            }

            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            request.setAttribute("purchaseRequests", list);
            request.setAttribute("userIdToName", userIdToName);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", pageIndex);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("sortOption", sortOption);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            // Admin có toàn quyền - PermissionHelper đã xử lý
            request.setAttribute("canApprove", PermissionHelper.hasPermission(currentUser, "Duyệt PR"));
            
            // Check permission for creating purchase request
            boolean hasCreatePurchaseRequestPermission = PermissionHelper.hasPermission(currentUser, "Tạo PR");
            request.setAttribute("hasCreatePurchaseRequestPermission", hasCreatePurchaseRequestPermission);

            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (prd != null) prd.close();
            if (userDAO != null) userDAO.close();
            if (rolePermissionDAO != null) rolePermissionDAO.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
