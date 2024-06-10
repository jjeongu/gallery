<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>작가님 소개</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.artistlist {
	width: 100%;
	margin: 0 auto;
}

.img-thumbnail {
	width: 300px;
	height: 300px;
}

/*ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ*/


.artistlist ul > li {
	display: inline-block; /*요소의 속성선언*/
	vertical-align: top; /*inline의 세로정렬*/
	width: 50%;
}

.artistlist ul > li > a {
	display: bolock;
	width: auto;
	text-decoration: none;
	margin: 5px;
	
}

.artistlist ul > li > a .artist {
	position: relative;
	overflow: hidden;
}


.artistlist ul > li > a .artist .top {
	position:  absolute;
	bottom: 150%;
	left: 30px;
	z-index: 2;
	color: #fff;
	font-size: 26px;
	font-weight: 900;
	transition: all .35s;
}

.artistlist ul > li > a .artist .botton {
	position:  absolute;
	bottom: 150%;
	left: 30px;
	z-index: 2;
	color: #fff;
	font-size: 12px;
	transition: all .35s;
}

.artistlist ul > li > a .screen img {
	width: 100%;
}

.artistlist ul > li > a:hover .top {
	bottom: 52%;
}

.artistlist ul > li > a:hover .botton {
	top: 52%;
}

.artistlist ul > li > a .artist::after {
	content: '';
	display: block;
	position: absolute;
	top: 0;
	left: 0;
	width: 97%;
	height: 100%;
	background: rgba(0,0,0,.5);
	z-index: 1;
	opacity: 0;
	transition:all .35s;
}

.artistlist ul > li > a:hover .artist::after {
	opacity: 1;
}
/*ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ*/

</style>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}

</script>


</head>

<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<div>
				<h3><i class="fa-solid fa-palette"></i> 아티스트 </h3>
				<hr class="border border-danger border-2 opacity-75">
			</div>
			
			<div class="body-main">
				<div class="artistlist">
					<ul class="row">
						<c:forEach var="art" items="${list}">
							<li class="col">
								<a href="${articleUrl}&member_id=${art.member_id}">
									<div class="artist">
										<div class="top" >${art.name}</div>
										<div class="botton"> ${art.name} 작가입니다.</div>
										<img src="${pageContext.request.contextPath}/uploads/artist/${art.img}" class="img-thumbnail">
									</div>	
								</a>
							</li>
						</c:forEach>
					</ul>
						
					<div class="col-auto">
				   		<c:if test="${sessionScope.member.userId=='admin'}">
				   			<button type="button" class="btn btn-outline-danger" for="danger-outlined" style="background: #ffffff; --bs-btn-padding-y: .25rem; --bs-btn-padding-x: .5rem; --bs-btn-font-size: .75rem;" onclick="location.href='${pageContext.request.contextPath}/artist/write';">작가 등록</button>
				   		</c:if>
				   </div>
				</div>
			</div>
		</div>
	</div>							

</main>			

<footer>
	<c:import url="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<c:import url="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>