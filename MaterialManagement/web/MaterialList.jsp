<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Material List</title>
    <meta charset="UTF-8">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 1280px;
            margin: 50px auto;
            background: #ffffff;
            border-radius: 20px;
            box-shadow: 0 6px 24px rgba(0, 0, 0, 0.05);
            padding: 40px 50px;
        }

        h2 {
            text-align: center;
            color: #d59f39;
            margin-bottom: 40px;
            font-size: 2.5rem;
            font-weight: 800;
        }

        .search-container {
            max-width: 900px;
            margin: 0 auto 40px auto;
            padding: 0 15px;
        }

        .search-form {
            display: flex;
            gap: 12px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .form-control {
            border-radius: 24px;
            border: 1px solid #ccc;
            padding: 10px 20px;
            font-size: 1rem;
            min-width: 250px;
        }

        .btn-search,
        .btn-sort {
            padding: 10px 20px;
            background-color: #d59f39;
            color: #fff;
            border: none;
            border-radius: 24px;
            font-size: 1rem;
            cursor: pointer;
            transition: 0.2s ease;
        }

        .btn-search:hover,
        .btn-sort:hover {
            background-color: #c08d2e;
            transform: scale(1.03);
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background-color: #ffffff;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.05);
        }

        table thead th {
            background-color: #d59f39 !important;
            color: white !important;
            font-weight: 700;
            font-size: 1rem;
            text-align: center;
            padding: 14px;
        }

        /* Bo tròn đầu bảng */
        thead th:first-child {
            border-top-left-radius: 16px;
        }

        thead th:last-child {
            border-top-right-radius: 16px;
        }

        td {
            font-size: 0.95rem;
            text-align: center;
            padding: 12px;
            color: #333;
            vertical-align: middle;
        }

        tr:nth-child(even) {
            background-color: #fff;
        }

        tr:hover {
            background-color: #f8f9fa;
            transition: 0.2s ease-in-out;
        }

        img {
            width: 100px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
            border: 1px solid #ccc;
        }

        .btn-primary {
            background-color: #d59f39 !important;
            border: none;
            border-radius: 24px;
            padding: 6px 16px;
            font-size: 0.9rem;
            font-weight: 500;
            color: white !important;
        }

        .btn-primary:hover {
            background-color: #c08d2e !important;
        }

        .status-active {
            color: #27ae60;
            font-weight: bold;
        }

        .status-disabled {
            color: #e74c3c;
            font-weight: bold;
        }

        .text-danger {
            color: #c0392b !important;
        }

        .btn-outline-dark {
            background-color: #d59f39 !important;
            color: white !important;
            border-radius: 24px;
            border: none;
        }

        .btn-outline-dark:hover {
            background-color: #c08d2e !important;
        }

        @media (max-width: 768px) {
            .container {
                padding: 20px;
            }

            h2 {
                font-size: 1.8rem;
            }

            .search-form {
                flex-direction: column;
                align-items: center;
            }

            .form-control {
                width: 100%;
            }

            .btn-search,
            .btn-sort {
                width: 100%;
            }

            table,
            thead,
            tbody,
            th,
            td,
            tr {
                font-size: 0.9rem;
            }
        }

        .input-group-custom {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .input-group-custom input[type="text"] {
            flex: 1;
            min-width: 250px;
            max-width: 400px;
        }
    </style>
</head>
<body>
    <div style="padding: 60px 30px">
    <!-- 🔍 Search Form -->
    <div class="search-container">
        <div class="row mb-4 justify-content-center">
            <div class="col-md-10">
                <form action="searchbycode" method="get" class="input-group-custom">
                    <input type="text" class="form-control" name="code" placeholder="Search by material code..."
                           value="${code != null ? code : ''}" required>
                    <button type="submit" class="btn-search">Search</button>
                    <a href="sortbymaterialname" class="btn-sort">Sort by Name</a>
                </form>
            </div>
        </div>
    </div>

    <!-- ✅ Bảng dữ liệu -->
    <c:choose>
        <c:when test="${empty productList}">
            <div class="text-center text-danger fw-bold fs-5">
                Not Found: <strong>${code}</strong>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover w-100 align-middle">
                    <thead class="text-center fs-6">
                    <tr>
                        <th style="width: 200px">Image</th>
                        <th>Material Code</th>
                        <th>Material Name</th>
                        <th>Status</th>
                        <th>Status</th>
                        <th>Category</th>
                        <th>Unit</th>
                        <th>Created At</th>
                        <th>Updated At</th>
                        <th>Disable</th>
                        <th style="width: 150px">Actions</th>
                    </tr>
                    </thead>
                    <tbody class="text-center">
                    <c:forEach var="product" items="${productList}">
                        <tr>
                            <td>
                                <img src="images/material1/${product.materialsUrl}" alt="${product.materialName}">
                            </td>
                            <td>${product.materialCode}</td>
                            <td>${product.materialName}</td>
                            <td>
                                <span class="badge ${product.materialStatus == 'new' ? 'bg-success' : (product.materialStatus == 'used' ? 'bg-warning' : 'bg-danger')}">
                                    ${product.materialStatus == 'new' ? 'New' : (product.materialStatus == 'used' ? 'Used' : 'Damaged')}
                                </span>
                            </td>
                            <td>${product.conditionPercentage}</td>
                            <td>${product.category != null ? product.category.category_name : 'N/A'}</td>
                            <td>${product.unit != null ? product.unit.unitName : 'N/A'}</td>
                            <td>${product.createdAt}</td>
                            <td>${product.updatedAt}</td>
                            <td>${product.disable ? 'Yes' : 'No'}</td>
                            <td>
                                <a href="ProductDetail?id=${product.materialId}" class="btn btn-sm btn-primary">View Detail</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- 🔙 Back button -->
    <div class="text-center mt-4">
        <a href="home" class="btn btn-outline-dark"
           style="padding: 10px 18px; font-size: 1rem;">⬅ Back to Home</a>
    </div>
</div>
</body>
</html>
