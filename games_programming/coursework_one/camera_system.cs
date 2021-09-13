/**
 * @create date 07-02-2021 20:38:32
 * @modify date 19-02-2021 09:26:22
 * @desc [Controls Main Camera, Moving with Player, and its Boundaries ]
 */

using UnityEngine;

public class CameraSystem : MonoBehaviour {
    private GameObject player;
    public float xMin, xMax, yMin, yMax;

    void Start() {
        player = GameObject.FindGameObjectWithTag("Player");
    }

    void Update() {
        float x = Mathf.Clamp(player.transform.position.x, xMin, xMax);
        float y = Mathf.Clamp(player.transform.position.y, yMin, yMax);
        gameObject.transform.position = new Vector3(x, y, gameObject.transform.position.z);
    }
}
