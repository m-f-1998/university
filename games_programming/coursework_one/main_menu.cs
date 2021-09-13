/**
 * @create date 03-02-2021 08:04:37
 * @modify date 19-02-2021 09:26:22
 * @desc [Main Menu Screen]
 */

using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MainMenu : MonoBehaviour {

    public Button start, score, instructions;

    void Start() {
		start.onClick.AddListener(StartGame);
        score.onClick.AddListener(HighScore);
        instructions.onClick.AddListener(Instructions);

        if (!(PlayerPrefs.HasKey("high-score"))) {
            PlayerPrefs.SetString("high-score", "0");
        }
        if (!(PlayerPrefs.HasKey("score-level-1"))) {
            PlayerPrefs.SetString("score-level-1", "0");
        }
        if (!(PlayerPrefs.HasKey("score-level-2"))) {
            PlayerPrefs.SetString("score-level-2", "0");
        }
        if (!(PlayerPrefs.HasKey("score-level-3"))) {
            PlayerPrefs.SetString("score-level-3", "0");
        }
    }

    private void StartGame() {
        SceneManager.LoadScene("Level 1");
    }

    private void HighScore() {
        SceneManager.LoadScene("High Score");
    }

    private void Instructions() {
        SceneManager.LoadScene("Instructions");
    }

}
