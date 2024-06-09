<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FanArt</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp" />

<style type="text/css">
.body-container {
	max-width: 800px;
}
.card-img-top{
	max-width: 250px;
	max-height: 150px;
}
</style>

<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}
</script>

</head>
<body>

	<header>
		<jsp:include page="/WEB-INF/views/layout/header.jsp" />
	</header>

	<main>
		<div class="container">
			<div class="body-container">
				<div>
					<h3>
						<i class="bi bi-app"></i> <a href="${pageContext.request.contextPath}/fanArt_board/list">팬아트</a>
						<span class="fs-6">${dataCount}개(${page}/${total_page})</span>
					</h3>
					<hr class="border border-danger border-2 opacity-75">
					
				<c:if test="${sessionScope.member.userRole == 1}">
					<button class="btn btn-secondary float-end py-1" disabled> 글올리기</button>
				</c:if>
				<c:if test="${sessionScope.member.userRole != 1 }">
					<button class="btn btn-secondary float-end py-1" onclick="location.href='${pageContext.request.contextPath}/fanArt_board/write'"> 글올리기</button>
				</c:if>
				</div>

				<c:forEach var="dto" items="${noticeList}">
				<table class="table table-hover board-list">
					<thead class="table-warning">
						<tr>
							<th>&nbsp;</th>
							<th class="subject">공지</th>
							<th class="name">작성자</th>
							<th class="date">작성일</th>
							<th class="hit">조회수</th>
						</tr>
					</thead>
					<tr onclick="location.href='${articleUrl}&num=${dto.num}'">
						<td>&nbsp;</td>
						<td>${dto.subject}</td>
						<td>${dto.name}</td>
						<td>${dto.reg_date}</td>
						<td>${dto.hitcount}</td>
					</tr>
				</table>
				</c:forEach>

				<div class="row row-cols-1 row-cols-md-3 g-4">
					<c:forEach var="dto" items="${list}">
					<div class="col">
						<div class="card h-100" onclick="location.href='${articleUrl}&num=${dto.num}'">
							<img src="${pageContext.request.contextPath}/uploads/fanArt/${dto.img}" class="card-img-top">
							<div class="card-body"><c:if test="${not empty dto.artName}">[${dto.artName}에게]</c:if> ${dto.name} 작성.
							<h5 class="card-title">${dto.subject}</h5>
							<!-- 
							<p>${dto.hitcount}</p>
							 -->
							</div>
							<div class="card-footer">
								<small class="text-body-secondary">${dto.reg_date}</small>
								<small class="text-body-secondary text-end">/ 조회수 : ${dto.hitcount}</small>
							</div>
						</div>
					</div>
					</c:forEach>
				</div>
				<div class="page-navigation">
					${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
				</div>
				<div class="row board-list-footer ">
				<div class="col">
					<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/fanArt_board/list';"><i class="bi bi-arrow-clockwise"></i></button>
				</div>
				<div class="col-auto text-center position-absolute start-50 translate-middle-x">
					<form class="row" name="searchForm" action="${pageContext.request.contextPath}/fanArt_board/list" method="post">
						<div class="col-auto p-1">
							<select name="schType" class="form-select">
								<option value="artist" ${schType=="artist"?"selected":""}>아티스트</option>
								<option value="name" ${schType=="name"?"selected":""}>작성자</option>
								<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
								<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
							</select>
						</div>
						<div class="col-auto p-1">
							<input type="text" name="kwd" value="${kwd}" class="form-control">
						</div>
						<div class="col-auto p-1">
							<button type="button" class="btn btn-light" onclick="searchList()"> <i class="bi bi-search"></i> </button>
						</div>
					</form>
				</div>
			</div>
			</div>
		</div>
	</main>

	<footer>
		<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
	</footer>

	<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp" />
</body>
</html>