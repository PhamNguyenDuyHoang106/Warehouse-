package controller;

import dal.MaterialDAO;
import dal.RolePermissionDAO;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "DeleteMaterialServlet", urlPatterns = {"/deletematerial"})
public class DeleteMaterialServlet extends HttpServlet {

    private RolePermissionDAO rolePermissionDAO;

    @Override
    public void init() throws ServletException {
        rolePermissionDAO = new RolePermissionDAO();
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
        if (roleId != 1 && !rolePermissionDAO.hasPermission(roleId, "DELETE_MATERIAL")) {
            request.setAttribute("error", "You don't have permission to delete materials.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            String materialId = request.getParameter("materialId");
            System.out.println("🛠️ [DeleteMaterialServlet] Form Parameter:");
            System.out.println("materialId: " + materialId);

            if (materialId == null || materialId.trim().isEmpty()) {
                System.out.println("[DeleteMaterialServlet] Error: Material ID is missing.");
                request.setAttribute("error", "Invalid material ID.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            MaterialDAO materialDAO = new MaterialDAO();
            boolean exists = materialDAO.getInformation(Integer.parseInt(materialId)) != null;

            if (!exists) {
                System.out.println("[DeleteMaterialServlet] Error: Material does not exist.");
                request.setAttribute("error", "Material does not exist.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            System.out.println("[DeleteMaterialServlet] Deleting material from database...");
            boolean deleteSuccess = materialDAO.deleteMaterial(Integer.parseInt(materialId));
            
            if (deleteSuccess) {
                System.out.println("[DeleteMaterialServlet] Material deleted successfully, redirecting to dashboardmaterial");
                response.sendRedirect("dashboardmaterial?success=Material deleted successfully");
            } else {
                System.out.println("[DeleteMaterialServlet] Cannot delete material - stock quantity > 0");
                request.setAttribute("error", "Cannot delete material. This material still has stock (quantity > 0).");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            System.out.println("DeleteMaterialServlet] Error: " + ex.getMessage());
            ex.printStackTrace();
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