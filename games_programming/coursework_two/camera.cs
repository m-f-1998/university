using UnityEngine;

public class CameraSystem : MonoBehaviour {
    
    public GameObject player;
    public float xMin, xMax, zMin, zMax, y;

    void Update() {
        float x = Mathf.Clamp(player.transform.position.x, xMin, xMax);
        float z = Mathf.Clamp(player.transform.position.z, zMin, zMax);
        gameObject.transform.position = new Vector3(x, y, z);
    
    }
}
