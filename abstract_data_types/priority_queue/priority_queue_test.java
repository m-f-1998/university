import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of Priority Queue
 *
 * @author Matthew Frankland
 *
 */

public class PriorityQueueTest {
	
    PriorityQueue queue
    
    @Before
    public void setup(){
        queue = new PriorityQueue(5);
    }
    
	@Test
	public void insertTest() {
		queue.insert(4);
		queue.insert(3);
		queue.insert(2);
		
		assertEquals(queue.size(),3);
		assertEquals(queue.min(),2);
	}

	@Test
	public void removeMinTest() {
		queue.insert(4);
		queue.insert(3);
		queue.insert(2);
		
		queue.removeMin();
		
		assertEquals(queue.size(),2);
		assertEquals(queue.min(), 3);
	}
}
