import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tutorial.SectionElement;
import tutorial.Tutorial;
import Utilities.IncrementalSet;
import Utilities.Utils;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import features.WordPurityFeatures;

public class WordPurities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sentenceWordPurities();
	}

	protected static void wordPurityOfOverLappingWords(int top) {
		HashMap<String, Double> topwords = new HashMap<String, Double>();
		CSVReader specreader;
		CSVReader purityreader;

		HashMap<String, Double> datetimePurity = new HashMap<String, Double>();

		int i = 0;
		try {
			specreader = new CSVReader(
					new FileReader(
							"C:/Users/gaya/prog/ebooks/javaspec_wordlist_orderedVerbs.csv"));
			purityreader = new CSVReader(new FileReader(
					"C:/Users/gaya/prog/ebooks/DateTime_orderedVerbs.csv"));

			String[] nextLine;
			while ((nextLine = purityreader.readNext()) != null) {
				datetimePurity
						.put(nextLine[0], Double.parseDouble(nextLine[1]));
			}

			while ((nextLine = specreader.readNext()) != null && i < top) {
				topwords.put(nextLine[0], Double.parseDouble(nextLine[1]));
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Finding overlapping words in top " + top);
		System.out.println("----------------------------");

		int overlapNum = 0;
		for (String word : topwords.keySet()) {
			if (datetimePurity.get(word) != null) {
				System.out.println(word + "," + topwords.get(word) + ","
						+ datetimePurity.get(word));
				overlapNum++;
			}
		}

		System.out.println("number of overlapping words is " + overlapNum);
		System.out.println();

	}

	protected static void wordPurityStat() throws IOException {
		HashMap<String, Double> topwords = new HashMap<String, Double>();
		CSVReader specreader;

		int i = 0;
		try {
			specreader = new CSVReader(new FileReader(
					"C:/Users/gaya/prog/ebooks/javaspec_wordlist_ordered.csv"));

			String[] nextLine;
			while ((nextLine = specreader.readNext()) != null) {
				topwords.put(nextLine[0], Double.parseDouble(nextLine[1]));
				i++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);
		WordPurityFeatures.eval(tutorial, 0);

		// contains words stat for each API
		HashMap<String, List<List<Double>>> wordStat = new HashMap<String, List<List<Double>>>();

		for (String apiElement : tutorial.sectionElementsPerElement.keySet()) {
			Set<String> words = new HashSet<String>();
			if (WordPurityFeatures.wordsRelSection.get(apiElement) != null)
				words.addAll(WordPurityFeatures.wordsRelSection.get(apiElement)
						.keySet());
			if (WordPurityFeatures.wordsNotRelSection.get(apiElement) != null)
				words.addAll(WordPurityFeatures.wordsNotRelSection.get(
						apiElement).keySet());

			for (String wordperAPI : words) {
				// 0-freq in rel sections,
				// 1-freq in not rel sections
				// 2-rpurity
				// 3-gpurity
				List<Double> wordStatperAPI = new ArrayList<Double>();

				Double relcount = (WordPurityFeatures.wordsRelSection
						.get(apiElement) == null) ? 0
						: (WordPurityFeatures.wordsRelSection.get(apiElement)
								.get(wordperAPI) == null) ? 0
								: WordPurityFeatures.wordsRelSection.get(
										apiElement).get(wordperAPI);
				Double notrelCount = (WordPurityFeatures.wordsNotRelSection
						.get(apiElement) == null) ? 0
						: (WordPurityFeatures.wordsNotRelSection
								.get(apiElement).get(wordperAPI) == null) ? 0
								: WordPurityFeatures.wordsNotRelSection.get(
										apiElement).get(wordperAPI);

				Double rpurity = relcount / (relcount + notrelCount);
				Double gpurity = relcount
						/ WordPurityFeatures.wordsPerTutorial.get(wordperAPI);

				wordStatperAPI.add(relcount);
				wordStatperAPI.add(notrelCount);
				wordStatperAPI.add(rpurity);
				wordStatperAPI.add(gpurity);

				if (wordStat.get(wordperAPI) == null) {
					List<List<Double>> stat = new ArrayList<List<Double>>();
					stat.add(wordStatperAPI);
					wordStat.put(wordperAPI, stat);
				} else {
					List<List<Double>> stat = wordStat.get(wordperAPI);
					stat.add(wordStatperAPI);
					wordStat.put(wordperAPI, stat);
				}

			}

		}

		// Aggregated statistics, cointains:
		// word,
		// freq in spec
		// freq in tutorial
		// number of times rpurity is 1.0
		// number of times gpurity is 1.0

		// MIN, AVG, MAX each of these
		// freq in rel sections,
		// freq in not rel sections
		// rpurity
		// gpurity

		HashMap<String, List<Double>> wordAggStat = new HashMap<String, List<Double>>();

		CSVWriter writer = new CSVWriter(new FileWriter(
				"C:/Users/gaya/prog/ebooks/wordpurityvstopwords.csv"));

		for (String word : topwords.keySet()) {
			Double specFreq = topwords.get(word);
			Double tutFreq = WordPurityFeatures.wordsPerTutorial.get(word);

			Double count_rpurity1 = 0.0;
			Double count_gpurity1 = 0.0;

			Double minrelFreq = Double.MAX_VALUE;
			Double avgrelFreq = 0.0;
			Double maxrelFreq = 0.0;

			Double minnotrelFreq = Double.MAX_VALUE;
			Double avgnotrelFreq = 0.0;
			Double maxnotrelFreq = 0.0;

			Double minrpurity = Double.MAX_VALUE;
			Double avgrpurity = 0.0;
			Double maxrpurity = 0.0;

			Double mingpurity = Double.MAX_VALUE;
			Double avggpurity = 0.0;
			Double maxgpurity = 0.0;

			List<List<Double>> wordStats = wordStat.get(word);
			if (wordStats != null) {
				for (List<Double> stat : wordStats) {
					if (stat.get(2) == 1.0)
						count_rpurity1++;
					if (stat.get(3) == 1.0)
						count_gpurity1++;

					if (stat.get(0) < minrelFreq)
						minrelFreq = stat.get(0);
					if (stat.get(0) > maxrelFreq)
						maxrelFreq = stat.get(0);
					avgrelFreq += stat.get(0) / wordStats.size();

					if (stat.get(1) < minnotrelFreq)
						minnotrelFreq = stat.get(1);
					if (stat.get(1) > maxnotrelFreq)
						maxnotrelFreq = stat.get(1);
					avgnotrelFreq += stat.get(1) / wordStats.size();

					if (stat.get(2) < minrpurity)
						minrpurity = stat.get(2);
					if (stat.get(2) > maxrpurity)
						maxrpurity = stat.get(2);
					avgrpurity += stat.get(2) / wordStats.size();

					if (stat.get(3) < mingpurity)
						mingpurity = stat.get(3);
					if (stat.get(3) > maxgpurity)
						maxgpurity = stat.get(3);
					avggpurity += stat.get(3) / wordStats.size();

				}

				List<Double> wordStatValues = new ArrayList<Double>();
				wordStatValues.add(specFreq);
				wordStatValues.add(tutFreq);
				wordStatValues.add(count_rpurity1);
				wordStatValues.add(count_gpurity1);

				wordStatValues.add(minrelFreq);
				wordStatValues.add(avgrelFreq);
				wordStatValues.add(maxrelFreq);

				wordStatValues.add(minnotrelFreq);
				wordStatValues.add(avgnotrelFreq);
				wordStatValues.add(maxnotrelFreq);

				wordStatValues.add(minrpurity);
				wordStatValues.add(avgrpurity);
				wordStatValues.add(maxrpurity);

				wordStatValues.add(mingpurity);
				wordStatValues.add(avggpurity);
				wordStatValues.add(maxgpurity);

				wordAggStat.put(word, wordStatValues);
				writer.writeNext(new String[] { word, specFreq.toString(),
						tutFreq.toString(), count_rpurity1.toString(),
						count_gpurity1.toString(), minrelFreq.toString(),
						avgrelFreq.toString(), maxrelFreq.toString(),
						minnotrelFreq.toString(), avgnotrelFreq.toString(),
						maxnotrelFreq.toString(), minrpurity.toString(),
						avgrpurity.toString(), maxrpurity.toString(),
						mingpurity.toString(), avggpurity.toString(),
						maxgpurity.toString(), });

			} else {
				tutFreq = 0.0;
				minrelFreq = 0.0;
				minnotrelFreq = 0.0;
				minrpurity = 0.0;
				mingpurity = 0.0;

				writer.writeNext(new String[] { word, specFreq.toString(),
						tutFreq.toString(), count_rpurity1.toString(),
						count_gpurity1.toString(), minrelFreq.toString(),
						avgrelFreq.toString(), maxrelFreq.toString(),
						minnotrelFreq.toString(), avgnotrelFreq.toString(),
						maxnotrelFreq.toString(), minrpurity.toString(),
						avgrpurity.toString(), maxrpurity.toString(),
						mingpurity.toString(), avggpurity.toString(),
						maxgpurity.toString(), });
			}

		}

		writer.close();
	}

	protected static void printTop10Words() {
		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);

		WordPurityFeatures.eval(tutorial, 0);

		for (String apiElement : tutorial.sectionElementsPerElement.keySet()) {
			try {

				Set<String> words = new HashSet<String>();
				if (WordPurityFeatures.wordsRelSection.get(apiElement) != null)
					words.addAll(WordPurityFeatures.wordsRelSection.get(
							apiElement).keySet());
				if (WordPurityFeatures.wordsNotRelSection.get(apiElement) != null)
					words.addAll(WordPurityFeatures.wordsNotRelSection.get(
							apiElement).keySet());

				CSVWriter writer = new CSVWriter(new FileWriter(
						"tutorials/WordPurity/" + apiElement + ".csv", true));

				for (String word : words) {
					System.out.println(apiElement + "-" + word);
					Double relcount = (WordPurityFeatures.wordsRelSection
							.get(apiElement) == null) ? 0
							: (WordPurityFeatures.wordsRelSection.get(
									apiElement).get(word) == null) ? 0
									: WordPurityFeatures.wordsRelSection.get(
											apiElement).get(word);
					Double notrelCount = (WordPurityFeatures.wordsNotRelSection
							.get(apiElement) == null) ? 0
							: (WordPurityFeatures.wordsNotRelSection.get(
									apiElement).get(word) == null) ? 0
									: WordPurityFeatures.wordsNotRelSection
											.get(apiElement).get(word);
					writer.writeNext(new String[] {
							word,
							relcount.toString(),
							notrelCount.toString(),
							WordPurityFeatures.wordsPerTutorial.get(word)
									.toString(),
							WordPurityFeatures.wordTags.get(word) });
				}
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// for(int i = 0; i<tutorial.sectionElements.size(); i++)
		// {
		// if (tutorial.sectionElements.get(i).getLabel())
		// {
		// System.out.print(tutorial.sectionElements.get(i).getApiElement().getAPIElementName()
		// + " - ");
		// for(Entry<String, Double> entry:wordList.entrySet())
		// {
		// System.out.print(entry.getKey()+"(" +entry.getValue()+ "), ");
		// }
		// System.out.println();
		// }
		// }
	}

	public static void sentenceWordPurities() {
		IncrementalSet releventSet = new IncrementalSet();
		IncrementalSet notReleventSet = new IncrementalSet();

		Tutorial tutorial = new Tutorial();
		tutorial.LoadTutorial("tutorials/jodatime.csv", true, true);

		IncrementalSet setToAdd;
		for (SectionElement sectionEl : tutorial.sectionElements) {
			List<CoreMap> apiSentences = sectionEl
					.getAPIElementSentences(false);
			if (sectionEl.getLabel())
				setToAdd = releventSet;
			else
				setToAdd = notReleventSet;

			boolean cltmet = false;
			boolean isnextWordCLT = false;
			for (CoreMap sent : apiSentences) {
				for (CoreLabel token : sent.get(TokensAnnotation.class)) {
					String word = token.get(LemmaAnnotation.class);
					String tag = token.get(PartOfSpeechAnnotation.class);

					if (tag.length() > 2)
						tag = tag.substring(0, 2);
					if (isnextWordCLT) {
						tag = "CLT";
						token.setTag(tag);
						isnextWordCLT = false;
					}

					if (word.equals("clt"))
						cltmet = true;
					else if (word.equals(":") && cltmet) {
						isnextWordCLT = true;
						cltmet = false;
					} else if (Character.isLetter(word.charAt(0))
							&& !Utils.isStopWord(word)) {
						if (!tag.equals("CLT"))
							word = word.toLowerCase();

						setToAdd.put(tag + ":" + word);

					}
				}
			}
		}

		releventSet.dump("dumps/relevent.csv", true);
		notReleventSet.dump("dumps/notrelevent.csv", true);
	}
}
