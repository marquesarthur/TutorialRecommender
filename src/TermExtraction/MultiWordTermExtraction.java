package TermExtraction;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Utilities.Utils;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;


public class MultiWordTermExtraction {
	public static MultiWordTermStatistics phrases = new MultiWordTermStatistics();;	
	public static HashMap<String, HashMap<String, Integer>> context = new HashMap<String, HashMap<String, Integer>>();
	public static HashMap<String, HashMap<String, Integer>> contextFreq = new HashMap<String, HashMap<String, Integer>>();
	public static MaxentTagger mtager;
	public static String libDir = "C:/Users/gaya/prog/workspace/TutorialRecommender/lib/"; ///diskless/swevo-2/gpetro6/
	public static String[] stopwords = {"more", "very", "demo", "example", "powerful", "such", "when", 
											"next", "previous", "last", "first", "other", "same"};
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			mtager = new MaxentTagger("lib/models/english-left3words-distsim.tagger");

			extractPhrases("tutorials/JavaTutorials/filtered"); ///diskless/swevo-2/gpetro6/workspace/xprima/plaintext/
			phrases.dumpTerms("dumps/multiwordterms");
			CalcProb("dumps/multiwordtermsSat.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	static void SplitSentencesToFile(String docDir, String outputPath)
	{
		try {

			File f = new File(docDir );
			File[] allFiles = f.listFiles();

			FileWriter outFile = new FileWriter(outputPath, true);
			PrintWriter out = new PrintWriter(outFile);

			for (File docFile:allFiles)
			{	
				if (docFile.isFile())
				{
					String[] sentences = null;

					InputStream modelIn = new FileInputStream(libDir + "apache-opennlp-1.5.2-incubating/en-sent.bin");		
					SentenceModel model = new SentenceModel(modelIn);
					SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
					sentences = sentenceDetector.sentDetect(readFileAsString(docFile.getPath()));

					for (String sent:sentences)
					{
						out.println(sent);
					}
				}
				else if (docFile.isDirectory())
				{
					SplitSentencesToFile(docFile.getAbsolutePath(), outputPath);
				}

			}	

			out.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());		
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	static String[] SplitSentences(String filePath)
	{
		String[] sentences = null;

		try {
			InputStream modelIn = new FileInputStream(libDir + "apache-opennlp-1.5.2-incubating/en-sent.bin");		
			SentenceModel model = new SentenceModel(modelIn);
			SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
			String text = readFileAsString(filePath);
			text = text.replaceAll("<tt>.*?</tt>", "cltnoun");
			Utils.groupConceptWords(text);
			sentences = sentenceDetector.sentDetect(text);

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());		
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return sentences;
	}

	public static void extractPhrases(String docDir) throws IOException
	{
		File f = new File(docDir );
		File[] allFiles = f.listFiles();

		for (File docFile:allFiles)
		{	
			if (docFile.isFile())
			{
				System.out.println(docFile.getPath());
				String[] sentences = SplitSentences(docFile.getPath());

				for(String sent:sentences)
				{	
					FilterPhrases(mtager.tagString(sent));
				}

			}
			else if (docFile.isDirectory())
			{
				extractPhrases(docFile.getAbsolutePath());
			}

		}	

	}

	public static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));

			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}		
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		finally
		{
			reader.close();
		}
		return fileData.toString();

	}

	//	private static String TagSentance(String sentance)
	//	{	
	//		String taggedSent = ""; 
	//		taggedSent = mtager.tagString(sentance);
	//		System.out.println(taggedSent);
	//		return taggedSent;
	//	}

	private static void FilterVerbalPhrases(String taggedSent) throws IOException
	{
		Matcher m;
		for (int i=1; i<=3; i++)
		{
			Pattern filter = Pattern.compile("([A-aZ-z0-9-]+_(VB|VBD|VBG|VBN|VBP|VBZ)\\s?){"+ i +"}([A-aZ-z0-9-]+_IN\\s?)?([A-aZ-z0-9-]+_ADVP\\s?)?");				
		
			m = filter.matcher(taggedSent);
			while(m.find()) 
			{
				String phrase = m.group(0).replaceAll("_[A-Za-z]+", "");
				
				// first verb should be lemmatized
				String[] words = phrase.split(" ");
				words[0] = Utils.getLemmedWord(words[0]);
				
				phrase = "";
				for(String word:words)
					phrase += word + " ";
				phrase = phrase.trim().toLowerCase();
				phrases.incrementFrequency(phrase);
			}
		}
	}
	
	private static void FilterPhrases(String taggedSent) throws IOException
	{
		Matcher m;
		for (int i=0; i<=10; i++)
		{
			Pattern filter = Pattern.compile("((([A-aZ-z0-9-]+_(JJ|JJR|JJS|NN|NNS|NNP|NNPS)\\s?)(([A-aZ-z0-9-]+_IN\\s?)?([A-aZ-z0-9-]+_(JJ|JJR|JJS|NN|NNS|NNP|NNPS)\\s?)){"+ i+"})[A-aZ-z0-9-]+_(NN|NNS|NNP|NNPS))");				
		
			m = filter.matcher(taggedSent);
			while(m.find()) 
			{
				String phrase = m.group(0).replaceAll("_[A-Za-z]+", "");				
				phrase = phrase.trim().toLowerCase();
				if (!phrase.contains("cltnoun"))					
					phrases.incrementFrequency(phrase);
			}
		}
	}

//	private static float CalcContextValue(String phrase)	
//	{
//		HashMap<String, Integer> phraseContext = context.get(phrase);
//
//		float contextFactor = 0;
//
//		if (phraseContext != null)
//			for (String contextWord:phraseContext.keySet())
//			{
//				HashMap<String, Integer> contextWrodFreq = contextFreq.get(contextWord);
//				float wordweigth = (float)contextWrodFreq.size() / (float)phrases.size();
//
//				contextFactor += wordweigth * phraseContext.get(contextWord);
//			}
//
//		return contextFactor;
//	}

	private static void CalcProb(String outputPath)
	{
		phrases.calcSubStringSat();
		phrases.calcCValue();

		phrases.dumpTermStat(outputPath);
	}

}
