<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
  
  <head>
    <meta charset="utf-8">
    <title>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Le styles -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style>
      body { padding-top: 60px; /* 60px to make the container go all the way
      to the bottom of the topbar */ }
    </style>
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js">
      </script>
    <![endif]-->
    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="assets/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="assets/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="assets/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png">
    <style>
    </style>
  </head>
  <body>
    <div class="navbar navbar-fixed-top navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="addConfiguration.html">
            SMARTJOIN
          </a>
          	<span style="top:25%; right:5%; position: absolute;  color: white;"><sec:authentication property="name"/> <a href="<c:url  value="j_spring_security_logout" />" > Logout</a></span>
          <ul class="nav">
          </ul>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="hero-unit">
        <sec:authorize ifAnyGranted="ROLE_SUPER">
        <a class="btn btn-primary" href="addConfiguration.html">
          Add User
        </a>
        </sec:authorize>
        <sec:authorize ifAnyGranted="ROLE_ADMIN">
         <a class="btn btn-primary" href="addConfiguration.html">
          Authorize Org.
        </a>
        </sec:authorize>
        <sec:authorize ifAnyGranted="ROLE_SUPER" >
        <a class="btn btn-primary" href="addUser.html">
          Add Privilege
        </a>
        </sec:authorize>
        <sec:authorize ifAnyGranted="ROLE_USER" ifNotGranted="ROLE_ADMIN">
        <a class="btn btn-primary" href="addConfiguration.html">
          Add Configuration
        </a>
        
        </sec:authorize>
      </div>
      <div>
      </div>
    </div>
    
    <script src="jquery-1.8.3.js">
    </script>
    
    <script src="assets/js/bootstrap.js">
    </script>
  </body>
</html>
