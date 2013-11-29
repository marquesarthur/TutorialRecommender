package Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class HTMLUtils {

	public static String removeEmptyTags(String html)
	{
		return html.replaceAll("<[a-zA-Z0-9]*></[a-zA-Z0-9]*>", "");
	}

	public static String renamePreTags(String html)
	{
		return html.replaceAll("<pre>", "<code>").replaceAll("</pre>", "</code>");
	}

	public static String removeDoubleLF(String html)
	{
		return html.replaceAll("(\\n\\s*)+", "\n");
	}

	public static String renamePreTagsXpath(String xpath)
	{
		return xpath.replaceAll("/pre", "/code");
	}

	public static Element getElementByXPath(String xpath, Document doc)
	{


		String[] tags = xpath.substring(11, xpath.length()).split("/"); // ommiting /html/body/
		Element clt = doc.body();

		int index;
		int divc = 0;
		boolean done = false;
		for(String tag:tags)
		{
			if (tag.startsWith("div"))
				divc++;
			
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
				System.out.println(xpath + " is not valid.");
				clt = null;
				break;
			}

			if (tag.equals("table"))
				clt = clt.select(">tbody").get(0);

		}

		return clt;
	}

	public static List<Integer> getCodeSnippetCLT(Element codeElement, String content, String fqn, String api, String kind, String[] titles)
	{
		List<Integer> indexes = new ArrayList<Integer>();

		if (codeElement.tagName().equals("pre"))
		{
			if (content.startsWith("M"))
			{
				content = content.split(":")[2];
			}
			else if (content.startsWith("F"))
			{
				content = content.split(":")[3];
			}
			else
			{
				String[] tmp = content.split(":")[1].split("\\.");

				if (tmp.length == 1)
					content = tmp[0];
				else
					content = tmp[1];
			}

			Pattern pattern;
			if (!kind.equals("method"))
				pattern = Pattern.compile("(?<=\\W)"+api+"(?=\\W)", Pattern.DOTALL);

			else
				pattern = Pattern.compile("(?<=\\W)"+api+"(?=\\()", Pattern.DOTALL);

			Matcher m = pattern.matcher(codeElement.html());

			while (m.find())
				indexes.add(m.regionStart());

			if (indexes.size()==0)
				System.out.println("nothing was found");
		}

		return indexes;
	}
	public static Element insertCLTTag(Element codeElement, String content, String fqn, String api, String kind, String[] titles)
	{
		if (codeElement != null)
		{
			if (!codeElement.hasText())
				System.out.println("need to be debugged");
			else
			{
				if (codeElement.tagName().startsWith("a") || codeElement.tagName().startsWith("tt") || 
						codeElement.tagName().equals("b") || codeElement.tagName().equals("i") || 
						codeElement.tagName().equals("code") || codeElement.tagName().equals("em"))
				{
					codeElement.html("<clt fqn=" + fqn + " api=" + api + " kind=" + kind + ">" + 
							codeElement.text() + "</clt>");
				}
				else 
				{
					codeElement = getTitle(codeElement, titles);

					if (codeElement != null)
						codeElement.html(codeElement.html().replaceAll("\\W" + content + "\\W", 
								"<clt fqn=" + fqn + " api=" + api + " kind=" + kind + ">" + 
										content + "</clt>"));
					else
						System.out.println("title not found.");

				}
			}
		}

		return codeElement;

	}

	public static Element getTitle(Element el, String[] titles)
	{
		for (String title:titles)			
			if (el.tagName().equals(title))
				return el;
		
		Elements subsectionTitles = new Elements();

		for(String title:titles)
			subsectionTitles.addAll(el.select(" " +title));

//		if(subsectionTitles.size() != 1)
//		{
//			System.out.println("problem with title");
//			return null;
//		}
//		else
			return subsectionTitles.get(0);
	}

	public static int getHTMLWordCount(String html)
	{
		Document doc = Jsoup.parse(html);
		Elements snippets = doc.getElementsByTag("pre");

		for(Element snippet:snippets)
			snippet.remove();

		String cleanHtml = doc.html();
		cleanHtml = Jsoup.clean(cleanHtml, Whitelist.none()).replaceAll("&lt;", "").replaceAll("&gt;", "");

		return Utils.getWordCount(cleanHtml);
	}

	public static void insertPreCLT(Map<String, List<Integer>> pretags) {
		
		
	}

}
