<!DOCTYPE html >
<%@include file="imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>


<div class="menu">
	<img src='<c:url value="/resources/img/images.JPG" />' />
	<div>
		Logged in as:<br /> ${account.user.name}<br />
	</div>
	<a href="<c:url value="/user/experiment" />">Experiment</a><br />
	<a href="<c:url value="/user/statistics" />">Statistics</a><br />
	<br />
	<a href="<c:url value="/j_spring_security_logout" />">Logout</a>
</div>