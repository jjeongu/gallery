﻿<%@ page contentType="text/html; charset=UTF-8" %>
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
.body-container h3 {
	font-family: DNFBitBitv2;
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

<script type="text/javascript">
function sendOk() {
    const f = document.noticeForm;
	let str;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/notice/${mode}";
    f.submit();
}
<c:if test="${mode=='update'}">
	function deleteFile(fileNum) {
		if(! confirm('파일을 삭제하시겠습니까')) {
			return;
		}
		
		let q='num=${dto.num}&page=${page}&size=${size}&fileNum='+fileNum;
		location.href='${pageContext.request.contextPath}/notice/deleteFile?'+q;
	}
</c:if>
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<h3><i class="bi bi-info-circle"></i> 공지사항 </h3>
			<hr class="border border-danger border-2 opacity-75">
			<div class="body-main">
				<form name="noticeForm" method="post" enctype="multipart/form-data">
					<table class="table write-form mt-5">
						<tr>
							<td class="bg-warning-subtle col-sm-2" scope="row">제 목</td>
							<td>
								<input type="text" name="subject" class="form-control" value="${dto.subject}">
							</td>
						</tr>	        
						<tr>
							<td class="bg-warning-subtle col-sm-2" scope="row">작성자명</td>
	 						<td>
								<p class="form-control-plaintext">${sessionScope.member.userName}</p>
							</td>
						</tr>
	
						<tr>
							<td class="bg-warning-subtle col-sm-2" scope="row">내 용</td>
							<td>
								<textarea name="content" id="content" class="form-control">${dto.content}</textarea>
							</td>
						</tr>
						<c:if test="${mode=='update'}">
							<input type="hidden" name="page" value="${page}">
							<input type="hidden" name="num" value="${dto.num}">
						</c:if>
						<tr>
							<td class="bg-warning-subtle col-sm-2">첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
							<td> 
								<input type="file" name="selectFile" multiple class="form-control">
							</td>
						</tr>
						<c:if test="${mode=='update'}">
							<c:forEach var="vo" items="${listFile}">
								<tr>
									<td class="bg-warning-subtle col-sm-2">첨부된파일</td>
									<td>
										<p class="form-control-plaintext">
											<a href="javascript:deleteFile(${vo.fileNum})"><i class="bi bi-trash"></i></a>
											${vo.uploadFilename}
										</p>
									</td>
								</tr>
							</c:forEach>
						</c:if>
					</table>
					
					<table class="table table-borderless">
	 					<tr>
							<td class="text-center">
								<button type="button" class="btn btn-dark" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}&nbsp;<i class="bi bi-check2"></i></button>
								<button type="reset" class="btn btn-light">다시입력</button>
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/notice/list';">${mode=='update'?'수정취소':'등록취소'}&nbsp;<i class="bi bi-x"></i></button>

							</td>
						</tr>
					</table>
				</form>
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