<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
	<head>
		<meta charset="UTF-8">
		<c:if test="${empty group}">
		<title>
			Pawddit | <spring:message code="homePage.title"/>
		</title>
		<meta name="description" content="<spring:message code="meta.description.feed.message"/>">
		</c:if>
		<c:if test="${!empty group}">
		<title>
			Pawddit | <c:out value="${group.name}" escapeXml="true"/>
		</title>
		<meta name="description" content="<spring:message code="meta.description.group.message"/>">
		</c:if>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="icon" href="<c:url value="/resources/images/tab-logo.png" />">
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/app.css" />" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/group.css" />" />
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
		<link href="https://fonts.googleapis.com/css?family=Kosugi" rel="stylesheet">
		<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU" crossorigin="anonymous">
	</head>
	<body class="main-font">
		<%@include file="navbar.jsp" %>
		<div class="application-background">
			<div class="center-content">
				<c:if test="${!empty group}">
					<%@include file="group.jsp" %>
				</c:if>
				<div class="timeFilterControls">
					<div class="time-indicator"><c:out value="${param.time}" escapeXml="true"/></div>
					<strong><spring:message code="time.filterBy.message"/></strong>
					<div>
						<a class="timeFilterButton js-lhour" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=lasthour&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.lastHour.message"/>
						</a>
					</div>
					|
					<div>
						<a class="timeFilterButton js-lday" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=lastday&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.lastDay.message"/>
						</a>
					</div>
					|
					<div>
						<a class="timeFilterButton js-lweek" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=lastweek&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.lastWeek.message"/>
						</a>
					</div>
					|
					<div>
						<a class="timeFilterButton js-lmonth" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=lastmonth&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.lastMonth.message"/>
						</a>
					</div>
					|
					<div>
						<a class="timeFilterButton js-lyear" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=lastyear&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.lastYear.message"/>
						</a>
					</div>
					|
					<div>
						<a class="timeFilterButton js-lall" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=all&page=1" escapeXml="true"/>">
							<spring:message code="filter.button.all.message"/>
						</a>
					</div>
				</div>
				<c:if test="${empty posts}">
					<c:if test="${empty user}">
						<h2 class="no-posts"><spring:message code="noPosts.made.signedOut.message"/></h2>
					</c:if>
					<c:if test="${!empty user}">
						<h2 class="no-posts"><spring:message code="noPosts.made.signedIn.message"/></h2>
					</c:if>
				</c:if>
				<c:forEach items="${posts}" var="post">
					<div class="post-container">
						<div class="post-header">
							<span class="header-button clickable" onclick='window.location="<c:url value='/profile/${post.owner.username}'/>"'><c:out value="${post.owner.username}" escapeXml="true"/></span>
							<span><strong>
								<spring:message code="postedIn.message"/>
							</strong></span>
							<a class="no-underline" href="<c:url value="/group/${post.group.name}"/>">
								<span class="header-button group-name"><c:out value="${post.group.name}" escapeXml="true"/></span>
							</a>
							<span><strong><time class="timeago" datetime='<c:out value="${post.date}" escapeXml="true"/>'></time></strong></span>
						</div>
						<hr>
						<div class="clickable post-center"  onclick='window.location="<c:url value='/group/${post.group.name}/${post.postid}'/>"'>
							<div class="post-center-text">
								<h2 class="post-wrap"><c:out value="${post.title}" escapeXml="true"/></h2>
								<div class="post-description-text post-wrap">
									<c:out value="${post.content}" escapeXml="true"/>
								</div>
							</div>
							<c:if test="${!empty post.imageid}">
								<img class="post-center-image" src="<c:url value="/image/${post.imageid}"/>" />
							</c:if>
						</div>
						<hr>
						<div class="post-info">
							<div class="info-item">
		         				<strong><i class="fas fa-comment"></i>
		         				<c:out value="${post.comments}" escapeXml="true"/> <spring:message code="comments.message"/>
		         				</strong>
		     				</div>
							<div class="info-item">
								<strong class="score-count"><c:out value="${post.votes}" escapeXml="true"/> <spring:message code="posts.votes"/> </strong>
		     				</div>
						</div>
					</div>
				</c:forEach>
				<br>
				<c:if test="${postsPageCount > 1}">
				<nav aria-label="...">
  					<ul class="pagination">
  						<c:choose>
  							<c:when test="${postsPage eq 1}">
    							<li class="page-item disabled">
      								<span class="page-link"><-</span>
    							</li>
    						</c:when>
    						<c:otherwise>
            					<li class="page-item">
      								<a class="page-link" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=${param.time != null ? param.time : 'all'}&page=${postsPage-1}" escapeXml="true"/>"><-</a>
    							</li>
         					</c:otherwise>
         				</c:choose>
    					<c:forEach var="i" begin="1" end="${postsPageCount}">
    						<c:choose>
  								<c:when test="${postsPage eq i}">
  									<li class="page-item active">
      									<span class="page-link"><c:out value="${i}" escapeXml="true"/><span class="sr-only">(current)</span></span>
    								</li>
    							</c:when>
    							<c:otherwise>
    								<li class="page-item"><a class="page-link" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=${param.time != null ? param.time : 'all'}&page=${i}" escapeXml="true"/>"><c:out value="${i}" escapeXml="true"/></a></li>
    							</c:otherwise>
         					</c:choose>
    					</c:forEach>
    					<c:choose>
    						<c:when test="${postsPage eq postsPageCount}">
    							<li class="page-item disabled">
      								<span class="page-link">-></span>
    							</li>
    						</c:when>
    						<c:otherwise>
    							<li class="page-item">
      								<a class="page-link" href="<c:out value="?sort=${param.sort != null ? param.sort : 'new'}&time=${param.time != null ? param.time : 'all'}&page=${postsPage+1}" escapeXml="true"/>">-></a>
    							</li>
    						</c:otherwise>
         				</c:choose>

  					</ul>
				</nav>
				</c:if>
			</div>
		</div>
		<%@include file="footer.jsp" %>
	</body>
	<%@include file="scripts.jsp" %>
</html>
