package controller;

import dal.CategoryDAO;
import dal.DepartmentDAO;
import dal.RequestDAO;
import entity.Category;
import entity.ExportRequest;
import entity.PurchaseOrder;
import entity.PurchaseRequest;
import entity.RepairRequest;
import entity.User;
import entity.Material;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "ViewRequestDetailsServlet", urlPatterns = {"/ViewRequestDetails"})
public class ViewRequestDetailsServlet extends HttpServlet {
    private static final int PAGE_SIZE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        RequestDAO requestDAO = null;
        DepartmentDAO departmentDAO = null;
        CategoryDAO categoryDAO = null;
        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            String type = request.getParameter("type");
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            Object requestObj = null;

            requestDAO = new RequestDAO();

            switch (type.toLowerCase()) {
                case "export":
                    requestObj = requestDAO.getExportRequestById(requestId);
                    break;
                case "purchase":
                    requestObj = requestDAO.getPurchaseRequestById(requestId);
                    break;
                case "repair":
                    requestObj = requestDAO.getRepairRequestById(requestId);
                    break;
                case "purchase_order":
                    requestObj = requestDAO.getPurchaseOrderById(requestId);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request type");
                    return;
            }

            if (requestObj == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }

            // Check access permissions
            int requestUserId = getRequestUserId(requestObj);
            boolean hasAccess = requestUserId == user.getUserId() || "Director".equalsIgnoreCase(user.getRoleName());
            if (!hasAccess) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            // Fetch materials for Export, Purchase, and Repair requests (not needed for Purchase Orders)
            List<Material> materials = new ArrayList<>();
            if (!type.equalsIgnoreCase("purchase_order")) {
                departmentDAO = new DepartmentDAO();
                materials = departmentDAO.getMaterials();
            }
            request.setAttribute("materials", materials);

            // Fetch categories
            categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

            // Paginate details
            List<?> details = getRequestDetails(requestObj);
            int totalDetails = details.size();
            int totalPages = (int) Math.ceil((double) totalDetails / PAGE_SIZE);
            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalDetails);
            List<?> paginatedDetails = details.subList(start, end);

            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.setAttribute("request", requestObj);
            request.setAttribute("requestType", type.equalsIgnoreCase("purchase_order") ? "PurchaseOrder" : type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
            request.setAttribute("details", paginatedDetails);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID or page number");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } finally {
            if (requestDAO != null) requestDAO.close();
            if (departmentDAO != null) departmentDAO.close();
            if (categoryDAO != null) categoryDAO.close();
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
        RequestDAO requestDAO = null;
        try {
            int requestId = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            String type = request.getParameter("type");
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            Object requestObj = null;

            requestDAO = new RequestDAO();

            // Fetch the request based on type
            switch (type.toLowerCase()) {
                case "export":
                    requestObj = requestDAO.getExportRequestById(requestId);
                    break;
                case "purchase":
                    requestObj = requestDAO.getPurchaseRequestById(requestId);
                    break;
                case "repair":
                    requestObj = requestDAO.getRepairRequestById(requestId);
                    break;
                case "purchase_order":
                    requestObj = requestDAO.getPurchaseOrderById(requestId);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request type");
                    return;
            }

            if (requestObj == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
                return;
            }

            int requestUserId = getRequestUserId(requestObj);
            if (requestUserId != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only the request owner can cancel it");
                return;
            }

            if ("cancel".equals(action)) {
                String status = getRequestStatus(requestObj);
                if (!"pending".equals(status)) {
                    request.setAttribute("message", "Error: Only pending requests can be cancelled.");
                    setRequestAttributes(request, response, requestObj, type, page);
                    return;
                }

                boolean cancelled = false;
                switch (type.toLowerCase()) {
                    case "export":
                        cancelled = requestDAO.cancelExportRequest(requestId, user.getUserId());
                        break;
                    case "purchase":
                        cancelled = requestDAO.cancelPurchaseRequest(requestId, user.getUserId());
                        break;
                    case "repair":
                        cancelled = requestDAO.cancelRepairRequest(requestId, user.getUserId());
                        break;
                    case "purchase_order":
                        cancelled = requestDAO.cancelPurchaseOrder(requestId, user.getUserId());
                        break;
                }

                if (cancelled) {
                    response.sendRedirect(request.getContextPath() + "/ViewRequestDetails?type=" + type + "&id=" + requestId + "&page=" + page + "&message=Request+cancelled+successfully");
                } else {
                    request.setAttribute("message", "Error: Failed to cancel request.");
                    setRequestAttributes(request, response, requestObj, type, page);
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID or page number");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } finally {
            if (requestDAO != null) requestDAO.close();
        }
    }

    private void setRequestAttributes(HttpServletRequest request, HttpServletResponse response, Object requestObj, String type, int page)
            throws ServletException, IOException, SQLException {
        DepartmentDAO departmentDAO = null;
        CategoryDAO categoryDAO = null;
        try {
            // Fetch materials for Export, Purchase, and Repair requests (not needed for Purchase Orders)
            List<Material> materials = new ArrayList<>();
            if (!type.equalsIgnoreCase("purchase_order")) {
                departmentDAO = new DepartmentDAO();
                materials = departmentDAO.getMaterials();
            }
            request.setAttribute("materials", materials);

            // Fetch categories
            categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);

        // Paginate details
        List<?> details = getRequestDetails(requestObj);
        int totalDetails = details.size();
        int totalPages = (int) Math.ceil((double) totalDetails / PAGE_SIZE);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalDetails);
        List<?> paginatedDetails = details.subList(start, end);

        request.setAttribute("request", requestObj);
        request.setAttribute("requestType", type.equalsIgnoreCase("purchase_order") ? "PurchaseOrder" : type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
        request.setAttribute("details", paginatedDetails);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/ViewRequestDetails.jsp").forward(request, response);
        } finally {
            if (departmentDAO != null) departmentDAO.close();
            if (categoryDAO != null) categoryDAO.close();
        }
    }

    private List<?> getRequestDetails(Object request) {
        if (request instanceof ExportRequest) {
            return ((ExportRequest) request).getDetails();
        } else if (request instanceof PurchaseRequest) {
            return ((PurchaseRequest) request).getDetails();
        } else if (request instanceof RepairRequest) {
            return ((RepairRequest) request).getDetails();
        } else if (request instanceof PurchaseOrder) {
            return ((PurchaseOrder) request).getDetails();
        }
        return new ArrayList<>();
    }

    private int getRequestUserId(Object request) {
        if (request instanceof ExportRequest) {
            return ((ExportRequest) request).getUserId();
        } else if (request instanceof PurchaseRequest) {
            return ((PurchaseRequest) request).getUserId();
        } else if (request instanceof RepairRequest) {
            return ((RepairRequest) request).getUserId();
        } else if (request instanceof PurchaseOrder) {
            return ((PurchaseOrder) request).getCreatedBy();
        }
        return 0;
    }

    private String getRequestStatus(Object request) {
        if (request instanceof ExportRequest) {
            return ((ExportRequest) request).getStatus();
        } else if (request instanceof PurchaseRequest) {
            return ((PurchaseRequest) request).getStatus();
        } else if (request instanceof RepairRequest) {
            return ((RepairRequest) request).getStatus();
        } else if (request instanceof PurchaseOrder) {
            return ((PurchaseOrder) request).getStatus();
        }
        return "";
    }
}