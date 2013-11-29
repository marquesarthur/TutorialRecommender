import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Utilities.DependencyStat;
import Utilities.RelationStat;
import Utilities.Utils;

import tutorial.SectionElement;
import tutorial.Tutorial;
import tutorial.TutorialSection;
import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.classify.SVMLightClassifier;
import edu.stanford.nlp.classify.SVMLightClassifierFactory;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.optimization.QNMinimizer;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.Pair;
import features.FeatureFactory;

public class TutorialRecommender {

	/**
	 * @param args
	 * @throws IOException
	 */
	
	private static String logPath = "";
	public static void main(String[] args) throws IOException {
		
		String[] tutorials = {
				"tutorials/JodaTime/jodatime.csv"
		, "tutorials/math/math_annotation_disagreements.csv"
		, 
		"tutorials/smack/smack_tutorial.csv",
		"tutorials/jcol/jcol_tutorial.csv",
		"tutorials/col/col_tutorial.csv"
		};
		
		boolean[] types = {true, false, false, false, false};
		
//		float[] optimalAlpha = {0.9f, 0.6f, 0.9f, 0.6f};
//		float[] optimalBeta = {0.1f, 0.5f, 0.4f, 0.9f};

		float alpha = 1.0f;
		float beta = 1.0f;
		
		boolean svm = false;
		
//		optimalParamsTest(tutorials, types, optimalAlpha, optimalBeta, svm);
//		
//		for (int i = 0 ; i < tutorials.length; i++)
//			System.out.format("For tutorial %d optimal parameters are: aplha=%2f beta=%2f\n", i, optimalAlpha[i], optimalBeta[i]);

		logPath = "predictions.txt";
		
//		featureSetTest(tutorials, types, alpha, beta, svm, logPath);
//		testSVM(tutorials, types, beta, alpha, logPath);
		
		
//		try {
//			crossTutorialTest(tutorials, types, alpha, beta, svm, logPath);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		String[] agreementsTutorials = {"tutorials/JodaTime/jodatime_agreements.csv"
//				, "tutorials/math/math_annotation_agreements.csv", 
//				"tutorials/smack/smack_agreements.csv",
//				"tutorials/jcol/jcol_agreements.csv"
//				};
//		testAgreements(agreementsTutorials, types, optimalAlpha, optimalBeta, svm);
		
//		learningCurve(tutorials, types);
		
		predict(tutorials, types, alpha, beta, svm);
	}

	private static void learningCurve(String[] tutorials, boolean[] types) throws IOException
	{
			boolean svm = true;
		
			RVFDataset<Boolean, String> dataSet = new RVFDataset<Boolean, String>();
			
			for(int i  = 0; i< tutorials.length; i++)
			{
					System.out.println("loading tutorial " + tutorials[i] + " as a train tutorial");
					Tutorial tut = new Tutorial();
					tut.LoadTutorial(tutorials[i], true, types[i]);
					dataSet.addAll(constructFeatures(tut, tut, true, true, true, true, true, true, false));
					System.out.println("done loading");
			}
			
			List<Integer> indexes = new ArrayList<Integer>();
			for (int i = 0; i<dataSet.size(); i++)
				indexes.add(i);
			
			Collections.shuffle(indexes);
			
			List<Double> testError = new ArrayList<Double>();
			List<Double> trainError = new ArrayList<Double>();
			
			RVFDataset<Boolean, String> testSet = new RVFDataset<Boolean, String>();
			for (int j = 0; j<=120; j++)
				testSet.add(dataSet.getRVFDatum(j));
			
			for (int i = 121; i<dataSet.size()-10; i++)
			{
				
//				System.out.println("Size of the training set is " + i);
				
				RVFDataset<Boolean, String> trainSet = new RVFDataset<Boolean, String>();
					
				for (int j = 121; j<=i+10; j++)
					trainSet.add(dataSet.getRVFDatum(j));
				
				Counter<String> stat = new ClassicCounter<String>();
				stat.setCount("TP", 0);
				stat.setCount("FP", 0);
				stat.setCount("TN", 0);
				stat.setCount("FN", 0);
				
				LinearClassifier<Boolean, String> c = train(trainSet, svm);
				
				stat = test(c, trainSet, svm, stat);
				trainError.add(stat.getCount("FN") + stat.getCount("FP"));
				
				double trainerror = stat.getCount("FN") + stat.getCount("FP");
				
				stat = new ClassicCounter<String>();
				stat.setCount("TP", 0);
				stat.setCount("FP", 0);
				stat.setCount("TN", 0);
				stat.setCount("FN", 0);
				
				stat = test(c, testSet, svm, stat);
				testError.add(stat.getCount("FN") + stat.getCount("FP"));
				double testerror = stat.getCount("FN") + stat.getCount("FP");
				System.out.println(trainerror + "," + testerror);
			}
		
	}
	
