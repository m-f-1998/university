import static org.junit.Assert.assertEquals;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

/**
 *
 * JUnit Tests on Operations of LinkedList Based Map Using IWLMap Interface
 *
 * @author Matthew Frankland
 *
 */

public class ListWLMapTest {
	private IWLMap testingMap = new ListIWLMap();
	private List<ILocation> locationList = new LinkedList<ILocation>();
	private WLoc mainLocation = new WLoc("example_document.txt", 23, "Data Structure");

	private int countLoc(String word) throws WLException {
		int count = 0;
		Iterator<ILocation> mapIterator = testingMap.locations(word);
		while (mapIterator.hasNext()) {
			mapIterator.next();
			count++;
		}
		return count;
	}

	@Test
	public void addNew() throws WLException {
		testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		assertEquals(testingMap.numberOfEntries(), 1);
	}

	@Test(expected = WLException.class)
	public void addNewException() throws WLException {
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		testingMap.addNew("Hello", locationList);
	}

	@Test
	public void addLocAlreadyInList() throws WLException {
		WLoc secondaryLocation = new WLoc("example_document.txt", 39, "Computer");
		locationList.add(mainLocation);
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		assertEquals(testingMap.numberOfEntries(), 1);
		testingMap.addLoc("Hello", secondaryLocation);
		assertEquals(testingMap.numberOfEntries(), 1);
		assertEquals(2, countLoc("Hello"));
	}

	@Test
	public void addLocNotInList() throws WLException {
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addLoc("Hello", mainLocation);
		assertEquals(testingMap.numberOfEntries(), 1);
		assertEquals(1, countLoc("Hello"));
	}

	@Test(expected = WLException.class)
	public void addLocException() throws WLException {
		locationList.add(mainLocation);
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		testingMap.addLoc("Hello", mainLocation);
	}

	@Test
	public void removeLoc() throws WLException {
		locationList.add(mainLocation);
        	testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		assertEquals(testingMap.numberOfEntries(), 1);
		testingMap.removeLoc("Hello", mainLocation);
		assertEquals(testingMap.numberOfEntries(), 0);
	}

	@Test(expected = WLException.class)
	public void removeLocException() throws WLException {
		testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		assertEquals(testingMap.numberOfEntries(), 1);
		testingMap.removeLoc("Hello", mainLocation);
	}

	@Test
	public void removeWord() throws WLException {
		testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.addNew("Hello", locationList);
		assertEquals(testingMap.numberOfEntries(), 1);
		testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
	}

	@Test(expected = WLException.class)
	public void removeWordException() throws WLException {
		testingMap.removeWord("Hello");
		assertEquals(testingMap.numberOfEntries(), 0);
		testingMap.removeWord("Hello");
	}
}
