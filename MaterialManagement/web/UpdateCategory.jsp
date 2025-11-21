<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="dal.CategoryDAO" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Waggy - Update Category</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous">
        <link rel="stylesheet" type="text/css" href="css/vendor.css">
        <link rel="stylesheet" type="text/css" href="style.css">
        <link rel="stylesheet" type="text/css" href="css/override-style.css">
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
                    <section id="UpdateCategory" style="background: url('images/background-img.png') no-repeat; background-size: cover;">
                        <div class="container">
                            <div class="row my-5 py-5">
                                <div class="col-10 bg-white p-4 mx-auto rounded shadow form-container">
                                    <h2 class="display-4 fw-normal text-center mb-4">Update <span class="text-primary">Category</span></h2>
                                    <%
                                        String message = (String) request.getAttribute("success");
                                        String error = (String) request.getAttribute("error");
                                    %>
                                    <% if (message != null) { %>
                                    <div class="alert alert-success"><c:out value="${message}" escapeXml="false"/></div>
                                    <% } %>
                                    <% if (error != null) { %>
                                    <div class="alert alert-danger"><c:out value="${error}" escapeXml="false"/></div>
                                    <% } %>
                                    <c:if test="${empty c}">
                                        <div class="alert alert-danger">Category not found.</div>
                                        <div class="d-grid gap-2 mb-3">
                                            <a href="${pageContext.request.contextPath}/Category?service=listCategory" class="btn btn-secondary btn-lg rounded-1">
                                            <i class="fas fa-arrow-left"></i> Back to Category List
                                        </a>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty c}">
                                        <form action="${pageContext.request.contextPath}/Category?service=updateCategory" method="post" id="updateCategoryForm">
                                            <input type="hidden" name="categoryID" value="${c.category_id}">

                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="code" class="form-label text-muted">Code</label>
                                                        <input type="text" id="code" name="code" 
                                                               class="form-control" 
                                                               placeholder="Category Code"
                                                               value="${fn:escapeXml(c.code)}" required>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="categoryName" class="form-label text-muted">Category Name</label>
                                                        <input type="text" id="categoryName" name="categoryName" 
                                                               class="form-control" 
                                                               value="${fn:escapeXml(c.category_name)}" required>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="status" class="form-label text-muted">Status</label>
                                                        <select id="status" name="status" class="form-select" required>
                                                            <option value="active" ${c.status == 'active' ? 'selected' : ''}>Active</option>
                                                            <option value="inactive" ${c.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                                 <div class="col-md-6">
                                                    <div class="mb-3">
                                                        <label for="parentID" class="form-label text-muted">Parent Category</label>
                                                        <select id="parentID" name="parentID" class="form-select">
                                                                    <option value="">None</option>
                            <c:forEach var="category" items="${categories}">
                                <c:if test="${category.category_id != c.category_id}">
                                    <option value="${category.category_id}" ${c.parent_id != null && category.category_id == c.parent_id ? 'selected' : ''}>
                                        ${category.category_id} - ${category.category_name}
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                </div>
</div>
            <div class="row">
                <div class="col-12">
                    <div class="mb-3">
                        <label for="description" class="form-label text-muted">Description</label>
                        <textarea id="description" name="description" class="form-control" 
                                  placeholder="Enter Description" rows="4">${fn:escapeXml(c.description)}</textarea>
                    </div>
                </div>
                                        </div>
            <div class="row">
                <div class="col-12">
                    <div class="d-grid gap-2 mb-3">
                        <button type="submit" name="submit" value="Update Category" class="btn btn-dark btn-lg rounded-1">
                                                <i class="fas fa-save"></i> Update Category
                                            </button>
                    </div>
                    <div class="d-grid gap-2 mb-3">
                        <a href="${pageContext.request.contextPath}/Category?service=listCategory" class="btn btn-secondary btn-lg rounded-1">
                            <i class="fas fa-times"></i> Back to Category List
                        </a>
                    </div>
                </div>
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
          </div> <!-- End main-content-body -->
        </div> <!-- End main-content-wrapper -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ENjdO4Dr2bkBIFxQpeoTz1HIcje39Wm4jDKdf19U8gI4ddQ3GYNS7NTKfAdVQSZe" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js" integrity="sha512-yFjZbTYRCJodnuyGlsKamNE/LlEaEAxSUDe5+u61mV8zzqJVFOH7TnULE2/PP/l5vKWpUNnF4VGVkXh3MjgLsg==" crossorigin="anonymous"></script>
<script src="js/plugins.js"></script>
<script src="js/script.js"></script>
<script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const updateForm = document.getElementById('updateCategoryForm');
        if (updateForm) {
            updateForm.addEventListener('submit', function(event) {
                let errors = [];
                
                const code = document.getElementById('code').value;
                if (!code) {
                    errors.push('Code is required.');
                    document.getElementById('code').classList.add('is-invalid');
                } else {
                    document.getElementById('code').classList.remove('is-invalid');
                }

                const status = document.getElementById('status').value;
                if (!status) {
                    errors.push('Status is required.');
                    document.getElementById('status').classList.add('is-invalid');
                } else {
                    document.getElementById('status').classList.remove('is-invalid');
                }

                const priority = document.getElementById('priority').value;
                if (!priority) {
                    errors.push('Priority is required.');
                    document.getElementById('priority').classList.add('is-invalid');
                } else {
                    document.getElementById('priority').classList.remove('is-invalid');
                }

                const description = document.getElementById('description').value;
                if (!description) {
                    errors.push('Description is required.');
                    document.getElementById('description').classList.add('is-invalid');
                } else {
                    document.getElementById('description').classList.remove('is-invalid');
                }

                if (errors.length > 0) {
                    event.preventDefault();
                }
            });
        }
    });
</script>
    </body>
</html>