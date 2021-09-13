import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class TestStatistics {

	@Test
	public void testOne() {
		Statistics stats = new Statistics();
		
		HashMap<String, Double> testMap1 = new HashMap<>();
		
//		Test for an array of length 1
		testMap1.put("val1", 1.0);
		
		HashMap<String, Double> res = stats.calculateStatistics(testMap1);
		
		assertEquals(1.0, res.get("mean"), 0);
		assertEquals(1.0, res.get("median"), 0);
		assertEquals(0.0, res.get("standard_deviation"), 0);
		assertEquals(0.0, res.get("variance"), 0);
		assertEquals(1.0, res.get("min"), 0);
		assertEquals(1.0, res.get("max"), 0);
		assertEquals(0.0, res.get("range"), 0);
		
	}
	
	@Test
	public void testFull() {
		
		Statistics stats = new Statistics();
		
		HashMap<String, Double> testMap1 = new HashMap<>();
		
//		List of size three with some known statistics
		testMap1.put("val1", 1.0);
		testMap1.put("val2", 3.0);
		testMap1.put("val3", 2.0);
		
		HashMap res = stats.calculateStatistics(testMap1);
		
//		These values worked out by hand
		assertEquals(2.0, res.get("mean"));
		assertEquals(2.0, res.get("median"));
		assertEquals(1.0, res.get("standard_deviation"));
		assertEquals(1.0, res.get("variance"));
		assertEquals(1.0, res.get("min"));
		assertEquals(3.0, res.get("max"));
		assertEquals(2.0, res.get("range"));
		
	}

	@Test
	public void testEmpty() {
		Statistics stats = new Statistics();
		
		HashMap<String, Double> testMap1 = new HashMap<>();
		HashMap res = stats.calculateStatistics(testMap1);
		
//		Test for an array of length 0
		assertEquals(-1.0, res.get("mean"));
		assertEquals(-1.0, res.get("median"));
		assertEquals(-1.0, res.get("standard_deviation"));
		assertEquals(-1.0, res.get("variance"));
		assertNotNull(res.get("min"));
		assertNotNull(res.get("max"));
		assertNotNull(res.get("range"));
	}
	
}
