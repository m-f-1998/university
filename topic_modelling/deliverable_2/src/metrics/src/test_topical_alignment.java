import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestTopicalAlignment {
	TopicModel model1;
	TopicModel model2;
	TopicModel model3;
	TopicAlignment topicalAlignment;
	TopicModel[] topicModels;
	Configuration conf;
	@Before
	public void setUp() throws Exception {
		topicModels = new TopicModel[3];
		
//		Load some test models
		String model1String = new String(Files.readAllBytes(Paths.get("topicAlignmentTesting/ModelTest1.json")));
		String model2String = new String(Files.readAllBytes(Paths.get("topicAlignmentTesting/ModelTest2.json")));
		String model3String = new String(Files.readAllBytes(Paths.get("topicAlignmentTesting/ModelTest3.json")));

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		model1 = gson.fromJson(model1String, TopicModel.class);		
		model2 = gson.fromJson(model2String, TopicModel.class);
		model3 = gson.fromJson(model3String, TopicModel.class);

		topicModels[0] = model1;
		topicModels[1] = model2;
		topicModels[2] = model3;
		topicalAlignment = new TopicAlignment();

//		Load testing configuration file
		conf = ConfigParser.parseConfigFile("topicAlignmentTesting/testTopicalAlignment.conf");
	}

	
	@Test
	public void test( ) {
		//File pathName = new File("outputModels/TopicalAlignment.json");
		topicalAlignment.calculate(topicModels, conf);		
		
		try {
			String topicalAlignmentString = new String(Files.readAllBytes(Paths.get("topicAlignmentTesting/outputModels/" + topicalAlignment.getMetricName() + ".json")));
			
			//Explanation of how this result is correct can be seen in the test report.
			String topicAlignmentCorrectString = "{ \"1\": { \"clusterID\": 1, \"left\": { \"clusterID\": 1, \"left\": { \"modelID\": \"1\", \"topicID\": \"topic2\", \"clusterID\": 1 }, \"right\": { \"modelID\": \"3\", \"topicID\": \"topic0\", \"clusterID\": 1 }, \"usedModels\": [ \"1\", \"3\" ] }, \"right\": { \"modelID\": \"2\", \"topicID\": \"topic0\", \"clusterID\": 1 }, \"usedModels\": [ \"2\", \"1\", \"3\" ] }, \"2\": { \"clusterID\": 2, \"left\": { \"clusterID\": 2, \"left\": { \"modelID\": \"1\", \"topicID\": \"topic1\", \"clusterID\": 2 }, \"right\": { \"modelID\": \"3\", \"topicID\": \"topic1\", \"clusterID\": 2 }, \"usedModels\": [ \"1\", \"3\" ] }, \"right\": { \"modelID\": \"2\", \"topicID\": \"topic1\", \"clusterID\": 2 }, \"usedModels\": [ \"2\", \"1\", \"3\" ] }, \"3\": { \"clusterID\": 3, \"left\": { \"clusterID\": 3, \"left\": { \"modelID\": \"2\", \"topicID\": \"topic2\", \"clusterID\": 3 }, \"right\": { \"modelID\": \"3\", \"topicID\": \"topic2\", \"clusterID\": 3 }, \"usedModels\": [ \"2\", \"3\" ] }, \"right\": { \"modelID\": \"1\", \"topicID\": \"topic0\", \"clusterID\": 3 }, \"usedModels\": [ \"1\", \"2\", \"3\" ] } }";
			
			topicAlignmentCorrectString = topicAlignmentCorrectString.replaceAll("[\n\t ]", "");
			topicalAlignmentString = topicalAlignmentString.replaceAll("[\n\t ]", "");
			
			assertEquals(topicalAlignmentString, topicAlignmentCorrectString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("File error");
		}
	}
}
