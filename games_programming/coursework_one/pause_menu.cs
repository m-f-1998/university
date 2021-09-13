/**
 * @create date 03-02-2021 12:41:10
 * @modify date 19-02-2021 09:26:22
 * @desc [Pause Menu Screen]
 */

using UnityEngine;
using UnityEngine.UI;

public class PauseMenu : MonoBehaviour {

    public Text score;
    public GameObject panel, player;
    
    private bool GameIsPaused = false;

    void Start() {
        panel.SetActive(GameIsPaused);
        score.text = "Current Score: " + player.GetComponent<PlayerScore>().GetTotalScore();
    }

    void Update() {
        if (Input.GetKeyDown(KeyCode.P)) {
            if (GameIsPaused) Resume();
            else Pause();
        }
    }

    public void Resume() {
        Camera.main.GetComponent<AudioSource>().Play();
        GameIsPaused = false;
        panel.SetActive(GameIsPaused);
        Time.timeScale = 1f;
    }

    void Pause() {
        Camera.main.GetComponent<AudioSource>().Pause();
        score.text = "Current Score: " + player.GetComponent<PlayerScore>().GetTotalScore();
        GameIsPaused = true;
        panel.SetActive(GameIsPaused);
        Time.timeScale = 0f;
    }

}
