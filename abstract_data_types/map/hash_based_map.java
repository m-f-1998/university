import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Implementation of A HashTable Based Map Using IWLMap and IHashWLMonitor
 * Interfaces
 * 
 * @author Matthew Frankland
 *
 */

public class HashWLMap implements IWLMap, IHashWLMonitor {
	private wordsAndLocations[] hashTableArray = new wordsAndLocations[13];
	private IHashCode inputCode;
	private float maxLoadFactor;
	private int numberOfOperations = 0;
	private int numberOfProbes = 0;

	public HashWLMap() throws WLException {
		throw new WLException(" - Can Not Be Directly Called As The IHashCode Needs To Be Specified");
	}

	public HashWLMap(IHashCode inputCode, float maxLoadFactor) {
		this.inputCode = inputCode;
		this.maxLoadFactor = maxLoadFactor;
	}

	private void checkLoad() {
		if (getLoadFactor() > getMaxLoadFactor()) {
			rehash();
		}
	}

	private boolean isPrime(int number) {
		for (int i = 2; i <= number / 2; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}

	private int doubleHashing(String word) {
		int hash = inputCode.giveCode(word);
		hash = hash % hashTableArray.length;
		while (hashTableArray[hash] != null) {
			numberOfProbes++;
			if (hash >= hashTableArray.length) {
				hash = hash - hashTableArray.length;
			} else {
				hash += collision(word);
				if (hash >= hashTableArray.length) {
					hash = hash - hashTableArray.length;
				}
			}
		}
		return hash;
	}

	private void rehash() {
		int newArraySize = hashTableArray.length * 2;
		while (!isPrime(newArraySize)) {
			newArraySize++;
		}
		wordsAndLocations[] oldHashTableArray = hashTableArray;
		hashTableArray = new wordsAndLocations[newArraySize];

		for (wordsAndLocations node : oldHashTableArray) {
			if (node != null) {
				int hash = doubleHashing(node.word);
				hashTableArray[hash] = node;
			}
		}
	}

	@Override
	public float getMaxLoadFactor() {
		return maxLoadFactor;
	}

	@Override
	public float getLoadFactor() {
		return (float) numberOfEntries() / hashTableArray.length;
	}

	@Override
	public float averNumProbes() {
		return (float) numberOfProbes / numberOfOperations;
	}

	private int collision(String word) {
		int secondaryHash = 3 - word.length() % 3;
        	return secondaryHash;
	}

	@Override
	public void addNew(String word, List<ILocation> locList) throws WLException {
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				if (node.word.equals(word)) {
					throw new WLException("'" + word + "' Already Exists in List.");
				}
			}
		}

		wordsAndLocations node = new wordsAndLocations(word, locList);
        	int hash = doubleHashing(word);
		hashTableArray[hash] = node;
		checkLoad();
		numberOfOperations++;
	}

	@Override
	public void addLoc(String word, ILocation loc) throws WLException {
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				if (node.word.equals(word)) {
					if (node.listOfLocations.contains(loc)) {
						throw new WLException("'" + word + "' Node Already Has This Location.");
					}
					node.listOfLocations.add(loc);
					numberOfOperations++;
					return;
				}
			}
		}
		List<ILocation> locList = new LinkedList<ILocation>();
		locList.add(loc);
		addNew(word, locList);
	}

	@Override
	public void removeWord(String word) throws WLException {
		boolean wordPresent = false;
		for (int i = 0; i < hashTableArray.length; i++) {
			numberOfProbes++;
			if (hashTableArray[i] != null) {
				if (hashTableArray[i].word.equals(word)) {
					hashTableArray[i] = null;
					wordPresent = true;
				}
			}
		}
		if (wordPresent == false) {
			throw new WLException("'" + word + "' Is Not In The Map. Cannot Remove.");
		}
		numberOfOperations++;
	}

	@Override
	public void removeLoc(String word, ILocation loc) throws WLException {
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				if (node.word.equals(word)) {
					if (node.listOfLocations.contains(loc)) {
						if (node.listOfLocations.size() == 1) {
							removeWord(word);
							return;
						}
						node.listOfLocations.remove(loc);
						numberOfOperations++;
						return;
					} else {
						throw new WLException("'" + word + "' Is Not Associated With The Given Location.");
					}
				}
			}
		}
		throw new WLException("'" + word + "' Is Not In The Map. Cannot Remove.");
	}

	@Override
	public Iterator<String> words() {
		Collection<String> collection = new LinkedList<String>();
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				collection.add(node.word);
			}
		}

		Iterator<String> returnIterator = collection.iterator();
		numberOfOperations++;
		return returnIterator;
	}

	@Override
	public Iterator<ILocation> locations(String word) throws WLException {
		Iterator<ILocation> returnIterator = null;
		boolean wordPresent = false;
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				if (node.word.equals(word)) {
					wordPresent = true;
					returnIterator = node.listOfLocations.iterator();
				}
			}
		}
		if (wordPresent == false) {
			throw new WLException("'" + word + "' Is Not In The Map. Cannot Remove.");
		}
		numberOfOperations++;
		return returnIterator;
	}

	@Override
	public int numberOfEntries() {
		int count = 0;
		for (wordsAndLocations node : hashTableArray) {
			numberOfProbes++;
			if (node != null) {
				count++;
			}
		}
		return count;
	}
	
}
