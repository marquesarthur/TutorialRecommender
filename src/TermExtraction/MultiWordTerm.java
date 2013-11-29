package TermExtraction;

public class MultiWordTerm {
	private String term;	
	private double wordNumber;
	private double frequency;
	private double subTermFreq; // number of times term occurs as substring in longer strings
	private double longTermNumber; // number of longer strings containing the term as substring
	private double cvalue;
	private double contextValue;
	private double ncValue;
	
	public String getTerm() {
		return term;
	}

	public void setTerm(String newTerm) {
		term = newTerm;
		setWordNumber();
	}

	public int getWordNumber() {
		return (int)wordNumber;
	}

	public void setWordNumber() {
		
		wordNumber = term.split(" ").length;
	}

	public int getFrequency() {
		return (int)frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void incrementFrequnecy()
	{
		frequency++;
	}
	
	public void incrementFrequencyBy(int freq)
	{
		frequency+=freq;
	}
	
	public int getSubTermFreq() {
		return (int)subTermFreq;
	}

	public void setSubTermFreq(int subTermFreq) {
		this.subTermFreq = subTermFreq;
	}

	public void incrementSubTermFreq()
	{
		this.subTermFreq++;
	}
	
	public void incrementSubTermFreqBy(int subTermFreq)
	{
		this.subTermFreq+=subTermFreq;
	}
	
	public int getLongTermNumber() {
		return (int)longTermNumber;
	}

	public void setLongTermNumber(int longTermNumber) {
		this.longTermNumber = longTermNumber;
	}

	public void incrementLongTermNumber()
	{
		this.longTermNumber++;
	}
	
	public void incrementLongTermNumberBy(int longTermNumber)
	{
		this.longTermNumber+=longTermNumber;
	}
	
	public double getCvalue() {
		return cvalue;
	}

	public void calcCValue() {
		double wn = wordNumber;
		if (wn != 1.0)
			wn = Math.log(wn)/Math.log(2);
		else
			wn = 0.5;
		if (longTermNumber == 0)
		{
			cvalue = (double) (wn * frequency);
		}
		else
		{
			double temp = (double) (subTermFreq/longTermNumber);
			cvalue = (double) (wn * (frequency - temp));
		}
	}

	public double getContextValue() {
		return contextValue;
	}

	public void setContextValue(double contextValue) {
		this.contextValue = contextValue;
	}

	public double getNcValue() {
		return ncValue;
	}

	public void setNcValue(double ncValue) {
		this.ncValue = ncValue;
	}

	public String toCSVString()
	{
		String csvString = term + "," + frequency + "," + subTermFreq + "," 
				+ longTermNumber + "," + wordNumber + "," + cvalue;
		
		return csvString;
	}
}
