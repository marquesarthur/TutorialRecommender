package Utilities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import TermExtraction.CLTDependency;

public class DependencyStat {

	private CLTDependency cltDep;


	private double npositive;
	private double nobs;
	
	private double pprob;
	private double nprob;
	private double pcprob;
	private double ncprob;

	private double zscore;

	List<DependencyStat> depStat = new ArrayList<DependencyStat>();

	public void addDependency(String governor, String relation, String dependent,
			double zscore)
	{
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		addDependency(newdep, zscore);
	}

	public void addDependency(CLTDependency dep, double zscore)
	{
		DependencyStat deps = new DependencyStat();
		deps.cltDep = dep;
		deps.zscore = zscore;

		depStat.add(deps);
	}

	public void addDependency(String governor, String relation, String dependent,
			double pprob, double nprob, double pcprob, double ncprob)
	{
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		addDependency(newdep, pprob, nprob, pcprob, ncprob);
	}

	public void addDependency(CLTDependency dep,
			double pprob, double nprob, double pcprob, double ncprob)
	{
		DependencyStat deps = new DependencyStat();
		deps.cltDep = dep;
		deps.pprob = pprob;
		deps.nprob = nprob;
		deps.pcprob = pcprob;
		deps.ncprob = ncprob;

		depStat.add(deps);
	}

	public void loadFromFile(String filePath)
	{
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filePath));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				addDependency(nextLine[0], nextLine[1], nextLine[2], Double.parseDouble(nextLine[3]), 
						Double.parseDouble(nextLine[4]), Double.parseDouble(nextLine[5]), 
						Double.parseDouble(nextLine[6]));
			}

		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadZScoreFromFile(String filePath)
	{
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filePath));

			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				addDependency(nextLine[0], nextLine[1], nextLine[2], Double.parseDouble(nextLine[3]));
			}

		} catch (FileNotFoundException e) {
			System.out.println(filePath + " is not a valid path.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getPprob(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.pprob;
		return 0;
	}

	public double getNprob(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.nprob;
		return 0;
	}

	public double getPcprob(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.pcprob;
		return 0;
	}

	public double getNcprob(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.ncprob;
		return 0;
	}

	public double getZscore(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.zscore;
		return 0;
	}

	public double getNpositive(CLTDependency dep) {
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
				return depS.npositive;
		return 0;
	}

	public double getNobs(CLTDependency dep) {
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
				return depS.nobs;
		return 0;
	}
	
	public double getNpositive(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.npositive;
		return 0;
	}

	public double getNobs(String governor, String relation, String dependent) {
		CLTDependency newdep = new CLTDependency(governor, dependent, relation);
		for(DependencyStat dep:depStat)
			if (dep.cltDep.equals(newdep))
				return dep.nobs;
		return 0;
	}

	public void setNpositive(CLTDependency dep, double positive) {
		boolean found = false;
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
			{
				depS.npositive = positive;
				found = true;
			}
		if (!found)
		{
			DependencyStat newDepStat = new DependencyStat();
			newDepStat.cltDep = dep;
			newDepStat.npositive = positive;
			depStat.add(newDepStat);
		}
	}

	public void incrementNpositive(CLTDependency dep) {
		boolean found = false;
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
			{
				depS.npositive++;
				found = true;
			}
		if (!found)
		{
			DependencyStat newDepStat = new DependencyStat();
			newDepStat.cltDep = dep;
			newDepStat.npositive = 1;
			depStat.add(newDepStat);
		}
	}
	
	public void setNobs(CLTDependency dep, double obs) {
		boolean found = false;
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
			{
				depS.nobs = obs;
				found = true;
			}
		if (!found)
		{
			DependencyStat newDepStat = new DependencyStat();
			newDepStat.cltDep = dep;
			newDepStat.nobs = obs;
			depStat.add(newDepStat);
		}
	
	}
	
	public void incrementNobs(CLTDependency dep) {
		boolean found = false;
		for(DependencyStat depS:depStat)
			if (depS.cltDep.equals(dep))
			{
				depS.nobs++;
				found = true;
			}
		if (!found)
		{
			DependencyStat newDepStat = new DependencyStat();
			newDepStat.cltDep = dep;
			newDepStat.nobs = 1;
			depStat.add(newDepStat);
		}
	}

	public void calcZScores()
	{
		// Compute population statistic
		double sumNpositives = 0;
		double sumNobs = 0;
		
		for(DependencyStat depS:depStat)
		{
			sumNobs += depS.nobs;
			sumNpositives += depS.npositive;
		}
		double prcPositive = sumNpositives/sumNobs;
		
		
		// The standard deviation and expected value of a binomial variable 
		double expectedPositive;
		double deltaPositive;
		double stdDeviation;
		
		for(DependencyStat depS:depStat)
		{
			expectedPositive = depS.nobs * prcPositive;
			deltaPositive = depS.npositive - expectedPositive;
			stdDeviation = Math.sqrt(depS.nobs * prcPositive * (1-prcPositive));
			
			depS.zscore = deltaPositive/stdDeviation;
		}
		
	}

}
