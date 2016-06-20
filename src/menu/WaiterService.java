package menu;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WaiterService extends Service {

	public WaiterService(Socket connection, ObjectInputStream ois, ObjectOutputStream oos, Document menu,
			Document database) {
		super(connection, ois, oos, menu, database);
	}

	public void run() {
		String requestType = "";
		while (connected) {
			responses = createRequestDocument();
			try {
				Document d = (Document) ois.readObject();
				requestType = getRequestType(d);
				switch (requestType) {
				case "users":
					orders();
					break;
				case "update":
					update(d);
					break;
				case "aniversary":
					aniversary(d);
					break;
				default:
					System.out.println("Waiter service defaulted.");
					break;
				}

			} catch (EOFException e) {
				System.out.println("Waiter disconnected.");
				connected = false;
				closeStreams();
			} catch (Exception e) {
				System.out.println("Exception caught in WaiterService.run.");
				connected = false;
				closeStreams();
			}
		}
	}

	protected String getRequestType(Document request) {
		return request.getDocumentElement().getFirstChild().getNodeName();
	}

	private void orders() {
		System.out.println("\nRETURNING ORDERS");
		try {
			oos.reset();
			oos.writeObject(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(Document d) {
		System.out.println("\nRETURNING UPDATE");

		try {
			String userId = (String) xPath.compile("string(//user/@id)").evaluate(d, XPathConstants.STRING);
			String orderId = (String) xPath.compile("string(//order/@id)").evaluate(d, XPathConstants.STRING);
			String newStatus = (String) xPath.compile("string(//order/@status)").evaluate(d, XPathConstants.STRING);
			String currentStatus = (String) xPath
					.compile("string(//user[@id = \"" + userId + "\"]/order[@id=\"" + orderId + "\"]/@status)")
					.evaluate(database, XPathConstants.STRING);

			System.out.println("Attempting to change user id=\"" + userId + "\" order=\"" + orderId
					+ "\" with current status=\"" + currentStatus + "\" to new status=\"" + newStatus + "\"");

			String expression = "//user[@id = \"" + userId + "\"]/order[@id=\"" + orderId + "\"]";
			Element e = (Element) xPath.compile(expression).evaluate(database, XPathConstants.NODE);
			e.setAttribute("status", newStatus);

			Element u = responses.createElement("user");
			Element o = responses.createElement("order");
			u.setAttribute("id", userId);
			o.setAttribute("id", orderId);
			o.setAttribute("status", newStatus);
			u.appendChild(o);
			rootElement.appendChild(u);
			oos.writeObject(responses);
		} catch (XPathException e) {
			System.out.println("Couldnt get element.");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Error while updating a status in WaiterService.update.");
		}
	}

	public void aniversary(Document d) {
		System.out.println("\nRETURNING ANIVERSARY");
		try {
			String userId = (String) xPath.compile("string(//user/@id)").evaluate(d, XPathConstants.STRING);
			String expression = "string(//user[@id = \"" + userId + "\"]/@birthday)";
			String birthday = (String) xPath.compile(expression).evaluate(database, XPathConstants.STRING);
			Element u = responses.createElement("user");
			u.setAttribute("id", userId);
			u.setAttribute("birthday", birthday);
			rootElement.appendChild(u);
			oos.writeObject(responses);
		} catch (XPathException e) {
			System.out.println("Couldnt get element.");
		} catch (Exception e) {
			System.out.println("Error while getting birthday in WaiterService.aniversary.");
		}
	}
}
