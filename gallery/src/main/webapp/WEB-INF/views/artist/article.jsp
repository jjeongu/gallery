<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>작가</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}

.slideimg {
	height: 300px;
}
.introduce {
	text-align: center;
	font-size: 80px;
}
#context {
	font-size: 50px;	
}

.container {
font-family: ONE-Mobile-POP;
}

</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

<c:if test="${sessionScope.member.userId=='admin'}">
	<script type="text/javascript">
		function deleteBoard() {
		    if(confirm("아티스트를 삭제 하시 겠습니까 ? ")) {
		    	let query = "member_id=${dto.member_id}";
			    let url = "${pageContext.request.contextPath}/artist/delete?" + query;
		    	location.href = url;
		    }
		}
	</script>
</c:if>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>

<!-- carousel -->
	<div class="container-fluid p-0" >
		<div id="carouselExampleAutoplaying" class="carousel slide" data-bs-ride="carousel">
		  <div class="carousel-inner">
		    <div class="carousel-item active">
		      <img src="${pageContext.request.contextPath}/resources/images/artist/PAT3.jpg" class="d-block w-100 slideimg">
		      <div class="carousel-caption d-none d-md-block fw-bold">
        		<h5 class="fs-3 , fw-bold" >PAT & MAT GALLERY </h5>
        		<p class="fs-5 , fw-bold"> PAT 작가님</p>
              </div>
		    </div>
		    <div class="carousel-item">
		      <img src="${pageContext.request.contextPath}/resources/images/artist/MAT1.jpg" class="d-block w-100 slideimg">
		      <div class="carousel-caption d-none d-md-block">
        		<h5 class="fs-3 , fw-bold">PAT & MAT GALLERY </h5>
        		<p class="fs-5 , fw-bold"> MAT 작가님</p>
              </div>
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
	</div>


 <div class="container">
	   <div class="row my-5 ms-auto">
	     <div class="col-6">
				<img src="${pageContext.request.contextPath}/uploads/artist/${dto.img}" 
									class="rounded-circle">
	   	 </div>
        <div class="col-6" >
            <h1 class="fs-1 , fw-bold" >Profile</h1>
           	<p class="fs-3 , fw-bold" > ${dto.name} </p> 
            <p class="fs-5" > Birth :  ${dto.birth} </p>
            <p class="fs-5"  > Site :
            	<a href="#" class="link-danger link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover"><i class="fa-brands fa-square-instagram"></i></a> |
            	<a href="#" class="link-primary link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover"><i class="fa-brands fa-twitter "></i></a> |
            	<a href="#" class="link-danger link-offset-2 link-underline-opacity-25 link-underline-opacity-100-hover"><i class="fa-brands fa-youtube"></i></a>
            </p>
        </div>
        
        
        <div class="row my-5 ms-auto">
	        <div class="col-6">
	       		 <h1 class="fs-3 , fw-bold" >INTRODUCE</h1>
	        </div>
	        <div class="col-6">
	             <p class="fs-6" > ${dto.introduce} </p>
	        </div>
	    </div>
        
       <hr class="border body-tertiary border-1 opacity-75">
        
        <div class="row my-5 ms-auto">
	        <div class="col-6">
	       		 <h1 class="fs-3 , fw-bold" >CAREER</h1>
	        </div>
	        <div class="col-6">
	             <p class="fs-6" > ${dto.career} </p>
	        </div>
	    </div>
	    
	    <hr class="border body-tertiary border-1 opacity-75">
        
        
       <div class="row my-5 ms-auto">
	        <div class="col-6">
	       		 <h1 class="fs-3 , fw-bold" >Works</h1>
	        </div>
	        <div class="col-6">
	              <p class="fs-6" > ${dto.represent} </p>
	        </div>
	    </div>
    </div>

			
				<table class="table table-borderless">
					<tr>
						<td width="50%">
							<c:if test="${sessionScope.member.userId=='admin'}">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/artist/update?member_id=${dto.member_id}';">수정</button>
							</c:if>
						
				    		<c:if test="${sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn btn-light" onclick="deleteBoard();">삭제</button>
				    		</c:if>
						</td>
						<td class="text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/artist/list';">리스트</button>
						</td>
					</tr>
				</table>

</div>

			
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>