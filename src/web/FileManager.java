package web;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FileManager {

	private DocumentBuilder documentBuilder;
	private Transformer transformer;
	private String docName;

	public FileManager() {
		docName = "document";
		try {
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
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
			File file = new File(docName + ".xml");
			documentBuilder.parse(file);
			return documentBuilder.parse(file);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public Document loadString(String docString) {
		InputSource is = new InputSource(new StringReader(docString));
	    try {
			return documentBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return null;
	}

	public boolean save(Document document) {
		if (docName.isEmpty())
			return false;
		saveAs(document, docName);
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
	
	public String docToString(Document document) {
		DOMImplementationLS domImplementation = (DOMImplementationLS) document.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(document);
	}

	public void logDoc(Document document) {
		System.out.println(docToString(document));
	}

}
