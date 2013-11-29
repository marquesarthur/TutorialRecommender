package features.sentencelevel;

import java.util.List;

import TermExtraction.DependencyExtractor;

import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class IsExampleFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		String formattedAPI = api.toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "_");
		boolean isExample = false;
		for (CoreMap sent:sentences)
		{
//			// if contains any of enumerated keywords than current APIElement is mentioned as an example
			if (sent.toString().toLowerCase().matches(".*(such as|for example|for instance).*"+formattedAPI+"([^A-Za-z].*)?"))
				isExample = true;
			
		}

		return isExample;
	}
	
	
	
	
	public static boolean containsExamplePhrase(Tree parent, String formattedAPI)
	{
		Tree[] children = parent.children();
		
		for (Tree child: children)
		{
			if (child.depth()>2)
			{
			   if (child.value().equals("PP"))
			   {	
				   String phrase = "";
				   List<Tree> leaves = child.getLeaves();
				   for (Tree leaf : leaves) 
			        	  phrase +=leaf.value() + " ";
				   
				   if (phrase.toLowerCase().matches(".*(such as|for example|for instance).*"+formattedAPI+"([^A-Za-z].*)?"))
					   return true;
			   }		   
			   else if (containsExamplePhrase(child, formattedAPI))
					   return true;
			}
		}
		
		return false;

	}
}
