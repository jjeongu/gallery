<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib  prefix="c" uri="jakarta.tags.core" %>
<%@ taglib  prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}
</style>

<c:if test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userId=='admin'}">
	<script type="text/javascript">
		function deleteBoard() {
		    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    	let query = "num=${dto.num}&page=${page}";
			    let url = "${pageContext.request.contextPath}/gallery/delete?" + query;
		    	location.href = url;
		    }
		}
	</script>
</c:if>


<link rel="icon" href="data:;base64,iVBORw0KGgo=">
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>




<main>
	<div class="container">
		<div class="body-container">	
			<div>
				<h3><i class="bi bi-image"></i> 갤러리 </h3>
				<hr class="border border-warning border-2 opacity-75">
			</div>
			
			<div class="body-main">
				
				<table class="table">

					<thead>
						<tr>
							<td>
							<strong><h2>${dto.artistName}</h2></strong> 
								
							</td>
						</tr>
					</thead>
					<tbody>
						<tr>
							
							<td align="right">
								${dto.reg_date}
							</td>
						</tr>
						
						<tr>
							<td colspan="2" style="border-bottom: none;">
								<img src="${pageContext.request.contextPath}/uploads/gallery/${dto.img}" 
									class="img-fluid img-thumbnail w-100 h-auto">
							</td>
						</tr>
						
						<tr>
							<td colspan="2">
								${dto.introduce}
							</td>
						</tr>
						
					</tbody>
				</table>
				
				<table class="table table-borderless">
					<tr>
						<td width="50%">
							<c:choose>
								<c:when test="${sessionScope.member.userRole==0}">
									<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/gallery/update?num=${dto.num}&page=${page}';">수정</button>
								</c:when>
								
							</c:choose>
					    	
							<c:choose>
					    		<c:when test="${sessionScope.member.userRole==0}">
					    			<button type="button" class="btn btn-light" onclick="deleteBoard();">삭제</button>
					    		</c:when>
					    		
					    	</c:choose>
						</td>
						<td class="text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/gallery/list?page=${page}';">리스트</button>
						</td>
					</tr>
				</table>
				
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