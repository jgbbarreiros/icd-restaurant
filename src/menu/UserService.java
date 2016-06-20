package menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class UserService extends Service {

	private static int userId = 0;
	private Element requestElement;
	private Element userElement;
	private Element debtElement;
	private double debt;
	private int orderId;

	public UserService(Socket connection, ObjectInputStream ois, ObjectOutputStream oos, Document menu,
			Document database) {
		super(connection, ois, oos, menu, database);
		debt = 0.0;
		orderId = 0;
		createUserEntry();
		System.out.println("User " + userId + " connected");
	}

	private void createUserEntry() {
		Element usersElement = database.getDocumentElement();

		userElement = database.createElement("user");
		userElement.setAttribute("id", Integer.toString(++userId));
		usersElement.appendChild(userElement);

		debtElement = database.createElement("debt");
		debtElement.appendChild(database.createTextNode(Double.toString(debt)));
		userElement.appendChild(debtElement);
	}

	public void run() {
		String requestType = "";
		getData();
		while (connected) {
			try {
				responses = createRequestDocument();
				request = (Document) ois.readObject();
				requestElement = (Element) request.getDocumentElement().getLastChild();
				requestType = getRequestType(request);
				System.out.println("\n" + requestType + " request");
				System.out.println(docToString(request));
				switch (requestType) {
				case "menu":
					menu();
					break;
				case "order":
					order();
					break;
				case "check":
					check();
					break;
				case "pay":
					pay();
					break;
				case "leave":
					leave();
					connected = false;
					break;
				default:
					System.out.println("Unkown request");
					break;
				}

			} catch (ClassNotFoundException e) {
				log("Did not receive XML");
				connected = false;
			} catch (IOException e) {
				log("User disconnected unexpectedly");
				connected = false;
			} catch (XPathExpressionException e) {
				log("Incorrect XML document received");
				connected = false;
			}
		}
		closeStreams();
	}

	protected String getRequestType(Document request) {
		return requestElement.getNodeName();
	}

	private void menu() throws XPathExpressionException, IOException {

		Element menuElem = responses.createElement("menu");
		rootElement.appendChild(menuElem);
		String expression;

		// get menu for certain day and type
		expression = "//" + requestElement.getAttribute("weekday") + "/" + requestElement.getAttribute("type")
				+ "/item";
		NodeList menuItems = (NodeList) xPath.compile(expression).evaluate(menu, XPathConstants.NODESET);

		for (int i = 0; i < menuItems.getLength(); i++) {
			Element menuItem = (Element) menuItems.item(i);

			// get names in certain language
			expression = "//item[@id='" + menuItem.getAttribute("itref") + "']/name/"
					+ requestElement.getAttribute("language") + "/text()";
			String name = (String) xPath.compile(expression).evaluate(menu, XPathConstants.STRING);
			Element menuItemNew = (Element) menuItem.cloneNode(true);
			responses.adoptNode(menuItemNew);
			menuItemNew.setAttribute("name", name);
			menuElem.appendChild(menuItemNew);
		}
		oos.reset();
		oos.writeObject(responses);
	}

	private void order() throws IOException, XPathExpressionException {

		// database update
		Element orderItems = (Element) requestElement.cloneNode(true);
		database.adoptNode(orderItems);
		orderItems.setAttribute("id", Integer.toString(++orderId));
		orderItems.setAttribute("status", "accepted");
		userElement.appendChild(orderItems);

		String expression = "sum(*/*[last()]/item/text())";
		String orderDebt = (String) xPath.compile(expression).evaluate(request, XPathConstants.STRING);
		debt += Double.parseDouble(orderDebt);

		debtElement.setTextContent(debt + "");

		// user response
		Element orderElement = responses.createElement("print");
		orderElement.appendChild(responses.createTextNode("Ordered successfully"));
		rootElement.appendChild(orderElement);

		fileManager.saveAs(database, "database");

		oos.reset();
		oos.writeObject(responses);
	}

	private void check() throws IOException, XPathExpressionException {

		Element checkElement = responses.createElement("check");
		String expression = "//user[@id='" + userId + "']/*";
		NodeList userStatus = (NodeList) xPath.compile(expression).evaluate(database, XPathConstants.NODESET);
		// NodeList userStatus = userElement.getChildNodes();
		for (int i = 0; i < userStatus.getLength(); i++) {
			Element item = (Element) userStatus.item(i);
			Element itemNew = (Element) item.cloneNode(true);
			responses.adoptNode(itemNew);
			checkElement.appendChild(itemNew);
		}
		rootElement.appendChild(checkElement);

		oos.reset();
		oos.writeObject(responses);
	}

	private void pay() throws IOException {

		debt = 0.0;
		debtElement.setTextContent(Double.toString(debt));

		Element leaveElement = responses.createElement("print");
		leaveElement.appendChild(responses.createTextNode("Paid"));
		rootElement.appendChild(leaveElement);

		oos.reset();
		oos.writeObject(responses);
	}

	private void leave() throws IOException {
		connected = false;

		Element leaveElement = responses.createElement("print");
		leaveElement.appendChild(responses.createTextNode("Ok"));
		rootElement.appendChild(leaveElement);

		oos.reset();
		oos.writeObject(responses);
	}

	private void getData() {
			Document d;
			try {
				d = (Document) ois.readObject();
				String s = (String) xPath.compile("string(//data/@birthday)").evaluate(d, XPathConstants.STRING);
				userElement.setAttribute("birthday", s);
			} catch (ClassNotFoundException e) {
				log("Did not receive XML");
				connected = false;
			} catch (IOException e) {
				log("User disconnected unexpectedly");
				connected = false;
			} catch (XPathExpressionException e) {
				log("Incorrect XML document received");
				connected = false;
			}
	}
	
	private void log(String message) {
		System.out.println("User Service " + userId + ": "  + message);
	}
}
