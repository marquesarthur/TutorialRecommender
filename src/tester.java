import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import similarity.XMLDocAPI;
import tutorial.SectionElement;
import tutorial.Splitter;
import tutorial.Tutorial;
import tutorial.TutorialProcessing;
import tutorial.TutorialSection;
import Utilities.HTMLUtils;
import Utilities.IncrementalSet;
import Utilities.SortedMapByValues;
import Utilities.Utils;
import au.com.bytecode.opencsv.CSVReader;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import features.OccuranceAsWordFeature;
import features.sentencelevel.IsExampleFeature;
import features.sentencelevel.IsInEnumFeature;
import features.sentencelevel.RelTypeFeature;

public class tester {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		wordNumFeatureTest();
	}

	public static void wordNumFeatureTest()
	{
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/math/math_annotation_disagreements.csv", true, false); //


		for(int i = 0; i<tutorial.sectionElements.size(); i++)
			System.out.println(tutorial.sectionElements.get(i).getSection().getTitle() +
					"," + tutorial.sectionElements.get(i).getApiElement().getAPIElementName() +
					OccuranceAsWordFeature.eval(tutorial, i));
	}
	public static void testSortedMapByValue()
	{
		SortedMapByValues<String, Double> map = new SortedMapByValues<String, Double>();
		
		map.put("k1", 10.0);
		map.put("k2", -1.0);
		map.put("k3", 1.0);
		
		map.printAll();
		
		Map<String, Double> subMap = map.topN(1);
		System.out.println(subMap);
		
		subMap = map.topN(2);
		System.out.println(subMap);
		
		subMap = map.topN(3);
		System.out.println(subMap);
	}
	
	public static void testEnumFeature()
	{
		Tutorial tutorial = new Tutorial();

		tutorial.LoadTutorial("tutorials/JodaTime/jodatime.csv", true, false); //

		IsInEnumFeature.testFeature(tutorial);
	}
	
	public static void javaTutStat() {
		StringBuilder str = new StringBuilder();

		Map<String, IncrementalSet<String>> pageAPIElement = new HashMap<String, IncrementalSet<String>>();
		Map<String, IncrementalSet<String>> apiElementPage = new HashMap<String, IncrementalSet<String>>();

		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(
					"tutorials/java/java_unique.csv"), ';');

			String[] nextLine;
			reader.readNext(); // skip title row
			while ((nextLine = reader.readNext()) != null) {
				String url = nextLine[0].substring(0,
						nextLine[0].lastIndexOf("/"));
				String xpath = nextLine[1];
				String content = nextLine[2];
				String simple_name = nextLine[3];
				String fqn = nextLine[4];
				String kind = nextLine[5];

				IncrementalSet<String> apis;
				if (pageAPIElement.containsKey(url))
					apis = pageAPIElement.get(url);
				else
					apis = new IncrementalSet<String>();

				apis.put(fqn);
				pageAPIElement.put(url, apis);

				IncrementalSet<String> pages;
				if (apiElementPage.containsKey(fqn))
					pages = apiElementPage.get(fqn);
				else
					pages = new IncrementalSet<String>();

				pages.put(url);
				apiElementPage.put(fqn, pages);
			}
			reader.close();

			for (String url : pageAPIElement.keySet()) {
				IncrementalSet<String> set = pageAPIElement.get(url);
				double sum = 0;
				for (String api : set.getAllKeys())
					sum += set.getValue(api);

				str.append(url + "," + set.getAllKeys().size() + "," + sum
						+ "\n");
			}

			for (String api : apiElementPage.keySet()) {
				IncrementalSet<String> set = apiElementPage.get(api);
				double sum = 0;
				for (String url : set.getAllKeys())
					sum += set.getValue(url);
				str.append(api + "," + set.getAllKeys().size() + "," + sum
						+ "\n");
			}

			// Utils.saveFile(str.toString(), "tutorials/java/java_stat.csv");

			IncrementalSet<String> set = apiElementPage.get("essential/io");
			double sum = 0;
			for (String url : set.getAllKeys())
				System.out.println(url + "-" + set.getValue(url));

		} catch (FileNotFoundException e) {
			System.out.println("path is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testLF() throws IOException {
		String filepath = "C:/Users/gaya/prog/workspace/TutorialRecommender/tutorials/smack/docs/merged/disco.html";
		Document doc = Jsoup.parse(Utils.readFileAsString(filepath));

		Utils.saveFile(doc.html(), filepath);
	}

	public static void testRegexReplace() {
		Pattern pattern = Pattern.compile("(>.*?)(dog)(.*?<)", Pattern.DOTALL);
		Matcher m = pattern.matcher(">The dog says meow. <"
				+ ">All dogs- say meow.<");

		StringBuffer newstr = new StringBuffer();
		while (m.find())
			m.appendReplacement(newstr, m.group(1) + "cat" + m.group(3));

		System.out.println(newstr.toString());
	}

	public static void testXPath() throws IOException, Exception {
		String xmlPath = "../APIDoc/data/xml_NLP/jodatime.xml";
		XMLDocAPI xmldoc = new XMLDocAPI(xmlPath);

		List<String> lemmas = xmldoc.getTokenLemmas("org.joda.time.Chronology");
		for (int i = 0; i < lemmas.size(); i++) {
			System.out.println(lemmas.get(i));
		}
	}

	public static void outputTutorialSectionLengths() {
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);

		for (String title : tutorial.sectionElementsPerSection.keySet()) {
			ArrayList<SectionElement> sections = tutorial.sectionElementsPerSection
					.get(title);
			int numberofWords = sections.get(0).getSection().getNumberofWords();
			System.out.println(numberofWords + "," + sections.size());
		}
	}

	public static void testCLTTagger() {
		String lInput = "CLT a javaclass to  a schematype   .";
		List<CoreMap> sents = Utils.getSentences(lInput, true);

		for (CoreMap sent : sents) {
			for (CoreLabel token : sent.get(TokensAnnotation.class))
				System.out.print(token.get(TextAnnotation.class) + "/"
						+ token.get(PartOfSpeechAnnotation.class) + " ");
			System.out.println("\n");
		}

	}

	protected static void crawlJavaSpec() throws IOException {
		String dirPath = "C:/Users/gaya/prog/ebooks/javaspec/";
		Utils.crawler(
				"http://docs.oracle.com/javase/specs/jls/se7/html/index.html",
				dirPath);
		TutorialProcessing.makeSentList(dirPath,
				"C:/Users/gaya/prog/ebooks/javaspec_sentlist.txt");
		TutorialProcessing.makeWordList(dirPath,
				"C:/Users/gaya/prog/ebooks/javaspec_wordlist.csv");
	}

	public static void testChunker() {
		String libDir = "/diskless/swevo-2/gpetro6/libs/";

		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = new FileInputStream(libDir
					+ "apache-opennlp-1.5.2-incubating/en-chunker.bin");
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		ChunkerME chunker = new ChunkerME(model);

		String sent[] = new String[] { "Rockwell", "International", "Corp.",
				"'s", "Tulsa", "unit", "said", "it", "signed", "a",
				"tentative", "agreement", "extending", "its", "contract",
				"with", "Boeing", "Co.", "to", "provide", "structural",
				"parts", "for", "Boeing", "'s", "747", "jetliners", "." };

		String pos[] = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN",
				"VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN",
				"IN", "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP",
				"POS", "CD", "NNS", "." };

		String[] tag = chunker.chunk(sent, pos);

		for (String t : tag)
			System.out.println(t);
	}

	protected static void reltypes() {
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);

		IncrementalSet<String> tutorialposrels = new IncrementalSet<String>();
		IncrementalSet<String> tutorialnegrels = new IncrementalSet<String>();

		for (int i = 0; i < tutorial.sectionElements.size(); i++) {
			Set<String> rels = RelTypeFeature.eval(tutorial, i);
			String relString = "";
			// for(String rel:rels)
			// {
			// if (rel.contains("prep"))
			// relString += "-prep";
			// else
			// relString += "-" +rel;
			// }

			if (tutorial.sectionElements.get(i).getLabel())
				for (String rel : rels)
					tutorialposrels.put(rel);
			else
				for (String rel : rels)
					tutorialnegrels.put(rel);
		}

		tutorialposrels.dump("dumps/jodatime_pos.csv", false);
		tutorialnegrels.dump("dumps/jodatime_neg.csv", false);
	}

	protected static void numberOfOverLappingWords(int top) {
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);

		HashSet<String> tutorialWords = new HashSet<String>();
		for (String title : tutorial.sectionElementsPerSection.keySet()) {
			String text = tutorial.sectionElementsPerSection.get(title).get(0)
					.getSection().getText();
			List<CoreLabel> wordList = Utils.getWordList(text, true);

			for (CoreLabel token : wordList)
				tutorialWords.add(token.get(TextAnnotation.class));
		}

		HashSet<String> topwords = new HashSet<String>();
		CSVReader reader;
		int i = 0;
		try {
			reader = new CSVReader(new FileReader(
					"C:/Users/gaya/prog/ebooks/javaspec_wordlist_ordered.csv"));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null && i < top) {
				topwords.add(nextLine[0]);
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Finding overlapping words in top " + top);
		System.out.println("----------------------------");

		int overlapNum = 0;
		for (String word : tutorialWords) {
			if (topwords.contains(word)) {
				System.out.println(word);
				overlapNum++;
			}
		}

		System.out.println("number of overlapping words is " + overlapNum);
		System.out.println();

	}

	protected static void testSoftWords() {
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/math_tutorial.csv", true, true);
		Pattern pattern = Pattern
				.compile("[A-Za-z][A-Za-z0-9]+(\\.[A-Za-z][A-Za-z0-9]+)+");

		String prev = "";
		for (SectionElement pair : tutorial.sectionElements) {
			if (!prev.equals(pair.getSection().getTitle())) {
				prev = pair.getSection().getTitle();
				String a = Utils.replaceCLTDelimiter(pair.getSection()
						.getHTML());

				// String text = pair.getProcessedSectionText();
				//
				// Matcher matcher = pattern.matcher(text);
				//
				// while(matcher.find())
				// {
				// System.out.println(matcher.group());
				// }

			}
		}
	}

	protected static void testSplitWords() {
		System.out.println(Utils.tokenizeTitle("SubSection3.2-Property"));
	}

	//
	// protected static void testSentenceDetector()
	// {
	// Tutorial tutorial = new Tutorial();
	// tutorial.LoadTutorial("tutorials/math_tutorial.csv");
	//
	// for (SectionElement pair:tutorial.sectionElements)
	// {
	// List<String> sentences = pair.getAPIElementSentences();
	//
	// for (String sent:sentences)
	// System.out.println(sent);
	// System.out.println("-------------");
	// }
	// }

	protected static List<TutorialSection> LoadSectionInfo() {
		String sectionInfoPath = "tutorials/math_tutorial.csv";
		List<TutorialSection> Labels = new ArrayList<TutorialSection>();

		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(sectionInfoPath), ',');

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				TutorialSection s = new TutorialSection(
						nextLine[0].equals("labeled"), nextLine[1],
						nextLine[2], nextLine[3], nextLine[4], nextLine[5],
						nextLine[6]);
				Labels.add(s);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(sectionInfoPath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Labels;
	}

	private static void calcKindStat(List<TutorialSection> Labels) {
		for (TutorialSection section : Labels) {
			if (section.kind.toLowerCase().equals("class")) {
				if (Character.isLowerCase(section.getAPIElement().charAt(0)))
					System.out.println(section.getAPIElement() + " knd is "
							+ section.kind);
			} else {
				if (Character.isUpperCase(section.getAPIElement().charAt(0)))
					System.out.println(section.getAPIElement() + " knd is "
							+ section.kind);
			}
		}
	}

	public static void testSelect() {
		String html = "<p>An <a href='http://example.com/'><b>example</b></a> link and after that is a second link called <a href='http://example2.com/'><b>example2</b></a></p> <p>testing select</p>";
		Document doc = Jsoup.parse(html);
		Element test = doc;
		test = test.select("p").get(0).select("a[href]").get(0);
		// for (Element link : links) {
		// doc.select("a").unwrap();
		// }
		System.out.println(doc.html());
	}

	public static void testPrepend() throws IOException {
		Document doc = Jsoup.parse(Utils
				.readFileAsString("tutorials/prependtest.txt"));
		List<Element> elements = doc.body().select(">*");
		Elements titles = doc.getElementsByTag("h2");

		// System.out.println(titles.get(0).toString());
		// System.out.println("------------");
		// titles.get(0).remove();

		List<Element> newelements = new ArrayList<Element>();
		Element title = new Element(titles.get(0).tag(), "");
		title.text(titles.get(0).text());
		for (Element el : elements) {
			Document tmpdoc = new Document("");
			tmpdoc.append(titles.get(0).toString());
			tmpdoc.append(el.toString());
			// System.out.println(tmpdoc.toString());
			// System.out.println("------------");

			newelements.add(tmpdoc);
		}

		for (Element el : newelements) {
			System.out.println(el.toString());
			System.out.println("------------");
		}

	}

	public static void testSectionSplit() throws IOException {
		TutorialSection section = new TutorialSection();
		section.setHTML(Utils.readFileAsString("tutorials/splittest2.txt"));
		List<Element> elements = Splitter.split(100, 50, section.getHTML(),
				new String[] {});

		for (Element el : elements) {
			System.out.println("----section length----"
					+ HTMLUtils.getHTMLWordCount(el.toString()));
			// System.out.println(el.toString());
			// System.out.println("---------------");
		}
	}

	public static void testCodeSegmentCollapse() throws IOException {
		String html = Utils.readFileAsString("tutorials/JodaTimeHTML.csv");
		Document doc = Jsoup.parse(html);

		// Collapse code segments
		int codeid = 0;
		List<String> codes = new ArrayList<String>();
		Elements snippets = doc.getElementsByClass("source");

		for (Element snippet : snippets) {
			String replaceText = "CODEID=" + codeid;
			codes.add(snippet.html());
			snippet.replaceWith(new TextNode(replaceText, ""));

			codeid++;
		}

		// removing </p>, <p> tags surrounding code segment
		html = doc.html();
		Pattern pattern = Pattern.compile("</p>\\s*(CODEID=\\d+)\\s*<p>");
		Matcher matcher = pattern.matcher(html);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String code = matcher.group(1);
			System.out.println("code is" + code);
			matcher.appendReplacement(sb, code);
		}

		// removing </p> before code segment.
		pattern = Pattern.compile("</p>\\s*(CODEID=\\d+)");
		matcher = pattern.matcher(sb.toString());
		sb = new StringBuffer();
		while (matcher.find()) {
			String code = matcher.group(1);
			System.out.println("code is: " + code);
			matcher.appendReplacement(sb, code);
		}

		// checking if code is the end of the sentence.
		// code is considered the end of the sentence if
		// 1. next symbol is upper-case
		// 2. next symbol is < as first symbol of the html tag
		pattern = Pattern.compile("(CODEID=\\d+)(\\s*)([A-Z])");
		matcher = pattern.matcher(sb.toString());
		sb = new StringBuffer();
		while (matcher.find()) {
			String code = matcher.group(1) + "." + matcher.group(2)
					+ matcher.group(3);
			System.out.println("code is: " + code);
			matcher.appendReplacement(sb, code);
		}

		pattern = Pattern.compile("(CODEID=\\d+)(\\s*)(<)");
		matcher = pattern.matcher(sb.toString());
		sb = new StringBuffer();
		while (matcher.find()) {
			String code = matcher.group(1) + "." + matcher.group(2)
					+ matcher.group(3);
			System.out.println("code is: " + code);
			matcher.appendReplacement(sb, code);
		}

		Utils.saveFile(sb.toString(), "tutorials/collapsedJT.html");
	}

	public static void testHTMLSentenceDetector() {
		try {
			InputStream modelIn = new FileInputStream(
					"opennlp_models/en-sent.bin");

			try {
				SentenceModel model = new SentenceModel(modelIn);
				SentenceDetectorME sentenceDetector = new SentenceDetectorME(
						model);

				String sentences[] = sentenceDetector
						.sentDetect("<h2>Introduction<a name=\"Introduction\"></a></h2> "
								+ "\n<p>"
								+ "\nJoda Time is like an iceberg, 9/10ths of it is invisible to user-code."
								+ "\nMany, perhaps most, applications will never need to see what's below the surface."
								+ "\nThis document provides an introduction to the Joda-Time API for the"
								+ "\naverage user, not for the would-be API developer."
								+ "\n</p>"
								+ "\n<p>"
								+ "\nThe bulk of the"
								+ "\ntext is devoted to code snippets that display the most common usage scenarios"
								+ "\nin which the library classes are used. In particular, we cover the usage of the"
								+ "\nkey <tt>DateTime</tt>, <tt>Interval</tt>, <tt>Duration</tt>"
								+ "\nand <tt>Period</tt> classes."
								+ "\n</p>"
								+ "\n<p>"
								+ "\nWe finish with a look at the important topic of formatting and parsing and a few"
								+ "\nmore advanced topics." + "\n</p>");

				for (String sent : sentences) {
					String cleanHtml = Jsoup.clean(sent, Whitelist.none())
							.replaceAll("&lt;", "").replaceAll("&gt;", "");
					System.out.println("sent: " + sent);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (IOException e) {
					}
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("sentence detector model cannot be found.");
		}

	}

}
