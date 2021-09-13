/**
 * @create date 06-02-2021 10:56:32
 * @modify date 18-02-2021 09:26:22
 * @desc [Keep Tally of a Player's Health]
 */
 
using UnityEngine;

public class PlayerHealth : MonoBehaviour {

    public GameObject death;
    public AudioClip died, hit;

    private float health = 3f;

    void Start() {
        death.SetActive(false);
    }

    public void reduceHealthByOne() {
        health--;
        if (health == 0f) Die("No Health Left");
        else {
            PlaySound(hit);
            GetComponent<PlayerScore>().SetReducedScore();
        }
    }

    public void Die(string reason) {
        Time.timeScale = 0f;
        PlaySound(died);
        GetComponent<PlayerMove>().deathReason.text = "You Died: " + reason;
        death.SetActive(true);
        Camera.main.GetComponent<AudioSource>().Pause();
    }

    private void PlaySound(AudioClip x) {
        GetComponent<AudioSource>().clip = x;
        GetComponent<AudioSource>().Play();
    }
}
