<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class='reply-info'>
	<span class='reply-count'>댓글${replyCount}개</span>
	<span>[목록, ${pageNo}/${total_page} 페이지]</span>
</div>

<table class='table table-borderless reply-list'>
	<c:forEach var="vo" items="${listReply}">
		<tr class="table-warning"  class='list-header'>
			<td width='50%'>
				<span class='bold'>${vo.name}</span>
			</td>
			<td width='50%' align='right'>
				<span>${vo.reg_date}</span>
				<c:choose>
					<c:when test="${sessionScope.member.userId==vo.member_id || sessionScope.member.userId=='admin'}">
						<span class='deleteReply' data-replyNum='${vo.r_num}' data-pageNo='${pageNo}'>삭제</span>
					</c:when>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td colspan='2' valign='top'>${vo.content}</td>
		</tr>

		<tr>
			<td>
				<button type='button' class='btn btn-light btnReplyAnswerLayout' data-replyNum='${vo.r_num}'>답글 <span id="answerCount${vo.r_num}"></span></button>
				<button type='button' class='btn btn-light btnSendReplyLike' data-r_num='${vo.r_num}' data-replyLike='1' title="좋아요"><i class="bi bi-hand-thumbs-up"></i> <span>${dto.likeCount}</span></button>
			</td>
			<td align='right'>
				&nbsp;
			</td>
		</tr>
	
	    <tr class='reply-answer'>
	        <td colspan='2'>
	            <div id='listReplyAnswer${vo.r_num}' class='answer-list'></div>
	            <div class="answer-form">
	                <div class='answer-left'>└</div>
	                <div class='answer-right'><textarea class='form-control'></textarea></div>
	            </div>
	             <div class='answer-footer'>
	                <button type='button' class='btn btn-light btnSendReplyAnswer' data-replyNum='${vo.r_num}'>답변 등록</button>
	            </div>
			</td>
	    </tr>
	</c:forEach>
</table>

<div class="page-navigation">
	${paging}
</div>							
