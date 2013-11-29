package similarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import Utilities.IncrementalSet;
import Utilities.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;


public class DocSimilarity {

	Map<String, List<String>> corpus = new HashMap<String, List<String>>();
	Map<String, HashSet<String>> docfreq = new HashMap<String, HashSet<String>>();

	Vocabulary vocab = new Vocabulary();

	public DocSimilarity() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws XPathExpressionException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XPathExpressionException, IOException {
		
		// build a corpus
		Date date= new Date();
		System.out.println(date);

		DocSimilarity docsim = new DocSimilarity();
		docsim.vocab.load("vocabulary");

//		String xmlPath = "../APIDoc/data/xml_NLP/jodatime.xml";
//		docsim.buildCorpusFromSingleFile(xmlPath);
		
//		String xmlPath = "javadocs/javadoc_math/";
//		String xmlPath = "javadocs/javadoc_java7/";
		String xmlPath = "javadocs/javadoc_smack/";
		docsim.buildCorpusFromDir(xmlPath);
		
		System.out.print("done building corpus:");
		Date corpusdate= new Date();
		System.out.println(corpusdate);

		// calculate similarities for all tutorial sections, each section text as a query
//		String tutorialCSVPath = "tutorials/JodaTime/jodatime-sections.csv";
//		String tutorialCSVPath = "tutorials/math/math-sections.csv";
//		String tutorialCSVPath = "tutorials/jcol/jcol-sections.csv";
		String tutorialCSVPath = "tutorials/smack/smack-sections.csv";
		Map<Integer, Map<String, Double>> similarityResults = docsim.tutorialSimilarities(tutorialCSVPath);
		
		
		// calculate precision-recall with different thresholds
//		String golden = "tutorials/JodaTime/jodatime-golden.csv";
//		String golden = "tutorials/math/math-golden.csv";
//		String golden = "tutorials/jcol/jcol-golden.csv";
		String golden = "tutorials/smack/smack-golden.csv";
		Map<Integer, Map<String, Boolean>> goldset = PrecisionRecallStat.loadGoldSet(golden);
		List<PrecisionRecallStat> precRecStat = PrecisionRecallStat.calc(similarityResults, goldset);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("threshold, precision, recall\n");
		for(PrecisionRecallStat precrec:precRecStat)
		{
			String out = precrec.threshold + "," + precrec.precision + "," + precrec.recall;
			strBuilder.append(out + "\n");
			System.out.println(out);
			
		}
		
//		String outFile = "joda-prec-recall.csv";
//		String outFile = "math-prec-recall.csv";
//		String outFile = "col-prec-recall.csv";
//		String outFile = "jcol-prec-recall.csv";
		String outFile = "smack-prec-recall.csv";
		Utils.saveFile(strBuilder.toString(), outFile);
		
		Date finishdate= new Date();
		System.out.println(finishdate);
	}

	public void buildCorpusFromSingleFile(String singleFile) throws IOException, XPathExpressionException
	{
		XMLDocAPI xmldoc = new XMLDocAPI(singleFile);
		List<String> apiElements = xmldoc.getAllAPIElements();

		for(String apiElement:apiElements)
		{
			if (!apiElement.contains("#"))
			{
				addDocumentToCorpus(apiElement, xmldoc.getTokenLemmas(apiElement));
			}
		}

		System.out.print("done building corpus:");
	}
	
	public void buildCorpusFromDir(String dirPath) throws XPathExpressionException, IOException
	{
		File dir = new File(dirPath);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				buildCorpusFromSingleFile(child.getAbsolutePath());
			}
			else
			{	if (!child.getName().equals("CVS"))
					buildCorpusFromDir(child.getAbsolutePath());
			}
		
	
		System.out.print("done building corpus:");
	}
	
	
	// calculates and returns similarity results for all tutorial sections
	public Map<Integer, Map<String, Double>> tutorialSimilarities(String tutorialCSVPath) throws IOException
	{
		Map<Integer, Map<String, Double>>  similarityResults = new HashMap<Integer, Map<String,Double>>();
		
		List<String> sections = Utils.readFileAsCSV(tutorialCSVPath);

		for (int i = 0; i< sections.size(); i++)
		{
			String section = sections.get(i);
			
			List<String> lemmaList = new ArrayList<String>();
			String text = Utils.cleanHtml(section, true);		
			List<CoreMap> sentences = Utils.getSentences(text, false);

			for(CoreMap sentence:sentences)
				for(CoreLabel token:sentence.get(TokensAnnotation.class))
				{
					String word = token.get(LemmaAnnotation.class);
					lemmaList.add(word);
				}

			similarityResults.put(i, calcSimilarity(lemmaList));
			
		}
		
		return similarityResults;
	}
	
	public Map<String, Double> calcSimilarity(List<String> query)
	{

		Map<String, Double> results = new HashMap<String, Double>();
		double[] doc1 = convertToVector(query, null);

		for(String doctitle:corpus.keySet())
		{
			double[] doc2 = convertToVector(corpus.get(doctitle), doc1);
			double sim = cosinSimilarity(doc1, doc2);

			doctitle.replace('$', '.');
			if (sim>1)
				System.out.println(sim);
			results.put(doctitle, sim);
		}
		
		return results;
	}

	public void addDocumentToCorpus(String title, List<String> words)
	{
		title = title.replace('$', '.');
		corpus.put(title, words);


		for(String word:words) 
		{
			HashSet<String> docset = docfreq.get(word);
			if (docset == null)
			{
				docset = new HashSet<String>();
			}
			docset.add(title);
			docfreq.put(word, docset);
		}
	}

	public double[] convertToVector(List<String> wordlist, double[] query)
	{
		IncrementalSet<String> wordSet = new IncrementalSet<String>();

		for(String word:wordlist)
			wordSet.put(word);

		double[] wordvector = new double[vocab.size()];

		for(String word:wordSet.getAllKeys())
		{
			int idx = vocab.getIdx(word);
			if (idx != -1)
			{
				double termdocfreq = 0;
				if (docfreq.get(word) != null)
					termdocfreq = docfreq.get(word).size();

				if (query != null)
					wordvector[idx] = wordSet.getValue(word) * Math.log((corpus.size()+1)/(termdocfreq + query[idx]>0?1:0));
				else
					wordvector[idx] = wordSet.getValue(word) * Math.log((corpus.size()+1)/(termdocfreq + 1));
			}
		}

		return wordvector;
	}

	public double cosinSimilarity(double[] doc1, double[] doc2)
	{
		double numerator = 0,doc1length =0, doc2length = 0;

		for(int i = 0; i < doc1.length; i++)
		{
			numerator += doc1[i] * doc2[i];
			doc1length += doc1[i] * doc1[i];
			doc2length += doc2[i] * doc2[i];
		}

		double cossim = numerator/(Math.sqrt(doc1length) * Math.sqrt(doc2length));

		return cossim;
	}
}
