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
</style>

<script type="text/javascript">
function sendLogin() {
    const f = document.loginForm;
	let str;
	
	str = f.userId.value;
    if(!str) {
        f.userId.focus();
        return;
    }

    str = f.userPwd.value;
    if(!str) {
        f.userPwd.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/member/login";
    f.submit();
}
$(function() {
	var cookieData = document.cookie;
	cName = 'remember=';
	var start = cookieData.indexOf(cName);
	var cValue = '';
	if(start != -1){
		start += cName.length;
		var end = cookieData.indexOf(';', start);
		if(end == -1)end = cookieData.length;
		cValue = cookieData.substring(start, end);
	}
	document.loginForm.userId.value = cValue;
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

	        <div class="row justify-content-center">
	            <div class="col">
	                <div class="border mt-5 p-4">
	                    <form name="loginForm" action="" method="post" class="row g-3">
	                        <h3 class="text-center"><img src="${pageContext.request.contextPath}/resources/images/logo.png"></h3>
	                        <div class="col-12">
	                            <label class="mb-1">아이디</label>
	                            <input type="text" name="userId" class="form-control" placeholder="아이디">
	                        </div>
	                        <div class="col-12">
	                            <label class="mb-1">패스워드</label>
	                            <input type="password" name="userPwd" class="form-control"
									autocomplete="off" placeholder="패스워드">
	                        </div>
	                        <div class="col-12">
	                            <div class="form-check">
	                                <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe" value="chk" ${check=='checked' ? 'checked':'checked'}>
	                                <label class="form-check-label" for="rememberMe"> 아이디 저장</label>
	                            </div>
	                        </div>
	                        <div class="col-12">
	                        	 <button type="button" class="btn btn-warning w-100" onclick="sendLogin();">Login</button>
	                        </div>
	                        <div class="col-12">
	                            <button type="button" class="btn btn-danger w-100" onclick="location.href='${pageContext.request.contextPath}/member/member'">Sign in</button>
	                        </div>
	                    </form>
	                </div>
	                <div class="d-grid">
							<p class="form-control-plaintext text-center text-primary">${message}</p>
	                </div>
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