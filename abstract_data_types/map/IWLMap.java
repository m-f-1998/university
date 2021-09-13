import java.util.Iterator;
import java.util.List;

/** Interface map implements */
public interface IWLMap {

	/**
	 * Inserts an entry to the map. Throws exception if word is already present.
	 * 
	 * @throws WLException
	 *             if the word is already present in the map.
	 */
	public void addNew(String word, List<ILocation> locList) throws WLException;

	/**
	 * Adds a new location to an entry of the map. Creates the entry if the word is
	 * not already present in the map.
	 * 
	 * @throws WLException
	 *             if the word-location already exists in the map.
	 */
	public void addLoc(String word, ILocation loc) throws WLException;

	/**
	 * Removes from the map the entry for word.
	 * 
	 * @throws WLException
	 *             if no entry with word in the map.
	 */
	public void removeWord(String word) throws WLException;

	/**
	 * Removes from the map location for word.
	 * 
	 * @throws WLException
	 *             if no entry with word in the map or if word is not associated
	 *             with given location.
	 */
	public void removeLoc(String word, ILocation loc) throws WLException;

	/**
	 * Returns an iterator over all words of the map.
	 */
	public Iterator<String> words();

	/**
	 * Returns an iterator over the locations of a given word.
	 * 
	 * @throws WLException
	 *             if no entry with word in the map.
	 */
	public Iterator<ILocation> locations(String word) throws WLException;

	/**
	 * Returns the number of entries stored in the map.
	 */
	public int numberOfEntries();

}
