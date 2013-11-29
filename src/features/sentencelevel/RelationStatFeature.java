package features.sentencelevel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import tutorial.Tutorial;
import TermExtraction.DependencyExtractor;
import Utilities.RelationStat;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class RelationStatFeature {

	public static RelationStat relStat = null;
	public static double eval(Tutorial tutorial, int currentSection, boolean relationProb)
	{ 
		if (relStat == null)
		{
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("settings"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filepath = properties.getProperty("rel_stat");
			relStat = new RelationStat();
			relStat.loadFromFile(filepath);
		}

		double relWeight = 0;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		api = api.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();

		int dcount = 0;
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

					if (relationProb)
					{
						double prcPositive = relStat.getPrcPositive(relation);
						double prcNegative = relStat.getPrcNegative(relation);

						
						if (prcNegative + prcPositive > 0)
						{
							if (prcPositive > prcNegative)
								relWeight += prcPositive;
							else
								relWeight -= prcNegative;
							
						}
						
						dcount++;
						
					}
					else
					{
						double zscore = relStat.getZscore(relation);
						double learntZscore = tutorial.tutRelStat.getZscore(relation);
						
						relWeight+=(tutorial.beta * zscore + (1-tutorial.beta) * learntZscore);
						relWeight+=zscore;
						dcount++;
					}
				}
			}
		}

		if (dcount > 0)
			return relWeight/Math.sqrt(dcount);
		else
			return 0;
	}

}
