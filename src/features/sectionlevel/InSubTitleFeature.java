package features.sectionlevel;

import java.util.List;

import features.IsInTitleAsWordFeaure;

import Utilities.Utils;

import tutorial.APIElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;

public class InSubTitleFeature {
		public static boolean eval(Tutorial tutorial, int currentSection)
		{
			TutorialSection section = tutorial.sectionElements.get(currentSection).getSection();
			APIElement apielement = tutorial.sectionElements.get(currentSection).getApiElement();
		
			String api = apielement.getAPIElementName();
			List<String> titleWords = Utils.tokenizeTitle(section.getSubTitle());
			
			return InTitleFeature.isInTitle(titleWords, api) || IsInTitleAsWordFeaure.eval(titleWords, api);
		}
	
}
