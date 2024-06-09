<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib  prefix="c" uri="jakarta.tags.core" %>
<%@ taglib  prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

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
    const f = document.artForm;
	let str;
	
	if(! f.member_id.value) {
        alert("등록할 아이디를 선택하세요. ");
        f.member_id.focus();
        return;
	}
	
    str = f.introduce.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.introduce.focus();
        return;
    }

    let mode = "${mode}";
    if( (mode === "write") && (!f.selectFile.value) ) {
        alert("이미지 파일을 추가 하세요. ");
        f.selectFile.focus();
        return;
    }
    
    f.action = "${pageContext.request.contextPath}/artist/${mode}";
    f.submit();
}


$(function() {
	let img = "${dto.img}";
	if( img ) { // 수정인 경우
		img = "${pageContext.request.contextPath}/uploads/artist/" + img;
		$(".write-form .img-viewer").empty();
		$(".write-form .img-viewer").css("background-image", "url("+img+")");
	}
	
	$(".write-form .img-viewer").click(function(){
		$("form[name=artForm] input[name=selectFile]").trigger("click"); 
	});
	
	$("form[name=artForm] input[name=selectFile]").change(function(){
		let file=this.files[0];
		if(! file) {
			$(".write-form .img-viewer").empty();
			if( img ) {
				img = "${pageContext.request.contextPath}/uploads/artist/" + img;
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

$(function(){
	$("form select[name=member_id]").change(function(){
		let member_id = $(this).val();
		
		if(! member_id) {
			$(".art-name").text("");
			$(".art-birth").text("");
			return false;
		}
		
		let name = $("form select[name=member_id] option:selected").attr("data-name");
		let birth = $("form select[name=member_id] option:selected").attr("data-birth");
		
		$(".art-name").text(name);
		$(".art-birth").text(birth);
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
			<div>
				<h3><i class="fa-solid fa-user-plus"></i>  아티스트</h3>
				<hr class="border border-warning border-2 opacity-75">
			</div>
			
			<div class="body-main">
				<form name="artForm" method="post" enctype="multipart/form-data">
					<table class="table write-form">

						<tr>
							<td class="bg-warning-subtle col-sm-1" scope="row">아이디</td>
							<td>
								<c:if test="${mode=='write'}">
									<select name="member_id">
										<option value="">아이디 선택</option>
										<c:forEach var="vo" items="${list}">
											<option value="${vo.member_id}" data-name="${vo.name}" data-birth="${vo.birth}">${vo.member_id}</option>
										</c:forEach>
									</select>									
								</c:if>
								<c:if test="${mode=='update'}">
									<input type="text" name="member_id" class="form-control" value="${dto.member_id}" readonly>
								</c:if>
								
							</td>
						</tr>

						<tr>
							<td class="bg-warning-subtle col-sm-1" scope="row">작가명</td>
							<td><p class="form-control-plaintext art-name">${dto.name}</p></td>
						</tr>
	
						<tr>
							<td class="bg-warning-subtle col-sm-1" scope="row" >생일</td>
								<td><p class="form-control-plaintext art-birth">${dto.birth}</td>
						</tr>
						<tr>
							<td class="bg-warning-subtle col-sm-3" scope="row">소개</td>
							<td>
								<textarea name="introduce" id="introduce" class="form-control">${dto.introduce}</textarea>
							</td>
						</tr>
						<tr>
							<td class="bg-warning-subtle col-sm-3" scope="row">경력</td>
							<td>
								<textarea name="career" id="career" class="form-control">${dto.career}</textarea>
							</td>
						</tr>
						<tr>
							<td class="bg-warning-subtle col-sm-4" scope="row">대표작</td> 
							<td>
								<textarea name="represent" id="represent" class="form-control">${dto.represent}</textarea>
							</td>
						</tr>

						<tr>
							<td class="bg-warning-subtle col-sm-4" scope="row">이미지</td> 
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
								<button type="button" class="btn btn-light" onclick="location.href='${pageContext.request.contextPath}/artist/list';">${mode=='update'?'수정취소':'등록취소'}&nbsp;<i class="bi bi-x"></i></button>
								
								<c:if test="${mode == 'update'}">
									<input type="hidden" name="img" value="${dto.img}">
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