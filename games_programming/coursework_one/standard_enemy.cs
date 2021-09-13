/**
 * @create date 05-02-2021 13:11:53
 * @modify date 19-02-2021 09:26:22
 * @desc [Movement of a Standard Enemy (fixed movement on the X or Y axis)]
 */

 using UnityEngine;
 
 public class StandardEnemy : MonoBehaviour {
 
    public int distance = 10;
    public bool yAxis = false;

    private float startPoint, destPoint;
    private int speed = 10;

    void Start () {
        startPoint = yAxis ? transform.position.y : transform.position.x;
        destPoint = yAxis ? transform.position.y + distance : transform.position.x + distance;
    }
    
    void Update() {
        if (gameObject.transform.position.y < -10) Destroy(gameObject);
        else {
            var curPos = yAxis ? transform.position.y : transform.position.x;
            if ((speed < 0 && curPos < startPoint) || (speed > 0 && curPos > destPoint)) speed *= -1;
            
            var dest = new Vector2(transform.position.x + (!yAxis ? distance : 0), transform.position.y + (yAxis ? distance : 0));
            transform.position = Vector2.MoveTowards(transform.position, dest, speed * Time.deltaTime);
        }
    }
 
 }