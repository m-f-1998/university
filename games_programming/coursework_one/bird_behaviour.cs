/**
 * @create date 07-02-2021 20:36:53
 * @modify date 19-02-2021 09:26:22
 * @desc [Controlls individual bird behaviour]
 */

using UnityEngine;

public class BirdBehaviour : MonoBehaviour {

    public GameObject player, coinPrefab;
    public Bird controller;
    public bool isMin = false, coin = false;

    private float noiseOffset, coinDrop = -1f;
    private bool isChild = false;

    void Start() {
        noiseOffset = Random.value * 10.0f;
    }

    void Update() {
        if (isChild) {
            var curPos = transform.position;
            var curRot = transform.rotation;

            if (!isMin && curPos.x > Camera.main.GetComponent<CameraSystem>().xMax || isMin && curPos.x < Camera.main.GetComponent<CameraSystem>().xMin) Destroy(gameObject);
            else if (!isMin && curPos.y > Camera.main.GetComponent<CameraSystem>().yMax || isMin && curPos.y < Camera.main.GetComponent<CameraSystem>().yMin) Destroy(gameObject);
            else {
                var noise = Mathf.PerlinNoise(Time.time, noiseOffset) * 2.0f - 1.0f;
                var velocity = controller.vel * (1.0f + noise * controller.velVar);
                
                var rotation = getRotation(curPos);
                if (rotation != curRot) transform.rotation = Quaternion.Slerp(rotation, curRot, Mathf.Exp(-controller.rotationCoeff * Time.deltaTime));

                var sys = Camera.main.GetComponent<CameraSystem>();
                if (coin) {
                    if (!isMin && curPos.x < sys.xMax || isMin && curPos.x > sys.xMin) coinDrop = Random.Range(curPos.x, sys.xMax - 0.01f);
                    coin = false;
                    player.GetComponent<PlayerMove>().IncreaseCoins(1);
                }

                if (coinDrop != -1f && ((!isMin && curPos.x <= coinDrop) || (isMin && curPos.x >= coinDrop))) {
                    var coinObj = Instantiate(coinPrefab, new Vector3(coinDrop, curPos.y, 0), Quaternion.Slerp(transform.rotation, UnityEngine.Random.rotation, 0.3f));
                    coinObj.GetComponent<Rigidbody2D>().gravityScale = 1f;
                    coinDrop = -1f;
                }

                Vector3 newPos = curPos + (isMin ? transform.right * -1 : transform.right) * (velocity * Time.deltaTime);
                newPos.z = 0;

                transform.position = newPos;
            }
        }
    }

    //MARK: Setters
    public void setChild() {
        isChild = true;
    }

    //MARK: Helpers
    private Quaternion getRotation(Vector3 curPos) {
        var sep = Vector3.zero;
        var ali = controller.transform.forward;
        var coh = controller.transform.position;
        var nearby = Physics.OverlapSphere(curPos, controller.dist, controller.search);

        foreach (var bird in nearby) {
            if (bird.gameObject == gameObject) continue;
            var diff = transform.position - bird.transform.transform.position;
            sep += diff * (Mathf.Clamp01(1.0f - diff.magnitude / controller.dist) / diff.magnitude);
            ali += bird.transform.forward;
            coh += bird.transform.position;
        }

        return Quaternion.FromToRotation(Vector3.up, (sep + (ali * 1.0f / nearby.Length) + ((coh * 1.0f / nearby.Length) - curPos).normalized).normalized);
    }
}