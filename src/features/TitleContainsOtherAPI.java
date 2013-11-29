package features;

import java.util.List;

import Utilities.Utils;

import tutorial.APIElement;
import tutorial.SectionElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;

public class TitleContainsOtherAPI {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{
		
		String title = tutorial.sectionElements.get(currentSection).getSection().getSubTitle();
		String apiname = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		
		List<SectionElement> sectionElPairs = tutorial.sectionElementsPerSection.get(title);
		for (SectionElement sectionElPair:sectionElPairs)
		{
			TutorialSection section = sectionElPair.getSection();
			APIElement apielement = sectionElPair.getApiElement();
			
			boolean inTitle = false;
			if (! apielement.getAPIElementName().equals(apiname))
			{
				List<String> apiWords = Utils.tokenizeTitle(apielement.getAPIElementName());
				List<String> titleWords = Utils.tokenizeTitle(section.getSubTitle());
				
				for(String api:apiWords)
					if (titleWords.contains(api))
					{
						inTitle = true;
					}
			}
			if (inTitle)
				return true;
		}
		return false;
				
	}
}
