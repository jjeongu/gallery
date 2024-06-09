<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>

<jsp:include page="/WEB-INF/views/admin/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}
</style>
<script type="text/javascript">
function updateUser(id) {
	let url = "${pageContext.request.contextPath}/admin/member/update?member_id="+id;
	location.href = url;
}
function deleteUser(id) {
	if(confirm('정말로 삭제하시겠습니까?')){
		let url = "${pageContext.request.contextPath}/admin/member/delete";
		
		$.ajax({
			type:"post",
			url:url,
			data: "member_id="+id,
			dataType:"json",
			success: function(data) {
				let result = data.result;
				if(result === "false"){
					alert("탈퇴가 실패했습니다.");
				} else {
					 location.reload();
				}
			}
		});
	}
}
</script>
</head>
<body>

<jsp:include page="/WEB-INF/views/admin/layout/left.jsp"/>
	
<main class="wrapper">
	<div class="body-container">
	    <div class="body-title">
			<h2><i class="fa-brands fa-perbyte"></i> 회원관리 </h2>
			<button class="btn btn-secondary float-end py-1" onclick="location.href='${pageContext.request.contextPath}/admin/member/member'"> 회원등록</button>
	    </div>
	    
	    <div class="body-main">
	    	<table class="table table-list text-center table-hover">
	    		<thead>
	    			<tr>
	    				<td class="id">아이디</td>
	    				<td class="name">이름</td>
	    				<td class="role">권한</td>
	    				<td class="reg_date">등록일</td>
	    				<td class="manage">관리</td>
	    			</tr>
	    		</thead>
	    		<tbody>
	    			<c:forEach var="dto" items="${list}">
	    			<tr class="${dto.role == 1? 'table-warning':''}">
	    				<td>${dto.userId}</td>
	    				<td>${dto.name}</td>
	    				<td>${dto.role == 1? "아티스트":"일반회원"}</td>
	    				<td>${dto.register_date}</td>
	    				<td>
	    					<button type="button" class="btn" onclick="updateUser('${dto.userId}')">수정</button>
	    					<button type="button" class="btn" onclick="deleteUser('${dto.userId}')">삭제</button>
	    				</td>
	    			</tr>
					</c:forEach>
	    		</tbody>
	    	</table>
		</div>
	</div>
</main>

<jsp:include page="/WEB-INF/views/admin/layout/footer.jsp"/>

<jsp:include page="/WEB-INF/views/admin/layout/staticFooter.jsp"/>
</body>
</html>