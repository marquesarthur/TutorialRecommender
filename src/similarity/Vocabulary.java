package similarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Utilities.Utils;

public class Vocabulary {

	ArrayList<String> vocabulary = new ArrayList<String>();
	Map<String, Integer> inverseVocab = new HashMap<String, Integer>();
	
	public Vocabulary() {
		// TODO Auto-generated constructor stub
	}

	public Vocabulary(String path) {
		try {
			load(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load(String path) throws IOException
	{
		vocabulary = (ArrayList<String>) Utils.readFileAsList(path);
		
		int idx = 0;
		for (String word:vocabulary)
			inverseVocab.put(word, idx++);
	}
	
	public int getIdx(String word)
	{
		Integer idx = inverseVocab.get(word);
		if (idx == null)
			idx = -1;
		
		return idx;
	}
	
	public String getWord(int idx)
	{
		return vocabulary.get(idx);
	}
	
	public int size()
	{
		return vocabulary.size();
	}
}
