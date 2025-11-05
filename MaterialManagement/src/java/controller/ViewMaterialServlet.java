package controller;

import dal.InventoryDAO;
import dal.MaterialDAO;
import dal.MaterialBatchDAO;
import entity.Inventory;
import entity.Material;
import entity.MaterialBatch;
import entity.MaterialDetails;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ViewMaterialServlet", urlPatterns = {"/viewmaterial"})
public class ViewMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ViewMaterialServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String materialIdStr = request.getParameter("materialId");
        
        if (materialIdStr == null || materialIdStr.isEmpty()) {
            response.sendRedirect("dashboardmaterial");
            return;
        }

        MaterialDAO materialDAO = null;
        InventoryDAO inventoryDAO = null;
        MaterialBatchDAO batchDAO = null;

        try {
            int materialId = Integer.parseInt(materialIdStr);
            
            materialDAO = new MaterialDAO();
            inventoryDAO = new InventoryDAO();
            batchDAO = new MaterialBatchDAO();

            // Get material info
            Material material = materialDAO.getInformation(materialId);

            if (material == null) {
                request.setAttribute("error", "Material not found");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // Get all racks containing this material (V8 - one material can be in multiple racks)
            List<Inventory> inventoryByRacks = inventoryDAO.getInventoryByMaterialId(materialId);

            // Get all batches for this material (FIFO tracking - batches can have different prices)
            List<MaterialBatch> batches = batchDAO.getBatchesByMaterial(materialId);

            // Calculate total stock from all racks
            java.math.BigDecimal totalStock = java.math.BigDecimal.ZERO;
            for (Inventory inv : inventoryByRacks) {
                if (inv.getStock() != null) {
                    totalStock = totalStock.add(inv.getStock());
                }
            }

            // Calculate batch totals (for display in JSP)
            java.math.BigDecimal totalQuantityIn = java.math.BigDecimal.ZERO;
            java.math.BigDecimal totalQuantityRemaining = java.math.BigDecimal.ZERO;
            for (MaterialBatch batch : batches) {
                if (batch.getQuantityIn() != null) {
                    totalQuantityIn = totalQuantityIn.add(batch.getQuantityIn());
                }
                if (batch.getQuantityRemaining() != null) {
                    totalQuantityRemaining = totalQuantityRemaining.add(batch.getQuantityRemaining());
                }
            }

            request.setAttribute("m", material);
            request.setAttribute("inventoryByRacks", inventoryByRacks);
            request.setAttribute("batches", batches);
            request.setAttribute("totalStock", totalStock);
            request.setAttribute("totalBatchQuantityIn", totalQuantityIn);
            request.setAttribute("totalBatchQuantityRemaining", totalQuantityRemaining);
            
            request.getRequestDispatcher("ViewMaterial.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid material ID: " + materialIdStr, e);
            request.setAttribute("error", "Invalid material ID");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error in ViewMaterialServlet", ex);
            request.setAttribute("error", "Error loading material details: " + ex.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (materialDAO != null) materialDAO.close();
            if (inventoryDAO != null) inventoryDAO.close();
            if (batchDAO != null) batchDAO.close();
        }
    }

}
