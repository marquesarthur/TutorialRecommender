package features.sectionlevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tutorial.SectionElement;
import tutorial.Tutorial;

public class NumberOfOccuranceFeature {
	public static int eval(Tutorial tutorial, int currentSection)
	{
		int count = 0;

		if (tutorial.sectionElements.get(currentSection).getSection().joda)
		{
			String highlightBeginning = "<SPAN style=\"BACKGROUND-COLOR:";

			int index = 0;
			do{
				index = tutorial.highlightElement(currentSection).toLowerCase().indexOf(highlightBeginning.toLowerCase(), index+1);			
				count++;
			}
			while (index != -1);
			count--;
		}
		else
		{
			String html = tutorial.sectionElements.get(currentSection).getSection().getHTML();
			String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
			
			Document doc = Jsoup.parse(html);
			Elements apiElements = doc.select("clt[api=" + api + "]");
			
			count += apiElements.size();
			
			// find api in code snippet
			Elements codesnippets =  doc.getElementsByTag("pre");
			for(Element codesnippet:codesnippets)
			{
				String snippethtml = codesnippet.html();
				Pattern apielementPattern = Pattern.compile("(?<=\\W)"+api + "(?=\\W)");
				Matcher matcher = apielementPattern.matcher(snippethtml);
				
				while(matcher.find())
					count++;
			}
		}

		return count;
	}
}
