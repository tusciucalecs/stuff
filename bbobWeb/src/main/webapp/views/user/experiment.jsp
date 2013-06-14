<!DOCTYPE html >
<%@include file="../imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<html class="no-js">
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<title>User Home Page</title>
<jsp:include page="../resources.jsp" />
</head>

<body>
 <jsp:include page="../menu.jsp" />
 <div class="main">
  User:
  <p>
   Welcome,
   <c:out value="${account.user.name}" />
   !
  </p>
  
  <c:if test="${running}">
  	The experiment is already running!
  </c:if>
  <c:if test="${not running}">
  	The experiment is not running. You can start one!<br/>
  	<a href="<c:url value="/user/startExperiment" />">Start</a>
  </c:if>
 </div>
</body>
</html>


