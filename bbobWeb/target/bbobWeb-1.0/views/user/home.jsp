<%@include file="../imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>
User:
<p>
	Welcome,
	<c:out value="${account.user.name}" />
	!
</p>
<a href="<c:url value="j_spring_security_logout" />" >Logout</a>
