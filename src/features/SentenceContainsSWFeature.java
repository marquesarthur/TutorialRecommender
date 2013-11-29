package features;

import java.util.List;

import Utilities.Utils;

import tutorial.Tutorial;
import edu.stanford.nlp.util.CoreMap;

public class SentenceContainsSWFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		Boolean containsAnySW = false;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		
		for (CoreMap sent:sentences)
		{
			List<String> seedWords = Utils.getSeedWords();
			for(String seedword:seedWords)
			{
				if( Utils.containsWord(sent.toString(), seedword))
					containsAnySW = true;
			}
		}

		return containsAnySW;
	}
	

}
