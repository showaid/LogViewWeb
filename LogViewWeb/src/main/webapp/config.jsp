<%@ page contentType = "text/html;charset=utf-8" %>
<%
final String content = (String)request.getAttribute("content");
final Object message = request.getAttribute("message");

%>
<% if (message!=null) { %>
<div id="message">
<%=message %>
</div>
<% }  %>

<form name="edit" action="edit" method="post">
<textarea rows="15" cols="90" name="content">
<%=content %>
</textarea>
<br/>
<input type="submit" name="update"/>
</form>

<br/>
<!-- 
<a href="list" target="configured">View configured logfiles</a>
 -->
 
<iframe src="list" name="configured" width="100%" height="30%" frameborder="0"/>
