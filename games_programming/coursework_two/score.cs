using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Score : MonoBehaviour
{

    public Text scoreText;
    public static float baseScore;
    public float currentScore;

    // Start is called before the first frame update
    void Start()
    {
        baseScore = 0f;
        currentScore = baseScore;
        scoreText.text = "Score: " + currentScore;
    }

    // Update is called once per frame
    void Update()
    {
        //testing purposes to be sure this works
        if(Input.GetKeyDown(KeyCode.C))
        {
            IncreaseScore();
        }
    }

    public void IncreaseScore() {
        currentScore = currentScore + 10f;
        scoreText.text = "Score: " + currentScore;
    }
}
