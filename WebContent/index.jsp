<%@ page import="web.Requester"%>
<%@ page import="web.FileManager"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
		FileManager fm = new FileManager();
		Requester requester = new Requester();
		
		String menu = "";
		String language = request.getParameter("language");
		String type = request.getParameter("type");
		String weekday = request.getParameter("weekday");
		if (language != null) {
			menu = fm.docToString(requester.menu(language, type, weekday));
		}
		
		String order = "";
		String language2 = request.getParameter("language2");
		String itrefs = request.getParameter("itrefs");
		if (language2 != null) {
			String[] itref = { "1", "8" };
			order = fm.docToString(requester.order(language2, itref));
		}
	%>

	<form>
		<h2>Request a Menu</h2>
		<input name="language" type="radio" value="en"> English<br>
		<input name="language" type="radio" value="pt"> Português<br>
		<input name="language" type="radio" value="fr"> Français<br>
		<input name="type" type="text"> Type<br>
		<input name="weekday" type="text"> Weekday<br>
		<input name="menu" type="submit" value="Request Menu"><br>
		<br>
		<textarea name="showmenu" rows="4" cols="50"><%=menu%></textarea>
	</form>
	
	<br><br>
	
	<form>
		<h2>Order</h2>
		<input name="language2" type="radio" value="en"> English<br>
		<input name="language2" type="radio" value="pt"> Português<br>
		<input name="language2" type="radio" value="fr"> Français<br>
		<input name="itrefs" type="text"> Items<br>
		<input name="order" type="submit" value="Order"><br>
		<textarea name="showmenu" rows="4" cols="50"><%=order%></textarea>
	</form>

</body>
</html>