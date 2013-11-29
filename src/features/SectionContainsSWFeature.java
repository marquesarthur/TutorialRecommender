package features;

import java.util.List;

import Utilities.Utils;

import tutorial.Tutorial;
import edu.stanford.nlp.util.CoreMap;

public class SectionContainsSWFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		Boolean containsAnySW = false;
		String text = tutorial.sectionElements.get(currentSection).getSection().getText();
		
		List<String> seedWords = Utils.getSeedWords();
		for(String seedword:seedWords)
		{
			if (Utils.containsWord(text, seedword))
				containsAnySW = true;
		}
		
		return containsAnySW;
	}
}
