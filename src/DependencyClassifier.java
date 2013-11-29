import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tutorial.Tutorial;
import TermExtraction.CLTDependency;
import TermExtraction.DependencyExtractor;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class DependencyClassifier {

	public static void trainTutorialStat(Tutorial tut) {

		List<CLTDependency> alldeps = extractDeps(tut);

		trainDepStat(tut, alldeps);
		trainRelStat(tut, alldeps);
	}

	public static void trainDepStat(Tutorial tut, List<CLTDependency> alldeps) {

		for (CLTDependency dep : alldeps) {
			if (dep.isPositive()) {
				tut.tutDepStat.incrementNpositive(dep);
			}

			tut.tutDepStat.incrementNobs(dep);
		}

		tut.tutDepStat.calcZScores();

	}

	public static void trainRelStat(Tutorial tut, List<CLTDependency> alldeps) {
		for (CLTDependency dep : alldeps) {
			if (dep.isPositive()) {
				tut.tutRelStat.incrementNpositive(dep.getRel());
			}

			tut.tutRelStat.incrementNobs(dep.getRel());
		}

		tut.tutRelStat.calcZScores();
	}

	public static List<CLTDependency> extractDeps(Tutorial tut) {
		List<CLTDependency> alldeps = new ArrayList<CLTDependency>();

		for (int i = 0; i < tut.sectionElements.size(); i++) {
			boolean isRelevant = tut.sectionElements.get(i).getLabel();
			String api = tut.sectionElements.get(i).getApiElement()
					.getAPIElementName();

			List<CoreMap> sentences = tut.sectionElements.get(i)
					.getAPIElementSentences(true);
			api = api.replaceAll("\\(", "").replaceAll("\\)", "")
					.replaceAll("\\.", "-").toLowerCase();

			for (CoreMap sent : sentences) {
				SemanticGraph dependencies = sent
						.get(CollapsedCCProcessedDependenciesAnnotation.class);
				List<CLTDependency> apiDeps = DependencyExtractor
						.getAPIDependencies(dependencies, api);
				for (CLTDependency dep : apiDeps) {
					if (isRelevant)
						dep.setPositive(true);
					else
						dep.setNegative(true);

					alldeps.add(dep);
				}
			}
		}

		return alldeps;
	}

	public static void save(List<CLTDependency> dependencies, String filename) {
		CSVWriter writer;
		try {

			writer = new CSVWriter(new FileWriter(filename), ',');
			for (CLTDependency dep : dependencies) {
				writer.writeNext(new String[] { dep.getGovernor(),
						dep.getRel(), dep.getDependant(),
						Boolean.toString(dep.isUseless()),
						Boolean.toString(dep.isPositive()),
						Boolean.toString(dep.isNegative()),
						Boolean.toString(dep.isPnDecide()),
						Boolean.toString(dep.isFunctional()),
						Boolean.toString(dep.isStructural()),
						Boolean.toString(dep.isDiscriptive()),
						Boolean.toString(dep.isCommand()),
						Boolean.toString(dep.isInterCLT()),
						Boolean.toString(dep.isOther()),
						Boolean.toString(dep.isTypeDecide()) });
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
