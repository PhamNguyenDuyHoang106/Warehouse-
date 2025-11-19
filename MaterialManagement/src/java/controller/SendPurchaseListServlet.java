package controller;

import dal.PurchaseRequestDAO;
import dal.UserDAO;
import entity.PurchaseRequest;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.EmailUtils;

@WebServlet(name = "SendPurchaseListServlet", urlPatterns = {"/SendPurchaseListServlet"})
public class SendPurchaseListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getRoleId() != 2) { // Only Director can send
            response.sendRedirect("ListPurchaseRequestsServlet?error=permission_denied");
            return;
        }

        try {
            String[] selectedIds = request.getParameterValues("selectedIds");

            if (selectedIds == null || selectedIds.length == 0) {
                response.sendRedirect("ListPurchaseRequestsServlet?error=no_selection");
                return;
            }

            UserDAO userDAO = new UserDAO();
            List<User> warehouseStaff = userDAO.getUsersByRoleId(3); 

            if (warehouseStaff.isEmpty()) {
                response.sendRedirect("ListPurchaseRequestsServlet?error=no_warehouse_staff");
                return;
            }
            
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            List<PurchaseRequest> purchaseRequests = new ArrayList<>();
            for (String idStr : selectedIds) {
                try {
                    int id = Integer.parseInt(idStr);
                    PurchaseRequest pr = prd.getPurchaseRequestById(id);
                    if (pr != null) {
                        purchaseRequests.add(pr);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid purchase request ID format: " + idStr);
                }
            }

            if (purchaseRequests.isEmpty()) {
                response.sendRedirect("ListPurchaseRequestsServlet?error=no_valid_requests_found");
                return;
            }

            String subject = "Selected Purchase Requests Notification";
            StringBuilder body = new StringBuilder();
            body.append("Hello Warehouse Team,\n\n");
            body.append("Please find the list of selected purchase requests that require your attention:\n\n");
            
            body.append(String.format("%-10s | %-18s | %-12s | %s\n", "ID", "Request Code", "Status", "Reason"));
            body.append("----------------------------------------------------------------------------------\n");
            for (PurchaseRequest pr : purchaseRequests) {
                body.append(String.format("%-10d | %-18s | %-12s | %s\n", 
                    pr.getId(), 
                    pr.getCode(), 
                    pr.getStatus(),
                    pr.getReason()));
            }
            body.append("\n\nPlease review and take necessary actions.\n\nThank you.");

            for (User staff : warehouseStaff) {
                EmailUtils.sendEmail(staff.getEmail(), subject, body.toString());
            }

            response.sendRedirect("ListPurchaseRequestsServlet?success=list_email_sent_selected");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ListPurchaseRequestsServlet?error=email_failed");
        }
    }
}
