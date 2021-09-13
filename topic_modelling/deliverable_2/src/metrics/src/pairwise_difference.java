import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import HungarianAlgorithm.HungarianAlgorithm;

public class PairwiseDifference extends AbstractMetric {
	final static String CONFIG_ID_ENABLED = "enabled";
	final static String CONFIG_ID_M = "top_M";
	final static String CONFIG_ID_DISTANCE_TYPE = "distance_type";
	
	final static String DISTANCE_TYPE_MANHATTAN = "manhattan";
	final static String DISTANCE_TYPE_EUCLIDEAN = "euclidean";
	final static String DISTANCE_TYPE_CHEBYSHEV = "chebyshev";
	final static String DISTANCE_TYPE_COSINE = "cosine";
	
//	Default value for top-M
	public static boolean enabled;
	public static int M;
	public static String distance_type;
	
	@Override
	public String getMetricName() {
		return "pairwise_difference";
	}

	public void loadParameters(Configuration config) {
		/** Load the config values for use **/
		
		enabled = loadParameterBoolean(config,CONFIG_ID_ENABLED,true);
		M = loadParameterInteger(config,CONFIG_ID_M,32);
		
		distance_type = loadParameterString(config,CONFIG_ID_DISTANCE_TYPE,DISTANCE_TYPE_MANHATTAN);
		
		if (!( distance_type.equals(DISTANCE_TYPE_MANHATTAN)
				|| distance_type.equals(DISTANCE_TYPE_EUCLIDEAN)
				|| distance_type.equals(DISTANCE_TYPE_CHEBYSHEV)
				|| distance_type.equals(DISTANCE_TYPE_COSINE)))
		{
			System.err.println(distance_type + " is not a recognised distance type, using default: " + DISTANCE_TYPE_MANHATTAN);
			distance_type = DISTANCE_TYPE_MANHATTAN;
		}
	}
	
	@Override
	public TopicModel[] calculate(TopicModel[] models, Configuration configuration)  {
//		Load the config values for use
		loadParameters(configuration);
		
		if(!enabled) {
			// Metric disabled so it doesn't run
			System.out.println("This metric has been disabled in the configuration file");
		} else {
			TopicModel[] newModels;
			for (int i=0; i<models.length; i++)
			{
				for (int j=i+1; j<models.length; j++)
				{
//					Only perform pairwise difference if the model's have the same number of topics
					if (models[i].getTopics().size() == models[j].getTopics().size())
					{
						newModels = calculatePairwiseBetweenTwoModels(models[i], models[j]);
						models[i]= newModels[0];
						models[j] = newModels[1];
					}
				}
			}
		}
		
		return models;
		
	}
	
