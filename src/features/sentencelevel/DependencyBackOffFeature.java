package features.sentencelevel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import TermExtraction.DependencyExtractor;
import Utilities.DependencyStat;

import tutorial.Tutorial;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class DependencyBackOffFeature {

	public static DependencyStat depStat = null;
	public static double eval(Tutorial tutorial, int currentSection)
	{ 
		if (depStat == null)
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
			String filepath = properties.getProperty("dep_stat");
			depStat = new DependencyStat();
			depStat.loadZScoreFromFile(filepath);
		}

		double depWeight = 0;
		List<CoreMap> sentences = tutorial.sectionElements.get(currentSection).getAPIElementSentences(true);
		String api = tutorial.sectionElements.get(currentSection).getApiElement().getAPIElementName();
		api = api.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\.", "-").toLowerCase();

		int dcount = 0;
		for (CoreMap sent:sentences)
		{
			SemanticGraph dependencies = sent.get(CollapsedCCProcessedDependenciesAnnotation.class);

			for (SemanticGraphEdge edge:dependencies.edgeIterable())
			{
				String dependent = edge.getDependent().get(TextAnnotation.class).toLowerCase();
				String governor = edge.getGovernor().get(TextAnnotation.class).toLowerCase();
				String relation = String.valueOf(edge.getRelation()).toLowerCase();
				
				if (edge.getGovernor().get(PartOfSpeechAnnotation.class).startsWith("VB"))
					relation+=DependencyExtractor.getDepenencyExtensions(dependencies, edge.getGovernor().get(TextAnnotation.class));
				
				if (edge.getDependent().get(PartOfSpeechAnnotation.class).startsWith("VB"))
					relation+=DependencyExtractor.getDepenencyExtensions(dependencies, edge.getDependent().get(TextAnnotation.class));
				
				if (dependent.startsWith("clt_" + api) || governor.startsWith("clt_" + api))
				{
					if (dependent.startsWith("clt_" + api))
					{
						dependent = "clt";
						if (! governor.startsWith("clt"))
							governor = edge.getGovernor().get(LemmaAnnotation.class).toLowerCase();
						else
							governor = "clt";
					}
					
					if (governor.startsWith("clt_" + api))
					{
						governor = "clt";
						if (! dependent.startsWith("clt"))
							dependent = edge.getDependent().get(LemmaAnnotation.class).toLowerCase();
						else 
							dependent = "clt";
					}
						
					
//					double pprob = depStat.getPcprob(governor, relation, dependent);
//					double nprob = depStat.getNcprob(governor, relation, dependent);
//					System.out.println(governor + "-" + relation + "-" + dependent + "-" + pprob + "-" + nprob);
//					if (pprob > nprob)
//						depWeight += pprob;
//					else
//						depWeight -= nprob;
					
					double zscore = depStat.getZscore(governor, relation, dependent);
					double learntZScore = tutorial.tutDepStat.getZscore(governor, relation, dependent);
					
//					System.out.println(governor + "-" + relation + "-" + dependent + "-" + zscore);
					
					depWeight += (tutorial.alpha * zscore  + (1- tutorial.alpha) * learntZScore);
//					depWeight += zscore ;
					dcount++;
				}
			}
		}
		
		if (dcount > 0)
			return depWeight/Math.sqrt(dcount);
		else
			return 0;
	}

}
