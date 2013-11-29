package features.sentencelevel;

import java.util.List;

import TermExtraction.DependencyExtractor;

import tutorial.SectionElement;
import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class IsInEnumFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		return isInEnum(tutorial, currentSection);
	}
	
	// if api is in NP whose first level children in parse tree are only NN,NP and CC 
	// and number of nouns is more than 2 
	public static boolean isInEnum(Tutorial tutorial, int currentSection)
	{
		boolean result = false;
		
		
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(false);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		String formattedAPI = api.toLowerCase().replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "_");
		formattedAPI ="clt_" + formattedAPI.toLowerCase();
		
		for (CoreMap sent:sentences)
		{
			Tree tree = sent.get(TreeAnnotation.class);
			result = isInEnum(tree, formattedAPI);
			
			if (result)
				break;
		}
				
		return result;
	}
	
	public static boolean isInEnum(Tree tree, String api)
	{
		if (tree.depth()<2)
			return false;
		
		Tree[] children = tree.children();
		
		// if all children are either nouns or cc or punctuation
		int nnCount = 0;
		boolean containsAPI = false;
		boolean containsOther = false;
		boolean containsAnd = false;
		boolean containsOr = false;
		for (Tree child: children)
		{	
		   if (child.value().startsWith("N"))
		   {
			  nnCount++;
			  if (!containsAPI)
				  containsAPI = containsAPI(child, api);
		   }
		   else if (child.value().equals("CC"))
		   {
			   String ccValue = child.firstChild().toString().toLowerCase();
			   if(ccValue.equals("and"))
				   containsAnd = true;
			   if(ccValue.equals("or"))
				   containsOr = true;
		   }
		   else if (!child.value().equals(","))
		   {
			   containsOther = true;
			   break;
		   }
		}
		
		if (!containsOther && containsAPI && 
				((containsAnd && nnCount > 2) 
						||(containsOr && nnCount > 1)))
			return true;
		else
		{
			boolean isEnum = false;
			for (Tree child: children)
			{
				isEnum = isInEnum(child, api);
				if(isEnum)
					return true;
			}
		}
		
		return false;
	}

	private static boolean containsAPI(Tree child, String api) {
		return child.toString().toLowerCase().contains(api);
	}
	
	
	public static void testFeature(Tutorial tutorial)
	{
		for(int i=0; i<tutorial.sectionElements.size(); i++)
		{
			SectionElement secEl = tutorial.sectionElements.get(i);
			System.out.println(secEl.getSection().getSubTitle() + "-" + secEl.getApiElement().getAPIElementName()+
					": "+isInEnum(tutorial, i));
		}
	}
}

