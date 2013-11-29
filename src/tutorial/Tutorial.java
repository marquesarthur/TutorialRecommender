package tutorial;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import TermExtraction.DependencyClassifier;
import Utilities.DependencyStat;
import Utilities.RelationStat;
import Utilities.Utils;
import au.com.bytecode.opencsv.CSVReader;

public class Tutorial {

	public List<SectionElement> sectionElements = new ArrayList<SectionElement>();
	public HashMap<String, ArrayList<SectionElement>> sectionElementsPerElement = new HashMap<String, ArrayList<SectionElement>>();
	public HashMap<String, ArrayList<SectionElement>> sectionElementsPerSection = new HashMap<String, ArrayList<SectionElement>>();
	
	
	public DependencyStat tutDepStat = new  DependencyStat();
	public RelationStat tutRelStat = new RelationStat();
	
	public boolean isMath;
	
	// alpha is weight parameter from 0 to 1 to combine learnt and fixed weight for dependencies 
	public float alpha = 1.0f;

	// beta is weight parameter from 0 to 1 to combine learnt and fixed weight for relations 
	public float beta = 1.0f;


	public void LoadTutorial(String sectionInfoPath, boolean parse, boolean joda)
	{
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(sectionInfoPath), ',');

			String[] nextLine;
			String prevTitle = "";
			TutorialSection  section = null;
			while ((nextLine = reader.readNext()) != null) {
				
				String title = nextLine[1];
				String subtitle = nextLine[2];
				String html = nextLine[3];
				String element = nextLine[4];				
				String fqn = nextLine[5];
				String kind = nextLine[6];
				boolean label =  Integer.valueOf(nextLine[7])==1; //
				
				boolean guess = false;
				if (nextLine.length  == 9)
					guess = Integer.valueOf(nextLine[8])==1;

				//System.out.println(subtitle);
				if (!prevTitle.equals(subtitle))
				{
					prevTitle = subtitle;					
					section = new TutorialSection(title, subtitle, html, parse);
				}
				
				if (joda)
				{
					section.setHTML(SectionElement.addCLTTags(section.getHTML(), element));
					section.joda = true;
				}
				
				//element = element.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-");

				
				APIElement apielement = new APIElement(element, fqn, kind, html);
				SectionElement secElPair = new SectionElement(section, apielement, label);
				secElPair.setGuessedLabel(guess);
				sectionElements.add(secElPair);
				
				if (sectionElementsPerElement.get(apielement.getAPIElementName()) == null)
				{
					ArrayList<SectionElement> newList = new ArrayList<SectionElement>();
					newList.add(secElPair);
					sectionElementsPerElement.put(apielement.getAPIElementName(), newList);
				}
				else
				{
					ArrayList<SectionElement> pairList = sectionElementsPerElement.get(apielement.getAPIElementName()) ;
					pairList.add(secElPair);
					sectionElementsPerElement.put(apielement.getAPIElementName(), pairList);
				}
				
				String titleKey = section.getTitle()+section.getSubTitle();
				if (sectionElementsPerSection.get(titleKey) == null)
				{
					ArrayList<SectionElement> newList = new ArrayList<SectionElement>();
					newList.add(secElPair);
					sectionElementsPerSection.put(titleKey, newList);
				}
				else
				{
					ArrayList<SectionElement> pairList = sectionElementsPerSection.get(titleKey) ;
					pairList.add(secElPair);
					sectionElementsPerSection.put(titleKey, pairList);
				}
			}
			reader.close();
			
			if (parse)
			{
				for (String title:sectionElementsPerSection.keySet())			
				{
					TutorialSection sec = sectionElementsPerSection.get(title).get(0).getSection();
//					System.out.println(sec.getTitle());
					if (sec.sentences == null)
						sec.parseSentences();
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println(sectionInfoPath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateTutorial(List<TutorialSection> tutorial, String subsectionTitle, String sectionTitle, String sectionHTML)
	{
		for(TutorialSection section:tutorial)
		{
			if (section.subsectionTitle.equals(subsectionTitle) && section.sectionTitle.equals(sectionTitle))
				section.setHTML(sectionHTML);
		}
	}

	public String highlghtSection(int currentIndex)
	{
		String title = (String) sectionElementsPerSection.keySet().toArray()[currentIndex];
		String textToHighlight = sectionElementsPerSection.get(title).get(0).getSection().getHTML();
		String color;
		for (SectionElement section:sectionElementsPerSection.get(title))
		{
			if (section.getLabel())
			{
				if (section.getGuessedLabel())
					color = "#aaffaa";
				else
					color = "#00cc00";
			}
			else				
			{
				if (!section.getGuessedLabel())
					color = "#ffaaaa";
				else
					color = "#cc0000";
			}
			
			if(!section.getSection().joda)
			{
				textToHighlight = SectionElement.getHighlightedText_math(textToHighlight, color, section.getApiElement().getAPIElementName());
			}
			else
			{
				textToHighlight = SectionElement.getHighlightedText_jodaTime(textToHighlight, color, section.getApiElement().getAPIElementName());
			}
			
		}
		return textToHighlight;		
	}
	
	public String highlightElement(int currentIndex)
	{
		String text = sectionElements.get(currentIndex).getSection().getHTML();
		String color = "#ffff00";
		String apiElement = sectionElements.get(currentIndex).getApiElement().getAPIElementName();
		String highlight;
		
		if(!sectionElements.get(currentIndex).getSection().joda)
		{
			highlight = SectionElement.getHighlightedText_math(text, color, apiElement);
		}
		else
		{
			highlight = SectionElement.getHighlightedText_jodaTime(text, color, apiElement);
		}
			
		return highlight;
	}

	public String highlightwords(String highlghtSection, String wordlistPath) {
		CSVReader reader;
		
		String textToHighlight = highlghtSection;
		String color = "#ffff00";
		try {
			reader = new CSVReader(new FileReader(wordlistPath), '\t');

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				String word = nextLine[0];
				textToHighlight = SectionElement.getHighlightedWord(textToHighlight, color, word);	
			}
			reader.close();
		}
		catch(IOException ex)
		{
			
		}
		return textToHighlight;	
	}
	
	// retruns tutorial text with 'RELCLT' word instead of relevant APIElements and 'CLT' instead of not relevant 
	public String getAnnotatedText()
	{
		StringBuilder tutorialText = new StringBuilder();
		
		for (String secTitle:sectionElementsPerSection.keySet())
		{
			List<SectionElement> sectionElements = sectionElementsPerSection.get(secTitle);
			String sectionHTML = sectionElements.get(0).getSection().getHTML();
			
			for (SectionElement sectionEl:sectionElements)
			{
				String apiElement = sectionEl.getApiElement().getAPIElementName();
				if(sectionEl.getLabel())
					sectionHTML = Pattern.compile("clt:"+apiElement).matcher(sectionHTML).replaceAll("RELCLT");
				else
					sectionHTML = Pattern.compile("clt:"+apiElement).matcher(sectionHTML).replaceAll("CLT");
			}
			
			tutorialText.append(Utils.HTML2Text(Utils.addPunctuationHTML(sectionHTML)) + "\n");
		}
		
		return tutorialText.toString();
		
	}
	
	public void trainStat()
	{
		DependencyClassifier.trainTutorialStat(this);
	}
	
	public void printPerAPIElement()
	{
		for (String api:sectionElementsPerElement.keySet())
		{
			System.out.println(api + ", " + sectionElementsPerElement.get(api).size());
		}
	}
}

