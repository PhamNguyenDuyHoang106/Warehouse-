package controller;

import dal.ExportDAO;
import dal.ExportDetailDAO;
import dal.ExportRequestDAO;
import dal.MaterialDAO;
import dal.RecipientDAO;
import dal.VehicleDAO;
import dal.WarehouseRackDAO;
import entity.Export;
import entity.ExportRequest;
import entity.Material;
import entity.Recipient;
import entity.User;
import entity.Vehicle;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ExportMaterialServlet", urlPatterns = {"/ExportMaterial"})
public class ExportMaterialServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ExportMaterialServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        ExportDAO exportDAO = null;
        ExportRequestDAO exportRequestDAO = null;
        MaterialDAO materialDAO = null;
        RecipientDAO recipientDAO = null;
        VehicleDAO vehicleDAO = null;
        WarehouseRackDAO rackDAO = null;

        try {
            exportDAO = new ExportDAO();
            exportRequestDAO = new ExportRequestDAO();
            materialDAO = new MaterialDAO();
            recipientDAO = new RecipientDAO();
            vehicleDAO = new VehicleDAO();
            rackDAO = new WarehouseRackDAO();

            // Generate next export code
            String nextExportCode = exportDAO.generateNextExportCode();
            request.setAttribute("nextExportCode", nextExportCode);

            // Get approved export requests (that haven't been exported yet)
            List<ExportRequest> approvedRequests = exportRequestDAO.getAllRequestsByStatus("approved");
            request.setAttribute("exportRequests", approvedRequests);

            // Get all materials for autocomplete
            List<Material> materials = materialDAO.getAllProducts();
            request.setAttribute("materials", materials);

            // Get all recipients
            List<Recipient> recipients = recipientDAO.getAllRecipients();
            request.setAttribute("recipients", recipients);

            // Get available vehicles
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            request.setAttribute("vehicles", vehicles);

            // Get active warehouse racks
            List<WarehouseRack> racks = rackDAO.getAllRacks();
            request.setAttribute("racks", racks);

            request.getRequestDispatcher("ExportMaterialForm.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in doGet for ExportMaterialServlet", e);
            request.setAttribute("error", "Error loading export form: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } finally {
            if (exportDAO != null) exportDAO.close();
            if (exportRequestDAO != null) exportRequestDAO.close();
            if (materialDAO != null) materialDAO.close();
            if (recipientDAO != null) recipientDAO.close();
            if (vehicleDAO != null) vehicleDAO.close();
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
        String exportCode = request.getParameter("exportCode");
        String exportRequestIdStr = request.getParameter("exportRequestId");
        String recipientIdStr = request.getParameter("recipientId");
        String vehicleIdStr = request.getParameter("vehicleId");
        String exportDateStr = request.getParameter("exportDate");
        String note = request.getParameter("note");

        String[] materialIds = request.getParameterValues("materialId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] rackIds = request.getParameterValues("rackId[]");
        String[] notes = request.getParameterValues("materialNote[]");

        // Basic validation
        if (exportCode == null || exportCode.trim().isEmpty()) {
            request.setAttribute("error", "Export code is required");
            doGet(request, response);
            return;
        }

        if (recipientIdStr == null || recipientIdStr.trim().isEmpty()) {
            request.setAttribute("error", "Recipient is required");
            doGet(request, response);
            return;
        }

        if (materialIds == null || materialIds.length == 0) {
            request.setAttribute("error", "At least one material is required");
            doGet(request, response);
            return;
        }

        // Process export
        ExportDAO exportDAO = null;
        ExportDetailDAO detailDAO = null;

        try {
            exportDAO = new ExportDAO();
            detailDAO = new ExportDetailDAO();

            // Create Export record
            Export export = new Export();
            export.setExportCode(exportCode);
            export.setExportedBy(user.getUserId());
            export.setRecipientId(Integer.parseInt(recipientIdStr));

            if (vehicleIdStr != null && !vehicleIdStr.trim().isEmpty()) {
                export.setVehicleId(Integer.parseInt(vehicleIdStr));
            }

            if (exportRequestIdStr != null && !exportRequestIdStr.trim().isEmpty()) {
                export.setExportRequestId(Integer.parseInt(exportRequestIdStr));
            }

            // Parse export date or use current time
            if (exportDateStr != null && !exportDateStr.trim().isEmpty()) {
                export.setExportDate(LocalDateTime.parse(exportDateStr + "T00:00:00"));
            } else {
                export.setExportDate(LocalDateTime.now());
            }

            export.setNote(note);

            int exportId = exportDAO.createExport(export);

            if (exportId > 0) {
                // Create Export_Details using STORED PROCEDURE
                boolean allDetailsAdded = true;
                StringBuilder errorMessages = new StringBuilder();

                for (int i = 0; i < materialIds.length; i++) {
                    int materialId = Integer.parseInt(materialIds[i]);
                    BigDecimal quantity = new BigDecimal(quantities[i]);
                    
                    Integer rackId = null;
                    if (rackIds != null && i < rackIds.length && 
                        rackIds[i] != null && !rackIds[i].trim().isEmpty()) {
                        rackId = Integer.parseInt(rackIds[i]);
                    }

                    String materialNote = (notes != null && i < notes.length) ? notes[i] : null;

                    // Use stored procedure for inventory validation
                    boolean added = detailDAO.addExportDetailWithSP(exportId, materialId, rackId, quantity, materialNote);
                    
                    if (!added) {
                        allDetailsAdded = false;
                        errorMessages.append("Failed to export material ID: ").append(materialId)
                                    .append(" (insufficient inventory). ");
                        LOGGER.log(Level.WARNING, "Failed to add export detail for material ID: " + materialId);
                    }
                }

                if (allDetailsAdded) {
                    response.sendRedirect("ExportList?success=Export created successfully");
                } else {
                    response.sendRedirect("ExportList?warning=Export created but some items failed: " + errorMessages.toString());
                }
            } else {
                request.setAttribute("error", "Failed to create export record");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format in export data", e);
            request.setAttribute("error", "Invalid data format. Please check your inputs.");
            doGet(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing export", e);
            request.setAttribute("error", "Error processing export: " + e.getMessage());
            doGet(request, response);
        } finally {
            if (exportDAO != null) exportDAO.close();
            if (detailDAO != null) detailDAO.close();
        }
    }
}
