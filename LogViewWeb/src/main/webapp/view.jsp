<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN" "http://www.w3.org/TR/REC-html40/strict.dtd">
<!--  $Id: layout.jsp,v 1.34 2006/07/23 17:18:21 mgriffa Exp $ -->
<%@ page contentType = "text/html;charset=utf-8" %>
<HTML>
<HEAD>
<title></title>
<link rel="stylesheet" href="style.css" type="text/css">
</HEAD>
<body>
<!-- 
http://www.howtocreate.co.uk/tutorials/javascript/timers
 -->

<script type="text/javascript">

function stop() {
	window.clearInterval(updateInterval);
	//alert('stopped');
}
<% // TODO fix possible injection here %>
var logid = "logid=<%=request.getParameter("id")%>";

function update() {
	// Get the snippet of the logfile
	if (XMLHttpRequest){
            var $class = new XMLHttpRequest();
    }else{
            var $class = new ActiveXObject("MSXML2.XMLHTTP.3.0");
    }
	
    $class.open("POST", logid +".view", true);
    $class.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    $class.setRequestHeader("Content-length", logid.length);
    $class.setRequestHeader("Connection", "close");
    $class.onreadystatechange = function(){
		if ($class.readyState == 4 && $class.status == 200) {
	    	var d = document.getElementById("log");
	    	var s = document.getElementById("status");
	       	if ($class.responseText){
				d.innerHTML = $class.responseText;
				s.innerHTML = "last updated :"+new Date();
	        }
        } 
	}
    $class.send(logid);
}

function start() {
	update();
	//updateInterval = window.setInterval(update, 2000);
	//alert("started");
}

</script>

<table id="rounded-corner">
<tr>
<td style="width: 40px"><a href="javascript:start()">update</a></td>
<td><a style="text-decoration: none;" id="status"></a></td>
</tr>
<tr>
<td colspan=3>
<div id="log" style="width: 100%; bottom: 0px; background: lightgray;">

loding...
</script>
</div>
</td>
</tr>
</table>

<script type="text/javascript">
update();
start();
</script>
</body>
</html>
