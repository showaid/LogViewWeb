<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN" "http://www.w3.org/TR/REC-html40/strict.dtd">
<!--  $Id: layout.jsp,v 1.34 2006/07/23 17:18:21 mgriffa Exp $ -->
<%@page contentType = "text/html;charset=utf-8" %>
<%@page import="java.text.DecimalFormat" %>
<%@page import="java.io.File"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<HTML>
<HEAD>
<title></title>
<link rel="stylesheet" href="style.css" type="text/css">
</HEAD>
<body>
<%@page import="java.util.Map"%>
<%
String pattern = "#.##";
DecimalFormat dformat = new DecimalFormat(pattern);
final List logs = (List)request.getAttribute("List");

%>
<table id="rounded-corner" >
<thead>
<tr>
	<th scope="col" class="rounded-company">File Name</th>
	<th scope="col" class="rounded-q3">Size</th>
	<th scope="col" class="rounded-q4">Last modified</th>
</tr>
</thead>
<tbody>
<%
for(Object o : logs) {
	File f = (File)o;
%>
<tr>
	<td>Add <a href="add?file=<%=f.toString() %>"><%=f %></a></td>
	<td><%=dformat.format(f.length()/1024.0) %>Kb</td>
	<td><%=new Date(f.lastModified()) %></td>
</tr>
<%
}
%>
</table>

<% if (logs==null) { %>
<p>NOTE: Tomcat logs cannot be found, or logs dir is not in
 ${catalina.home}/logs.</p>

<% } /* empty */ %>

</body>
</html>