	private static void crossTutorialTest(String[] tutorials, boolean[] types,
			float alpha, float beta, boolean svm, String logPath) throws IOException {
		
		Tutorial testTutorial = new Tutorial();
		testTutorial.tutDepStat = new DependencyStat();
		testTutorial.tutRelStat = new RelationStat();
		testTutorial.alpha = alpha;
		testTutorial.beta = beta;
		
		
		for(int testIndex = 0; testIndex < tutorials.length; testIndex++)
		{
			RVFDataset<Boolean, String> testSet = new RVFDataset<Boolean, String>();
			RVFDataset<Boolean, String> trainSet = new RVFDataset<Boolean, String>();
			
			for(int i  = 0; i< tutorials.length; i++)
			{
				if (i == testIndex){	
					System.out.println("loading tutorial " + tutorials[i] + " as a test tutorial");
					testTutorial.LoadTutorial(tutorials[i], true, types[i]);
					testSet = constructFeatures(testTutorial, testTutorial, true, true, true, true, true, true, false);
					System.out.println("done loading");
				}
				else{
					System.out.println("loading tutorial " + tutorials[i] + " as a train tutorial");
					Tutorial tut = new Tutorial();
					tut.LoadTutorial(tutorials[i], true, types[i]);
					trainSet.addAll(constructFeatures(tut, tut, true, true, true, true, true, true, false));
					System.out.println("done loading");
				}
			}
			
			Counter<String> stat = new ClassicCounter<String>();
			stat.setCount("TP", 0);
			stat.setCount("FP", 0);
			stat.setCount("TN", 0);
			stat.setCount("FN", 0);
			
			System.out.println("Run and test");
			stat = runAndTest(trainSet, testSet, svm, stat);
			
			System.out.println("output results");
			printStat(stat, testTutorial.alpha, testTutorial.beta, logPath);
		
		}
		
		
		
	}

