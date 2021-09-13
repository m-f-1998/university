import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestPerplexity {
	TopicModel model1;
	TopicModel model2;
	TopicModel model3;
	Perplexity perplexity;
	Configuration conf;

	@Before
	public void setUp() throws Exception {
//		Load some test models
		String model1String = new String(Files.readAllBytes(Paths.get("perplexityTesting/testModel1.json")));
		String model2String = new String(Files.readAllBytes(Paths.get("perplexityTesting/testModel2.json")));
		String model3String = new String(Files.readAllBytes(Paths.get("perplexityTesting/testModel3.json")));

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		model1 = gson.fromJson(model1String, TopicModel.class);
		model2 = gson.fromJson(model2String, TopicModel.class);
		model3 = gson.fromJson(model3String, TopicModel.class);

		perplexity = new Perplexity();

//		Load testing configuration file
		conf = ConfigParser.parseConfigFile("perplexityTesting/testPerplexityConfig.conf");
	}

	@Test
	public void loadConfigParameters() {
		assertEquals(true,perplexity.loadParameterBoolean(conf,"enabled",false));
		
		assertEquals("perplexityTesting/perplexityTestCorpus.txt",
		perplexity.loadParameterString(conf,"corpus_path",null)
		);
		
		assertEquals(2.0,perplexity.loadParameterDouble(conf,"log_base",0.0),0);
		assertEquals(0.00001,perplexity.loadParameterDouble(conf,"weight_smoothing",0.0),0);
	}
	
	@Test
	public void readCorpus() {
		String emptyPath = "";
		String invalidPath = "anInvalidPath";
		String goodPath = "perplexityTesting/perplexityTestCorpus.txt";
		
		String[] expectedNormalList = {
				"0	en	word0 word1 word1 word0 word2",
				"1	en	word2 word1 word2 word1 word1"
				};

		assertThrows(FileNotFoundException.class,
				() ->{perplexity.readLinesFromFile(emptyPath);} );
		assertThrows(FileNotFoundException.class,
				() ->{perplexity.readLinesFromFile(invalidPath);} );
		try {
			assertArrayEquals(expectedNormalList, perplexity.readLinesFromFile(goodPath));
		} catch (FileNotFoundException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void normalisationOfTopics() {
		/** Ensure that word counts are correctly normalised into probabilities
		 * 
		 * Expected value for each word should be:
		 * Total_word_count / Specific_word_count
		 **/
		Map<String, Map<String, Double>> normalModel1 = perplexity.normaliseModel(model1);
		Map<String, Map<String, Double>> normalModel2 = perplexity.normaliseModel(model2);
		Map<String, Map<String, Double>> normalModel3 = perplexity.normaliseModel(model3);

		assertEquals(0.5, normalModel1.get("topic0").get("word0"), 0);
		assertEquals(0.5, normalModel1.get("topic0").get("word1"), 0);
		assertEquals(0.5, normalModel1.get("topic1").get("word0"), 0);
		assertEquals(0.5, normalModel1.get("topic1").get("word1"), 0);

		assertEquals(1.0 / 3, normalModel2.get("topic0").get("word0"), 0);
		assertEquals(2.0 / 3, normalModel2.get("topic0").get("word1"), 0);
		assertEquals(2.0 / 3, normalModel2.get("topic1").get("word0"), 0);
		assertEquals(1.0 / 3, normalModel2.get("topic1").get("word1"), 0);

		assertEquals(1.0 / 4, normalModel3.get("topic0").get("word0"), 0);
		assertEquals(3.0 / 4, normalModel3.get("topic0").get("word1"), 0);
		assertEquals(3.0 / 4, normalModel3.get("topic1").get("word0"), 0);
		assertEquals(1.0 / 4, normalModel3.get("topic1").get("word1"), 0);
	}

	@Test
	public void wordProbability() {
		/**
		 * Test the function "getWordProbability"
		 * 
		 * Expected value should be:
		 * log2(word_probability) IF word is within topic
		 * log2(smoothing_value) IF word is not within topic
		 **/
		Map<String, Map<String, Double>> normalModel1 = perplexity.normaliseModel(model1);
		
		// log2(0.00001) = approx. -16.609
		assertEquals(-16.609, perplexity.getWordProbability(normalModel1.get("topic0"), ""), 0.01);
		assertEquals(-1.0, perplexity.getWordProbability(normalModel1.get("topic0"), "word0"), 0);
		assertEquals(-1.0, perplexity.getWordProbability(normalModel1.get("topic0"), "word1"), 0);
		assertEquals(-16.609, perplexity.getWordProbability(normalModel1.get("topic0"), "word2"), 0.01);
		assertEquals(-1.0, perplexity.getWordProbability(normalModel1.get("topic1"), "word0"), 0);
		assertEquals(-1.0, perplexity.getWordProbability(normalModel1.get("topic1"), "word1"), 0);
		assertEquals(-16.609, perplexity.getWordProbability(normalModel1.get("topic1"), "word2"), 0.01);
	}

	@Test
	public void sentenceProbability() {
		/** Test the function "getSentenceProbability"
		 * 
		 *  Expected value should be:
		 *  Sum of word probabilities of all words within the sentence
		 **/
		perplexity.loadParameters(conf);
		
		Map<String, Map<String, Double>> normalModel1 = perplexity.normaliseModel(model1);
		Map<String, Map<String, Double>> normalModel2 = perplexity.normaliseModel(model2);

		String[] sentence0 = {};
		String[] sentence1 = { "word0", "word1", "word1", "word0", "word2" };
		String[] sentence2 = { "word2", "word1", "word2", "word1", "word1" };

		assertEquals(0, perplexity.getSentenceProbability(normalModel1.get("topic0"), sentence0), 0);
		assertEquals(0, perplexity.getSentenceProbability(normalModel1.get("topic1"), sentence0), 0);
		assertEquals(0, perplexity.getSentenceProbability(normalModel2.get("topic0"), sentence0), 0);
		assertEquals(0, perplexity.getSentenceProbability(normalModel2.get("topic1"), sentence0), 0);
		
		// 4log2(0.5) + log2(0.00001)
		assertEquals(-20.609, perplexity.getSentenceProbability(normalModel1.get("topic0"), sentence1), 0.01);
		// 3log2(0.5) + 2log2(0.00001)
		assertEquals(-36.219, perplexity.getSentenceProbability(normalModel1.get("topic0"), sentence2), 0.01);
		// 4log2(0.5) + log2(0.00001)
		assertEquals(-20.609, perplexity.getSentenceProbability(normalModel1.get("topic1"), sentence1), 0.01);
		// 3log2(0.5) + 2log2(0.00001)
		assertEquals(-36.219, perplexity.getSentenceProbability(normalModel1.get("topic1"), sentence2), 0.01);

		// 2log2(1/3) + 2log2(2/3) + log2(0.00001)
		assertEquals(-20.949, perplexity.getSentenceProbability(normalModel2.get("topic0"), sentence1), 0.01);
		// 3log2(2/3) + 2log2(0.00001)
		assertEquals(-34.974, perplexity.getSentenceProbability(normalModel2.get("topic0"), sentence2), 0.01);
		// 2log2(1/3) + 2log2(2/3) + log2(0.00001)
		assertEquals(-20.949, perplexity.getSentenceProbability(normalModel2.get("topic1"), sentence1), 0.01);
		// 3log2(1/3) + 2log2(0.00001)
		assertEquals(-37.974, perplexity.getSentenceProbability(normalModel2.get("topic1"), sentence2), 0.01);
	}

	@Test
	public void corpusProbability() {
		/** 
		 * Test the function "getCorpusProbability"
		 * 
		 * Expected value should be:
		 * Sum of sentence probabilities for all sentences within the corpus
		 **/
		Map<String, Map<String, Double>> normalModel1 = perplexity.normaliseModel(model1);
		Map<String, Map<String, Double>> normalModel2 = perplexity.normaliseModel(model2);

		String sentence1 = "0	en	word0 word1 word1 word0 word2";
		String sentence2 = "1	en	word2 word1 word2 word1 word1";

		String[] emptyCorpus = {};
		String[] testCorpus = { sentence1, sentence2 };
		
		assertEquals(0, perplexity.getCorpusProbability(normalModel1.get("topic0"), emptyCorpus), 0);
		assertEquals(0, perplexity.getCorpusProbability(normalModel1.get("topic1"), emptyCorpus), 0);
		assertEquals(0, perplexity.getCorpusProbability(normalModel2.get("topic0"), emptyCorpus), 0);
		assertEquals(0, perplexity.getCorpusProbability(normalModel2.get("topic1"), emptyCorpus), 0);

		// 7log2(0.5) + 3log2(0.00001)
		assertEquals(-56.829, perplexity.getCorpusProbability(normalModel1.get("topic0"), testCorpus), 0.01);
		// 7log2(0.5) + 3log2(0.00001)
		assertEquals(-56.829, perplexity.getCorpusProbability(normalModel1.get("topic1"), testCorpus), 0.01);

		// 2log2(1/3) + 5log2(2/3) + 3log2(0.00001)
		assertEquals(-55.924, perplexity.getCorpusProbability(normalModel2.get("topic0"), testCorpus), 0.01);
		// 5log2(1/3) + 2log2(2/3) + 3log2(0.00001)
		assertEquals(-58.927, perplexity.getCorpusProbability(normalModel2.get("topic1"), testCorpus), 0.01);
	}

	@Test
	public void corpusWordCount() {
		/** 
		 * Test the function "getTotalWordsFromCorpus"
		 * 
		 * Expected value should be:
		 * Exact number of document words contained within a corpus
		 * NOTE: This assumes the format above where the document words are after two
		 * markers, one for the document number and one for the language
		 **/
		String sentence1 = "0	language	word01 word02 word03 word04 word05";
		String sentence2 = "1	language	word06 word07 word08 word09 word10";
		String sentence3 = "2	language	word11 word12 word13 word14 word15";

		String[] corpus0Words = {};
		String[] corpus5Words = { sentence1 };
		String[] corpus10Words = { sentence1, sentence2 };
		String[] corpus15Words = { sentence1, sentence2, sentence3 };

		assertEquals(0, perplexity.getTotalWordsFromCorpus(corpus0Words));
		assertEquals(5, perplexity.getTotalWordsFromCorpus(corpus5Words));
		assertEquals(10, perplexity.getTotalWordsFromCorpus(corpus10Words));
		assertEquals(15, perplexity.getTotalWordsFromCorpus(corpus15Words));
	}

	@Test
	public void topicPerplexities() {
		/**
		 * Ensure the Perplexity metric outputs the correct values for each topic's
		 * perplexity
		 * 
		 * Expected value should be:
		 * LogBase ^ - (normalised_corpus_probability)
		 **/
		TopicModel[] models = { model1, model2, model3 };
		String metricName = perplexity.getMetricName();

		perplexity.calculate(models, conf);

		Map<String, Double> perplexityModel1 = (Map<String, Double>) model1.getMetrics().get(metricName);
		Map<String, Double> perplexityModel2 = (Map<String, Double>) model2.getMetrics().get(metricName);

		// 2 ^ -(-56.829 / 10)
		assertEquals(51.371, perplexityModel1.get("topic0"), 0.01);
		// 2 ^ -(-56.829 / 10)
		assertEquals(51.371, perplexityModel1.get("topic1"), 0.01);

		// 2 ^ -(-55.924 / 10)
		assertEquals(48.248, perplexityModel2.get("topic0"), 0.01);
		// 2 ^ -(-58.927 / 10)
		assertEquals(59.399, perplexityModel2.get("topic1"), 0.01);
	}
	
	@Test
	public void equalPerplexities() {
		/**
		 * Two different topics with the same word values should have the same
		 * perplexities
		 **/
		TopicModel[] models = { model1 };

		perplexity.calculate(models, conf);

		Map<String, Double> perplexities = (Map<String, Double>) model1.getMetrics().get(perplexity.getMetricName());

		assertEquals(perplexities.get("topic0"), perplexities.get("topic1"));
	}
	
	@Test
	public void addMetricToModels() {
		/** Perplexity should be added as a metric in the model objects **/
		TopicModel[] models = { model1, model2, model3 };

		perplexity.calculate(models, conf);

		for (TopicModel model : models) {
			assertTrue(model.getMetrics().keySet().contains(perplexity.getMetricName()));
		}
	}

}
