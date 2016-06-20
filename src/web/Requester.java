package web;

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
	 private ObjectInputStream is;
	 private ObjectOutputStream os;
//	private BufferedReader is;
//	private PrintWriter os;

	public Requester() {
		fileManager = new FileManager();
	}

	private void makeConnection() throws UnknownHostException, IOException {
		connection = new Socket(DEFAULT_HOSTNAME, DEFAULT_PORT);
		 os = new ObjectOutputStream(connection.getOutputStream());
		 is = new ObjectInputStream(connection.getInputStream());
//		os = new PrintWriter(connection.getOutputStream(), true);
//		is = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}

	private void sendRequest(Document document) throws IOException {
		os.writeObject(document);
//		fileManager.logDoc(document);
//		os.write(fileManager.docToString(document));
//		os.println(fileManager.docToString(document));
	}

	private Document getResponse() throws ClassNotFoundException, IOException {
		 return (Document) is.readObject();
//		return fileManager.loadString(is.readLine());
	}

	public Document menu(String language, String type, String weekday) throws IOException, ClassNotFoundException {
		makeConnection();
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
