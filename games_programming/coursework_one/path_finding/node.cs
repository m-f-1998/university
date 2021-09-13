/**
 * @create date 10-02-2021 18:41:10
 * @modify date 19-02-2021 09:26:22
 * @desc [A* Pathfinding - Node]
 */

public class Node {
    
    private bool walkable;
    private int x, y, gCost, hCost, heapIndex;
    public Node parent;

    public Node(float _price, int _x, int _y) {
        walkable = _price != 0.0f;
        x = _x;
        y = _y;
    }

    public int GetFCost() {
        return gCost + hCost;
    }

    public int HCost {
        get { return hCost; }
        set { hCost = value; }
    }

    public int GCost {
        get { return gCost; }
        set { gCost = value; }
    }

    public int GetXPos() {
        return x;
    }

    public int GetYPos() {
        return y;
    }

    public int GetIndex() {
        return heapIndex;
    }

    public bool IsWalkable() {
        return walkable;
    }

    public void SetIndex(int index) {
        heapIndex = index;
    }

	public int CompareTo(Node nodeToCompare) {
		int compare = GetFCost().CompareTo(nodeToCompare.GetFCost());
		if (compare == 0) compare = hCost.CompareTo(nodeToCompare.hCost);
		return -compare;
	}
}