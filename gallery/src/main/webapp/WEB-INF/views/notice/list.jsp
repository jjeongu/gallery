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
.regdate_small {
		display: none;
	}
@media(max-width:800px) {
	.regdate_large {
		display: none;
	}
	.regdate_small {
		display: block;
	}
}
.body-container {
	max-width: 800px;
}
.body-title {
	text-align: center;
}
.body-container h3 {
	font-family: DNFBitBitv2;
}
.hover:hover {
	cursor: pointer;
	background: #eee;
}
.noticeitem p {
	margin: 0;
}
</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>

<script type="text/javascript">
	$(function() {
		$('#checkAll').click(function(){
			$('input[name=check]').prop('checked', $(this).hasClass("active"));
		});
		
		$('#deletebtn').click(function(){
			let cnt=$('input[name=check]:checked').length;
			if(cnt==0) {
				alert('삭제할 게시물을 선택해주세요');
				return;
			}
			if(confirm('게시물을 삭제하시겠습니까')) {
				const f=document.listForm;
				f.action='${pageContext.request.contextPath}/notice/deleteList';
				f.submit();
			}
		});
		
		$('.noticeitem input[name=check]').click(function(e){
			e.stopPropagation();
		});
	});
</script>
 
<main>
	<div class="container">
		<div class="body-container">
			<h3 class="border-bottom border-danger border-3"><i class="bi bi-info-circle"></i> 공지사항 </h3>
			<div class="body-main">
				<form name="listForm" method="post">
					<input type="hidden" name="page" value="${page}">
					<input type="hidden" name="schType" value="${schType}">
					<input type="hidden" name="kwd" value="${kwd}">
					<div>
						<c:forEach var="dto" items="${list}" varStatus="status">
							<div class="noticeitem row p-3 hover"
							onclick="location.href='${pageContext.request.contextPath}/notice/article?page=${page}&num=${dto.num}&schType=${schType}&kwd=${kwd}';">
								<div class="col-sm-3 text-center regdate_large">
									<p class="fs-2">${dto.reg_date.substring(0,4)}년
									<p class="fs-2">${dto.reg_date.substring(4,6)}월 ${dto.reg_date.substring(6)}일
								</div>
								<div class="col-sm-9" style="overflow:hidden;">
									<p class="fs-4 text-truncate">${dto.subject}
									<p class="fs-5 text-truncate">${dto.content}<br>
									<span class="fs-6">조회수 : ${dto.hitcount}</span>
									<span class="fs-6 regdate_small">작성일 : ${dto.reg_date.substring(0,4)}.${dto.reg_date.substring(4,6)}.${dto.reg_date.substring(6)}</span>
									<c:if test="${sessionScope.member.userRole==0}">
										<input type="checkbox" class="form-check-input" value="${dto.num}" name="check">   
									</c:if>
								</div>
							</div>
						</c:forEach>
					</div>
				</form>
				<div class="page-navigation">
					${dataCount==0?"등록된 게시글이 없습니다":paging}
				</div>
				
				<div class="row board-list-footer">
					<div class="col p-1">
						<c:if test="${sessionScope.member.userRole==0}">
          					<button type="button" class="btn btn-light" id="checkAll" data-bs-toggle="button">전체선택</button>
						</c:if>
					</div>
          			<div class="col p-1">
          				<c:if test="${sessionScope.member.userRole==0}">
							<button type="button" class="btn btn-light" id="deletebtn">삭제하기</button>
						</c:if>
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
								<input type="hidden" name="size" value="${size}">
								<button type="submit" class="btn btn-light"> <i class="bi bi-search"></i> </button>
							</div>
						</form>
					</div>
					<div class="col p-1">
						<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/list';">새로고침</button>
					</div>
					<div class="col p-1 text-end">
						<c:if test="${sessionScope.member.userId=='admin'}">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/write';">글올리기</button>
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