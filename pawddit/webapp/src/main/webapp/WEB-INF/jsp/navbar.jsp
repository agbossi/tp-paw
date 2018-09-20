<nav class="navbar-component">
	<a class="no-underline" href="<c:url value="/"/>">
		<h2 class="logo">Pawddit.</h2>
	</a>
	<c:if test="${empty user}">
	<%@include file="externalNavbar.jsp" %>
	</c:if>
	<c:if test="${!empty user}">
	<%@include file="internalNavbar.jsp" %>
    </c:if>
</nav>
