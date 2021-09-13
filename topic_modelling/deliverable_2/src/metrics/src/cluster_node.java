import java.util.ArrayList;

/*ClusterNode class:
It is a standalone leafNode containing only a singular topic if its modelID, topicID are not null and its clusterID is null, this means that the topic has 
not been assigned to any clusters yet. If the clusterID is not null that means that the topic is assigned to a cluster, the clusterID represents the clusterID 
of the cluster's parent.
If the ClusterNode contains a modelID and topicID which are null, then it's the parent node. UsedModels contains the list of models that are within the cluster, to
avoid having duplicate models within a cluster.

*/

public class ClusterNode {
    public String modelID;
    public String topicID;
    public Integer clusterID;
    public ClusterNode left;
    public ClusterNode right;
    
    public ArrayList<String> usedModels;
}
