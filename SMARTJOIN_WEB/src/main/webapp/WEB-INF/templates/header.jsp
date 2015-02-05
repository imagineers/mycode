<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<div class="navbar navbar-fixed-top navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="addGoal.html">
            SMARTJOIN
          </a>
          	<span style="top:25%; right:5%; position: absolute;  color: white;"><sec:authentication property="name"/> <a href="<c:url  value="j_spring_security_logout" />" > Logout</a></span>
          <ul class="nav">
          </ul>
        </div>
      </div>
    </div>
</body>
</html>