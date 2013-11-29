package features.sentencelevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class RelTypeFeature {

	public static Set<String> eval(Tutorial tutorial, int currentSection)
	{ 
		Set<String> deps = new HashSet<String>();

		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		api = api.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();

		for (CoreMap sent:sentences)
		{
			SemanticGraph dependencies = sent.get(CollapsedCCProcessedDependenciesAnnotation.class);

			for (SemanticGraphEdge edge:dependencies.edgeIterable())
			{
				String dependent = edge.getDependent().get(TextAnnotation.class).toLowerCase();
				String governor = edge.getGovernor().get(TextAnnotation.class).toLowerCase();

				if (!String.valueOf(edge.getRelation()).equals("nn") && 
						!String.valueOf(edge.getRelation()).equals("det") && 
						!String.valueOf(edge.getRelation()).equals("dep"))
				{
					if (dependent.startsWith("clt_" + api))
					{	

						//						if (String.valueOf(edge.getRelation()).contains("prep"))
						//							deps.add("prep");
						//						else 
//						if (String.valueOf(edge.getRelation()).contains("conj"))
//							deps.add(governor+",conj");
//						else if (String.valueOf(edge.getRelation()).contains("subj"))
//							deps.add(governor+",subj");
//						else if (String.valueOf(edge.getRelation()).contains("obj"))
//							deps.add(governor+",obj");
//						else
							deps.add(governor + "," + String.valueOf(edge.getRelation()));

					//	System.out.println(api + " - " + String.valueOf(edge.getRelation()));
					}

					else if ( governor.startsWith("clt_" + api))
					{
//						if (String.valueOf(edge.getRelation()).contains("conj"))
//							deps.add("conj," + dependent);
//						else if (String.valueOf(edge.getRelation()).contains("subj"))
//							deps.add("subj," + dependent);
//						else if (String.valueOf(edge.getRelation()).contains("obj"))
//							deps.add("obj," + dependent);
//						else
							deps.add(String.valueOf(edge.getRelation())+ "," + dependent);
					}
				}
			}
		}


		return deps;
	}

}
