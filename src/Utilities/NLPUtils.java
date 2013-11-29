package Utilities;

public class NLPUtils {

	public static String getStem(String word)
	{
		PorterStemmer s = new PorterStemmer();
		s.add(word.toCharArray(), word.length());
		s.stem();
		
		return s.toString();
	}
}
