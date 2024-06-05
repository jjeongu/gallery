<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<iframe style="display: none;" src="https://www.youtube.com/embed/r2ko422xW0w?autoplay=1" title="지능이 떨어지는 브금" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

<nav class="navbar navbar-expand-lg bg-transparent">
	<div class="container-fluid">
		<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
			<img src="${pageContext.request.contextPath}/resources/images/logo.png" style="width: 200px; height: 70px;">
		</a>
		<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
      		<span class="navbar-toggler-icon"></span>
    	</button>
    	<div class="collapse navbar-collapse" id="navbarNavDropdown">
			<ul class="navbar-nav nav ms-auto">
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
           				<li><a class="dropdown-item" href="${pageContext.request.contextPath}/fanArt_board/list">팬아트 게시판</a></li>
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
					<li><a class="dropdown-item" href="${pageContext.request.contextPath}/faq/list">FAQ</a></li>
					<li><hr class="dropdown-divider"></li>
					<li><a class="dropdown-item" href="#">Q&amp;A</a></li>
					<li><hr class="dropdown-divider"></li>
					<li><a class="dropdown-item" href="${pageContext.request.contextPath}/report/list">신고</a></li>
				</ul>
				</li>
        		<li class="nav-item">
				<c:if test="${empty sessionScope.member}">
					<a class="nav-link" onclick="dialogLogin();" >Log-in</a>
				</c:if>
				<c:if test="${not empty sessionScope.member}">
					<a class="nav-link" href="${pageContext.request.contextPath}/member/logout">Log-out</a>					
				</c:if>
				</li>
				<li>
				<c:if test="${not empty sessionScope.member && sessionScope.member.userRole != 0}">
					<a href="${pageContext.request.contextPath}/member/update"><i class="bi bi-gear"></i></a>
				</c:if>
				<c:if test="${sessionScope.member.userRole == 0}">
					<a href="${pageContext.request.contextPath}/member/admin"><i class="bi bi-gear"></i></a>
				</c:if>
				</li>
			</ul>
		</div>
	</div>
</nav>

	<script type="text/javascript">
		function dialogLogin() {
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
		    $("form[name=modelLoginForm] input[name=userId]").val(unescape(cValue));
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
	<div class="modal fade" id="loginModal" tabindex="-1" data-bs-keyboard="false" 
			aria-labelledby="loginModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content border=primary border-5" style="border-radius: 50%;">
				<div class="modal-header justify-content-center" style="border-bottom-color: transparent;">
					<img src="${pageContext.request.contextPath}/resources/images/logo.png">
				</div>
				<div class="modal-body">
	                <div class="p-3">
	                    <form name="modelLoginForm" action="" method="post" class="row g-3 justify-content-center">
	                    	<div class="mt-0">
	                    		 <p class="form-control-plaintext">로그인 후 이용하실 수 있습니다</p>
	                    	</div>
	                        <div class="mt-0">
	                            <input type="text" name="userId" class="form-control" placeholder="아이디">
	                        </div>
	                        <div>
	                            <input type="password" name="userPwd" class="form-control" autocomplete="off" placeholder="패스워드">
	                        </div>
	                        <div>
	                            <div class="form-check">
	                                <input class="form-check-input" type="checkbox" id="rememberMeModel" name="rememberMe" value="chk" ${check=='checked' ? 'checked':'checked'}>
	                                <label class="form-check-label" for="rememberMeModel"> 아이디 저장</label>
	                            </div>
	                        </div>
	                        <div class="p-0 row row-cols-2 justify-content-center" style="width: 200px; height: 75px;">
	                        	<div class="col align-self-center w-50 h-100">
		                            <button type="button" class="w-100 h-100 border" onclick="sendModelLogin();" style="border: none; border-radius: 50%; background: #ffc107;">Login</button>
	                        	</div>
	                        	<div class="col align-self-center w-50 h-100">
		                            <button type="button" class="w-100 h-100 border" onclick="location.href='${pageContext.request.contextPath}/member/member'" style="border: none; border-radius: 50%; background: #dc3545;">Sign in</button>
	                        	</div>
	                        </div>
	                    </form>
	                </div>
	        
				</div>
			</div>
		</div>
	</div>	