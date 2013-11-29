package features.sentencelevel;

import java.util.List;

import tutorial.Tutorial;
import TermExtraction.DependencyExtractor;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class NegationFeature {

	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		api = api.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();

		boolean neg = isNegated(sentences, api);
		
		if (!neg)
			neg = containsNetagtivePattern(sentences, api);
		return neg;
	}
	
	public static boolean isNegated(List<CoreMap> sentences, String api) 
	{
		boolean hasMD = false;
		
		for (CoreMap sent:sentences)
		{
			SemanticGraph dependencies = sent.get(CollapsedCCProcessedDependenciesAnnotation.class);

			for (SemanticGraphEdge edge:dependencies.edgeIterable())
			{
				String relation = String.valueOf(edge.getRelation()).toLowerCase();

				String dependent = edge.getDependent().get(TextAnnotation.class).toLowerCase();
				String governor = edge.getGovernor().get(TextAnnotation.class).toLowerCase();

				if (dependent.startsWith("clt_" + api) || governor.startsWith("clt_" + api))
				{

					if (edge.getGovernor().get(PartOfSpeechAnnotation.class).startsWith("VB"))
						relation+=DependencyExtractor.getDepenencyExtensions(dependencies, edge.getGovernor().get(TextAnnotation.class));

					if (edge.getDependent().get(PartOfSpeechAnnotation.class).startsWith("VB"))
						relation+=DependencyExtractor.getDepenencyExtensions(dependencies, edge.getDependent().get(TextAnnotation.class));
					
					relation = relation.toLowerCase();
					if (relation.contains("_neg"))
						hasMD = true;
				}
			}
		}
		return hasMD;
	}
	
	public static boolean containsNetagtivePattern(List<CoreMap> sentences, String api)
	{
		String[] pattern1  = {"instead", "of"};
		String[] pattern2 = {"rather", "than"};
		
		for (CoreMap sent:sentences)
		{
			List<CoreLabel> tokens = sent.get(TokensAnnotation.class);
			for(int i = 0; i < tokens.size()-2; i++)
			{
				String word = tokens.get(i).get(TextAnnotation.class);
				String nextword = tokens.get(i+1).get(TextAnnotation.class);
				String secondword = tokens.get(i+2).get(TextAnnotation.class);
				String thirdword = (i < tokens.size()-3)?tokens.get(i+3).get(TextAnnotation.class):"";
				
				if ((word.equals(pattern1[0]) &&  nextword.equals(pattern1[1])) ||
						(word.equals(pattern2[0]) &&  nextword.equals(pattern2[1])) &&
						(secondword.equals(api) || thirdword.equals(api)))
						return true;
			}
		}
		
		return false;
	}
}
