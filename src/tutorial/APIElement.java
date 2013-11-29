package tutorial;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class APIElement {
	private String apiElementName;
	private String fqn;
	private String kind;
	
	
	private List<String> occurenceTexts = new ArrayList<String>();
	
	public APIElement(String pApiElement)
	{
		apiElementName = pApiElement;
	}

	public APIElement(String pApiElement, String pFqn, String pKind)
	{
		apiElementName = pApiElement;
		fqn = pFqn;
		kind = pKind;
	}
	
	public APIElement(String pApiElement, String html)
	{
		this(pApiElement);
		loadInfo(html);
	}

	public APIElement(String pApiElement, String pFqn, String pKind, String html)
	{
		this(pApiElement, pFqn, pKind);
		loadInfo(html);
	}
	
	public String getAPIElementName() {
		return apiElementName;
	}
	public void setAPIElementName(String aPIElement) {
		apiElementName = aPIElement;
	}
	public String getFqn() {
		return fqn;
	}
	public void setFqn(String fqn) {
		this.fqn = fqn;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}

	
	public List<String> getOccurenceTexts() {
		return occurenceTexts;
	}
	public void setOccurenceTexts(List<String> occurenceTexts) {
		this.occurenceTexts = occurenceTexts;
	}

	private void loadInfo(String html) {
		Document doc = Jsoup.parse(html);
		Elements clts = doc.select("clt[api=" + apiElementName + "]");
		
		for(Element clt: clts)
		{
			occurenceTexts.add(clt.text());
		}
	}
	
	
}
