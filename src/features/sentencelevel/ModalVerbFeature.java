package features.sentencelevel;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import TermExtraction.CLTDependency;
import TermExtraction.DependencyExtractor;
import tutorial.Tutorial;

public class ModalVerbFeature {
	public static boolean eval(Tutorial tutorial, int currentSection)
	{ 
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		api = api.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();

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
					
					if (relation.contains("_MD") || relation.contains("_TO"))
						hasMD = true;
				}
			}
		}
		
		return hasMD;
	}
}
