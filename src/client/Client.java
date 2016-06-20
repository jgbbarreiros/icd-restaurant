package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.w3c.dom.Document;

import web.FileManager;

public abstract class Client {

	public final static String DEFAULT_HOSTNAME = "localhost";
	public final static int DEFAULT_PORT = 5025;
	protected FileManager fm;
	private Socket connection;
	private ObjectInputStream is;
	private ObjectOutputStream os;
	// private BufferedReader is;
	// private PrintWriter os;
	protected boolean connected;

	protected Client() {
		fm = new FileManager();
	}

	protected void openStreams() {
		try {
			connection = new Socket(DEFAULT_HOSTNAME, DEFAULT_PORT);
			os = new ObjectOutputStream(connection.getOutputStream());
			is = new ObjectInputStream(connection.getInputStream());
			connected = true;
			// is = new BufferedReader(new
			// InputStreamReader(connection.getInputStream()));
			// os = new PrintWriter(connection.getOutputStream(), true);
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

	protected void sendRequest(Document document) throws IOException {
		os.writeObject(document);
//		fileManager.logDoc(document);
//		os.write(fileManager.docToString(document));
//		os.println(fileManager.docToString(document));
	}

	protected Document getResponse() throws ClassNotFoundException, IOException {
		 return (Document) is.readObject();
//		return fileManager.loadString(is.readLine());
	}
	
	protected void connect() {
		openStreams();
		request();
		closeStreams();
	}
	
	protected abstract void request();

}
