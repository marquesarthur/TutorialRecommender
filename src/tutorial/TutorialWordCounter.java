package tutorial;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import Utilities.Utils;

public class TutorialWordCounter {

	public static void main(String[] args)
	{
		String jodaTimeTutorialDir = "C:/Users/gaya/prog/workspace/TutorialDocs/joda";
		String mathTutorialDir = "C:/Users/gaya/prog/workspace/TutorialDocs/math";
		String javaTutorialDir = "C:/Users/gaya/prog/workspace/TutorialDocs/java";
		String jankovTutorialDir = "C:/Users/gaya/prog/workspace/TutorialDocs/jcol";
		String smackTutorialDir = "C:/Users/gaya/prog/workspace/TutorialDocs/smack";
		
		
		try {
			int count = getTutorialWordCount(jodaTimeTutorialDir);
			System.out.println("Jode " + count);
			
			count = getTutorialWordCount(mathTutorialDir);
			System.out.println("Math " + count);
			
			count = getTutorialWordCount(javaTutorialDir);
			System.out.println("java " + count);
			
			count = getTutorialWordCount(jankovTutorialDir);
			System.out.println("jankov " + count);
			
			count = getTutorialWordCount(smackTutorialDir);
			System.out.println("smack " + count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static int getTutorialWordCount(String tutorialDir) throws IOException
	{
		int count = 0;
		
		File dir = new File(tutorialDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				String html = Utils.readFileAsString(child.getAbsolutePath());
				String text = Utils.cleanHtml(html, true);
				
				int tmp = Utils.getWordCount(text);
				if (tmp == 0)
					System.out.println(child.getAbsolutePath());
				count+=tmp;
			}
			else
				count+=getTutorialWordCount(child.getAbsolutePath());
		
		return count;
	}
}
