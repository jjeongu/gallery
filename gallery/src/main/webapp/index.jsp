<%@ page contentType="text/html; charset=UTF-8" %>
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
.container {
	padding: 5% 0;
}

.container img {
	width: 80%;
	animation-duration: 3s;
  	animation-name: slidein;
}
.bounce-text {
	width:100%;
}

@keyframes slidein {
  from {
    margin-left: 100%;
    width: 80%;
  }

  to {
    margin-left: 0%;
    width: 80%;
  }
}
</style>

<style>
  @keyframes fall {
     0% {transform: translateY(-200px);}
     80% {transform: translateY(-200px);}
     100% {transform: translateY(0px);}
  }

  @keyframes bounce {
       0% {transform: translate(0, 0) scaleX(1.2);}
       10% {transform: translate(10px, -40px) scaleX(1.2);}
       20% { transform: translate(20px, -70px);}
       30% {transform: translate(30px, -90px)}
       40% {transform: translate(40px, -100px);}
       50% {transform: translate(50px, -105px);}
       60% {transform: translate(60px, -100px);}
       70% {transform: translate(70px, -90px);}
       80% {transform: translate(80px, -70px);}
       90% {transform: translate(90px, -40px);}
       100% {transform: translate(100px, 0);}
     }
        
        
        @keyframes bounce2 {
            0% {transform: translate(100px, 0) scaleX(1.2);}
            10% {transform: translate(105px, -20px) scaleX(1.1);}
            20% {transform: translate(110px, -40px);}
            30% {transform: translate(115px, -55px);}
            40% {transform: translate(120px, -68px);}
            50% {transform: translate(125px, -73px);}
            60% {transform: translate(130px, -75px);}
            70% {transform: translate(135px, -50px);}
            80% {transform: translate(140px, -30px);}
            90% {transform: translate(145px, -10px);}
            100% {transform: translate(150px, 0);}
        }
        
        @keyframes bounce3 {
            0% {transform: translate(153px, 0) scaleX(1.2);}
            10% {transform: translate(156px, -12px) scaleX(1.1);}
            20% {transform: translate(159px, -23px);}
            30% {transform: translate(162px, -34px);}
            40% {transform: translate(165px, -42px);}
            50% {transform: translate(168px, -45px);}
            60% {transform: translate(171px, -40px);}
            70% {transform: translate(174px, -35px);}
            80% {transform: translate(177px, -20px);}
            90% {transform: translate(180px, -10px);}
            100% {transform: translate(183px, 0);}
        }

        .bounce-text {
            display: inline-block;
            font-size: 2rem;
            animation: fall 2s ease-out forwards, bounce 1s ease-out 2s forwards, bounce2 1s ease-out 3s forwards, bounce3 1s ease-out 4s forwards;
        }
</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
</header>
	
<main class="container" onclick="location.href='${pageContext.request.contextPath}/main'";>
	<div class="text-center">
		<div class="bounce-text"><img src="${pageContext.request.contextPath}/resources/images/door.png"></div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>