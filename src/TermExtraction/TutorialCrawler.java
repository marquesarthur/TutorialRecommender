package TermExtraction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import Utilities.Utils;
import edu.stanford.nlp.util.CoreMap;


public class TutorialCrawler {
	
	public static void main(String[] args)
	{
		downloadTutorial();
	}
	
	public static void downloadTutorial()
	{
		String tutorialURL = "http://docs.oracle.com/javase/tutorial/index.html";
		String saveDir = "tutorials/JavaTutorials/Html/";
		String cleanHTMLDir = "tutorials/JavaTutorials/CleanHTML/";
		String plainTextDir = "tutorials/JavaTutorials/PlainText/";
		String filteredDir = "tutorials/JavaTutorials/filtered/";
		
		try {
			Utils.crawler(tutorialURL, saveDir);
			cleanHTML(saveDir, cleanHTMLDir);
			//HTMLToText(cleanHTMLDir, plainTextDir);
			filteredSentences(cleanHTMLDir, filteredDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanHTML(String htmlDir, String outputDir) throws IOException
	{
		File outdir = new File(outputDir);
		if (!outdir.exists())
			outdir.mkdirs();
		
		File dir = new File(htmlDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{	
				if (child.getAbsolutePath().endsWith(".html"))
				{
					String cleanHTML = Utils.cleanHtml(Utils.readFileAsString(child.getAbsolutePath()), false);
					//cleanHTML = Pattern.compile("<tt>.*?</tt>", Pattern.DOTALL).matcher(cleanHTML).replaceAll("cltnoun ");
					Utils.saveFile(cleanHTML, Utils.combinePaths(outputDir ,Utils.getPageName(child.getAbsolutePath())));
				}
			}
			else
				cleanHTML(child.getAbsolutePath(), Utils.combinePaths(outputDir ,Utils.getPageName(child.getAbsolutePath())));
	}
	
	public static void filteredSentences(String htmlDir, String outputDir) throws IOException
	{	

		
		String filteredSents = "";
		File dir = new File(htmlDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				String cleanHtml = Utils.readFileAsString(child.getAbsolutePath());
				cleanHtml = Pattern.compile("<tt>", Pattern.DOTALL).matcher(cleanHtml).replaceAll("<tt>clt_");
				String plainText = Utils.HTML2Text(Utils.addPunctuationHTML(cleanHtml));
				
				List<CoreMap> sentences = Utils.getSentences(plainText, false);
				for (CoreMap sent:sentences)
				{
					if (sent.toString().contains("clt"))
					{
						String senttext = sent.toString().replaceAll("\n", "  ") + "\n";
						filteredSents +=senttext;
					}
				}
				
			}
		Utils.saveFile(filteredSents, outputDir + "filteredSentswithCLT.txt");
	}
	
	public static void HTMLToText(String htmlDir, String outputDir) throws IOException
	{
		File outdir = new File(outputDir);
		if (!outdir.exists())
			outdir.mkdirs();
		
		File dir = new File(htmlDir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				String cleanHTML = Utils.readFileAsString(child.getAbsolutePath());
				cleanHTML = Pattern.compile("<tt>.*?</tt>", Pattern.DOTALL).matcher(cleanHTML).replaceAll("cltnoun ");
				String plainText = Utils.HTML2Text(Utils.addPunctuationHTML(cleanHTML));
				
				Utils.saveFile(plainText, Utils.combinePaths(outputDir ,Utils.getPageName(child.getAbsolutePath())).replace("html", "txt"));
			}
			else
				HTMLToText(child.getAbsolutePath(), Utils.combinePaths(outputDir ,Utils.getPageName(child.getAbsolutePath())).replace("html", "txt"));
	}
}
