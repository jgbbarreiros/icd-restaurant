package menu;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileManager {

	private DocumentBuilder documentBuilder;
	private Transformer transformer;
	private String currentDocName;

	public FileManager() {
		currentDocName = "document";
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			System.err.println(e.getMessage());
		} catch (TransformerConfigurationException e) {
			System.err.println(e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			System.err.println(e.getMessage());
		}
	}

	public Document blank() {
		return documentBuilder.newDocument();
	}

	public Document load(String docName) {
		try {
			File file = new File(docName);
			documentBuilder.parse(file);
			return documentBuilder.parse(file);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public boolean save(Document document) {
		if (currentDocName.isEmpty())
			return false;
		saveAs(document, currentDocName);
		return true;
	}

	public boolean saveAs(Document document, String docName) {
		try {
			// TODO document has root element ? go on : create and append root
			// element
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(docName + ".xml"));
			transformer.transform(source, result);
			return true;
		} catch (TransformerException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

}
