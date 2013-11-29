package features.sentencelevel;

import Utilities.Utils;
import tutorial.Tutorial;

public class InFirstSentenceFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		String firstSentences = tutorial.sectionElements.get(currentSection).getSection().sentences.get(1).toString();
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
	
		boolean contains = Utils.containsWord(firstSentences, api);
//		if(!contains)
//			System.out.println(firstSentences + "- do not contain -" + api );
		
		return contains;
	}
}
