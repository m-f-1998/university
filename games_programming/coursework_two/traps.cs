using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Traps : MonoBehaviour
{

    public static float maxPots;
    public float currentPots;
    public static float maxIce;
    public float currentIce;

    public Text potText;
    public Text iceText;

    // Start is called before the first frame update
    void Start()
    {
        maxPots = 2; //can be editted to test to one to test if things are working
        currentPots = maxPots;
        potText.text = "x " + currentPots;

        maxIce = 2;
        currentIce = maxIce;
        iceText.text = "x " + currentIce;
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
