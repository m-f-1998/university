import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestPairwiseDifference {
	TopicModel model1;
	TopicModel model2;
	TopicModel model3;
	
	PairwiseDifference pairwiseDifference;
	
	@Before
	public void setUp() throws IOException
	{
		pairwiseDifference = new PairwiseDifference();
		
//		Load some test models
		String model1String = new String(Files.readAllBytes(Paths.get("pairwiseDifferenceTesting/testModel1.json")));
		String model2String = new String(Files.readAllBytes(Paths.get("pairwiseDifferenceTesting/testModel2.json")));
		String model3String = new String(Files.readAllBytes(Paths.get("pairwiseDifferenceTesting/testModel3.json")));

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		model1 = gson.fromJson(model1String, TopicModel.class);
		model2 = gson.fromJson(model2String, TopicModel.class);
		model3 = gson.fromJson(model3String, TopicModel.class);
		
	}

	@Test
	public void testEuclidean() {
		Configuration confEuclidean = ConfigParser.parseConfigFile("pairwiseDifferenceTesting/pairwiseDifferenceTestConfigEuclidean.conf");
		
//		Make list of the models
		TopicModel[] models = {model1, model2, model3};
		
		pairwiseDifference.calculate(models, confEuclidean);
		
		model1 = models[0];
		model2 = models[1];
		model3 = models[2];
		
		Map<String, Double> metric1 = (Map<String, Double>) model1.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric2 = (Map<String, Double>) model2.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric3 = (Map<String, Double>) model3.getMetrics().get(pairwiseDifference.getMetricName());
		
//		Identical models should have distance of 0
		assertEquals(0, (double) metric1.get("1"), 0);
		assertEquals(0, (double) metric2.get("0"), 0);
		
//		See justification in testing report
		assertEquals(Math.sqrt(2)/6, (double) metric1.get("2"), 0.0001);
		assertEquals(Math.sqrt(2)/6, (double) metric2.get("2"), 0.0001);
		assertEquals(Math.sqrt(2)/6, (double) metric3.get("0"), 0.0001);
		assertEquals(Math.sqrt(2)/6, (double) metric3.get("1"), 0.0001);
	}
	
	@Test
	public void testManhattan() {
		
//		Load manhattan conf
		Configuration confManhattan = ConfigParser.parseConfigFile("pairwiseDifferenceTesting/pairwiseDifferenceTestConfigManhattan.conf");
		
//		Make list of the models
		TopicModel[] models = {model1, model2, model3};
		
		pairwiseDifference.calculate(models, confManhattan);
		
		model1 = models[0];
		model2 = models[1];
		model3 = models[2];
		
		Map<String, Double> metric1 = (Map<String, Double>) model1.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric2 = (Map<String, Double>) model2.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric3 = (Map<String, Double>) model3.getMetrics().get(pairwiseDifference.getMetricName());
		
//		Identical models should have distance of 0
		assertEquals(0, (double) metric1.get("1"), 0);
		assertEquals(0, (double) metric2.get("0"), 0);
		
//		See justification in testing report
		assertEquals(1.0/3.0, (double) metric1.get("2"), 0);
		assertEquals(1.0/3.0, (double) metric2.get("2"), 0);
		assertEquals(1.0/3.0, (double) metric3.get("0"), 0);
		assertEquals(1.0/3.0, (double) metric3.get("1"), 0);

	}
	
	@Test
	public void testChebyshev() {
		Configuration confChebyshev = ConfigParser.parseConfigFile("pairwiseDifferenceTesting/pairwiseDifferenceTestConfigChebyshev.conf");
		
//		Make list of the models
		TopicModel[] models = {model1, model2, model3};
		
		pairwiseDifference.calculate(models, confChebyshev);
		
		model1 = models[0];
		model2 = models[1];
		model3 = models[2];
		
		Map<String, Double> metric1 = (Map<String, Double>) model1.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric2 = (Map<String, Double>) model2.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric3 = (Map<String, Double>) model3.getMetrics().get(pairwiseDifference.getMetricName());
		
//		Identical models should have distance of 0
		assertEquals(0, (double) metric1.get("1"), 0);
		assertEquals(0, (double) metric2.get("0"), 0);
		
//		See justification in testing report
		assertEquals(1.0/6.0, (double) metric1.get("2"), 0.0001);
		assertEquals(1.0/6.0, (double) metric2.get("2"), 0.0001);
		assertEquals(1.0/6.0, (double) metric3.get("0"), 0.0001);
		assertEquals(1.0/6.0, (double) metric3.get("1"), 0.0001);
	}
	
	@Test
	public void testCosine() {
		Configuration confEuclidean = ConfigParser.parseConfigFile("pairwiseDifferenceTesting/pairwiseDifferenceTestConfigCosine.conf");
		
//		Make list of the models
		TopicModel[] models = {model1, model2, model3};
		
		pairwiseDifference.calculate(models, confEuclidean);
		
		model1 = models[0];
		model2 = models[1];
		model3 = models[2];
		
		Map<String, Double> metric1 = (Map<String, Double>) model1.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric2 = (Map<String, Double>) model2.getMetrics().get(pairwiseDifference.getMetricName());
		Map<String, Double> metric3 = (Map<String, Double>) model3.getMetrics().get(pairwiseDifference.getMetricName());
		
//		Identical models should have distance of 0
		assertEquals(0, (double) metric1.get("1"), 0);
		assertEquals(0, (double) metric2.get("0"), 0);
		
//		See justification in testing report
		double expected_dist = (10 - (3*Math.sqrt(10) )) / 10.0;
		assertEquals(expected_dist, (double) metric1.get("2"), 0.0001);
		assertEquals(expected_dist, (double) metric2.get("2"), 0.0001);
		assertEquals(expected_dist, (double) metric3.get("0"), 0.0001);
		assertEquals(expected_dist, (double) metric3.get("1"), 0.0001);
	}

}
