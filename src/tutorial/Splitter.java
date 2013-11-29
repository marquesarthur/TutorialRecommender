package tutorial;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import Utilities.HTMLUtils;
import Utilities.Utils;

public class Splitter {
	public static List<Element> split(int avgLength, int margin, String html, String[] titles) {

		Document doc = Jsoup.parse(html);
		Element body = doc.body();
		
		Element subsectionTitlenode = splitsubsectionTitle(body, titles); 

		// select 1st level children???? maybe I should get all leaves of the html tree????
		Elements children = body.select(">*"); 

		if (children.size() == 0)
			System.out.println("section length is bigger then avg and has only one child: Needs to be dubugged");

		// maybe there are many nested divs, but return tags when they inclose TextNode
		List<Node> childNodes = new  ArrayList<Node>(children);

		if (childNodes.size() == 1)
		{
			List<Node> singlenode = new ArrayList<Node>(childNodes);

			while(singlenode.size() == 1 && (singlenode.get(0) instanceof TextNode))
			{
				singlenode = singlenode.get(0).childNodes();
			}

			if (!(singlenode.get(0) instanceof TextNode))
				childNodes =  singlenode;
		}

		// split all section elements to parts smaller than margin. 
		// So that it is not possible to make bigger chunk than avgLength + margin
		List<Node> splittedList = new ArrayList<Node>();
		childNodes = cleanNodeList(childNodes);

		for (Node node:childNodes)
			splittedList.addAll(splitNode(node, margin));

//		System.out.println("-------------------");

		List<Element> subsectionTitledList = new ArrayList<Element>(); 
		if (splittedList.size() > 0)
		{
			List<Node> mergedList = mergeByLogic(splittedList);

			List<Element> remergedList = remergeNodes(avgLength, margin, mergedList);

			if (subsectionTitlenode!= null)
			{
				Document tmpdoc = new Document("");
				tmpdoc.append(remergedList.get(0).toString());
				subsectionTitledList.add(tmpdoc);

				for (int i=1;i<remergedList.size(); i++)
				{
					tmpdoc = new Document("");

					tmpdoc.append("<h3>" + subsectionTitlenode.text()+" - part " + (i+1) + "</h3>");
					tmpdoc.append(remergedList.get(i).toString().replace("<dt>", "<h4>").replace("</dt>", "</h4>"));

					subsectionTitledList.add(tmpdoc);
				}
			}
		}
		else
			System.out.println("happened");
		return subsectionTitledList;
	}
	
	// merge nodes together as one unit by html structure logic
		// 1. <dt> should be merged with following <dd>s
		// 2. <ul> should be merged with previous text
		private static List<Node> mergeByLogic(List<Node> nodelist)
		{
			List<Node> mergedNodes = new ArrayList<Node>();

			int i=0;
			while(i < nodelist.size())
			{

				// merge <dt> tags with all following <dd>s tag if they are separated with only <br> or empty TextNode tags
				Node currentNode = nodelist.get(i);
				if (currentNode instanceof Element && ((Element) currentNode).tagName().equals("dt") )
				{	
					Document doc = new Document("");
					doc.append(currentNode.toString());

					i++;	// to start with next node

					while (i < nodelist.size() && (nodelist.get(i) instanceof Element && ((Element) nodelist.get(i)).tagName().equals("dd")))
						doc.append(nodelist.get(i++).toString());
					mergedNodes.add(doc);				
				}
				else
				{
					mergedNodes.add(currentNode);
					i++;
				}
			}
			
			List<Node> mergedNodes2 = new ArrayList<Node>();
			
			Node prevNode = mergedNodes.get(0);
			Node currentNode = mergedNodes.get(0);
			i=1;
			while(i < mergedNodes.size())
			{

				currentNode = mergedNodes.get(i);
				if (currentNode instanceof Element && ((Element) currentNode).tagName().equals("ul") )
				{	
					Document doc = new Document("");
					doc.append(prevNode.toString());
					doc.append(currentNode.toString());
					
					mergedNodes2.add(doc);
					
					if (i+1 < mergedNodes.size())
					{
						prevNode = mergedNodes.get(++i);
						currentNode = prevNode;
					}
				}
				else
				{
					mergedNodes2.add(prevNode);
					prevNode = currentNode;
				}
				i++;
			}
			
			if (prevNode == currentNode)
				mergedNodes2.add(prevNode);
			
			
			return mergedNodes2;
		}


