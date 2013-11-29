package features.sectionlevel;

import java.util.List;

import tutorial.Tutorial;
import tutorial.TutorialSection;

public class InCodeFeature {
	public static String eval(Tutorial tutorial, int currentSection)
	{
		boolean incode = tutorial.sectionElements.get(currentSection).isInCode();
		boolean hascode = tutorial.sectionElements.get(currentSection).hasCode();
		
		if(hascode)
			if (incode)
				return "hascode&&incode";
			else
				return "hascode&&notincode";
		else
			return "nocode";
	}		
}
