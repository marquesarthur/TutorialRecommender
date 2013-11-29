package features;

import java.util.List;

import Utilities.Utils;

import tutorial.Tutorial;
import edu.stanford.nlp.util.CoreMap;

public class ContainsWordFromList {
	public static boolean eval(Tutorial tutorial, int currentSection, boolean positive)
	{ 
		Boolean containsAnySW = false;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		
		for (CoreMap sent:sentences)
		{
			List<String> words ;
			if (positive)
				words = Utils.getPositiveWords();
			else
				words = Utils.getNegativeWords();
			for(String seedword:words)
			{
				if( Utils.containsWord(sent.toString(), seedword))
					containsAnySW = true;
			}
		}

		return containsAnySW;
	}
	

}