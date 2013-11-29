package features.sectionlevel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tutorial.Tutorial;

public class APIAsMethodFeature {

	public static int eval(Tutorial tutorial, int currentSection)
	{
		if (!tutorial.sectionElements.get(currentSection).getSection().joda)
		{
			int notClassCount = 0;
			String sectionHTML =  tutorial.sectionElements.get(currentSection).getSection().getHTML();
			String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();

			Document doc = Jsoup.parse(sectionHTML);
			Elements elements = doc.select("clt[api=" + api + "]");

			for (Element el:elements)
			{
				String content = el.text();
				String[] apiParts = content.split("_");

				// the format of the content is as follows
				// clt(_packagename)?_classname(_(methodname | fieldname))?

				// if last part is not api then it is not mentioned as class
				if (!apiParts[apiParts.length-1].equals(api))
					notClassCount++;
			}

			return notClassCount;
		}
		else 
		{
			if (!tutorial.sectionElements.get(currentSection).getApiElement().getKind().toLowerCase().equals("class"))
				return NumberOfOccuranceFeature.eval(tutorial, currentSection);
			else
				return 0;
		}
	}
}
