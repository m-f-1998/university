import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of Binary Search Tree
 *
 * @author Matthew Frankland
 *
 */

public class BinarySearchTreeTest {
	BinarySearchTree bst;
	DLinkedList dl;
	
	@Before
	public void setup(){
		bst = new BinarySearchTree();

	}
	@Test
	public void testReturnInOrderTraversalEmpty() {
		dl = bst.returnInOrderTraversal();
		assertNull(dl.getHeadValue());
	}
	
	@Test
	public void testReturnInOrderTraversalTreeInOrder() {
		bst.insert(1);
		bst.insert(2);
		bst.insert(3);
		dl = bst.returnInOrderTraversal();
		assertEquals(1,dl.deleteAtHead());
		assertEquals(2,dl.deleteAtHead());
		assertEquals(3,dl.deleteAtHead());
	}
	
	@Test
	public void testReturnInOrderTraversalTreeRandom() {
		bst.insert(5);
		bst.insert(2);
		bst.insert(4);
		dl = bst.returnInOrderTraversal();
		assertEquals(2,dl.deleteAtHead());
		assertEquals(4,dl.deleteAtHead());
		assertEquals(5,dl.deleteAtHead());
	}
}
