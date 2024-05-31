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
}
.body-title {
	text-align: center;
}
.body-title h3 {
	 font-size: 50px;
	 padding-bottom: 20px;
}
.hover:hover {
	cursor: pointer;
	background: #eee;
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
			<div class="body-title mb-0">
				<h3><i class="bi bi-clipboard"></i> 공지사항 </h3>
			</div>
			<div class="body-main">
				<form name="listForm" method="post">
					<div>
					<c:forEach var="dto" items="${list}" varStatus="status">
						<div class="row p-3 hover border" onclick="location.href='${pageContext.request.contextPath}/notice/article?page=${page}&num=${dto.num}';">
							<input type="checkbox" class="form-check-input">      
							<div class="col">
								<h2>${dto.reg_date}</h2>
							</div>
							<div class="col">
								<p>${dto.subject}
								<p>${dto.content}
							</div>
							<div class="col">
								<p>${dto.hitcount}
							</div>
						</div>
					</c:forEach>
					</div>
				</form>
				
				<div class="page-navigation">
					${dataCount==0?"등록된 게시글이 없습니다":paging}
				</div>

				<div class="row board-list-footer">
					<div class="col">
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/list';"><i class="bi bi-arrow-clockwise"></i></button>
					</div>
					<div class="col-6 text-center">
						<form class="row" name="searchForm" action="${pageContext.request.contextPath}/notice/list" method="post">
							<div class="col-auto p-1">
								<select name="schType" class="form-select">
									<option value="all" ${schType=="all"?"selected":""}>제목+내용</option>
									<option value="userName" ${schType=="userName"?"selected":""}>작성자</option>
									<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
									<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
									<option value="content" ${schType=="content"?"selected":""}>내용</option>
								</select>
							</div>
							<div class="col-auto p-1">
								<input type="text" name="kwd" value="${kwd}" class="form-control">
							</div>
							<div class="col-auto p-1">
								<button type="submit" class="btn btn-light"> <i class="bi bi-search"></i> </button>
							</div>
						</form>
					</div>
					<div class="col text-end">
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/write';">글올리기</button>
					</div>
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