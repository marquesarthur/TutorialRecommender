package features;

import java.io.FileWriter;
import java.io.IOException;

import tutorial.Tutorial;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.stats.ClassicCounter;
import features.sectionlevel.APIAsMethodFeature;
import features.sectionlevel.InCodeFeature;
import features.sectionlevel.InSubTitleFeature;
import features.sectionlevel.InTitleFeature;
import features.sectionlevel.IsOnlyAPIFeature;
import features.sectionlevel.MoreThanOnceFeature;
import features.sectionlevel.NumberOfOccuranceFeature;
import features.sectionlevel.NumberOfSectionsFeature;
import features.sentencelevel.DependencyBackOffFeature;
import features.sentencelevel.FirstWordFeature;
import features.sentencelevel.InCodeSentenceFeature;
import features.sentencelevel.InFirstSentenceFeature;
import features.sentencelevel.InImportantSentenceFeature;
import features.sentencelevel.InstructiveSentenceFeature;
import features.sentencelevel.IsExampleFeature;
import features.sentencelevel.IsInBracketsFeatures;
import features.sentencelevel.IsInEnumFeature;
import features.sentencelevel.ModalVerbFeature;
import features.sentencelevel.NegationFeature;
import features.sentencelevel.RelationStatFeature;
import features.tutoriallevel.NumberOfAPIElements;

public class FeatureFactory {

	public ClassicCounter<String> computeFeatures(Tutorial tutorial,
			 int currentSection, Tutorial fullTutorial, 
			 boolean numbericFeatures, boolean tutorialLevel, 
			 boolean sectionLevel, boolean sentenceLevel, 
			 boolean dependencies, boolean relations, boolean relationProb) throws IOException {

//		CSVWriter writer = new CSVWriter(new FileWriter("tutorials/features.csv", true));
		
		ClassicCounter<String> features = new ClassicCounter<String>();
	
		String incode = InCodeFeature.eval(tutorial, currentSection);
		
		boolean morethanonce = MoreThanOnceFeature.eval(tutorial, currentSection);
		boolean lessthanonce = !morethanonce;
		
		boolean intitle = !tutorial.sectionElements.get(currentSection).getSection().getTitle().equals(tutorial.sectionElements.get(currentSection).getSection().getSubTitle())
				&& InTitleFeature.eval(tutorial, currentSection);
		
		boolean inSubtitle = InSubTitleFeature.eval(tutorial, currentSection);
	
		boolean firstWord = FirstWordFeature.eval(tutorial, currentSection);
		
		boolean isExample = IsExampleFeature.eval(tutorial, currentSection);
		
		boolean isInBrackets = false;
		if(IsInBracketsFeatures.eval(tutorial, currentSection))
		{
			features.setCount("isInBrackets", 1);
			isInBrackets = true;
		}
		
		boolean isimportant = false;
		if (InImportantSentenceFeature.eval(tutorial, currentSection))
		{
			features.setCount("important", 1);
			isimportant = true;
		}

//		boolean isinstructive = false;
//		if (InstructiveSentenceFeature.eval(tutorial, currentSection))
//		{
//			features.setCount("instructive", 1);
//			isinstructive = true;
//		}
		
		boolean withCode = false;
		if (InCodeSentenceFeature.eval(tutorial, currentSection))
		{
			features.setCount("withCode", 1);
			withCode = true;
		}
		
//		not changing anything when first word is there		
		boolean isInFirstSent = InFirstSentenceFeature.eval(tutorial, currentSection);
		
		boolean hasModalVerb = ModalVerbFeature.eval(tutorial, currentSection);
		
		boolean hasNegation = NegationFeature.eval(tutorial, currentSection);
		
		boolean isEnum = IsInEnumFeature.eval(tutorial, currentSection);
		
		boolean isonlyone = IsOnlyAPIFeature.eval(fullTutorial, tutorial, currentSection);
		
		
		
		int num = NumberOfOccuranceFeature.eval(tutorial, currentSection);
		int allapi = NumberOfAPIElements.eval(tutorial, currentSection);
//		features.setCount("num", (double)num/(double)allapi);
		
		int sectionN = NumberOfSectionsFeature.eval(tutorial.sectionElements.get(currentSection), fullTutorial);
		double tfidf = (double)num * Math.log((double)tutorial.sectionElementsPerSection.size()/(double)sectionN);
		
		double wordnum = OccuranceAsWordFeature.eval(tutorial, currentSection);
		if (wordnum>5)
			wordnum = 5;
		wordnum /=5;
		
		
		// N of times clt occurs as method, normalized with N of times clt occurs
		int methodNum = APIAsMethodFeature.eval(tutorial, currentSection);
		
		
		boolean isIncode = false;
		boolean notincode = false;
		
		if (incode.equals("hascode&&incode"))
			isIncode = true;
		else
			notincode = true;
		
		if (tutorialLevel)
		{
			if(intitle)
				features.setCount("intitle", 1);
			
			if (isonlyone)
				features.setCount("isonlyone", 1);
			
			
		}
		
		if (sectionLevel)
		{
			if(!incode.equals("nocode"))
				features.setCount(incode, 1);
			
			if (morethanonce)
				features.setCount("more_than_once", 1);
			else
				features.setCount("once", 1);
			

			if (inSubtitle)
				features.setCount("inSubtitle", 1);
			

			if (isInFirstSent)
				features.setCount("firstSentence", 1);
			
			
		}
		
		if (sentenceLevel)
		{

			if (firstWord)
				features.setCount("firstWord", 1);
			

			if(isExample)
				features.setCount("isExample", 1);
			
			if (hasModalVerb)
				features.setCount("modal", 1);
			

			if (hasNegation)
				features.setCount("negation", 1);
			
			if(isEnum)
				features.setCount("isEnum", 1.0);
			
		}
		if (numbericFeatures)
		{
			features.setCount("tfidf", tfidf);
			
			features.setCount("wordnum", wordnum);
			
			features.setCount("methodNum", methodNum/num);	
		}
		
		if (dependencies)
		{
			double depWeight = DependencyBackOffFeature.eval(tutorial, currentSection);
			if (depWeight != 0)
				features.setCount("deps", depWeight);
			
		}
		
		if(relations)
		{
			features.setCount("fixedrel", RelationStatFeature.eval(tutorial, currentSection, relationProb));
		}
//		
//		writer.writeNext(new String[] {Double.toString((double)num/(double)allapi), Double.toString(wordnum), 
//									   Double.toString((double)num/(double)sectionN), Integer.toString(methodNum),
//										Boolean.toString(isIncode), Boolean.toString(notincode), 
//										Boolean.toString(morethanonce), Boolean.toString(lessthanonce), 
//										Boolean.toString(intitle), Boolean.toString(inSubtitle),
//										Boolean.toString(isInFirstSent), Boolean.toString(firstWord),
//										Boolean.toString(isExample),Boolean.toString(isInBrackets),
//										Boolean.toString(isimportant), Boolean.toString(isinstructive),
//										Boolean.toString(withCode), Boolean.toString(hasModalVerb),
//										Boolean.toString(isonlyone), Boolean.toString(hasNegation)});
//		writer.close();
//		
		return features;
	}
}
