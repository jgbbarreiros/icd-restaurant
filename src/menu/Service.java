package menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public abstract class Service extends Thread {

	protected Socket connection;
	protected boolean connected;
	protected Document responses;
	protected Document menu;
	protected Document database;
	protected Document request;
	protected ObjectInputStream ois;
	protected ObjectOutputStream oos;
	protected FileManager fileManager;
	protected XPath xPath = XPathFactory.newInstance().newXPath();
	protected Element rootElement;

	public Service(Socket connection, ObjectInputStream ois, ObjectOutputStream oos, Document menu, Document database) {
		fileManager = new FileManager();
		this.menu = menu;
		this.database = database;
		this.connection = connection;
		this.ois = ois;
		this.oos = oos;
		connected = true;
		responses = createRequestDocument();
	}

	protected Document createRequestDocument() {
		Document d = fileManager.blank();
		rootElement = d.createElement("responses");
		d.appendChild(rootElement);
		return d;
	}

	protected void closeStreams() {
		System.out.println("Service - Client disconnected...");
		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public abstract void run();

	public String docToString(Document doc) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(doc);
	}

	protected abstract String getRequestType(Document request);

}