<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN" "http://www.w3.org/TR/REC-html40/strict.dtd">
<!--  $Id: layout.jsp,v 1.34 2006/07/23 17:18:21 mgriffa Exp $ -->
<%@ page contentType = "text/html;charset=utf-8" %>
<HTML>
<HEAD>
<title></title>
<link rel="stylesheet" href="style.css" type="text/css">
</HEAD>
<body>

<%

String p = request.getParameter("page");
// TODO add better logic for initial page
String initial = "list";

// Prevent XSS, accept only limited, expected parameteres for initial iframe
if (
		p!=null && (
			p.equals("add")
			|| p.equals("edit")
			)
		) {
	initial = p;
}


%>

<table id="rounded-corner" style="width: 180px; align: left">
<thead>
<tr>
<th scope="col" class="rounded-company">Welcome to logview</th>
</tr>
</thead>
<tr>
<td><a target="content" href="list">List configured logfiles</a></td>
</tr>
<tr>
<td><a target="content" href="smartaddlog">Smart Add log</a></td>
</tr>
<tr>
<td><a target="content" href="add">Add a file</a></td>
</tr>

<!--
<tr>
<td><a target="content" href="edit">Edit initialization file</a> </td>
</tr>
-->

<tr>
<td><a target="content" href="saveall" onclick="return confirm('Save all configured files?')">Save all</a></td>
</tr>
<tr>
<td><a target="content" href="remove" onclick="return confirm('Remove all configured files?')">Remove all</a></td>
</tr>
<tr>
<td><a target="content" href="internal.jsp">internal log</a> </td>
</tr>
</table>
<p>
<table id="rounded-corner" style="width: 180px; align: left">
<tbody>
<tr>
<td><a target="_blank" href="https://github.com/showaid/improved-logview-web/wiki">Online documentation</a><br/></td>
</tr>
<tr>
<td><a target="_blank" href="https://github.com/showaid/improved-logview-web/issues/new">Report an issue</a><br/></td>
</tr>
</tbody>
</table>
<div id="content" style="position: absolute; top: 2px; left: 200px; right: 2px; bottom: 5px;">
<iframe name="content" src="<%=initial %>" style="height: 100%; width: 100%;" frameborder="0"/>
</div>

</body>
</html>