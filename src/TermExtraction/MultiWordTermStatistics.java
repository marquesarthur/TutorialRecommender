package TermExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MultiWordTermStatistics {
	Map<String, MultiWordTerm> mwTermsStat = new HashMap<String, MultiWordTerm>();

	public void incrementFrequency(String term)
	{
		incrementFrequencyBy(term, 1);
	}

	public void incrementFrequencyBy(String term, int freq)
	{
		MultiWordTerm mwTerm = mwTermsStat.get(term);
		if (mwTerm != null)
			mwTerm.incrementFrequencyBy(freq);
		else
		{
			mwTerm = new MultiWordTerm();
			mwTerm.setTerm(term);
			mwTerm.incrementFrequencyBy(freq);
		}
		mwTermsStat.put(term, mwTerm);
	}

	public void incrementSubTermFreq(String term)
	{
		incrementSubTermFreqBy(term, 1);
	}
	
	public void incrementSubTermFreqBy(String term, int number)
	{
		MultiWordTerm mwTerm = mwTermsStat.get(term);
		if (mwTerm != null)
			mwTerm.incrementSubTermFreqBy(number);
		else
		{
			mwTerm = new MultiWordTerm();
			mwTerm.setTerm(term);
			mwTerm.incrementSubTermFreqBy(number);
		}
		mwTermsStat.put(term, mwTerm);
	}
	
	public void incrementLongTermNumber(String term)
	{
		incrementLongTermNumberBy(term, 1);
	}
	
	public void incrementLongTermNumberBy(String term, int number)
	{
		MultiWordTerm mwTerm = mwTermsStat.get(term);
		if (mwTerm != null)
			mwTerm.incrementLongTermNumberBy(number);
		else
		{
			mwTerm = new MultiWordTerm();
			mwTerm.setTerm(term);
			mwTerm.incrementLongTermNumberBy(number);			
		}
		mwTermsStat.put(term, mwTerm);
	}
	
	public void updateMWTerm(MultiWordTerm mwTerm)
	{
		mwTermsStat.put(mwTerm.getTerm(), mwTerm);
	}

	public void calcSubStringSat()
	{

		for(String term:mwTermsStat.keySet())
		{	
			for(String longerTaggedTerm:mwTermsStat.keySet())
			{
				if (longerTaggedTerm.contains(term) && !longerTaggedTerm.equals(term))
				{
					MultiWordTerm mwTerm = mwTermsStat.get(longerTaggedTerm);					
					incrementSubTermFreqBy(term, mwTerm.getFrequency());
					incrementLongTermNumber(term);
				}
			}
		}
	}
	
	public void calcCValue()
	{
		for(String term:mwTermsStat.keySet())
		{	
			MultiWordTerm mwTerm = mwTermsStat.get(term);
			mwTerm.calcCValue();
		}
	}
	
	public void dumpTerms(String outputPath)
	{
		FileWriter outFile;
		try {
			outFile = new FileWriter(outputPath, true);

			PrintWriter out = new PrintWriter(outFile);

			for(String term:mwTermsStat.keySet())
			{
				MultiWordTerm mwTerm = mwTermsStat.get(term);

				out.println(mwTerm.getTerm() + "," + mwTerm.getFrequency());
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dumpTermStat(String outputPath)
	{
		FileWriter outFile;
		try {
			outFile = new FileWriter(outputPath, true);

			PrintWriter out = new PrintWriter(outFile);

			for(String term:mwTermsStat.keySet())
			{
				MultiWordTerm mwTerm = mwTermsStat.get(term);
				out.println(mwTerm.toCSVString());
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
