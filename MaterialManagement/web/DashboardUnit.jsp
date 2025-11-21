<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%@
taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Unit Management Dashboard</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link rel="stylesheet" type="text/css" href="css/vendor.css" />
    <link rel="stylesheet" type="text/css" href="style.css" />
    <link rel="stylesheet" type="text/css" href="css/override-style.css">
    <style>
      body {
        background-color: #f8f9fa;
        padding: 20px;
      }
      .table-responsive {
        margin: 20px 0;
      }
      .content {
        padding-left: 20px;
        font-family: "Roboto", sans-serif;
      }
      .dashboard-title {
        color: #e2b77a;
        font-weight: bold;
        font-size: 2.2rem;
        margin-bottom: 24px;
      }
      .btn-action {
        width: 50px;
        height: 32px;
        padding: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 2px;
      }
      .pagination {
        justify-content: center;
        margin-top: 20px;
      }
      .pagination .page-item.active .page-link {
        background-color: #dead6f;
        border-color: #dead6f;
        color: #fff;
      }
      .pagination .page-link {
        color: #dead6f;
      }
      .pagination .page-link:hover {
        background-color: #dead6f;
        border-color: #dead6f;
        color: #fff;
      }
      .pagination .page-item.disabled .page-link {
        color: #6c757d;
      }
      .badge-base-yes {
        background-color: #28a745 !important;
        color: #fff !important;
        font-weight: bold;
        padding: 6px 12px;
        font-size: 0.9rem;
      }
      .badge-base-no {
        background-color: #6c757d !important;
        color: #fff !important;
        font-weight: bold;
        padding: 6px 12px;
        font-size: 0.9rem;
      }
      .badge-status-active {
        background-color: #28a745 !important;
        color: #fff !important;
        font-weight: bold;
        padding: 6px 12px;
        font-size: 0.9rem;
      }
      .badge-status-inactive {
        background-color: #dc3545 !important;
        color: #fff !important;
        font-weight: bold;
        padding: 6px 12px;
        font-size: 0.9rem;
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
            <div class="col-12">
        <!-- Page Content -->
        <div class="col-md-9 col-lg-10 content px-md-4">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="dashboard-title mb-0">Unit List</h2>
            <!-- Only show Add New Unit button if user has permission to create unit or is admin -->
            <c:if
              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Tạo đơn vị')}"
            >
              <a
                href="AddUnit"
                class="btn flex-shrink-0"
                style="
                  background-color: #e2b77a;
                  color: #fff;
                  height: 60px;
                  min-width: 260px;
                  font-size: 1.25rem;
                  font-weight: 500;
                  border-radius: 6px;
                  padding: 0 32px;
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                "
              >
                <i class="fas fa-plus me-2"></i> Add New Unit
              </a>
            </c:if>
          </div>
          <div
            class="d-flex align-items-center gap-3 mb-4"
            style="flex-wrap: wrap"
          >
            <form
              class="d-flex align-items-center gap-3 flex-shrink-0"
              action="UnitList"
              method="get"
              style="margin-bottom: 0"
            >
              <input
                class="form-control"
                type="search"
                name="keyword"
                placeholder="Search By Name Or Symbol"
                value="${keyword}"
                aria-label="Search"
                style="
                  min-width: 260px;
                  max-width: 320px;
                  height: 60px;
                  border: 2px solid gray;
                  border-radius: 6px;
                  font-size: 1.1rem;
                "
              />
              <button
                class="btn"
                type="submit"
                style="
                  background-color: #e2b77a;
                  color: #fff;
                  height: 60px;
                  min-width: 150px;
                  font-size: 1.1rem;
                  font-weight: 500;
                  border-radius: 6px;
                "
              >
                <i class="fas fa-search me-2"></i> Search
              </button>
              <a
                href="UnitList"
                class="btn"
                style="
                  background-color: #6c757d;
                  color: #fff;
                  height: 60px;
                  min-width: 150px;
                  font-size: 1.1rem;
                  font-weight: 500;
                  border-radius: 6px;
                "
                >Clear</a
              >
            </form>
          </div>
          <div class="table-responsive">
            <table
              class="table table-bordered table-hover align-middle text-center"
            >
              <thead class="table-light">
                <tr>
                  <th>ID</th>
                  <th>Unit Code</th>
                  <th>Unit Name</th>
                  <th>Symbol</th>
                  <th>Is Base</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${not empty units}">
                    <c:forEach var="unit" items="${units}">
                      <tr>
                        <td>${unit.id}</td>
                        <td>${unit.unitCode != null ? unit.unitCode : ''}</td>
                        <td>${unit.unitName}</td>
                        <td>${unit.symbol != null ? unit.symbol : ''}</td>
                        <td>
                          <span class="badge ${unit.base ? 'badge-base-yes' : 'badge-base-no'}">
                            ${unit.base ? 'Yes' : 'No'}
                          </span>
                        </td>
                        <td>
                          <span class="badge ${unit.status == 'active' ? 'badge-status-active' : 'badge-status-inactive'}">
                            ${unit.status == 'active' ? 'Active' : 'Inactive'}
                          </span>
                        </td>

                        <td style="display: flex; justify-content: center">
                            <a
                              href="UnitList?action=view&id=${unit.id}"
                              class="btn btn-action btn-info btn-sm me-1"
                              title="View Details"
                              ><i class="fas fa-eye"></i
                            ></a>
                            <c:if
                              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Sửa đơn vị')}"
                            >
                              <a
                                href="EditUnit?id=${unit.id}"
                                class="btn btn-action btn-warning btn-sm me-1"
                                title="Edit"
                                ><i class="fas fa-edit"></i
                              ></a>
                            </c:if>

                            <c:if
                              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'Xóa đơn vị')}"
                            >
                              <form
                                action="DeleteUnit"
                                method="post"
                                onsubmit="return confirm('Are you sure you want to delete this unit? (Materials belonging to this unit will remain unchanged)');"
                              >
                                <input
                                  type="hidden"
                                  name="id"
                                  value="${unit.id}"
                                />
                                <button
                                  type="submit"
                                  class="btn btn-action btn-danger btn-sm"
                                  title="Delete"
                                >
                                  <i class="fas fa-trash"></i>
                                </button>
                              </form>
                            </c:if>
                          </td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td colspan="7" class="text-center text-muted">
                        No units found.
                      </td>
                    </tr>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
          <!-- Pagination -->
          <nav class="mt-3">
            <ul class="pagination justify-content-center">
              <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a
                  class="page-link"
                  href="UnitList?page=${currentPage - 1}&keyword=${keyword}"
                  >Previous</a
                >
              </li>
              <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage == i ? 'active' : ''}">
                  <a
                    class="page-link"
                    href="UnitList?page=${i}&keyword=${keyword}"
                    >${i}</a
                  >
                </li>
              </c:forEach>
              <li
                class="page-item ${currentPage == totalPages ? 'disabled' : ''}"
              >
                <a
                  class="page-link"
                  href="UnitList?page=${currentPage + 1}&keyword=${keyword}"
                  >Next</a
                >
              </li>
            </ul>
          </nav>
        </div>
        <!-- end content -->
            </div>
          </div>
        </div>
      </div> <!-- End main-content-body -->
    </div> <!-- End main-content-wrapper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
