<!DOCTYPE html >
<%@include file="imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<link rel="stylesheet" type="text/css"
 href="<c:url value="/resources/css/style.css" />">
<script type="text/javascript"
 src="<c:url value="/resources/js/libs/jquery/jquery-1.7.1.min.js" />"></script>
 <script type="text/javascript"
 src="<c:url value="/resources/js/libs/json/json-2.3.min.js" />"></script>
<link rel="stylesheet"
 href="<c:url value="/resources/css/jquery-ui-1.8.19.custom.css" />"
 type="text/css" />
<script type="text/javascript"
 src="<c:url value="/resources/js/libs/jquery/jquery-ui-1.8.19.custom.min.js" />"></script>
 
<script type="text/javascript">
    $.ajaxSetup({
        cache : false
    });
</script>

<script type="text/javascript">
    var bbobContext = "<%= response.encodeURL(request.getContextPath()) %>";
</script>