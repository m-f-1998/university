import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of Stack
 *
 * @author Matthew Frankland
 *
 */

public class StackTest {

	Stack st;
    
	@Before
	public void setup()
	{
		st = new Stack(2);
	}
	
	@Test
	public void testPush() {
		st.push("a");
		
		assertEquals(st.size(),1);
	}
	
	@Test
	public void testPop() {
		st.push("b)");
		st.pop();
		
		assertEquals(st.size(),0);
	}
	
	@Test (expected=StackException.class)
	public void testFullStack() {
		st.push("c");
		st.push("d");
		st.push("e");
	}
	
	@Test(expected=StackException.class)
	public void testEmptyStack() {
		st.pop();
	}
	
}
