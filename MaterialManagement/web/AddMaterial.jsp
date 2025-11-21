<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
<!-- JSP page using JSTL for dynamic data display -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add New Material - Material Management</title>
    
    <!-- Import Bootstrap and FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="css/vendor.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <link rel="stylesheet" type="text/css" href="css/override-style.css">

    <!-- Custom form styling -->
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
        }
        .content {
            padding-left: 20px;
            font-family: 'Roboto', sans-serif;
        }
        .form-container .form-control, .form-container .form-select {
            height: 48px;
            font-size: 1rem;
        }
        .form-container .form-label {
            font-size: 0.9rem;
            margin-bottom: 0.25rem;
        }
        .form-container .btn {
            font-size: 1rem;
            padding: 0.75rem;
        }
        .form-select { padding: 0.6rem 1rem; border-color: #dee2e6; cursor: pointer; }
        .form-select:focus { border-color: #86b7fe; box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25); }
        .form-select option { padding: 8px; }
        .required-field::after { content: "*"; color: red; margin-left: 4px; } /* Red asterisk for required fields */
        .btn-brown {
            background-color: #DEB887 !important;
            color: #fff !important;
            border: none;
        }
        .btn-brown:hover, .btn-brown:focus {
            background-color: #c49b63 !important;
            color: #fff !important;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <jsp:include page="Header.jsp" />

    <!-- Main Content Wrapper - Bao sidebar và body content -->
    <div class="main-content-wrapper">
      <!-- Sidebar - Nằm trong wrapper -->
      <div class="sidebar-wrapper-inner">
        <jsp:include page="Sidebar.jsp" />
      </div>
      
      <!-- Main Content Body - Nằm trong wrapper, bên cạnh sidebar -->
      <div class="main-content-body">
        <div class="container-fluid my-4" style="padding-left: 30px; padding-right: 30px;">
          <div class="row">
            <div class="col-12 px-md-4 py-4">
                <section id="AddMaterial" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                    <div class="container">
                        <div class="row my-5 py-5">
                            <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                <h2 class="display-4 fw-normal text-center mb-4">Add New <span class="text-primary">Material</span></h2>
                                
                                <!-- Display error message if it exists -->
                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger" role="alert">
                                        ${error}
                                    </div>
                                </c:if>

                                <!-- Display success message if it exists -->
                                <c:if test="${not empty success}">
                                    <div class="alert alert-success" role="alert">
                                        ${success}
                                    </div>
                                </c:if>

                                <% Map<String, String> errors = (Map<String, String>) request.getAttribute("errors");
                                   if (errors != null && !errors.isEmpty()) { %>
                                    <div class="alert alert-danger" style="margin-bottom: 16px;">
                                        <ul style="margin-bottom: 0;">
                                        <% for (String err : errors.values()) { %>
                                            <li><%= err %></li>
                                        <% } %>
                                        </ul>
                                    </div>
                                <% } %>

                                <!-- Form for adding new material -->
                                <form action="addmaterial" method="POST" enctype="multipart/form-data">
                                    <!-- Material Code and Name -->
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label for="materialCode" class="form-label required-field">Material Code</label>
                                            <input type="text" class="form-control" id="materialCode" name="materialCode" value="${param.materialCode != null ? param.materialCode : (materialCode != null ? materialCode : (m != null ? m.materialCode : ''))}" readonly>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="materialName" class="form-label required-field">Material Name</label>
                                            <input type="text" class="form-control" id="materialName" name="materialName" value="${param.materialName != null ? param.materialName : (materialName != null ? materialName : (m != null ? m.materialName : ''))}">
                                        </div>
                                    </div>

                                    <!-- Material Image: choose image or URL -->
                                    <div class="mb-3">
                                        <label class="form-label">Material Image</label>
                                        <div class="mb-3">
                                            <div id="imagePreview" style="display: none;">
                                                <img id="previewImg" class="img-thumbnail" style="width: 150px; height: 150px; object-fit: cover;">
                                            </div>
                                        </div>

                                        <!-- Tabs for choosing image upload or URL input -->
                                        <ul class="nav nav-tabs" id="imageInputTabs" role="tablist">
                                            <li class="nav-item" role="presentation">
                                                <button class="nav-link active" id="upload-tab" data-bs-toggle="tab" data-bs-target="#upload-content" type="button" role="tab">Media Upload</button>
                                            </li>
                                            <li class="nav-item" role="presentation">
                                                <button class="nav-link" id="url-tab" data-bs-toggle="tab" data-bs-target="#url-content" type="button" role="tab">Media URL</button>
                                            </li>
                                        </ul>

                                        <!-- Content of each tab -->
                                        <div class="tab-content pt-3" id="imageInputTabContent">
                                            <div class="tab-pane fade show active" id="upload-content" role="tabpanel">
                                                <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                                            </div>
                                            <div class="tab-pane fade" id="url-content" role="tabpanel">
                                                <input type="url" class="form-control" id="materialsUrl" name="materialsUrl" placeholder="Enter image URL" value="${param.materialsUrl != null ? param.materialsUrl : (materialsUrl != null ? materialsUrl : (m != null ? m.rawUrl : ''))}">
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Material Status -->
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label for="materialStatus" class="form-label required-field">Material Status</label>
                                            <select class="form-select" id="materialStatus" name="materialStatus" required>
                                                <option value="">Select Status</option>
                                                <option value="active" ${param.materialStatus == 'active' ? 'selected' : (materialStatus != null && materialStatus == 'active' ? 'selected' : (m != null && m.materialStatus == 'active' ? 'selected' : ''))}>Active</option>
                                                <option value="inactive" ${param.materialStatus == 'inactive' ? 'selected' : (materialStatus != null && materialStatus == 'inactive' ? 'selected' : (m != null && m.materialStatus == 'inactive' ? 'selected' : ''))}>Inactive</option>
                                                <option value="discontinued" ${param.materialStatus == 'discontinued' ? 'selected' : (materialStatus != null && materialStatus == 'discontinued' ? 'selected' : (m != null && m.materialStatus == 'discontinued' ? 'selected' : ''))}>Discontinued</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="categoryInput" class="form-label required-field">Category</label>
                                            <input type="text" class="form-control" id="categoryInput" placeholder="Type category name" value="${param.categoryName != null ? param.categoryName : (categoryName != null ? categoryName : (m != null ? m.category.category_name : ''))}">
                                            <input type="hidden" id="categoryId" name="categoryId" value="${param.categoryId != null ? param.categoryId : (categoryId != null ? categoryId : (m != null ? m.category.category_id : ''))}">
                                            <div id="categorySuggestions" class="list-group" style="position: absolute; z-index: 1000; width: 100%; max-height: 200px; overflow-y: auto; display: none;"></div>
                                        </div>
                                    </div>

                                    <!-- Unit -->
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label for="unitInput" class="form-label required-field">Unit</label>
                                            <input type="text" class="form-control" id="unitInput" placeholder="Type unit name" value="${param.unitName != null ? param.unitName : (unitName != null ? unitName : (m != null ? m.unit.unitName : ''))}">
                                            <input type="hidden" id="unitId" name="unitId" value="${param.unitId != null ? param.unitId : (unitId != null ? unitId : (m != null ? m.unit.id : ''))}">
                                            <div id="unitSuggestions" class="list-group" style="position: absolute; z-index: 1000; width: 100%; max-height: 200px; overflow-y: auto; display: none;"></div>
                                        </div>
                                    </div>



                                    <!-- Submit and Cancel buttons -->
                                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                        <button type="submit" class="btn btn-brown">
                                            <i class="fas fa-save"></i> Save Material
                                        </button>
                                        <a href="dashboardmaterial" class="btn btn-secondary ms-2">
                                            <i class="fas fa-times"></i> Cancel
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>

    <!-- Import Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Preview ảnh khi chọn file hoặc nhập URL, không validate gì ở client
        document.getElementById('imageFile').addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file) {
                document.getElementById('materialsUrl').value = '';
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('previewImg').src = e.target.result;
                    document.getElementById('imagePreview').style.display = 'block';
                }
                reader.readAsDataURL(file);
                const uploadTab = document.getElementById('upload-tab');
                bootstrap.Tab.getOrCreateInstance(uploadTab).show();
            }
        });

        document.getElementById('materialsUrl').addEventListener('input', function(event) {
            const url = event.target.value.trim();
            if (url) {
                document.getElementById('imageFile').value = '';
            }
        });

        // Truyền categories và units sang JS
        var categories = [
            <c:forEach var="cat" items="${categories}" varStatus="loop">
                {id: ${cat.category_id}, name: "${cat.category_name}"}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];
        var units = [
            <c:forEach var="u" items="${units}" varStatus="loop">
                {id: ${u.id}, name: "${u.unitName}"}<c:if test="${!loop.last}">,</c:if>
            </c:forEach>
        ];

        // Autocomplete cho category
        const categoryInput = document.getElementById('categoryInput');
        const categorySuggestions = document.getElementById('categorySuggestions');
        const categoryHiddenId = document.getElementById('categoryId');
        let lastCategoryValue = categoryInput.value;
        
        categoryInput.addEventListener('input', function() {
            const value = this.value.trim();
            const valueLower = value.toLowerCase();
            categorySuggestions.innerHTML = '';
            
            // Chỉ reset hidden input nếu user thay đổi text khác với giá trị đã chọn
            const selectedCategory = categories.find(cat => cat.name.toLowerCase() === valueLower);
            if (!selectedCategory) {
                categoryHiddenId.value = '';
            }
            
            if (value.length === 0) {
                categorySuggestions.style.display = 'none';
                return;
            }
            const matches = categories.filter(cat => cat.name.toLowerCase().includes(valueLower));
            if (matches.length === 0) {
                categorySuggestions.style.display = 'none';
                return;
            }
            matches.forEach(cat => {
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'list-group-item list-group-item-action';
                item.textContent = cat.name;
                item.onclick = function() {
                    categoryInput.value = cat.name;
                    categoryHiddenId.value = cat.id;
                    lastCategoryValue = cat.name;
                    categorySuggestions.innerHTML = '';
                    categorySuggestions.style.display = 'none';
                };
                categorySuggestions.appendChild(item);
            });
            categorySuggestions.style.display = 'block';
        });
        document.addEventListener('click', function(e) {
            if (!categoryInput.contains(e.target) && !categorySuggestions.contains(e.target)) {
                categorySuggestions.innerHTML = '';
                categorySuggestions.style.display = 'none';
            }
        });

        // Autocomplete cho unit
        const unitInput = document.getElementById('unitInput');
        const unitSuggestions = document.getElementById('unitSuggestions');
        const unitHiddenId = document.getElementById('unitId');
        let lastUnitValue = unitInput.value;
        
        unitInput.addEventListener('input', function() {
            const value = this.value.trim();
            const valueLower = value.toLowerCase();
            unitSuggestions.innerHTML = '';
            
            // Chỉ reset hidden input nếu user thay đổi text khác với giá trị đã chọn
            const selectedUnit = units.find(u => u.name.toLowerCase() === valueLower);
            if (!selectedUnit) {
                unitHiddenId.value = '';
            }
            
            if (value.length === 0) {
                unitSuggestions.style.display = 'none';
                return;
            }
            const matches = units.filter(u => u.name.toLowerCase().includes(valueLower));
            if (matches.length === 0) {
                unitSuggestions.style.display = 'none';
                return;
            }
            matches.forEach(u => {
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'list-group-item list-group-item-action';
                item.textContent = u.name;
                item.onclick = function() {
                    unitInput.value = u.name;
                    unitHiddenId.value = u.id;
                    lastUnitValue = u.name;
                    unitSuggestions.innerHTML = '';
                    unitSuggestions.style.display = 'none';
                };
                unitSuggestions.appendChild(item);
            });
            unitSuggestions.style.display = 'block';
        });
        document.addEventListener('click', function(e) {
            if (!unitInput.contains(e.target) && !unitSuggestions.contains(e.target)) {
                unitSuggestions.innerHTML = '';
                unitSuggestions.style.display = 'none';
            }
        });
    </script>
</body>
</html>
