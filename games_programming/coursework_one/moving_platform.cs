/**
 * @create date 03-02-2021 10:29:51
 * @modify date 19-02-2021 09:26:22
 * @desc [Moving Platform, Direction Dependant]
 */

using UnityEngine;

public class MovingPlatform : MonoBehaviour {
    
    public float speed = 10f, distance = 10f;
    public enum Direction{Horizontal, Vertical, Down, DiagonalLeft, DiagonalRight};
    public Direction direction;

    private Vector3 startPosition;

    void Start () {
        startPosition = transform.position;
    }

    void Update () {
        var right = (speed < 0 && transform.position.x < startPosition.x) || (speed > 0 && transform.position.x > startPosition.x + distance);
        var left = (speed < 0 && transform.position.x > startPosition.x) || (speed > 0 && transform.position.x < startPosition.x + distance);
        var up = (speed < 0 && transform.position.y < startPosition.y) || (speed > 0 && transform.position.y > startPosition.y + distance);
        var down = (speed < 0 && transform.position.y > startPosition.y) || (speed > 0 && transform.position.y < startPosition.y - distance);
         switch(direction){
            case Direction.Horizontal:
                if (right) speed *= -1;
                transform.position = new Vector2(transform.position.x + speed * Time.deltaTime, transform.position.y);
                break;
            case Direction.Vertical:
                if (up) speed *= -1;
                transform.position = new Vector2(transform.position.x, transform.position.y + speed * Time.deltaTime);
                break;
            case Direction.Down:
                if (down) speed *= -1;
                transform.position = new Vector2(transform.position.x, transform.position.y - speed * Time.deltaTime);
                break;
            case Direction.DiagonalLeft:
                if (left && up) speed *= -1;
                transform.position = new Vector2(transform.position.x - speed * Time.deltaTime, transform.position.y + speed * Time.deltaTime);
                break;
            case Direction.DiagonalRight:    
                if (right && up) speed *= -1;
                transform.position = new Vector2(transform.position.x + speed * Time.deltaTime, transform.position.y + speed * Time.deltaTime);
                break;
        }
    }
}
