<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>



<nav class="navbar navbar-expand-lg bg-transparent">
	<div class="container-fluid">
		<a class="navbar-brand" href="${pageContext.request.contextPath}/main">
			<img src="${pageContext.request.contextPath}/resources/images/logo.png" style="width: 200px; height: 70px;">
		</a>
		<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
      		<span class="navbar-toggler-icon"></span>
    	</button>
    	<div class="collapse navbar-collapse" id="navbarNavDropdown">
			<ul class="nav ms-auto">
				<li class="nav-item">
					<a class="nav-link" href="${pageContext.request.contextPath}/notice/list">Notice</a>
        		</li>
        		<li class="nav-item">
        			<a class="nav-link" href="${pageContext.request.contextPath}/artist/list">Artist</a>
        		</li>
        		<li class="nav-item">
          			<a class="nav-link" href="${pageContext.request.contextPath}/gallery/list">Gallery</a>
        		</li>
        		<li class="nav-item dropdown">
       				<a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown"
       				 aria-expanded="false" href="${pageContext.request.contextPath}/community/list">Community</a>
					<ul class="dropdown-menu dropdown-menu-warning">
            			<li><a class="dropdown-item" href="${pageContext.request.contextPath}/free_board/list">자유게시판</a></li>
            			<li><hr class="dropdown-divider"></li>
           				<li><a class="dropdown-item" href="#">팬아트 게시판</a></li>
            			<li><hr class="dropdown-divider"></li>
            			<li><a class="dropdown-item" href="#">팬 게시판</a></li>
            			<li><hr class="dropdown-divider"></li>
            			<li><a class="dropdown-item" href="${pageContext.request.contextPath}/art_board/list">작가 게시판</a></li>
					</ul>
        		</li>
        		<li class="nav-item dropdown">
          			<a class="nav-link dropdown-toggle" role="button" data-bs-toggle="dropdown"
       				 aria-expanded="false" href="#">Contact</a>
          <ul class="dropdown-menu dropdown-menu-warning">
            <li><a class="dropdown-item" href="#">연락처</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">FAQ</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Q&A</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">신고</a></li>
          </ul>
        </li>
        		<li class="nav-item">
          			<a class="nav-link" href="${pageContext.request.contextPath}/contact/list">Contact</a>
        		</li>
        		<li class="nav-item">
          			 <c:if test="${empty sessionScope.member}">
          			 <a class="nav-link" onclick="dialogLogin();" >Log-in</a>
        </c:if>
        <c:if test="${not empty sessionScope.member}">
        <a class="nav-link" href="${pageContext.request.contextPath}/member/logout">Log-out</a>
        </c:if>
				</li>
			</ul>
		</div>
	</div>
</nav>

	<!-- Login Modal -->
	<script type="text/javascript">
		function dialogLogin() {
		    $("form[name=modelLoginForm] input[name=userId]").val("");
		    $("form[name=modelLoginForm] input[name=userPwd]").val("");
		    
			$("#loginModal").modal("show");	
			
		    $("form[name=modelLoginForm] input[name=userId]").focus();
		}
	
		function sendModelLogin() {
		    var f = document.modelLoginForm;
			var str;
			
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
	</script>
	<div class="modal fade" id="loginModal" tabindex="-1"
			data-bs-backdrop="static" data-bs-keyboard="false" 
			aria-labelledby="loginModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="loginViewerModalLabel">Login</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body">
	                <div class="p-3">
	                    <form name="modelLoginForm" action="" method="post" class="row g-3">
	                    	<div class="mt-0">
	                    		 <p class="form-control-plaintext">계정으로 로그인 하세요</p>
	                    	</div>
	                        <div class="mt-0">
	                            <input type="text" name="userId" class="form-control" placeholder="아이디">
	                        </div>
	                        <div>
	                            <input type="password" name="userPwd" class="form-control" autocomplete="off" placeholder="패스워드">
	                        </div>
	                        <div>
	                            <div class="form-check">
	                                <input class="form-check-input" type="checkbox" id="rememberMeModel">
	                                <label class="form-check-label" for="rememberMeModel"> 아이디 저장</label>
	                            </div>
	                        </div>
	                        <div>
	                            <button type="button" class="btn btn-primary w-100" onclick="sendModelLogin();">Login</button>
	                        </div>
	                        <div>
	                    		 <p class="form-control-plaintext text-center">
	                    		 	<a href="#" class="text-decoration-none me-2">패스워드를 잊으셨나요 ?</a>
	                    		 </p>
	                    	</div>
	                    </form>
	                    <hr class="mt-3">
	                    <div>
	                        <p class="form-control-plaintext mb-0">
	                        	아직 회원이 아니세요 ?
	                        	<a href="${pageContext.request.contextPath}/member/member" class="text-decoration-none">회원가입</a>
	                        </p>
	                    </div>
	                </div>
	        
				</div>
			</div>
		</div>
	</div>	