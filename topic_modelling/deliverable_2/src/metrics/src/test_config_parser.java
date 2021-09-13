import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestConfigParser {
	Configuration conf;
	
	@Before
	public void setUp() {
		conf = ConfigParser.parseConfigFile("configParserTesting/testConfiguration.conf");
	}
	
	@Test
	public void testString() {
//		Test simple string
		assertEquals(conf.get("optionString"), "Message");
	}
	
	@Test
	public void testBoolean() {
//		Test boolean values
		assertEquals(conf.get("optionBooleanTrue"), true);
		assertEquals(conf.get("optionBooleanFalse"), false);
	}
	
	@Test
	public void testSymbols() {
//		Special string symbol checks
		assertEquals(conf.get("optionDash"), "a-");
		assertEquals(conf.get("optionUnderscore"), "a_");
		assertEquals(conf.get("optionColon"), "a:");
		assertEquals(conf.get("optionSlash"), "a/");
	}
	
	@Test
	public void testInt() {
//		Int test
		assertEquals(conf.get("optionInt"), 75);
	}
	
	@Test
	public void testDouble() {
//		Double test
		assertEquals(conf.get("optionDouble"), 1.001);
	}
	
	@Test
	public void testSymbolsInKey() {
//		Test key can use _ and - chars
		assertEquals(conf.get("option_underscore"), 1);
		assertEquals(conf.get("option-dash"), 1);
	}
	
	@Test
	public void testNull() {
//		Test that blank param returns null and one after it still works 
		assertEquals(conf.get("optionNull"), null);
		assertEquals(conf.get("optionAfterNull"), 1);
	}

	@Test
	public void testPathExample() {
//		Dir string test
		assertEquals(conf.get("optionDirForwardSlash"), "C:/Users/Workspace/test_directory/file-1.txt");
		assertEquals(conf.get("optionDirBackwardSlash"), "C:\\Users\\Workspace\\test_directory\\file-1.txt");
	}

}
