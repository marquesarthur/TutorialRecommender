package features;

import java.util.List;

import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class SectionLengthFeature {
	public static int eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAllSentences(false);
		int count = 0;
		for (CoreMap sent:sentences)
		{
			count +=sent.get(TokensAnnotation.class).size();
		}
	
		return count;
	}
}
