package controller;

import dal.MaterialDAO;
import dal.RolePermissionDAO;
import entity.User;
import utils.PermissionHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "DeleteMaterialServlet", urlPatterns = {"/deletematerial"})
public class DeleteMaterialServlet extends BaseServlet {
    private static final Logger LOGGER = Logger.getLogger(DeleteMaterialServlet.class.getName());
    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        rolePermissionDAO = new RolePermissionDAO();
        registerDAO(rolePermissionDAO);
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
        // Admin có toàn quyền - PermissionHelper đã xử lý
        if (!PermissionHelper.hasPermission(user, "Xóa NVL")) {
            request.setAttribute("error", "Bạn không có quyền xóa nguyên vật liệu.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            String materialId = request.getParameter("materialId");

            if (materialId == null || materialId.trim().isEmpty()) {
                request.setAttribute("error", "Invalid material ID.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            MaterialDAO materialDAO = null;
            try {
                materialDAO = new MaterialDAO();
                boolean exists = materialDAO.getInformation(Integer.parseInt(materialId)) != null;

                if (!exists) {
                    request.setAttribute("error", "Material does not exist.");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                    return;
                }

                boolean deleteSuccess = materialDAO.deleteMaterial(Integer.parseInt(materialId));
                
                if (deleteSuccess) {
                    response.sendRedirect("dashboardmaterial?success=Material deleted successfully");
                } else {
                    request.setAttribute("error", "Cannot delete material. This material still has stock (quantity > 0).");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                }
            } finally {
                if (materialDAO != null) materialDAO.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error deleting material", ex);
            request.setAttribute("error", "An error occurred while deleting the material: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("dashboardmaterial");
    }
}