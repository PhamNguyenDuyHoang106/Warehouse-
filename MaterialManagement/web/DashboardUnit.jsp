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
    </style>
  </head>
  <body>
    <jsp:include page="Header.jsp" />
    <div class="container-fluid">
      <div class="row">
        <!-- Sidebar -->
        <div class="col-md-3 col-lg-2 bg-light p-0">
          <jsp:include page="Sidebar.jsp" />
        </div>
        <!-- Page Content -->
        <div class="col-md-9 col-lg-10 content px-md-4">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="dashboard-title mb-0">Unit List</h2>
            <!-- Chỉ hiển thị nút Add New Unit nếu user có quyền CREATE_UNIT hoặc là admin -->
            <c:if
              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'CREATE_UNIT')}"
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
                  <th>Unit Name</th>
                  <th>Symbol</th>
                  <th>Description</th>
                  <!-- Chỉ hiển thị cột Actions nếu user có quyền UPDATE_UNIT hoặc DELETE_UNIT hoặc là admin -->
                  <c:if
                    test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_UNIT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_UNIT')}"
                  >
                    <th>Actions</th>
                  </c:if>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${not empty units}">
                    <c:forEach var="unit" items="${units}">
                      <tr>
                        <td>${unit.id}</td>
                        <td>${unit.unitName}</td>
                        <td>${unit.symbol}</td>
                        <td>${unit.description}</td>

                        <c:if
                          test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_UNIT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_UNIT')}"
                        >
                          <td style="display: flex; justify-content: center">
                            <c:if
                              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_UNIT')}"
                            >
                              <a
                                href="EditUnit?id=${unit.id}"
                                class="btn btn-action btn-warning btn-sm me-1"
                                title="Edit"
                                ><i class="fas fa-edit"></i
                              ></a>
                            </c:if>

                            <c:if
                              test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_UNIT')}"
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
                        </c:if>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td colspan="4" class="text-center text-muted">
                        No units found.
                      </td>
                      <c:if
                        test="${sessionScope.user.roleId == 1 or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'UPDATE_UNIT') or rolePermissionDAO.hasPermission(sessionScope.user.roleId, 'DELETE_UNIT')}"
                      >
                        <td></td>
                      </c:if>
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
      <!-- end row -->
    </div>
    <!-- end container-fluid -->
    <jsp:include page="Footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
