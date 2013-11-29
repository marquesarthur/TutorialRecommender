package features.sectionlevel;

import java.util.ArrayList;
import java.util.List;

import features.IsInTitleAsWordFeaure;

import Utilities.Utils;

import tutorial.APIElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;

public class InTitleFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{
		TutorialSection section = tutorial.sectionElements.get(currentSection).getSection();
		APIElement apielement = tutorial.sectionElements.get(currentSection).getApiElement();
		String api = apielement.getAPIElementName();
		List<String> titleWords = Utils.tokenizeTitle(section.getTitle());
		
		return isInTitle(titleWords, api) || IsInTitleAsWordFeaure.eval(titleWords, api);
	}
	
	public static boolean isInTitle(List<String> tokensizedTitle, String apiname)
	{
		if(apiname.contains("."))
		{
			String[] parts = apiname.split("\\.");
			for(int i = parts.length-1; i>=0; i--)
				if(!parts[i].contains("("))
				{
//					System.out.println("InTitleFeature: " + apiname + "=>" +parts[i]);
					apiname = parts[i];
					break;
				}
		}
		
		return tokensizedTitle.contains(apiname);
	}
}
