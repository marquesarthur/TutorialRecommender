package TermExtraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

import Utilities.Utils;

public class ImperativeMood {

	public static void main(String[] args) {
		try {
			String filetoProcess = "tutorials/JodaTime/annotatedJT.txt";
			String collapsedFile = "tutorials/JodaTime/collapsed.txt";
			
			String text = Utils.readFileAsString(filetoProcess);
			
			findImportantSentences(text);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static boolean isImperativeMood(CoreMap parsedSentence)
	{
		List<String> verbs = new ArrayList<String>();
		
		for(CoreLabel token:parsedSentence.get(TokensAnnotation.class))
		{
			if(token.get(PartOfSpeechAnnotation.class).equals("VB") || token.get(PartOfSpeechAnnotation.class).equals("VBP"))
				verbs.add(token.getString(TextAnnotation.class).toLowerCase());
		}
		
		SemanticGraph dependencies = parsedSentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		
		for (SemanticGraphEdge edge:dependencies.edgeIterable())
		{
			String dependent = edge.getDependent().get(TextAnnotation.class).toLowerCase();
			String governor = edge.getGovernor().get(TextAnnotation.class).toLowerCase();
			
			if (edge.getRelation().toString().contains("subj") && !dependent.equals("you") && 
						!dependent.equals("one") ) 
			{
				verbs.remove(governor);
			}
			else if ((edge.getRelation().toString().contains("aux") && 	!dependent.equals("to")) ||
					edge.getRelation().toString().equals("infmod") ||
					edge.getRelation().toString().equals("xcomp") ||
					edge.getRelation().toString().equals("cop")||
					edge.getRelation().toString().equals("partmod") ||
					edge.getRelation().toString().equals("agent"))
			{	
				verbs.remove(dependent);
			}
		}
		
		if(!verbs.isEmpty())
			return true;
		else
			return false;
	}
	
	public static boolean isImportantSentence(CoreMap sentence)
	{
		boolean important = false;
		
		// is important if contains modal verb
		for(CoreLabel token:sentence.get(TokensAnnotation.class))
			if (token.get(PartOfSpeechAnnotation.class).equals("MD"))
				important = true;
		
		// is important if contains "note"
		if (sentence.toString().toLowerCase().contains("note"))
			important = true;
		
		// is important if the sentence is in imperative mood
		if (isImperativeMood(sentence))
			important = true;
		
		return important;
	}
	
	public static List<String> findImportantSentences(String text)
	{
		List<String> imperativeSents = new ArrayList<String>();
		List<CoreMap> sentences= Utils.getSentences(text, true);
		for (CoreMap sentence:sentences)
		{
			if(isImportantSentence(sentence))
			{
				imperativeSents.add(sentence.toString());
				System.out.println(sentence.toString());
			}
		}
		return imperativeSents;
	}
}
