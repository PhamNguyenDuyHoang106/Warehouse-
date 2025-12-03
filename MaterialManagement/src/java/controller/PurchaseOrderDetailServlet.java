package controller;

import dal.PurchaseOrderDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import entity.PurchaseOrder;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PurchaseOrderDetailServlet", urlPatterns = {"/PurchaseOrderDetail"})
public class PurchaseOrderDetailServlet extends HttpServlet {

    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        // Admin (roleId == 1) has full access - check first before permission check
        boolean hasListPermission;
        if (user.getRoleId() == 1) {
            hasListPermission = true;
            System.out.println("✅ PurchaseOrderDetailServlet - User " + user.getUsername() + " is ADMIN (roleId=1), granting full access");
        } else {
            hasListPermission = PermissionHelper.hasPermission(user, "DS đơn đặt hàng");
        }
        request.setAttribute("hasViewPurchaseOrderDetailPermission", hasListPermission);
        if (!hasListPermission) {
            request.setAttribute("error", "You do not have permission to view purchase order details.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Admin có toàn quyền - PermissionHelper đã xử lý
        boolean hasHandleRequestPermission = PermissionHelper.hasPermission(user, "Xác nhận PO");
        request.setAttribute("hasHandleRequestPermission", hasHandleRequestPermission);
        boolean hasSendToSupplierPermission = PermissionHelper.hasPermission(user, "Gửi PO");
        request.setAttribute("hasSendToSupplierPermission", hasSendToSupplierPermission);

        try {
            int poId = Integer.parseInt(request.getParameter("id"));
            PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);

            if (purchaseOrder == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase Order not found");
                return;
            }

            UserDAO userDAO = new UserDAO();
            User creator = userDAO.getUserById(purchaseOrder.getCreatedBy());
            request.setAttribute("creator", creator);

            int itemsPerPage = 5;
            int currentPage = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }
            int totalItems = purchaseOrder.getDetails() != null ? purchaseOrder.getDetails().size() : 0;
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
            if (totalPages == 0) totalPages = 1;
            int fromIndex = (currentPage - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            List<entity.PurchaseOrderDetail> pagedDetails = new java.util.ArrayList<>();
            if (purchaseOrder.getDetails() != null && totalItems > 0 && fromIndex < totalItems) {
                pagedDetails = purchaseOrder.getDetails().subList(fromIndex, toIndex);
            }
            
            Map<Integer, String> materialImages = new HashMap<>();
            if (purchaseOrder.getDetails() != null) {
                for (entity.PurchaseOrderDetail detail : purchaseOrder.getDetails()) {
                    String url = detail.getMaterialImageUrl();
                    if (url == null || url.trim().isEmpty()) {
                        url = "default.jpg";
                    }
                    materialImages.put(detail.getMaterialId(), url);
                }
            }
            request.setAttribute("materialImages", materialImages);
            request.setAttribute("pagedDetails", pagedDetails);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("totalItems", totalItems);

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("purchaseOrder", purchaseOrder);
            request.getRequestDispatcher("/PurchaseOrderDetail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Purchase Order ID");
        } catch (Exception e) {
            throw new ServletException("Error fetching purchase order details", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
        try {
            String action = request.getParameter("action");
            int poId = Integer.parseInt(request.getParameter("poId"));
            String status = request.getParameter("status");
            String approvalReason = request.getParameter("approvalReason");
            String rejectionReason = request.getParameter("rejectionReason");
            PurchaseOrder purchaseOrder = purchaseOrderDAO.getPurchaseOrderById(poId);
            if ("updateStatus".equals(action)) {
                String normalizedStatus = status != null ? status.trim().toLowerCase() : "";
                if ("sent".equals(normalizedStatus)) {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    boolean hasSendToSupplierPermission = PermissionHelper.hasPermission(user, "Gửi PO");
                    if (!hasSendToSupplierPermission || purchaseOrder == null || !"draft".equalsIgnoreCase(purchaseOrder.getStatus())) {
                        request.setAttribute("error", "You do not have permission to send purchase order.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                } else if ("confirmed".equals(normalizedStatus)) {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    boolean hasConfirmPermission = PermissionHelper.hasPermission(user, "Xác nhận PO");
                    if (!hasConfirmPermission || purchaseOrder == null) {
                        request.setAttribute("error", "You do not have permission to confirm purchase order.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                } else if ("cancelled".equals(normalizedStatus)) {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    boolean hasCancelPermission = PermissionHelper.hasPermission(user, "Hủy PO");
                    if (!hasCancelPermission || purchaseOrder == null) {
                        request.setAttribute("error", "You do not have permission to cancel purchase order.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid status update");
                    return;
                }
                boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, normalizedStatus, user.getUserId(), approvalReason, rejectionReason);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + poId + "&message=Status updated successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + poId + "&message=Error updating status");
                }
            } else {
                doGet(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/PurchaseOrderDetail?id=" + request.getParameter("poId") + "&message=Error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for viewing purchase order details";
    }
} 