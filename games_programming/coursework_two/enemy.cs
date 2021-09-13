using UnityEngine;

public class Enemy : MonoBehaviour {

    public GameObject Player;
    float MoveSpeed = 1f;
    float MaxDist = 1.45f;
    float MinDist = 11f;

    bool flag = false;

    void Update() {
      
        transform.LookAt(Player.transform);
     
        if(Vector3.Distance(transform.position, Player.transform.position) < MinDist) {

            if (flag) {
                if (Vector3.Distance(transform.position, Player.transform.position) < MaxDist + 2) {
                    return;
                }
            }

            flag = false;
            
            GetComponent<Animator>().SetTrigger("move_forward");

            transform.position += transform.forward * MoveSpeed * Time.deltaTime;
           
            if(Vector3.Distance(transform.position, Player.transform.position) < MaxDist) {

                flag = true;
                
                GetComponent<Animator>().SetTrigger("attack_short");
                Player.GetComponent<Health>().DecreaseHealth();
            
            } 
    
        }
     
    }
}