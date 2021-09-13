import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of Linked List
 *
 * @author Matthew Frankland
 *
 */

public class LinkedListTest {

	LinkedList list;
	
	@Before
	public void setup(){
		list = new LinkedList();
	}
	
	@Test
	public void testCalculateSizeEmpty() {
		assertEquals(0, list.calculateSize());
	}

	@Test
	public void testCalculateSizeMany() {
		list.addAtHead(2);
		list.addAtHead(3);
		list.addAtHead(4);
		assertEquals(3,list.calculateSize());
	}

	@Test
	public void testCalculateTotalEmpty() {
		assertEquals(0,list.calculateTotal());
	}

	@Test
	public void testCalculateTotalMany() {
		list.addAtHead(2);
		list.addAtHead(3);
		list.addAtHead(4);
		assertEquals(9,list.calculateTotal());
	}
}
