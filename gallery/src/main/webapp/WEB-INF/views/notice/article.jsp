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
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
<c:if test="${sessionScope.member.userId=='admin'}">
	<script type="text/javascript">
	function deleteNotice() {
		if(! confirm('게시글을 삭제하시겠습니까')) {
			return;
		}
		let q='${query}&num=${dto.num}';
		let url='${pageContext.request.contextPath}/notice/delete';
		location.href=url+'?'+q;
	}
	</script>
</c:if>
<main>
	<div class="container">
		<div class="body-container">	
			<div class="body-title">
				<h3><i class="bi bi-clipboard"></i> 공지사항 </h3>
			</div>
			
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
							<td align="right">
								${dto.reg_date} | 조회 ${dto.hitcount}
							</td>
						</tr>
						
						<tr>
							<td colspan="2" valign="top" height="200" style="border-bottom:none;">
								${dto.content}
							</td>
						</tr>	
						<tr>
						</tr>
						
						<tr>
							<td colspan="2">
								이전글 : 
								<c:if test="${not empty prevDto}">
									<a href="${pageContext.request.contextPath}/notice/article?schType=${schType}&kwd=${kwd}&page=${page}&num=${prevDto.num}">${prevDto.subject}</a>
								</c:if>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								다음글 : 
								<c:if test="${not empty nextDto}">
									<a href="${pageContext.request.contextPath}/notice/article?schType=${schType}&kwd=${kwd}&page=${page}&num=${nextDto.num}">${nextDto.subject}</a>
								</c:if>
								
							</td>
						</tr>
						
					</tbody>
				</table>
				
				<table class="table table-borderless">
					<tr>
						<td width="50%">
							<c:if test="${sessionScope.member.userId=='admin'}">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/update?num=${dto.num}&page=${page}&size=${size}';">수정</button>
								<button type="button" class="btn btn-light" onclick="deleteNotice();">삭제</button>	
							</c:if>	
						</td>
						<td class="text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/list?${query}';">리스트</button>
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