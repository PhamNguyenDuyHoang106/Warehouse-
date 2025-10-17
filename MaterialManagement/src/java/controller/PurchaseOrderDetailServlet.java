package controller;

import dal.PurchaseOrderDAO;
import dal.RolePermissionDAO;
import dal.UserDAO;
import entity.PurchaseOrder;
import entity.User;
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
        boolean hasListPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "VIEW_PURCHASE_ORDER_LIST");
        request.setAttribute("hasViewPurchaseOrderDetailPermission", hasListPermission);
        if (!hasListPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        boolean hasHandleRequestPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "HANDLE_REQUEST");
        request.setAttribute("hasHandleRequestPermission", hasHandleRequestPermission);
        boolean hasSendToSupplierPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "SENT_TO_SUPPLIER");
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
            List pagedDetails = new java.util.ArrayList();
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
                if ("approved".equals(status) || "rejected".equals(status)) {
                    boolean hasHandleRequestPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "HANDLE_REQUEST");
                    if (!hasHandleRequestPermission || purchaseOrder == null || !"pending".equals(purchaseOrder.getStatus())) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                        return;
                    }
                } else if ("sent_to_supplier".equals(status)) {
                    boolean hasSendToSupplierPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "SENT_TO_SUPPLIER");
                    if (!hasSendToSupplierPermission || purchaseOrder == null || !"approved".equals(purchaseOrder.getStatus())) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                        return;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid status update");
                    return;
                }
                boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, status, user.getUserId(), approvalReason, rejectionReason);
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