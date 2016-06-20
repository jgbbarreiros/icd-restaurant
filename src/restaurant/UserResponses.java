package restaurant;

import java.util.Calendar;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UserResponses {

	private static FileManager fm = new FileManager();

	public static Document menu(String language, String type, String weekday, Document menu) {

		// create new response document
		Document response = fm.blank();
		Element root = response.createElement("responses");
		response.appendChild(root);
		Element menuElem = response.createElement("menu");
		root.appendChild(menuElem);

		// get weekday and meal type
		if (!(type.equals("lunch") || type.equals("lunch") || weekday.equals("weekday") || weekday.equals("restday"))) {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			type = calendar.get(Calendar.HOUR_OF_DAY) < 19 ? "lunch" : "dinner";
			weekday = calendar.get(Calendar.DAY_OF_WEEK) > 6 || calendar.get(Calendar.DAY_OF_WEEK) == 1 ? "restday"
					: "weekday";
		}
		String expression = "//" + weekday + "/" + type + "/item";

		// get items from menu
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList menuItems = (NodeList) xPath.compile(expression).evaluate(menu, XPathConstants.NODESET);
			for (int i = 0; i < menuItems.getLength(); i++) {
				Element menuItem = (Element) menuItems.item(i);

				// get names in certain language
				expression = "//item[@id='" + menuItem.getAttribute("itref") + "']/name/" + language + "/text()";
				String name = (String) xPath.compile(expression).evaluate(menu, XPathConstants.STRING);
				Element menuItemNew = (Element) menuItem.cloneNode(true);

				// add items to response document
				response.adoptNode(menuItemNew);
				menuItemNew.setAttribute("name", name);
				menuElem.appendChild(menuItemNew);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		fm.logDoc(response);
		return response;
	}

	public static Document order(String language, String[] itref, Document menu, Document database, int userId) {

		// create new response document
		Document response = fm.blank();
		Element root = response.createElement("responses");
		response.appendChild(root);

		XPath xPath = XPathFactory.newInstance().newXPath();
		// check if its a new entry
		// TODO check if has id
		Element userElement;
		int orderId;
		double debt;

		// else create entry and send number as response
		// client should store id in cookies
		orderId = 0;
		debt = 0.0;
		Element usersElement = database.getDocumentElement();
		userElement = database.createElement("user");
		userElement.setAttribute("id", Integer.toString(++userId));
		usersElement.appendChild(userElement);
		Element debtElement = database.createElement("debt");
		debtElement.appendChild(database.createTextNode(Double.toString(debt)));
		userElement.appendChild(debtElement);

		// database order
		Element order = database.createElement("order");
		order.setAttribute("id", Integer.toString(++orderId));
		order.setAttribute("status", "accepted");
		usersElement.appendChild(order);

		
		Document todaysMenu = menu(language, "unkown", "unkown", menu); // cheat get today menu using menu()
		String expression;
		for (int i = 0; i < itref.length; i++) {
			expression = "//item[@itref='" + itref[i] + "']";
			Element item = null;
			try {
				item = (Element) xPath.compile(expression).evaluate(todaysMenu, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			Element itemNew = (Element) item.cloneNode(true);
			database.adoptNode(itemNew);
			order.appendChild(itemNew);
			// add up order debt
			debt += Double.parseDouble(itemNew.getTextContent());
		}
		debtElement.setTextContent(debt + "");
		fm.logDoc(database);
		fm.saveAs(database, "database");
		return response;
	}
	
	public static Document check(Document database) { 
		return null;
	}

}
