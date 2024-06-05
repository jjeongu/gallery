<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FanArt</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}

.write-form .img-viewer {
	cursor: pointer;
	border: 1px solid #ccc;
	width: 45px;
	height: 45px;
	border-radius: 45px;
	background-image: url("${pageContext.request.contextPath}/resources/images/add_photo.png");
	position: relative;
	z-index: 9999;
	background-repeat : no-repeat;
	background-size : cover;
}
</style>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board2.css" type="text/css">

<script type="text/javascript">
function sendOk() {
    const f = document.photoForm;
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

    let mode = "${mode}";
    if( (mode === "write") && (!f.selectFile.value) ) {
        alert("이미지 파일을 추가 하세요. ");
        f.selectFile.focus();
        return;
    }
    
    f.action = "${pageContext.request.contextPath}/fanArt_board/${mode}";
    f.submit();
}

$(function() {
	let img = "${dto.img}";
	if( img ) {
		img = "${pageContext.request.contextPath}/uploads/fanArt/" + img;
		$(".write-form .img-viewer").empty();
		$(".write-form .img-viewer").css("background-image", "url("+img+")");
	}
	
	$(".write-form .img-viewer").click(function(){
		$("form[name=photoForm] input[name=selectFile]").trigger("click");
	});
	
	$("form[name=photoForm] input[name=selectFile]").change(function(){
		let file=this.files[0];
		if(! file) {
			$(".write-form .img-viewer").empty();
			if( img ) { 
				img = "${pageContext.request.contextPath}/uploads/fanArt/" + img;
				$(".write-form .img-viewer").css("background-image", "url("+img+")");
			} else {
				img = "${pageContext.request.contextPath}/resources/images/add_photo.png";
				$(".write-form .img-viewer").css("background-image", "url("+img+")");
			}
			return false;
		}
		
		if(! file.type.match("image.*")) {
			this.focus();
			return false;
		}
		
		let reader = new FileReader();
		reader.onload = function(e) {
			$(".write-form .img-viewer").empty();
			$(".write-form .img-viewer").css("background-image", "url("+e.target.result+")");
		}
		reader.readAsDataURL(file);
	});
});
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main>
	<div class="container">
		<div class="body-container">	
			<div class="body-title">
				<h3><i class="bi bi-image"></i> 팬아트 </h3>
			</div>
			
			<div class="body-main">
				<form name="photoForm" method="post" enctype="multipart/form-data">
					<table class="table write-form mt-5">
						<tr>
							<td class="bg-light col" scope="row">제 목</td>
							<td>
								<select name="artist" class="form-select d-inline" style="max-width: 25% !important;">
								<c:if test="${sessionScope.member.userRole==0}">
									<option value="">공지</option>
								</c:if>
								<c:forEach var="art" items="${list}">
									<option value="${art.userId}" ${art.userId eq dto.artist ? "selected":""}>${art.name}</option>
								</c:forEach>
								</select>
								<input type="text" name="subject" class="form-control d-inline" style="max-width: 70% !important;" value="${dto.subject}">
							</td>
						</tr>
	        			
	        			<c:if test="${sessionScope.member.userRole==0}">
	        			<tr>
							<td class="bg-light col-sm-2" scope="row">공지여부</td>
							<td>
								<input type="checkbox" class="form-check-input" name="notice" id="notice" value="1" ${dto.notice==1 ? "checked ":"" } >
								<label class="form-check-label" for="notice"> 공지</label>
							</td>
						</tr>
	        			</c:if>
						<tr>
							<td class="bg-light col-sm-2" scope="row">작성자명</td>
	 						<td>
								<p class="form-control-plaintext">${sessionScope.member.userName}</p>
							</td>
						</tr>
	
						<tr>
							<td class="bg-light col-sm-2" scope="row">내 용</td>
							<td>
								<textarea name="content" id="content" class="form-control">${dto.content}</textarea>
							</td>
						</tr>
						
						<tr>
							<td class="bg-light col-sm-2" scope="row">이미지</td>
							<td>
								<div class="img-viewer"></div>
								<input type="file" name="selectFile" accept="image/*" class="form-control" style="display: none;">
							</td>
						</tr>
						
					</table>
					
					<table class="table table-borderless">
	 					<tr>
							<td class="text-center">
								<button type="button" class="btn btn-dark" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}&nbsp;<i class="bi bi-check2"></i></button>
								<button type="reset" class="btn btn-light">다시입력</button>
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/fanArt_board/list';">${mode=='update'?'수정취소':'등록취소'}&nbsp;<i class="bi bi-x"></i></button>
								
								<c:if test="${mode=='update'}">
									<input type="hidden" name="num" value="${dto.num}">
									<input type="hidden" name="img" value="${dto.img}">
									<input type="hidden" name="page" value="${page}">
								</c:if>
								
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