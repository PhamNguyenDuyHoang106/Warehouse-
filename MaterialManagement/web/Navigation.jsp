<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="text-center mb-4 nav-buttons">
        <a href="profile" class="btn btn-primary">Profile</a>
        <a href="${pageContext.request.contextPath}/ChangePassword.jsp" class="btn btn-secondary">Change Password</a>
    <c:if test="${sessionScope.userPermissions.contains('VIEW_APPLICATION')}">
        <a href="ViewRequests" class="btn btn-success">My Applications</a>
    </c:if>
</div>