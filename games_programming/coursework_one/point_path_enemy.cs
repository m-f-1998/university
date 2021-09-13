/**
 * @create date 03-02-2021 12:41:10
 * @modify date 19-02-2021 09:26:22
 * @desc [Enemy That Follows Fixed Point Path]
 */

using System.Collections.Generic;
using UnityEngine;

public class PointPathEnemy : MonoBehaviour {

    public int speed = 10;
    public LayerMask unwalkableMask;
    public Vector3[] directions = new Vector3[4];
    public bool reverse = false;

    private bool[,] tilesmap;
    private GridPath grid;
    private bool newPath = true, directionFlag = false;
    private int pathIndex = 0, goalIndex = 0;
    private List<Vector2> path = new List<Vector2>(); // from (0, 0) to (Constants.WIDTH-1, Constants.HEIGHT-1)

    void Start() {
        tilesmap = new bool[Constants.WIDTH, Constants.HEIGHT];
        for (int i = 0; i < Constants.WIDTH; i ++)
            for (int j = 0; j < Constants.HEIGHT; j ++)
                tilesmap[i, j] = !(Physics2D.OverlapCircle(new Vector2(i, j), 0.5f, unwalkableMask)); // true if walkable, false if not
        grid = new GridPath(Constants.WIDTH, Constants.HEIGHT, tilesmap);
    }

    void Update() {
        if ((!reverse && goalIndex < directions.Length) || reverse) {
            if (newPath) {
                Path.FindPath(grid, transform.position, directions[goalIndex], ref path);
                newPath = false;
            }
            if (pathIndex < path.Count) {
                var p = new Vector3(path[pathIndex].x, path[pathIndex].y, 0);
                if (transform.position != p) {
                    transform.position = Vector3.MoveTowards(transform.position, p, Time.deltaTime * speed);
                } else {
                    if (pathIndex < path.Count-1) pathIndex++;
                    else {
                        if (reverse && goalIndex == directions.Length - 1) { directionFlag = true; } else if (reverse && goalIndex == 0) { directionFlag = false; }
                        if (!directionFlag) { goalIndex++; } else { goalIndex--; }
                        pathIndex = 0;
                        newPath = true;
                        path = new List<Vector2>();
                    }
                    
                }
            }
        }
    }

}