<%@ page contentType = "text/html;charset=utf-8" %>
<%
final Object message = request.getAttribute("message");

%>

<th>
<% if (message!=null) { %>
<div id="message">
<%=message %>
</div>
<% }  %>


<form id="rounded-corner" name="add" action="add" method="post">
Filename (local to server): <input type="text" name="file"/>
<input type="submit" name="add" value="Add"/> 

</form>
<br/> 
<iframe src="list" name="configured" width="100%" height="30%" frameborder="0"/>