	private TopicModel[] calculatePairwiseBetweenTwoModels(TopicModel firstModel, TopicModel secondModel) {
		
//		Get the topics
		Map<String, Map<String, Integer>> firstModelTopics = firstModel.getTopics();
		Map<String, Map<String, Integer>> secondModelTopics = secondModel.getTopics();
		
		List<Map<String, Double>> firstNormalisedTopMVectors = new ArrayList<>();
		List<Map<String, Double>> secondNormalisedTopMVectors = new ArrayList<>();
		
//		Get top-m words and normalise for first model
		for (String topicId:firstModelTopics.keySet())
		{
//			Get top M-words and Normalise
			Map<String, Integer> topic = firstModelTopics.get(topicId);
			topic = getTopWords(topic);
			Map<String, Double> normalisedTopic = normaliseWeights(topic);
			
//			Add to list of topics
			firstNormalisedTopMVectors.add(normalisedTopic);
		}
		
//		Get top-m words and normalise for second model
		for (String topicId:secondModelTopics.keySet())
		{
//			Get top M-words and Normalise
			Map<String, Integer> topic = secondModelTopics.get(topicId);
			topic = getTopWords(topic);
			Map<String, Double> normalisedTopic = normaliseWeights(topic);
			
//			Add to list of topics
			secondNormalisedTopMVectors.add(normalisedTopic);
		}
		
//		Initialise distance matrix
		int firstNumTopics = firstNormalisedTopMVectors.size();
		int secondNumTopics = secondNormalisedTopMVectors.size();
		double[][] distanceMatrix = new double[firstNumTopics][secondNumTopics];
		
			
		Map<String, Double> topic1, topic2;
//		Populate the distance matrix
		for (int i=0; i<firstNumTopics; i++)
		{
			for (int j=i; j<secondNumTopics; j++)
			{
				topic1 = firstNormalisedTopMVectors.get(i);
				topic2 = secondNormalisedTopMVectors.get(j);
				distanceMatrix[i][j] = distanceBetweenTopics(topic1, topic2);
				distanceMatrix[j][i] = distanceBetweenTopics(topic1, topic2);
			}
		}
		
//		Perform the Hungarian assignment algorithm
		HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(copyDistanceMatrix(distanceMatrix));
		int[][] assignment = hungarianAlgorithm.findOptimalAssignment();
		
//		Initialise distance
		double totalDistance = 0.0;
		
//		Loop over assignments and sum the distances from the matrix
		int row, col;
		for (int i=0; i<assignment.length; i++)
		{
			col = assignment[i][0];
			row = assignment[i][1];

			totalDistance += distanceMatrix[col][row];
		}

//		Get model IDs
		String firstModelID = firstModel.getID();
		String secondModelID = secondModel.getID();
		
//		Maps to store the differences for each model
		Map<String, Double> firstMap, secondMap;
		
//		For both models, load map if it exists, otherwise make one
		if (firstModel.getMetrics().containsKey(getMetricName())) {
			firstMap = (Map<String, Double>) firstModel.getMetrics().get(getMetricName());
		} 
		else {
			firstMap = new HashMap<String, Double>();
		}
		if (secondModel.getMetrics().containsKey(getMetricName())) {
			secondMap = (Map<String, Double>) secondModel.getMetrics().get(getMetricName());
		} 
		else {
			secondMap = new HashMap<String, Double>();
		}
		
//		Add new calculated distances to respective model's maps
		firstMap.put(secondModelID, totalDistance);
		secondMap.put(firstModelID, totalDistance);
		
//		Add calculated differences to both models
		firstModel.addMetrics(getMetricName(), firstMap);
		secondModel.addMetrics(getMetricName(), secondMap);
		
//		Make a pair
		TopicModel[] pair = {firstModel, secondModel};
		return pair;
	}
	
//	Method to get the top M words from a topic, replacing the rest with zero
	private Map<String, Integer> getTopWords(Map<String, Integer> inputVector)
	{
		Map<String, Integer> topWordsVector = new HashMap<>();
		List<String> topWordsList = new ArrayList<>();
		String maxWord;
		int maxVal, currentVal;
		
//		Loop M times to get top M words
		for (int i=0; i<M; i++)
		{
			maxWord = null;
			maxVal = 0;
			
//			Find next largest
			for (String currentWord: inputVector.keySet())
			{
				currentVal = inputVector.get(currentWord); 
//				Check if word has higher weighting
				if (currentVal > maxVal)
				{
					
	//				Check that the word hasn't already been counted
					if (!topWordsList.contains(currentWord))
					{
						maxWord = currentWord;
						maxVal = currentVal;
					}
				}
			}
						
//			Add if a word was found
			if (maxWord != null)
			{
				topWordsList.add(maxWord);
			}
		}
		
//		Add to the new vector
		for (String word: inputVector.keySet())
		{
//			Add the existing weight if found in the top words list
			if (topWordsList.contains(word))
			{
				topWordsVector.put(word, inputVector.get(word));
			}
//			Add zero otherwise
			else
			{
				topWordsVector.put(word, 0);
			}
		}
		
		return topWordsVector;
	}
	
//	Method to calculate normalised weights (probabilities) for words in a topic
	private Map<String, Double> normaliseWeights(Map<String, Integer> inputVector)
	{
		Map<String, Double> normalisedVector = new HashMap<>();
		int sum = 0; 
		
//		Calculate the sum of weights
		for (String word: inputVector.keySet())
		{
			sum += inputVector.get(word);
		}
		
//		Calculate the normalised weights
		for (String word: inputVector.keySet())
		{
			normalisedVector.put(word, Double.valueOf((double) inputVector.get(word) / sum) );
		}
		
		return normalisedVector;
	}
	
//	Method to calculate the distance between two topics
	private double distanceBetweenTopics(Map<String, Double> topic1, Map<String, Double> topic2)
	{
		
		if (distance_type.equals(DISTANCE_TYPE_MANHATTAN))
		{
			return manhattanDistance(topic1, topic2);
		}
		else if (distance_type.equals(DISTANCE_TYPE_EUCLIDEAN))
		{
			return euclideanDistance(topic1, topic2);
		}
		else if (distance_type.equals(DISTANCE_TYPE_CHEBYSHEV))
		{
			return chebyshevDistance(topic1, topic2);
		}
		else if (distance_type.equals(DISTANCE_TYPE_COSINE))
		{
			return cosineDistance(topic1, topic2);
		}
//		Should never reach this block
		else
		{
			return -1;
		}
	}
	
