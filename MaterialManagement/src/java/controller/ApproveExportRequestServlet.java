package controller;

import dal.ExportRequestDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import java.io.IOException;

@WebServlet(name = "ApproveExportRequestServlet", urlPatterns = {"/ApproveExportRequest"})
public class ApproveExportRequestServlet extends HttpServlet {

    private ExportRequestDAO exportRequestDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        exportRequestDAO = new ExportRequestDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }
        
        int roleId = user.getRoleId();
        if (roleId != 1 && roleId != 2 && !rolePermissionDAO.hasPermission(roleId, "HANDLE_REQUEST")) {
            request.setAttribute("error", "You do not have permission to approve export requests.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        
        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String approvalReason = request.getParameter("approvalReason");
            ExportRequest req = exportRequestDAO.getById(requestId);
            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList");
                return;
            }
            if (!"pending".equals(req.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/ViewExportRequest?id=" + requestId);
                return;
            }
            if (approvalReason == null || approvalReason.trim().isEmpty()) {
                request.setAttribute("error", "Approval reason is required.");
                request.getRequestDispatcher("/ViewExportRequest.jsp").forward(request, response);
                return;
            }
            req.setStatus("approved");
            req.setApprovedBy(user.getUserId());
            req.setApprovalReason(approvalReason);
            boolean success = exportRequestDAO.update(req);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ViewExportRequest?id=" + requestId);
            } else {
                request.setAttribute("error", "Failed to approve request.");
                request.getRequestDispatcher("/ViewExportRequest.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ExportRequestList");
        }
    }
}