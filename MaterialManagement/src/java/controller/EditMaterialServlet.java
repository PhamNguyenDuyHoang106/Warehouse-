package controller;

import dal.CategoryDAO;
import dal.MaterialDAO;
import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Category;
import entity.Material;
import entity.Unit;
import entity.User;
import utils.MaterialValidator;
import utils.PermissionHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "EditMaterialServlet", urlPatterns = {"/editmaterial"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public class EditMaterialServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(EditMaterialServlet.class.getName());
    private static final String UPLOAD_DIRECTORY = "images/material";
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        // Admin (roleId == 1) has full access - check first before permission check
        if (roleId != 1) {
            if (!PermissionHelper.hasPermission(user, "Sửa NVL")) {
                request.setAttribute("error", "You do not have permission to edit material.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        }

        String materialId = request.getParameter("materialId");
        
        if (materialId == null || materialId.trim().isEmpty()) {
            response.sendRedirect("dashboardmaterial");
            return;
        }

        MaterialDAO materialDAO = null;
        CategoryDAO categoryDAO = null;
        UnitDAO unitDAO = null;
        Material material = null;
        try {
            materialDAO = new MaterialDAO();
            int id = Integer.parseInt(materialId);
            material = materialDAO.getInformation(id);
            
            if (material == null) {
                response.sendRedirect("dashboardmaterial");
                return;
            }

            request.setAttribute("m", material);
            
            categoryDAO = new CategoryDAO();
            unitDAO = new UnitDAO();
            
            List<Category> categories = categoryDAO.getAllCategories();
            List<Unit> units = unitDAO.getAllUnits();
            
            // Đảm bảo không null
            if (categories == null) {
                categories = new ArrayList<>();
            }
            if (units == null) {
                units = new ArrayList<>();
            }
            
            request.setAttribute("categories", categories);
            request.setAttribute("units", units);
            
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("dashboardmaterial");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading material/categories/units", e);
            request.setAttribute("error", "Đã xảy ra lỗi khi tải dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (materialDAO != null) materialDAO.close();
            if (categoryDAO != null) categoryDAO.close();
            if (unitDAO != null) unitDAO.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        
        // Admin (roleId == 1) has full access - check first before permission check
        if (roleId != 1) {
            if (!PermissionHelper.hasPermission(user, "Sửa NVL")) {
                request.setAttribute("error", "You do not have permission to edit material.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        }

        try {
            request.setCharacterEncoding("UTF-8");

            String materialId = request.getParameter("materialId");
            String materialCode = request.getParameter("materialCode");
            String materialName = request.getParameter("materialName");
            String materialStatus = request.getParameter("materialStatus");
            String categoryId = request.getParameter("categoryId");
            String unitId = request.getParameter("unitId");
            String barcode = request.getParameter("barcode");
            String urlInput = request.getParameter("materialsUrl");
            urlInput = urlInput != null ? urlInput.trim() : "";
            String minStockStr = request.getParameter("minStock");
            String maxStockStr = request.getParameter("maxStock");
            String weightPerUnitStr = request.getParameter("weightPerUnit");
            String volumePerUnitStr = request.getParameter("volumePerUnit");
            String shelfLifeDaysStr = request.getParameter("shelfLifeDays");
            boolean isSerialized = "on".equalsIgnoreCase(request.getParameter("isSerialized"));
            boolean isBatchControlled = !"off".equalsIgnoreCase(request.getParameter("isBatchControlled"));

            Map<String, String> errors = new HashMap<>();
            BigDecimal minStock = parseDecimal(minStockStr, "minStock", errors, "Tồn kho tối thiểu");
            BigDecimal maxStock = parseDecimal(maxStockStr, "maxStock", errors, "Tồn kho tối đa");
            BigDecimal weightPerUnit = parseDecimal(weightPerUnitStr, "weightPerUnit", errors, "Khối lượng/đơn vị");
            BigDecimal volumePerUnit = parseDecimal(volumePerUnitStr, "volumePerUnit", errors, "Thể tích/đơn vị");
            Integer shelfLifeDays = parseInteger(shelfLifeDaysStr, "shelfLifeDays", errors, "Hạn sử dụng (ngày)");

            MaterialDAO materialDAO = null;
            CategoryDAO categoryDAO = null;
            UnitDAO unitDAO = null;
            try {
                materialDAO = new MaterialDAO();
                Material existingMaterial = materialDAO.getInformation(Integer.parseInt(materialId));
                if (existingMaterial == null) {
                    request.setAttribute("error", "Vật tư không tồn tại.");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                    return;
                }

                Material material = new Material();
                material.setMaterialId(Integer.parseInt(materialId));
                material.setMaterialCode(materialCode != null ? materialCode.trim() : "");
                material.setMaterialName(materialName != null ? materialName.trim() : "");
                material.setMaterialStatus(materialStatus != null ? materialStatus.trim() : "");
                material.setBarcode(barcode != null ? barcode.trim() : null);
                material.setMinStock(minStock);
                material.setMaxStock(maxStock);
                material.setWeightPerUnit(weightPerUnit);
                material.setVolumePerUnit(volumePerUnit);
                material.setShelfLifeDays(shelfLifeDays);
                material.setSerialized(isSerialized);
                material.setBatchControlled(isBatchControlled);

                Category category = new Category();
                category.setCategory_id(categoryId != null && !categoryId.isEmpty() ? Integer.parseInt(categoryId) : 0);
                material.setCategory(category);

                Unit unit = new Unit();
                unit.setId(unitId != null && !unitId.isEmpty() ? Integer.parseInt(unitId) : 0);
                material.setUnit(unit);

                material.setUrl(urlInput.isEmpty() ? existingMaterial.getUrl() : urlInput);

                errors.putAll(MaterialValidator.validateMaterialUpdate(material));

                if (!errors.isEmpty()) {
                    categoryDAO = new CategoryDAO();
                    unitDAO = new UnitDAO();
                    request.setAttribute("errors", errors);
                    request.setAttribute("m", material);
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.setAttribute("units", unitDAO.getAllUnits());
                    request.setAttribute("barcode", barcode);
                    request.setAttribute("materialsUrl", urlInput);
                    request.setAttribute("minStock", minStockStr);
                    request.setAttribute("maxStock", maxStockStr);
                    request.setAttribute("weightPerUnit", weightPerUnitStr);
                    request.setAttribute("volumePerUnit", volumePerUnitStr);
                    request.setAttribute("shelfLifeDays", shelfLifeDaysStr);
                    request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
                    return;
                }

                String uploadedImage = handleImageUpload(request.getPart("imageFile"));
                String finalUrl = uploadedImage != null
                        ? uploadedImage
                        : (!urlInput.isEmpty() ? urlInput : existingMaterial.getRawUrl());
                material.setUrl(finalUrl);

                material.setUpdatedBy(user.getUserId());

                materialDAO.updateMaterial(material);
                response.sendRedirect("dashboardmaterial?success=Material updated successfully");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in doPost", e);
                request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
                request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
            } finally {
                if (materialDAO != null) materialDAO.close();
                if (categoryDAO != null) categoryDAO.close();
                if (unitDAO != null) unitDAO.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doPost outer", e);
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("EditMaterial.jsp").forward(request, response);
        }
    }

    private String handleImageUpload(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + "/";
                Files.createDirectories(Paths.get(buildUploadPath));
                filePart.write(buildUploadPath + fileName);

                Path projectRoot = Paths.get(buildUploadPath).getParent().getParent().getParent().getParent();
                Path sourceDir = projectRoot.resolve("web").resolve("images").resolve("material");
                Files.createDirectories(sourceDir);
                    Files.copy(
                            Paths.get(buildUploadPath + fileName),
                            sourceDir.resolve(fileName),
                            StandardCopyOption.REPLACE_EXISTING
                    );
        return fileName;
    }

    private BigDecimal parseDecimal(String value, String key, Map<String, String> errors, String label) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            errors.put(key, label + " không hợp lệ.");
            return BigDecimal.ZERO;
        }
    }

    private Integer parseInteger(String value, String key, Map<String, String> errors, String label) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            errors.put(key, label + " không hợp lệ.");
            return null;
        }
    }
}