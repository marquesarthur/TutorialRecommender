package TermExtraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tutorial.Tutorial;
import Utilities.IncrementalSet;
import Utilities.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class DependencyExtractor {

	public static void main(String[] args) {
		try {
			String filetoProcess = "tutorials/JavaTutorials/filtered/filteredSents.txt";
			String collapsedFile = "tutorials/JavaTutorials/filtered/collapsed.txt";
			//printNeighborWords(filetoProcess);

			//			collapsNounPhrases(filetoProcess, collapsedFile);			
			extractingAllDependencies(filetoProcess);

			//			extractingTutorialDependencies();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	public static List<CLTDependency> getAPIDependencies(SemanticGraph dependencies, String api)
	{
		List<CLTDependency> apiDeps = new ArrayList<CLTDependency>();

		for (SemanticGraphEdge edge:dependencies.edgeIterable())
		{
			String relation = String.valueOf(edge.getRelation()).toLowerCase();

			if (!relation.equals("pobj") && !relation.equals("dep") && !relation.equals("nn")
					&& !relation.equals("amod") && !relation.equals("appos"))
			{
				String dependent = edge.getDependent().get(TextAnnotation.class).toLowerCase();
				String governor = edge.getGovernor().get(TextAnnotation.class).toLowerCase();

				if (edge.getGovernor().get(PartOfSpeechAnnotation.class).startsWith("VB"))
					relation+=DependencyExtractor.getDepenencyExtensions(dependencies, governor);

				if (edge.getDependent().get(PartOfSpeechAnnotation.class).startsWith("VB"))
					relation+=DependencyExtractor.getDepenencyExtensions(dependencies, dependent);

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

					CLTDependency dep = new CLTDependency(governor.toLowerCase(), 
							dependent.toLowerCase(), 
							relation);	
					apiDeps.add(dep);
				}
			}
		}

		return apiDeps;
	}

	public static void matchDependencies()
	{
		//		List<CoreMap> testsentences= Utils.getSentences("class is a subtype of cltnoun-interface.");
		//		for (CoreMap sentence:testsentences)
		//		{
		//			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		//			System.out.println(dependencies.toString());
		//			
		//			Set<SemanticGraphEdge> edges = dependencies.getEdgeSet();
		//			
		//			
		//			for (SemanticGraphEdge edge:edges)
		//			{
		//				String dependent = edge.getDependent().get(TextAnnotation.class);
		//				String governor = edge.getGovernor().get(TextAnnotation.class);
		//				String rel = String.valueOf(edge.getRelation());
		//				
		//				if (dependent.matches("cltnoun.*") || governor.matches("cltnoun.*"))
		//				{
		//					
		//					for (SemanticGraphEdge filterededge:filtered.edgeIterable())
		//					{
		//						
		//						String filtereddependent = filterededge.getDependent().get(TextAnnotation.class);
		//						String filteredgovernor = filterededge.getGovernor().get(TextAnnotation.class);
		//						String filteredrel = String.valueOf(filterededge.getRelation());
		//						
		//						if (((dependent.startsWith("cltnoun") && filtereddependent.startsWith("cltnoun")) ||
		//								filtereddependent.equals(dependent)) && ((governor.startsWith("cltnoun") && filteredgovernor.startsWith("cltnoun")) ||
		//										filteredgovernor.equals(governor))  
		//								&& filteredrel.equals(rel))
		//						{
		//							System.out.println(governor + " --" + rel + "-> " + dependent);
		//						}
		//					}
		//				}
		//			}
		//		}
	}

	public static void extractingTutorialDependencies() throws IOException
	{	

		System.out.println((new Date()).toString());

		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true); 
		String tutorialText = tutorial.getAnnotatedText();
		Utils.saveFile(tutorialText, "dumps/annotatedJT.txt");

		IncrementalSet<CLTDependency> dependencySet = new IncrementalSet<CLTDependency>();		
		String sentdeps = "";


		tutorialText = collapsNounPhrases(tutorialText);
		List<CoreMap> sentences= Utils.getSentences(tutorialText, true);

		for (CoreMap sentence:sentences)
		{

			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			for (SemanticGraphEdge edge:dependencies.edgeIterable())
			{
				if (edge.getDependent().get(TextAnnotation.class).contains("CLT") ||
						edge.getGovernor().get(TextAnnotation.class).contains("CLT"))
				{
					CLTDependency dep = new CLTDependency(edge.getGovernor().get(TextAnnotation.class).toLowerCase(), 
							edge.getDependent().get(TextAnnotation.class).toLowerCase(), 
							String.valueOf(edge.getRelation()));	
					sentdeps += edge.getGovernor().get(TextAnnotation.class).toLowerCase() + "-" +
							String.valueOf(edge.getRelation()) + "-" +
							edge.getDependent().get(TextAnnotation.class).toLowerCase() + ", ";
					dependencySet.put(dep);
				}
			}
			sentdeps +="\n";

		}

		dependencySet.dump("dependency_JT.csv", false);		
		Utils.saveFile(sentdeps, "sentdeps_JT.txt");

		System.out.println((new Date()).toString());
	}
	public static String getDepenencyExtensions(SemanticGraph dependencies, String verb)
	{
		String extensions = ""; 
		for (SemanticGraphEdge edge:dependencies.edgeIterable())
		{
			if (edge.getGovernor().get(TextAnnotation.class).equals(verb) )
			{
				if (String.valueOf(edge.getRelation()).equals("aux"))
				{
					extensions += "_" + edge.getDependent().get(PartOfSpeechAnnotation.class);
				}

				else if (String.valueOf(edge.getRelation()).equals("cop"))
					extensions += "_" + edge.getDependent().get(TextAnnotation.class);
				else if (String.valueOf(edge.getRelation()).equals("neg"))
					extensions += "_neg";
			}
		}

		return extensions;
	}

	public static void extractingAllDependencies(String filetoProcess) throws IOException
	{	
		System.out.println((new Date()).toString());
		String text = Utils.readFileAsString(filetoProcess);
		text = collapsNounPhrases(text);

		List<CoreMap> sentences= Utils.getSentences(text, true);

		IncrementalSet<CLTDependency> dependencySet = new IncrementalSet<CLTDependency>();
		HashMap<CLTDependency, List<String>> depMap = new HashMap<CLTDependency, List<String>>();

		String sentdeps = "";
		for (CoreMap sentence:sentences)
		{

			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);

			for (SemanticGraphEdge edge:dependencies.edgeIterable())
			{
				String governor = edge.getGovernor().get(TextAnnotation.class).toString();
				String dependent = edge.getDependent().get(TextAnnotation.class).toString();
				if (!governor.startsWith("clt"))
					governor = edge.getGovernor().get(LemmaAnnotation.class).toLowerCase();

				if (!dependent.startsWith("clt"))
					dependent = edge.getDependent().get(LemmaAnnotation.class).toLowerCase();

				if (dependent.startsWith("clt") )
				{
					String rel = String.valueOf(edge.getRelation());
					if (edge.getGovernor().get(PartOfSpeechAnnotation.class).startsWith("VB"))
						rel+=getDepenencyExtensions(dependencies, edge.getGovernor().get(TextAnnotation.class));

					CLTDependency dep = new CLTDependency(governor, "clt", rel);

					List<String> sents = depMap.get(dep);
					if (sents == null)
					{
						sents = new ArrayList<String>();
					}
					sents.add(sentence.toString().replaceAll(dependent.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}"), "clt"));
					depMap.put(dep, sents);

				}

				if (governor.startsWith("clt"))
				{
					String rel = String.valueOf(edge.getRelation());
					if (edge.getDependent().get(PartOfSpeechAnnotation.class).startsWith("VB"))
						rel+=getDepenencyExtensions(dependencies, edge.getDependent().get(TextAnnotation.class));

					CLTDependency dep = new CLTDependency("clt", dependent, rel);

					List<String> sents = depMap.get(dep);
					if (sents == null)
					{
						sents = new ArrayList<String>();

					}
					sents.add(sentence.toString().replaceAll(governor, "clt"));
					depMap.put(dep, sents);
				}
			}

		}

		for (CLTDependency dep:depMap.keySet())
		{
			List<String> sents = depMap.get(dep);
			if (sents.size()>1)
			{
				for (String sent:sents)
					sentdeps+=dep.getGovernor() + "," + 
							dep.getRel() + "," +
							dep.getDependant() + ",\\\"" + sent + "\\\"\n";
			}
		}

		//dependencySet.dump("dependency.csv", false);
		Utils.saveFile(sentdeps, "new-sentdeps.txt");
		System.out.println((new Date()).toString());
	}

	public static void printNeighborWords(String filetoProcess) throws IOException
	{
		IncrementalSet prevWords = new IncrementalSet();
		IncrementalSet nextWords = new IncrementalSet();

		String text = Utils.readFileAsString(filetoProcess);
		text = Utils.groupConceptWords(text);
		List<CoreMap> sentences= Utils.getSentences(text, true);

		for(CoreMap sent:sentences)
		{
			List<CoreLabel> tokens = sent.get(TokensAnnotation.class);

			for(int i = 0; i< tokens.size(); i++)
			{

				String word = tokens.get(i).get(TextAnnotation.class);
				String prevWord = "";
				String nextWord = "";
				String nextWord2 = "";
				if(word.endsWith("cltnoun"))
				{	
					if (i>0)
					{
						prevWord = tokens.get(i-1).get(TextAnnotation.class);
						if (prevWord.matches("[A-Za-z]+") && !Utils.isStopWord(prevWord) 
								&& tokens.get(i-1).get(PartOfSpeechAnnotation.class).toLowerCase().startsWith("nn"))
							prevWords.put(prevWord);
						else
							prevWord = "";
					}
					if (i<tokens.size()-1)
					{
						nextWord = tokens.get(i+1).get(TextAnnotation.class);
						if (nextWord.matches("[A-Za-z]+") && !Utils.isStopWord(nextWord)
								&& tokens.get(i+1).get(PartOfSpeechAnnotation.class).toLowerCase().startsWith("nn"))
							nextWords.put(nextWord);
						else
							nextWord = "";
					}

					if (i<tokens.size()-2)
					{
						nextWord2 = tokens.get(i+2).get(TextAnnotation.class);
						if (nextWord2.matches("[A-Za-z]+") && !Utils.isStopWord(nextWord2)
								&& tokens.get(i+2).get(PartOfSpeechAnnotation.class).toLowerCase().startsWith("nn"))
							nextWords.put(nextWord);
						else
							nextWord2 = "";
					}

					if (nextWord != "" || prevWord != "")
						System.out.println(prevWord + "," + word + "," + nextWord + "," + nextWord2);
				}
			}
		}

		prevWords.dump("prevwords.csv", false);
		nextWords.dump("nextwords.csv", false);
	}

	public static void collapsNounPhrases(String fileToProcess, String outputfile) throws IOException
	{	
		String text = Utils.readFileAsString(fileToProcess);
		Utils.saveFile(collapsNounPhrases(text), outputfile);
	}

	public static String collapsNounPhrases(String text)
	{
		StringBuilder newfile = new StringBuilder();
		text = Utils.groupConceptWords(text);
		List<CoreMap> sentences= Utils.getSentences(text, true);

		for (CoreMap sentence:sentences)
		{
			Tree tree = sentence.get(TreeAnnotation.class);
			String newsentence = collapsNounPhrases(tree);
			newfile.append(newsentence + "\n");			
		}

		return newfile.toString().replaceAll("-LRB-", "(").replaceAll("-RRB-", ")");
	}

	public static String collapsNounPhrases(Tree parent)
	{
		Tree[] children = parent.children();

		String newSentence = "";
		for (Tree child: children){
			if (child.isLeaf())
				newSentence += child.value() + " ";
			else if (child.value().equals("NP"))
			{			
				if (child.depth()<=3)
				{
					List<Tree> leaves = child.getLeaves(); //leaves correspond to the tokens
					String nounphrase = "";
					int cltnumber = 0;
					String clt = "";
					for (Tree leaf : leaves) 
					{  
						if(leaf.value().toLowerCase().startsWith("clt"))
						{
							cltnumber++;
							clt = leaf.value();
						}
						nounphrase +=leaf.value() + " ";
					}

					if (cltnumber == 1)
					{
						// System.out.println(nounphrase + " - " + clt);
						nounphrase = clt + " ";
					}

					newSentence += nounphrase;
				}
				else
					newSentence +=collapsNounPhrases(child) + " ";
			}		   
			else
			{
				newSentence +=collapsNounPhrases(child) + " ";
			}
		}

		return newSentence;

	}
}
