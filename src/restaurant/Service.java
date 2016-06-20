package restaurant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Service extends Thread {

	private static int userId = 0;
	protected FileManager fm;
	protected Socket connection;
	protected Document database;
	protected Document menu;
	protected boolean connected;

	private ObjectInputStream is;
	private ObjectOutputStream os;
//	private BufferedReader is;
//	private PrintWriter os;

	public Service(Socket connection, Document database, Document menu) {
		fm = new FileManager();
		this.connection = connection;
		this.database = database;
		this.menu = menu;
		connected = true;
	}

	protected void openStreams() {
		try {
			 is = new ObjectInputStream(connection.getInputStream());
			 os = new ObjectOutputStream(connection.getOutputStream());
//			is = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//			os = new PrintWriter(connection.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void closeStreams() {
		System.out.println("Service: Client disconnected...");
		try {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
			if (connection != null)
				connection.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private Document getRequest() throws ClassNotFoundException, IOException {
		 return (Document) is.readObject();
//		String a = is.readLine();
//		System.out.println(a);
//		return fm.loadString(a);
	}

	private void sendResponse(Document document) throws IOException {
		 os.writeObject(document);
		// os.println(fm.docToString(document));
//		os.write(fm.docToString(document));
	}

	@Override
	public void run() {
		openStreams();
		while (connected) {

			try {
				// get request
				Document request = getRequest();
				Element requestElement = (Element) request.getDocumentElement().getLastChild();

				// log received request
				System.out.println("\n" + requestElement.getNodeName() + " request");
				fm.logDoc(request);

				// send response
				switch (requestElement.getNodeName()) {
				case "menu":
					String language = requestElement.getAttribute("language");
					String type = requestElement.getAttribute("type");
					String weekday = requestElement.getAttribute("weekday");
					sendResponse(UserResponses.menu(language, type, weekday, menu));
					break;
				case "order":
					language = requestElement.getAttribute("language");
					NodeList items = requestElement.getChildNodes();
					String[] itref = new String[items.getLength()];
					for (int i = 0; i < items.getLength(); i++) {
						Element item = (Element) items.item(i);
						itref[i] = item.getAttribute("itref");
					}
					sendResponse(UserResponses.order(language, itref, menu, database, userId));
					break;
				case "check":
					sendResponse(UserResponses.check(database));
					break;
				default:
					// TODO send error response
					System.out.println("Unkown request");
					break;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				connected = false;
			}

		}
		closeStreams();
	}

}
