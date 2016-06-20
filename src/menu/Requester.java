package menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Requester {

	public final static String DEFAULT_HOSTNAME = "localhost";
	public final static int DEFAULT_PORT = 5025;
	private FileManager fileManager;
	private Socket connection;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public Requester() {
		fileManager = new FileManager();
	}

	private void makeConnection() throws UnknownHostException, IOException {
		connection = new Socket(DEFAULT_HOSTNAME, DEFAULT_PORT);
		oos = new ObjectOutputStream(connection.getOutputStream());
		ois = new ObjectInputStream(connection.getInputStream());
	}

	private void sendRequest(Document document) throws IOException {
		// oos.reset();
		oos.writeObject(document);
	}

	private Document getResponse() throws ClassNotFoundException, IOException {
		return (Document) ois.readObject();
	}

	private void login() throws IOException {
		Document login = fileManager.blank();
		Element loginElement = login.createElement("login");
		login.appendChild(loginElement);
		Element clientElement = login.createElement("client");
		clientElement.appendChild(login.createTextNode("user"));
		loginElement.appendChild(clientElement);
		sendRequest(login);
	}

	private void sendData(String input) throws IOException {
		Document d = fileManager.blank();
		Element data = d.createElement("data");
		data.setAttribute("birthday", input);
		d.appendChild(data);
		sendRequest(d);
	}

	public Document requestMenu(String language, String type, String weekday)
			throws IOException, ClassNotFoundException {
		makeConnection();
		login();
		sendData("yes");
		Document requests = fileManager.blank();
		Element rootElement = requests.createElement("requests");
		requests.appendChild(rootElement);
		Element menu = requests.createElement("menu");
		menu.setAttribute("language", language);
		menu.setAttribute("type", type);
		menu.setAttribute("weekday", weekday);
		rootElement.appendChild(menu);
		sendRequest(requests);
		return getResponse();
	}

	public Document order(String language, String[] itref) throws IOException, ClassNotFoundException {
		makeConnection();
		login();
		sendData("yes");

		Document requests = fileManager.blank();
		Element rootElement = requests.createElement("requests");
		requests.appendChild(rootElement);
		Element order = requests.createElement("order");
		order.setAttribute("language", language);
		rootElement.appendChild(order);
		for (int i = 0; i < itref.length; i++) {
			Element item = requests.createElement("item");
			item.setAttribute("itref", itref[i]);
			order.appendChild(item);
		}
		sendRequest(requests);
		return getResponse();
	}

}
