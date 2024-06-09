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

#gallery_fonts {
	font-family: LOTTERIACHAB;
}



button {
  margin: 20px;
}
.custom-btn {
  width: 130px;
  height: 40px;
  color: #fff;
  border-radius: 5px;
  padding: 10px 25px;
  font-family: 'Lato', sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
   box-shadow:inset 2px 2px 2px 0px rgba(255,255,255,.5),
   7px 7px 20px 0px rgba(0,0,0,.1),
   4px 4px 5px 0px rgba(0,0,0,.1);
  outline: none;
}


.btn-13 {
  background-color: #89d8d3;
background-image: linear-gradient(315deg, #F7BE81 0%, #F7BE81 74%);
  border: none;
  z-index: 1;
  font-family: DNFBitBitv2;
  
}
.btn-13:after {
  position: absolute;
  content: "";
  width: 100%;
  height: 0;
  bottom: 0;
  left: 0;
  z-index: -1;
  border-radius: 5px;
   background-color: #4dccc6;
background-image: linear-gradient(315deg, #F7BE81 0%, #FFFFFF 74%);
  box-shadow:
   -7px -7px 20px 0px #fff9,
   -4px -4px 5px 0px #fff9,
   7px 7px 20px 0px #0002,
   4px 4px 5px 0px #0001;
  transition: all 0.3s ease;
}
.btn-13:hover {
  color: #fff;
}
.btn-13:hover:after {
  top: 0;
  height: 100%;
}
.btn-13:active {
  top: 2px;
}




</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/assets/css/main.css">

</head>
<body class="is-preload">

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container" id="wrapper">
		<section id="main">
						<!-- Items -->
							<div class="items">

								<div class="item intro span-2" id="gallery_fonts">
									<h1>Pat & Mat</h1>
									<p>이곳은 Pat&Mat 갤러리입니다<br/>
									깜찍한 두 분의 작품을 감상하세요<br></p>
									
								</div>
								
						    <c:forEach var="dto" items="${list1}" varStatus="status">
								<article class="item thumb span-2" onclick="location.href='${articleUrl}&num=${dto.num}';">
									<h2>${dto.introduce}</h2>
									<a href="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" class="image"><img src="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" alt=""></a>
								</article>
							</c:forEach>
								

							</div>
							
							

						<!-- Items -->
							<div class="items">

							    <c:forEach var="dto" items="${list2}" varStatus="status">
								<article class="item thumb span-3" onclick="location.href='${articleUrl}&num=${dto.num}';">
									<h2>${dto.introduce}</h2>
									<a href="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" class="image"><img src="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" alt=""></a>
								</article>
							</c:forEach>
							</div>
							

					</section>
					 <div class="col-auto" >
				   		<c:if test="${sessionScope.member.userId=='admin'}">
				   			<button type="button" class="btn btn-13"  onclick="location.href='${pageContext.request.contextPath}/gallery/write';">작품 등록</button>
				   		</c:if>
				   </div>

						


				<!-- Footer -->
					<section id="footer">
						<section>
							<p><strong><a href="${pageContext.request.contextPath}/main ">Pat&Mat</a></strong>갤러리는 
							현재 가장 훌륭하신 분의 의해 운영되고 있습니다.<br> 
							자, 모든 훌륭한 작품을 감상하셔보십시오.  
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
								<li>&copy; Untitled</li><li>Design: <a href="http://html5up.net">K Y J</a></li>
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