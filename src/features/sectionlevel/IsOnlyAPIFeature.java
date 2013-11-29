package features.sectionlevel;

import tutorial.Tutorial;

public class IsOnlyAPIFeature {

	public static boolean eval(Tutorial fulltutorial, Tutorial tutorial, int currentSection)
	{
		String titleKey = tutorial.sectionElements.get(currentSection).getSection().getTitle() + tutorial.sectionElements.get(currentSection).getSection().getSubTitle();
		
		return fulltutorial.sectionElementsPerSection.get(titleKey).size() == 1;
	}
}
