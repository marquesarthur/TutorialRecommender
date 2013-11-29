package tutorial;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import TermExtraction.DependencyExtractor;
import Utilities.HTMLUtils;
import Utilities.Utils;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;


public class TutorialSection {

	public String sectionTitle;
	public String subsectionTitle;
	private String sectionHTML;
	public String text;
	public List<CoreMap> sentences;
	public List<CoreMap> apiSentences;
	public boolean joda = false;
	
	private boolean isLabeled;
	private boolean label;
	private boolean guessedLabel;
	
	private String APIElement;
	public String fqn;
	public String kind;
	private String url;

	private Paragraph[] sectionText;
	
	private List<String> codesnippets;

	// for testing purposes
	public TutorialSection()
	{}

	public TutorialSection(String psubsectionTitle, String purl)
	{
		subsectionTitle = psubsectionTitle;
		url = purl; 
	} 

	public TutorialSection(String pSectionTitle, String pSubSectionTitle, String pSubSectionHTML, String pAPIElement, String pfqn, String pKind)
	{
		sectionTitle = pSectionTitle;
		subsectionTitle = pSubSectionTitle;
		sectionHTML = pSubSectionHTML;
		APIElement = pAPIElement;
		fqn = pfqn;
		kind = pKind;
		codesnippets = new  ArrayList<String>();
	}
	
//	public TutorialSection(String psubsectionTitle, String pHtml, String pAPIElement, String pfqn, String pkind)
//	{
//		subsectionTitle = psubsectionTitle;
//		sectionHTML = pHtml;
//		text = Jsoup.clean(sectionHTML, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");
//		APIElement = pAPIElement;		
//		fqn = pfqn;
//		kind = pkind;		
//	}

