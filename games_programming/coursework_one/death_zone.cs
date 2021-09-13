/**
 * @create date 02-02-2021 10:38:56
 * @modify date 19-02-2021 09:26:22
 * @desc [Water]
 */

using UnityEngine;

public class DeathZone : MonoBehaviour {
    void OnTriggerEnter2D(Collider2D coll) {
        if(coll.tag == "Player") coll.GetComponent<PlayerHealth>().Die("It's Difficult To Swim");
    }
}
