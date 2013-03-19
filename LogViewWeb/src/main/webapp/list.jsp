<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN" "http://www.w3.org/TR/REC-html40/strict.dtd">
<!--  $Id: layout.jsp,v 1.34 2006/07/23 17:18:21 mgriffa Exp $ -->
<%@page contentType="text/html;charset=utf-8" %>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Date"%>
<%@page import="java.net.URLEncoder"%>
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
final Map logs = (Map)request.getAttribute("Map");

%>
<table id="rounded-corner" >
<thead>
<tr>
	<th scope="col" class="rounded-company">File Name</th>
	<th scope="col" class="rounded-q1">Remove</th>
	<th scope="col" class="rounded-q2">View all</th>
	<th scope="col" class="rounded-q3">Size</th>
	<th scope="col" class="rounded-q4">Last modified</th>
</tr>
</thead>
<tbody>
<% if (logs.isEmpty()) { %>
<tr>
<td colspan="4">
NOTE: There is no logfile configured, you may want <a href="add">add a logfile</a>.
<td>
</tr>
<%
}
%>

<%
for(Object k : logs.keySet()) {
//	final File f = new File((String)k);
	final File f = new File((String)logs.get(k));
%>

<tr>
	<td><a href="live.jsp?id=<%=k %>"><%=logs.get(k) %></a></td>
	<td><a style="font-size: x-small;" href="remove?id=<%=k %>">remove</a></td>
	<td><a style="font-size: x-small;" href="view.jsp?id=<%=k %>">viewall</a></td>
	<td><%=dformat.format(f.length()/1024.0)%>Kb</td>
	<td><%=new Date(f.lastModified()) %></td>
</tr>

<% } %>
</tbody>
</table>

</body>
</html>