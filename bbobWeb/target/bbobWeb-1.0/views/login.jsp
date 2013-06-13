<!DOCTYPE html >
<%@include file="imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<html>
<head>
<title><fmt:message key="cipis" /></title>
</head>


<body>
	<div>
		<form method="post"
			action="<c:url value="/j_spring_security_check" />">

			<div>
				<label for="j_username"><fmt:message key="login.username" /></label>
				<input type="text" name="j_username" id="j_username" />
			</div>

			<div>
				<label for="jpass"><fmt:message key="login.password" /></label> <input
					type="password" name="j_password" id="jpass" />
			</div>

			<div>
				<input type="submit" value="<fmt:message key="login.login.button"/>" />
			</div>
			<hr>
		</form>

		<div>
			<c:if test="${not empty param.error}">
				<span>
					${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </span>
				<span> Login error</span>
			</c:if>
		</div>
	</div>
</body>
</html>



