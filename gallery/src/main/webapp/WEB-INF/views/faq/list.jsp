<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>FAQ</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">

<style type="text/css">
.body-container {
	max-width: 800px;
}

.text-end {
 margin-top: 15px;
}


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

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<main>
		<div class="body-container">
			<div class="body-title">
				<h3> 🔺 FAQ </h3>
			</div>
			
			<div class="accordion" id="accordionPanelsStayOpenExample">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="accordion-item">
						<h2 class="accordion-header" id="heading-${status.index}">
							<button class="accordion-button collapsed" type="button" data-toggle="collapse" data-target="#collapse-${status.index}" aria-expanded="false" aria-controls="collapse-${status.index}">
								${dto.subject}
							</button>
						</h2>
						<div id="collapse-${status.index}" class="accordion-collapse collapse" aria-labelledby="heading-${status.index}" data-parent="#accordionPanelsStayOpenExample">
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
	
			<div class="col text-end">
				<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/faq/write';">글올리기</button>
			</div>
		</div>
</main>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>