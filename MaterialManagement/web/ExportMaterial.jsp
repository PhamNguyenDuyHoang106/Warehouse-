<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.User, java.util.Map" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    User admin = (User) session.getAttribute("user");
    if (admin == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<html lang="en">
    <head>
        <title>Waggy - Export Material</title>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Chilanka&family=Montserrat:wght@300;400;500&display=swap"
              rel="stylesheet">
        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
        <style>
            .material-form .form-control, .material-form .form-select {
                height: 48px;
                font-size: 1rem;
                padding: 0.375rem 0.75rem;
                border: 1px solid #ced4da;
                border-radius: 0.25rem;
                background-color: #fff;
                transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
            }
            .material-form .form-label {
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }
            .material-form .btn {
                font-size: 1rem;
                padding: 0.75rem;
            }
            .material-form .material-img {
                max-height: 100px;
                max-width: 100px;
                object-fit: cover;
                border-radius: 5px;
            }
            .material-form table {
                border-collapse: collapse;
                width: 100%;
            }
            .material-form th, .material-form td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #dee2e6;
            }
            .material-form th {
                background-color: #f8f9fa;
            }
            .material-form .alert {
                border-radius: 8px;
                font-size: 1rem;
            }
            .material-form .form-control:focus {
                border-color: #86b7fe;
                box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
                outline: 0;
            }
            .material-form .form-control.is-invalid {
                border-color: #dc3545;
            }
            .material-form .form-control.is-invalid:focus {
                box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
            }
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
            .pagination {
                display: flex;
                justify-content: center;
                margin-top: 20px;
            }
            .pagination a {
                margin: 0 5px;
                padding: 8px 12px;
                border: 1px solid #ced4da;
                border-radius: 0.25rem;
                background-color: #fff;
                color: #0d6efd;
                text-decoration: none;
                font-size: 1rem;
            }
            .pagination a.active {
                background-color: #0d6efd;
                color: #fff;
                border-color: #0d6efd;
            }
            .pagination a.disabled {
                color: #6c757d;
                cursor: not-allowed;
                opacity: 0.5;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Header.jsp" />

        <section id="export-material" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
            <div class="container">
                <div class="row my-5 py-5">
                    <div class="col-12 bg-white p-4 rounded shadow material-form">
                        <c:if test="${not hasExportMaterialPermission}">
                            <div class="alert alert-danger">You do not have permission to export materials.</div>
                            <div class="text-center mt-3">
                                <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Material List</a>
                            </div>
                        </c:if>
                        <c:if test="${hasExportMaterialPermission}">
                            <h2 class="display-4 fw-normal text-center mb-4">
                                <i class="fas fa-box-open me-2"></i>Export <span class="text-primary">Material</span>
                            </h2>
                            <c:if test="${not empty success}">
                                <div class="alert alert-success">${success}</div>
                            </c:if>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>

                            <h3 class="fw-normal mb-3">Add Material to Export List</h3>
                            <form action="ExportMaterial" method="post" class="mb-5" onsubmit="return validateAddForm()">
                                <input type="hidden" name="action" value="add">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="materialName" class="form-label text-muted">Material</label>
                                        <input type="text" id="materialName" name="materialName" class="form-control" required
                                               placeholder="Type material name or code">
                                        <input type="hidden" name="materialId" id="materialId">
                                        <div class="invalid-feedback">Please enter a valid material name or code.</div>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="quantity" class="form-label text-muted">Quantity</label>
                                        <input type="number" name="quantity" id="quantity" min="1" class="form-control" required>
                                        <div class="invalid-feedback">Please enter a quantity greater than 0.</div>
                                    </div>
                                    <div class="col-12 mt-4">
                                        <button type="submit" class="btn btn-dark btn-lg rounded-1 w-100">
                                            <i class="fas fa-plus-circle me-2"></i>Add to Export List
                                        </button>
                                    </div>
                                </div>
                            </form>

                            <h3 class="fw-normal mb-3">Current Export List</h3>
                            <c:if test="${not empty exportDetails}">
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Image</th>
                                                <th>Material</th>
                                                <th>Code</th>
                                                <th>Quantity</th>
                                                <th>Available Stock</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${exportDetails}">
                                                <tr>
                                                    <c:set var="imgUrl" value="${materialMap[detail.materialId].materialsUrl}" />
                                                    <c:choose>
                                                        <c:when test="${empty imgUrl}">
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/default-material.png' />
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(imgUrl, 'http://') || fn:startsWith(imgUrl, 'https://')}">
                                                            <c:set var="finalUrl" value='${imgUrl}' />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="finalUrl" value='${pageContext.request.contextPath}/images/material/${imgUrl}' />
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <td>
                                                        <img src="${finalUrl}" class="material-img" alt="${materialMap[detail.materialId].materialName}">
                                                    </td>
                                                    <td>${materialMap[detail.materialId].materialName}</td>
                                                    <td>${materialMap[detail.materialId].materialCode}</td>
                                                    <td>
                                                        <form action="ExportMaterial" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="updateQuantity">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="page" value="${currentPage}">
                                                            <input type="number" name="quantity" value="${detail.quantity}" min="1"
                                                                   class="form-control me-2" style="width: 100px;" required>
                                                            <button type="submit" class="btn btn-outline-primary btn-sm" title="Update Quantity">
                                                                <i class="fas fa-sync-alt"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                    <td>${stockMap[detail.materialId]}</td>
                                                    <td>
                                                        <form action="ExportMaterial" method="post">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="materialId" value="${detail.materialId}">
                                                            <input type="hidden" name="quantity" value="${detail.quantity}">
                                                            <input type="hidden" name="page" value="${currentPage}">
                                                            <button type="submit" class="btn btn-outline-danger btn-sm">Remove</button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                    <c:if test="${totalPages > 1}">
                                        <div class="pagination">
                                            <c:if test="${currentPage > 1}">
                                                <a href="ExportMaterial?page=${currentPage - 1}">Previous</a>
                                            </c:if>
                                            <c:if test="${currentPage <= 1}">
                                                <a class="disabled">Previous</a>
                                            </c:if>
                                            <c:forEach begin="1" end="${totalPages}" var="i">
                                                <a href="ExportMaterial?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                                            </c:forEach>
                                            <c:if test="${currentPage < totalPages}">
                                                <a href="ExportMaterial?page=${currentPage + 1}">Next</a>
                                            </c:if>
                                            <c:if test="${currentPage >= totalPages}">
                                                <a class="disabled">Next</a>
                                            </c:if>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                            <c:if test="${empty exportDetails}">
                                <div class="text-center py-5">
                                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                                    <p class="text-muted">No materials added to the export list yet.</p>
                                </div>
                            </c:if>

                            <h3 class="fw-normal mb-3 mt-5">Confirm Export</h3>
                            <form action="ExportMaterial" method="post" class="confirm-form" onsubmit="return validateConfirmForm()">
                                <input type="hidden" name="action" value="export">
                                <input type="hidden" name="page" value="${currentPage}">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="recipientName" class="form-label text-muted">Recipient</label>
                                        <input type="text" list="recipientList" id="recipientName" class="form-control" required
                                               oninput="updateRecipientId(this)" placeholder="Type recipient name or department">
                                        <input type="hidden" name="recipientUserId" id="recipientUserId">
                                        <datalist id="recipientList">
                                            <c:forEach var="user" items="${users}">
                                                <option value="${fn:escapeXml(user.fullName)} (${user.departmentName != null ? fn:escapeXml(user.departmentName) : 'No Department'})"
                                                        data-id="${user.userId}"
                                                        data-name="${fn:escapeXml(user.fullName)}"
                                                        data-department="${fn:escapeXml(user.departmentName != null ? user.departmentName : 'No Department')}">
                                                    ${fn:escapeXml(user.fullName)} (${user.departmentName != null ? fn:escapeXml(user.departmentName) : 'No Department'})
                                                </option>
                                            </c:forEach>
                                        </datalist>
                                        <div class="invalid-feedback">Please select a valid recipient.</div>
                                    </div>
                                    <div class="col-12">
                                        <label for="note" class="form-label text-muted">Note</label>
                                        <input type="text" name="note" id="note" maxlength="100" class="form-control" placeholder="Enter note">
                                        <div class="invalid-feedback">Please enter a valid note.</div>
                                    </div>
                                    <div class="col-12 mt-4">
                                        <div class="d-grid gap-2">
                                            <button type="submit" class="btn btn-dark btn-lg rounded-1">Confirm Export</button>
                                            <a href="StaticInventory" class="btn btn-outline-secondary btn-lg rounded-1">Back to Inventory</a>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>

        <script src="js/jquery-1.11.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
        <script src="js/plugins.js"></script>
        <script src="js/script.js"></script>
        <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    setTimeout(function() {
                        if (alert.style.display !== 'none') {
                            alert.style.transition = 'all 0.5s ease-out';
                            alert.style.opacity = '0';
                            alert.style.transform = 'translateY(-10px)';
                            setTimeout(function() {
                                alert.style.display = 'none';
                            }, 500);
                        }
                    }, 3000);
                });

                const materials = [
                    <c:forEach var="material" items="${materials}" varStatus="loop">
                        {
                            label: "${fn:escapeXml(material.materialName)} (${fn:escapeXml(material.materialCode)})",
                            value: "${fn:escapeXml(material.materialName)}",
                            id: "${material.materialId}",
                            name: "${fn:escapeXml(material.materialName)}",
                            code: "${fn:escapeXml(material.materialCode)}"
                        }${loop.last ? '' : ','}
                    </c:forEach>
                ];

                $("#materialName").autocomplete({
                    source: function(request, response) {
                        const term = request.term.toLowerCase();
                        const matches = materials.filter(material => 
                            material.name.toLowerCase().includes(term) || 
                            material.code.toLowerCase().includes(term)
                        );
                        response(matches);
                    },
                    select: function(event, ui) {
                        document.getElementById('materialId').value = ui.item.id;
                        document.getElementById('materialName').value = ui.item.name;
                        document.getElementById('materialName').classList.remove('is-invalid');
                    },
                    change: function(event, ui) {
                        if (!ui.item) {
                            const inputValue = document.getElementById('materialName').value.toLowerCase().trim();
                            const selectedMaterial = materials.find(material => 
                                material.name.toLowerCase() === inputValue || 
                                material.code.toLowerCase() === inputValue
                            );
                            if (selectedMaterial) {
                                document.getElementById('materialId').value = selectedMaterial.id;
                                document.getElementById('materialName').value = selectedMaterial.name;
                                document.getElementById('materialName').classList.remove('is-invalid');
                            } else {
                                document.getElementById('materialId').value = '';
                                document.getElementById('materialName').classList.add('is-invalid');
                            }
                        }
                    },
                    minLength: 1
                });

                function debounce(func, wait) {
                    let timeout;
                    return function executedFunction(...args) {
                        const later = () => {
                            clearTimeout(timeout);
                            func(...args);
                        };
                        clearTimeout(timeout);
                        timeout = setTimeout(later, wait);
                    };
                }

                function updateRecipientId(input) {
                    const inputValue = input.value.toLowerCase().trim();
                    const datalist = document.getElementById('recipientList');
                    const options = datalist.querySelectorAll('option');
                    const selectedOption = Array.from(options).find(option => {
                        const displayText = option.value.toLowerCase();
                        const name = option.getAttribute('data-name').toLowerCase();
                        const department = option.getAttribute('data-department').toLowerCase();
                        return displayText === inputValue || name === inputValue || department === inputValue;
                    });
                    document.getElementById('recipientUserId').value = selectedOption ? selectedOption.getAttribute('data-id') : '';
                    if (!selectedOption) {
                        document.getElementById('recipientName').classList.add('is-invalid');
                    } else {
                        document.getElementById('recipientName').classList.remove('is-invalid');
                    }
                }

                document.getElementById('recipientName').addEventListener('input', debounce(function() {
                    updateRecipientId(document.getElementById('recipientName'));
                }, 100));

                function validateAddForm() {
                    const form = document.querySelector('form[action="ExportMaterial"]');
                    let isValid = true;
                    const materialId = form.querySelector('#materialId');
                    const materialName = form.querySelector('#materialName');
                    const quantity = form.querySelector('#quantity');

                    if (!materialId.value) {
                        materialName.classList.add('is-invalid');
                        isValid = false;
                    } else {
                        materialName.classList.remove('is-invalid');
                    }

                    if (!quantity.value || quantity.value <= 0) {
                        quantity.classList.add('is-invalid');
                        isValid = false;
                    } else {
                        quantity.classList.remove('is-invalid');
                    }

                    return isValid;
                }

                function validateConfirmForm() {
                    const form = document.querySelector('.confirm-form');
                    let isValid = true;
                    const recipientId = form.querySelector('#recipientUserId');
                    const recipientName = form.querySelector('#recipientName');
                    const note = form.querySelector('#note');

                    if (!recipientId.value) {
                        recipientName.classList.add('is-invalid');
                        isValid = false;
                    } else {
                        recipientName.classList.remove('is-invalid');
                    }

                    if (note.value.length > 100) {
                        note.classList.add('is-invalid');
                        note.nextElementSibling.textContent = 'Note must not exceed 100 characters.';
                        isValid = false;
                    } else {
                        note.classList.remove('is-invalid');
                        note.nextElementSibling.textContent = 'Please enter a valid note.';
                    }

                    if (${fn:length(fullExportDetails)} === 0) {
                        alert("Cannot confirm export: The material list is empty.");
                        isValid = false;
                    }

                    return isValid;
                }
            });
        </script>
    </body>
</html>