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
	
	flex-wrap: wrap;
	
}
	
	
.body-title {
	text-align: center;	
}

.body-title h3 {
	font-size: 50px;	
	padding-bottom: 20px;
}





#close-btn {
	position: absolute;
	right: 10px;
	top: 10px;
	font-size: 40px;
	color: #fff;
	cursor: pointer;
	z-index:2;
}




/*
#lightbox button {
	position: absolute;
	top: 50%;
	transform: translateY(-50%);
	font-size: 20px;
	color: #fff;
	background: rgba(0,0,0,.5);
	border: none;
	padding: 10px;
	transition: background-color 0.3s;
	cursor: pointer;
}

#prev-btn {
	left: 10px;
}

#next-btn{
	right: 10px;
}

#lightbox button: hover {
	background-color : rgba(0,0,0,.8);
}
*/



.item {cursor: pointer;  background: #DC143C;}
.item img { display: block; width: 100%; height: 200px; border-radius: 5px; }
.item img:hover { scale: 101.7%; }
.item .item-title {
	font-size: 14px;
	font-weight: 500;
	padding: 10px 2px 0;
	
	width: 175px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	
}


/*
.row {
	display: flex;
	justify-content: center;
	padding: 20px;
	flex-wrap: wrap;
}

.row img {
	margin: 10px;
	cursor: pointer;
	max-width: 200px;
	width: 50%;
	border-radius: 10px;
}

#lightbox {
	position: fixed;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	display: flex;
	display: none;
	justify-content: center;
	align-items: center;
	flex-direction: column;
	background: rgba(0,0,0,.8);
}


#lightbox-img {
	max-width: 80%;
	min-height: 60vh;
	box-shadow:  0 0 25px; rgba(0,0,0,.8);
	border-radius: 10px;
}

#thumbnail-container {
	
	margin-top: 40px;
	display: flex;
	
	flex-direction: row;
	flex-wrap: wrap;
	justify-content: center;
	gap: 10px;
	
}

.thumbnail {
	border-radius: 10px;
	width: 100px;
	cursor: pointer;
	border: 2px solid #fff;
	transition: opacity 0.3s;
	
}

.thumbnail: hover,
.thumbnail.active-thumbnail {
	opacity: 0.7;
}
*/

</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<div class="body-title">
				<h3 class="border-bottom border-danger border-3"  >갤러리</h3>
			</div>
			
			<div class="body-main">
				<div class="row mb-2 list-header">
					<div class="col-auto me-auto">
						<p class="form-control-plaintext">
							${dataCount}개(${page}/${total_page} 페이지)
						</p>
					</div>
				   <div class="col-auto">
				   		<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/gallery/write';">사진올리기</button>
				   </div>
				  </div>
				  
				  <div class="row row-cols-4 px-1 py-1 g-2">
				  		<!-- 그림 -->

				  			<c:forEach var="dto" items="${list}" varStatus="status">
						 			<div class="col border rounded p-1 item"
						 				onclick="location.href='${articleUrl}&num=${dto.num}';">
						 				<img src="${pageContext.request.contextPath}/uploads/gallery/${dto.img}">
						 				
						 			</div>
						 	</c:forEach>
				  		
				  		
				  		
				  		
				  		
		
				  		<!-- 그림 
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		<img src=" ${pageContext.request.contextPath}/resources/images/gallery_img1.jpg">
				  		-->
				  		
				  </div>
				  
				  <!-- 미리보기화면
				  <div id="lightbox">
				  	<span id="close-btn">&times;</span>
				  	
						<img img id="lightbox-img" src="" alt="lightbox image" > 				  	
				  		<img id="lightbox-img" src="${pageContext.request.contextPath}/resources/images/gallery_img1.jpg" alt="lightbox image" > 
				  		<div id="thumbnail-container">
					  		<img id="lightbox-img" src="${pageContext.request.contextPath}/resources/images/gallery_img1.jpg" alt="Thumbnail 1" class="thumbnail active-thumbnail"> 
					  		<img id="lightbox-img" src="${pageContext.request.contextPath}/resources/images/gallery_img1.jpg" alt="Thumbnail 1" class="thumbnail active-thumbnail"> 
					  		<img id="lightbox-img" src="${pageContext.request.contextPath}/resources/images/gallery_img1.jpg" alt="Thumbnail 1" class="thumbnail active-thumbnail"> 
					  		<img id="lightbox-img" src="${pageContext.request.contextPath}/resources/images/gallery_img1.jpg" alt="Thumbnail 1" class="thumbnail active-thumbnail"> 				  		
				  		</div>
				  	<button id="prev-btn">&lt; Prev</button>
				  	<button id="next-btn">Next &gt;</button>
				  	
				  </div>
				  <script src="./script.js"></script>
				  -->
				  
				  <div class="page-navigation">
				  	${dataCount == 0 ? "등록된 게시물 없습니다." : paging }
				  </div>
				  
				
			</div>
		</div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>