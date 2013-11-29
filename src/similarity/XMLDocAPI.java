package similarity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class XMLDocAPI {

	Document doc = null;
	public XMLDocAPI() {

	}

	public XMLDocAPI(String xmlDocPath) {
		try {
			openXMLDocument(xmlDocPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int numberOfClasses()
	{
		int count = 0;
	
		if (doc != null)
		{
			List<String> apielements = new ArrayList<String>();
	
			NodeList nodes = doc.getElementsByTagName("Reference");
			for (int i=0; i<nodes.getLength();i++){
				if (!nodes.item(i).getTextContent().contains("#"))
				{
					System.out.println(nodes.item(i).getTextContent());
					count++;
				}
			}
		}

		return count;

	}

	public List<String> getAllTokenTexts()
	{
		if (doc == null)
			return null;

		List<String> words = new ArrayList<String>();

		NodeList nodes = doc.getElementsByTagName("Token");
		for (int i=0; i<nodes.getLength();i++){			
			words.add(nodes.item(i).getTextContent());
		}

		return words;

	}

	public void openXMLDocument(String xmlDocPath) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace( true );
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(xmlDocPath);
	}

	public List<String> getAllTokenLemmas() 
	{
		if (doc == null)
			return null;

		List<String> lemmas = new ArrayList<String>();

		NodeList nodes = doc.getElementsByTagName("Token");
		for (int i=0; i<nodes.getLength();i++){
			Element el = (Element)nodes.item(i);
			lemmas.add(el.getAttribute("Lemma"));
		}

		return lemmas;
	}

	public List<String> getAllAPIElements()
	{
		if (doc == null)
			return null;

		List<String> apielements = new ArrayList<String>();

		NodeList nodes = doc.getElementsByTagName("Reference");
		for (int i=0; i<nodes.getLength();i++){			
			apielements.add(nodes.item(i).getTextContent());
		}

		return apielements;
	}

	public List<String> getTokenLemmas(String apiElement) throws XPathExpressionException
	{
		// Create a XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// Create a XPath object
		XPath xpath = xFactory.newXPath();

		// Compile the XPath expression
		XPathExpression expr = xpath.compile("//ReferenceDocument[contains(Reference,\""+apiElement+"\")]//Token");
		// Run the query and get a nodeset
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		List<String> lemmas = new ArrayList<String>();
		for (int i=0; i<nodes.getLength();i++){
			Element el = (Element)nodes.item(i);
			lemmas.add(el.getAttribute("Lemma"));	    	
		}

		return lemmas;
	}

	public static void main(String[] args) {
		String xmlPath = "../APIDoc/data/xml_NLP/jodatime.xml";
		try {
			XMLDocAPI xmlDoc = new XMLDocAPI(xmlPath); 
			List<String> lemmas = xmlDoc.getAllTokenLemmas();
			for(String lemma:lemmas)
				System.out.println(lemma);

			List<String> words = xmlDoc.getAllTokenTexts();
			for(String word:words)
				System.out.println(word);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
