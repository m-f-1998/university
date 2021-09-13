package malletWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.*;

/**
 * 
 * Unit testing for TopicModel.java
 * 
 */
public class TopicModelTest {
	TopicModel t;

	@Before
	public void preRun() {
    	t = new TopicModel(5, 1.0, 0.01);
	}
	
    @Test
    public void testInstances() {
    	// Once instances added to model they cannot be retrieved
    	// Wrapper sets var with instances and then sets them in model
    	// Thus test checks wrapper's var instead
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        
        assertNull(t.getInstances());
        t.setInstances(instances);
        assertEquals(instances, t.getInstances());
    }
    
    @Test
    public void testNumTopics() {
    	assertEquals(5, t.getNumTopics());
    }
    
    @Test
    public void testAlphaSymetric() {
        assertTrue(!t.getSymmetricAlpha());
    	t.setSymmetricAlpha(true);
        assertTrue(t.getSymmetricAlpha());
    }
    
    @Test
    public void testAlphaSum() {
    	assertEquals(1.0, t.getAlphaSum(), .2);
    }
    
    @Test
    public void testBeta() {
    	assertEquals(0.01, t.getBeta(), .2);
    }
    
    @Test
    public void testSeed() {
    	assertEquals(-1, t.getSeed());
    	t.setSeed(9);
    	assertEquals(9, t.getSeed());
    }
    
    @Test
    public void testIterations() {
    	assertEquals(1000, t.getIterations());
    	t.setIterations(20);
    	assertEquals(20, t.getIterations());
    }
    
    @Test
    public void testBurnInPeriod() {
    	assertEquals(200, t.getBurnin());
    	t.setBurninPeriod(50);
    	assertEquals(50, t.getBurnin());
    }
    
    @Test
    public void testOptimiseInterval() {
    	assertEquals(50, t.getOptimiseInterval());
    	t.setOptimiseInterval(33);
    	assertEquals(33, t.getOptimiseInterval());
    }
    
    @Test
    public void testCoherence() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader;
		try {
			fileReader = new InputStreamReader(new FileInputStream(new File("src/main/resources/modelCorpus.txt")), "UTF-8");
	        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			fail("Should not have thrown any exception");
		}
		
		t.setInstances(instances);
		t.setIterations(50);
		t.runModel();
        
        assertEquals(5, t.getCoherence().keySet().size()); // Test coherence result for each topic        
    }
    
    @Test
    public void testLogLikelehood() {
        assertEquals(0.00, t.getLL(), .2);
        assertTrue(Double.isNaN(t.getLLPerNumToken()));

        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        Reader fileReader;
		try {
			fileReader = new InputStreamReader(new FileInputStream(new File("src/main/resources/modelCorpus.txt")), "UTF-8");
	        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			fail("Should not have thrown any exception");
		}
		
		t.setInstances(instances);
		t.setIterations(50);
		t.runModel();
		
		System.out.println("test");
		
		assertTrue(t.getLL() < t.getLLPerNumToken());  // Likelihood should be less than normalised Likelihood        
		
    }
    
}
