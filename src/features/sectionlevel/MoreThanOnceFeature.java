package features.sectionlevel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tutorial.Tutorial;
import tutorial.TutorialSection;

public class MoreThanOnceFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{
		return NumberOfOccuranceFeature.eval(tutorial, currentSection) > 1;
		
	}
}
