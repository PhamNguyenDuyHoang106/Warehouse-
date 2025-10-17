<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Waggy - Create Export Request</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- Bootstrap & Fonts -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/vendor.css">
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="css/date-validation.css">
    <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">

    <style>
        .export-form .form-control, .export-form .form-select {
            height: 48px;
            font-size: 1rem;
            font-family: 'Roboto', Arial, sans-serif;
        }
        .export-form .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .export-form .btn {
            font-size: 1rem;
            padding: 0.75rem 1.25rem;
        }
        .export-form .material-row {
            margin-bottom: 1rem;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 1rem;
        }
        #reason, textarea {
            font-family: 'Roboto', Arial, sans-serif;
            height: 40px;
            padding: 6px 12px;
            resize: vertical;
             align-items: center;
            justify-content: center;
            padding-top: 32px; 
        }
        /* Giao diện autocomplete giống ExportMaterial.jsp */
        .ui-autocomplete {
            max-height: 200px;
            overflow-y: auto;
            overflow-x: hidden;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            background-color: #fff;
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
            z-index: 1000;
        }
        .ui-menu-item {
            padding: 8px 12px;
            font-size: 1rem;
            cursor: pointer;
        }
        .ui-menu-item:hover {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>
<jsp:include page="Header.jsp" />
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3 col-lg-2 bg-light p-0">
            <jsp:include page="SidebarEmployee.jsp" />
        </div>
        <div class="col-md-9 col-lg-10">
            <section id="create-request" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                <div class="container">
                    <div class="row my-5 py-5">
                        <div class="col-12 bg-white p-4 rounded shadow export-form">
                            <c:if test="${not hasCreateExportRequestPermission}">
                                <div class="alert alert-danger">You do not have permission to create export requests.</div>
                                <div class="text-center mt-3">
                                    <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                                </div>
                            </c:if>
                            <c:if test="${hasCreateExportRequestPermission}">
                                <h2 class="display-4 fw-normal text-center mb-4">Create <span class="text-primary">Export Request</span></h2>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger">${error}</div>
                                </c:if>
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success text-center" style="font-size:1.1rem; font-weight:600;">${success}</div>
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

                                <form action="CreateExportRequest" method="post">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label for="requestCode" class="form-label text-muted">Request Code</label>
                                            <input type="text" class="form-control" id="requestCode" name="requestCode" value="${requestCode}" readonly>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="deliveryDate" class="form-label text-muted">Delivery Date</label>
                                            <input type="date" class="form-control" id="deliveryDate" name="deliveryDate" value="${submittedDeliveryDate}">
                                            <c:if test="${not empty errors.deliveryDate}">
                                                <div class="text-danger small mt-1">${errors.deliveryDate}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12">
                                            <label for="reason" class="form-label text-muted">Reason</label>
                                            <textarea class="form-control" id="reason" name="reason" rows="1" style="height: 40px; padding: 6px 12px; resize: vertical;">${submittedReason}</textarea>
                                            <c:if test="${not empty errors.reason}">
                                                <div class="text-danger small mt-1">${errors.reason}</div>
                                            </c:if>
                                        </div>
                                    </div>

                                    <h3 class="fw-normal mt-5 mb-3">Materials</h3>
                                    <div id="materialList">
                                        <div class="row material-row align-items-center gy-2">
                                            <div class="col-md-3">
                                                <label class="form-label text-muted">Material</label>
                                                <input type="text" class="form-control material-name-input" name="materialNames[]" placeholder="Type material name or code" autocomplete="off" data-touched="false">
                                                <input type="hidden" name="materials[]" class="material-id-input">
                                                <div class="invalid-feedback" style="display:none;">Please enter a valid material name or code.</div>
                                            </div>
                                            <div class="col-md-2">
                                                <label class="form-label text-muted">In Stock</label>
                                                <input type="text" class="form-control stock-quantity" readonly value="0">
                                            </div>
                                            <div class="col-md-2">
                                                <label class="form-label text-muted">Quantity</label>
                                                <input type="number" class="form-control quantity-input" name="quantities[]" min="1" data-touched="false">
                                                <div class="invalid-feedback" style="display:none;">Not enough stock!</div>
                                            </div>
                                            <div class="col-md-2" style="display:none;">
                                                <label class="form-label text-muted">Condition</label>
                                                <select class="form-select" name="conditions[]" >
                                                    <option value="new">New</option>
                                                    <option value="used">Used</option>
                                                    <option value="refurbished">Refurbished</option>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <img src="images/placeholder.png" class="img-fluid rounded material-image" style="height: 80px; width: 100%; object-fit: cover;" alt="Material Image">
                                            </div>
                                            <div class="col-md-1 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mt-3">
                                        <button type="button" class="btn btn-outline-secondary" id="addMaterial">Add Material</button>
                                    </div>

                                    <div class="mt-5 d-grid gap-2">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1">Submit Request</button>
                                        <a href="dashboardmaterial" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                                    </div>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>
<jsp:include page="Footer.jsp" />

<script src="js/jquery-1.11.0.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/date-validation.js"></script>
<script>
var materialsData = [];
<c:forEach var="material" items="${materials}">
materialsData.push({
    label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
    value: "${fn:escapeXml(material.materialName)}",
    id: "${material.materialId}",
    name: "${fn:escapeXml(material.materialName)}",
    code: "${fn:escapeXml(material.materialCode)}",
    imageUrl: "${fn:escapeXml(material.materialsUrl)}",
    stock: ${material.quantity}
});
</c:forEach>



function updateMaterialRowAutocomplete(row) {
    const nameInput = row.querySelector('.material-name-input');
    const idInput = row.querySelector('.material-id-input');
    const img = row.querySelector('.material-image');
    const stockInput = row.querySelector('.stock-quantity');
    const quantityInput = row.querySelector('.quantity-input');
    $(nameInput).autocomplete({
        source: function(request, response) {
            const term = request.term.toLowerCase();
            const matches = materialsData.filter(material => 
                material.name.toLowerCase().includes(term) || 
                material.code.toLowerCase().includes(term)
            );
            response(matches);
        },
        select: function(event, ui) {
            idInput.value = ui.item.id;
            nameInput.value = ui.item.name;
            nameInput.classList.remove('is-invalid');
            // Update image and stock
            let imgUrl = ui.item.imageUrl && ui.item.imageUrl !== 'null' ? ui.item.imageUrl : '';
            if (imgUrl.startsWith('http') || imgUrl.startsWith('/') || imgUrl.startsWith('images/material/')) {
                img.src = imgUrl;
            } else if (imgUrl) {
                img.src = 'images/material/' + imgUrl;
            } else {
                img.src = 'images/material/default.jpg';
            }
            stockInput.value = ui.item.stock;
            quantityInput.max = ui.item.stock;
            validateQuantity(quantityInput);
        },
        change: function(event, ui) {
            if (!ui.item) {
                const inputValue = nameInput.value.toLowerCase().trim();
                const selectedMaterial = materialsData.find(material => 
                    material.name.toLowerCase() === inputValue || 
                    material.code.toLowerCase() === inputValue
                );
                if (selectedMaterial) {
                    idInput.value = selectedMaterial.id;
                    nameInput.value = selectedMaterial.name;
                    nameInput.classList.remove('is-invalid');
                    // Update image and stock
                    let imgUrl = selectedMaterial.imageUrl && selectedMaterial.imageUrl !== 'null' ? selectedMaterial.imageUrl : '';
                    if (imgUrl.startsWith('http') || imgUrl.startsWith('/') || imgUrl.startsWith('images/material/')) {
                        img.src = imgUrl;
                    } else if (imgUrl) {
                        img.src = 'images/material/' + imgUrl;
                    } else {
                        img.src = 'images/material/default.jpg';
                    }
                    stockInput.value = selectedMaterial.stock;
                    quantityInput.max = selectedMaterial.stock;
                    validateQuantity(quantityInput);
                } else {
                    idInput.value = '';
                    nameInput.classList.add('is-invalid');
                    img.src = 'images/material/default.jpg';
                    stockInput.value = 0;
                    quantityInput.max = null;
                }
            }
        },
        minLength: 0
    }).on('focus', function() {
        $(this).autocomplete('search', '');
    });
}

function validateMaterialNameInput(input, showError = false) {
    const invalidFeedback = input.parentElement.querySelector('.invalid-feedback');
    const touched = input.getAttribute('data-touched') === 'true';
    const value = input.value.trim();
    const idInput = input.parentElement.querySelector('.material-id-input');
    let isValid = !!idInput.value;
    if ((showError || touched) && !isValid) {
        input.classList.add('is-invalid');
        if (invalidFeedback) invalidFeedback.style.display = 'block';
    } else {
        input.classList.remove('is-invalid');
        if (invalidFeedback) invalidFeedback.style.display = 'none';
    }
    return isValid;
}

function validateQuantityInput(input, showError = false) {
    const invalidFeedback = input.parentElement.querySelector('.invalid-feedback');
    const touched = input.getAttribute('data-touched') === 'true';
    const stock = parseInt(input.max, 10);
    const quantity = parseInt(input.value, 10);
    let isValid = !isNaN(stock) && !isNaN(quantity) && quantity > 0 && quantity <= stock;
    if ((showError || touched) && !isValid) {
        input.classList.add('is-invalid');
        if (invalidFeedback) invalidFeedback.style.display = 'block';
    } else {
        input.classList.remove('is-invalid');
        if (invalidFeedback) invalidFeedback.style.display = 'none';
    }
    return isValid;
}

// Initial setup for the first row and date validation
document.addEventListener('DOMContentLoaded', function() {
    // Setup material rows
    document.querySelectorAll('.material-row').forEach(row => {
        updateMaterialRowAutocomplete(row);
        // Material name input
        const nameInput = row.querySelector('.material-name-input');
        nameInput.addEventListener('input', function() {
            nameInput.setAttribute('data-touched', 'true');
            validateMaterialNameInput(nameInput, false);
        });
        nameInput.addEventListener('blur', function() {
            nameInput.setAttribute('data-touched', 'true');
            validateMaterialNameInput(nameInput, true);
        });
        // Quantity input
        const quantityInput = row.querySelector('.quantity-input');
        quantityInput.addEventListener('input', function() {
            quantityInput.setAttribute('data-touched', 'true');
            validateQuantityInput(quantityInput, false);
        });
        quantityInput.addEventListener('blur', function() {
            quantityInput.setAttribute('data-touched', 'true');
            validateQuantityInput(quantityInput, true);
        });
    });
    // Initialize date validation
    if (window.dateValidation) {
        window.dateValidation.initDateValidation();
    }
    // Remove client-side validation - let server handle all validation
    // Form will submit directly to server for Java validation
    
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
        
        // Restore data to existing rows
        const materialList = document.getElementById('materialList');
        const existingRows = materialList.querySelectorAll('.material-row');
        
        for (let i = 0; i < submittedMaterialNames.length; i++) {
            if (i < existingRows.length) {
                // Use existing row
                const row = existingRows[i];
                row.querySelector('.material-name-input').value = submittedMaterialNames[i];
                row.querySelector('.quantity-input').value = submittedQuantities[i];
            } else {
                // Create new rows if needed
                const newRow = document.createElement('div');
                newRow.className = 'row material-row align-items-center gy-2';
                newRow.innerHTML = `
                    <div class="col-md-3">
                        <label class="form-label text-muted">Material</label>
                        <input type="text" class="form-control material-name-input" name="materialNames[]" placeholder="Type material name or code" autocomplete="off" data-touched="false">
                        <input type="hidden" name="materials[]" class="material-id-input">
                        <div class="invalid-feedback" style="display:none;">Please enter a valid material name or code.</div>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label text-muted">In Stock</label>
                        <input type="text" class="form-control stock-quantity" readonly value="0">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label text-muted">Quantity</label>
                        <input type="number" class="form-control quantity-input" name="quantities[]" min="1" data-touched="false">
                        <div class="invalid-feedback" style="display:none;">Not enough stock!</div>
                    </div>
                    <div class="col-md-2" style="display:none;">
                        <label class="form-label text-muted">Condition</label>
                        <select class="form-select" name="conditions[]" >
                            <option value="new">New</option>
                            <option value="used">Used</option>
                            <option value="refurbished">Refurbished</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <img src="images/placeholder.png" class="img-fluid rounded material-image" style="height: 80px; width: 100%; object-fit: cover;" alt="Material Image">
                    </div>
                    <div class="col-md-1 d-flex align-items-end">
                        <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                    </div>
                `;
                materialList.appendChild(newRow);
                
                // Set values
                newRow.querySelector('.material-name-input').value = submittedMaterialNames[i];
                newRow.querySelector('.quantity-input').value = submittedQuantities[i];
                
                // Setup autocomplete
                updateMaterialRowAutocomplete(newRow);
            }
        }
    </c:if>
});

// When add material row
document.getElementById('addMaterial').addEventListener('click', function () {
    const materialList = document.getElementById('materialList');
    const firstRow = materialList.querySelector('.material-row');
    const newRow = firstRow.cloneNode(true);
    // Reset fields in the new row
    newRow.querySelector('.material-name-input').value = '';
    newRow.querySelector('.material-id-input').value = '';
    newRow.querySelector('.material-name-input').classList.remove('is-invalid');
    newRow.querySelector('.stock-quantity').value = '0';
    newRow.querySelector('.quantity-input').value = '';
    newRow.querySelector('.quantity-input').classList.remove('is-invalid');
    newRow.querySelector('select[name="conditions[]"]').value = 'new';
    newRow.querySelector('.material-image').src = 'images/material/default.jpg';
    materialList.appendChild(newRow);
    updateMaterialRowAutocomplete(newRow);
});

// Validate quantity on input
document.addEventListener('input', function(e) {
    if(e.target.classList.contains('quantity-input')) {
        validateQuantity(e.target);
    }
});

// Remove material row
document.addEventListener('click', function (e) {
    if (e.target.classList.contains('remove-material')) {
        const materialRows = document.querySelectorAll('.material-row');
        if (materialRows.length > 1) {
            e.target.closest('.material-row').remove();
        }
    }
});

function validateQuantity(quantityInput) {
    const stock = parseInt(quantityInput.max, 10);
    const quantity = parseInt(quantityInput.value, 10);
    if (!isNaN(stock) && !isNaN(quantity) && quantity > stock) {
        quantityInput.classList.add('is-invalid');
    } else {
        quantityInput.classList.remove('is-invalid');
    }
}


</script>
</body>
</html>