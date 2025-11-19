package controller;

import dal.CategoryDAO;
import dal.PurchaseRequestDAO;
import dal.UserDAO;
import dal.RolePermissionDAO;
import dal.MaterialDAO;
import utils.PermissionHelper;
import entity.Category;
import entity.Material;
import entity.PurchaseRequest;
import entity.PurchaseRequestDetail;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PurchaseRequestValidator;

/**
 * Servlet for handling creation of purchase requests.
 */
@WebServlet(name = "CreatePurchaseRequestServlet", urlPatterns = {"/CreatePurchaseRequest"})
public class CreatePurchaseRequestServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CreatePurchaseRequestServlet.class.getName());

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

        UserDAO userDAO = new UserDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();

        boolean hasPermission = PermissionHelper.hasPermission(currentUser, "Tạo PR");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            PurchaseRequestDAO prd = new PurchaseRequestDAO();
            List<User> users = userDAO.getAllUsers();
            List<Category> categories = categoryDAO.getAllCategories();
            List<Material> materials = materialDAO.getAllProducts();

            String requestCode = prd.generateNextRequestCode();
            Date today = new Date(System.currentTimeMillis());
            Date defaultExpected = Date.valueOf(today.toLocalDate().plusDays(7));

            request.setAttribute("users", users);
            request.setAttribute("categories", categories);
            request.setAttribute("materials", materials);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("requestDate", today.toString());
            request.setAttribute("expectedDate", defaultExpected.toString());
            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
            request.setAttribute("submittedReason", "");

            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login.jsp");
            return;
        }

        PurchaseRequestDAO prd = new PurchaseRequestDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        UserDAO userDAO = new UserDAO();

        boolean hasPermission = PermissionHelper.hasPermission(currentUser, "Tạo PR");
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to create purchase requests.");
            request.getRequestDispatcher("PurchaseRequestList.jsp").forward(request, response);
            return;
        }

        try {
            String reason = request.getParameter("reason");
            String expectedDateStr = request.getParameter("expectedDate");

            String[] materialNames = request.getParameterValues("materialName[]");
            String[] materialIds = request.getParameterValues("materialId[]");
            String[] quantities = request.getParameterValues("quantity[]");
            String[] notes = request.getParameterValues("note");

            Map<String, String> formErrors = PurchaseRequestValidator.validatePurchaseRequestFormData(reason, expectedDateStr);
            Map<String, String> detailErrors = PurchaseRequestValidator.validatePurchaseRequestDetails(materialNames, quantities);
            formErrors.putAll(detailErrors);

            Date expectedDate = null;
            if (expectedDateStr != null && !expectedDateStr.trim().isEmpty()) {
                try {
                    expectedDate = Date.valueOf(expectedDateStr.trim());
                } catch (IllegalArgumentException e) {
                    formErrors.put("expectedDate", "Invalid expected date format.");
                }
            }

            Date requestDate = new Date(System.currentTimeMillis());

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<Material> materials = materialDAO.getAllProducts();
                String requestCode = prd.generateNextRequestCode();

                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate.toString());
                request.setAttribute("expectedDate", expectedDateStr);

                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);

                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            List<PurchaseRequestDetail> purchaseRequestDetails = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (int i = 0; i < materialNames.length; i++) {
                String materialName = materialNames[i];
                if (materialName == null || materialName.trim().isEmpty()) {
                    continue;
                }

                int materialId = 0;
                if (materialIds != null && materialIds.length > i && materialIds[i] != null && !materialIds[i].isEmpty()) {
                    try {
                        materialId = Integer.parseInt(materialIds[i]);
                    } catch (NumberFormatException e) {
                        materialId = 0;
                    }
                }

                if (materialId <= 0) {
                    formErrors.put("material_" + i, "Invalid material selected at row " + (i + 1));
                    continue;
                }

                BigDecimal quantity = BigDecimal.ZERO;
                try {
                    quantity = new BigDecimal(quantities[i]);
                } catch (NumberFormatException e) {
                    formErrors.put("quantity_" + i, "Invalid quantity at row " + (i + 1));
                    continue;
                }

                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setMaterialId(materialId);
                detail.setQuantity(quantity);
                String note = (notes != null && notes.length > i) ? notes[i] : null;
                detail.setNote(note != null && !note.trim().isEmpty() ? note.trim() : null);

                Material material = materialDAO.getInformation(materialId);
                if (material != null) {
                    detail.setMaterialName(material.getMaterialName());
                    detail.setMaterialCode(material.getMaterialCode());
                    if (material.getDefaultUnit() != null) {
                        detail.setUnitId(material.getDefaultUnit().getId());
                        detail.setUnitName(material.getDefaultUnit().getUnitName());
                    }
                }

                // Unit price estimate currently not provided -> keep zero
                detail.setUnitPriceEstimate(BigDecimal.ZERO);
                detail.setTotalEstimate(BigDecimal.ZERO);

                purchaseRequestDetails.add(detail);
            }

            if (!formErrors.isEmpty()) {
                List<Category> categories = categoryDAO.getAllCategories();
                List<Material> materials = materialDAO.getAllProducts();
                String requestCode = prd.generateNextRequestCode();

                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", formErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate.toString());
                request.setAttribute("expectedDate", expectedDateStr);
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            User freshUser = userDAO.getUserById(currentUser.getUserId());

            PurchaseRequest purchaseRequest = new PurchaseRequest();
            purchaseRequest.setCode(prd.generateNextRequestCode());
            purchaseRequest.setRequestBy(currentUser.getUserId());
            Integer departmentId = freshUser != null ? freshUser.getDepartmentId() : currentUser.getDepartmentId();
            // department_id is NOT NULL in schema V12 - ensure it's not null
            if (departmentId == null) {
                LOGGER.log(Level.SEVERE, "User {0} does not have a department assigned. Cannot create Purchase Request.", currentUser.getUsername());
                request.setAttribute("error", "Bạn chưa được gán vào phòng ban. Vui lòng liên hệ quản trị viên.");
                List<Category> categories = categoryDAO.getAllCategories();
                List<Material> materials = materialDAO.getAllProducts();
                String requestCode = prd.generateNextRequestCode();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", requestCode);
                request.setAttribute("requestDate", requestDate.toString());
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }
            purchaseRequest.setDepartmentId(departmentId);
            purchaseRequest.setRequestDate(requestDate);
            purchaseRequest.setExpectedDate(expectedDate);
            purchaseRequest.setStatus("submitted");
            purchaseRequest.setReason(reason != null ? reason.trim() : null);
            purchaseRequest.setDetails(purchaseRequestDetails);
            purchaseRequest.setTotalAmount(totalAmount);

            Map<String, String> validationErrors = PurchaseRequestValidator.validatePurchaseRequest(purchaseRequest);
            if (!validationErrors.isEmpty()) {
                validationErrors.putAll(formErrors);
                List<Category> categories = categoryDAO.getAllCategories();
                List<Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("errors", validationErrors);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", purchaseRequest.getCode());
                request.setAttribute("requestDate", requestDate.toString());
                request.setAttribute("expectedDate", expectedDateStr);
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
                return;
            }

            boolean created = prd.createPurchaseRequestWithDetails(purchaseRequest, purchaseRequestDetails);
            if (created) {
                response.sendRedirect("ListPurchaseRequests?success=created");
            } else {
                request.setAttribute("error", "Failed to create purchase request. Please try again.");
                List<Category> categories = categoryDAO.getAllCategories();
                List<Material> materials = materialDAO.getAllProducts();
                request.setAttribute("categories", categories);
                request.setAttribute("materials", materials);
                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                request.setAttribute("requestCode", purchaseRequest.getCode());
                request.setAttribute("requestDate", requestDate.toString());
                request.setAttribute("expectedDate", expectedDateStr);
                request.setAttribute("submittedReason", reason);
                request.setAttribute("submittedMaterialNames", materialNames);
                request.setAttribute("submittedQuantities", quantities);
                request.setAttribute("submittedNotes", notes);
                request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating purchase request", e);
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("PurchaseRequestForm.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
