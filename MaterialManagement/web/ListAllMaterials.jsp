<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>List Material By Supplier</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', sans-serif;
            padding: 30px;
        }

        .supplier-buttons {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 12px;
            margin-bottom: 30px;
        }

        .supplier-button {
            background-color: #3498db;
            color: #fff;
            border: none;
            padding: 10px 18px;
            border-radius: 25px;
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        .supplier-button:hover {
            background-color: #2980b9;
            transform: translateY(-2px);
        }

        .product-card {
            transition: transform 0.2s ease-in-out;
        }

        .product-card:hover {
            transform: translateY(-5px);
        }

        .card-title {
            font-size: 1.2rem;
            font-weight: bold;
        }

        .card-text {
            font-size: 0.95rem;
        }

        .price {
            color: #dc3545;
            font-weight: bold;
            font-size: 1.1rem;
        }

        .no-data, .error {
            text-align: center;
            font-weight: bold;
            margin: 20px;
        }

        .supplier-label {
            font-size: 0.9rem;
            font-style: italic;
            color: #198754;
        }

        .back-button {
            text-align: right;
            margin-bottom: 20px;
        }

        .card {
            min-height: 450px; /* Tăng chiều cao để chứa thêm thông tin */
        }

        .status-new { color: #28a745; }
        .status-used { color: #ffc107; }
        .status-damaged { color: #dc3545; }
        .condition-text { font-style: italic; color: #6c757d; }
    </style>
</head>
<body>

    <div class="back-button">
        <a href="index.html" class="btn btn-primary">🔙 Back to Home</a>
    </div>

    <h2 class="text-center mb-4">📦 List Material By Supplier</h2>

    <!-- ✅ Supplier Buttons -->
    <div class="supplier-buttons">
        <a href="getbysupplier?supplierId=1"><button class="supplier-button">Lenovo Technology Co., Ltd.</button></a>
        <a href="getbysupplier?supplierId=2"><button class="supplier-button">CLUBLU Corporation</button></a>
        <a href="getbysupplier?supplierId=3"><button class="supplier-button">Essager Vietnam Co., Ltd.</button></a>
        <a href="getbysupplier?supplierId=4"><button class="supplier-button">Kingston Technology Corporation</button></a>
        <a href="getbysupplier?supplierId=5"><button class="supplier-button">Ugreen Vietnam Co., Ltd.</button></a>
        <a href="getbysupplier?supplierId=6"><button class="supplier-button">Lexar International</button></a>
        <a href="getbysupplier?supplierId=7"><button class="supplier-button">Cool Moon Tech</button></a>
        <a href="getbysupplier?supplierId=8"><button class="supplier-button">ASUS Global Pte. Ltd.</button></a>
        <a href="getbysupplier?supplierId=9"><button class="supplier-button">Gigabyte Technology</button></a>
        <a href="getbysupplier?supplierId=10"><button class="supplier-button">RelayTech Solutions</button></a>
    </div>

    <!-- ✅ Error Message -->
    <c:if test="${not empty error}">
        <p class="error text-danger">${error}</p>
    </c:if>

    <!-- ✅ No Data Found -->
    <c:if test="${empty materialList}">
        <p class="no-data text-muted">Not found material.</p>
    </c:if>

    <!-- ✅ Product List -->
    <div class="row row-cols-1 row-cols-md-3 g-4">
        <c:forEach var="material" items="${materialList}">
            <div class="col">
                <div class="card h-100 shadow-sm product-card">
                    <c:choose>
                        <c:when test="${not empty material.materialsUrl}">
                            <img src="${material.materialsUrl}" class="card-img-top" alt="${material.materialName}"
                                 style="height: 220px; width: 100%; object-fit: contain; background-color: #fff; padding: 10px;">
                        </c:when>
                        <c:otherwise>
                            <img src="images/default.jpg" class="card-img-top" alt="No Image"
                                 style="height: 220px; width: 100%; object-fit: contain; background-color: #f0f0f0; padding: 10px;">
                        </c:otherwise>
                    </c:choose>
                    <div class="card-body">
                        <h5 class="card-title">${material.materialName}</h5>
                        <p class="card-text mb-1">Code: <strong>${material.materialCode}</strong></p>
                        <p class="card-text mb-1">
                            Status: 
                            <span class="status-${material.materialStatus == 'new' ? 'new' : material.materialStatus == 'used' ? 'used' : 'damaged'}">
                                ${material.materialStatus}
                            </span>
                        </p>
                        <p class="card-text mb-1 condition-text">
  Status: <span class="badge ${material.materialStatus == 'new' ? 'bg-success' : (material.materialStatus == 'used' ? 'bg-warning' : 'bg-danger')}">
    ${material.materialStatus == 'new' ? 'New' : (material.materialStatus == 'used' ? 'Used' : 'Damaged')}
  </span>
</p>
                        <p class="card-text mb-1">Unit ID: ${material.unit != null ? material.unit.id : 'N/A'}</p>
                        <p class="supplier-label">
                            <c:choose>
                                <c:when test="${not empty material.supplier}">
                                    ✅ ${material.supplier.supplierName}
                                </c:when>
                              
                            </c:choose>
                        </p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

</body>
</html>