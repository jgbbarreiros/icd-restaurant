package restaurant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.w3c.dom.Document;

import restaurant.Service;
import restaurant.FileManager;

public class Server {

	public final static int DEFAULT_PORT = 5025;
	private FileManager fm;
	private Document database;
	private Document details;
	private Document menu;
	private ServerSocket serverSocket;
	private Socket socket;

	public Server(String restaurantPath) {
		fm = new FileManager();
		loadFiles(restaurantPath);
		getDetails(details);
	}

	private void getDetails(Document details) {
		// TODO
	}

	private void loadFiles(String path) {
		database = fm.load(path + "/database");
		details = fm.load(path + "/details");
		menu = fm.load(path + "/menu");
	}

	private void launch() throws IOException {
		serverSocket = new ServerSocket(DEFAULT_PORT);
		System.out.println("New Server listening on localhost:" + DEFAULT_PORT + "...");
		while (true) {
			socket = serverSocket.accept();
			Thread th = new Service(socket, database, menu);
			th.start();
		}
	}

	public static void main(String[] args) {
		Server server = new Server("Restaurant/Monapettit");

		// Server server1 = new Server("Restaurant/Monapettit");
		// Server server2 = new Server("Restaurant/Praisetheburger");
		// Server server3 = new Server("Restaurant/Ricksavenue");
		// Server server4 = new Server("Restaurant/Dianasdiner");

		try {
			server.launch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