	private static void testAgreements(String[] tutorials,
			boolean[] types, float[] optimalAlpha, float[] optimalBeta,
			boolean svm, String logPath) {
		
		System.out.println("Test only on agreements");
		System.out.println("-----------------------");
		
		for(int i  = 0; i< tutorials.length; i++)
		{
			Tutorial tutorial = new Tutorial();
	
			tutorial.LoadTutorial(tutorials[i], true, types[i]);
			int fold = tutorial.sectionElements.size();
			
			try {

					
						tutorial.tutDepStat = new DependencyStat();
						tutorial.tutRelStat = new RelationStat();
						tutorial.alpha = optimalAlpha[i];
						tutorial.beta = optimalBeta[i];
						 
						boolean numbericFeatures = true;
						boolean tutorialLevel = true; 
						boolean sectionLevel = true;
						boolean sentenceLevel = true; 
						boolean dependencies = true;
						boolean relations = true;
						boolean relationProb = false;
						 
						Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, false);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
	}

	private static void predict(String[] tutorials, boolean[] types, float optimalAlpha, float optimalBeta, boolean svm)
	{
		System.out.println("Predicit");
		System.out.println("--------------------------");
		
		for(int i  = 0; i< tutorials.length; i++)
		{
			Utils.saveFile("Predicit relevance for tutorial - " + tutorials[i], logPath, true);
	
			Tutorial tutorial = new Tutorial();
			
			tutorial.LoadTutorial(tutorials[i], true, types[i]);
			int fold = tutorial.sectionElements.size();
			
			try {
				tutorial.tutDepStat = new DependencyStat();
				tutorial.tutRelStat = new RelationStat();
				tutorial.alpha = optimalAlpha;
				tutorial.beta = optimalBeta;
				 
				boolean numbericFeatures = true;
				boolean tutorialLevel = true; 
				boolean sectionLevel = true;
				boolean sentenceLevel = true; 
				boolean dependencies = true;
				boolean relations = true;
				boolean relationProb = false;
				 
				Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
						 sectionLevel, sentenceLevel, 
						 dependencies, relations, relationProb, svm);
				
				
				
				printStat(stat, tutorial.alpha, tutorial.beta, logPath);
			}
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	
	private static void featureSetTest(String[] tutorials, boolean[] types, float optimalAlpha, float optimalBeta, boolean svm, String logPath)
	{
		System.out.println("Feature configuration tests");
		System.out.println("--------------------------");
		
		for(int i  = 0; i< tutorials.length; i++)
		{
			Tutorial tutorial = new Tutorial();
			
			tutorial.LoadTutorial(tutorials[i], true, types[i]);
			int fold = tutorial.sectionElements.size();
			
			try {

					
						tutorial.tutDepStat = new DependencyStat();
						tutorial.tutRelStat = new RelationStat();
						tutorial.alpha = optimalAlpha;
						tutorial.beta = optimalBeta;
						 
						boolean numbericFeatures = true;
						boolean tutorialLevel = true; 
						boolean sectionLevel = true;
						boolean sentenceLevel = true; 
						boolean dependencies = true;
						boolean relations = true;
						boolean relationProb = true;
						 
						Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						Utils.saveFile("all features\n", logPath, true);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = true; 
						dependencies = true;
						relations = true;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("withtout dependencies\n", logPath, true);
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = true; 
						dependencies = false;
						relations = true;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("withtout relations\n", logPath, true);
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = true; 
						dependencies = true;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("without dependencies and relations\n", logPath, true);
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = true; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						
						Utils.saveFile("only numeric, tutorial level and section\n", logPath, true);
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = false; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only numeric and tutorial level\n", logPath, true);
						numbericFeatures = true;
						tutorialLevel = true; 
						sectionLevel = false;
						sentenceLevel = false; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only relation\n", logPath, true);
						numbericFeatures = false;
						tutorialLevel = true; 
						sectionLevel = false;
						sentenceLevel = false; 
						dependencies = false;
						relations = true;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only deps\n", logPath, true);
						numbericFeatures = false;
						tutorialLevel = true; 
						sectionLevel = false;
						sentenceLevel = false; 
						dependencies = true;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only sentence level\n", logPath, true);
						numbericFeatures = false;
						tutorialLevel = false; 
						sectionLevel = false;
						sentenceLevel = true; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only section level\n", logPath, true);
						numbericFeatures = false;
						tutorialLevel = false; 
						sectionLevel = true;
						sentenceLevel = false; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only tutorial level\n", logPath, true);
						numbericFeatures = false;
						tutorialLevel = true; 
						sectionLevel = false;
						sentenceLevel = false; 
						dependencies = false;
						relations = false;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("only numeric\n", logPath, true);
						
						numbericFeatures = true;
						tutorialLevel = false; 
						sectionLevel = false;
						sentenceLevel = false; 
						dependencies = false;
						relations = false;
						relationProb = false;
						
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						Utils.saveFile("everything without numeric\n", logPath, true);
						
						numbericFeatures = false;
						tutorialLevel = true; 
						sectionLevel = true;
						sentenceLevel = true; 
						dependencies = true;
						relations = true;
						relationProb = false;
						 
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void testSVM(String[] tutorials, boolean[] types, float optimalAlpha, float optimalBeta, String logPath) throws IOException
	{
		Utils.saveFile("SVM test\n", logPath, true);
		Utils.saveFile("--------\n", logPath, true);
		for(int i  = 0; i< tutorials.length; i++)
		{
			Tutorial tutorial = new Tutorial();
	
			tutorial.LoadTutorial(tutorials[i], true, types[i]);
			int fold = tutorial.sectionElements.size();
			
			try {

					
						tutorial.tutDepStat = new DependencyStat();
						tutorial.tutRelStat = new RelationStat();
						tutorial.alpha = optimalAlpha;
						tutorial.beta = optimalBeta;
						 
						boolean numbericFeatures = true;
						boolean tutorialLevel = true; 
						boolean sectionLevel = true;
						boolean sentenceLevel = true; 
						boolean dependencies = true;
						boolean relations = true;
						boolean relationProb = false;
						 
						Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, true);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
						
						tutorial.tutDepStat = new DependencyStat();
						tutorial.tutRelStat = new RelationStat();
						tutorial.alpha = optimalAlpha;
						tutorial.beta = optimalBeta;
						 
						
						numbericFeatures = false;
						stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, true);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	private static void test(String[] tutorials, boolean[] types, float[] optimalAlpha, float[] optimalBeta, boolean svm, String logPath)
	{
		for(int i  = 0; i< tutorials.length; i++)
		{
			Tutorial tutorial = new Tutorial();
	
			tutorial.LoadTutorial(tutorials[i], true, types[i]);
			int fold = tutorial.sectionElements.size();
			
			try {

					
						tutorial.tutDepStat = new DependencyStat();
						tutorial.tutRelStat = new RelationStat();
						tutorial.alpha = optimalAlpha[i];
						tutorial.beta = optimalBeta[i];
						 
						boolean numbericFeatures = true;
						boolean tutorialLevel = true; 
						boolean sectionLevel = true;
						boolean sentenceLevel = true; 
						boolean dependencies = true;
						boolean relations = true;
						boolean relationProb = true;
						 
						Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
								 sectionLevel, sentenceLevel, 
								 dependencies, relations, relationProb, svm);
						
						printStat(stat, tutorial.alpha, tutorial.beta, logPath);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	private static void optimalParamsTest(String[] tutorials, boolean[] types, float[] optimalAlpha, 
											float[] optimalBeta, boolean svm, String logPath) throws IOException {
		Utils.saveFile("Optimal parameters test\n", logPath, true);
		Utils.saveFile("------------------------\n", logPath, true);
		double maxf1 = 0;
		double f1 = 0;
		for(int i  = 0; i< tutorials.length; i++)
			{
				maxf1 = 0;
				Tutorial tutorial = new Tutorial();
		
				tutorial.LoadTutorial(tutorials[i], true, types[i]);
				int fold = tutorial.sectionElements.size();
				
				try {

					for(float alpha = 0; alpha<=1.05; alpha +=0.1)
					{
						for(float beta = 0; beta<=1.05; beta+=0.1)
						{
							tutorial.tutDepStat = new DependencyStat();
							tutorial.tutRelStat = new RelationStat();
							tutorial.alpha = alpha;
							tutorial.beta = beta;
							 
							boolean numbericFeatures = true;
							boolean tutorialLevel = true; 
							boolean sectionLevel = true;
							boolean sentenceLevel = true; 
							boolean dependencies = true;
							boolean relations = true;
							boolean relationProb = false;

							Counter<String> stat = trainCV(fold, tutorial,numbericFeatures, tutorialLevel, 
									 sectionLevel, sentenceLevel, 
									 dependencies, relations, relationProb, svm);
							
							f1 = printStat(stat, tutorial.alpha, tutorial.beta, logPath);
							
							if(f1 > maxf1)
							{
								optimalAlpha[i] = alpha;
								optimalBeta[i] = beta;
								maxf1 = f1;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
	}

	private static double printStat(Counter<String> stat, float alpha, float beta, String logPath) throws IOException {

//		System.out.println();
//		System.out.println("statistics for Leave-one-out cross validation");
//		System.out.println("-----------------------------------------------");

		DecimalFormat df = new DecimalFormat("#.##");

//		System.out.println();
//		System.out.println("Confusion matrix with absolut values");
//		System.out.println("-----------------------------------");
//
//		System.out.println("\t\t originalT \t originalF");
//		System.out.println("guessedT\t " + df.format(stat.getCount("TP"))
//				+ "\t\t" + df.format(stat.getCount("FP")));
//		System.out.println("guessedF\t " + df.format(stat.getCount("FN"))
//				+ "\t\t" + df.format(stat.getCount("TN")));
//		System.out.println();
//
//		System.out.println();
//		System.out.println("Confusion matrix with regulazied values");
//		System.out.println("-----------------------------------");
//
//		double totalsize = stat.getCount("TP") + stat.getCount("FP")
//				+ stat.getCount("FN") + stat.getCount("TN");
//
//		stat.setCount("TP", stat.getCount("TP") / totalsize);
//		stat.setCount("FP", stat.getCount("FP") / totalsize);
//		stat.setCount("FN", stat.getCount("FN") / totalsize);
//		stat.setCount("TN", stat.getCount("TN") / totalsize);
//
//		System.out.println("\t\t originalT \t originalF");
//		System.out.println("guessedT\t " + df.format(stat.getCount("TP"))
//				+ "\t\t" + df.format(stat.getCount("FP")));
//		System.out.println("guessedF\t " + df.format(stat.getCount("FN"))
//				+ "\t\t" + df.format(stat.getCount("TN")));
//		System.out.println();

		double precision = stat.getCount("TP")
				/ (stat.getCount("TP") + stat.getCount("FP"));
		double recall = stat.getCount("TP")
				/ (stat.getCount("TP") + stat.getCount("FN"));
		double f1 = (2 * precision * recall) / (precision + recall);

//		System.out.println("\tprecision\t" + df.format(precision));
//		System.out.println("\trecall\t\t" + df.format(recall));
//		System.out.println("\tf1\t\t" + df.format(f1));
		
		Utils.saveFile(alpha + "," + beta+","+df.format(precision) +","
				+ df.format(recall) + "," +df.format(f1)+"\n", logPath, true);
		
		return f1;
	}

	private static Counter<String> trainCV(int fold, Tutorial tut, 
			 boolean numbericFeatures, boolean tutorialLevel, 
			 boolean sectionLevel, boolean sentenceLevel, 
			 boolean dependencies, boolean relations, boolean relationProb, boolean svm)
			throws IOException {
		
		Counter<String> stat = new ClassicCounter<String>();
		stat.setCount("TP", 0);
		stat.setCount("FP", 0);
		stat.setCount("TN", 0);
		stat.setCount("FN", 0);
		
		// when fold = 1 then calculate just train error
		if (fold != 1) {
			for (int i = 0; i < fold; i++) {

				Pair<Tutorial, Tutorial> splitedTut = splitTutorial(tut, fold,i);				
				//splitedTut.first.trainStat();
				splitedTut.second.tutDepStat = splitedTut.first.tutDepStat;
				splitedTut.second.tutRelStat = splitedTut.first.tutRelStat;
				RVFDataset<Boolean, String> trainset = constructFeatures(splitedTut.first, tut,numbericFeatures, tutorialLevel, 
						 sectionLevel, sentenceLevel, 
						 dependencies, relations, relationProb);
				RVFDataset<Boolean, String> testset = constructFeatures(splitedTut.second, tut,numbericFeatures, tutorialLevel, 
						 sectionLevel, sentenceLevel, 
						 dependencies, relations, relationProb);
				stat = runAndTest(trainset, testset, svm, stat);
				
			}
		} else {
			// train and test on the same dataset
			tut.trainStat();
			RVFDataset<Boolean, String> dataset = constructFeatures(tut, tut, numbericFeatures, tutorialLevel, 
					 sectionLevel, sentenceLevel, 
					 dependencies, relations, relationProb);
			
			stat = runAndTest(dataset, dataset, svm, stat);
		}

		return stat;

	}

	private static LinearClassifier<Boolean, String> train(RVFDataset<Boolean, String> trainSet,  boolean svm)
	{
		QNMinimizer qm = new QNMinimizer(15);
		qm.shutUp();
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
		
		SVMLightClassifierFactory<Boolean, String> svmlcFactory = null;
		LinearClassifierFactory<Boolean, String> lcFactory = null;
		if (svm)
			svmlcFactory = new SVMLightClassifierFactory<Boolean, String>("lib/svm/svm_learn", "lib/svm/svm_learn");
		else
			lcFactory = new LinearClassifierFactory<Boolean, String>(qm);
		
		LinearClassifier<Boolean, String> c;
		if (svm)
			c = svmlcFactory.trainClassifier(trainSet);
		else
			c= lcFactory.trainClassifier(trainSet);
		
		return c;
	}
	
	private static Counter<String> test(LinearClassifier<Boolean, String> c, RVFDataset<Boolean, String> testSet, boolean svm, 
										Counter<String> stat)
	{
		for (int index = 0; index < testSet.size(); index++) {
			Datum<Boolean, String> d = testSet.getRVFDatum(index);
			Utils.saveFile(c.classOf(d) + "\n", logPath, true);
			if (d.label()) {
				if (c.classOf(d))
					stat.incrementCount("TP");
				else
					stat.incrementCount("FN");
			} else {
				if (c.classOf(d))
					stat.incrementCount("FP");
				else
					stat.incrementCount("TN");
			}
		}
		
		return stat;
	}
	
	private static Counter<String> runAndTest(RVFDataset<Boolean, String> trainSet, RVFDataset<Boolean, String> testSet, boolean svm, Counter<String> stat)
	{
		LinearClassifier<Boolean, String> c = train(trainSet, svm);
		
		stat = test(c, testSet, svm, stat);
		
		return stat;
	}
	
	private static Pair<Tutorial, Tutorial> splitTutorial(Tutorial tut,
			int fold, int devIndex) {
		
		int tutSize = tut.sectionElements.size();
		
		int devSize = (int) (tutSize / fold);
		// int trainSize = dataset.size() - devSize;

		int devStartIndex = (int) ((tutSize / fold) * devIndex);

		Tutorial dev = new Tutorial();
		Tutorial train = new Tutorial();

		dev.alpha = tut.alpha;
		train.alpha = tut.alpha;
		
		dev.beta = tut.beta;
		train.beta = tut.beta;
		
		Tutorial temp;
		for (int i = 0; i < tutSize; i++) {
			SectionElement currentSection = tut.sectionElements.get(i);
			if (i >= devStartIndex && i < devSize + devStartIndex)
				temp = dev;
			else
				temp = train;
			
			temp.sectionElements.add(currentSection);
			String api = currentSection.getApiElement().getAPIElementName();
			if (temp.sectionElementsPerElement.get(api) == null)
			{
				ArrayList<SectionElement> newList = new ArrayList<SectionElement>();
				newList.add(currentSection);
				temp.sectionElementsPerElement.put(api, newList);
			}
			else
			{
				ArrayList<SectionElement> pairList = temp.sectionElementsPerElement.get(api) ;
				pairList.add(currentSection);
				temp.sectionElementsPerElement.put(api, pairList);
			}
			
			String secTitle = currentSection.getSection().getSubTitle();
			if (temp.sectionElementsPerSection.get(secTitle) == null)
			{
				ArrayList<SectionElement> newList = new ArrayList<SectionElement>();
				newList.add(currentSection);
				temp.sectionElementsPerSection.put(secTitle, newList);
			}
			else
			{
				ArrayList<SectionElement> pairList = temp.sectionElementsPerSection.get(secTitle) ;
				pairList.add(currentSection);
				temp.sectionElementsPerSection.put(secTitle, pairList);
			}
		}
		return new Pair<Tutorial, Tutorial>(train, dev);
	}

	private static Counter<String> trainCV(int fold,
			RVFDataset<Boolean, String> dataset) {
		Counter<String> stat = new ClassicCounter<String>();
		stat.setCount("TP", 0);
		stat.setCount("FP", 0);
		stat.setCount("TN", 0);
		stat.setCount("FN", 0);

		QNMinimizer qm = new QNMinimizer(15);
		qm.shutUp();
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));

		LinearClassifierFactory<Boolean, String> lcFactory = new LinearClassifierFactory<Boolean, String>(
				qm);

		if (fold != 1) {
			for (int i = 0; i < fold; i++) {
				Pair<RVFDataset<Boolean, String>, RVFDataset<Boolean, String>> splitedDS = split(
						dataset, fold, i);
				LinearClassifier<Boolean, String> c = lcFactory
						.trainClassifier(splitedDS.first);

				for (int index = 0; index < splitedDS.second.size(); index++) {
					Datum<Boolean, String> d = splitedDS.second
							.getRVFDatum(index);
					System.out.println(splitedDS.second
							.getRVFDatumSource(index)
							+ ","
							+ splitedDS.second.getRVFDatumId(index)
							+ ","
							+ d.toString()
							+ " ,  "
							+ c.classOf(d)
							+ ","
							+ c.scoreOf(d, true) + "," + c.scoreOf(d, false));
					if (d.label()) {
						if (c.classOf(d))
							stat.incrementCount("TP");
						else
							stat.incrementCount("FN");
					} else {
						if (c.classOf(d))
							stat.incrementCount("FP");
						else
							stat.incrementCount("TN");
					}
				}
			}
		} else {
			for (int index = 0; index < dataset.size(); index++) {

				LinearClassifier<Boolean, String> c = lcFactory
						.trainClassifier(dataset);
				Datum<Boolean, String> d = dataset.getRVFDatum(index);

				System.out.println(dataset.getRVFDatumSource(index) + ","
						+ dataset.getRVFDatumId(index) + "," + d.toString()
						+ " ,  " + c.classOf(d) + "," + c.scoreOf(d, true)
						+ "," + c.scoreOf(d, false));
				if (d.label()) {
					if (c.classOf(d))
						stat.incrementCount("TP");
					else
						stat.incrementCount("FN");
				} else {
					if (c.classOf(d))
						stat.incrementCount("FP");
					else
						stat.incrementCount("TN");
				}
			}
		}

		return stat;
	}

	private static RVFDataset<Boolean, String> constructFeatures(
			Tutorial tutorial, Tutorial fullTutorial, 
			 boolean numbericFeatures, boolean tutorialLevel, 
			 boolean sectionLevel, boolean sentenceLevel, 
			 boolean dependencies, boolean relations, boolean relationProb) throws IOException {
		FeatureFactory features = new FeatureFactory();

		RVFDataset<Boolean, String> dataset = new RVFDataset<Boolean, String>();
		for (int i = 0; i < tutorial.sectionElements.size(); i++) {
			ClassicCounter<String> counter = features.computeFeatures(tutorial,
					i, fullTutorial,numbericFeatures, tutorialLevel, 
					 sectionLevel, sentenceLevel, 
					 dependencies, relations, relationProb);
			counter.setCount("constant", 1);
			RVFDatum<Boolean, String> d = new RVFDatum<Boolean, String>(
					counter, tutorial.sectionElements.get(i).getLabel());
			dataset.add(d, tutorial.sectionElements.get(i).getSection()
					.getSubTitle(), tutorial.sectionElements.get(i)
					.getApiElement().getAPIElementName());
		}

		return dataset;
	}

	private static Pair<RVFDataset<Boolean, String>, RVFDataset<Boolean, String>> split(
			RVFDataset<Boolean, String> dataset, int fold, int devIndex) {

		int devSize = (int) (dataset.size() / fold);
		// int trainSize = dataset.size() - devSize;

		int devStartIndex = (int) ((dataset.size() / fold) * devIndex);

		RVFDataset<Boolean, String> dev = new RVFDataset<Boolean, String>();
		RVFDataset<Boolean, String> train = new RVFDataset<Boolean, String>();

		for (int i = 0; i < dataset.size(); i++) {
			if (i >= devStartIndex && i < devSize + devStartIndex)
				dev.add(dataset.getRVFDatum(i), dataset.getRVFDatumSource(i),
						dataset.getRVFDatumId(i));
			else
				train.add(dataset.getRVFDatum(i), dataset.getRVFDatumSource(i),
						dataset.getRVFDatumId(i));
		}

		// int[][] devData = new int[devSize][];
		// double[][] devValues = new double[devSize][];
		// int[] devLabels = new int[devSize];
		//
		// int[][] trainData = new int[trainSize][];
		// double[][] trainValues = new double[trainSize][];
		// int[] trainLabels = new int[trainSize];
		//
		//
		// System.arraycopy(dataset.getDataArray(), devStartIndex, devData, 0,
		// devSize);
		// System.arraycopy(dataset.getValuesArray(), devStartIndex, devValues,
		// 0, devSize);
		// System.arraycopy(dataset.getLabelsArray(), devStartIndex, devLabels,
		// 0, devSize);
		//
		// System.arraycopy(dataset.getDataArray(), 0, trainData, 0,
		// devStartIndex);
		// System.arraycopy(dataset.getValuesArray(), 0, trainValues, 0,
		// devStartIndex);
		// System.arraycopy(dataset.getLabelsArray(), 0, trainLabels, 0,
		// devStartIndex);
		//
		// System.arraycopy(dataset.getDataArray(), devStartIndex+devSize,
		// trainData, devStartIndex, trainSize-devStartIndex);
		// System.arraycopy(dataset.getValuesArray(), devStartIndex+devSize,
		// trainValues, devStartIndex, trainSize-devStartIndex);
		// System.arraycopy(dataset.getLabelsArray(), devStartIndex+devSize,
		// trainLabels, devStartIndex, trainSize-devStartIndex);
		//
		// RVFDataset<Boolean, String> dev = new RVFDataset<Boolean,
		// String>(dataset.labelIndex(), devLabels, dataset.featureIndex(),
		// devData, devValues);
		// RVFDataset<Boolean, String> train = new RVFDataset<Boolean,
		// String>(dataset.labelIndex(), trainLabels, dataset.featureIndex(),
		// trainData, trainValues);

		return new Pair<RVFDataset<Boolean, String>, RVFDataset<Boolean, String>>(
				train, dev);

	}

}
