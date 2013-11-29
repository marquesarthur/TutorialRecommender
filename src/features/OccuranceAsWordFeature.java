package features;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tutorial.Tutorial;
import Utilities.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class OccuranceAsWordFeature {
	public static double eval(Tutorial tutorial, int currentSection)
	{
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getSection().sentences;
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		
		
		
		//if api name is a combined name, then split it and add parts to regex
		String[] apiparts = api.split("(?=\\p{Upper})");
		List<String> apiStemParts = new ArrayList<String>();;
		
		for(int i = 1; i<apiparts.length; i++)
			apiStemParts.add(Utils.getLemmedWord(apiparts[i]).toLowerCase());
		
		double count = 0;
		// skip title which should be the first sentence, because it is counted in other feature
		for (int i = 1; i<sentences.size(); i++)
		{
			CoreMap sent = sentences.get(i);
			for(CoreLabel token:sent.get(TokensAnnotation.class))
			{
				String lemmedWord = token.get(LemmaAnnotation.class).toLowerCase();
				if (lemmedWord.equals(api.toLowerCase()))
					count++;
				if (apiStemParts.contains(lemmedWord ))
					count += 1.0/(double)apiStemParts.size();
			}
		}
		return count;
	}
}