	// load labeled data for classification
	public TutorialSection(String psectionTitle, String psubsectionTitle, String pHtml, boolean collapseCodes)
	{		
		sectionTitle = psectionTitle;
		subsectionTitle = psubsectionTitle;
		sectionHTML = pHtml;		
		
		if (collapseCodes)
		{
			Document doc = Jsoup.parse(sectionHTML);
			codesnippets = Utils.collapseCodeSegments(doc);
			sectionHTML = doc.html();
		}
		text = Jsoup.clean(sectionHTML, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");

	}

	// load data for annotation
	public TutorialSection(Boolean plabeled, String pSectionTitle, String psubsectionTitle, String pHtml, String pAPIElement, String pfqn, String pKind)
	{		
		isLabeled = plabeled;
		sectionTitle = pSectionTitle;
		subsectionTitle = psubsectionTitle;
		setHTML(pHtml);		
		codesnippets = new  ArrayList<String>();
		
		fqn = pfqn;		
		APIElement = pAPIElement;
		kind = pKind;
	}


	public String getHTML()
	{
		return sectionHTML;
	}
	
	public void setHTML(String html)
	{
		sectionHTML = html;
		text = Jsoup.clean(sectionHTML, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");
	}
	
	public String getText()
	{
		return text;
	}

	public boolean getOriginalLable()
	{
		return label;
	}

	public boolean getGuessedLable()
	{
		return guessedLabel;
	}

	public void setURL(String purl)
	{
		url = purl;
	}
	public String getURL()
	{
		return url;
	}


	public String getTitle()
	{
		return sectionTitle;
	}

	public String getSubTitle()
	{
		return subsectionTitle;
	}

	public String getAPIElement()
	{
		return APIElement;
	}

	public boolean isInCode(APIElement apiElement)
	{
		Boolean inCode = false;
		for (String code:codesnippets)
		{
			
			if (Pattern.compile(".*[^A-Za-z]" + apiElement.getAPIElementName() + "[^A-Za-z].*", Pattern.DOTALL).matcher(code).matches())
				inCode = true;
		}
		return inCode;
	}

	public boolean hasCode()
	{
		return !codesnippets.isEmpty();
	}


	public void parseSentences()
	{
		text = Utils.HTML2Text( 
				Utils.addPunctuationHTML(
				Utils.replaceCLTDelimiter(
				Utils.modifyAPIElementText(sectionHTML))));
		text = DependencyExtractor.collapsNounPhrases(text);		
		sentences = Utils.getSentences(text, true);
		apiSentences = new ArrayList<CoreMap>();
	}
	

//	public static void LoadSectionInfo(List<TutorialSection> sections, String sectionInfoPath) {
//		CSVReader reader;
//		try {
//			reader = new CSVReader(new FileReader(sectionInfoPath), '\t');
//
//			String[] nextLine;
//			while ((nextLine = reader.readNext()) != null) {
//				//System.out.println(Boolean.valueOf(nextLine[3]));
//				TutorialSection  s = new TutorialSection(nextLine[0], nextLine[1], nextLine[2], Boolean.valueOf(nextLine[3]));					
//				sections.add(s);
//			}
//			reader.close();
//		} catch (FileNotFoundException e) {
//			System.out.println(sectionInfoPath + " is not a valid path.");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}

	public void saveToFile(String saveFilePath)
	{
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(saveFilePath, true));
			writer.writeNext(new String[] {subsectionTitle,APIElement,Boolean.toString(label)});
			writer.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveToFile(List<TutorialSection> tutorial, String saveFilePath) throws IOException
	{
		CSVWriter writer = new CSVWriter(new FileWriter(saveFilePath, false));
		for (TutorialSection section:tutorial)
			writer.writeNext(new String[] {"", section.sectionTitle,section.subsectionTitle, section.getHTML(), section.APIElement, section.fqn, section.kind, "false"});
		writer.close();
	}

	public String toString()
	{
		return subsectionTitle + ":" + url;
	}

	public boolean equals(Object sectionObj)
	{
		TutorialSection section = (TutorialSection)sectionObj;
		return (section.sectionTitle.toLowerCase().equals(sectionTitle.toLowerCase())
				&& section.subsectionTitle.toLowerCase().equals(subsectionTitle.toLowerCase()) 
				&& section.APIElement.toLowerCase().equals(APIElement.toLowerCase()));
	}

	public void setRel(boolean prel)
	{
		label = prel;
	}

	public void setLabeled(boolean pisLabeled)
	{
		isLabeled = pisLabeled;
	}

	public boolean isLabeled()
	{
		return isLabeled;
	}

	public String getState()
	{
		if(isLabeled)
			return "labeled";
		return "";
	}

	public int getSectionLength()
	{
		return HTMLUtils.getHTMLWordCount(sectionHTML);
	}

	private boolean containsTextNode(List<Node> childrenList)
	{
		boolean containsTextNode = false;

		for (Node child:childrenList)
			if (child instanceof TextNode)
			{
				containsTextNode = true;
				break;
			}

		return containsTextNode;
	}

	

		
	public String getClassName()
	{
		String result;
		String[] hierarchy = fqn.split("\\.");
		if (!kind.equals("class"))
			result = hierarchy[hierarchy.length-2];
		else
			result = hierarchy[hierarchy.length-1];
		
		return result;
	}

	
	
	private String addClassnameToMethods(String apiName, String fqn, String kind) {
		
		String result = apiName;
		if (!apiName.contains("."))
		{	
			result = getClassName() + "." + apiName;
		}
		
		return result;
	}
	
	public int getNumberofWords()
	{
		int size = 0;
		 
		for (CoreMap sent:sentences)
		{
			if(sent.get(TokensAnnotation.class).size()>1)
			{
				size += sent.get(TokensAnnotation.class).size();
			}
		}
		
		return size;
	}

	public List<String> getLemmaList()
	{
		List<String> lemmaList = new ArrayList<String>();

		for(CoreMap sentence:sentences)
			for(CoreLabel token:sentence.get(TokensAnnotation.class))
			{
				String word = token.get(LemmaAnnotation.class);
				lemmaList.add(word);
			}
		
		return lemmaList;
	}
}
