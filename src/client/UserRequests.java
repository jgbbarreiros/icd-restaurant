package client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import restaurant.FileManager;

public class UserRequests {
	
	private static FileManager fm = new FileManager();
	
	public static Document menu(String language, String type, String weekday) {
		Document requests = fm.blank();
		Element rootElement = requests.createElement("requests");
		requests.appendChild(rootElement);
		Element menu = requests.createElement("menu");
		menu.setAttribute("language", language);
		menu.setAttribute("type", type);
		menu.setAttribute("weekday", weekday);
		rootElement.appendChild(menu);
		return requests;
	}
	
	public static Document order(String language, String[] itref) {
		Document requests = fm.blank();
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
		return requests;
	}

}
