package menu;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Waiter extends Client {

	private Scanner keyboard;

	public Waiter() {
		super();
		clientType = "waiter";
		keyboard = new Scanner(System.in);
	}

	public void request() {
		System.out.println("waiter requested");

		while (connected) {
			requests = createRequestDocument();
			System.out.println("========================\n");
			System.out.println("Choose a command:\n");
			System.out.println("\t 1. Check orders");
			System.out.println("\t 2. Modify status.");
			System.out.println("\t 3. Check aniversary.");
			System.out.print("\t 4. Leave \n>> ");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				orders();
				break;
			case 2:
				update();
				break;
			case 3:
				aniversary();
				break;
			case 4:
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

	private void orders() {
		Element users = requests.createElement("users");
		rootElement.appendChild(users);
		try {
			oos.writeObject(requests);
			// Reads the whole clients list and orders.
			Document response = (Document) ois.readObject();
			System.out.println(docToString(response));
			System.out.println((String) xPath.compile("string(//user[@id='0']//@status)").evaluate(response,
					XPathConstants.STRING));
		} catch (Exception e) {
			System.out.println("Exception caught in Waiter.orders.");
			e.printStackTrace();
		}
	}

	public void update() {
		boolean invalid = true;
		System.out.print("Insert user ID: > ");
		String cid = Integer.toString(keyboard.nextInt());

		System.out.print("Insert order ID: > ");
		String oid = Integer.toString(keyboard.nextInt());
		String status = "";
		while (invalid) {
			System.out.println("Choose new status for user \"" + cid + "\" with order id \"" + oid + "\":");
			System.out.println("\t 1. Accepted");
			System.out.println("\t 2. Ready.");
			System.out.println("\t 3. Delivered.");
			System.out.print("\t 4. Complete \n >> ");
			int choice = keyboard.nextInt();
			switch (choice) {
			case 1:
				status = "accepted";
				invalid = false;
				break;
			case 2:
				status = "ready";
				invalid = false;
				break;
			case 3:
				status = "delivered";
				invalid = false;
				break;
			case 4:
				status = "complete";
				invalid = false;
				break;
			default:
				System.out.println("Please choose a valid status.");
			}
		}
		try {
			Element e = requests.createElement("update");
			Element c = requests.createElement("user");
			Element o = requests.createElement("order");
			rootElement.appendChild(e);
			e.appendChild(c);
			c.appendChild(o);
			c.setAttribute("id", cid); // client id attribute
			o.setAttribute("id", oid); // order id attribute
			o.setAttribute("status", status); // order status
			oos.writeObject(requests);
			System.out.println(docToString(requests));
			Document d = (Document) ois.readObject();
			System.out.println(docToString(d));
			String confirmation = (String) xPath.compile("string(//order/@status)").evaluate(d, XPathConstants.STRING);
			if (status.equals(confirmation)) {
				System.out.println("User's order updated succesfully.");
			} else {
				System.out.println("Could not update user's order.");
			}
		} catch (Exception e1) {
			System.out.println("Exception caught in Waiter.update.");
		}
		
	}

	public void aniversary() {
		try {
			System.out.print("Aniversary of user ID: \n>> ");
			String cid = Integer.toString(keyboard.nextInt());

			Element a = requests.createElement("aniversary");
			Element c = requests.createElement("user");
			c.setAttribute("id", cid);
			rootElement.appendChild(a);
			a.appendChild(c);

			oos.writeObject(requests);
			Document r = (Document) ois.readObject();
			String expression = "string(//user[@id = \"" + cid + "\"]/@birthday)";
			String confirmation = (String) xPath.compile(expression).evaluate(r, XPathConstants.STRING);
			if (confirmation.equalsIgnoreCase("yes")) {
				confirmation = "IS";
			} else {
				confirmation = "IS NOT";
			}
			System.out.println(">>>> It " + confirmation + " user[" + cid + "]'s birthday.");
		} catch (InputMismatchException e) {
			System.out.println("Please introduce a valid INTEGER id.");
		} catch (IOException e) {
			System.out.println("Exception caught in Waiter.aniversary.");
		} catch (ClassNotFoundException e) {
			System.out.println("Exception in reading in Waiter.aniversary.");
		} catch (XPathExpressionException e) {
			System.out.println("Expection in xPath expression. Couldn't get evaluate in Waiter.aniversary.");
		}
	}

	public static void main(String[] args) {
		Waiter waiter = new Waiter();
		waiter.connect();
	}
}
