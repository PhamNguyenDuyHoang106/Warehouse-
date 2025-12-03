package controller;

import dal.PurchaseOrderDAO;
import dal.RolePermissionDAO;
import dal.SupplierDAO;
import entity.PurchaseOrder;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.EmailUtils;
import utils.PermissionHelper;

@WebServlet(name = "PurchaseOrderListServlet", urlPatterns = {"/PurchaseOrderList"})
public class PurchaseOrderListServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderListServlet.class.getName());
    private final PurchaseOrderDAO purchaseOrderDAO = new PurchaseOrderDAO();
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
    
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    private static final int MAX_ITEMS_PER_PAGE = 100;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/Login.jsp?returnUrl=" + request.getRequestURI());
                return;
            }

            // Admin (roleId == 1) has full access - check first before permission check
            boolean hasPermission;
            if (user.getRoleId() == 1) {
                hasPermission = true;
                LOGGER.log(Level.INFO, "✅ PurchaseOrderListServlet - User {0} is ADMIN (roleId=1), granting full access", user.getUsername());
            } else {
                hasPermission = PermissionHelper.hasPermission(user, "DS đơn đặt hàng");
            }
            request.setAttribute("hasViewPurchaseOrderListPermission", hasPermission);
            if (!hasPermission) {
                LOGGER.log(Level.WARNING, "User {0} attempted to access PurchaseOrderList without permission.", user.getUsername());
                request.setAttribute("error", "Bạn không có quyền xem danh sách đơn đặt hàng.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // Admin có toàn quyền - PermissionHelper đã xử lý
            boolean hasSendToSupplierPermission = PermissionHelper.hasPermission(user, "Gửi PO");
            request.setAttribute("hasSendToSupplierPermission", hasSendToSupplierPermission);
            boolean hasHandleRequestPermission = PermissionHelper.hasPermission(user, "Xác nhận PO") || user.getRoleId() == 1;
            request.setAttribute("hasHandleRequestPermission", hasHandleRequestPermission);

            String status = request.getParameter("status");
            String poCode = request.getParameter("poCode");
            String sortBy = request.getParameter("sortBy");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            int page = DEFAULT_PAGE;
            int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
            
            try {
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.isEmpty()) {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) page = DEFAULT_PAGE;
                }
                
                String itemsPerPageStr = request.getParameter("itemsPerPage");
                if (itemsPerPageStr != null && !itemsPerPageStr.isEmpty()) {
                    itemsPerPage = Integer.parseInt(itemsPerPageStr);
                    if (itemsPerPage < 1) itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
                    if (itemsPerPage > MAX_ITEMS_PER_PAGE) itemsPerPage = MAX_ITEMS_PER_PAGE;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid number format for page or itemsPerPage: " + e.getMessage(), e);
            }
            
            LocalDate startDate = null;
            LocalDate endDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateStr, formatter);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Invalid start date format: " + startDateStr, e);
                }
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr, formatter);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Invalid end date format: " + endDateStr, e);
                }
            }

            List<PurchaseOrder> purchaseOrders = purchaseOrderDAO.getPurchaseOrders(page, itemsPerPage, status, poCode, startDate, endDate, sortBy);
            int totalItems = purchaseOrderDAO.getPurchaseOrderCount(status, poCode, startDate, endDate);
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            if (purchaseOrders == null) {
                LOGGER.log(Level.SEVERE, "Purchase orders list is null after fetching from DAO.");
                request.setAttribute("error", "An error occurred while loading data. Please try again later.");
            }

            request.setAttribute("purchaseOrders", purchaseOrders);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("itemsPerPage", itemsPerPage);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("status", status);
            request.setAttribute("poCode", poCode);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            
            // Check permission for creating purchase order
            boolean hasCreatePurchaseOrderPermission;
            if (user.getRoleId() == 1) {
                hasCreatePurchaseOrderPermission = true;
            } else {
                hasCreatePurchaseOrderPermission = PermissionHelper.hasPermission(user, "Tạo PO");
            }
            request.setAttribute("hasCreatePurchaseOrderPermission", hasCreatePurchaseOrderPermission);

            request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for PurchaseOrderListServlet.", e);
            request.setAttribute("error", "An unexpected error occurred. Please try again later.");
            try {
                request.getRequestDispatcher("PurchaseOrderList.jsp").forward(request, response);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Critical error: Could not forward to error page from doGet.", ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            }
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

        try {
            String action = request.getParameter("action");
            if ("updateStatus".equals(action)) {
                int poId = Integer.parseInt(request.getParameter("poId"));
                String status = request.getParameter("status");
                String approvalReason = request.getParameter("approvalReason");
                String rejectionReason = request.getParameter("rejectionReason");

                LOGGER.log(Level.INFO, "Updating PO status - poId: {0}, status: {1}, approvalReason: {2}, rejectionReason: {3}", 
                    new Object[]{poId, status, approvalReason, rejectionReason});

                if ("sent".equalsIgnoreCase(status)) {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    boolean hasSendToSupplierPermission = PermissionHelper.hasPermission(user, "Gửi PO");
                    if (!hasSendToSupplierPermission) {
                        request.setAttribute("error", "Bạn không có quyền gửi đơn đặt hàng.");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                }

                boolean success = purchaseOrderDAO.updatePurchaseOrderStatus(poId, status, user.getUserId(), approvalReason, rejectionReason);
                LOGGER.log(Level.INFO, "Update PO status result - poId: {0}, status: {1}, success: {2}", 
                    new Object[]{poId, status, success});

                if (success) {
                    if ("sent".equalsIgnoreCase(status)) {
                        try {
                            PurchaseOrder po = purchaseOrderDAO.getPurchaseOrderById(poId);
                            if (po != null && po.getSupplierId() != null) {
                                entity.Supplier supplier = new SupplierDAO().getSupplierByID(po.getSupplierId());
                                if (supplier != null && supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
                                    String subject = "[Notification] Purchase Order Sent";
                                    String content = "Dear Supplier,<br><br>You have a new purchase order (PO Code: " + po.getPoCode() + ") sent to you. Please log in to the system to view details.<br><br>Thank you.";
                                    try {
                                        EmailUtils.sendEmail(supplier.getEmail(), subject, content);
                                    } catch (Exception e) {
                                        LOGGER.log(Level.WARNING, "[MAIL] Failed to send email to supplier {0}: {1}", new Object[]{supplier.getEmail(), e.getMessage()});
                                    }
                                } else {
                                    LOGGER.log(Level.WARNING, "[MAIL] Supplier {0} has no valid email for PO {1}.", new Object[]{supplier != null ? supplier.getSupplierName() : "N/A", po.getPoCode()});
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "[MAIL] Error when sending email to supplier for PO {0}: {1}", new Object[]{poId, e.getMessage()});
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?success=Status updated successfully");
                    return;
                } else {
                    LOGGER.log(Level.WARNING, "Failed to update status for purchase order ID: " + poId + " to " + status);
                    response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?error=Error updating status");
                    return;
                }
            } else {
                LOGGER.log(Level.WARNING, "Unknown action: " + action);
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid PO ID or other number format in doPost: " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?error=Error: Invalid input format");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost for PurchaseOrderListServlet.", e);
            response.sendRedirect(request.getContextPath() + "/PurchaseOrderList?error=Error: An unexpected error occurred");
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for listing and managing purchase orders";
    }
} 