import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import Utilities.Utils;

import similarity.XMLDocAPI;
import tutorial.SectionElement;
import tutorial.Tutorial;
import features.WordPurityFeatures;

public class TutorialStatistics {

	public static void main(String[] args) throws IOException {
		String[] tutorials = {
				"tutorials/JodaTime/jodatime.csv"
		, "tutorials/math/math_annotation_disagreements.csv"
		, 
		"tutorials/smack/smack_tutorial.csv",
		"tutorials/jcol/jcol_tutorial.csv",
		"tutorials/col/col_tutorial.csv"
		};
		
		boolean[] types = {true, false, false, false, false};
		
		for(int i = 0; i< 5; i++)
			printStat(tutorials[i], types[i]);
//				printAPIStat();

//		printAVGWordCount();
//		sectionLengths();
	}

	public static void sectionLengths()
	{
		String filepath = "tutorials/JodaTime/jodatime.csv";
		boolean type = true;

		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial(filepath, true, type);

		for(String title:tutorial.sectionElementsPerSection.keySet())
		{
			System.out.println(Utils.getWordCount(tutorial.sectionElementsPerSection.get(title).get(0).getSection().getText()));
		}

	}



public static void printAVGWordCount()
{
	String[] tutorials = {
//			"tutorials/JodaTime/jodatime.csv"
//			, "tutorials/math/math_annotation_disagreements.csv", 
//			"tutorials/smack/smack_tutorial.csv",
//			"tutorials/jcol/jcol_tutorial.csv",
			"tutorials/col/col_tutorial.csv"
	};

	boolean[] types = {
//			true, false, false, false, 
			false};

	for(int i  = 0; i< tutorials.length; i++)
	{
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial(tutorials[i], true, types[i]);

		float wordcount = 0;
		for(String title:tutorial.sectionElementsPerSection.keySet())
		{
			wordcount += Utils.getWordCount(tutorial.sectionElementsPerSection.get(title).get(0).getSection().getText());
		}

		wordcount /=tutorial.sectionElementsPerSection.size();

		System.out.println(tutorials[i] + " average size is " + wordcount);
	}

}
public static void printAPIStat()
{
	XMLDocAPI xmldoc = new XMLDocAPI("javadocs/jodatime.xml");

	int count = xmldoc.numberOfClasses();
	System.out.println(count);

	printStat("tutorials/jcol/jcol_tutorial.csv", false);
}

public static void printStat(String tutPath, boolean type) {
	Tutorial tutorial = new Tutorial();

	tutorial.LoadTutorial(tutPath, false, type);

	printNumberOfSectionElPairs(tutorial);
	printNumberOfRelSectionElPairs(tutorial);

	printNumberOfUniqueAPIElements(tutorial);
	printNumberofRelUniqueAPIElements(tutorial);

	printNumberOfAPIElementsPerSection(tutorial);

	printNumberOfAPIElementsPerElement(tutorial);

	//		printNumberOfUniqueWordsPerElement(tutorial);
}

public static void printNumberOfSectionElPairs(Tutorial tutorial) {
	System.out.println("number of Section-APIElement pairs is "
			+ tutorial.sectionElements.size());
}

public static void printNumberOfRelSectionElPairs(Tutorial tutorial) {
	int count = 0;
	for (SectionElement pair : tutorial.sectionElements) {
		if (pair.getLabel())
			count++;
	}

	System.out.println("number of relevant pairs is " + count);
}

public static void printNumberOfUniqueAPIElements(Tutorial tutorial) {
	System.out.println("number of unique API Elements is "
			+ tutorial.sectionElementsPerElement.size());
}

public static void printNumberofRelUniqueAPIElements(Tutorial tutorial) {
	int count = 0;
	for (String apiElement : tutorial.sectionElementsPerElement.keySet())
		for (SectionElement pair : tutorial.sectionElementsPerElement
				.get(apiElement))
			if (pair.getLabel()) {
				count++;
				break;
			}

	System.out
	.println("number of unique relevant API Elements is " + count);
}

public static void printNumberOfAPIElementsPerSection(Tutorial tutorial) {
	System.out.println("-------------------------------------------");
	System.out
	.println("number of API Elements per Section in relevant/total");
	System.out.println("-------------------------------------------");
	int count = 0;
	for (String sectionTitle : tutorial.sectionElementsPerSection.keySet()) {
		count = 0;
		for (SectionElement pair : tutorial.sectionElementsPerSection
				.get(sectionTitle))
			if (pair.getLabel())
				count++;
		System.out.println(tutorial.sectionElementsPerSection.get(sectionTitle).get(0).getSection().getTitle()
				+ ","
				+ tutorial.sectionElementsPerSection.get(sectionTitle).get(0).getSection().getSubTitle()
				+ ", "
				+ count
				+ ","
				+ tutorial.sectionElementsPerSection.get(sectionTitle)
				.size());
	}

	System.out.println();
}

public static void printNumberOfAPIElementsPerElement(Tutorial tutorial) {
	System.out.println("-------------------------------------------");
	System.out
	.println("number of unique API Elements per Section in relevant/total");
	System.out.println("-------------------------------------------");
	int count = 0;
	for (String apiElement : tutorial.sectionElementsPerElement.keySet()) {
		count = 0;
		for (SectionElement pair : tutorial.sectionElementsPerElement
				.get(apiElement))
			if (pair.getLabel())
				count++;
		System.out
		.println(apiElement
				+ ","
				+ count
				+ ","
				+ tutorial.sectionElementsPerElement
				.get(apiElement).size());
	}

	System.out.println();
}

public static void printNumberOfUniqueWordsPerElement(Tutorial tutorial) {
	WordPurityFeatures.calcWordPurity(tutorial);

	System.out.println("-------------------------------------------");
	System.out
	.println("number of wordes met once/total number of unique words per relevant API Elements");
	System.out.println("-------------------------------------------");

	for (String apielement : tutorial.sectionElementsPerElement.keySet()) {
		HashMap<String, Double> mergedWords = new HashMap<String, Double>();

		for (SectionElement pair : tutorial.sectionElementsPerElement
				.get(apielement)) {
			if (pair.getLabel()) {
				mergedWords = mergeHashMaps(mergedWords,
						WordPurityFeatures.wordPurityPerElement.get(pair
								.getApiElement().getAPIElementName()));
			}
		}
		int count = 0;
		for (String word : mergedWords.keySet())
			if (WordPurityFeatures.wordsPerTutorial.get(word) == 1.0)
				count++;

		System.out.println(apielement + ": " + count + "/"
				+ mergedWords.size());
	}

}

private static HashMap<String, Double> mergeHashMaps(
		HashMap<String, Double> hm1, TreeMap<String, Double> hm2) {
	HashMap<String, Double> mergedHM = new HashMap<String, Double>();
	mergedHM.putAll(hm2);
	for (String key : mergedHM.keySet()) {
		Double value = hm1.get(key);

		if (value == null)
			value = 0.0;

		Double mergedvalue = mergedHM.get(key) + value;

		mergedHM.put(key, mergedvalue);
	}

	return mergedHM;
}
}
