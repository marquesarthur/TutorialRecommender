package features.sentencelevel;

import java.util.List;

import tutorial.Tutorial;
import edu.stanford.nlp.util.CoreMap;

public class IsInBracketsFeatures {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		Boolean isInBrackets = false;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		String formattedAPI = api.toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "_");
		
		for (CoreMap sent:sentences)
		{
			if (!sent.toString().toLowerCase().matches(".*\\(note[^\\)]*"+formattedAPI+".*") &&
					sent.toString().toLowerCase().matches(".*\\([^\\)]*"+formattedAPI+".*"))
				isInBrackets = true;
		}

		return isInBrackets;
	}
}
