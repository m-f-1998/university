using UnityEngine;
using System;
using System.Collections.Generic;

public class Boxes : MonoBehaviour {
    
    public GameObject box;
    public GameObject enemy;
    public GameObject player;
    private GameObject newCube, cylinder;

    public string[] boxColorHexCodes = new string[]{
        "#98101E",
        "#5C12AB",
        "#1F512A",
        "#0f6b5f",
    };

    private int index = 0;
    private List <int> randomX = new List<int>();
    private List <int> randomZ = new List<int>();
    private int[] boxX = new int[]{
        -15, -7, 7, 15
    };

    private int num_obstacles = 4;
    private const int NUM_ENEMIES = 3;

    void Start() {
        for(int i = 0; i < (boxColorHexCodes.Length * 3) + num_obstacles + NUM_ENEMIES; i++){
            int numToAdd = UnityEngine.Random.Range(-20,20);
            while(randomX.Contains(numToAdd)){
                numToAdd = UnityEngine.Random.Range(-20,20);
            }
            randomX.Add(numToAdd);
        }

        for(int i = 0; i < (boxColorHexCodes.Length * 3) + num_obstacles + NUM_ENEMIES; i++){
            int numToAdd = UnityEngine.Random.Range(10,35);
            while(randomZ.Contains(numToAdd)){
                numToAdd = UnityEngine.Random.Range(10,35);
            }
            randomZ.Add(numToAdd);
        }

        while (index < randomX.Count) {
            if (index >= (boxColorHexCodes.Length * 3) + num_obstacles) {
                GameObject newEnemy = Instantiate(enemy, new Vector3(randomX[index / 3], 0, randomZ[index]), Quaternion.identity);
                newEnemy.name = "Enemy";
                newEnemy.tag = "Enemy";
                newEnemy.AddComponent(typeof(Enemy));
                newEnemy.GetComponent<Enemy>().Player = player;
                newEnemy.transform.localScale = new Vector3(.5f, .5f, .5f);
                newEnemy.SetActive(true);
            } if (index >= (boxColorHexCodes.Length * 3)) {
                cylinder = GameObject.CreatePrimitive(PrimitiveType.Cylinder);
                cylinder.name = "Obstacle";
                cylinder.tag = "Wall";
                cylinder.transform.position = new Vector3(randomX[index], 0, randomZ[index]);
                cylinder.GetComponent<MeshRenderer>().enabled = true;
            } else {
                Color color;
                if ( ColorUtility.TryParseHtmlString(boxColorHexCodes[Convert.ToInt32(Math.Floor(Convert.ToDecimal(index / 3)))], out color)) {
                    if (((index % 3) == 0)) {
                        newCube = GameObject.CreatePrimitive(PrimitiveType.Cube);
                        newCube.name = "Unlock Wall";
                        newCube.transform.position = new Vector3(boxX[index / 3], 0, 38f);
                        newCube.AddComponent(typeof(BoxCollider));
                        newCube.AddComponent(typeof(Wall));
                        newCube.SetActive(true);
                        newCube.GetComponent<MeshRenderer>().enabled = true;
                        newCube.GetComponent<MeshRenderer>().material.color = color;
                        newCube.GetComponent<Wall>().player = player;
                    }

                    Vector3 myVector = new Vector3(randomX[index], 0, randomZ[index]);
                    GameObject go = Instantiate(box, myVector, Quaternion.identity);
                    go.name = "Chest";
                    go.tag = "Box";
                    go.AddComponent(typeof(Rigidbody));
                    go.AddComponent(typeof(BoxCollider));
                    go.GetComponent<Rigidbody>().drag = 5;
                    go.GetComponent<Rigidbody>().constraints = RigidbodyConstraints.FreezeRotation | RigidbodyConstraints.FreezePositionY;
                    go.GetComponent<MeshRenderer>().enabled = true;
                    go.GetComponent<MeshRenderer>().material.color = color;
                }
            }
            index++;
        }
    }

}