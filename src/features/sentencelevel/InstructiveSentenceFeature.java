package features.sentencelevel;

import java.util.List;

import tutorial.Tutorial;
import TermExtraction.ImperativeMood;
import edu.stanford.nlp.util.CoreMap;

public class InstructiveSentenceFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		String formattedAPI = api.toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-");

		boolean isExample;
		for (CoreMap sent:sentences)
		{
			isExample = false;
			
			// if contains any of enumerated keywords than current APIElement is mentioned as an example
			if (sent.toString().toLowerCase().matches(".*(such as|for example|for instance|in particular).*"+formattedAPI+"([^A-Za-z].*)?"))
				isExample = true;		

			if (!isExample && sent.toString().toLowerCase().matches("(to|when|by|try|note|in order)([^A-Za-z].*)?"))
				return true;
		}
		return false;
	}
}
