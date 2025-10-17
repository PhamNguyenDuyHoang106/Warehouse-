package controller;

import dal.DepartmentDAO;
import dal.ExportDAO;
import dal.InventoryDAO;
import dal.RolePermissionDAO;
import entity.Department;
import entity.Export;
import entity.ExportDetail;
import entity.Material;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ExportMaterialServlet", urlPatterns = {"/ExportMaterial"})
public class ExportMaterialServlet extends HttpServlet {

    private ExportDAO exportDAO;
    private DepartmentDAO departmentDAO;
    private InventoryDAO inventoryDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 3;

    @Override
    public void init() throws ServletException {
        exportDAO = new ExportDAO();
        departmentDAO = new DepartmentDAO();
        inventoryDAO = new InventoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "EXPORT_MATERIAL");
        request.setAttribute("hasExportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to export materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            int currentPage = getCurrentPage(request);
            loadDataAndForward(request, response, currentPage);
        } catch (Exception e) {
            request.setAttribute("error", "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
            request.getRequestDispatcher("/ExportMaterial.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        boolean hasPermission = rolePermissionDAO.hasPermission(user.getRoleId(), "EXPORT_MATERIAL");
        request.setAttribute("hasExportMaterialPermission", hasPermission);
        if (!hasPermission) {
            request.setAttribute("error", "You do not have permission to export materials.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int currentPage = getCurrentPage(request);
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                handleAddMaterial(request, response, session, user, currentPage);
            } else if ("export".equals(action)) {
                handleExport(request, response, session, user, currentPage);
            } else if ("remove".equals(action)) {
                handleRemoveMaterial(request, response, session, currentPage);
            } else if ("updateQuantity".equals(action)) {
                handleUpdateQuantity(request, response, session, currentPage);
            } else {
                request.setAttribute("error", "Invalid action.");
                loadDataAndForward(request, response, currentPage);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            loadDataAndForward(request, response, currentPage);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input data: " + e.getMessage());
            loadDataAndForward(request, response, currentPage);
        }
    }

    private int getCurrentPage(HttpServletRequest request) {
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        return currentPage;
    }

    private void handleAddMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user, int currentPage)
            throws ServletException, IOException, SQLException {
        String materialIdStr = request.getParameter("materialId");
        String quantityStr = request.getParameter("quantity");
        List<ExportDetail> detailsToAdd = new ArrayList<>();
        List<Material> materials = departmentDAO.getMaterials();
        Map<Integer, Material> materialMap = new HashMap<>();
        for (Material material : materials) {
            materialMap.put(material.getMaterialId(), material);
        }

        if (materialIdStr == null || materialIdStr.isEmpty() || 
            quantityStr == null || quantityStr.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        int materialId = Integer.parseInt(materialIdStr);
        int quantity = Integer.parseInt(quantityStr);

        if (quantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        int currentStock = inventoryDAO.getStockByMaterialId(materialId);
        if (currentStock < quantity) {
            request.setAttribute("error", "Insufficient stock for material ID: " + materialId + ". Available: " + currentStock);
            loadDataAndForward(request, response, currentPage);
            return;
        }

        if (!materialMap.containsKey(materialId)) {
            request.setAttribute("error", "Selected material is invalid.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        ExportDetail detail = new ExportDetail();
        detail.setMaterialId(materialId);
        detail.setQuantity(quantity);
        detail.setStatus("draft");
        detail.setCreatedAt(LocalDateTime.now());
        detail.setUpdatedAt(LocalDateTime.now());
        detailsToAdd.add(detail);

        Integer tempExportId = (Integer) session.getAttribute("tempExportId");
        if (tempExportId == null) {
            Export export = new Export();
            export.setExportDate(LocalDateTime.now());
            export.setExportedBy(user.getUserId());
            export.setRecipientUserId(user.getUserId());
            export.setCreatedAt(LocalDateTime.now());
            export.setUpdatedAt(LocalDateTime.now());
            tempExportId = exportDAO.createExport(export);
            session.setAttribute("tempExportId", tempExportId);
        }

        for (ExportDetail detailToAdd : detailsToAdd) {
            detailToAdd.setExportId(tempExportId);
        }
        exportDAO.createExportDetails(detailsToAdd);
        request.setAttribute("success", "Material added to export list.");

        List<ExportDetail> fullExportDetails = exportDAO.getDraftExportDetails(tempExportId);
        int totalItems = fullExportDetails.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        loadDataAndForward(request, response, currentPage);
    }

    private void handleRemoveMaterial(HttpServletRequest request, HttpServletResponse response, HttpSession session, int currentPage)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        Integer tempExportId = (Integer) session.getAttribute("tempExportId");
        if (tempExportId == null) {
            request.setAttribute("error", "No export session found.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        exportDAO.removeExportDetail(tempExportId, materialId, quantity);
        request.setAttribute("success", "Material removed from export list.");

        List<ExportDetail> fullExportDetails = exportDAO.getDraftExportDetails(tempExportId);
        int totalItems = fullExportDetails.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        loadDataAndForward(request, response, currentPage);
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response, HttpSession session, int currentPage)
            throws ServletException, IOException, SQLException {
        int materialId = Integer.parseInt(request.getParameter("materialId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        Integer tempExportId = (Integer) session.getAttribute("tempExportId");
        if (tempExportId == null) {
            request.setAttribute("error", "No export session found.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        if (quantity <= 0) {
            request.setAttribute("error", "Quantity must be greater than 0.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        int currentStock = inventoryDAO.getStockByMaterialId(materialId);
        if (currentStock < quantity) {
            request.setAttribute("error", "Insufficient stock for material ID: " + materialId + ". Available: " + currentStock);
            loadDataAndForward(request, response, currentPage);
            return;
        }

        ExportDetail existingDetail = exportDAO.getExportDetailByMaterial(tempExportId, materialId);
        if (existingDetail == null) {
            request.setAttribute("error", "Export detail not found for material ID: " + materialId);
            loadDataAndForward(request, response, currentPage);
            return;
        }

        exportDAO.updateExportDetailQuantity(existingDetail.getExportDetailId(), quantity);
        request.setAttribute("success", "Quantity updated successfully for material ID: " + materialId);
        loadDataAndForward(request, response, currentPage);
    }

    private void handleExport(HttpServletRequest request, HttpServletResponse response, HttpSession session, User user, int currentPage)
            throws ServletException, IOException, SQLException {
        Integer tempExportId = (Integer) session.getAttribute("tempExportId");
        if (tempExportId == null) {
            request.setAttribute("error", "No materials selected for export.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        List<ExportDetail> details = exportDAO.getDraftExportDetails(tempExportId);
        if (details.isEmpty()) {
            request.setAttribute("error", "Export list is empty.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        for (ExportDetail detail : details) {
            int currentStock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
            if (currentStock < detail.getQuantity()) {
                request.setAttribute("error", "Insufficient stock for material ID: " + detail.getMaterialId() + ". Available: " + currentStock);
                loadDataAndForward(request, response, currentPage);
                return;
            }
        }

        int recipientUserId;
        try {
            recipientUserId = Integer.parseInt(request.getParameter("recipientUserId"));
            List<Integer> roleIds = List.of(3, 4);
            List<User> validRecipients = exportDAO.getUsersByRoleIds(roleIds);
            boolean validRecipient = validRecipients.stream()
                    .anyMatch(u -> u.getUserId() == recipientUserId);
            if (!validRecipient) {
                request.setAttribute("error", "Invalid recipient user ID. Must be a user with role ID 3 or 4.");
                loadDataAndForward(request, response, currentPage);
                return;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid recipient user ID format.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        String note = request.getParameter("note");

        if (note != null && note.length() > 100) {
            request.setAttribute("error", "Note must not exceed 100 characters.");
            loadDataAndForward(request, response, currentPage);
            return;
        }

        try {
            Export export = new Export();
            export.setExportId(tempExportId);
            export.setExportDate(LocalDateTime.now());
            export.setExportedBy(user.getUserId());
            export.setRecipientUserId(recipientUserId);
            export.setNote(note);
            export.setUpdatedAt(LocalDateTime.now());

            exportDAO.updateExport(export);
            exportDAO.confirmExport(tempExportId);
            exportDAO.updateInventoryByExportId(tempExportId, user.getUserId());

            String exportCode = exportDAO.getExportCode(tempExportId);
            session.setAttribute("tempExportId", null);
            request.setAttribute("success", "Export completed successfully with code: " + exportCode);
            currentPage = 1;
        } catch (SQLException e) {
            request.setAttribute("error", "Export failed: " + e.getMessage());
            e.printStackTrace();
        }
        loadDataAndForward(request, response, currentPage);
    }

    private void loadDataAndForward(HttpServletRequest request, HttpServletResponse response, int currentPage)
            throws ServletException, IOException {
        try {
            List<Department> departments = departmentDAO.getDepartments();
            List<Material> materials = departmentDAO.getMaterials();
            List<Integer> roleIds = List.of(3, 4);
            List<User> users = exportDAO.getUsersByRoleIds(roleIds);
            Integer tempExportId = (Integer) request.getSession().getAttribute("tempExportId");
            List<ExportDetail> fullExportDetails = (tempExportId != null) 
                    ? exportDAO.getDraftExportDetails(tempExportId) 
                    : new ArrayList<>();

            int totalItems = fullExportDetails.size();
            int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) {
                currentPage = totalPages;
            } else if (currentPage < 1) {
                currentPage = 1;
            }

            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, totalItems);
            List<ExportDetail> exportDetails = fullExportDetails.subList(start, end);

            Map<Integer, Material> materialMap = new HashMap<>();
            for (Material material : materials) {
                materialMap.put(material.getMaterialId(), material);
            }

            Map<Integer, Integer> stockMap = new HashMap<>();
            for (ExportDetail detail : fullExportDetails) {
                int stock = inventoryDAO.getStockByMaterialId(detail.getMaterialId());
                stockMap.put(detail.getMaterialId(), stock);
            }

            request.setAttribute("departments", departments);
            request.setAttribute("materials", materials);
            request.setAttribute("users", users);
            request.setAttribute("fullExportDetails", fullExportDetails);
            request.setAttribute("exportDetails", exportDetails);
            request.setAttribute("materialMap", materialMap);
            request.setAttribute("stockMap", stockMap);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
        } catch (SQLException e) {
            request.setAttribute("error", "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/ExportMaterial.jsp").forward(request, response);
    }
}