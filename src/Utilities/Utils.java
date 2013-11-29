package Utilities;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import tutorial.TutorialSection;
import au.com.bytecode.opencsv.CSVReader;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class Utils {

	private static boolean init = false;


	private static StanfordCoreNLP splitPipeline;
	private static StanfordCoreNLP pipeline;
	private static Boolean isStanfordInit = false;
	private static void initStanford()
	{
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.put("customAnnotatorClass.clttag", "Utilities.CLTTagAnnotator");

		props.put("annotators", "tokenize, ssplit, clttag, lemma, parse");
		pipeline = new StanfordCoreNLP(props, false);

		// creates a StanfordCoreNLP object 
		Properties splitProps = new Properties();
		splitProps.put("annotators", "tokenize, ssplit, pos, lemma");
		splitPipeline = new StanfordCoreNLP(splitProps);

		isStanfordInit = true;
	}

	// Variables for tokenizer
	private static Tokenizer tokenizer;

	private static void initLangModels()
	{
		try
		{
			InputStream modelIn = new FileInputStream("opennlp_models/en-token.bin");

			try {
				TokenizerModel model = new TokenizerModel(modelIn);
				tokenizer = new TokenizerME(model);
				init = true;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("sentence detector model cannot be found.");
		}
	}


	public static CoreMap parseSentence(String sent)
	{
		if (!isStanfordInit)
			initStanford();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(sent);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		return sentences.get(0);
	}

	public static List<CoreMap> getSentences(String text, boolean parse)
	{

		if (!isStanfordInit)
			initStanford();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		if (parse)
			pipeline.annotate(document);
		else
			splitPipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String> sentenceList = new ArrayList<String>();
		for(CoreMap sentence: sentences) {
			sentenceList.add(sentence.toString());
		}
		return sentences;

	}

	public static String[] getSentencesOpenNLP(TutorialSection section)
	{
		String sentences[] = {};
		try
		{
			InputStream modelIn = new FileInputStream("opennlp_models/en-sent.bin");

			try {
				SentenceModel model = new SentenceModel(modelIn);
				SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

				sentences = sentenceDetector.sentDetect(section.getText());

			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("sentence detector model cannot be found.");
		}

		return sentences;
	}

	public static String getLemmedWord(String word)
	{
		if (!isStanfordInit)
			initStanford();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(word);

		// run all Annotators on this text
		pipeline.annotate(document);

		List<CoreLabel> tokens = document.get(SentencesAnnotation.class).get(0).get(TokensAnnotation.class);
		CoreLabel token = tokens.get(tokens.size()-1);

		return token.get(LemmaAnnotation.class);
	}

	

	public static int getWordCount(String text)
	{
		if (!init)
			initLangModels();
		String tokens[] = tokenizer.tokenize(text);
		
		return tokens.length;

	}

	// add dots and commas to HTML
	public static String addPunctuationHTML(String html)
	{
		Document doc = Jsoup.parse(html);

		String[] tags = {"h1", "h2", "h3", "h4", "dt", "dd", "p", "tr"};

		for (String tag:tags)
		{
			Elements titles = doc.getElementsByTag(tag);
			for (Element title:titles)
			{
				String tmp = title.text();
				if (!tmp.trim().equals(""))
				{
					if (Character.isDigit(tmp.charAt(tmp.length()-1)) || Character.isAlphabetic(tmp.charAt(tmp.length()-1)))
						title.html(title.html() + ". <br>");
					else
						title.html(title.html() + "<br>");
				}
			}
		}

		String[] commaTags = {"il", "td"};

		for (String tag:commaTags)
		{
			Elements titles = doc.getElementsByTag(tag);
			if (!titles.isEmpty())
			{
				for (int i=0; i < titles.size()-1; i++)
				{
					Element title = titles.get(i);
					String tmp = title.text();
					if (Character.isDigit(tmp.charAt(tmp.length()-1)) || Character.isAlphabetic(tmp.charAt(tmp.length()-1)))
						title.html(title.text() + ",");
				}


				Element title = titles.get(titles.size()-1);
				String tmp = title.text();
				if (Character.isDigit(tmp.charAt(tmp.length()-1)) || Character.isAlphabetic(tmp.charAt(tmp.length()-1)))
					title.html(title.text() + ".");
			}
		}

		return doc.html();

	}
	
	public static String modifyAPIElementText(String html) {
		Document doc = Jsoup.parse(html);
		Elements clts = doc.select("clt");
		
		for(Element clt: clts)
		{
			if (clt.text().equals(clt.attr("api")))
				clt.text("clt_" + clt.attr("api"));
			else
			{
				String[] content = clt.text().split("\\.");
				//System.out.println(clt.text() + "=>" + "clt_" + clt.attr("api") + "_" +content[content.length-1]);				
				clt.text("clt_" + clt.attr("api") + "_" +content[content.length-1]);

			}
		}
		
		return doc.html();
	}
	
	public static String replaceCLTDelimiter(String html)
	{
		Pattern pattern = Pattern.compile("[A-Za-z][A-Za-z0-9]+(\\.[A-Za-z][A-Za-z0-9]+)+");

		Matcher matcher = pattern.matcher(html);
		StringBuffer sb  = new StringBuffer();;
		if (!matcher.find())
			sb.append(html);
		else
		{
			matcher.reset();
			while(matcher.find())
			{
				String codeliketerm = matcher.group();			
				matcher.appendReplacement(sb, codeliketerm.replaceAll("\\.", "_"));
			}
			matcher.appendTail(sb);
		}

		return sb.toString();
	}

	public static String HTML2Text(String html)
	{
		String cleanHTML = Jsoup.clean(html, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");
		cleanHTML = cleanHTML.replaceAll("&nbsp;", " ");

		return cleanHTML;
	}

	public static List<String> collapseCodeSegments(Document doc) {


		List<String> codepgh = new ArrayList<String>();

		//Collapse code segments
		int codeid = 0; 

		Elements snippets = doc.getElementsByTag("pre");
		
		for(Element snippet:snippets)
		{
			String replaceText = "CODEID"+codeid;
			codepgh.add(snippet.html());
			snippet.replaceWith(new TextNode(replaceText, ""));

			codeid++;
		}

		// removing </p>, <p> tags surrounding code segment 
		String html = doc.html();
		Pattern pattern = Pattern.compile("</p>\\s*(CODEID\\d+)\\s*<p>");
		Matcher matcher = pattern.matcher(html);
		StringBuffer sb  = new StringBuffer();;
		if (!matcher.find())
			sb.append(html);
		else
		{
			matcher.reset();
			while(matcher.find())
			{
				String code = matcher.group(1);			
				matcher.appendReplacement(sb, code);
			}
			matcher.appendTail(sb);
		}
		// removing <p> tags after code segment 
		pattern = Pattern.compile("(CODEID\\d+)\\s*<p>");
		matcher = pattern.matcher(sb.toString());
		if (matcher.find())
		{
			sb=new StringBuffer();
			matcher.reset();
			while(matcher.find())
			{
				String code = matcher.group(1);			
				matcher.appendReplacement(sb, code);
			}
			matcher.appendTail(sb);
		}

		// removing </p> before code segment.
		pattern = Pattern.compile("</p>\\s*(CODEID\\d+)");
		matcher = pattern.matcher(sb.toString());
		if (matcher.find())
		{
			sb=new StringBuffer();
			matcher.reset();
			while(matcher.find())
			{
				String code = matcher.group(1);			
				matcher.appendReplacement(sb, code);
			}
			matcher.appendTail(sb);
		}


		// checking if code is the end of the sentence.
		// code is considered the end of the sentence if
		// 1. next symbol is upper-case
		// 2. next symbol is < as first symbol of the html tag
		pattern = Pattern.compile("(CODEID\\d+)(\\s*)([A-Z])");
		matcher = pattern.matcher(sb.toString());
		if (matcher.find())
		{
			sb=new StringBuffer();
			matcher.reset();
			while(matcher.find())
			{
				String code = matcher.group(1) + "." + matcher.group(2)+ matcher.group(3);
				matcher.appendReplacement(sb, code);
			}
			matcher.appendTail(sb);
		}

		pattern = Pattern.compile("(CODEID\\d+)(\\s*)(<)");
		matcher = pattern.matcher(sb.toString());
		if (matcher.find())
		{
			sb=new StringBuffer();
			matcher.reset();
			while(matcher.find())
			{
				String code = matcher.group(1) + "." + matcher.group(2)+ matcher.group(3);
				matcher.appendReplacement(sb, code);
			}
			matcher.appendTail(sb);
		}

		doc.html(sb.toString());
		return codepgh;
	}

	public static List<String> tokenizeTitle(String title)
	{
		List<String> titlewordsList = new ArrayList<String>();

		String[] words = title.split("[\\W]");
		for(String word:words)
		{
			titlewordsList.add(word); //splitWord
		}

		for (int i = 0; i< titlewordsList.size(); i++)
		{
			PorterStemmer s = new PorterStemmer();

			String word = titlewordsList.get(i);
			s.add(word.toCharArray(), word.length());
			s.stem();
			titlewordsList.set(i, s.toString());
		}

		return titlewordsList;
	}

	public static List<String> splitWord(String word)
	{
		List<String> words = new ArrayList<String>();

		Pattern pattern = Pattern.compile("([A-Z][a-z]+)");
		Matcher matcher = pattern.matcher(word);

		while(matcher.find())
			words.add(matcher.group());

		return words;
	}

	public static String[] stopWords = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};

	public static List<String> getStopWordsList()
	{
		List<String> stopwordList = new ArrayList<String>();

		for (String word:stopWords)
			stopwordList.add(word);
		return stopwordList;

	}

	public static boolean isStopWord(String word)
	{
		List<String> stopWordsList = getStopWordsList();
		if(stopWordsList.contains(word.toLowerCase()))
			return true;
		return false;
	}

	public static List<CoreLabel> getWordList(String text, boolean removeStopWords)
	{
		List<String> stopWordsList = getStopWordsList();
		if (!isStanfordInit)
			initStanford();

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<CoreLabel> wordsList = new ArrayList<CoreLabel>();
		for(CoreMap sentence: sentences) {
			for(CoreLabel token:sentence.get(TokensAnnotation.class))
			{
				String word = token.get(TextAnnotation.class).toLowerCase();
				//	    		PorterStemmer s = new PorterStemmer();
				//    		   
				//    			s.add(word.toCharArray(), word.length());
				//    			s.stem();
				//    			word = s.toString();
				token.set(TextAnnotation.class, word);
				if (!removeStopWords || (removeStopWords && !stopWordsList.contains(word)))
					if (Character.isAlphabetic(word.toCharArray()[0]))
						wordsList.add(token);
			}
		}

		return wordsList;	

	}

	public static boolean containsWord(String text, String word)
	{
		return text.matches(".*\\s" + word + "\\s.*") || text.matches(".*clt_"+word+"\\s.*");
	}

	public static List<String> getSeedWords()
	{
		List<String> seedWords = new ArrayList<String>();
		CSVReader reader;

		try {
			reader = new CSVReader(new FileReader("tutorials/seedwords.csv"));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null)
				seedWords.add(nextLine[0]);
			reader.close();
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}

		return seedWords;
	}

	public static List<String> getPositiveWords() {
		List<String> seedWords = new ArrayList<String>();
		CSVReader reader;

		try {
			reader = new CSVReader(new FileReader("tutorials/positivewords.csv"));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null)
				seedWords.add(nextLine[0]);
			reader.close();
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}

		return seedWords;
	}

	public static List<String> getNegativeWords() {
		List<String> seedWords = new ArrayList<String>();
		CSVReader reader;

		try {
			reader = new CSVReader(new FileReader("tutorials/negativewords.csv"));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null)
				seedWords.add(nextLine[0]);
			reader.close();
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}

		return seedWords;
	}

	public static void crawler(String url, String saveDir) throws IOException
	{
		File f = new File(saveDir + getPageName(url));
		if (!f.exists())
		{
			System.out.println("processing " + url + "...");
			Document doc = Jsoup.connect(url).get();

			String htmlText = cleanHtml(doc.html(), false);
			saveFile(htmlText, saveDir + getPageName(url), false);

			Elements links = doc.select("a");

			for (Element link:links)
			{
				String subUrl = link.attr("href").split("#")[0];

				if (!subUrl.startsWith("javascript") && getExtension(subUrl).startsWith("ht"))
				{
					URL baseUrl = new URL(url);
					URL surl = new URL( baseUrl , subUrl);
					subUrl = surl.toString();

					if (getPageRoot(subUrl).startsWith(getPageRoot(url)))
						crawler(subUrl, saveDir);
				}
			}
		}


	}

	public static String cleanHtml(String html, boolean turnToText) throws IOException
	{	
		html = HTMLUtils.removeDoubleLF(html);
		String cleanHtml = "";
		Document doc ;
		try				
		{
			doc = Jsoup.parse(html);		
			Element body = doc.select("body").get(0);

			//remove head
			Elements head = body.getElementsByTag("head");		
			for (Element e:head)
			{
				e.remove();
			}

			// remove script
			Elements scripts = body.getElementsByTag("script");		
			for (Element e:scripts)
			{
				e.remove();
			}

			// remove script
			Elements noscripts = body.getElementsByTag("noscript");		
			for (Element e:noscripts)
			{
				e.remove();
			}

			//<span id="BreadCrumbs">
			Elements spans = body.select("span#BreadCrumbs");		
			for (Element e:spans)
			{
				e.remove();
			}

			//title="Permalink to this headline"
			// Permalink to this definition
			Elements as = body.getElementsByTag("a");
			for(Element e:as)
			{
				if (e.attr("title").startsWith("Permalink to this headline"))
					e.remove();
				else if (e.className().equals("reference internal"))
					e.remove();
			}



			//<div class="sidebar">
			//<div class="twitter">
			//<div class="googleplus">
			//<div class="toc">		
			//<div class="menu-header">
			//<div class="help_breadcrumbs">
			//<div class="xleft">
			//<div class="xright">
			//<div class="footer">
			//<div id="breadcrumbs">
			// <div id="footer">
			//<div id="leftColumn">
			Elements divs = body.getElementsByTag("div");		
			for (Element e:divs)
			{
				if (e.className().equals("sidebar") || 
						e.className().equals("twitter") ||
						e.className().equals("googleplus") ||
						e.className().equals("toc") ||
						e.className().equals("menu-header") ||
						e.className().equals("help_breadcrumbs") ||
						e.className().equals("xleft") ||
						e.className().equals("xright") ||
						e.className().equals("NavBit") ||
						e.className().equals("nav") ||
						e.className().equals("PrintHeaders") ||						
//						e.className().equals("codeblock") ||
						e.className().equals("method") ||
						e.className().equals("class") ||
						e.className().equals("footer") ||
						e.id().equals("breadcrumbs") ||
						e.id().equals("BreadCrumbs") ||
						e.id().equals("footer") ||
						e.id().equals("leftColumn") ||
						e.id().equals("StayFocusd-infobar") ||
						e.id().equals("TopBar") ||
						e.id().equals("LeftBar") ||
						e.id().equals("Footer") ||
						e.id().equals("Footer2"))
					e.remove();
			}

			Elements tables = body.getElementsByClass("longtable docutils");
			for (Element e:tables)
			{
				e.remove();
			}

			//<pre class="programlisting">
			Elements pres = body.getElementsByTag("pre");		
			for (Element e:pres)
			{
				if (e.className().equals("programlisting") ||
						e.className().equals("brush:java") ||
						e.className().equals("screen"))
					e.remove();
			}

			//class="topnav"
			Elements topnav = body.getElementsByClass("topnav");		
			for (Element e:topnav)
			{	
				e.remove();
			}

			// removing empty tags
			boolean changed;
			do
			{
				changed = false;
				Elements allelements = body.getAllElements();		
				for (Element e:allelements)
				{	
					if (e.text().trim().equals(""))
					{
						changed = true;
						e.remove();
					}

				}				
			}
			while(changed);



			cleanHtml = body.html();
			if (turnToText)
			{
				cleanHtml = Jsoup.clean(cleanHtml, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");
				cleanHtml.replaceAll("&nbsp;", "\n");
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return cleanHtml;
	}
	public static void saveFile(String text, String outfile) throws IOException
	{	
		saveFile(text, outfile, false);
	}
	
	public static void saveFile(String text, String outfile, boolean append)
	{	
		FileWriter fstream;
		try {
			fstream = new FileWriter(outfile, append);
		
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static String getPageName(String url)
	{
		String[] tmp = url.split("/");
		if (url.endsWith("/"))			
			return tmp[tmp.length-2];
		else
			return tmp[tmp.length-1];
	}

	public static String getPageRoot(String url)
	{
		if (url.lastIndexOf("/") != -1)
			return url.substring(0, url.lastIndexOf("/"));
		return "";
	}

	public static List<String> readFileAsCSV(String filePath) throws IOException
	{
		List<String> strList = new ArrayList<String>();
		
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(filePath), '\t');

			String[] nextLine;
			
			while ((nextLine = reader.readNext()) != null) {
				strList.add(nextLine[0]);
			}

		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}

		return strList;
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

	public static String getExtension(String filename)
	{
		String[] parts = filename.split("\\.");

		if (parts.length>1)
			return parts[1];
		else
			return "";
	}

	static List<String> concepts = null;
	public static String groupConceptWords(String text)
	{
		try {
			if (concepts == null)
				concepts = readFileAsList("concepts");

			for (String concept:concepts)
			{	
				//System.out.println(concept);
				String regex = concept.replaceAll(" ", "\\\\s+");

				String replacement;
				if (!concept.contains("CLT"))
					replacement = concept.replaceAll(" ", "");
				else
					replacement = "clt";
				text = text.replaceAll("(?i)" + regex, replacement);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return text;
	}

	public static List<String> readFileAsList(String filePath) throws IOException
	{
		List<String> stringList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = br.readLine()) != null) {
			stringList.add(line);
		}
		br.close();

		return stringList;
	}

	public static String combinePaths(String path, String name)
	{
		if (path.endsWith("/"))
			path +=name;
		else
			path+="/" + name;

		return path;
	}

	private static List<List<TaggedWord>> getPOSTags(String pInput)
	{
		List<List<TaggedWord>> lResult = new ArrayList<List<TaggedWord>>();
		LexicalizedParser lLexicalizedParser = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		DocumentPreprocessor lDocumentPreprocessor = new DocumentPreprocessor(new StringReader(pInput));
		for (List<HasWord> lSentence : lDocumentPreprocessor)
		{
			for (int lCount = 0; lCount < lSentence.size(); lCount++)
			{
				// TODO: this is example specific
				if (lSentence.get(lCount).word().equals("include"))
				{
					CoreLabel lCoreLabel = new CoreLabel();
					lCoreLabel.setWord(lSentence.get(lCount).word());
					lCoreLabel.setTag("NN");
					lSentence.set(lCount, lCoreLabel);
				}
			}
			Tree lParse = lLexicalizedParser.apply(lSentence);
			lResult.add(lParse.taggedYield());
		}
		return lResult;
	}
	
	public static String getClassName(String fqn, String kind)
	{
		//System.out.println(fqn);
		String result;
		String[] hierarchy = fqn.split("\\.");
		if (!kind.equals("class"))
			result = hierarchy[hierarchy.length-2];
		else
			result = hierarchy[hierarchy.length-1];
		
		return result;
	}
} 
