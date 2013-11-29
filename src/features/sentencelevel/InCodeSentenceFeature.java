package features.sentencelevel;

import java.util.List;

import tutorial.Tutorial;
import edu.stanford.nlp.util.CoreMap;

public class InCodeSentenceFeature {
	
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);

		boolean withCode = false;
		for (CoreMap sent:sentences)
		{
			if (sent.toString().contains("CODEID"))
				withCode = true;
		}

		return withCode;
	}
}
