import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestTopicModel {
	TopicModel noMetrics;
	TopicModel noParameters;
	TopicModel noTopics;
	TopicModel emptyModel;
	TopicModel fullModel;
	
	@Test
	public void noMetricsModel() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String noMetricsModel = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest1.json")));

		noMetrics = gson.fromJson(noMetricsModel, TopicModel.class);
		
		assertEquals(noMetrics.getMetrics(), new HashMap<String, Object>());
	}
	
	@Test
	public void noParametersModel() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String noParametersModel = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest2.json")));
		
		noParameters = gson.fromJson(noParametersModel, TopicModel.class);
		
		assertEquals(noParameters.getMetrics(), new HashMap<String, Object>());
	}
	
	@Test
	public void noTopicsModel() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String noTopicsModel = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest3.json")));
		
		noTopics = gson.fromJson(noTopicsModel, TopicModel.class);
		
		assertEquals(noTopics.getTopics(), new HashMap<String, Map<String, Integer>>());
	}
	
	@Test
	public void empty() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String empty = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest4.json")));
		
		emptyModel = gson.fromJson(empty, TopicModel.class);
		
		assertEquals(emptyModel.getID(), "");
		assertEquals(emptyModel.getTopics(), new HashMap<String, Map<String, Integer>>());
		assertEquals(emptyModel.getParameters(), new HashMap<String, Object>());
		assertEquals(emptyModel.getMetrics(), new HashMap<String, Object>());
	}
	
	@Test
	public void addMetricToEmpty() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String empty = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest4.json")));
		
		emptyModel = gson.fromJson(empty, TopicModel.class);
		emptyModel.addMetrics("log", 1);
		
		Map<String, Object> metric = new HashMap<String, Object>();
		metric.put("log", 1);
		
		assertEquals(emptyModel.getMetrics(), metric);
	}
	
	@Test
	public void fullModel() throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String fullModelString = new String(Files.readAllBytes(Paths.get("topicModelTestModels/ModelTest5.json")));

		fullModel = gson.fromJson(fullModelString, TopicModel.class);
		
		Map<String, Map<String, Integer>> topics = new HashMap<String, Map<String, Integer>>();
		Map<String, Integer> words = new HashMap<String, Integer>();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> metrics = new HashMap<String, Object>();
		
		metrics.put("log_likelihood", -8.099141810362212);
		
		parameters.put("iterations", 20.0);
		words.put("cancel", 1);
		words.put("reuse", 1);
		words.put("half", 5);
		
		topics.put("topic0", words);
		
		assertEquals(fullModel.getID(), "ffffffffeecb1efb");
		assertEquals(fullModel.getTopics(), topics);
		assertEquals(fullModel.getParameters(), parameters);
		assertEquals(fullModel.getMetrics(), metrics);
	}
}
