<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="Login.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Create Repair Request</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

        <style>
            .repair-form .form-control, .repair-form .form-select {
                height: 48px;
                font-size: 1rem;
            }
            .repair-form .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .repair-form .btn {
                font-size: 1rem;
                padding: 0.75rem 1.25rem;
            }
            .repair-form .material-row {
                margin-bottom: 1rem;
                border-bottom: 1px solid #dee2e6;
                padding-bottom: 1rem;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp"/>

        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-md-3 col-lg-2 bg-light p-0">
                    <jsp:include page="SidebarEmployee.jsp" />
                </div>
                <!-- Page Content -->
                <div class="col-md-9 col-lg-10">
                    <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                        <div class="container">
                            <div class="row my-5 py-5">
                                <div class="col-12 bg-white p-4 rounded shadow repair-form">
                                    <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Repair Request</span></h2>

                                    <c:if test="${not empty errorMessage}">
                                        <div class="alert alert-danger">${errorMessage}</div>
                                    </c:if>
                                    <c:if test="${not empty errors}">
                                        <div class="alert alert-danger" style="margin-bottom: 16px;">
                                            <ul style="margin-bottom: 0;">
                                                <c:forEach var="error" items="${errors}">
                                                    <li>${error.value}</li>
                                                    </c:forEach>
                                            </ul>
                                        </div>
                                    </c:if>

                                    <form action="repairrequest" method="post" id="repairForm">
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label class="form-label text-muted">Request Code</label>
                                                <input type="text" class="form-control" name="requestCode" value="${requestCode}" readonly>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label text-muted">Request Date</label>
                                                <input type="text" class="form-control" name="requestDate" value="${requestDate}" readonly>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label text-muted">Repair Reason</label>
                                                <textarea class="form-control" name="reason" rows="3" placeholder="Please describe the reason for repair request...">${submittedReason}</textarea>
                                            </div>
                                        </div>
                                        <h3 class="fw-normal mt-5 mb-3">Materials for Repair</h3>
                                        <div id="materialList">

                                            <div class="row material-row align-items-end gy-3">
                                                <div class="col-md-3">
                                                    <label class="form-label text-muted">Material</label>
                                                    <input type="text" class="form-control material-autocomplete" name="materialName[]" placeholder="Type material name or code" autocomplete="off">
                                                    <c:if test="${not empty errors.material_0}">
                                                        <div class="text-danger small mt-1">${errors.material_0}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-2">
                                                    <label class="form-label text-muted">Quantity</label>
                                                    <input type="number" class="form-control" name="quantity[]" min="1" step="1" oninput="this.value = this.value.replace(/[^0-9]/g, '')" placeholder="Enter quantity" value="1">
                                                    <c:if test="${not empty errors.quantity_0}">
                                                        <div class="text-danger small mt-1">${errors.quantity_0}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-4">
                                                    <label class="form-label text-muted">Damage Description</label>
                                                    <input type="text" class="form-control" name="damageDescription[]" placeholder="Describe the damage">
                                                    <c:if test="${not empty errors.damageDescription_0}">
                                                        <div class="text-danger small mt-1">${errors.damageDescription_0}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-2">
                                                    <label class="form-label text-muted">Repairer</label>
                                                    <select class="form-select" name="supplierId">
                                                        <option value="" selected>Select repairer</option>
                                                        <c:forEach var="supplier" items="${supplierList}">
                                                            <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <c:if test="${not empty errors.supplierId}">
                                                        <div class="text-danger small mt-1">${errors.supplierId}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-1 d-flex align-items-end justify-content-center">
                                                    <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                                </div>
                                            </div>

                                            <div class="mt-3">
                                                <button type="button" class="btn btn-outline-secondary" id="addMaterial">Add Material</button>
                                            </div>
                                            <div class="mt-5 d-grid gap-2">
                                                <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                                                <a href="home" class="btn btn-outline-secondary btn-lg rounded-1">Back to Home</a>
                                            </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Autocomplete for material names
            const availableMaterials = [
            <c:forEach var="m" items="${materialList}" varStatus="loop">
            "${fn:escapeXml(m.materialName)} (damaged)"${loop.last ? '' : ','}
            </c:forEach>
            ];

            function setupAutocomplete(input) {
                $(input).autocomplete({
                    source: availableMaterials,
                    minLength: 1
                });
            }

            // Apply autocomplete to existing material inputs
            document.querySelectorAll('.material-autocomplete').forEach(setupAutocomplete);

            // Add new material row
            document.getElementById('addMaterial').addEventListener('click', function () {
                const materialList = document.getElementById('materialList');
                const newRow = document.createElement('div');
                newRow.className = 'row material-row align-items-center gy-2';
                newRow.innerHTML = `
                    <div class="col-md-3">
                        <label class="form-label text-muted">Material</label>
                        <input type="text" class="form-control material-autocomplete" name="materialName[]" placeholder="Type material name or code" autocomplete="off">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label text-muted">Quantity</label>
                        <input type="number" class="form-control" name="quantity[]" min="1" step="1" oninput="this.value = this.value.replace(/[^0-9]/g, '')" placeholder="Enter quantity" value="1">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label text-muted">Damage Description</label>
                        <input type="text" class="form-control" name="damageDescription[]" placeholder="Describe the damage">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label text-muted">Repairer</label>
                        <select class="form-select" name="supplierId">
                            <option value="" selected>Select repairer</option>
            <c:forEach var="supplier" items="${supplierList}">
                                <option value="${supplier.supplierId}">${supplier.supplierName}</option>
            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-1 d-flex align-items-center">
                        <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                    </div>
                `;
                materialList.appendChild(newRow);
                setupAutocomplete(newRow.querySelector('.material-autocomplete'));
            });

            // Remove material row
            document.addEventListener('click', function (e) {
                const removeBtn = e.target.closest('.remove-material');
                if (removeBtn) {
                    const rows = document.querySelectorAll('.material-row');
                    if (rows.length > 1) {
                        removeBtn.closest('.material-row').remove();
                    }
                }
            });

            // Restore submitted data if there were validation errors
            <c:if test="${not empty submittedMaterialNames}">
            const submittedMaterialNames = [
                <c:forEach var="materialName" items="${submittedMaterialNames}" varStatus="status">
            "${fn:escapeXml(materialName)}"<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];
            const submittedQuantities = [
                <c:forEach var="quantity" items="${submittedQuantities}" varStatus="status">
            "${fn:escapeXml(quantity)}"<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];
            const submittedDamageDescriptions = [
                <c:forEach var="damageDescription" items="${submittedDamageDescriptions}" varStatus="status">
            "${fn:escapeXml(damageDescription)}"<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];

            // Restore data to existing rows
            const materialList = document.getElementById('materialList');
            const existingRows = materialList.querySelectorAll('.material-row');

            for (let i = 0; i < submittedMaterialNames.length; i++) {
                if (i < existingRows.length) {
                    // Use existing row
                    const row = existingRows[i];
                    row.querySelector('input[name="materialName[]"]').value = submittedMaterialNames[i];
                    row.querySelector('input[name="quantity[]"]').value = submittedQuantities[i];
                    row.querySelector('input[name="damageDescription[]"]').value = submittedDamageDescriptions[i];
                } else {
                    // Create new rows if needed
                    const newRow = document.createElement('div');
                    newRow.className = 'row material-row align-items-center gy-2';
                    newRow.innerHTML = `
                            <div class="col-md-3">
                                <label class="form-label text-muted">Material</label>
                                <input type="text" class="form-control material-autocomplete" name="materialName[]" placeholder="Type material name or code" autocomplete="off">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Quantity</label>
                                <input type="number" class="form-control" name="quantity[]" min="1" step="1" oninput="this.value = this.value.replace(/[^0-9]/g, '')" placeholder="Enter quantity" value="1">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label text-muted">Damage Description</label>
                                <input type="text" class="form-control" name="damageDescription[]" placeholder="Describe the damage">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label text-muted">Repairer</label>
                                <select class="form-select" name="supplierId">
                                    <option value="" selected>Select repairer</option>
                <c:forEach var="supplier" items="${supplierList}">
                                        <option value="${supplier.supplierId}">${supplier.supplierName}</option>
                </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-1 d-flex align-items-center">
                                <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                            </div>
                        `;
                    materialList.appendChild(newRow);

                    // Set values
                    newRow.querySelector('input[name="materialName[]"]').value = submittedMaterialNames[i];
                    newRow.querySelector('input[name="quantity[]"]').value = submittedQuantities[i];
                    newRow.querySelector('input[name="damageDescription[]"]').value = submittedDamageDescriptions[i];

                    // Setup autocomplete
                    setupAutocomplete(newRow.querySelector('.material-autocomplete'));
                }
            }
            </c:if>

        </script>
    </body>
</html>