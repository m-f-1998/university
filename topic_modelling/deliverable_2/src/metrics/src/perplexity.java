import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Perplexity extends AbstractMetric {
	// Config IDs for parameters
	final static String CONFIG_ID_ENABLED = "enabled";
	final static String CONFIG_ID_CORPUS_PATH = "corpus_path";
	final static String CONFIG_ID_LOG_BASE = "log_base";
	final static String CONFIG_ID_WEIGHT_SMOOTHING = "weight_smoothing";

	// Parameters, with preloaded defaults
	static boolean enabled;
	static String corpusPath;
	static double logBase;
	static double weightSmoothing;

	@Override
	public String getMetricName() {
		return "perplexity";
	}
	
	public void loadParameters(Configuration config) {
		enabled = loadParameterBoolean(config,CONFIG_ID_ENABLED,true);
		logBase = loadParameterDouble(config,CONFIG_ID_LOG_BASE,2.0);
		weightSmoothing = loadParameterDouble(config,CONFIG_ID_WEIGHT_SMOOTHING,0.00001);
		corpusPath = loadParameterString(config,CONFIG_ID_CORPUS_PATH,null);
	}

	@Override
	public TopicModel[] calculate(TopicModel[] models, Configuration configuration) {
		// Load the config values for use
		loadParameters(configuration);
		
		// Run calculation if a corpus path has been specified
		if (!enabled) {
			// Metric disabled so it doesn't run
			System.out.println("This metric has been disabled in the configuration file");
		} else if (corpusPath == null) {
			// No path to a test corpus was specified
			System.err.println("Error in " + getMetricName() + ": No path to a test corpus was specified");
		} else {
			String[] corpusItems;
			try {
				corpusItems = readLinesFromFile(corpusPath);
				
				if (corpusItems.length != 0) {
					// Apply perplexity metric to all models
					for (TopicModel model : models) {
						Map<String, Map<String, Double>> nm = normaliseModel(model);
						model.addMetrics(getMetricName(), calculatePerplexity(nm, corpusItems));
					}
				} else {
					System.err.println("Error in " + getMetricName() + ": Corpus file empty");
				}
				
			} catch (FileNotFoundException e) {
				System.err.println("Error in " + getMetricName() + ": Corpus file not found");
			} catch (IOException e) {
				System.err.println("Error in " + getMetricName() + ": File could not be opened/closed");
			}
		}

		return models;
	}

	public String[] readLinesFromFile(String filepath) throws FileNotFoundException, IOException{
		ArrayList<String> lineList = new ArrayList<>();

		FileReader fr = new FileReader(filepath);
		BufferedReader br = new BufferedReader(fr);

		try {
			String line = br.readLine();
			while (line != null) {
				lineList.add(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			System.err.println("Error in " + getMetricName() + ": Cannot load lines from corpus file");
		}
		br.close();

		return lineList.toArray(new String[lineList.size()]);
	}

	public Map<String, Map<String, Double>> normaliseModel(TopicModel model) {
		Double total = 0.0;

		// Full topics dictionary

		Map<String, Map<String, Integer>> topics = model.getTopics();
		ArrayList<Double> listOfSums = new ArrayList<Double>();

		Map<String, Map<String, Double>> normalisedTopics = new HashMap<>();

		// going through the topics to get their sums
		for (Entry<String, Map<String, Integer>> topic : topics.entrySet()) {
			Map<String, Integer> topicValues = topic.getValue();

			for (Entry<String, Integer> word : topicValues.entrySet()) {
				total = total + word.getValue();
			}

			listOfSums.add(total);
			total = 0.0;
		}

		// going through the topics to change the values off the words from weight to
		// word probabilities
		int counter = 0;
		for (Entry<String, Map<String, Integer>> topic : topics.entrySet()) {
			Map<String, Integer> topicValues = topic.getValue();
			Map<String, Double> normalisedTopicWords = new HashMap<>();
			for (Entry<String, Integer> word : topicValues.entrySet()) {
				normalisedTopicWords.put(word.getKey(),
						Double.valueOf(Double.valueOf(word.getValue()) / listOfSums.get(counter)));
			}
			counter++;
			normalisedTopics.put(topic.getKey(), normalisedTopicWords);
		}
		return normalisedTopics;
	}

	private Map<String, Double> calculatePerplexity(Map<String, Map<String, Double>> normalisedModel,
			String[] corpusItems) {
		int numberOfWordsInCorpus = getTotalWordsFromCorpus(corpusItems);

		// double tpSum = 0;
		Map<String, Double> topicPerplexities = new HashMap<>();

		for (String topicID : normalisedModel.keySet()) {
			Map<String, Double> topic = normalisedModel.get(topicID);
			double corpusProbability = getCorpusProbability(topic, corpusItems);

			double logLikelihoodAvg = corpusProbability / numberOfWordsInCorpus;

			double topicPerplexity = Math.pow(logBase, -logLikelihoodAvg);

			topicPerplexities.put(topicID, topicPerplexity);
		}

		return topicPerplexities;
	}

	public double getCorpusProbability(Map<String, Double> topic, String[] corpusItems) {
		double logLikelihoodSum = 0.0;

		for (String line : corpusItems) {

			String[] sentenceWords = getWordsFromSentence(line);

			logLikelihoodSum += getSentenceProbability(topic, sentenceWords);

		}

		return logLikelihoodSum;
	}

	public double getSentenceProbability(Map<String, Double> topic, String[] line) {
		double count = 0.0;
		for (String word : line) {
			count += getWordProbability(topic, word);
		}
		return count;
	}

	public double getWordProbability(Map<String, Double> topic, String word) {

		if (topic.containsKey(word)) {
			return logB(logBase, topic.get(word));
		} else {
			return logB(logBase, weightSmoothing);
			// return 0.0;
		}

	}

	private String[] getWordsFromSentence(String sentence) {

		String[] tabDelimitedString = sentence.split("\t");

		String[] sentenceWords = tabDelimitedString[2].split(" ");

		return sentenceWords;
	}

	public int getTotalWordsFromCorpus(String[] corpus) {
		int total = 0;

		for (String line : corpus) {
			total += getWordsFromSentence(line).length;
		}

		return total;
	}

	private double logB(double base, double value) {
		return (Math.log(value) / Math.log(base));
	}

}
