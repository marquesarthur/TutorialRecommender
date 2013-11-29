package features.tutoriallevel;

import java.util.List;

import features.sectionlevel.NumberOfOccuranceFeature;

import tutorial.SectionElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;

public class NumberOfAPIElements {
	public static int eval(Tutorial tutorial, int currentSection)
	{
		TutorialSection section = tutorial.sectionElements.get(currentSection).getSection();
		int sum = 0;
		
		for (SectionElement sectionElPair:tutorial.sectionElements)
			if (section.getSubTitle().equals(sectionElPair.getSection().getSubTitle()))
			{
				sum += NumberOfOccuranceFeature.eval(tutorial, currentSection);
			}
		
		return sum;
	}
}
