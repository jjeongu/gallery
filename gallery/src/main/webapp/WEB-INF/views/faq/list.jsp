<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>

<c:import url="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
	
}

#ex {
margin-top: 10px;}
</style>

<c:if test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userRole==0}">
<script type="text/javascript">
	function deleteFaq(num) {
		if(confirm("게시글 삭제 갈겨~~~~")){
			let query = "num="+num;
			let url = "${pageContext.request.contextPath}/faq/delete?" + query;
			location.href = url;
		}
	}
	
	function updateFaq(num) {
		
	}
</script>

</c:if>

</head>
<body>

<header>
	<c:import url="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<div class="body-title">
				<h3> 🔺 FAQ </h3>
			</div>
			
			<div class="body-main">
				<div class="accordion" id="accordionFlushExample">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="accordion-item">
						<h2 class="accordion-header" id="heading-${status.index}">
							<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapse${status.index}" aria-expanded="false" aria-controls="flush-collapse${status.index}">
								${dto.subject}
							</button>
						</h2>
						<div id="flush-collapse${status.index}" class="accordion-collapse collapse" data-bs-parent="#accordionFlushExample">
							<div class="accordion-body">
							${dto.content}
								
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userRole==0}">
										<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/faq/update?num=${dto.num}';">수정</button>
									</c:when>
								</c:choose>
								
								<c:choose>
									<c:when test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userRole==0}">
										<button type="button" class="btn btn-light" onclick="deleteFaq(${dto.num});">삭제</button>
									</c:when>
								</c:choose>
							</div>
						</div>
					</div>
				</c:forEach>
				</div>
			</div>
				<div class="col text-end" id="ex">
					<c:if test="${sessionScope.member.userRole == 0}">
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/free_board/write';">글올리기</button>
					</c:if>
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