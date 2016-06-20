package menu;

import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class User extends Client {

	private Document menu;
	public Calendar calendar;
	private Date date;
	private Scanner keyboard;

	public User() {
		super();
		clientType = "user";
//		keyboard = new Scanner(System.in);
		date = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(date);
//		System.out.println("ola");
		
	}

	@Override
	public void request() {
//		sendData();
		while (connected) {
			requests = createRequestDocument();
			System.out.println("\n============================");
			System.out.println(calendar.getTime());
			menuOptions(new String[] { "Request Menu", "Order", "Check order", "Pay order", "Leave" });
			switch (readInput()) {
			case 1:
				menu();
				break;
			case 2:
				order();
				break;
			case 3:
				check();
				break;
			case 4:
				pay();
				break;
			case 5:
				leave();
				connected = false;
				System.out.println("Bye");
				break;
			default:
				System.out.println("Please choose a valid option.");
				break;
			}
		}
		keyboard.close();
	}

	private int readInput() {
		try {
			return keyboard.nextInt();
		} catch (InputMismatchException e) {
			keyboard.nextLine();
			return 0;
		}
	}

	public static void main(String[] args) {
		User user = new User();
		user.connect();
	}

	private void menu() {

		// choose language
		menuOptions(new String[] { "english", "português", "français", "Cancel" });
		String language = null;
		switch (readInput()) {
		case 1:
			language = "en";
			break;
		case 2:
			language = "pt";
			break;
		case 3:
			language = "fr";
			break;
		case 4:
			return;
		default:
			System.out.println("Please choose a valid option.");
			break;
		}

		// choose date
		menuOptions(new String[] { "Current date", "Other date", "Cancel" });
		switch (readInput()) {
		case 1:
			menu = requestMenu(language, getType(calendar), getWeekday(calendar));
			showMenu(menu);
			break;
		case 2:
			Calendar cal = setDate();
			showMenu(requestMenu(language, getType(cal), getWeekday(cal)));
			break;
		case 3:
			return;
		default:
			System.out.println("Please choose a valid option.");
			break;
		}
	}

	private Calendar setDate() {
		System.out.print("year = ");
		int year = keyboard.nextInt();
		System.out.print("month = ");
		int month = keyboard.nextInt() - 1;
		System.out.print("day = ");
		int day = keyboard.nextInt();
		System.out.print("hour = ");
		int hour = keyboard.nextInt();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, 0);
		return cal;
	}

	public String getType(Calendar cal) {
		return cal.get(Calendar.HOUR_OF_DAY) < 19 ? "lunch" : "dinner";
	}

	public String getWeekday(Calendar cal) {
		return calendar.get(Calendar.DAY_OF_WEEK) > 6 || calendar.get(Calendar.DAY_OF_WEEK) == 1 ? "restday"
				: "weekday";
	}

	public Document requestMenu(String language, String type, String weekday) {
		requests = createRequestDocument();
		Element menu = requests.createElement("menu");
		menu.setAttribute("language", language);
		menu.setAttribute("type", type);
		menu.setAttribute("weekday", weekday);
		rootElement.appendChild(menu);
		sendRequest(requests);
		return getResponse();
	}

	public void showMenu(Document menu) {
		System.out.println(docToString(menu));
	}

	private void order() {
		if (menu == null) {
			System.out.println("You need to request today's menu first");
			return;
		}
		System.out.println("\nInsert item id's separated by commas:");
		System.out.print(">> ");
		keyboard.nextLine();
		String[] orderList = keyboard.nextLine().trim().split(",");
		Element order = requests.createElement("order");
		rootElement.appendChild(order);
		String expression;
		for (int i = 0; i < orderList.length; i++) {
			expression = "//item[@itref='" + orderList[i] + "']";
			Element item = null;
			try {
				item = (Element) xPath.compile(expression).evaluate(menu, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			Element itemNew = (Element) item.cloneNode(true);
			requests.adoptNode(itemNew);
			order.appendChild(itemNew);
		}
		sendRequest(requests);
		showOrder(getResponse());
	}

	private void showOrder(Document order) {
		System.out.println(docToString(order));
	}

	private void check() {
		Element check = requests.createElement("check");
		rootElement.appendChild(check);

		Element status = requests.createElement("status");
		check.appendChild(status);

		Element debt = requests.createElement("debt");
		check.appendChild(debt);

		sendRequest(requests);
		showCheck(getResponse());
	}

	private void showCheck(Document check) {
		System.out.println(docToString(check));
		fileManager.saveAs(check, "check");
	}

	private void pay() {
		Element pay = requests.createElement("pay");
		rootElement.appendChild(pay);

		sendRequest(requests);
		showPay(getResponse());
	}

	private void showPay(Document pay) {
		System.out.println(docToString(pay));
	}

	private void leave() {
		Element leave = requests.createElement("leave");
		rootElement.appendChild(leave);

		sendRequest(requests);
		showLeave(getResponse());
	}

	private void showLeave(Document leave) {
		System.out.println(docToString(leave));
	}

	private void menuOptions(String[] options) {
		System.out.println("\nChoose a command:");
		int number = 1;
		for (String option : options) {
			System.out.println("\t" + number++ + ". " + option);
		}
		System.out.print(">> ");
	}

//	private void sendData() {
//		Document d = fileManager.blank();
//		Element data = d.createElement("data");
//		String input = "";
//		menuOptions(new String[] { "My birthday is today", "My birthday is NOT today" });
//		switch (keyboard.nextInt()) {
//		case 1:
//			input = "yes";
//			break;
//		case 2:
//			input = "no";
//		}
//		data.setAttribute("birthday", input);
//		d.appendChild(data);
//		sendRequest(d);
//	}
	
	public void sendData(String input) {
		Document d = fileManager.blank();
		Element data = d.createElement("data");
		data.setAttribute("birthday", input);
		d.appendChild(data);
		sendRequest(d);
	}
}
