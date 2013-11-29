package features;

import java.util.ArrayList;
import java.util.List;


import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class ContainsTitleWordsFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		Boolean containsTitleWords = false;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		String formattedAPI = api.toLowerCase().replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\.", "_");
		
		String[] words = tutorial.sectionElements.get(currentSection).getSection().getSubTitle().split("-");
		List<String> titleWordsList = new ArrayList<String>();
		for (String word:words)
			titleWordsList.add(word);
		
		for (int i = 1; i < sentences.size(); i++)
		{
			CoreMap sent = sentences.get(i);
			for (CoreLabel token:sent.get(TokensAnnotation.class))
			{	
				String[] sectionwords = token.get(TextAnnotation.class).split(":"); 
				if (titleWordsList.contains(token.get(TextAnnotation.class)))					
					containsTitleWords = true;
			}
		}

		return containsTitleWords;
	}
}
