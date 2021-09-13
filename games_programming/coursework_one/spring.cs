/**
 * @create date 06-02-2021 10:54:32
 * @modify date 19-02-2021 09:26:22
 * @desc [A Bouncy Spring with Sound and Custom Height]
 */

using UnityEngine;

public class Spring : MonoBehaviour {
    
    public float ySpringSpeed = 100f;
    public AudioClip springJump;
    
    void OnCollisionEnter2D(Collision2D coll){
        if (coll.gameObject.tag == "Player") {
            Vector3 imp = coll.gameObject.transform.position - transform.position;
            if (imp.y > 0) {
                GetComponent<AudioSource>().clip = springJump;
                GetComponent<AudioSource>().Play();
                coll.collider.GetComponent<Rigidbody2D>().AddForce(Vector2.up * ySpringSpeed, ForceMode2D.Impulse);
            }
        }
    }

}