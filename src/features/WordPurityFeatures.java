package features;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Utilities.Utils;

import tutorial.SectionElement;
import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;

public class WordPurityFeatures {
	public static void eval(Tutorial tutorial, int currentSection)
	{ 
		calcWordPurity(tutorial);
	}
	
	public static HashMap<String, TreeMap<String, Double>> wordPurityPerElement = null;
	public static HashMap<String, HashMap<String, Double>> wordsPerSection = null;
	
	public static HashMap<String, HashMap<String, Double>> wordsRelSection = null;
	public static HashMap<String, HashMap<String, Double>> wordsNotRelSection = null;
	public static HashMap<String, HashMap<String, Double>> wordsOtherSection = null;	
	
	public static HashMap<String, Double> wordsPerTutorial = null;
	public static HashMap<String, String> wordTags = null;
	
	public static void calcWordPurity(Tutorial tutorial)
	{
		if (wordPurityPerElement == null)
		{
			wordsPerSection = new HashMap<String, HashMap<String, Double>>();
			wordsRelSection = new HashMap<String, HashMap<String, Double>>();
			wordsNotRelSection = new HashMap<String, HashMap<String, Double>>();
			
			wordsPerTutorial = new HashMap<String, Double>();
			wordPurityPerElement = new HashMap<String, TreeMap<String,Double>>();
			
			wordTags = new HashMap<String, String>();
			
			for(String section:tutorial.sectionElementsPerSection.keySet())
			{
					String text = tutorial.sectionElementsPerSection.get(section).get(0).getSection().getText();
					List<CoreLabel> wordList = Utils.getWordList(text, true);
					
					for(CoreLabel token:wordList)
					{
						String word = token.get(TextAnnotation.class);
						incrementWordCount(word, wordsPerTutorial, 1.0);
						incrementSubHM(section, word, wordsPerSection, 1.0);
						wordTags.put(word, token.get(PartOfSpeechAnnotation.class));
					}
					
				}
			}
			
			for(SectionElement sectionEl:tutorial.sectionElements)
			{
				if(sectionEl.getLabel())
				{
					String title = sectionEl.getSection().getSubTitle();
					
					// for each word that appears in relevant section 
					for (String word:wordsPerSection.get(title).keySet())
					{
						//Double purity = wordsPerSection.get(title).get(word)/wordsPerTutorial.get(word);
						incrementSubHM(sectionEl.getApiElement().getAPIElementName(), word, wordsRelSection , 1.0);
					}
				}
				else
				{
					String title = sectionEl.getSection().getSubTitle();
					
					// for each word that appears in not relevant section 
					for (String word:wordsPerSection.get(title).keySet())
					{
						//Double purity = wordsPerSection.get(title).get(word)/wordsPerTutorial.get(word);
						incrementSubHM(sectionEl.getApiElement().getAPIElementName(), word, wordsNotRelSection, 1.0);
					}
				}
			}
			
	}
	
	public static void incrementWordCount(String word, HashMap<String, Double> hm, Double value)
	{
		Double count = hm.get(word);
		if (count == null)
			hm.put(word, value);
		else
			hm.put(word, count+value);
	}
	
	private static void incrementSubHM(String sectionTitle, String word, HashMap<String, HashMap<String, Double>> hm, Double value)
	{
		HashMap<String, Double> subHM = hm.get(sectionTitle);
		if (subHM == null)
		{
			subHM = new HashMap<String, Double>();
			subHM.put(word, value);
			hm.put(sectionTitle, subHM);
		}
		else
		{
			incrementWordCount(word, subHM, value);
			hm.put(sectionTitle, subHM);
		}
	}	
}

