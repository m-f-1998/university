/**
 * @create date 19-02-2021 16:08:48
 * @modify date 19-02-2021 16:08:48
 * @desc [Main Controller for Moving Player]
 */

using UnityEngine;
using UnityEngine.SceneManagement;
using System.Collections.Generic;
using System;
using UnityEngine.UI;

public class PlayerMove : MonoBehaviour {
    public int speed = 10;
    public GameObject key, secretKey, deadlyKey, nextLevelPanel;
    public Button next, restartLevel;
    public GameObject[] stars;
    public Text goal, score, alert, deathReason;
    public Animator animator;
    public AudioClip secretKeySound, deathJump;

    private System.Random rnd = new System.Random();
    private bool facingRight = false, grounded = false, doubleJump = false;
    private float jumpHeight = -1;
    // private int level = 1;
    private int numCoins = 0;

    void Start() {
        nextLevelPanel.SetActive(false);
        next.onClick.AddListener(NextLevel);
        restartLevel.onClick.AddListener(RestartLevel);
    }

    void Update() {
        animator.SetFloat("Speed", Math.Abs(Input.GetAxis("Horizontal") * speed));
        if ((grounded || doubleJump) && Input.GetButtonDown("Jump")) Jump();
        if ((Input.GetAxis("Horizontal") < 0.0f && !facingRight) || (Input.GetAxis("Horizontal") > 0.0f && facingRight)) FlipPlayer();
        GetComponent<Rigidbody2D>().velocity = new Vector2 (Input.GetAxis("Horizontal") * speed, GetComponent<Rigidbody2D>().velocity.y);
    }

    void OnEnable() { // Generate Starting Positions for Keys and Player Each Time Scene Is Called (i.e. after dying)
        SceneManager.sceneLoaded += OnLevelFinishedLoading;
    }
         
    void OnDisable() {
        SceneManager.sceneLoaded -= OnLevelFinishedLoading;
    }

    void OnLevelFinishedLoading(Scene scene, LoadSceneMode mode) {
        numCoins = GameObject.FindGameObjectsWithTag("Coin").Length;
        List<List<int>> res = new List<List<int>>(); // min x, max x, y
        foreach (string tag in Constants.spawnable) {
            foreach (GameObject g in GameObject.FindGameObjectsWithTag(tag)) {
                res.Add(
                    new List<int>() {
                        (int) Math.Ceiling(g.transform.position.x - g.transform.localScale.x / 2),
                        (int) Math.Ceiling(g.transform.position.x + g.transform.localScale.x / 2),
                        (int) Math.Ceiling(g.transform.position.y + g.transform.localScale.y / 2) + 1
                    }
                );
            }
        }

        int ranSafeGround  = rnd.Next(0, res.Count);
        if (res[ranSafeGround][0] < res[ranSafeGround][1]) {
            int ranX  = rnd.Next(res[ranSafeGround][0], res[ranSafeGround][1]); // random starting position
            transform.position = new Vector2(ranX, res[ranSafeGround][2]);
            res.Remove(res[ranSafeGround]);
            
            int col = rnd.Next(0, Constants.colorsStandard.Count);
            if (GetLevel() != 2) goal.text = "Goal: Collect the keys that make the colour '" + Constants.colorsStandard[col][0] + "'";

            for (int i = 0; i < Constants.keys[GetLevel()]["NUM_NORMAL_KEYS"]; i++)
                createKey(ref res, key).GetComponent<SpriteRenderer>().color = hexColor(Constants.colorsStandard[col][i + 1]);

            for (int i = 0; i < Constants.keys[GetLevel()]["NUM_DEADLY_KEYS"]; i++)
                createKey(ref res, deadlyKey).GetComponent<SpriteRenderer>().color = hexColor(Constants.colorsDeadly[rnd.Next(0, 3)]);

            for (int i = 0; i < Constants.keys[GetLevel()]["NUM_SECRET_KEYS"]; i++)
                createKey(ref res, secretKey).GetComponent<SpriteRenderer>().color = Color.yellow;
        }
    }

    void OnTriggerEnter2D(Collider2D coll) {
        switch (coll.gameObject.tag) {
            case "Exit":
                if (GetComponent<PlayerScore>().GetNumKeysCollected() == Constants.keys[GetLevel()]["NUM_NORMAL_KEYS"]) ShowFinishScene();
                else CreateAlert("You're Missing Some Keys!");
                break;
            case "SecretKey":
                GetComponent<AudioSource>().clip = secretKeySound;
                GetComponent<AudioSource>().Play();
                CreateAlert("Secret Area Unlocked!");
                foreach (GameObject i in GameObject.FindGameObjectsWithTag("SecretWall")) Destroy(i);
                Destroy(coll.gameObject);
                break;
            case "DeadlyKey":
                GetComponent<PlayerHealth>().Die("Watch Out For That Deadly Key!");
                Destroy(coll.gameObject);
                break;
        }
    }

    void OnCollisionEnter2D(Collision2D coll){
        if (jumpHeight != -1 && transform.position.y + 25 < jumpHeight) {
            GetComponent<PlayerHealth>().Die("Fell From A Great Height");
        } else {
            switch (coll.gameObject.tag) {
                case "Platform":
                    transform.SetParent(coll.transform);
                    goto case "Ground";
                case "Ground":
                case "SafeGround":
                case "SecretGround":
                    grounded = true;
                    animator.SetBool("IsJumping", false);
                    break;
                case "Enemy":
                    HitEnemy(coll);
                    break;
            }
        }
        jumpHeight = -1;
    }
    
