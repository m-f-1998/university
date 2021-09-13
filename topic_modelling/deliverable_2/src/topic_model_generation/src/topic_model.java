package malletWrapper;

import java.lang.Runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.HashMap;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.topics.TopicModelDiagnostics;

public class TopicModel {

	private ParallelTopicModel model;
	private int numTopics;
	private InstanceList instances;

	public TopicModel(int numTopics, double alpha_t, double beta_t) {
		if (numTopics > 0 && alpha_t > 0 && beta_t > 0) {
			this.numTopics = numTopics;
			model = new ParallelTopicModel(numTopics, alpha_t, beta_t);
		} else {
			throw new IllegalArgumentException("Parameters must be greater than 0");
		}
	}

	public TopicModel(int numTopics) {
		this.numTopics = numTopics;
		model = new ParallelTopicModel(numTopics);
	}

	/**
	 * 
	 * Parameters needed to infer topics for new documents (includes corpus)
	 * 
	 * @param instances
	 * 
	 */
	void setInstances(InstanceList instances){	
		this.instances = instances;	
		model.addInstances(instances);
	}
	
	InstanceList getInstances() {
		return this.instances;
	}
	
	int getNumTopics() {
		return model.numTopics;
	}

	/**
	 * 
	 * Sets symmetric alpha on topic model to true or false
	 *  
	 * @param symAlpha
	 * 
	 */
	void setSymmetricAlpha(Boolean symAlpha){
		model.setSymmetricAlpha(symAlpha);
	}
	
	double getAlphaSum(){	
		return model.alphaSum;
	}
	
	double getBeta(){	
		return model.beta;
	}
	
	/**
	 * 
	 * Get symmetric alpha from model
	 * 
	 */
	Boolean getSymmetricAlpha() {
		return model.usingSymmetricAlpha;
	}

	/**
	 * 
	 * Sets seed index on topic model
	 * 
	 * @param seed
	 * 
	 */
	void setSeed(int seed) {
		model.setRandomSeed(seed);
	}
	
	/**
	 * 
	 * Get models seed index
	 * 
	 */
	int getSeed() {
		return model.randomSeed;
	}

	/**
	 * 
	 * Run modelling on custom number of threads
	 * 
	 * @param threads
	 * 
	 */
	void setThreads(int threads) {
		model.setNumThreads(threads);
	}

	/**
	 * 
	 * Run modelling on all available processors
	 * 
	 */
	void setMaxThreads() {
		int processors = Runtime.getRuntime().availableProcessors();
		model.setNumThreads(processors);
	}

	/**
	 * 
	 * @param iterations Number of iterations taken to sample the corpus during topic generation
	 * 
	 */
	void setIterations(int iterations) {
		model.setNumIterations(iterations);
	}
	
	int getIterations() {
		return model.numIterations;
	}

	/**
	 * 
	 * @param burn The number of iterations before optimisation starts
	 * 
	 */
	void setBurninPeriod(int burn) {
		model.setBurninPeriod(burn);
	}
	
	int getBurnin() {
		return model.burninPeriod;
	}

	/**
	 * 
	 * Allows the data to better fit the model by allowing some topics to be more prominent than others.
	 * 
	 * @param optim Number of iterations before optimisation occurs
	 * 
	 */
	void setOptimiseInterval(int optim) {
		model.setOptimizeInterval(optim);
	}
	
	int getOptimiseInterval() {
		return model.optimizeInterval;
	}

	/**
	 * 
	 * Prints error to error stream with a stack trace if exception exists, then exists program
	 * 
	 * @param m Error message
	 * @param e Thrown exception
	 * 
	 */
	private void error(String m, Exception e) {
		System.err.println(m);
		if (e != null) e.printStackTrace();
		System.exit(1);
	}

	/**
	 * 
	 * Run the model if instantiated
	 * 
	 */
	public void runModel() {
		try { model.estimate(); }
		catch (IOException | NullPointerException e) { error("Model not instantiated", e); }
	}

	/**
	 * 
	 * Measures whether the words in a topic tend to coexist together.
	 * 
	 * @return Coherence scores for model on number of topics produced, in production order
	 * 
	 */
	HashMap<String, Double> getCoherence() {
		TopicModelDiagnostics m;
		HashMap<String, Double> coherencies = new HashMap<>();
		try {
			m = new TopicModelDiagnostics(model, numTopics);
			int i = 0;
			for (Double coherency : m.getCoherence().scores) {
				coherencies.put("topic" + i, coherency);
				i++;
			}
			
		} catch (NullPointerException e) {
			error("Model not instantiated", null);
		}
		return coherencies;
	}

	/**
	 * 
	 * @return Sorted set of word IDs to count pairs in a topic
	 * 
	 */
	private ArrayList<TreeSet<IDSorter>> getSortedWords() {
		if (model == null) error("Model not instantiated", null);
		try {
			return model.getSortedWords();
		} catch (NullPointerException e) {
			return new ArrayList<>();
		}
	}
	
	/**
	 * 
	 * @return Parameters needed to infer topics for new documents (includes corpus)
	 * 
	 */
	TopicInferencer getInference() {
		if (model == null) error("Model not instantiated", null);
		return model.getInferencer();
	}

	/**
	 * 
	 * @return Model's log likelihood (unnormalised)
	 * 
	 */
	double getLL(){
		return model.modelLogLikelihood();
	}

	/**
	 * 
	 * @return Model's log likelihood (normalised)
	 * 
	 */
	double getLLPerNumToken(){
		return model.modelLogLikelihood() / model.totalTokens;
	}

	/**
	 * 
	 * @return Topic IDs in a model to a HashMap with key=words and value=word-weight
	 * 
	 */
	LinkedHashMap<String, HashMap<String, Integer>> wordWeight() {
		LinkedHashMap<String, HashMap<String, Integer>> res = new LinkedHashMap<>();
		Alphabet dataAlphabet = instances.getDataAlphabet(); // all characters in the instance list
		ArrayList<TreeSet<IDSorter>> topicSortedWords = getSortedWords();
		
		for (int topic = 0; topic < model.numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
			HashMap<String, Integer> wordProbs = new HashMap<>();

			while (iterator.hasNext()) { // while words exist in each topic
				IDSorter idCountPair = iterator.next();
				wordProbs.put(String.valueOf(dataAlphabet.lookupObject(idCountPair.getID())), (int) idCountPair.getWeight());
			}

			res.put("topic" + topic, wordProbs);

		}

		return res;
	}
}