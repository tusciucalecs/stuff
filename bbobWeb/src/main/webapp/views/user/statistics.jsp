<!DOCTYPE html >
<%@include file="../imports.jsp"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<html class="no-js">
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
<title>Statistics</title>
<jsp:include page="../resources.jsp" />
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
	google.load('visualization', '1', {
		packages : [ 'corechart' ]
	});
	google.setOnLoadCallback(draw);

	function draw() {
		var matrix, divId;
		<c:forEach items="${statistics}" var="statistic" varStatus="index">
			$("#visualization").append('<fieldset style="float:left;"><legend>${statistic.name}</legend><div id="${statistic.name}"></div></fieldset>');
			divId = "${statistic.name}";
			matrix = ${statistic.printMatrix()};
			drawVisualization(matrix, divId);
		</c:forEach>
		// 		var matrix = [ [ 'Mon', 20, 20, 20, 20 ], [ 'Tue', 31, 38, 55, 66 ],
		// 				[ 'Wed', 50, 55, 77, 80 ], [ 'Thu', 120, 77, 66, 50 ] ];
		// 		var divId = 'visualization';
		// 		drawVisualization(matrix, divId);
	}
	function drawVisualization(matrix, divId) {
		// Populate the data table.
		var dataTable = google.visualization.arrayToDataTable(matrix, true);

		// Draw the chart.
		var chart = new google.visualization.CandlestickChart(document
				.getElementById(divId));
		chart.draw(dataTable, {
			legend : 'none',
			width : 450,
			height : 300
		});
	}
</script>
</head>

<body>
	<jsp:include page="../menu.jsp" />
	<div class="main">
	    Statistics for:
		<ul id="triple">
			<li><a href="<c:url value="/user/statistics" />">Overall statistic</a></li>
			<c:forEach items="${experiments}" var="experiment" varStatus="index">
				<li><a href="<c:url value="/user/statistics?experiment=${experiment.id}" />">
					Experiment ${experiment.id} ran by ${experiment.user.name} on ${experiment.date}</a></li>
			</c:forEach>
		</ul>
		<div id="visualization"></div>
	</div>
</body>
</html>


