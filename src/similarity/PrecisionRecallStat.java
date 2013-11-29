package similarity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

import sun.awt.windows.ThemeReader;
import tutorial.TutorialSection;

public class PrecisionRecallStat {

	public static double ThresholdStep = 0.05;
	
		
	double precision = 0;
	double recall = 0;
	double threshold = 0;
	
	public static List<PrecisionRecallStat> calc(Map<Integer, Map<String, Double>> results, Map<Integer, Map<String, Boolean>> golden)
	{
		List<PrecisionRecallStat> stat = new ArrayList<PrecisionRecallStat>();
		for(double threshold = 0; threshold<=1; threshold+=ThresholdStep)
		{
			PrecisionRecallStat precrec = new PrecisionRecallStat();
			precrec.threshold = threshold;
			
			double tp = 0,fp =0,tn=0,fn=0;
			int count = 0;
			for(Integer docid:golden.keySet())
			{	
				count++;
				Map<String, Double> docresults = results.get(docid);
				Map<String, Boolean> docgolden = golden.get(docid);
				
				for(String data:docgolden.keySet())
				{ 
					Boolean goldendata = docgolden.get(data);
					Boolean resultdata = (docresults.get(data)>=threshold)?true:false;
					
					
					if (resultdata)
					{
						if (goldendata)
							tp++;
						else
							fp++;
					}
					else
					{
						if (goldendata)
							fn++;
						else
							tn++;						
					}
				}				
			}
			if ((tp+fp) == 0)
				precrec.precision = 0;
			else
				precrec.precision = tp / (tp + fp);
			
			if ((tp+fn) == 0)
				precrec.recall = 0;
			else
				precrec.recall = tp / (tp + fn);
			
			stat.add(precrec);
		}
		return stat;
	}
	
	// this function load gold set for further comparision
	// input file should have 3 columns - docid, dataindicator (e.g. apielement), classification result (true, false)
	public static Map<Integer, Map<String, Boolean>> loadGoldSet(String filePath) throws IOException
	{
		Map<Integer, Map<String, Boolean>> goldset = new HashMap<Integer, Map<String,Boolean>>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(filePath));

			String[] nextLine = {};
			Integer prevdocid = 0;
			nextLine = reader.readNext();
			while (nextLine != null) {
				
				Map<String, Boolean> golddata = new HashMap<String, Boolean>();
				do
				{
					golddata.put(nextLine[1], Boolean.parseBoolean(nextLine[2]));
				}
				while((nextLine = reader.readNext()) != null && prevdocid.equals(Integer.parseInt(nextLine[0])) );
				
				goldset.put(prevdocid, golddata);
				
				if (nextLine != null)
					prevdocid = Integer.parseInt(nextLine[0]);
				
				
			}
		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			reader.close();
		}
		return goldset;
	}
}
