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
				  		
				  		
				  		
				  		
				  		
		
				  		
				  		
				  </div>
				  
				  
				  
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