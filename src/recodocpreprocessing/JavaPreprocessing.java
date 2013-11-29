package recodocpreprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import Utilities.Utils;

// in Smack tutorial sections are separated by titles and are not included in any tag
// need to be fixed for Recodoc
public class JavaPreprocessing {

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		Properties properties = new Properties();
        properties.load(new FileInputStream("settings"));
        String inputdir = properties.getProperty("rmi_doc");
        String outputdir = properties.getProperty("rmi_preproc_doc");
        
        preprocessTutorial(inputdir, outputdir);
	}
	
	public static void preprocessTutorial(String inputdir, String outputdir) throws IOException
	{
		File output = new File(outputdir);
		if (!output.exists())
			output.mkdirs();

		File dir = new File(inputdir);
		for (File child : dir.listFiles()) 
			if(child.isFile())
			{
				String improvedHTML = preprocessFile(child.getAbsolutePath());
				Utils.saveFile(improvedHTML, output.getAbsolutePath() + File.separator + child.getName());
			}
			else
			{
				preprocessTutorial(child.getAbsolutePath(), output.getAbsolutePath() + File.separator + child.getName());
			}
	}
	
	public static String preprocessFile(String filepath) throws IOException
	{	
		String html = addSectionDiv(Utils.readFileAsString(filepath));
		
		return html; 
	}
	
	public static String addSectionDiv(String html)
	{	
		Pattern pattern = Pattern.compile("(<(h2|h3|h4|div id=\"PageTitle\")>.*?</(h2|h3|h4|div)>.*?)(?=(<(h2|h3|h4)>.*?</(h2|h3|h4)>)|<div id=\"foot.*?\">|</body>|$)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		
		Matcher m = pattern.matcher(html);		
		while (m.find())
		{
			html = html.replace(m.group(1),"<div class=\"subsection\">\n"+m.group(1)+"</div>\n");	
		}
		
		return html;
	}
}
