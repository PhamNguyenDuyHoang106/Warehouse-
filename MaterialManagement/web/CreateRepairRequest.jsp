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
        <title>Create Repair Request</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/vendor.css">
        <link rel="stylesheet" href="style.css">
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">

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
            .repair-form .material-image {
                height: 80px;
                width: 100%;
                object-fit: cover;
                border-radius: 12px;
                background: #f8f9fa;
                box-shadow: 0 1px 4px rgba(0,0,0,0.07);
                border: 1px solid #eee;
                display: block;
            }
            .ui-autocomplete {
                background: #fff;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                max-height: 300px;
                overflow-y: auto;
                box-shadow: 0 4px 16px rgba(0,0,0,0.08);
                z-index: 9999 !important;
                font-size: 1rem;
                padding: 4px 0;
            }
            .ui-menu-item {
                border-bottom: 1px solid #f0f0f0;
            }
            .ui-menu-item:last-child {
                border-bottom: none;
            }
            .ui-menu-item-wrapper {
                padding: 0 !important;
                cursor: pointer;
                display: flex !important;
                align-items: center;
                gap: 12px;
                padding: 8px 12px !important;
                transition: all 0.2s ease;
            }
            .ui-menu-item-wrapper:hover,
            .ui-menu-item-wrapper.ui-state-active {
                background: #f0f4fa !important;
                color: #0d6efd !important;
                border: none !important;
                margin: 0 !important;
            }
            .autocomplete-img {
                width: 45px;
                height: 45px;
                object-fit: cover;
                border-radius: 6px;
                border: 1px solid #e0e0e0;
                flex-shrink: 0;
            }
            .autocomplete-info {
                display: flex;
                flex-direction: column;
                flex-grow: 1;
                min-width: 0;
            }
            .autocomplete-name {
                font-weight: 600;
                color: #333;
                font-size: 0.95rem;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .autocomplete-code {
                font-size: 0.8rem;
                color: #6c757d;
            }
            .ui-menu-item-wrapper.ui-state-active .autocomplete-name {
                color: #0d6efd;
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

                                    <form action="CreateRepairRequest" method="post" id="repairForm">
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
                                            <div class="col-md-6">
                                                <label class="form-label text-muted">Repairer/Supplier</label>
                                                <select class="form-select" name="supplierId" required>
                                                    <option value="">Select repairer</option>
                                                    <c:forEach var="supplier" items="${supplierList}">
                                                        <option value="${supplier.supplierId}" ${submittedSupplierId == supplier.supplierId ? 'selected' : ''}>${supplier.supplierName}</option>
                                                    </c:forEach>
                                                </select>
                                                <c:if test="${not empty errors.supplierId}">
                                                    <div class="text-danger small mt-1">${errors.supplierId}</div>
                                                </c:if>
                                            </div>
                                        </div>
                                        
                                        <h3 class="fw-normal mt-5 mb-3">Materials for Repair</h3>
                                        <div id="materialList">
                                            <div class="row material-row align-items-center gy-2">
                                                <div class="col-md-3">
                                                    <label class="form-label text-muted">Material</label>
                                                    <input type="text" class="form-control material-name-input" name="materialName[]" placeholder="Type material name or code" autocomplete="off">
                                                    <input type="hidden" name="materialId[]" class="material-id-input">
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
                                                <div class="col-md-2">
                                                    <label class="form-label text-muted">Damage Description</label>
                                                    <input type="text" class="form-control" name="damageDescription[]" placeholder="Describe the damage" required>
                                                    <c:if test="${not empty errors.damageDescription_0}">
                                                        <div class="text-danger small mt-1">${errors.damageDescription_0}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-md-2">
                                                    <img class="material-image" src="${pageContext.request.contextPath}/images/material/default.jpg" alt="Material Image">
                                                </div>
                                                <div class="col-md-1 d-flex align-items-center">
                                                    <button type="button" class="btn btn-outline-danger remove-material">Remove</button>
                                                </div>
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

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            const contextPath = '${pageContext.request.contextPath}';
            function resolveMediaUrl(url) {
                if (!url || url === 'null') {
                    return `${contextPath}/images/material/default.jpg`;
                }
                if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/')) {
                    return url;
                }
                return `${contextPath}/${url}`;
            }

            // Autocomplete for material names with IDs
            const materialsData = [
            <c:forEach var="m" items="${materialList}" varStatus="loop">
            {
                label: "${fn:escapeXml(m.materialName)} (${fn:escapeXml(m.materialCode)})",
                value: "${fn:escapeXml(m.materialName)}",
                id: "${m.materialId}",
                name: "${fn:escapeXml(m.materialName)}",
                code: "${fn:escapeXml(m.materialCode)}",
                imageUrl: "${fn:escapeXml(m.materialsUrl)}"
            }${loop.last ? '' : ','}
            </c:forEach>
            ];

            function updateMaterialRowAutocomplete(row) {
                const nameInput = row.querySelector('.material-name-input');
                const idInput = row.querySelector('.material-id-input');
                const img = row.querySelector('.material-image');
                
                $(nameInput).autocomplete({
                    source: function(request, response) {
                        const term = request.term.toLowerCase();
                        let matches;

                        if (term.length === 0) {
                            // Khi chưa gõ gì: chỉ hiển thị 10 sản phẩm đầu tiên
                            matches = materialsData.slice(0, 10);
                        } else {
                            matches = materialsData.filter(material =>
                                material.name.toLowerCase().includes(term) ||
                                material.code.toLowerCase().includes(term)
                            );
                        }
                        response(matches);
                    },
                    select: function(event, ui) {
                        idInput.value = ui.item.id;
                        nameInput.value = ui.item.name;
                        img.src = resolveMediaUrl(ui.item.imageUrl);
                        return false;
                    },
                    focus: function(event, ui) {
                        event.preventDefault(); // tránh tự động ghi đè input
                        return false;
                    },
                    minLength: 0 // Cho phép hiển thị danh sách khi click (chưa gõ gì)
                }).autocomplete("instance")._renderItem = function(ul, item) {
                    // Custom render với ảnh thumbnail
                    let imgUrl = resolveMediaUrl(item.imageUrl);
                    
                    return $("<li>")
                        .append(
                            $("<div class='ui-menu-item-wrapper'>")
                                .append($("<img>").attr("src", imgUrl).addClass("autocomplete-img"))
                                .append(
                                    $("<div class='autocomplete-info'>")
                                        .append($("<div class='autocomplete-name'>").text(item.name))
                                        .append($("<div class='autocomplete-code'>").text("Code: " + item.code))
                                )
                        )
                        .appendTo(ul);
                };

                // Khi click hoặc focus, tự động hiển thị gợi ý ban đầu
                $(nameInput).on('focus click', function () {
                    $(this).autocomplete('search', '');
                });
            }

            // Apply autocomplete to existing material inputs on page load
            document.addEventListener('DOMContentLoaded', function() {
                document.querySelectorAll('.material-row').forEach(row => {
                    updateMaterialRowAutocomplete(row);
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
                        row.querySelector('.material-name-input').value = submittedMaterialNames[i];
                        row.querySelector('input[name="quantity[]"]').value = submittedQuantities[i];
                        row.querySelector('input[name="damageDescription[]"]').value = submittedDamageDescriptions[i];
                    } else {
                        // Create new rows if needed
                        const addButton = document.getElementById('addMaterial');
                        addButton.click();
                        
                        const newRow = materialList.querySelector('.material-row:last-child');
                        newRow.querySelector('.material-name-input').value = submittedMaterialNames[i];
                        newRow.querySelector('input[name="quantity[]"]').value = submittedQuantities[i];
                        newRow.querySelector('input[name="damageDescription[]"]').value = submittedDamageDescriptions[i];
                    }
                }
                </c:if>
            });

            // Add new material row
            document.getElementById('addMaterial').addEventListener('click', function () {
                const materialList = document.getElementById('materialList');
                const firstRow = materialList.querySelector('.material-row');
                const newRow = firstRow.cloneNode(true);
                
                // Clear values in new row
                newRow.querySelector('.material-name-input').value = '';
                newRow.querySelector('.material-id-input').value = '';
                newRow.querySelector('input[name="quantity[]"]').value = '1';
                newRow.querySelector('input[name="damageDescription[]"]').value = '';
                newRow.querySelector('.material-image').src = resolveMediaUrl(null);
                
                materialList.appendChild(newRow);
                updateMaterialRowAutocomplete(newRow);
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
        </script>
    </body>
</html>
