package controller;

import dal.ExportRequestDAO;
import dal.RolePermissionDAO;
import entity.ExportRequest;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import entity.User;
import java.io.IOException;

@WebServlet(name = "ApproveExportRequestServlet", urlPatterns = {"/ApproveExportRequest"})
public class ApproveExportRequestServlet extends BaseServlet {

    private ExportRequestDAO exportRequestDAO;
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        exportRequestDAO = new ExportRequestDAO();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(exportRequestDAO);
        registerDAO(rolePermissionDAO);
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
        
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Duyệt yêu cầu xuất")) {
            request.setAttribute("error", "Bạn không có quyền duyệt yêu cầu xuất.");
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
                response.sendRedirect(request.getContextPath() + "/ExportRequestList?error=Request is not in pending status");
                return;
            }
            if (approvalReason == null || approvalReason.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList?error=Approval reason is required");
                return;
            }
            req.setStatus("approved");
            req.setApprovedBy(user.getUserId());
            req.setApprovalReason(approvalReason);
            boolean success = exportRequestDAO.update(req);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList?success=Request approved successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/ExportRequestList?error=Failed to approve request.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ExportRequestList");
        }
    }
}