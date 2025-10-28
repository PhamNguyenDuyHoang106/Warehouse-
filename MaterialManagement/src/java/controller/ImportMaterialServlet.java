package controller;

import dal.ImportDAO;
import dal.ImportDetailDAO;
import dal.MaterialDAO;
import dal.SupplierDAO;
import dal.WarehouseRackDAO;
import entity.Import;
import entity.ImportDetail;
import entity.Material;
import entity.Supplier;
import entity.User;
import entity.WarehouseRack;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ImportValidator;

@WebServlet(name = "ImportMaterialServlet", urlPatterns = {"/ImportMaterial"})
public class ImportMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ImportMaterialServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        ImportDAO importDAO = null;
        MaterialDAO materialDAO = null;
        SupplierDAO supplierDAO = null;
        WarehouseRackDAO rackDAO = null;

        try {
            importDAO = new ImportDAO();
            materialDAO = new MaterialDAO();
            supplierDAO = new SupplierDAO();
            rackDAO = new WarehouseRackDAO();

            // Generate next import code
            String nextImportCode = importDAO.generateNextImportCode();
            request.setAttribute("nextImportCode", nextImportCode);

            // Get all materials for autocomplete
            List<Material> materials = materialDAO.getAllProducts();
            request.setAttribute("materials", materials);

            // Get all suppliers
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            request.setAttribute("suppliers", suppliers);

            // Get active warehouse racks
            List<WarehouseRack> racks = rackDAO.getAllRacks();
            request.setAttribute("racks", racks);

            request.getRequestDispatcher("ImportMaterialForm.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for ImportMaterialServlet", e);
            request.setAttribute("error", "Error loading import form: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (importDAO != null) importDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (supplierDAO != null) supplierDAO.close();
            if (rackDAO != null) rackDAO.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        // Read form data
        String importCode = request.getParameter("importCode");
        String supplierIdStr = request.getParameter("supplierId");
        String importDateStr = request.getParameter("importDate");
        String note = request.getParameter("note");

        String[] materialIds = request.getParameterValues("materialId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] unitPrices = request.getParameterValues("unitPrice[]");
        String[] rackIds = request.getParameterValues("rackId[]");

        Map<String, String> errors = new HashMap<>();

        // Validate import code
        String importCodeError = ImportValidator.validateImportCode(importCode);
        if (importCodeError != null) {
            errors.put("importCode", importCodeError);
        }

        // Validate supplier (optional)
        String supplierError = ImportValidator.validateSupplierId(supplierIdStr);
        if (supplierError != null) {
            errors.put("supplierId", supplierError);
        }

        // Validate import details
        Map<String, String> detailErrors = ImportValidator.validateImportDetails(
                materialIds, quantities, unitPrices, rackIds);
        errors.putAll(detailErrors);

        // Check for duplicate materials
        if (materialIds != null && materialIds.length > 0) {
            Map<String, String> duplicateErrors = ImportValidator.checkDuplicateMaterials(materialIds);
            errors.putAll(duplicateErrors);
        }

        // If there are errors, go back to form
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("submittedImportCode", importCode);
            request.setAttribute("submittedSupplierId", supplierIdStr);
            request.setAttribute("submittedImportDate", importDateStr);
            request.setAttribute("submittedNote", note);
            request.setAttribute("submittedMaterialIds", materialIds);
            request.setAttribute("submittedQuantities", quantities);
            request.setAttribute("submittedUnitPrices", unitPrices);
            request.setAttribute("submittedRackIds", rackIds);
            doGet(request, response);
            return;
        }

        // Process import
        ImportDAO importDAO = null;
        ImportDetailDAO detailDAO = null;

        try {
            importDAO = new ImportDAO();
            detailDAO = new ImportDetailDAO();

            // Create Import record
            Import importObj = new Import();
            importObj.setImportCode(importCode);
            importObj.setImportedBy(user.getUserId());

            if (supplierIdStr != null && !supplierIdStr.trim().isEmpty()) {
                importObj.setSupplierId(Integer.parseInt(supplierIdStr));
            }

            // Parse import date or use current time
            if (importDateStr != null && !importDateStr.trim().isEmpty()) {
                importObj.setImportDate(LocalDateTime.parse(importDateStr + "T00:00:00"));
            } else {
                importObj.setImportDate(LocalDateTime.now());
            }

            importObj.setNote(note);

            int importId = importDAO.createImport(importObj);

            if (importId > 0) {
                // Create Import_Details
                boolean allDetailsAdded = true;
                for (int i = 0; i < materialIds.length; i++) {
                    ImportDetail detail = new ImportDetail();
                    detail.setImportId(importId);
                    detail.setMaterialId(Integer.parseInt(materialIds[i]));

                    // Rack ID is optional
                    if (rackIds != null && i < rackIds.length && 
                        rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                        detail.setRackId(Integer.parseInt(rackIds[i]));
                    }

                    detail.setQuantity(new BigDecimal(quantities[i]));
                    detail.setUnitPrice(new BigDecimal(unitPrices[i]));
                    detail.setStatus("imported");

                    boolean added = detailDAO.addImportDetail(detail);
                    if (!added) {
                        allDetailsAdded = false;
                        LOGGER.log(Level.WARNING, "Failed to add import detail for material ID: " + materialIds[i]);
                    }
                }

                if (allDetailsAdded) {
                    response.sendRedirect("ImportList?success=Import created successfully");
                } else {
                    response.sendRedirect("ImportList?warning=Import created but some details failed");
                }
            } else {
                request.setAttribute("error", "Failed to create import record");
                doGet(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing import", e);
            request.setAttribute("error", "Error processing import: " + e.getMessage());
            request.setAttribute("submittedImportCode", importCode);
            request.setAttribute("submittedSupplierId", supplierIdStr);
            request.setAttribute("submittedImportDate", importDateStr);
            request.setAttribute("submittedNote", note);
            request.setAttribute("submittedMaterialIds", materialIds);
            request.setAttribute("submittedQuantities", quantities);
            request.setAttribute("submittedUnitPrices", unitPrices);
            request.setAttribute("submittedRackIds", rackIds);
            doGet(request, response);
        } finally {
            if (importDAO != null) importDAO.close();
            if (detailDAO != null) detailDAO.close();
        }
    }
}
