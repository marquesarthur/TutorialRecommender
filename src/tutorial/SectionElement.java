package tutorial;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Utilities.Utils;
import au.com.bytecode.opencsv.CSVWriter;

import edu.stanford.nlp.util.CoreMap;

public class SectionElement {
	private TutorialSection section;
	private APIElement apiElement;
	private boolean isLabeled;
	private boolean label;
	private boolean guessedLabel;
	
	
	public SectionElement(TutorialSection pSection, APIElement pApiElement, Boolean pLabel)
	{
		setSection(pSection);
		apiElement = pApiElement;
		label = pLabel; 
	}
	
	public Boolean getIsLabeled() {
		return isLabeled;
	}
	public void setIsLabeled(Boolean label) {
		this.isLabeled = label;
	}
	
	public Boolean getLabel() {
		return label;
	}
	public void setLabel(Boolean label) {
		this.label = label;
	}
	public Boolean getGuessedLabel() {
		return guessedLabel;
	}
	public void setGuessedLabel(Boolean guessedLabel) {
		this.guessedLabel = guessedLabel;
	}

	public APIElement getApiElement() {
		return apiElement;
	}
	public void setApiElement(APIElement apiElement) {
		this.apiElement = apiElement;
	}

	public TutorialSection getSection() {
		return section;
	}

	public void setSection(TutorialSection section) {
		this.section = section;
	}
	
	public String getProcessedSectionText()
	{
		String sectionText = Utils.HTML2Text( 
				Utils.addPunctuationHTML(
				Utils.replaceCLTDelimiter(
				modifyAPIElementText(section.getHTML()))));
		
		return sectionText;
	}
	
	public List<CoreMap> getAllSentences(boolean parse)
	{
		String sectionText = getProcessedSectionText();

		List<CoreMap> sentences = Utils.getSentences(sectionText, parse);
		return sentences;
	}
	
	public List<CoreMap> getAPIElementSentences(boolean parse)
	{	
		List<CoreMap> sentences = section.sentences;
		
			//getAllSentences(parse);
		List<CoreMap> apiSentences = new ArrayList<CoreMap>();
		
		String formattedAPI = apiElement.getAPIElementName().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();
		if (sentences == null)
			System.out.println("Warning");
		for(CoreMap sent:sentences)
		{
			if(sent.toString().toLowerCase().indexOf("clt_"+formattedAPI) != -1)
				apiSentences.add(sent);
		}

		if (apiSentences.isEmpty())
			System.out.println("WARNGING: In getAPIElementSentences " + apiElement.getAPIElementName() + "," + section.getSubTitle()	);
		return apiSentences;
	}
	
	private String modifyAPIElementText(String html) {
		Document doc = Jsoup.parse(html);
		Elements clts = doc.select("clt[api=" + apiElement.getAPIElementName() + "]");
		
		for(Element clt: clts)
		{
			if (clt.text().equals(clt.attr("api")))
				clt.text("clt_" + clt.attr("api"));
			else
			{
				String[] content = clt.text().split("\\.");
				System.out.println(clt.text() + "=>" + "clt_" + clt.attr("api") + "_" +content[content.length-1]);				
				clt.text("clt_" + clt.attr("api") + "_" +content[content.length-1]);

			}
		}
		
		return doc.html();
	}
	
	public boolean isInCode()
	{
		return section.isInCode(apiElement);
	}

	public boolean hasCode()
	{
		return section.hasCode();
	}
	
