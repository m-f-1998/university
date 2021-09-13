/**
 * @create date 07-02-2021 15:11:53
 * @modify date 19-02-2021 09:26:22
 * @desc [Enemy that Flies Best Path to Player]
 */

using System.Collections.Generic;
using UnityEngine;

public class BestPathEnemy : MonoBehaviour {

    public GameObject target;
    public int speed = 10;
    public LayerMask unwalkableMask;

    private bool[,] tilesmap;
    private GridPath grid;
    private bool active;
    private int index = 0;

    void Start() {
        tilesmap = new bool[Constants.WIDTH, Constants.HEIGHT];
        for (int i = 0; i < Constants.WIDTH; i ++)
            for (int j = 0; j < Constants.HEIGHT; j ++)
                tilesmap[i, j] = !(Physics2D.OverlapCircle(new Vector2(i, j), 0.5f, unwalkableMask)); // true if walkable, false if not
        grid = new GridPath(Constants.WIDTH, Constants.HEIGHT, tilesmap);
    }

    void Update() {
        List<Vector2> path = new List<Vector2>(); // from (0, 0) to (Constants.WIDTH-1, Constants.HEIGHT-1)
        if (active && index == 0 && target.GetComponent<PlayerMove>().GetGrounded())
            Path.FindPath(grid, transform.position, target.transform.position, ref path);
        if (path.Count > 0) { // path will either be a list of Points (x, y), or an empty list if no path is found.
            var p = new Vector3(path[index].x, path[index].y, 0);
            if (transform.position != p) transform.position = Vector3.MoveTowards(transform.position, p, Time.deltaTime * speed);
            else index = index < path.Count - 1 ? index + 1 : 0;
        } else index = 0;
    }

    void OnTriggerEnter2D(Collider2D coll) {
        if (coll.gameObject.tag == "Player") active = true;
    }

    void OnTriggerExit2D(Collider2D coll){
        if (coll.gameObject.tag == "Player") active = false;
    }

}