	private double manhattanDistance(Map<String, Double> topic1, Map<String, Double> topic2)
	{
		double distance = 0;
		
		Set<String> words = new HashSet<>((topic1.keySet()));
		words.addAll(topic1.keySet());
		
		Double weight1, weight2;
		for (String word: words)
		{
//			Get the word weights
			weight1 = getWeightSafe(topic1, word);
			weight2 = getWeightSafe(topic2, word);
			
//			Calculate the absolute difference between the weights and add to total
			distance += Math.abs(weight1 - weight2);
		}
		
		return distance;
	}

	private double euclideanDistance(Map<String, Double> topic1, Map<String, Double> topic2)
	{
		double distance = 0;
		
		Set<String> words = new HashSet<>((topic1.keySet()));
		words.addAll(topic1.keySet());
		
		Double weight1, weight2;
		for (String word: words)
		{
//			Get the word weights
			weight1 = getWeightSafe(topic1, word);
			weight2 = getWeightSafe(topic2, word);
			
//			Calculate the square difference between the weights and add to sum
			distance += Math.pow(weight1 - weight2, 2);
		}
		
//		Sqrt the sum of square differences
		distance = Math.sqrt(distance);
		
		return distance;
	}
	
	private double chebyshevDistance(Map<String, Double> topic1, Map<String, Double> topic2)
	{
		double maxDistance = 0;
		
		Set<String> words = new HashSet<>((topic1.keySet()));
		words.addAll(topic1.keySet());
		
		double weight1, weight2;
		double diff;
		for (String word: words)
		{
//			Get the word weights
			weight1 = getWeightSafe(topic1, word);
			weight2 = getWeightSafe(topic2, word);
			
//			Find the larger of the differences
			diff = Math.abs(weight1 - weight2);
			if (diff > maxDistance)
			{
				maxDistance = diff;
			}
		}
		
		return maxDistance;
	}

	private double cosineDistance(Map<String, Double> topic1, Map<String, Double> topic2)
	{		
		Set<String> words = new HashSet<>((topic1.keySet()));
		words.addAll(topic1.keySet());
		
		double weight1, weight2;
		double aProdBSum = 0;
		double aSquaredSum = 0;
		double bSquaredSum = 0;
		
		for (String word: words)
		{
//			Get the word weights
			weight1 = getWeightSafe(topic1, word);
			weight2 = getWeightSafe(topic2, word);
			
			aProdBSum += weight1 * weight2;
			aSquaredSum += Math.pow(weight1, 2);
			bSquaredSum += Math.pow(weight2, 2);
		}
		
		double similarity = aProdBSum / (Math.sqrt(aSquaredSum) * Math.sqrt(bSquaredSum));
		
		double distance = 1 - similarity;
		return distance;
	}
	
//	Return the weight of a word in a topic, or zero if it can't be found
	private double getWeightSafe(Map<String, Double> topic, String word)
	{
		double weight;
//		Check if topic2 has this word, if not use weight of 0
		if (topic.containsKey(word))
		{
			weight = topic.get(word);
		}
		else
		{
			weight = 0.0;
		}
		
		return weight;
	}

//	Method to deep copy a matrix
	private double[][] copyDistanceMatrix(double[][] distanceMatrix)
	{
		int firstNumTopics = distanceMatrix.length;
		int secondNumTopics = distanceMatrix[0].length;
		double[][] newDistanceMatrix = new double[firstNumTopics][secondNumTopics];
		
		for (int i=0; i<firstNumTopics; i++)
		{
			for (int j=0; j<secondNumTopics; j++)
			{
				newDistanceMatrix[i][j] = distanceMatrix[i][j];
			}
		}
		
		return newDistanceMatrix;
	}
	
//	Display distance matrix for debug purposes
	private void printDistanceMatrix(double[][] distanceMatrix)
	{
		int firstNumTopics = distanceMatrix.length;
		int secondNumTopics = distanceMatrix[0].length;
		
		System.out.println("Distance Matrix:");
		for (int i=0; i<firstNumTopics; i++)
		{
			for (int j=0; j<secondNumTopics; j++)
			{
				System.out.print(distanceMatrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

}
