/**
 * @create date 06-02-2021 10:54:32
 * @modify date 18-02-2021 09:26:22
 * @desc [Keep Tally of a Player's Score, Time, & Global Score]
 */

using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class PlayerScore : MonoBehaviour {

    public float timeLeft = 120;
    public GameObject timeLeftUI, scoreUI;
    public AudioClip coin, key;
    public Animator animator;

    private int levelScore = 0, totalScore = 0, keysCollected = 0;
    private bool expired = false;

    void OnEnable() {
        SceneManager.sceneLoaded += CheckKeys;
    }
         
    void OnDisable() {
        SceneManager.sceneLoaded -= CheckKeys;
    }

    void CheckKeys(Scene scene, LoadSceneMode mode) {
        if (GetNumKeysCollected() == Constants.keys[GetComponent<PlayerMove>().GetLevel()]["NUM_NORMAL_KEYS"])
            animator.SetBool("OpenDoor", true);
    }

    void Update () {
        timeLeft -= Time.deltaTime;
        timeLeftUI.GetComponent<Text>().text = ("Time Left: " + (int) timeLeft);
        scoreUI.GetComponent<Text>().text = ("Score: " + totalScore);
        
        if (totalScore < 0 && !expired) {
            GetComponent<PlayerHealth>().Die("A Negative Score - Really?");
            expired = true;
        }
        else if (timeLeft < 0.1f && !expired) {
            GetComponent<PlayerHealth>().Die("Timer Expired");
            expired = true;
        }
    }

    void OnTriggerEnter2D(Collider2D coll) {
        switch(coll.gameObject.tag) {
            case "Coin":
                GetComponent<AudioSource>().clip = coin;
                GetComponent<AudioSource>().Play();
                IncreaseScore(10);
                Destroy(coll.gameObject);
                break;
            case "Key":
                GetComponent<AudioSource>().clip = key;
                GetComponent<AudioSource>().Play();
                IncreaseScore(20);
                FoundKey();
                Destroy(coll.gameObject);
                break;
        }
    }

    // MARK: Getters

    public int GetLevelScore() {
        return levelScore;
    }

    public int GetTotalScore() {
        return totalScore;
    }

    public int GetNumKeysCollected() {
        return keysCollected;
    }

    // MARK: Setters

    public void SetReducedScore() {
        if (totalScore - 5 + (int) (timeLeft * 2.5) < 0) {
            totalScore -= 5 + (int) (timeLeft * 2.5);
            levelScore -= 5 + (int) (timeLeft * 2.5);
        }
    }

    // MARK: Helper Functions

    private void IncreaseScore(int bonus) {
        totalScore += bonus + (int) (timeLeft * 5);
        levelScore += bonus + (int) (timeLeft * 5);
    }

    void DisableText() { // called using invoke
        GetComponent<PlayerMove>().alert.text = "";
    }

    public void ResetKeys() {
        animator.SetBool("OpenDoor", false);
        keysCollected = 0;
        levelScore = 0;
    }

    public void FinishGame() {
        IncreaseScore(totalScore);
        if (int.Parse(PlayerPrefs.GetString("high-score")) < totalScore)
            PlayerPrefs.SetString("high-score", totalScore.ToString());
    }

    private void FoundKey() {
        keysCollected += 1;
        if (GetNumKeysCollected() == Constants.keys[GetComponent<PlayerMove>().GetLevel()]["NUM_NORMAL_KEYS"]) {
            GetComponent<PlayerMove>().alert.text = "Door Unlocked!";
            Invoke("DisableText", 2.5f);
            animator.SetBool("OpenDoor", true);
        }
    }
}
