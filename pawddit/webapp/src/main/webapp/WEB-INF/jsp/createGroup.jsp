<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
	<head>
		<meta charset="UTF-8">
		<title>
			Pawddit | <spring:message code="createGroup.title"/>
		</title>
		<meta name="description" content="<spring:message code="meta.description.creategroup.message"/>">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="icon" href="<c:url value="/resources/images/tab-logo.png" />">
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/createGroup.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/app.css" />" />
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
		<link href="https://fonts.googleapis.com/css?family=Kosugi" rel="stylesheet">
		<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU" crossorigin="anonymous">
	</head>
	<body class="main-font">
		<%@include file="navbar.jsp" %>
		<div class="application-background">
    		<div class="center-content">
    			<div class="create-group-component">
    				<h2>
						<spring:message code="createGroup.title"/>
					</h2>
    				<br>
    				<c:url value="/createGroup" var="postPath"/>
    				<form:form modelAttribute="createGroupForm" action="${groupPath}" accept-charset="UTF-8"  method="post">
    					  <div class="form-group">
    					    <form:label for="group-name" path="name">
								<spring:message code="groupName.title"/>
							</form:label>
							<spring:message code="groupName.placeholder" var="groupNamePlaceholder"/>
    					    <form:input type="text" path="name" class="form-control" id="group-name" placeholder="${groupNamePlaceholder}"/>
    					    <form:errors path="name" cssClass="formError" element="p"/>
    					  </div>
    					  <div class="form-group">
    					    <form:label for="group-description" path="description">
								<spring:message code="groupDescription.title"/>
							</form:label>
    					    <form:textarea path="description" class="form-control" id="group-description" rows="6"></form:textarea>
    					    <form:errors path="description" cssClass="formError" element="p"/>
    					  </div>
    					  <button type="submit" class="create-group-btn app-btn-primary">
									<spring:message code="createGroupConfirmation.button.message"/>
						  	</button>
								<a href="<c:url value="/"/>">
	    					  <button type="button" class="app-btn-secondary">
										<spring:message code="cancelGroupCreation.button.message"/>
	   						  </button>
								</a>
    				</form:form>
    			</div>
    		</div>
    	</div>
		<%@include file="footer.jsp" %>
	</body>
	<%@include file="scripts.jsp" %>
</html>
