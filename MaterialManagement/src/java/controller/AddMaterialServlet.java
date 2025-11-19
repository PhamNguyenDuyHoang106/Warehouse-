package controller;

import dal.MaterialDAO;
import dal.CategoryDAO;
import dal.UnitDAO;
import dal.RolePermissionDAO;
import entity.Material;
import entity.Category;
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
import java.util.Map;

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/addmaterial"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class AddMaterialServlet extends HttpServlet {

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
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }
            session = request.getSession();
            session.setAttribute("redirectURL", requestURI);
            response.sendRedirect("LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int roleId = user.getRoleId();
        if (!PermissionHelper.hasPermission(user, "Tạo NVL")) {
            request.setAttribute("error", "Bạn không có quyền thêm mới vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        CategoryDAO cd = new CategoryDAO();
        UnitDAO ud = new UnitDAO();
        request.setAttribute("categories", cd.getAllCategories());
        request.setAttribute("units", ud.getAllUnits());

        MaterialDAO materialDAO = new MaterialDAO();
        int maxNum = materialDAO.getMaxMaterialNumber();
        String newMaterialCode = "MAT" + (maxNum + 1);
        request.setAttribute("materialCode", newMaterialCode);

        request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
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
        if (!PermissionHelper.hasPermission(user, "Tạo NVL")) {
            request.setAttribute("error", "Bạn không có quyền thêm mới vật tư.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            request.setCharacterEncoding("UTF-8");

            String materialCode = request.getParameter("materialCode");
            materialCode = materialCode != null ? materialCode.trim() : "";
            String materialName = request.getParameter("materialName");
            materialName = materialName != null ? materialName.trim() : "";
            String materialStatus = request.getParameter("materialStatus");
            materialStatus = materialStatus != null ? materialStatus.trim() : "";
            String categoryIdStr = request.getParameter("categoryId");
            categoryIdStr = categoryIdStr != null ? categoryIdStr.trim() : "";
            String unitIdStr = request.getParameter("unitId");
            unitIdStr = unitIdStr != null ? unitIdStr.trim() : "";
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

            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                    materialCode, materialName, materialStatus, categoryIdStr, unitIdStr, minStockStr, maxStockStr);

            if (!urlInput.isEmpty() && urlInput.length() > 500) {
                errors.put("materialsUrl", "Độ dài URL không được vượt quá 500 ký tự.");
            }

            if (!errors.isEmpty()) {
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setBarcode(barcode);
                m.setCategory(new Category());
                m.setUnit(new Unit());

                request.setAttribute("errors", errors);
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                request.setAttribute("barcode", barcode);
                request.setAttribute("materialsUrl", urlInput);
                request.setAttribute("minStock", minStockStr);
                request.setAttribute("maxStock", maxStockStr);
                request.setAttribute("weightPerUnit", weightPerUnitStr);
                request.setAttribute("volumePerUnit", volumePerUnitStr);
                request.setAttribute("shelfLifeDays", shelfLifeDaysStr);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            String relativeFilePath = handleImageUpload(request.getPart("imageFile"));
            String finalUrl = relativeFilePath != null ? relativeFilePath : (urlInput.isEmpty() ? null : urlInput);

            if (finalUrl != null && finalUrl.length() > 500) {
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setBarcode(barcode != null ? barcode.trim() : null);
                if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                    try {
                        Category cat = new Category();
                        cat.setCategory_id(Integer.parseInt(categoryIdStr));
                        m.setCategory(cat);
                    } catch (NumberFormatException ignored) {}
                }
                if (unitIdStr != null && !unitIdStr.isEmpty()) {
                    try {
                        Unit u = new Unit();
                        u.setId(Integer.parseInt(unitIdStr));
                        m.setUnit(u);
                    } catch (NumberFormatException ignored) {}
                }
                m.setUrl(finalUrl);

                request.setAttribute("error", "Đường dẫn URL không được vượt quá 500 ký tự.");
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                request.setAttribute("barcode", barcode);
                request.setAttribute("materialsUrl", urlInput);
                request.setAttribute("minStock", minStockStr);
                request.setAttribute("maxStock", maxStockStr);
                request.setAttribute("weightPerUnit", weightPerUnitStr);
                request.setAttribute("volumePerUnit", volumePerUnitStr);
                request.setAttribute("shelfLifeDays", shelfLifeDaysStr);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            if (categoryIdStr == null || categoryIdStr.isEmpty()) {
                request.setAttribute("error", "Bạn phải chọn Category từ danh sách gợi ý.");
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }
            if (unitIdStr == null || unitIdStr.isEmpty()) {
                request.setAttribute("error", "Bạn phải chọn Unit từ danh sách gợi ý.");
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            int categoryId = Integer.parseInt(categoryIdStr);
            int unitId = Integer.parseInt(unitIdStr);

            Material material = new Material();
            material.setMaterialCode(materialCode);
            material.setMaterialName(materialName);
            material.setUrl(finalUrl);
            material.setMaterialStatus(materialStatus);
            material.setBarcode(barcode != null ? barcode.trim() : null);
            material.setMinStock(parseDecimal(minStockStr));
            material.setMaxStock(parseDecimal(maxStockStr));
            material.setWeightPerUnit(parseDecimal(weightPerUnitStr));
            material.setVolumePerUnit(parseDecimal(volumePerUnitStr));
            material.setShelfLifeDays(parseInteger(shelfLifeDaysStr));
            material.setSerialized(isSerialized);
            material.setBatchControlled(isBatchControlled);

            Category category = new Category();
            category.setCategory_id(categoryId);
            material.setCategory(category);

            Unit unit = new Unit();
            unit.setId(unitId);
            material.setUnit(unit);
            material.setCreatedBy(user.getUserId());
            material.setUpdatedBy(user.getUserId());

            MaterialDAO materialDAO = new MaterialDAO();
            if (materialDAO.isMaterialCodeExists(materialCode)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "Mã vật tư đã tồn tại.");
                request.setAttribute("m", material);
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            if (materialDAO.isMaterialNameAndStatusExists(materialName, materialStatus)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "Tên vật tư đã tồn tại với trạng thái này.");
                request.setAttribute("m", material);
                request.setAttribute("materialCode", materialCode);
                request.setAttribute("materialsUrl", urlInput);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            materialDAO.addMaterial(material);
            response.sendRedirect("dashboardmaterial?success=Material added successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
            request.setAttribute("categories", new CategoryDAO().getAllCategories());
            request.setAttribute("units", new UnitDAO().getAllUnits());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
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

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}