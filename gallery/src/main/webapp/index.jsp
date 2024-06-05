<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.container img {
	width: 80%;
	animation-duration: 3s;
  	animation-name: slidein;
}

@keyframes slidein {
  from {
    margin-left: 100%;
    width: 80%;
  }

  to {
    margin-left: 0%;
    width: 80%;
  }
}
</style>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main class="container p-0" onclick="location.href='${pageContext.request.contextPath}/main'";>
	<div class="text-center">
		<img src="${pageContext.request.contextPath}/resources/images/door.png">
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>