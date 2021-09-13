import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Implementation of a LinkedList Based Map Using IWLMap Interface
 * 
 * @author Matthew Frankland
 *
 */

public class ListIWLMap implements IWLMap {

	private static List<wordsAndLocations> listMap = new LinkedList<wordsAndLocations>();

	@Override
	public void addNew(String word, List<ILocation> locList) throws WLException {
        	Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode.word == word) {
				throw new WLException(word + " Already Exists in List.");
			}
		}
		if (mapIterator.hasNext() == false) {
			wordsAndLocations newNode = new wordsAndLocations(word, locList);
			listMap.add(newNode);
		}
	}

	@Override
	public void addLoc(String word, ILocation loc) throws WLException {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode.listOfLocations.contains(loc)) {
				throw new WLException(word + " Already Has Given Location.");
			}
			if (iteratedNode.word == word) {
				iteratedNode.listOfLocations.add(loc);
				return;
			}
		}
		List<ILocation> locList = new LinkedList<ILocation>();
		locList.add(loc);
		wordsAndLocations node = new wordsAndLocations(word, locList);
		listMap.add(node);
	}

	@Override
	public void removeWord(String word) throws WLException {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode.word == word) {
				listMap.remove(iteratedNode);
				return;
			}
		}
		throw new WLException(word + " Is Not In The Map. Cannot Remove.");
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void removeLoc(String word, ILocation loc) throws WLException {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode.listOfLocations.contains(loc)) {
				if (iteratedNode.listOfLocations.size() == 1) {
					removeWord(word);
					return;
				}
				listMap.remove(iteratedNode.listOfLocations.remove(loc));
				return;
			}
		}
		throw new WLException(word + " Does Not Have Specified Location. Cannot Remove.");
	}

	@Override
	public Iterator<String> words() {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		Collection<String> collection = new LinkedList<String>();
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			collection.add(iteratedNode.word);
		}
		Iterator<String> returnIterator = collection.iterator();
		return returnIterator;
	}

	@Override
	public Iterator<ILocation> locations(String word) throws WLException {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		Iterator<ILocation> returnIterator = null;
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode.word == word) {
				returnIterator = iteratedNode.listOfLocations.iterator();
			}
		}
		if (returnIterator == null) {
			throw new WLException(" Word Not Present In Map.");
		}
		return returnIterator;
	}

	@Override
	public int numberOfEntries() {
		Iterator<wordsAndLocations> mapIterator = listMap.iterator();
		int count = 0;
		while (mapIterator.hasNext()) {
			wordsAndLocations iteratedNode = mapIterator.next();
			if (iteratedNode != null) {
				count++;
			}
		}
		return count;
	}
	
}
