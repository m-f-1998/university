using UnityEngine;
using System.Collections.Generic;

public class Wall: MonoBehaviour {

    private Dictionary<string, int> collected = new Dictionary<string, int>();
    public GameObject player;

    void OnCollisionEnter(Collision collision) {
        if (collision.gameObject.tag == "Box") {
            if (collision.gameObject.GetComponent<MeshRenderer>().material.color == gameObject.GetComponent<MeshRenderer>().material.color) {
                string colourString = collision.gameObject.GetComponent<MeshRenderer>().material.color.ToString();
                if (!collected.ContainsKey(colourString)) {
                    collected.Add(colourString, 1);
                } else {
                    collected[colourString] = collected[colourString] + 1;
                }
                if (collected[colourString] == 3) {
                    print ("boxes unlocked");
                }
                player.GetComponent<Score>().IncreaseScore();
                print(collected[colourString].ToString() + " Box Collected Of Colour " + colourString);
                Destroy(collision.gameObject);
            }
        }
        // Check Flags On Wall to Unlock Chest, Animation For Opening Chest, Needing Key/Coins
        collision.gameObject.GetComponent<Rigidbody>().velocity = Vector3.zero;
    }

}