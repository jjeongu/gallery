<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${mode=="member"?"회원가입":"정보수정"}</title>

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

window.addEventListener('load', () => {
	const el = document.querySelector('form input[name=birth]');
	el.addEventListener('keydown', e => e.preventDefault());
});

function memberOk() {
	const f = document.memberForm;
	let str;

	str = f.userId.value;
	if( !/^[a-z][a-z0-9_]{4,9}$/i.test(str) ) { 
		alert("아이디를 다시 입력 하세요. ");
		f.userId.focus();
		return;
	}
	
	/*
	let mode = "${mode}";
	if(mode === "member" && f.userIdValid.value === "false"){
		str = "아이디 중복 검사가 실행되지 않았습니다.";
		$("#userId").closest(".userId-box").find(".help-block").html(str);
		f.userId.focus();
		return;
	}
	*/
	
	str = f.userPwd.value;
	if( !/^(?=.*[a-z])(?=.*[!@#$%^*+=-]|.*[0-9]).{5,10}$/i.test(str) ) { 
		alert("패스워드를 다시 입력 하세요. ");
		f.userPwd.focus();
		return;
	}

	if( str !== f.userPwd2.value ) {
        alert("패스워드가 일치하지 않습니다. ");
        f.userPwd.focus();
        return;
	}
	
    str = f.userName.value;
    if( !/^[가-힣]{2,5}$/.test(str) ) {
        alert("이름을 다시 입력하세요. ");
        f.userName.focus();
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

   	f.action = "${pageContext.request.contextPath}/member/${mode}";
    f.submit();
}
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
				<h3><i class="bi bi-person-square"></i> ${mode=="member"?"회원가입":"정보수정"} </h3>
			</div>
				<div class="alert alert-danger invisible" role="alert" ></div>
			
			<div class="body-main">
				<form name="memberForm" method="post">
					<div class="row">
						<div class="col col-3">아이디</div>
						<div class="col col-6"><input type="text" name="userId" class="form-control" value="${dto.userId}" ${mode=="update" ? "readonly ":""} placeholder="아이디"></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">비밀번호</div>
						<div class="col-6"><input type="password" name="userPwd" class="form-control" placeholder="비밀번호" autocomplete="off"></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">비밀번호 확인</div>
						<div class="col col-6"><input type="password" name="userPwd2" id="userPwd2" class="form-control" autocomplete="off" placeholder="비밀번호 확인"></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">이름</div>
						<div class="col col-6"><input type="text" name="userName" class="form-control" value="${dto.name}" placeholder="이름"></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">생일</div>
						<div class="col col-6"><input type="date" name="birth" class="form-control" value="${dto.birth}" ${mode=="update" ? "readonly ":""}></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">전화번호</div>
						<div class="col-2"><input type="tel" name="tel1" class="form-control" value="${dto.tel1}" maxlength="3"></div>
						<div class="col-1" style="width: 2%;"><p class="form-control-plaintext text-center">-</p></div>
						<div class="col col-2"><input type="tel" name="tel2" class="form-control" value="${dto.tel2}" maxlength="4"></div>
						<div class="col-1" style="width: 2%;"><p class="form-control-plaintext text-center">-</p></div>
						<div class="col col-2"><input type="tel" name="tel3" class="form-control" value="${dto.tel3}" maxlength="4"></div>
					</div>
					<div class="row pt-2">
						<div class="col col-3">이메일</div>
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
				            <button type="button" name="sendButton" class="btn btn-primary" onclick="memberOk();"> ${mode=="member"?"회원가입":"정보수정"} <i class="bi bi-check2"></i></button>
				            <button type="button" class="btn btn-danger" onclick="location.href='${pageContext.request.contextPath}/';"> ${mode=="member"?"가입취소":"수정취소"} <i class="bi bi-x"></i></button>
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