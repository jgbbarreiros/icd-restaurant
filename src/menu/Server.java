package menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class Server {

	public final static int DEFAULT_PORT = 5025;
	private static ServerSocket serverSocket;
	private static Document doc;
	private static Document menu;
	private static Document database;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket service;

	public Server(String path) {
		loadFiles(path);
	}

	private void loadFiles(String path) {
		FileManager fm = new FileManager();
		XPath xPath = XPathFactory.newInstance().newXPath();
		doc = fm.blank();
		doc = fm.load(path);
		try {
			String menuPath = (String) xPath.compile("string(//menu/@path)").evaluate(doc, XPathConstants.STRING);
			String databasePath = (String) xPath.compile("string(//database/@path)").evaluate(doc,
					XPathConstants.STRING);
			menu = fm.blank();
			menu = fm.load(menuPath);
			database = fm.blank();
			database = fm.load(databasePath);
		} catch (XPathExpressionException e) {
			System.out.println("Could not find specified files.");
		}
	}

	private void launch() throws IOException {
		serverSocket = new ServerSocket(DEFAULT_PORT);
		System.out.println("Server listening on localhost:" + DEFAULT_PORT + "...");
		while (true) {
			try {
				service = serverSocket.accept();
				openStreams();
				Thread th;
				switch (getClientType((Document) ois.readObject())) {
				case "user":
					th = new UserService(service, ois, oos, menu, database);
					break;
				case "waiter":
					th = new WaiterService(service, ois, oos, menu, database);
					System.out.println("Creating WaiterService.");
					break;
				default:
					System.out.println("Defaulted in switch...");
					continue;
				}
				th.start();
			} catch (IOException e) {
				closeStreams();
				continue;
			} catch (ClassNotFoundException e) {
				System.out.println("Problem in reading object.");
			}
		}
	}

	protected void openStreams() throws IOException {
		ois = new ObjectInputStream(service.getInputStream());
		oos = new ObjectOutputStream(service.getOutputStream());
		oos.flush();
	}

	protected void closeStreams() {
		System.out.println("Server - Client disconnected...");
		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (service != null)
				service.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private String getClientType(Document login) {
		return login.getDocumentElement().getFirstChild().getTextContent();
	}

	public static void main(String[] args) {
		Server server = new Server("monapettit.xml");

		// Server server1 = new Server("monapettit");
		// Server server2 = new Server("praisetheburger");
		// Server server3 = new Server("ricksavenue");
		// Server server4 = new Server("dianasdiner");

		try {
			server.launch();
		} catch (IOException e) {
			System.err.println("Exception in server: " + e);
		}
	}
	
	public String docToString(Document doc) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(doc);
	}
}
