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

import tutorial.APIElement;
import tutorial.SectionElement;
import tutorial.Tutorial;

import Utilities.IncrementalSet;
import Utilities.SortedMapByValues;
import Utilities.Utils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;


public class DocSimilarityTopN {

	Map<String, List<String>> corpus = new HashMap<String, List<String>>();
	Map<String, HashSet<String>> docfreq = new HashMap<String, HashSet<String>>();

	Vocabulary vocab = new Vocabulary();

	public DocSimilarityTopN() {
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

		DocSimilarityTopN docsim = new DocSimilarityTopN();
		docsim.vocab.load("vocabulary");

//				String xmlPath = "../APIDoc/data/xml_NLP/jodatime.xml";
//				docsim.buildCorpusFromSingleFile(xmlPath);

//				String xmlPath = "javadocs/javadoc_math/";
//				String xmlPath = "javadocs/javadoc_java7/";
		String xmlPath = "javadocs/javadoc_smack/";
		docsim.buildCorpusFromDir(xmlPath);

		System.out.print("done building corpus:");
		Date corpusdate= new Date();
		System.out.println(corpusdate);

		// calculate similarities for all tutorial sections, each section text as a query
//				String tutorialCSVPath = "tutorials/JodaTime/jodatime.csv";
//				String tutorialCSVPath = "tutorials/math/math_annotation_disagreements.csv";
//				String tutorialCSVPath = "tutorials/jcol/jcol_tutorial.csv";
		String tutorialCSVPath = "tutorials/smack/smack_tutorial.csv";
		docsim.tutorialSimilarities(tutorialCSVPath, false);


		
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
	public void tutorialSimilaritiesTh(String tutorialCSVPath, boolean type) throws IOException
	{

		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial(tutorialCSVPath, false, type);
		 
		Map<String,SortedMapByValues<String, Double>> simResults = calcSim(tutorial);
		
		double threshold = getThreshold(tutorial, simResults);
		
		for(String apiName:tutorial.sectionElementsPerElement.keySet())
		{
			Map<String, Double> elResults = simResults.get(apiName).getByThreshold(threshold);
			
			// calculate precision and recall
			double ctp = 0,cfp = 0, cfn = 0, ctn = 0;
			double stp = 0,sfp = 0, sfn = 0, stn = 0;
			for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
			{
				if(secEl.getLabel())
				{
					if(secEl.getGuessedLabel())
						ctp++;
					else
						cfn++;
					
					if (elResults.get(secEl.getSection().getTitle()+secEl.getSection().getSubTitle()) != null)
						stp++;
					else
						sfn++;
				}
				else
				{
					if(secEl.getGuessedLabel())
						cfp++;
					else
						ctn++;
					
					if (elResults.get(secEl.getSection().getTitle()+secEl.getSection().getSubTitle()) == null)
						sfp++;
					else
						stn++;
				}
				
			}
			
			// print precision, recall, f1 for both cases
			double cprecision = ctp + cfp == 0? 0 : ctp	/ (ctp + cfp);
			double crecall = ctp + cfn == 0 ? 0 : ctp / (ctp + cfn);
			double cf1 = cprecision + crecall == 0? 0:(2 * cprecision * crecall) / (cprecision + crecall);
			
			double sprecision = stp + sfp == 0? 0: stp	/ (stp + sfp);
			double srecall = stp + sfn == 0? 0:stp / (stp + sfn);
			double sf1 = sprecision + srecall == 0? 0:(2 * sprecision * srecall) / (sprecision + srecall);
			
			System.out.println(apiName + "-" + tutorial.sectionElementsPerElement.get(apiName).size()+"-"  + "-" + cprecision + "-" + crecall + "-" + cf1 +
										"-" +sprecision + "-" + srecall +"-" + sf1);
		
		}
	}

	public Map<String,SortedMapByValues<String, Double>> calcSim(Tutorial tutorial) throws IOException
	{
		Map<String,SortedMapByValues<String, Double>> simResults  = new HashMap<String, SortedMapByValues<String,Double>>();
		for(String apiName:tutorial.sectionElementsPerElement.keySet())
		{
			// for each pair calc similarity and put in sorted list
			SortedMapByValues<String, Double> elResults = new SortedMapByValues<String, Double>();
			
			for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
			{
				String sectionText = secEl.getSection().getText();
				String apiFqn = secEl.getApiElement().getFqn();
				
				double sim = calcSimilarity(getSectionWordLemmas(sectionText), apiFqn);
				elResults.put(secEl.getSection().getTitle()+secEl.getSection().getSubTitle(), sim);
			}
			
			simResults.put(apiName, elResults);			
		}
		
		return simResults;
	}
	
	public double getThreshold(Tutorial tutorial, Map<String,SortedMapByValues<String, Double>> simResults) throws IOException
	{
		double threshold = 0;
		for(String apiName:tutorial.sectionElementsPerElement.keySet())
		{
			// count number of recommendations needed
			int cntRecommendation = 0;
			for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
			{
				if(secEl.getLabel())
					cntRecommendation++;
			}
			
			double size = tutorial.sectionElementsPerElement.get(apiName).size();
			
			// if count > 0, means if there are any recommendations
			if(cntRecommendation > 0)
			{	
				// for each pair calc similarity and put in sorted list
				SortedMapByValues<String, Double> elResults = simResults.get(apiName);
				threshold += elResults.getNValue(cntRecommendation)/size;
				simResults.put(apiName, elResults);
			}
			
		}
		
		return threshold;
	}
	
	// calculates and returns similarity results for all tutorial sections
	public void tutorialSimilarities(String tutorialCSVPath, boolean type) throws IOException
	{
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial(tutorialCSVPath, false, type);

		for(String apiName:tutorial.sectionElementsPerElement.keySet())
		{
			// count number of recommendations needed
			int cntRecommendation = 0;
			for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
			{
				if(secEl.getGuessedLabel())
					cntRecommendation++;
			}

			// if count > 0, means if there are any recommendations
			if(cntRecommendation > 0)
			{
				// for each pair calc similarity and put in sorted list
				SortedMapByValues<String, Double> simResults = new SortedMapByValues<String, Double>();
				
				for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
				{
					String sectionText = secEl.getSection().getText();
					String apiFqn = secEl.getApiElement().getFqn();
					
					double sim = calcSimilarity(getSectionWordLemmas(sectionText), apiFqn);
					simResults.put(secEl.getSection().getTitle()+secEl.getSection().getSubTitle(), sim);
				}
				
				// take top(count) from sorted list
				Map<String, Double> topN = simResults.topN(cntRecommendation);
				
				// calculate precision and recall
				double ctp = 0,cfp = 0, cfn = 0, ctn = 0;
				double stp = 0,sfp = 0, sfn = 0, stn = 0;
				for(SectionElement secEl:tutorial.sectionElementsPerElement.get(apiName))
				{
					if(secEl.getLabel())
					{
						if(secEl.getGuessedLabel())
							ctp++;
						else
							cfn++;
						
						if (topN.get(secEl.getSection().getTitle()+secEl.getSection().getSubTitle()) != null)
							stp++;
						else
							sfn++;
					}
					else
					{
						if(secEl.getGuessedLabel())
							cfp++;
						else
							ctn++;
						
						if (topN.get(secEl.getSection().getTitle()+secEl.getSection().getSubTitle()) != null)
							sfp++;
						else
							stn++;
					}
					
				}
				
				// print precision, recall, f1 for both cases
				double cprecision = ctp + cfp == 0? 0 : ctp	/ (ctp + cfp);
				double crecall = ctp + cfn == 0 ? 0 : ctp / (ctp + cfn);
				double cf1 = cprecision + crecall == 0? 0:(2 * cprecision * crecall) / (cprecision + crecall);
				
				double sprecision = stp + sfp == 0? 0: stp	/ (stp + sfp);
				double srecall = stp + sfn == 0? 0:stp / (stp + sfn);
				double sf1 = sprecision + srecall == 0? 0:(2 * sprecision * srecall) / (sprecision + srecall);
				
				System.out.println(apiName + "-" + tutorial.sectionElementsPerElement.get(apiName).size()+"-" +cntRecommendation + "-" + cprecision + "-" + crecall + "-" + cf1 +
											"-" +sprecision + "-" + srecall +"-" + sf1);
			}
			else
			{
				System.out.println(apiName + ": " + tutorial.sectionElementsPerElement.get(apiName).size()+"-" +cntRecommendation);
			}
		}

	}

	public List<String> getSectionWordLemmas(String sectionText) throws IOException
	{
		List<String> lemmaList = new ArrayList<String>();
		String text = Utils.cleanHtml(sectionText, true);		
		List<CoreMap> sentences = Utils.getSentences(text, false);

		for(CoreMap sentence:sentences)
			for(CoreLabel token:sentence.get(TokensAnnotation.class))
			{
				String word = token.get(LemmaAnnotation.class);
				lemmaList.add(word);
			}
		
		return lemmaList;
	}
	
	public double calcSimilarity(List<String> query, String api)
	{

		double[] doc1 = convertToVector(query, null);

		double[] doc2 = convertToVector(corpus.get(api), doc1);
		double sim = cosinSimilarity(doc1, doc2);
		
		return sim;
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
