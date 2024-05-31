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
.body-container {
	max-width: 800px;
}
.slideimg {
	height: 500px;
}
.introduce {
	text-align: center;
	font-size: 80px;
	font-family: DNFBitBitv2;
}
#context {
	font-size: 30px;
	
}
</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container-fluid p-0" >
		<div id="carouselExampleAutoplaying" class="carousel slide" data-bs-ride="carousel">
		  <div class="carousel-inner">
		    <div class="carousel-item active">
		      <img src="${pageContext.request.contextPath}/resources/images/main_slide1.jpg" class="d-block w-100 slideimg">
		    </div>
		    <div class="carousel-item">
		      <img src="${pageContext.request.contextPath}/resources/images/main_slide2.jpg" class="d-block w-100 slideimg">
		    </div>
		    <div class="carousel-item">
		      <img src="${pageContext.request.contextPath}/resources/images/main_slide3.jpg" class="d-block w-100 slideimg">
		    </div>
		  </div>
		  <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleAutoplaying" data-bs-slide="prev">
		    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
		    <span class="visually-hidden">Previous</span>
		  </button>
		  <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleAutoplaying" data-bs-slide="next">
		    <span class="carousel-control-next-icon" aria-hidden="true"></span>
		    <span class="visually-hidden">Next</span>
		  </button>
		</div>
		<div class="introduce">
			<p>
				PAT&amp;MAT<br>
				GALLERY
			<p id="context">
					PAT&amp;MAT은  PAT 작가와 MAT 작가의 갤러리 홈페이지 입니다. <br>
				<br><br>

		</div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>