		// split individual html node to parts smaller than limit. 
		private static List<Node> splitNode(Node nodeToSplit, int limit)
		{
			List<Node> splittedNodes = new ArrayList<Node>();	

			int wordCount = HTMLUtils.getHTMLWordCount(nodeToSplit.toString()); 
			if(wordCount > limit)
			{
				List<Node> childrenList = nodeToSplit.childNodes();
				childrenList = cleanNodeList(childrenList);

				if (!(nodeToSplit instanceof TextNode)&& 
						(!((Element)nodeToSplit).tagName().equals("table")) &&
						(!((Element)nodeToSplit).tagName().equals("ul")) && 
						(!((Element)nodeToSplit).tagName().equals("dd")) &&
						(!((Element)nodeToSplit).tagName().equals("dt")) &&
						!((Element)nodeToSplit).tagName().equals("p")) 
				{

					if (childrenList.size() >= 1)
						for(Node child:childrenList)
							splittedNodes.addAll(splitNode(child, limit));
					else // don't know can this happen?
					{
						splittedNodes.add(nodeToSplit);
						System.out.println("ATTANTION: this happened");
					}
				}
				else // add directly if it is dd, dt, table, ul, p tags
					splittedNodes.add(nodeToSplit);
			}
			else 
				splittedNodes.add(nodeToSplit);

			return splittedNodes;
		}
		
		private static List<Node> cleanNodeList(List<Node> nodeList)
		{
			List<Node> cleanList = new ArrayList<Node>();
			for(Node node:nodeList)
			{
				if (!(node instanceof Element && ((Element) node).tagName().equals("br")) &&
						!(node instanceof TextNode && ((TextNode) node).text().trim().equals("")))
					cleanList.add(node);
			}
			return cleanList;
		}

		// Recursively merge splitted nodes of html
		private static  List<Element> remergeNodes(int avgLength, int margin, List<Node> nodes) {

			// add elements till reaching size limit
			Document firstDoc = new Document("");
			List<Node> secondDoc = new ArrayList<Node>(); 

			int firstsize = 0;
			int nodesize = 0;
			int i = 0;

			do
			{	
				if (i > nodes.size()-1)
					System.out.println("wrong index: index is " + i + " and nodes.size is " + nodes.size());


				nodesize = HTMLUtils.getHTMLWordCount(nodes.get(i).toString());

				if (firstsize > avgLength-margin && firstsize + nodesize > avgLength + margin)
					break;
				else
				{
					firstsize += nodesize;
					firstDoc.append(nodes.get(i++).toString());
				}

			} 
			while (i<nodes.size() && (firstsize <= avgLength || 
					(nodesize = HTMLUtils.getHTMLWordCount(nodes.get(i).toString())) == 0));

//			if(firstsize > avgLength + margin)
//				System.out.println("chunch bigger than margin " + firstsize);

			Elements splited = new Elements();
			splited.add(firstDoc);

			for (int j = i; j< nodes.size(); j++)
				secondDoc.add(nodes.get(j));

			// in case of the last left chunk there is no split needed.
			if (i<nodes.size())
			{	
				splited.addAll(remergeNodes(avgLength, margin, secondDoc));			
			}

			return splited;
		}

		// modifies document: removes subsectionTitle
		public static Element splitsubsectionTitle(Element doc, String[] titles)
		{
			Elements subsectionTitles = new Elements();
			
			for(String title:titles)
				subsectionTitles.addAll(doc.select(" " + title));

			Element subsectionTitle = null;
			if (subsectionTitles.size() ==  0)
				System.out.println("ERROR: no subsectionTitle found.");
			else 
			{
				if (subsectionTitles.size() > 1)				
					System.out.println("ERROR: more than one subsectionTitle found.");
				
				//subsectionTitles.get(0).remove();
				subsectionTitle = subsectionTitles.get(0);
			}

			return subsectionTitle;
		}
}
