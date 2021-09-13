/**
 * @create date 10-02-2021 18:41:10
 * @modify date 19-02-2021 09:26:22
 * @desc [A* Pathfinding - Heap]
 */

public class Heap {
	
	private Node[] items;
	private int itemCount;
	
	public Heap(int maxHeapSize) {
		items = new Node[maxHeapSize];
	}

	//MARK: Getters

	public int GetCount() {
		return itemCount;
	}

	//MARK: Helper Functions

	public bool NodeExists(Node i) {
		return Equals(items[i.GetIndex()], i);
	}
	
	public void Add(Node item) {
		item.SetIndex(itemCount);
		items[itemCount] = item;
		int parentIndex = (item.GetIndex()-1)/2;
		while (true) {
			Node parentItem = items[parentIndex];
			if (item.CompareTo(parentItem) > 0)
				Swap (item,parentItem);
			else break;
			parentIndex = (item.GetIndex()-1)/2;
		}
		itemCount++;
	}

	public Node Remove() {
		Node first = items[0];
		itemCount--;
		items[0] = items[itemCount];
		items[0].SetIndex(0);
		while (true) {
			int left = items[0].GetIndex() * 2 + 1;
			int right = items[0].GetIndex() * 2 + 2;
			int index = 0;
			if (left < itemCount) {
				index = left;
				if (right < itemCount)
					if (items[left].CompareTo(items[right]) < 0) index = right;
				if (items[0].CompareTo(items[index]) < 0) Swap(items[0], items[index]);
				else break;
			} else break;
		}
		return first;
	}
	
	private void Swap(Node i, Node j) {
		items[i.GetIndex()] = j;
		items[j.GetIndex()] = i;
		int index = i.GetIndex();
		i.SetIndex(j.GetIndex());
		j.SetIndex(index);
	}
}