/**
 * @create date 02-02-2021 10:04:25
 * @modify date 19-02-2021 09:26:22
 * @desc [High-Score System]
 */

using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class HighScore : MonoBehaviour {
    public Text levelOneScore, levelTwoScore, levelThreeScore, totalScore;
    public Button back;

    void Start() {
        back.onClick.AddListener(BackButton);
        totalScore.text = "Highest Score: " + PlayerPrefs.GetString("high-score");
        levelOneScore.text = "Level 1: " + PlayerPrefs.GetString("score-level-1");
        levelTwoScore.text = "Level 2: " + PlayerPrefs.GetString("score-level-2");
        levelThreeScore.text = "Level 3: " + PlayerPrefs.GetString("score-level-3");
    }

    private void BackButton() {
        SceneManager.LoadScene("MainMenu");
    }
}
