package controller;

import dal.SupplierDAO;
import dal.RolePermissionDAO;
import entity.Supplier;
import entity.User;
import utils.SupplierValidator;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "SupplierServlet", urlPatterns = {"/Supplier"})
public class SupplierServlet extends HttpServlet {
    private SupplierDAO supplierDAO;
    private RolePermissionDAO rolePermissionDAO;
    private static final int PAGE_SIZE = 5;

    @Override
    public void init() throws ServletException {
        supplierDAO = new SupplierDAO();
        rolePermissionDAO = new RolePermissionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int roleId = currentUser.getRoleId();
        String action = request.getParameter("action");
        if (action == null) action = "list";

        if (roleId != 1) {
            if ("list".equals(action) && !rolePermissionDAO.hasPermission(roleId, "VIEW_LIST_SUPPLIER")) {
                request.setAttribute("error", "You do not have permission to view the supplier list.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            if ("edit".equals(action) && !rolePermissionDAO.hasPermission(roleId, "UPDATE_SUPPLIER") && !rolePermissionDAO.hasPermission(roleId, "VIEW_DETAIL_SUPPLIER")) {
                request.setAttribute("error", "You do not have permission to edit or view supplier details.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            if ("delete".equals(action) && !rolePermissionDAO.hasPermission(roleId, "DELETE_SUPPLIER")) {
                request.setAttribute("error", "You do not have permission to delete suppliers.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }

        switch (action) {
            case "list":
                listSuppliers(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteSupplier(request, response);
                break;
            default:
                listSuppliers(request, response);
                break;
        }
    }

    private void listSuppliers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String sortBy = request.getParameter("sortBy");
        String pageStr = request.getParameter("page");
        int currentPage = pageStr != null ? Integer.parseInt(pageStr) : 1;
        if (currentPage < 1) currentPage = 1;

        List<Supplier> list;
        int totalSuppliers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            list = supplierDAO.searchSuppliers(keyword);
            totalSuppliers = list.size();
            request.setAttribute("keyword", keyword);
        } else {
            list = supplierDAO.getAllSuppliers();
            totalSuppliers = list.size();
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "name_asc":
                    Collections.sort(list, Comparator.comparing(Supplier::getSupplierName));
                    break;
                case "name_desc":
                    Collections.sort(list, Comparator.comparing(Supplier::getSupplierName).reversed());
                    break;
            }
            request.setAttribute("sortBy", sortBy);
        }

        int totalPages = (int) Math.ceil((double) totalSuppliers / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, list.size());
        if (start < list.size()) {
            list = list.subList(start, end);
        } else {
            list = Collections.emptyList();
        }

        request.setAttribute("supplierList", list);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("rolePermissionDAO", rolePermissionDAO); 
        request.getRequestDispatcher("/SupplierList.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Supplier supplier = supplierDAO.getSupplierByID(id);
                request.setAttribute("supplier", supplier);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid supplier ID.");
            }
        } else {
            String newSupplierCode = supplierDAO.generateNextSupplierCode();
            request.setAttribute("newSupplierCode", newSupplierCode);
        }
        request.setAttribute("rolePermissionDAO", rolePermissionDAO);
        request.getRequestDispatcher("/SupplierEdit.jsp").forward(request, response);
    }

    private void deleteSupplier(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                supplierDAO.deleteSupplier(id);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid supplier ID.");
                try {
                    request.getRequestDispatcher("/SupplierList.jsp").forward(request, response);
                } catch (ServletException | IOException ex) {
                    throw new ServletException("Error forwarding to SupplierList.jsp", ex);
                }
                return;
            }
        }
        response.sendRedirect("Supplier?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            request.setAttribute("error", "Please log in to access this page.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int roleId = currentUser.getRoleId();
        String idStr = request.getParameter("supplier_id");

        if (roleId != 1) {
            if ((idStr == null || idStr.isEmpty()) && !rolePermissionDAO.hasPermission(roleId, "CREATE_SUPPLIER")) {
                request.setAttribute("error", "You do not have permission to create suppliers.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            if (idStr != null && !idStr.isEmpty() && !rolePermissionDAO.hasPermission(roleId, "UPDATE_SUPPLIER")) {
                request.setAttribute("error", "You do not have permission to update suppliers.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }

        String code = request.getParameter("supplier_code");
        String name = request.getParameter("supplier_name");
        String contactInfo = request.getParameter("contact_info");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone_number");
        String email = request.getParameter("email");
        String desc = request.getParameter("description");
        String taxId = request.getParameter("tax_id");

        Map<String, String> errors = SupplierValidator.validateSupplierFormData(
            code, name, contactInfo, address, phone, email, taxId, desc
        );

        if (!errors.isEmpty()) {
            Supplier supplier = new Supplier();
            supplier.setSupplierCode(code != null ? code : "");
            supplier.setSupplierName(name != null ? name : "");
            supplier.setContactInfo(contactInfo != null ? contactInfo : "");
            supplier.setAddress(address != null ? address : "");
            supplier.setPhoneNumber(phone != null ? phone : "");
            supplier.setEmail(email != null ? email : "");
            supplier.setDescription(desc != null ? desc : "");
            supplier.setTaxId(taxId != null ? taxId : "");
            
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    supplier.setSupplierId(Integer.parseInt(idStr));
                            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
            }
            
            StringBuilder errorMessage = new StringBuilder();
            for (String error : errors.values()) {
                errorMessage.append(error).append(" ");
            }
            
            request.setAttribute("supplier", supplier);
            request.setAttribute("error", errorMessage.toString().trim());
            request.setAttribute("rolePermissionDAO", rolePermissionDAO); 
            try {
                request.getRequestDispatcher("/SupplierEdit.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                throw new ServletException("Error forwarding to SupplierEdit.jsp", e);
            }
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(code);
        supplier.setSupplierName(name);
        supplier.setContactInfo(contactInfo);
        supplier.setAddress(address);
        supplier.setPhoneNumber(phone);
        supplier.setEmail(email);
        supplier.setDescription(desc);
        supplier.setTaxId(taxId);

        if (idStr == null || idStr.isEmpty()) {
            supplierDAO.addSupplier(supplier);
        } else {
            try {
                int id = Integer.parseInt(idStr);
                supplier.setSupplierId(id);
                supplierDAO.updateSupplier(supplier);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid supplier ID.");
                try {
                    request.getRequestDispatcher("/SupplierList.jsp").forward(request, response);
                } catch (ServletException | IOException ex) {
                    throw new ServletException("Error forwarding to SupplierList.jsp", ex);
                }
                return;
            }
        }
        response.sendRedirect("Supplier?action=list");
    }

    @Override
    public String getServletInfo() {
        return "SupplierServlet handles supplier management operations with permission checks";
    }
}