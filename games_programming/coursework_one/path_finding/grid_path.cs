/**
 * @create date 10-02-2021 18:41:10
 * @modify date 21-02-2021 18:10:43
 * @desc [A* Pathfinding - GridPath]
 */

using System.Collections.Generic;

public class GridPath {
    private Node[,] nodes;
    private int x, y;

    public GridPath(int width, int height, bool[,] walkable) {
        x = width;
        y = height;
        nodes = new Node[width, height];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                nodes[i, j] = new Node(walkable[i, j] ? 1.0f : 0.0f, i, j);
    }

    //MARK: Getters

    public int GetSize() {
		return x * y;
	}

    public Node GetNode(int x, int y) {
        return nodes[x, y];
    }

    public List<Node> GetNeighbours(Node node) { 
        List<Node> neighbours = new List<Node>();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int k = node.GetXPos() + i, l = node.GetYPos() + j;
                if (k >= 0 && k < x && l >= 0 && l < y) neighbours.Add(nodes[k, l]);
            }
        return neighbours;
    }
}