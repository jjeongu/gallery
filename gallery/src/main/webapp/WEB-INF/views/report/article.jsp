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
.body-container h3 {
	font-family: DNFBitBitv2;
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
<c:if test="${sessionScope.member.userId=='admin'}">
	<script type="text/javascript">
	function deleteReport() {
		if(! confirm('게시글을 삭제하시겠습니까')) {
			return;
		}
		let url='${pageContext.request.contextPath}/report/delete';
		location.href=url+'?page=${page}&num=${dto.num}&schType=${schType}&kwd=${kwd}';
	}
	
	function sendMail() {
		let url='${pageContext.request.contextPath}/report/send';
		location.href=url+'?page=${page}&num=${dto.num}';
	}
	</script>
</c:if>
<main>
	<div class="container">
		<div class="body-container">	
			<h3 class="border-bottom border-danger border-3"><i class="bi bi-exclamation-triangle"></i> 신고 및 건의사항 </h3>
			
			<div class="body-main">
				
				<table class="table">
					<thead>
						<tr>
							<td colspan="2" align="center">
								${dto.subject}
							</td>
						</tr>
					</thead>
					
					<tbody>
						<tr>
							<td align="left">
								작성자 : ${dto.userName}
							</td>
							<td align="right">
								${dto.reg_date.substring(0,4)}년 ${dto.reg_date.substring(4,6)}월 ${dto.reg_date.substring(6)}일
							</td>
						</tr>
						
						<tr>
							<td colspan="2" valign="top" height="200" style="border-bottom:none;">
								${dto.content}
							</td>
						</tr>	
						<tr>
							<td colspan="2">
								<c:if test="${listFile.size()!=0}">
								<div class="dropdown">
  									<button class="btn btn-secondary btn-danger dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
    									첨부 파일 ${listFile.size()}개
  									</button>
  									<ul class="dropdown-menu">
										<c:forEach var="vo" items="${listFile}" varStatus="status">
    										<li>
    											<a href="${pageContext.request.contextPath}/report/download?fileNum=${vo.fileNum}">${vo.uploadFilename}</a>
    										</li>
										</c:forEach>
									</ul>
								</div>
								</c:if>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								다음글 : 
								<c:if test="${not empty nextDto}">
									<a href="${pageContext.request.contextPath}/report/article?schType=${schType}&kwd=${kwd}&page=${page}&num=${nextDto.num}">${nextDto.subject}</a>
								</c:if>
								
							</td>
						</tr>
						<tr>
							<td colspan="2">
								이전글 : 
								<c:if test="${not empty prevDto}">
									<a href="${pageContext.request.contextPath}/report/article?schType=${schType}&kwd=${kwd}&page=${page}&num=${prevDto.num}">${prevDto.subject}</a>
								</c:if>
							</td>
						</tr>
					</tbody>
				</table>
				
				<table class="table table-borderless">
					<tr>
						<td width="50%">
							<c:if test="${sessionScope.member.userRole==0}">
								<button type="button" class="btn btn-light" onclick="deleteReport();">삭제</button>
								<button type="button" class="btn btn-light" onclick="sendMail();">접수 완료</button>		
							</c:if>
							<c:if test="${sessionScope.member.userRole==2}">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/report/update?num=${dto.num}&page=${page}';">수정</button>
							</c:if>
						</td>
						<td class="text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/report/list?page=${page}&num=${dto.num}&schType=${schType}&kwd=${kwd}';">리스트</button>
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