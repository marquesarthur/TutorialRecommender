package tutorial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import recodocpreprocessing.JavaPreprocessing;

import Utilities.HTMLUtils;
import Utilities.Utils;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import features.WordPurityFeatures;

public class TutorialProcessing {


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		preprocessSmack();
	}

	private static void preprocessVCol() throws Exception
	{
		Properties properties = new Properties();
        properties.load(new FileInputStream("settings"));
        
		String docpath = properties.getProperty("vcol_doc");
		
		String[] titles = {"h1", "h2", "h3"};
		String recodoc_output = properties.getProperty("vcol_recodoc");
		mergeSmack(recodoc_output, docpath, titles);

		String sectionattribute = "section";
		List<TutorialSection> apitutorial = setSectionInfo(docpath + "merged/", sectionattribute, titles);
		System.out.println("size:" +apitutorial.size());
		apitutorial = removeRepetitions(apitutorial);

		//List<List<TutorialSection>> tutorialSplit = selectRandomSections(apitutorial, 100);

		TutorialSection.saveToFile(apitutorial, properties.getProperty("vcol_forannotation"));

		//calcTutorialStatistics(apitutorial);

	}
	
	private static void preprocessJCol() throws Exception
	{
		Properties properties = new Properties();
        properties.load(new FileInputStream("settings"));
        
		String docpath = properties.getProperty("jcol_preproc_doc");		
		String index_filepath = docpath + "index.html";
		
		String htmlText = Utils.readFileAsString(index_filepath);
		htmlText = Utils.cleanHtml(htmlText, false);
		Utils.saveFile(htmlText, docpath + "cleaned_index.html");

		String[] titles = {"h1", "h2", "h3"};
		String recodoc_output = properties.getProperty("jcol_recodoc");
		mergeSmack(recodoc_output, docpath, titles);

		String sectionattribute = "subsection";
		List<TutorialSection> apitutorial = setSectionInfo(docpath + "merged/", sectionattribute, titles);
		System.out.println("size:" +apitutorial.size());
		apitutorial = removeRepetitions(apitutorial);

		//List<List<TutorialSection>> tutorialSplit = selectRandomSections(apitutorial, 100);

		TutorialSection.saveToFile(apitutorial, properties.getProperty("jcol_forannotation"));

		//calcTutorialStatistics(apitutorial);

	}
	
	private static void preprocessCol() throws Exception
	{
		Properties properties = new Properties();
        properties.load(new FileInputStream("settings"));
        
		String docpath = properties.getProperty("col_doc");
		String index_filepath = docpath + "toc.htm";
		
		String htmlText = Utils.readFileAsString(index_filepath);
		htmlText = Utils.cleanHtml(htmlText, false);
		//Utils.saveFile(htmlText, docpath + "cleaned_index.html");

		List<TutorialSection> tutorial = extractTableOfContent(htmlText);	
		//loadSectionsHtml(tutorial, docpath, "http://docs.oracle.com/javase/tutorial/collections/");		
		
		String[] titles = {"h1", "h2", "h3", "h4"};
		String recodoc_output = properties.getProperty("col_recodoc");

		mergeSmack(recodoc_output, docpath, titles);

		String preproc_doc = properties.getProperty("col_preproc_doc");
		JavaPreprocessing.preprocessTutorial(docpath + "merged/", preproc_doc);
		
		String sectionattribute = "subsection";
		List<TutorialSection> apitutorial = setSectionInfo(preproc_doc, sectionattribute, titles);
		System.out.println("size:" +apitutorial.size());
		apitutorial = removeRepetitions(apitutorial);

		TutorialSection.saveToFile(apitutorial, properties.getProperty("col_forannotation"));
	}
	
		private static void preprocessRMI() throws Exception
		{
			Properties properties = new Properties();
	        properties.load(new FileInputStream("settings"));
	        
			String docpath = properties.getProperty("rmi_doc");
			String index_filepath = docpath + "toc.html";
			
//			String htmlText = Utils.readFileAsString(index_filepath);
//			htmlText = Utils.cleanHtml(htmlText, false);
//			Utils.saveFile(htmlText, docpath + "cleaned_index.html");
//
//			List<TutorialSection> tutorial = extractTableOfContent(htmlText);			
//			loadSectionsHtml(tutorial, docpath);		
			
			String[] titles = {"h1", "h2", "h3", "h4"};
			String recodoc_output = properties.getProperty("rmi_recodoc");

			mergeSmack(recodoc_output, docpath, titles);

			String preproc_doc = properties.getProperty("rmi_preproc_doc");
//			JavaPreprocessing.preprocessTutorial(docpath + "merged/", preproc_doc);
			
			String sectionattribute = "subsection";
			List<TutorialSection> apitutorial = setSectionInfo(preproc_doc, sectionattribute, titles);
			System.out.println("size:" +apitutorial.size());
			apitutorial = removeRepetitions(apitutorial);

			TutorialSection.saveToFile(apitutorial, properties.getProperty("rmi_forannotation"));
		}
	
	private static void preprocessSmack() throws Exception
	{
		String docpath = "tutorials/ssmack/docs/";
		String index_filepath = docpath + "index.html";
		String htmlText = Utils.readFileAsString(index_filepath);
		htmlText = Utils.cleanHtml(htmlText, false);
		Utils.saveFile(htmlText, docpath + "cleaned_index.html");

		String[] titles = {"div[class=header]", "div[class=subheader]", "p[class=subheader]", "h3", "h4"};
		String recodoc_output = "tutorials/ssmack/smack_new1.csv";

		mergeSmack(recodoc_output, docpath, titles);

		String sectionattribute = "subsection";
		List<TutorialSection> apitutorial = setSectionInfo(docpath + "merged/", sectionattribute, titles);
		System.out.println("size:" +apitutorial.size());
		apitutorial = removeRepetitions(apitutorial);

		//List<List<TutorialSection>> tutorialSplit = selectRandomSections(apitutorial, 100);

		TutorialSection.saveToFile(apitutorial, "tutorials/ssmack/smack_tutorial.csv");

		//calcTutorialStatistics(apitutorial);

	}

	private static void preprocessMath() throws IOException
	{
		String htmlText = Utils.readFileAsString("tutorials/math_index.htm");
		htmlText = Utils.cleanHtml(htmlText, false);
		Utils.saveFile(htmlText, "tutorials/clean_math_index.html");

		List<TutorialSection> tutorial = extractTableOfContent(htmlText);
		String docpath = "tutorials/math/";
		//loadSectionsHtml(tutorial, docpath);		
		//mergeRecodocResults("tutorials/math_unique.csv", docpath);

		String sectionattribute = "section";
		String[] titles = {"h2", "h3", "h4"};
		List<TutorialSection> apitutorial = setSectionInfo("tutorials/math/merged/", sectionattribute, titles);
		System.out.println("size:" +apitutorial.size());
		apitutorial = removeRepetitions(apitutorial);

		//List<List<TutorialSection>> tutorialSplit = selectRandomSections(apitutorial, 100);

		TutorialSection.saveToFile(apitutorial, "tutorials/math_tutorial.csv");

		//calcTutorialStatistics(apitutorial);
	}
	private static List<List<TutorialSection>> selectRandomSections(
			List<TutorialSection> apitutorial, int selectCount) {
		List<TutorialSection> traintList = new ArrayList<TutorialSection>();
		List<TutorialSection> testList = new ArrayList<TutorialSection>();

		Random randomGen = new Random();

		int sum = 0;
		while (sum < selectCount)
		{
			String sectionTitle = apitutorial.get(0).sectionTitle;
			String subsectionTitle = apitutorial.get(0).subsectionTitle;
			int index = 0;
			while(index < apitutorial.size() && apitutorial.get(index).sectionTitle.equals(sectionTitle))
			{
				int numberofSubsections = randomGen.nextInt(getNumberOfSubsections(apitutorial, sectionTitle)) + 1 ;
				int numberofCopied = 0;
				while (numberofCopied < numberofSubsections)
				{
					numberofCopied++;
					while(index < apitutorial.size() && apitutorial.get(index).subsectionTitle.equals(subsectionTitle))
					{
						traintList.add(apitutorial.get(index++));
						sum++;
					}
					subsectionTitle = apitutorial.get(index).subsectionTitle;
				}
				sectionTitle = apitutorial.get(index).sectionTitle;

			}
			randomGen.nextInt(2);
		}
		List<List<TutorialSection>> split = new ArrayList<List<TutorialSection>> ();
		split.add(traintList);
		split.add(testList);

		return split;
	}

	private static int getNumberOfSubsections(
			List<TutorialSection> apitutorial, String sectionTitle) {
		int index = 0;
		int count = 0;

		while (index < apitutorial.size() && !apitutorial.get(index++).sectionTitle.equals(sectionTitle));

		String prevSubsectionTitle = apitutorial.get(index).subsectionTitle;
		while (index < apitutorial.size() && apitutorial.get(index).sectionTitle.equals(sectionTitle))
		{
			count++;
			while (index < apitutorial.size() && apitutorial.get(index++).subsectionTitle.equals(prevSubsectionTitle));
			prevSubsectionTitle  = apitutorial.get(index).subsectionTitle;
		}

		return count;
	}


	private static int AVGLength = 150;
	private static int MARGIN = 70;




	public static void calcTutorialStatistics(List<TutorialSection> tutorial) throws IOException
	{
		HashMap<String, List<Integer>> hmap = new HashMap<String, List<Integer>>();
		HashMap<String, List<String>> sectionclasses = new HashMap<String, List<String>>();
		for(TutorialSection section:tutorial)
		{	
			List<Integer> stat = hmap.get(section.getTitle());
			if (stat == null)
			{
				stat = new ArrayList<Integer>();
				stat.add(0); //class
				stat.add(0); //method
				stat.add(0); //method with class
				stat.add(0); //field
				stat.add(0); //other
			}

			if(section.kind.equals("class"))
				stat.set(0, stat.get(0)+1);
			else if (section.kind.equals("method"))
			{
				stat.set(1, stat.get(1)+1);

				String className = section.getClassName();
				List<String> classes = sectionclasses.get(section.getTitle());
				if (classes != null && classes.contains(className))
					stat.set(2, stat.get(2)+1);
				else if (classes != null)
					classes.add(className);
				else
				{
					classes = new ArrayList<String>();
					classes.add(className);
				}
				sectionclasses.put(section.getTitle(), classes);
			}
			else if (section.kind.equals("field"))
				stat.set(3, stat.get(3)+1);
			else 
				stat.set(4, stat.get(4)+1);

			hmap.put(section.getTitle(), stat);			
		}

		CSVWriter writer = new CSVWriter(new FileWriter("tutorials/tutorial_stat.csv", true));
		for(String key:hmap.keySet())
		{
			List<Integer> stat = hmap.get(key); 
			writer.writeNext(new String[] {key, Integer.toString(stat.get(0)), Integer.toString(stat.get(1)), 
					Integer.toString(stat.get(2)), Integer.toString(stat.get(3)), Integer.toString(stat.get(4))});
		}
		writer.close();
	}


	public static List<TutorialSection> extractTableOfContent(String html)
	{
		List<TutorialSection> tutorial = new ArrayList<TutorialSection>();

		Document doc = Jsoup.parse(html);
		Elements links = doc.select("a");

		for (Element link:links)
		{
			String title = link.text();
			title = title.replaceAll("0\\.\\s", "").replace("/", ".");
			String url = link.attr("href").split("#")[0];
			tutorial.add(new TutorialSection(title, url));
			//System.out.println(url);
		}
		return tutorial;
	}

	public static void loadSectionsHtml(List<TutorialSection> tutorial, String docpath, String baseURL) throws IOException
	{
		for (TutorialSection section:tutorial)
		{	
			Document doc = Jsoup.connect(baseURL + section.getURL()).get();

			File f = new File(docpath + Utils.getPageRoot(section.getURL()));
			if (!f.exists())
				f.mkdirs();
			Utils.saveFile(doc.html(), docpath + section.getURL());
		}
	}

	static int sectionCount = 0;
	static int splitcount = 0;
	static int initCount = 0;
	
	public static List<TutorialSection> setSectionInfo(String htmlDir, String sectionAtt, String[] titles) throws IOException
	{


		List<TutorialSection> apitutorial = new ArrayList<TutorialSection>();

		File dir = new File(htmlDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				int numberofclt = 0;
				String cleanHTML = Utils.cleanHtml(Utils.readFileAsString(child.getAbsolutePath()), false); 
				Document sectiondoc = Jsoup.parse(cleanHTML);

				Elements divs = sectiondoc.select("div." + sectionAtt);
				initCount +=divs.size();
				
				Element parentSection = null;
				Element currentSection;
				for (Element div:divs)
				{
					currentSection = null;

					if(!div.select(" " + titles[0]).isEmpty())
					{
						parentSection = div.select("  " + titles[0]).get(0);
						currentSection = parentSection;						
					}

					if (!div.select(" " + titles[1]).isEmpty() )
						currentSection = div.select(" " + titles[1]).get(0);

//					if (!div.select(" " + titles[2]).isEmpty() )
//						currentSection = div.select(" " + titles[2]).get(0);

					if (currentSection != null)
					{
						//if section is too big then we need to split it.
						List<Element> newsections =  new ArrayList<Element>();
						if (HTMLUtils.getHTMLWordCount(div.html()) > AVGLength + MARGIN)
						{
							newsections.addAll(Splitter.split(AVGLength, MARGIN, div.html(), titles));						
						}
						else
							newsections.add(div);

						//						dumpSplittedTutorial(newsections, "tutorials/math_splitted");	

						if (newsections.size() < 1)
							System.out.println("boo");
						splitcount+=newsections.size();
						
						for (Element el:newsections)
						{
							String title = Splitter.splitsubsectionTitle(el, titles).text();

							Elements clts = el.getElementsByTag("clt");
							numberofclt+=clts.size();

							if (clts.size()>0)
								sectionCount++;


							for (Element clt:clts)
							{	
								String kind = clt.attr("kind"); 
								//								if(!clt.attr("kind").toLowerCase().equals("method") && Character.isLowerCase(clt.attr("api").charAt(0)))
								//									kind = "method";
								//								else if (!clt.attr("kind").toLowerCase().equals("field") && Character.isUpperCase(clt.attr("api").charAt(0)))
								//									kind  = "class";

								if (!kind.equals("class") )
								{
									String classname = Utils.getClassName(clt.attr("fqn"), kind);
									clt.text(clt.text().replaceAll(clt.attr("api"), classname + "." + clt.attr("api")));
									clt.attr("api", classname);
									Tutorial.updateTutorial(apitutorial, title, parentSection==null?"":parentSection.html(), el.html());
									kind = "class";
								}


								apitutorial.add(new TutorialSection(parentSection == null ? "":parentSection.html(),title, el.html(), clt.attr("api"), clt.attr("fqn"), kind));
							}

//							Elements pres = el.getElementsByTag("pre");
//
//							if (pres.size()>0)
//								sectionCount++;
	
						}

					}
					else
						System.out.println("boo");
				}
				
				System.out.println(child.getName() + "," + numberofclt);

			}
			else
				apitutorial.addAll(setSectionInfo(child.getAbsolutePath(), sectionAtt, titles));

		System.out.println("section count is " + sectionCount);

		System.out.println("number of initial sections" + initCount);
		
		System.out.println("Split count" + splitcount);
		
		return apitutorial;

	}

	public static String getSectionNumber(String title)
	{
		return title.split(" ")[0];
	}


	private static void mergeSmack(String recodocfilepath, String htmlDir, String[] titles) throws Exception
	{	
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(recodocfilepath), ';');

			reader.readNext(); // skip column titles

			String[] nextLine;
			int linecount = 0;
			int cltcount = 0;
			while ((nextLine = reader.readNext()) != null) {				
				String url = nextLine[0];
				String xpath = nextLine[1];
				String content = nextLine[2];
				String simple_name = nextLine[3];
				String fqn = nextLine[4];
				String kind = nextLine[5];
				String isAbstract = nextLine[6];

				if (!xpath.contains("/pre"))
				{
					//System.out.println(url + "-" + xpath);
					linecount++;

					// if merged version exists then update merged file.
					String mergedUrl = htmlDir + "merged/" + url;
					System.out.println(url);
					File f = new File(mergedUrl);
					
					String html = "";
					if (f.exists())
						html = Utils.readFileAsString(mergedUrl);
					else		
					{
						File f1 = new File(htmlDir + url);
						if (f1.exists())
							html = Utils.readFileAsString(htmlDir + url);
					}
					if (!html.equals(""))
					{
						html = HTMLUtils.removeDoubleLF(HTMLUtils.removeEmptyTags(html));
	
						Document doc = Jsoup.parse(html);
	
						Element codeElement = HTMLUtils.getElementByXPath(xpath, doc);
						HTMLUtils.insertCLTTag(codeElement, content, fqn, simple_name, kind, titles);
						
						File mergesubdir = new File(htmlDir + "merged/" + Utils.getPageRoot(url));
						if (!mergesubdir.exists())
							mergesubdir.mkdirs();
						
						Utils.saveFile(doc.html(), htmlDir + "merged/" + url);
	
						cltcount++;
					}
				}
			}

			System.out.println("number of recodoc lines processed is " + linecount);
			System.out.println("number of clt inserted is " + cltcount);

			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(recodocfilepath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public static void mergeRecodocResults(String recodocResults, String htmlDir) throws IOException
	{
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(recodocResults), ';');

			reader.readNext();
			String[] nextLine;
			int linecount = 0;
			int cltcount = 0;
			while ((nextLine = reader.readNext()) != null) {
				String url = Utils.getPageName(nextLine[0]);
				linecount++;

				File f = new File(htmlDir + "merged/" + url);
				// load and remove empty tags
				String html;
				if (f.exists())
					html = Utils.readFileAsString(htmlDir + "merged/" + url).replaceAll("<[a-zA-Z0-9]*></[a-zA-Z0-9]*>", "");
				else					
					html = Utils.readFileAsString(htmlDir + url).replaceAll("<[a-zA-Z0-9]*></[a-zA-Z0-9]*>", "");
				Document doc = Jsoup.parse(html);

				String xpath = nextLine[1];
				String[] tags = xpath.substring(11, xpath.length()).split("/"); // ommiting /html/body/
				Element clt = doc.body();

				int index;
				for(String tag:tags)
				{
					index = 0;
					int bindex = tag.indexOf('[');
					int eindex = tag.indexOf(']');
					if (bindex != -1)
					{
						index = Integer.parseInt(tag.substring(bindex+1, eindex))-1;
						tag = tag.substring(0, bindex);
					}

					try
					{
						clt = clt.select(">" + tag).get(index);
					}
					catch(IndexOutOfBoundsException ex)
					{
						System.out.println(xpath + " for " + nextLine[2] + " is not valid.");
						clt = null;
						break;
					}

					if (tag.equals("table"))
						clt = clt.select(">tbody").get(0);

				}

				if (clt != null)
				{
					if (!clt.hasText())
						System.out.println("need to be debugged");
					else
					{
						if(tags[tags.length - 1].startsWith("a") || tags[tags.length - 1].startsWith("tt"))
							clt.html("<clt fqn=" + nextLine[7] + " api=" + nextLine[6] + " kind=" + nextLine[5] + ">" + 
									clt.text() + "</clt>");
						else
						{
							Element title = null;
							if (tags[tags.length-1].endsWith("]"))
							{
								Elements titles = clt.select(">h3");
								if(titles.size() == 0)
									titles = clt.select(">h4");
								title = titles.get(0);
							}
							else
								title = clt.select(">h2").get(0);

							if (title != null)
								title.html(title.html().replace(nextLine[2], 
										"<clt fqn=" + nextLine[7] + " api=" + nextLine[6] + " kind=" + nextLine[5] + ">" + 
												nextLine[2] + "</clt>"));
							else
								System.out.println("title not found.");

						}
						cltcount++;

						Utils.saveFile(doc.html(), htmlDir + "merged/" + url);
					}
				}
			}

			System.out.println("number of recodoc lines processed is " + linecount);
			System.out.println("number of clt inserted is " + cltcount);

			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(recodocResults + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<TutorialSection> removeRepetitions(List<TutorialSection> apitutorial)
	{
		List<TutorialSection> uniquetutorial = new ArrayList<TutorialSection>();
		int sum = 0;
		for(TutorialSection section: apitutorial)
		{
			if(!uniquetutorial.contains(section))
				uniquetutorial.add(section);
			else
				sum++;
		}

		System.out.println("number of removed rows is " + sum);
		return uniquetutorial;
	}





	// for test
	private static void dumpSplittedTutorial(List<Element> el, String saveFilePath) throws IOException	
	{
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(saveFilePath, true));
			for (Element section:el)
				writer.writeNext(new String[] {"1", section.toString()});			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			writer.close();
		}

	}



	public static void makeSentList(String dirPath, String output) throws IOException
	{
		FileWriter fstream = new FileWriter(output);
		BufferedWriter out = new BufferedWriter(fstream);

		File dir = new File(dirPath);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				System.out.println("Splitting to sentences " + child.getAbsolutePath() + "...");
				Document doc = Jsoup.parse(Utils.readFileAsString(child.getAbsolutePath()));

				String text = Utils.HTML2Text(Utils.addPunctuationHTML(doc.html()));
				List<CoreMap> sentCoreMaps = Utils.getSentences(text, false);

				for (CoreMap sent:sentCoreMaps)
				{
					sent.toString();
					out.write(sent.toString() + "\n");
				}

			}
		out.close();

	}

	public static void makeWordList(String dirPath, String output) throws IOException
	{
		HashMap<String, Double> wordsPerTutorial = new HashMap<String, Double>();
		HashMap<String, String> wordtags = new HashMap<String, String>();

		File dir = new File(dirPath);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				System.out.println("Splitting to tokens " + child.getAbsolutePath() + "...");
				Document doc = Jsoup.parse(Utils.readFileAsString(child.getAbsolutePath()));

				String text = Utils.HTML2Text(Utils.addPunctuationHTML(doc.html()));
				List<CoreLabel> wordList = Utils.getWordList(text, true);

				for(CoreLabel token:wordList)
				{
					String word = token.get(TextAnnotation.class);
					WordPurityFeatures.incrementWordCount(word, wordsPerTutorial, 1.0);		
					wordtags.put(word, token.get(PartOfSpeechAnnotation.class));
				}

			}
		CSVWriter writer = new CSVWriter(new FileWriter(output, false));

		for(String word:wordsPerTutorial.keySet())
			writer.writeNext(new String[] {word, wordsPerTutorial.get(word).toString(), wordtags.get(word)});

		writer.close();

	}

}


