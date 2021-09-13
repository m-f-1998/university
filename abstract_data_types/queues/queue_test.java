import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of Queue
 *
 * @author Matthew Frankland
 *
 */

public class QueueTest {

	Queue queue;
	
	@Before
	public void setup()
	{
		q = new Queue();
	}
	
	@Test
	public void testIsEmpty() {
		assertTrue(q.isEmpty());
	}
	
	@Test
	public void testIsEmptyFalse() {
		q.enqueue(2);
		
		assertFalse(q.isEmpty());
	}

	@Test
	public void testSize() {
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		assertEquals(3, q.size());
	}

	@Test
	public void testEnqueue() {
		q.enqueue(1);
		q.enqueue(2);
		assertEquals(1,q.front());
	}
	
	@Test(expected = QueueException.class)  
	public void testEmptyDequeue() {  
		q.dequeue();
	}
	
	@Test
	public void testDequeue() {
		q.enqueue(1);
		q.enqueue(2);
		
		assertEquals(1,q.dequeue());
	}
}
