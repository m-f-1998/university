/**
 * @create date 10-02-2021 18:41:10
 * @modify date 21-02-2021 18:10:49
 * @desc [A* Pathfinding - Best Path]
 */

using UnityEngine;
using System.Collections.Generic;
using System;

public class Path {
    public static List<Vector2> FindPath(GridPath grid, Vector2 pos1, Vector2 pos2, ref List<Vector2> path) {
        Node start = grid.GetNode((int) Math.Ceiling(pos1.x), (int) Math.Ceiling(pos1.y));
        Node target = grid.GetNode((int) Math.Ceiling(pos2.x), (int) Math.Ceiling(pos2.y));

        Heap open = new Heap(grid.GetSize());
        HashSet<Node> closed = new HashSet<Node>();
        open.Add(start);    

        while (open.GetCount() > 0) {
            Node cur = open.Remove();
            closed.Add(cur);

            if (cur.GetYPos() == target.GetYPos() && cur.GetXPos() == target.GetXPos()) {
                while (cur != start) {
                    path.Add(new Vector2(cur.GetXPos(), cur.GetYPos()));
                    cur = cur.parent;
                }
                path.Reverse();
                break;
            }

            foreach (Node n in grid.GetNeighbours(cur)) {
                if (!n.IsWalkable() || closed.Contains(n)) continue;
                int newCost = cur.GCost + GetDistance(cur, n);
                if (newCost < n.GCost || !open.NodeExists(n)) {
                    n.GCost = newCost;
                    n.HCost = GetDistance(n, target);
                    n.parent = cur;

                    if (!open.NodeExists(n)) open.Add(n);
                }
            }
        }

        return path;
    }

    private static int GetDistance(Node i, Node j) {
        int k = Mathf.Abs(i.GetXPos() - j.GetXPos());
        int l = Mathf.Abs(i.GetYPos() - j.GetYPos());
        if (k > l) return 14 * l + 10 * (k - l);
        return 14 * k + 10 * (l - k);
    }
}