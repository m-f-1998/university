/**
 * @create date 02-02-2021 10:38:56
 * @modify date 19-02-2021 09:26:22
 * @desc [Coin]
 */

using UnityEngine;

public class Coin : MonoBehaviour {

    void Update() {
        if (transform.position.y < Camera.main.GetComponent<CameraSystem>().yMin) Destroy(gameObject);
        if (transform.position.y > Camera.main.GetComponent<CameraSystem>().yMax) Destroy(gameObject);
    }

    void OnTriggerEnter2D(Collider2D coll) {
        if (coll.gameObject.tag == "Ground" || coll.gameObject.tag == "SafeGround" || coll.gameObject.tag == "SecretGround")
            if (GetComponent<Rigidbody2D>().gravityScale == 1) {
                GetComponent<Rigidbody2D>().velocity = Vector2.zero;
                GetComponent<Rigidbody2D>().gravityScale = 0f;
            }
    }
}
