package similarity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.jsoup.nodes.Element;

import au.com.bytecode.opencsv.CSVWriter;

import Utilities.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class VocabularyBuilder {

	HashSet<String> vocabulary = new HashSet<String>();

	public VocabularyBuilder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VocabularyBuilder	vocab = new VocabularyBuilder();
		vocab.addVocabularyFromXML("../APIDoc/data/xml_NLP/jodatime.xml");
		try {
			vocab.addVocabularyFromHTMLFile("tutorials/JodaTime/jt");
			vocab.saveVocabulary("vocabulary", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addVocabularyFromXML(String xmlPath)
	{
		XMLDocAPI xmlDoc = new XMLDocAPI(xmlPath); 
		List<String> lemmas = xmlDoc.getAllTokenLemmas();

		for(String lemma:lemmas)
			addVocabWord(lemma);
	}

	public void addVocabularyFromHTMLFiles(String fileDir) throws IOException
	{
		File dir = new File(fileDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
				addVocabularyFromHTMLFile(child.getAbsolutePath());
			else
				addVocabularyFromHTMLFiles(child.getAbsolutePath());
	}

	public void addVocabularyFromHTMLFile(String filePath) throws IOException
	{
		String text = Utils.cleanHtml(Utils.readFileAsString(filePath), true);		
		List<CoreMap> sentences = Utils.getSentences(text, true);

		for(CoreMap sentence:sentences)
			for(CoreLabel token:sentence.get(TokensAnnotation.class))
			{
				String word = token.get(LemmaAnnotation.class);
				addVocabWord(word);
			}
	}


	public void saveVocabulary(String path, boolean append) throws IOException
	{
		FileWriter fstream = new FileWriter(path);
		BufferedWriter out = new BufferedWriter(fstream);
		for(String word:vocabulary)
			out.write(word + "\n");			
		out.close();

	}

	private void addVocabWord(String word)
	{
		word = word.toLowerCase();
		
		if (isVocabWord(word))
			vocabulary.add(word);
	}

	private boolean isVocabWord(String word)
	{
		// if first character of the word is letter
		// then it is a valid word for vocabulary
		if (!Character.isLetter(word.toCharArray()[0]))
			return false;
		
		if(Utils.isStopWord(word))
			return false;
		
		if (word.length() == 1)
			return false;
		
		return true;
	}

}
