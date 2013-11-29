package features;

import java.util.ArrayList;
import java.util.List;

import tutorial.Tutorial;
import Utilities.NLPUtils;
import Utilities.Utils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class IsInTitleAsWordFeaure {
	public static boolean eval(List<String> titleWords, String api)
	{
		//if api name is a combined name, then split it and add parts to regex
		String[] apiparts = api.split("(?=\\p{Upper})");
		List<String> apiStemParts = new ArrayList<String>();;
		
		for(int i = 1; i<apiparts.length; i++)
			apiStemParts.add(NLPUtils.getStem(apiparts[i]).toLowerCase());
		
		int count = 0;
		for(String word:titleWords)
		{
			word = word.toLowerCase();
			if (apiStemParts.contains(word))
			{
				apiStemParts.remove(word);
				count++;
			}
				
		}
		if(count>0 && apiStemParts.size() == 0)
			return true;
		else
			return false;
	}
}
