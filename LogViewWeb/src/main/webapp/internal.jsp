<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN" "http://www.w3.org/TR/REC-html40/strict.dtd">
<!--  $Id: layout.jsp,v 1.34 2006/07/23 17:18:21 mgriffa Exp $ -->
<%@ page contentType = "text/html;charset=utf-8" %>
<HTML>
<HEAD>
<meta http-equiv="refresh" content="2">
<title></title>
<link rel="stylesheet" href="style.css" type="text/css">
</HEAD>
<body>
<%
String b = logview.Buffer.getInstance().get(); 
%>
<table id="rounded-corner">
<thead>
<tr>
<th scope="col" class="rounded-company">Log in TCP buffer</th>
</tr>
</thead>
<tbody>
<tr>
<td>
<pre>
<tt><%= b %></tt>
</pre>
</td>
</tr>
</tbody>
<tfoot>
<tr>
<td><%=b.length() %> characters in buffer</td>
</tr>
</tfoot>
</table>
</body>
</html>