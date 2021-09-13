using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Health : MonoBehaviour
{

    public Text healthText;
    public float fullHealth;
    public float currentHealth;

    // Start is called before the first frame update
    void Start()
    {
        fullHealth = 100f;
        currentHealth = fullHealth;
        healthText.text = "Player Health: " + currentHealth;
    }

    public void DecreaseHealth()
    {
        currentHealth = currentHealth - 10f;
        healthText.text = "Player Health: " + currentHealth;
    }
}
