package features.sectionlevel;

import tutorial.APIElement;
import tutorial.SectionElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;

public class NumberOfSectionsFeature {
	public static int eval(SectionElement sectionEl, Tutorial fulltutorial)
	{
		APIElement api = sectionEl.getApiElement();
		int sum = 0;
		
		for (SectionElement sectionElPair:fulltutorial.sectionElements)
			if (api.getFqn().equals(sectionElPair.getApiElement().getFqn()))
			{
				sum++;
			}
		
		return sum;
	}
}
