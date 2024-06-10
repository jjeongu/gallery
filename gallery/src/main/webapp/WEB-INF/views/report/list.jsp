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
.page-navigation ul {
    display: flex;
    flex-direction: row;
    padding: 0;
}
.page-navigation li {
    list-style-type: none;
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
				<h3><i class="bi bi-exclamation-triangle"></i> 신고 및 건의사항 </h3>
				<hr class="border border-danger border-2 opacity-75">
			<div class="body-main">
				<c:if test="${sessionScope.member.userRole!=0}">
					<div class="alert alert-danger" role="alert">
						패트와 매트 작가에 대해 악플, 비방 등을 목격하셨거나 건의사항이 있는 회원님들은 아래를 통해 작성해주시기 바랍니다.<br>
						작성하신 회원님의 신원은 익명으로 보호하며 조사 결과 허위사실일 경우 처벌받을 수 있습니다.
					</div>
				</c:if>
				<c:if test="${sessionScope.member.userRole==0}">
					<div class="alert alert-primary" role="alert">
						관리자님은 접수된 신고, 건의사항 등을 확인하고 처리해주세요<br>
						접수 완료 시 해당 글을 작성하신 회원님께 이메일이 발송됩니다.
					</div>
				</c:if>
				

				<form name="listForm" method="post">		
					<input type="hidden" name="page" value="${page}">
					<input type="hidden" name="schType" value="${schType}">
					<input type="hidden" name="kwd" value="${kwd}">
					<table class="table table-hover board-list">
						<thead class="table-warning">
							<tr>
								<th class="num">번호</th>
								<th class="subject">제목</th>
								<th class="name">작성자</th>
								<th class="date">작성일</th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach var="dto" items="${list}" varStatus="status">
								<tr>
									<td>${dataCount-(page-1)*size-status.index}</td>
									<td class="left">
										<span class="d-inline-block text-truncate align-middle" style="max-width: 390px;"><a href="${articleUrl}&num=${dto.num}" class="text-reset">${dto.subject}</a></span>
									</td>
									<td>${dto.userName}</td>
									<td>${dto.reg_date.substring(0,4)}-${dto.reg_date.substring(4,6)}-${dto.reg_date.substring(6)}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</form>
				
				<div class="page-navigation">
					${dataCount==0?"등록된 게시글이 없습니다":paging}
				</div>

				<div class="row board-list-footer">
					<div class="col">
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/report/list';"><i class="bi bi-arrow-clockwise"></i></button>
					</div>
					<div class="col-6 text-center">
						<c:if test="${sessionScope.member.userRole==0}">
							<form class="row" name="searchForm" action="${pageContext.request.contextPath}/report/list" method="post">
								<div class="col-4 p-1">
									<select name="schType" class="form-select">
										<option value="all" ${schType=="all"?"selected":""}>제목+내용</option>
										<option value="name" ${schType=="userName"?"selected":""}>작성자</option>
										<option value="reg_date" ${schType=="reg_date"?"selected":""}>등록일</option>
										<option value="subject" ${schType=="subject"?"selected":""}>제목</option>
										<option value="content" ${schType=="content"?"selected":""}>내용</option>
									</select>
								</div>
								<div class="col-6 p-1">
									<input type="text" name="kwd" value="${kwd}" class="form-control">
								</div>
								<div class="col-2 p-1">
									<button type="submit" class="btn btn-light"> <i class="bi bi-search"></i> </button>
								</div>
							</form>
						</c:if>
					</div>
					<div class="col text-end">
						<c:if test="${sessionScope.member.userRole==2}">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/report/write';">신고하기</button>
						</c:if>
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