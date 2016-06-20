package client;

import java.io.IOException;
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
	private Document requests;
	private Element rootElement;

	public User() {
		super();
		keyboard = new Scanner(System.in);
		date = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(date);
	}

	@Override
	public void request() {
		while (connected) {
			try {
				System.out.println("\n============================");
				System.out.println(calendar.getTime());
				menuOptions(new String[] { "Request Menu", "Order", "Check order", "Pay order", "Leave" });
				switch (readInput()) {
				case 1:
					String language = chooseLanguage();
					sendRequest(UserRequests.menu(language, "unkown", "unkown"));
					getResponse();
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
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				connected = false;
				keyboard.close();
			}
		}
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

	private String chooseLanguage() {
		menuOptions(new String[] { "english", "português", "français", "Cancel" });
		String language = "en";
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
		default:
			System.out.println("Default language selected: 'en'");
		}
		return language;
	}

	private void menu() {

		// choose date
		menuOptions(new String[] { "Current date", "Other date", "Cancel" });
		switch (readInput()) {
		case 1:
			fm.logDoc(UserRequests.menu("en", "unkown", "unkown"));
			break;
		case 2:
			Calendar cal = setDate();
			fm.logDoc(UserRequests.menu("en", getType(cal), getWeekday(cal)));
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

	private void order() {
		if (menu == null) {
			System.out.println("You need to request today's menu first");
			return;
		}
		System.out.println("\nInsert item id's separated by commas:");
		System.out.print(">> ");
		keyboard.nextLine();
		String[] itrefs = keyboard.nextLine().trim().split(",");
		// sendRequest(requests);
		// showOrder(getResponse());
	}

	private void showOrder(Document order) {
		System.out.println(fm.docToString(order));
	}

	private void check() {
		Element check = requests.createElement("check");
		rootElement.appendChild(check);

		Element status = requests.createElement("status");
		check.appendChild(status);

		Element debt = requests.createElement("debt");
		check.appendChild(debt);

//		sendRequest(requests);
//		showCheck(getResponse());
	}

	private void showCheck(Document check) {
		System.out.println(fm.docToString(check));
		fm.saveAs(check, "check");
	}

	private void pay() {
		Element pay = requests.createElement("pay");
		rootElement.appendChild(pay);

//		sendRequest(requests);
//		showPay(getResponse());
	}

	private void showPay(Document pay) {
		System.out.println(fm.docToString(pay));
	}

	private void leave() {
		Element leave = requests.createElement("leave");
		rootElement.appendChild(leave);

//		sendRequest(requests);
//		showLeave(getResponse());
	}

	private void showLeave(Document leave) {
		System.out.println(fm.docToString(leave));
	}

	private void menuOptions(String[] options) {
		System.out.println("\nChoose a command:");
		int number = 1;
		for (String option : options) {
			System.out.println("\t" + number++ + ". " + option);
		}
		System.out.print(">> ");
	}
}
