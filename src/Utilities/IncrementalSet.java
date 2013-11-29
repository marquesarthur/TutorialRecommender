package Utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;

public class IncrementalSet<T> {

	HashMap<T, Double> objSet = new HashMap<T, Double>();

	public void put(T obj)
	{
		Double oldValue = objSet.get(obj);
		if (oldValue == null)
		{
			objSet.put(obj, 1.0);
		}
		else
		{
			objSet.put(obj, oldValue + 1.0);
		}			
	}
	
	public void put(T obj, Double pValue)
	{
		Double oldValue = objSet.get(obj);
		if (oldValue == null)
		{
			objSet.put(obj, pValue);
		}
		else
		{
			objSet.put(obj, oldValue + pValue);
		}	
	}
	
	public Set<T> getAllKeys()
	{
		return objSet.keySet();
	}
	
	public Double getValue(T obj)
	{
		Double value = objSet.get(obj);
		if (value == null)
		{
			value = 0.0;
		}
		
		return value;
	}
	
	public void dump(String saveFilePath, boolean needsplit)
	{
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(saveFilePath, true));
			for(Object obj:objSet.keySet())
			{
				if (needsplit)
				{
					String word = (String) obj;
					String[] parts = word.split(":");
					writer.writeNext(new String[] {parts[1], parts[0], objSet.get(word).toString()});
				}
				else
					writer.writeNext(new String[] {obj.toString(), objSet.get(obj).toString()});
			}
			writer.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
