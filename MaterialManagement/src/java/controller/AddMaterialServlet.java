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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
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
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_MATERIAL")) {
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
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "CREATE_MATERIAL")) {
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
            String urlInput = request.getParameter("materialsUrl");
            urlInput = urlInput != null ? urlInput.trim() : "";

            
    


            Map<String, String> errors = MaterialValidator.validateMaterialFormData(
                materialCode, materialName, materialStatus, categoryIdStr, unitIdStr);

            if (!errors.isEmpty()) {
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setCategory(new Category());
                m.setUnit(new Unit());

                request.setAttribute("errors", errors);
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                
                // Trả lại giá trị từ request parameters để giữ lại dữ liệu đã nhập
                request.setAttribute("categoryName", request.getParameter("categoryName"));
                request.setAttribute("categoryId", request.getParameter("categoryId"));
                request.setAttribute("unitName", request.getParameter("unitName"));
                request.setAttribute("unitId", request.getParameter("unitId"));
                request.setAttribute("materialsUrl", urlInput);
                
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("imageFile");
            String relativeFilePath;

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

                String buildUploadPath = getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + "/";
                Files.createDirectories(Paths.get(buildUploadPath));
                filePart.write(buildUploadPath + fileName);
    

                Path projectRoot = Paths.get(buildUploadPath).getParent().getParent().getParent().getParent();
                Path sourceDir = projectRoot.resolve("web").resolve("images").resolve("material");
                Files.createDirectories(sourceDir);
                try {
                    Files.copy(
                            Paths.get(buildUploadPath + fileName),
                            sourceDir.resolve(fileName),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                relativeFilePath = fileName;
            } else if (urlInput != null && !urlInput.trim().isEmpty()) {
                relativeFilePath = urlInput.trim();

            } else {
                relativeFilePath = "default.jpg";
            }

            // Validate categoryIdStr và unitIdStr trước khi parseInt
            if (categoryIdStr == null || categoryIdStr.isEmpty()) {
                request.setAttribute("error", "Bạn phải chọn Category từ danh sách gợi ý.");
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setCategory(new Category());
                m.setUnit(new Unit());
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                
                // Trả lại giá trị từ request parameters để giữ lại dữ liệu đã nhập
                request.setAttribute("categoryName", request.getParameter("categoryName"));
                request.setAttribute("categoryId", request.getParameter("categoryId"));
                request.setAttribute("unitName", request.getParameter("unitName"));
                request.setAttribute("unitId", request.getParameter("unitId"));
                request.setAttribute("materialsUrl", urlInput);
                
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }
            if (unitIdStr == null || unitIdStr.isEmpty()) {
                request.setAttribute("error", "Bạn phải chọn Unit từ danh sách gợi ý.");
                Material m = new Material();
                m.setMaterialCode(materialCode);
                m.setMaterialName(materialName);
                m.setMaterialStatus(materialStatus);
                m.setCategory(new Category());
                m.setUnit(new Unit());
                request.setAttribute("m", m);
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("materialCode", materialCode);
                
                // Trả lại giá trị từ request parameters để giữ lại dữ liệu đã nhập
                request.setAttribute("categoryName", request.getParameter("categoryName"));
                request.setAttribute("categoryId", request.getParameter("categoryId"));
                request.setAttribute("unitName", request.getParameter("unitName"));
                request.setAttribute("unitId", request.getParameter("unitId"));
                request.setAttribute("materialsUrl", urlInput);
                
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

            int categoryId = Integer.parseInt(categoryIdStr);
            int unitId = Integer.parseInt(unitIdStr);

            Material m = new Material();
            m.setMaterialCode(materialCode);
            m.setMaterialName(materialName);
            m.setMaterialsUrl(relativeFilePath);
            m.setMaterialStatus(materialStatus);

            Category category = new Category();
            category.setCategory_id(categoryId);
            m.setCategory(category);

            Unit unit = new Unit();
            unit.setId(unitId);
            m.setUnit(unit);

            m.setDisable(false);
            m.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            m.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

    

            MaterialDAO md = new MaterialDAO();
            if (md.isMaterialCodeExists(materialCode)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "Mã vật tư đã tồn tại.");
                request.setAttribute("m", m);
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

    
            if (md.isMaterialNameAndStatusExists(materialName, materialStatus)) {
                request.setAttribute("categories", new CategoryDAO().getAllCategories());
                request.setAttribute("units", new UnitDAO().getAllUnits());
                request.setAttribute("error", "This material name already exists with the selected status. Please choose a different name or status.");
                request.setAttribute("m", m);
                request.setAttribute("materialCode", materialCode);
                request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
                return;
            }

    
            md.addMaterial(m);
            response.sendRedirect("dashboardmaterial?success=Material added successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + ex.getMessage());
            request.setAttribute("categories", new CategoryDAO().getAllCategories());
            request.setAttribute("units", new UnitDAO().getAllUnits());
            request.getRequestDispatcher("AddMaterial.jsp").forward(request, response);
        }
    }
}