	public static String getHighlightedText_jodaTime(String text, String color, String apiElement) // for jodaimte
	{
		String highlightBeginning = "<SPAN style=\"BACKGROUND-COLOR: " + color + "\">";
		String highlightingEnding = "</span>";
		String formattedAPI = apiElement.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-");
		String formattedText = text; 

		int index = text.indexOf("</h3>"); 
		if (index == -1)
			index = text.indexOf("</h2>");

		if (index != -1)
			formattedText = text.substring(0, index)
			.replaceFirst(formattedAPI, highlightBeginning +apiElement+highlightingEnding) 
			+ text.substring(index, text.length());


		formattedText = formattedText.replaceAll("<tt>clt_"+formattedAPI+"</tt>", 
				"<tt>" + highlightBeginning +apiElement+highlightingEnding+"</tt>");


		int currentIndex =  formattedText.indexOf("<pre>");		
		while (currentIndex != -1)
		{
			currentIndex+=5;
			int codeEnding = formattedText.indexOf("</pre>", currentIndex);
			int elementIndex = formattedText.indexOf(apiElement, currentIndex);			
			while(elementIndex!=-1 && elementIndex<codeEnding)
			{
				if (!Character.isLetter(formattedText.charAt(elementIndex-1)) && 
						!Character.isLetter(formattedText.charAt(apiElement.length() + elementIndex)))					
				{
					formattedText = formattedText.substring(0, elementIndex) + highlightBeginning + 
							apiElement + highlightingEnding + formattedText.substring(elementIndex+apiElement.length(), formattedText.length());

					currentIndex = elementIndex+apiElement.length() + highlightBeginning.length() + highlightingEnding.length();
				}
				else
					currentIndex =elementIndex+apiElement.length();
				elementIndex =  formattedText.indexOf(apiElement, currentIndex);
				codeEnding = formattedText.indexOf("</pre>", currentIndex);
			}

			currentIndex =  formattedText.indexOf("<pre>", currentIndex);
		}
		return formattedText;
	}

	public static String getHighlightedText_math(String text, String color, String apiElement) // for math after merging with recodoc
	{
		String highlightBeginning = "<SPAN style=\"BACKGROUND-COLOR: " + color + "\">";
		String highlightEnding = "</SPAN>";
		
		Document doc = Jsoup.parse(text);
		Elements apiElements = doc.select("clt[api=" + apiElement + "]");
		
		for(Element apielement: apiElements)
		{
			Document tmp = new Document("");
			String[] apis = apielement.text().split("\\.");
			if (apis.length == 2)
				apielement.html(highlightBeginning + apis[0] + highlightEnding + "." +apis[1]);
			else
				apielement.wrap(highlightBeginning);
		}
		
		// highlight code snippet
		Elements codesnippets =  doc.getElementsByTag("pre");
		for(Element codesnippet:codesnippets)
		{
			String html = codesnippet.html();
			Pattern apielementPattern = Pattern.compile("(?<=\\W)"+apiElement + "(?=\\W)");
			Matcher matcher = apielementPattern.matcher(html);
			
			codesnippet.html(matcher.replaceAll(highlightBeginning + apiElement + highlightEnding ));
			
		}
		
		
		// remove clt tags for display
		Elements clts = doc.getElementsByTag("clt");
		for(Element clt:clts)			
		{
			clt.unwrap();
//			clt.replaceWith(new TextNode(clt.text(), ""));
		}
		
		return doc.html();
	}

	public void saveToFile(String saveFilePath)
	{
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(saveFilePath, true));
			writer.writeNext(new String[] {section.getSubTitle(),apiElement.getAPIElementName(),Boolean.toString(label)});
			writer.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getHighlightedWord(String textToHighlight,
			String color, String word) {
		String highlightBeginning = "<SPAN style=\"BACKGROUND-COLOR: " + color + "\">";
		String highlightingEnding = "</span>";
		String formattedText = textToHighlight; 

		formattedText = formattedText.replaceAll("\\s" + word + "\\s", 
				" " + highlightBeginning + word + highlightingEnding + " ");

		return formattedText;
	}
	
	public static String addCLTTags(String text, String apiElement)
	{
		String formattedElement = apiElement.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-");
		String searchapi = apiElement.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\.", "\\\\.");
		
		String formattedText = text; 

		int index = text.indexOf("</h3>"); 
		if (index == -1)
			index = text.indexOf("</h2>");

		if (index != -1)
			formattedText = text.substring(0, index)
			.replaceFirst(searchapi, "clt_" +formattedElement) 
			+ text.substring(index, text.length());


		formattedText = formattedText.replaceAll("<tt>"+searchapi+"</tt>", 
				"<tt>" + "clt_" + formattedElement +"</tt>");

		return formattedText;
	}
}
