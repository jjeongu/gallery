﻿<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Q&amp;A</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

<c:if test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userRole==0}">
	<script type="text/javascript">
		function deleteOk(mode) {
			let s = mode === "question" ? "질문" : "답변";
			
			if(confirm(s + "을 삭제 하시 겠습니까 ? ")) {
				let query = "num=${dto.num}&${query}&mode="+mode;
				let url = "${pageContext.request.contextPath}/qna/delete?" + query;
				location.href = url;
			}
		}
	</script>
</c:if>

<c:if test="${sessionScope.member.userRole==0}">
	<script type="text/javascript">
		$(function(){
			let answer = "${dto.answer}";
			if(! answer) {
				$(".reply").show();
			}
		});
		
		$(function(){
			$(".btnSendAnswer").click(function(){
				const f = document.answerForm;
				if(! f.answer.value.trim()) {
					f.answer.focus();
					return false;
				}
				
				f.action = "${pageContext.request.contextPath}/qna/answer";
				f.submit();
			});
		});
	</script>
</c:if>

<c:if test="${sessionScope.member.userId==dto.answer_id}">
<script type="text/javascript">
	$(function(){
		$(".btnUpdateAnswer").click(function(){
			let mode = $(this).attr("data-mode");
			if(mode === "update") {
				$(".reply").show();
				$(this).text("답변 수정 취소")
				$(this).attr("data-mode", "cancel");
			} else {
				$(".reply").hide();
				$(this).attr("data-mode", "update");
				$(this).text("답변 수정")
			}
		});
	});
</script>
</c:if>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<div >
				<h3><i class="bi bi-whatsapp"></i> Q&amp;A </h3>
				<hr class="border border-danger border-2 opacity-75">
			</div>
			
			<div class="body-main">
				
				<table class="table mb-0">
					<tbody>
						<tr>
							<td colspan="2" align="center" class="px-0 pb-0">
								<div class="row gx-0">
									<div class="col-sm-1 bg-danger-subtle me-1">
										<p class="form-control-plaintext text-black fw-bold">Q.</p>
									</div>
									<div class="col bg-danger-subtle">
										<p class="form-control-plaintext text-black fw-bold">${dto.subject}</p>
									</div>
								</div>
							</td>
						</tr>					
						<tr>
							<td width="50%">
								이름 : ${dto.name}
							</td>
							<td align="right">
								문의일자 : ${dto.reg_date}
							</td>
						</tr>
						
						<tr>
							<td colspan="2" valign="top" height="200">
								${dto.content}
							</td>
						</tr>
					</tbody>
				</table>
					
				<c:if test="${not empty dto.answer}">
					<table class="table mb-0">
						<tbody>
							<tr>
								<td colspan="2" align="center" class="p-0">
									<div class="row gx-0">
										<div class="col-sm-1 bg-warning-subtle me-1">
											<p class="form-control-plaintext text-black fw-bold">A.</p>
										</div>
										<div class="col bg-warning-subtle">
											<p class="form-control-plaintext text-black fw-bold">${dto.subject}</p>
										</div>
									</div>
								</td>
							</tr>
						
							<tr>
								<td width="50%">
									담당자 : ${dto.answer_name}				
								</td>
								<td align="right">
									답변일자 :  ${dto.answer_date}
								</td>
							</tr>
							
							<tr>
								<td colspan="2" valign="top" height="150">
									${dto.answer}
								</td>
							</tr>
						</tbody>
					</table>
				</c:if>
					
				<table class="table mb-2">
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty prevDto}">
										<a href="${pageContext.request.contextPath}/qna/article?num=${prevDto.num}&${query}">${prevDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextDto}">
										<a href="${pageContext.request.contextPath}/qna/article?num=${nextDto.num}&${query}">${nextDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</table>
				
				<table class="table table-borderless">
					<tr>
						<td width="50%">
							<c:if test="${sessionScope.member.userId==dto.member_id}">
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/qna/update?num=${dto.num}&page=${page}';">질문수정</button>
							</c:if>
					    	
				    		<c:if test="${sessionScope.member.userId==dto.member_id || sessionScope.member.userRole==0}">
				    			<button type="button" class="btn btn-light" onclick="deleteOk('question');">질문삭제</button>
				    		</c:if>
				    		
							<c:if test="${not empty dto.answer and sessionScope.member.userId==dto.answer_id}">
								<button type="button" class="btn btn-light btnUpdateAnswer" data-mode="update">답변수정</button>
							</c:if>
							<c:if test="${not empty dto.answer && (sessionScope.member.userId==dto.answer_id)}">
								<button type="button" class="btn btn-light" onclick="deleteOk('answer');">답변삭제</button>
							</c:if>
				    		
						</td>
						<td class="text-end">
							<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/qna/list?${query}';">리스트</button>
						</td>
					</tr>
				</table>
				
				<div class="reply" style="display: none;">
					<form name="answerForm" method="post">
						<div class='form-header'>
							<span class="bold">질문에 대한 답변</span>
						</div>
						
						<table class="table table-borderless reply-form">
							<tr>
								<td>
									<textarea class='form-control' name="answer">${dto.answer}</textarea>
								</td>
							</tr>
							<tr>
							   <td align='right'>
							   		<input type="hidden" name="num" value="${dto.num}">	
							   		<input type="hidden" name="page" value="${page}">					   
							        <button type='button' class='btn btn-light btnSendAnswer'>답변 등록</button>
							    </td>
							 </tr>
						</table>
					</form>
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