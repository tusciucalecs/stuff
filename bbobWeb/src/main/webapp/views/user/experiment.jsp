<!DOCTYPE html >
<%@include file="../imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<html class="no-js">
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<title>Experiment</title>
<jsp:include page="../resources.jsp" />
</head>

<body>
	<jsp:include page="../menu.jsp" />
	<div class="main">
        One experiment is gonna run all 4 algorithms (DDE, JADE, DE and DEASP)<br/> 
        on the functions { 1, 2, 3, 4, 5, 15, 16, 17, 18, 19 }(instance 1) from the COCO library<br/>
        using 30 as dimension.<br/>
        Only one experiment can be ran at the same moment by all users.<br/>
        <br/>
		<c:if test="${running}">
  	     One experiment is running!
        </c:if>
		<c:if test="${not running}">
  	The experiment is not running. You can start one!<br />
			<a href="<c:url value="/user/startExperiment" />">Start</a>
		</c:if>
	</div>
</body>
</html>