    void OnCollisionExit2D(Collision2D coll){
        if (coll.gameObject.tag == "Platform") {
            grounded = false;
            jumpHeight = transform.position.y;
            transform.SetParent(null);
        } else if (coll.gameObject.tag == "Ground" || coll.gameObject.tag == "SafeGround" || coll.gameObject.tag == "SecretGround") {
            jumpHeight = transform.position.y;
            grounded = false;
        }
    }

    //MARK: Getters
    
    public int GetLevel() {
        return Int32.Parse(SceneManager.GetActiveScene().name.Substring(SceneManager.GetActiveScene().name.Length - 1));
    }

    public bool GetGrounded() {
        return grounded;
    }

    //MARK: Setters

    public void IncreaseCoins(int coins) {
        coins += coins;
    }

    //MARK: Helper functions

    private GameObject createKey(ref List<List<int>> res, GameObject key) {
        int ranSafeGround  = rnd.Next(0, res.Count);
        int ranX  = rnd.Next(res[ranSafeGround][0], res[ranSafeGround][1]);
        var prefab = Instantiate(key, new Vector2(ranX, res[ranSafeGround][2]), Quaternion.identity);
        res.Remove(res[ranSafeGround]);
        return prefab;
    }

    private Color hexColor(string hexColor) {
        Color c = new Color();
        ColorUtility.TryParseHtmlString(hexColor, out c);
        return c;
    }

    private void Jump() {
        GetComponent<Rigidbody2D>().AddForce(Vector2.up * 1250);
        if (grounded) {
            animator.SetBool("IsJumping", true);
            doubleJump = true;
            grounded = false;
        } else {
            doubleJump = false;
        }
    }

    private void FlipPlayer() {
        facingRight = !facingRight;
        Vector2 localScale = transform.localScale;
        localScale.x *= -1;
        transform.localScale = localScale;
    }

    private void RestartLevel() {
        GetComponent<PlayerScore>().ResetKeys();
        Time.timeScale = 1f;
        SceneManager.LoadScene("Level " + GetLevel());
        Camera.main.GetComponent<AudioSource>().Play();
    }
    
    private void NextLevel() {
        if (GetLevel() == 3) {
            GetComponent<PlayerScore>().FinishGame();
            SceneManager.LoadScene("Main Menu");
        } else {
            Time.timeScale = 1f;
            GetComponent<PlayerScore>().ResetKeys();
            SceneManager.LoadScene("Level " + (GetLevel() + 1));
            Camera.main.GetComponent<AudioSource>().Play();
        }
    }

    private void CreateAlert(string m) {
        alert.text = m;
        Invoke("DisableText", 2.5f);
    }

    private void DisableText() {
        alert.text = "";
    }

    private void HitEnemy(Collision2D coll) {
        Vector3 imp = coll.gameObject.transform.position - transform.position;
        if (Mathf.Abs(imp.x) <= Mathf.Abs(imp.y) && imp.y <= 0) {
            if (coll.gameObject.GetComponent<StandardEnemy>() != null && !coll.gameObject.GetComponent<StandardEnemy>().yAxis) { // Destroy Enemy
                GetComponent<Rigidbody2D>().AddForce(Vector2.up * 1000);
                coll.gameObject.GetComponent<Rigidbody2D>().AddForce(Vector2.right * 200);
                coll.gameObject.GetComponent<Rigidbody2D>().gravityScale = 20;
                coll.gameObject.GetComponent<Rigidbody2D>().freezeRotation = false;
                coll.gameObject.GetComponent<BoxCollider2D>().enabled = false;
                GetComponent<AudioSource> ().clip = deathJump;
                GetComponent<AudioSource> ().Play();
                return;
            }
        }
        Vector2 forceDir = imp.x <= 0 ? Vector2.right : Vector2.left;
        GetComponent<Rigidbody2D>().velocity = Vector2.zero;
        GetComponent<Rigidbody2D>().AddForce(new Vector2(0, 120), ForceMode2D.Impulse);
        GetComponent<Rigidbody2D>().AddForce(forceDir * 10000);
        GetComponent<PlayerHealth>().reduceHealthByOne();
    }

    private void SetStars() {
        var minTime3 = GetComponent<PlayerScore>().timeLeft - (GetComponent<PlayerScore>().timeLeft/3);
        var minTime2 = GetComponent<PlayerScore>().timeLeft - (GetComponent<PlayerScore>().timeLeft/2);
        var stars3 = ((numCoins * (10 + (minTime3 * 5))) + (Constants.keys[GetLevel()]["NUM_NORMAL_KEYS"] * (20 + (minTime3 * 5)))) + (minTime3 * 5);
        var stars2 = ((numCoins * (10 + (minTime2 * 5))) + (Constants.keys[GetLevel()]["NUM_NORMAL_KEYS"] * (20 + (minTime2 * 5)))) + (minTime2 * 5);

        if (GetComponent<PlayerScore>().GetLevelScore() > minTime3) for (int i = 0; i < 2; i++) stars[i].SetActive(true);
        else if (GetComponent<PlayerScore>().GetLevelScore() > minTime2) for (int i = 0; i < 1; i++) stars[i].SetActive(true);
        else stars[0].SetActive(true);
    }

    private void ShowFinishScene() {
        if (int.Parse(PlayerPrefs.GetString("score-level-" + GetLevel().ToString())) < GetComponent<PlayerScore>().GetLevelScore())
            PlayerPrefs.SetString(("score-level-" + GetLevel().ToString()), GetComponent<PlayerScore>().GetLevelScore().ToString());
        nextLevelPanel.SetActive(true);
        Camera.main.GetComponent<AudioSource>().Pause();
        score.text = "Score: " + GetComponent<PlayerScore>().GetLevelScore().ToString();
        Time.timeScale = 0f;
        SetStars();
    }

}
