/**
 * @create date 02-02-2021 10:04:59
 * @modify date 19-02-2021 09:26:22
 * @desc [Instruction Screen]
 */

using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class Instructions : MonoBehaviour {
    public Button back;

    void Start() {
        back.onClick.AddListener(BackButton);
    }

    private void BackButton() {
        SceneManager.LoadScene("MainMenu");
    }
}
