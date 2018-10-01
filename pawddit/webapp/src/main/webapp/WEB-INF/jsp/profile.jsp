<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
	<head>
		<meta charset="UTF-8">
		<title>
			Pawddit | <spring:message code="profile.title"/>
		</title>
		<meta name="description" content="feed">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="icon" href="<c:url value="/resources/images/tab-logo.png" />">
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/application.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/buttons.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/profile.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/account.css" />" />
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
		<link href="https://fonts.googleapis.com/css?family=Kosugi" rel="stylesheet">
		<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU" crossorigin="anonymous">
	</head>
	<body class="main-font">
		<%@include file="navbar.jsp" %>
		<div class="application-background">
			<div class="center-content">
				<div class="profile">
					<div class="profile-component">
						<div class="user-image-container">
							<i class="fas fa-user fa-7x"></i>
						</div>
						<h2 class="username-title"><c:out value="${userProfile.username}" escapeXml="true"/></h2>
						<p class="activity-title"> placeholder joined 13/04/1999 <br> placeholer data.</p>
					</div>
					<div class="activity-component">
						<h4 class="margin-title">
							<spring:message code="profileActivity.title"/>
						</h4>
							<nav class="activity">
							  <div class="nav nav-tabs" id="nav-tab" role="tablist">
							    <a class="nav-item nav-link active" id="nav-posts-tab" data-toggle="tab" href="#nav-posts" role="tab" aria-controls="nav-posts" aria-selected="true">
									<spring:message code="profilePosts.title"/>
								</a>
							    <a class="nav-item nav-link" id="nav-comments-tab" data-toggle="tab" href="#nav-comments" role="tab" aria-controls="nav-comments" aria-selected="false">
										<spring:message code="profileComments.title"/>
									</a>
							    <a class="nav-item nav-link" id="nav-upvotes-tab" data-toggle="tab" href="#nav-upvotes" role="tab" aria-controls="nav-contact" aria-selected="false">
									<spring:message code="profileUpvotes.title"/>
								</a>
			 				 </div>
							</nav>
							<div class="tab-content" id="nav-tabContent">
			  					<div class="tab-pane fade show active" id="nav-posts" role="tabpanel" aria-labelledby="nav-posts-tab">
										<c:if test="${empty posts}">
											<br>
					  					<strong>
					  						<spring:message code="userDoesNotHavePosts.message" arguments="${userProfile.username}" />
					  					</strong>
										</c:if>
									<c:forEach items="${posts}" var="post">
										<div class="post-container">
											<div class="post-header">
												<span class="header-button clickable" onclick='window.location="<c:url value='/profile/${post.owner.username}'/>"'><c:out value="${post.owner.username}" escapeXml="true"/></span>
												<span>
													<strong>
														<spring:message code="postedIn.message"/>
													</strong>
												</span>
												<a class="no-underline" href="<c:url value="/group/${post.group.name}"/>">
													<span class="header-button group-name"><c:out value="${post.group.name}" escapeXml="true"/></span>
												</a>
												<span><strong><time class="timeago" datetime='<c:out value="${post.date}" escapeXml="true"/>'></time></strong></span>
											</div>
											<hr>
											<h2 class="clickable" onclick='window.location="<c:url value='/group/${post.group.name}/${post.postid}'/>"'><c:out value="${post.title}" escapeXml="true"/></h2>
											<div class="post-description-text">
												<c:out value="${post.content}" escapeXml="true"/>
											</div>
											<hr>
											<div class="post-info">
												<div class="info-item">
															<strong><i class="fas fa-comment"></i>
															<c:out value="${post.comments}" escapeXml="true"/> <spring:message code="comments.message"/>
															</strong>
													</div>
												<div class="info-item">
													<a class="no-underline" href="<c:url value=""/>">
														<i class="fas fa-arrow-up icon-color"></i>
													</a>
													<strong class="score-count"><c:out value="${post.votes}" escapeXml="true"/></strong>
													<a class="no-underline" href="<c:url value=""/>">
														<i class="fas fa-arrow-down icon-color"></i>
													</a>
												</div>
											</div>
										</div>
									</c:forEach>
								</div>
			  				<div class="tab-pane fade" id="nav-comments" role="tabpanel" aria-labelledby="nav-comments-tab">
								<br>
								<strong>
									<spring:message code="userDoesNotHaveComments.message" arguments="${userProfile.username}"/>
								</strong>
								</div>
			  				<div class="tab-pane fade" id="nav-upvotes" role="tabpanel" aria-labelledby="nav-upvotes-tab">
			  					<br>
			  					<strong>
			  						<spring:message code="userDoesNotHaveUpvotes.message" arguments="${userProfile.username}"/>
			  					</strong>
			  				</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		<%@include file="footer.jsp" %>
	</body>
	<%@include file="scripts.jsp" %>
</html>
