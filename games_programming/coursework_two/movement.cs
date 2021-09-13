using UnityEngine;

public class Movement : MonoBehaviour {

    Vector3 up = Vector3.zero,
    right = new Vector3(0, 90, 0),
    down = new Vector3(0, 180, 0),
    left = new Vector3(0, 270, 0),
    currentDirection = Vector3.zero;

    Vector3 nextPos, destination, direction;

    float speed = 2f;
    float rayLength = 1f;

    bool canMove;

    public float xMin, xMax, zMin, zMax;

    void Start() {

        currentDirection = up;
        nextPos = Vector3.forward;
        destination = transform.position;
    
    }

    void Update() {

        Move();
    
    }

    void Move() {

        if (destination != transform.position && !Input.anyKey) {

            GetComponent<Animator>().ResetTrigger("Run");
            destination = transform.position;

        }

        transform.position = Vector3.MoveTowards(transform.position, destination, speed * Time.deltaTime);

        if (Input.GetKey(KeyCode.W) || Input.GetKey(KeyCode.UpArrow)) {

            nextPos = Vector3.forward;
            currentDirection = up;
            canMove = true;

        }

        if (Input.GetKey(KeyCode.S) || Input.GetKey(KeyCode.DownArrow)) {

            nextPos = Vector3.back;
            currentDirection = down;
            canMove = true;

        }

        if (Input.GetKey(KeyCode.D) || Input.GetKey(KeyCode.RightArrow)) {

            nextPos = Vector3.right;
            currentDirection = right;
            canMove = true;

        }

        if (Input.GetKey(KeyCode.A) || Input.GetKey(KeyCode.LeftArrow)) {

            nextPos = Vector3.left;
            currentDirection = left;
            canMove = true;

        }

        if (Vector3.Distance(destination, transform.position) <= 0.00001f) {

            transform.localEulerAngles = currentDirection;

            if (canMove) {

                destination = transform.position + nextPos;

                if (destination.x <= xMin || destination.x >= xMax || destination.z <= zMin || destination.z >= zMax) {
                    destination = transform.position;
                } else GetComponent<Animator>().SetTrigger("Run");

                direction = nextPos;
                canMove = false;

            }

        }

    }
    
}
