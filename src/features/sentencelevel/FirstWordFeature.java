package features.sentencelevel;

import java.util.List;


import tutorial.Tutorial;
import Utilities.*;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class FirstWordFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		Boolean isFirstWord = false;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getSection().sentences;
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		 
		// ignore the first sentence because thats the title.
		for (int i = 1; i<sentences.size(); i++)
		{
			CoreMap sent = sentences.get(i);
			if(sent.get(TokensAnnotation.class).size()>1)
			{
				String firstword = "";
				int j = 0;
				do
				{
					firstword = sent.get(TokensAnnotation.class).get(j++).get(TextAnnotation.class);
				}
				while(Utils.isStopWord(firstword));
				
				
//				PorterStemmer s = new PorterStemmer();
//				s.add(firstword.toCharArray(), firstword.length());
//				s.stem();
//				firstword = s.toString();
//				
//				s.add(api.toCharArray(), api.length());
//				api = s.toString();
				
				if (firstword.startsWith(api) || firstword.startsWith("clt_" + api))
					isFirstWord = true;
			}
		}

		return isFirstWord;
	}
}
