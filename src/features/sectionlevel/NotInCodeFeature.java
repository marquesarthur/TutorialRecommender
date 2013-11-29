package features.sectionlevel;

import java.util.List;

import tutorial.Tutorial;
import tutorial.TutorialSection;

public class NotInCodeFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{
		return tutorial.sectionElements.get(currentSection).hasCode() && 
				!tutorial.sectionElements.get(currentSection).isInCode();
	}
}
