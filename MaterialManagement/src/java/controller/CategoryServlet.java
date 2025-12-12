package controller;

import dal.CategoryDAO;
import dal.RolePermissionDAO;
import entity.Category;
import entity.User;
import utils.PermissionHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/Category"})
public class CategoryServlet extends BaseServlet {
    private CategoryDAO categoryDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 5;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryDAO = new CategoryDAO();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(categoryDAO);
        registerDAO(rolePermissionDAO);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String service = request.getParameter("service");
        if (service == null) service = "listCategory";
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/Category.jsp").forward(request, response);
            return;
        }
        int roleId = currentUser.getRoleId();
        boolean isAdmin = roleId == 1;
        try {
            switch (service) {
                case "viewCategory": {
                    String categoryIdRaw = request.getParameter("category_id");
                    if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                        request.setAttribute("error", "Category ID is required.");
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                        return;
                    }
                    try {
                        int categoryId = Integer.parseInt(categoryIdRaw);
                        Category category = categoryDAO.getCategoryById(categoryId);
                        if (category != null) {
                            request.setAttribute("category", category);
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/CategoryDetail.jsp").forward(request, response);
                        } else {
                            request.setAttribute("error", "Category not found with ID: " + categoryId);
                            request.getRequestDispatcher("/error.jsp").forward(request, response);
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("error", "Invalid category ID.");
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                    break;
                }
                case "updateCategory": {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    if (!PermissionHelper.hasPermission(currentUser, "Sửa danh mục")) {
                        request.setAttribute("error", "You do not have permission to update categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        String categoryIdRaw = request.getParameter("category_id");
                        if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                            sendErrorResponse(response, "Missing category ID.");
                            return;
                        }
                        try {
                            int categoryId = Integer.parseInt(categoryIdRaw);
                            Category category = categoryDAO.getCategoryById(categoryId);
                            if (category == null) {
                                request.setAttribute("error", "No category found with ID: " + categoryId);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }
                            request.setAttribute("c", category);
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                        } catch (NumberFormatException e) {
                            sendErrorResponse(response, "Invalid category ID.");
                        }
                    } else {
                        try {
                            int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                            Category existingCategory = categoryDAO.getCategoryById(categoryId);
                            if (existingCategory == null) {
                                request.setAttribute("error", "Category not found.");
                                request.getRequestDispatcher("/Category.jsp").forward(request, response);
                                return;
                            }

                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String statusParam = request.getParameter("status");
                            String code = request.getParameter("code");
                            String parentIDRaw = request.getParameter("parentID");

                            List<String> validationErrors = new ArrayList<>();

                            if (categoryName == null || categoryName.trim().isEmpty()) {
                                validationErrors.add("Category name cannot be empty.");
                            }

                            if (code == null || code.trim().isEmpty()) {
                                validationErrors.add("Category code cannot be empty.");
                            }

                            String normalizedStatus = normalizeStatus(statusParam);
                            if (normalizedStatus == null) {
                                validationErrors.add("Status is invalid. Please choose 'active' or 'inactive'.");
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                try {
                                    parentId = Integer.valueOf(parentIDRaw.trim());
                                    if (parentId == categoryId) {
                                        validationErrors.add("A category cannot be its own parent category.");
                                    } else if (categoryDAO.getCategoryById(parentId) == null) {
                                        validationErrors.add("Selected parent category does not exist."); 
                                    }
                                } catch (NumberFormatException ex) {
                                    validationErrors.add("Parent category ID is invalid.");
                                }
                            }

                            if (categoryName != null && categoryDAO.isCategoryNameExists(categoryName.trim(), categoryId)) {
                                validationErrors.add("Category name '" + categoryName.trim() + "' already exists.");
                            }

                            if (code != null && categoryDAO.isCategoryCodeExists(code.trim(), categoryId)) {
                                validationErrors.add("Category code '" + code.trim() + "' already exists.");
                            }

                            if (!validationErrors.isEmpty()) {
                                existingCategory.setCategoryName(categoryName);
                                existingCategory.setCategoryCode(code);
                                existingCategory.setDescription(description);
                                existingCategory.setStatus(statusParam);
                                existingCategory.setParentId(parentId);

                                request.setAttribute("error", String.join("<br/>", validationErrors));
                                request.setAttribute("c", existingCategory);
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                                return;
                            }

                            Category category = new Category();
                            category.setCategoryId(categoryId);
                            category.setCategoryName(categoryName.trim());
                            category.setCategoryCode(code.trim());
                            category.setDescription(description != null ? description.trim() : null);
                            category.setStatus(normalizedStatus);
                            category.setParentId(parentId);

                            boolean updated = categoryDAO.updateCategory(category);
                            if (updated) {
                                response.sendRedirect("Category?service=listCategory");
                            } else {
                                String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to update category. Error details: " + daoError);
                                request.setAttribute("c", existingCategory);
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while updating category: " + e.getMessage());
                            request.setAttribute("c", categoryDAO.getCategoryById(Integer.parseInt(request.getParameter("categoryID"))));
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/UpdateCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "deleteCategory": {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    if (!PermissionHelper.hasPermission(currentUser, "Xóa danh mục")) {
                        request.setAttribute("error", "You do not have permission to delete categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String categoryIdRaw = request.getParameter("category_id");
                    if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                        sendErrorResponse(response, "Missing category ID.");
                        return;
                    }
                    try {
                        int categoryId = Integer.parseInt(categoryIdRaw);
                        Category category = categoryDAO.getCategoryById(categoryId);
                        if (category == null) {
                            request.setAttribute("error", "No category found with ID: " + categoryId);
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                            return;
                        }
                        List<Category> childCategories = categoryDAO.getAllCategories().stream()
                                .filter(c -> c.getParent_id() != null && c.getParent_id() == categoryId)
                                .collect(Collectors.toList());
                        if (!childCategories.isEmpty()) {
                            request.setAttribute("error", "Cannot delete category due to dependent subcategories.");
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                            return;
                        }
                        boolean deleted = categoryDAO.deleteCategory(categoryId);
                        if (deleted) {
                            response.sendRedirect("Category?service=listCategory");
                        } else {
                            String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                            request.setAttribute("error", "Unable to delete category. Error details: " + daoError);
                            request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        }
                    } catch (NumberFormatException e) {
                        sendErrorResponse(response, "Invalid category ID.");
                    } catch (Exception e) {
                        sendErrorResponse(response, "Error while deleting category: " + e.getMessage());
                    }
                    break;
                }

                case "addCategory": {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    if (!PermissionHelper.hasPermission(currentUser, "Tạo danh mục")) {
                        request.setAttribute("error", "You do not have permission to create categories.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String submit = request.getParameter("submit");
                    if (submit == null) {
                        int maxNum = categoryDAO.getMaxCategoryNumber();
                        String newCategoryCode = "CAT" + (maxNum + 1);
                        request.setAttribute("categoryCode", newCategoryCode);
                        request.setAttribute("categories", categoryDAO.getAllCategories());
                        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                        request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                    } else {
                        try {
                            String categoryName = request.getParameter("categoryName");
                            String description = request.getParameter("description");
                            String status = request.getParameter("status");
                            String code = request.getParameter("code");
                            String parentIDRaw = request.getParameter("parentID");

                            List<String> validationErrors = new ArrayList<>();

                            if (categoryName == null || categoryName.trim().isEmpty()) {
                                validationErrors.add("Category name cannot be empty.");
                            }

                            if (code == null || code.trim().isEmpty()) {
                                validationErrors.add("Category code cannot be empty.");
                            }

                            String normalizedStatus = normalizeStatus(status);
                            if (normalizedStatus == null) {
                                validationErrors.add("Status is invalid. Please choose 'active' hoặc 'inactive'.");
                            }

                            Integer parentId = null;
                            if (parentIDRaw != null && !parentIDRaw.trim().isEmpty()) {
                                try {
                                    parentId = Integer.valueOf(parentIDRaw.trim());
                                    if (categoryDAO.getCategoryById(parentId) == null) {
                                        validationErrors.add("Parent category does not exist.");
                                    }
                                } catch (NumberFormatException ex) {
                                    validationErrors.add("Parent category ID is invalid.");
                                }
                            }

                            if (categoryName != null && categoryDAO.isCategoryNameExists(categoryName.trim(), null)) {
                                validationErrors.add("Category name '" + categoryName.trim() + "' already exists.");
                            }

                            if (code != null && categoryDAO.isCategoryCodeExists(code.trim(), null)) {
                                validationErrors.add("Category code '" + code.trim() + "' already exists.");
                            }

                            if (!validationErrors.isEmpty()) {
                                request.setAttribute("error", String.join("<br/>", validationErrors));
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.setAttribute("enteredCategoryCode", code);
                                request.setAttribute("enteredCategoryName", categoryName);
                                request.setAttribute("enteredDescription", description);
                                request.setAttribute("enteredStatus", status);
                                request.setAttribute("enteredParentID", parentIDRaw);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                                return;
                            }

                            Category category = new Category();
                            category.setCategoryName(categoryName.trim());
                            category.setCategoryCode(code.trim());
                            category.setDescription(description != null ? description.trim() : null);
                            category.setStatus(normalizedStatus != null ? normalizedStatus : "active");
                            category.setParentId(parentId);

                            boolean inserted = categoryDAO.insertCategory(category);
                            if (inserted) {
                                response.sendRedirect("Category?service=listCategory");
                            } else {
                                String daoError = categoryDAO.getLastError() != null ? categoryDAO.getLastError() : "No detailed error from CategoryDAO.";
                                request.setAttribute("error", "Unable to add category. Error details: " + daoError);
                                request.setAttribute("categories", categoryDAO.getAllCategories());
                                request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                                request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                            }
                        } catch (Exception e) {
                            request.setAttribute("error", "Error while adding category: " + e.getMessage());
                            request.setAttribute("categories", categoryDAO.getAllCategories());
                            request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                            request.getRequestDispatcher("/InsertCategory.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "listCategory": {
                    // Admin có toàn quyền - PermissionHelper đã xử lý
                    if (!PermissionHelper.hasPermission(currentUser, "DS danh mục")) {
                        request.setAttribute("error", "Bạn không có quyền xem danh sách danh mục.");
                        request.getRequestDispatcher("/Category.jsp").forward(request, response);
                        return;
                    }
                    String name = request.getParameter("name");
                    String status = request.getParameter("status");
                    String sortBy = request.getParameter("sortBy");
                    int currentPage = 1;
                    String pageStr = request.getParameter("page");
                    try {
                        if (pageStr != null && !pageStr.trim().isEmpty()) {
                            currentPage = Integer.parseInt(pageStr);
                            if (currentPage < 1) currentPage = 1;
                        }
                    } catch (NumberFormatException e) {
                    }

                    String sortCol = null;
                    String sortOrder = null;
                    if (sortBy != null && !sortBy.isEmpty()) {
                        if (sortBy.endsWith("_asc")) {
                            sortCol = sortBy.replace("_asc", "");
                            sortOrder = "asc";
                        } else if (sortBy.endsWith("_desc")) {
                            sortCol = sortBy.replace("_desc", "");
                            sortOrder = "desc";
                        }
                    }

                    List<Category> filteredList = categoryDAO.searchCategories(name, status, sortCol, sortOrder);
                    int totalCategories = filteredList.size();
                    int totalPages = (int) Math.ceil((double) totalCategories / PAGE_SIZE);
                    if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
                    int start = (currentPage - 1) * PAGE_SIZE;
                    int end = Math.min(start + PAGE_SIZE, filteredList.size());
                    List<Category> paginatedList = (start < filteredList.size()) ? filteredList.subList(start, end) : Collections.emptyList();

                    request.setAttribute("data", paginatedList);
                    request.setAttribute("currentPage", currentPage);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("name", name);
                    request.setAttribute("status", status);
                    request.setAttribute("sortBy", sortBy);
                    request.setAttribute("rolePermissionDAO", rolePermissionDAO);
                    request.setAttribute("categoryDAO", categoryDAO);
                    request.setAttribute("categories", categoryDAO.getAllCategories());
                    request.getRequestDispatcher("/Category.jsp").forward(request, response);
                    break;
                }

                default:
                    sendErrorResponse(response, "Unsupported service: " + service);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/Category.jsp").forward(request, response);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"" + errorMessage + "\"}");
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Category management servlet";
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }
        String normalized = status.trim().toLowerCase();
        if ("active".equals(normalized)) {
            return "active";
        }
        if ("inactive".equals(normalized)) {
            return "inactive";
        }
        return null;
    }
}