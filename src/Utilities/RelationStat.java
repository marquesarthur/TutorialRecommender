package Utilities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class RelationStat {

	private String relation;
	private double zscore;

	private double prcPositive;
	private double prcNegative;

	private double npositive;
	private double nobs;
	
	
	List<RelationStat> relStat = new ArrayList<RelationStat>();

	public void addRelation(String relation, double zscore, double prcPositive, double prcNegative)
	{
		RelationStat rel = new RelationStat();
		rel.relation = relation;
		rel.zscore = zscore;

		rel.prcPositive = prcPositive;
		rel.prcNegative = prcNegative;
		relStat.add(rel);
	}

	public void loadFromFile(String filePath)
	{
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filePath));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				addRelation(nextLine[0], Double.parseDouble(nextLine[3]), 
						Double.parseDouble(nextLine[4]), Double.parseDouble(nextLine[5]));
			}

		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getZscore(String relation) {
		for(RelationStat rel:relStat)
			if (rel.relation.equals(relation))
				return rel.zscore;
		return 0;
	}
	
	public double getPrcPositive(String relation) {
		for(RelationStat rel:relStat)
			if (rel.relation.equals(relation))
				return rel.prcPositive;
		return 0;
	}
	
	public double getPrcNegative(String relation) {
		for(RelationStat rel:relStat)
			if (rel.relation.equals(relation))
				return rel.prcNegative;
		return 0;
	}
	
	public double getNpositive(String rel) {
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
				return relS.npositive;
		return 0;
	}

	public double getNobs(String rel) {
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
				return relS.nobs;
		return 0;
	}

	public void setNpositive(String rel, double positive) {
		boolean found = false;
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
			{
				relS.npositive = positive;
				found = true;
			}
		if (!found)
		{
			RelationStat newrelStat = new RelationStat();
			newrelStat.relation = rel;
			newrelStat.npositive = positive;
			relStat.add(newrelStat);
		}
	}

	public void incrementNpositive(String rel) {
		boolean found = false;
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
			{
				relS.npositive++;
				found = true;
			}
		if (!found)
		{
			RelationStat newrelStat = new RelationStat();
			newrelStat.relation = rel;
			newrelStat.npositive = 1;
			relStat.add(newrelStat);
		}
	}
	
	public void setNobs(String rel, double obs) {
		boolean found = false;
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
			{
				relS.nobs = obs;
				found = true;
			}
		if (!found)
		{
			RelationStat newrelStat = new RelationStat();
			newrelStat.relation = rel;
			newrelStat.nobs = obs;
			relStat.add(newrelStat);
		}
	
	}
	
	public void incrementNobs(String rel) {
		boolean found = false;
		for(RelationStat relS:relStat)
			if (relS.relation.equals(rel))
			{
				relS.nobs++;
				found = true;
			}
		if (!found)
		{
			RelationStat newrelStat = new RelationStat();
			newrelStat.relation = rel;
			newrelStat.nobs = 1;
			relStat.add(newrelStat);
		}
	}

	public void calcZScores()
	{
		// Compute population statistic
		double sumNpositives = 0;
		double sumNobs = 0;
		
		for(RelationStat relS:relStat)
		{
			sumNobs += relS.nobs;
			sumNpositives += relS.npositive;
		}
		double prcPositive = sumNpositives/sumNobs;
		
		
		// The standard deviation and expected value of a binomial variable 
		double expectedPositive;
		double deltaPositive;
		double stdDeviation;
		
		for(RelationStat relS:relStat)
		{
			expectedPositive = relS.nobs * prcPositive;
			deltaPositive = relS.npositive - expectedPositive;
			stdDeviation = Math.sqrt(relS.nobs * prcPositive * (1-prcPositive));
			
			relS.zscore = deltaPositive/stdDeviation;
		}
		
	}

}
