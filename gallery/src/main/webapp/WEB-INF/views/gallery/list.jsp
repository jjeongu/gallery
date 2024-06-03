<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1 , user-scalable=no" />
<title>spring</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 100%;
	background-color: lightred;
}
	
.body-title {
	text-align: center;	
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/main.css">

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>



	<div class="container" id="wrapper">
		<section id="main">
		

						<!-- Items -->
							<div class="items">

								<div class="item intro span-2">
									<h1>Parallelism</h1>
									<p>A responsive portfolio site<br />
									template by KYJ</p>
									
								</div>
							<c:forEach var="dto" items="${list}" varStatus="status">
								<article class="item thumb span-2" onclick="location.href='${articleUrl}&num=${dto.num}';">
									<h2>${dto.introduce}</h2>
									<a href="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" class="image"><img src="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" alt=""></a>
								</article>
							</c:forEach>
								

							</div>
							
							

						<!-- Items -->
							<div class="items">

								<article class="item thumb span-3"><h2>Kingdom of the Wind</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/gallery_img2jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/gallery_img2jpg" alt=""></a></article>
								<article class="item thumb span-1"><h2>The Pursuit</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/06.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/06.jpg" alt=""></a></article>
								<article class="item thumb span-2"><h2>Boundless</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/07.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/07.jpg" alt=""></a></article>
								<article class="item thumb span-2"><h2>The Spectators</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/08.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/08.jpg" alt=""></a></article>
								<article class="item thumb span-1"><h2>You really got me</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/01.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/01.jpg" alt=""></a></article>
								<article class="item thumb span-2"><h2>Ad Infinitum</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/02.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/02.jpg" alt=""></a></article>
								<article class="item thumb span-1"><h2>Different.</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/03.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/03.jpg" alt=""></a></article>
								<article class="item thumb span-2"><h2>Kingdom of the Wind</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/05.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/05.jpg" alt=""></a></article>
								<article class="item thumb span-1"><h2>Elysium</h2><a href="${pageContext.request.contextPath}/resources/images/fulls/04.jpg" class="image"><img src="${pageContext.request.contextPath}/resources/images/thumbs/gallery_img2jpg" alt=""></a></article>

							</div>
							

					</section>
					 <div class="col-auto">
				   		<c:if test="${sessionScope.member.userId=='admin'}">
				   			<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/gallery/write';">사진올리기</button>
				   		</c:if>
				   </div>

				<!-- Footer -->
					<section id="footer">
						<section>
							<p>This is <strong>Parallelism</strong>, a responsive portfolio site template by <a href="http://html5up.net">HTML5 UP</a>. Free for personal
							and commercial use under the <a href="http://html5up.net/license">Creative Commons Attribution</a> license.</p>
						</section>
						<section>
							<ul class="icons">
								<li><a href="#" class="icon brands fa-twitter"><span class="label">Twitter</span></a></li>
								<li><a href="#" class="icon brands fa-instagram"><span class="label">Instagram</span></a></li>
								<li><a href="#" class="icon brands fa-facebook-f"><span class="label">Facebook</span></a></li>
								<li><a href="#" class="icon brands fa-dribbble"><span class="label">Dribbble</span></a></li>
								<li><a href="#" class="icon solid fa-envelope"><span class="label">Email</span></a></li>
							</ul>
							<ul class="copyright">
								<li>&copy; Untitled</li><li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
							</ul>
						</section>
					</section>
					
					
					
					
					

			</div>
</main>			


			
			
		<!-- Scripts -->
			<script src="${pageContext.request.contextPath}/resources/assets/js/jquery.poptrox.min.js"></script>
			<script src="${pageContext.request.contextPath}/resources/assets/js/browser.min.js"></script>
			<script src="${pageContext.request.contextPath}/resources/assets/js/breakpoints.min.js"></script>
			<script src="${pageContext.request.contextPath}/resources/assets/js/util.js"></script>
			<script src="${pageContext.request.contextPath}/resources/assets/js/main.js"></script>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>