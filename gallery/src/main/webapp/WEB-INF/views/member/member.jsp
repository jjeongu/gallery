<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${title}</title>

<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.body-container {
	max-width: 800px;
}
</style>

<script type="text/javascript">
function changeEmail() {
    const f = document.memberForm;
	    
    let str = f.selectEmail.value;
    if(str !== "direct") {
        f.email2.value = str; 
        f.email2.readOnly = true;
        f.email1.focus(); 
    }
    else {
        f.email2.value = "";
        f.email2.readOnly = false;
        f.email1.focus();
    }
}

function memberOk() {
	const f = document.memberForm;
	let str;

	str = f.userId.value;
	if( !/^[a-z][a-z0-9_]{2,9}$/i.test(str) ) { 
		alert("아이디를 다시 입력 하세요. ");
		f.userId.focus();
		return;
	}
	
	let mode = "${mode}";
	if(mode === "member" && f.userIdValid.value === "false"){
		str = "아이디 중복 검사가 실행되지 않았습니다.";
		$("#userId").closest(".userId-box").find(".help-block").html(str);
		f.userId.focus();
		return;
	}
	
	str = f.userPwd.value;
	if( !/^(?=.*[a-z])(?=.*[!@#$%^*+=-]|.*[0-9]).{2,10}$/i.test(str) ) { 
		alert("패스워드를 다시 입력 하세요. ");
		f.userPwd.focus();
		return;
	}

	if( str !== f.userPwd2.value ) {
        alert("패스워드가 일치하지 않습니다. ");
        f.userPwd.focus();
        return;
	}

    str = f.birth.value;
    if( !str ) {
        alert("생년월일를 입력하세요. ");
        f.birth.focus();
        return;
    }
    
    str = f.tel1.value;
    if( !str ) {
        alert("전화번호를 입력하세요. ");
        f.tel1.focus();
        return;
    }

    str = f.tel2.value;
    if( !/^\d{3,4}$/.test(str) ) {
        alert("숫자만 가능합니다. ");
        f.tel2.focus();
        return;
    }

    str = f.tel3.value;
    if( !/^\d{4}$/.test(str) ) {
    	alert("숫자만 가능합니다. ");
        f.tel3.focus();
        return;
    }
    
    str = f.email1.value.trim();
    if( !str ) {
        alert("이메일을 입력하세요. ");
        f.email1.focus();
        return;
    }

    str = f.email2.value.trim();
    if( !str ) {
        alert("이메일을 입력하세요. ");
        f.email2.focus();
        return;
    }
    
   	f.action = "${pageContext.request.contextPath}${sessionScope.member.userRole == 0 ? '/admin':''}/member/${mode}";
    f.submit();
}
window.addEventListener('load', () => {
	const el = document.querySelector('form input[name=birth]');
	el.addEventListener('keydown', e => e.preventDefault());
	
	$("#userId").on('input', function(e) {
		if($("#userIdValid").val() === "true"){
			let str = "아이디는 3~10자 이내이며, 첫글자는 영문자로 시작해야 합니다.";
			$("#userId").closest(".userId-box").find(".help-block").html(str);
			$("#userIdValid").val("false");
		}
	});

});

function userIdCheck() {
	let userId = $("#userId").val();
	if( !/^[a-z][a-z0-9_]{2,9}$/i.test(userId) ) { 
		let str = "아이디는 3~10자 이내이며, 첫글자는 영문자로 시작해야 합니다.";
		$("#userId").focus();
		$("#userId").closest(".userId-box").find(".help-block").html(str);
		return;
	}
	
	let url = "${pageContext.request.contextPath}/member/userIdCheck";
	let query = "userId="+userId;
	
	$.ajax({
		type:"post",
		url:url,
		data:query,
		dataType:"json",
		success: function(data) {
			let passed = data.passed;
			
			if(passed === "true"){
				let s = "<span style='color:blue; font-weight:700;'>"+userId + " 아이디는 사용가능합니다.</span>";
				$(".userId-box").find(".help-block").html(s);
				$("#userIdValid").val("true");
			} else {
				let s = "<span style='color:red; font-weight:700;'>"+userId + " 아이디는 사용할 수 없습니다.</span>";
				$(".userId-box").find(".help-block").html(s);
				$("#userIdValid").val("false");
			}
		}
	});
}
</script>

<c:if test="${mode=='update'}">
<script type="text/javascript">
function deleteUser() {
	let url='${pageContext.request.contextPath}/member/delete';
	
	$.ajax({
		type:"post",
		url:url,
		data:"",
		dataType:"json",
		success: function(data) {
			let result = data.result;
			
			if(result === "false"){
				alert("탈퇴가 실패했습니다.");
			}
		}
	});
}
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
			<div class="body-title">
				<h3><i class="bi bi-person-square"></i> ${title} </h3>
			</div>
				<div class="alert alert-danger invisible" role="alert" ></div>
			
			<div class="body-main">
				<form name="memberForm" method="post">
					<div class="row pt-2 userId-box">
						<div class="col-sm-2">아이디</div>
						<div class="col-sm-10">
							<div class="row">
							<div class="col-5 pe-1"><input type="text" name="userId" id="userId" class="form-control" value="${dto.userId}" ${mode=="update" ? "readonly ":""} placeholder="아이디"></div>
							<div class="col-3">
							<c:if test="${mode=='member'}">
								<button type="button" class="btn btn-light" onclick="userIdCheck();">아이디중복검사</button>
							</c:if>
							</div>
						</div>
						<c:if test="${mode=='member'}">
							<small class="form-control-plaintext help-block">아이디는 3~10자 이내이며, 첫글자는 영문자로 시작해야 합니다.</small>
						</c:if>
						</div>
					</div>
					<div class="row pt-2">
						<div class="col-sm-2">비밀번호</div>
						<div class="col-sm-7">
							<input type="password" name="userPwd" class="form-control" placeholder="비밀번호" autocomplete="off">
							<small class="form-control-plaintext">패스워드는 3~10자이며 하나 이상의 숫자나 특수문자가 포함되어야 합니다.</small>
						</div> 
					</div>
					<div class="row pt-2">
						<div class="col-sm-2">비밀번호 확인</div>
						<div class="col-sm-7"><input type="password" name="userPwd2" id="userPwd2" class="form-control" autocomplete="off" placeholder="비밀번호 확인"></div>
					</div>
					<div class="row pt-2">
						<div class="col-sm-2">이름</div>
						<div class="col-sm-7"><input type="text" name="userName" class="form-control" value="${dto.name}" placeholder="이름"></div>
					</div>
					<c:if test="${sessionScope.member.userRole == 0 }">
					<div class="row pt-2">
						<div class="col-sm-2">권한</div>
						<div class="col-sm-5">
							<select class="form-select" name="role">
								<option value="2" ${dto.role ==2 ? 'selected':''}>일반회원</option>
								<option value="1" ${dto.role ==1 ? 'selected':''}>아티스트</option>
								<option value="0" ${dto.role ==0 ? 'selected':''}>관리자</option>
							</select>
						</div>
					</div>
					
					</c:if>
					<div class="row pt-2">
						<div class="col-sm-2">생일</div>
						<div class="col-sm-4"><input type="date" name="birth" class="form-control" value="${dto.birth}" ${mode=="update" ? "readonly ":""}></div>
					</div>
					<div class="row pt-2">
						<div class="col-sm-2">전화번호</div>
						<div class="col-2"><input type="tel" name="tel1" class="form-control" value="${dto.tel1}" maxlength="3"></div>
						<div class="col-1" style="width: 2%;"><p class="form-control-plaintext text-center">-</p></div>
						<div class="col col-2"><input type="tel" name="tel2" class="form-control" value="${dto.tel2}" maxlength="4"></div>
						<div class="col-1" style="width: 2%;"><p class="form-control-plaintext text-center">-</p></div>
						<div class="col col-2"><input type="tel" name="tel3" class="form-control" value="${dto.tel3}" maxlength="4"></div>
					</div>
					<div class="row pt-2">
						<div class="col-sm-2">이메일</div>
						<div class="col-2">
							<select name="selectEmail" id="selectEmail" class="form-select" onchange="changeEmail();">
								<option value="">선 택</option>
								<option value="naver.com" ${dto.email2=="naver.com" ? "selected" : ""}>네이버</option>
								<option value="gmail.com" ${dto.email2=="gmail.com" ? "selected" : ""}>지메일</option>
								<option value="hanmail.net" ${dto.email2=="hanmail.net" ? "selected" : ""}>한메일</option>
								<option value="hotmail.com" ${dto.email2=="hotmail.com" ? "selected" : ""}>핫메일</option>
								<option value="direct">직접입력</option>
							</select>
						</div>
						<div class="col input-group">
							<input type="text" name="email1" class="form-control" maxlength="30" value="${dto.email1}" >
						    <span class="input-group-text p-1" style="border: none; background: none;">@</span>
							<input type="text" name="email2" class="form-control" maxlength="30" value="${dto.email2}" readonly>
						</div>
					</div>
					
					<div class="row mt-3">
				        <div class="text-center">
				            <button type="button" name="sendButton" class="btn btn-primary" onclick="memberOk();"> ${title.substring(title.length()-4)} <i class="bi bi-check2"></i></button>
				            <c:if test="${sessionScope.member.userRole == 0 }">
				            <button type="button" class="btn btn-danger" onclick="location.href='${pageContext.request.contextPath}/admin/member';"> ${title.substring(title.length()-2)+="취소"} <i class="bi bi-x"></i></button>
				            </c:if>
				            <c:if test="${sessionScope.member.userRole != 0 }">
				            <button type="button" class="btn btn-danger" onclick="location.href='${pageContext.request.contextPath}/';"> ${title.substring(title.length()-2)+="취소"} <i class="bi bi-x"></i></button>
				            </c:if>
				            <c:if test="${mode=='update'}">
				            <!--
				            <button type="button" class="btn btn-warning" onclick="deleteUser();"> 회원탈퇴 <i class="bi bi-x"></i></button>
				            -->
				            </c:if>
							<input type="hidden" name="userIdValid" id="userIdValid" value="false">
				        </div>
				    </div>
				    
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