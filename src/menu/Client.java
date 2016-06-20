package menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public abstract class Client {

	public final static String DEFAULT_HOSTNAME = "localhost";
	public final static int DEFAULT_PORT = 5025;
	protected XPath xPath;
	protected FileManager fileManager;
	protected Document requests;
	protected Element rootElement;

	private Socket connection;
	protected ObjectInputStream ois;
	protected ObjectOutputStream oos;
	protected boolean connected;
	protected String clientType;

	public Client() {
		xPath = XPathFactory.newInstance().newXPath();
		fileManager = new FileManager();
		requests = createRequestDocument();
		connected = false;
	}

	protected Document createRequestDocument() {
		Document d = fileManager.blank();
		rootElement = d.createElement("requests");
		d.appendChild(rootElement);
		return d;
	}

	public void connect() {
		try {
			connection = new Socket(DEFAULT_HOSTNAME, DEFAULT_PORT);
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());
			connected = true;
			login();
//			request();
		} catch (ConnectException e) {
			System.out.println("There is no server listenning. \nLeaving...");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		finally {
//			try {
//				if (ois != null)
//					ois.close();
//				if (oos != null)
//					oos.close();
//				if (connection != null)
//					connection.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	private void login() throws IOException {
		Document login = fileManager.blank();
		Element loginElement = login.createElement("login");
		login.appendChild(loginElement);

		Element clientElement = login.createElement("client");
		clientElement.appendChild(login.createTextNode(clientType));
		loginElement.appendChild(clientElement);

		oos.writeObject(login);
		oos.flush();
	}

	protected void sendRequest(Document document) {
		try {
			oos.reset(); // in case it's the same document with alterations
			oos.writeObject(document);
		} catch (IOException e) {
			connected = false;
			e.printStackTrace();
		}
	}

	protected Document getResponse() {
		try {
			return (Document) ois.readObject();
		} catch (ClassNotFoundException e) {
			connected = false;
			e.printStackTrace();
			return null; // TODO return error document message
		} catch (IOException e) {
			connected = false;
			e.printStackTrace();
			return null; // TODO return error document message
		}
	}

	public abstract void request();

	public String docToString(Document doc) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(doc);
	}
}
