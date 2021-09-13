import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Comparator;

public class TopicAlignment extends AbstractMetric {
	final static String CONFIG_ID_ENABLED = "enabled";
	final static String CONFIG_ID_M = "top_M";
	
	public static boolean enabled;
	public static int M = 3;
	public int clusterID = 0;
	
	ArrayList topMVector = new ArrayList();

	@Override
	public String getMetricName() {
		return "topic_alignment";
	}
	
	public void loadParameters(Configuration config) {
		/** Load the config values for use **/

		enabled = loadParameterBoolean(config,CONFIG_ID_ENABLED,true);
		M = loadParameterInteger(config,CONFIG_ID_M,3);
	}

	@Override
	public TopicModel[] calculate(TopicModel[] models,  Configuration configuration) {
		loadParameters(configuration);
		
		if(!enabled) {
			// Metric disabled so it doesn't run
			System.out.println("This metric has been disabled in the configuration file");
		} else {
			Map<String, ClusterNode> leafNodes = new TreeMap<>();
			Map<String, Map<String, Map<String, Double>>> normalisedModels = normaliseModel(models);
			List<Map<String, Map<String, Map<String, Double>>>> V = topM(normalisedModels, leafNodes);
			
			
			Map<String, Double[][]> DistanceMatrices = distanceCalculation(V);
	
			
			
			Map<String, ClusterNode> clusteredModels = agglomerativeClustering(DistanceMatrices, leafNodes);	
			
			writeFile(clusteredModels, configuration);
		}

		return models;
	}
	
	//gets the shortest distance in the distance matrices and passes the value to updateDistanceMatrix  for processing and returns a tree of clusters
	private Map<String, ClusterNode>  agglomerativeClustering (Map<String,Double[][]> distanceMatrices, Map<String, ClusterNode> leafNodes){	
		Map<String, ClusterNode> clusterOfTrees = new TreeMap<>();
		String[] shortestValues = new String[3];

		shortestValues[0] = "1";
		while(shortestValues[0] != null) {
			shortestValues = shortestDistance(distanceMatrices);
			
			if (shortestValues[0] == null) {
				break;
			}
			updateDistanceMatrix(distanceMatrices, leafNodes, shortestValues, clusterOfTrees);
		}
		
		return clusterOfTrees;
	}
	
	
	//normalising words within a model
	private Map<String, Map<String, Map<String, Double>>> normaliseModel(TopicModel[] models) {
		Double total = 0.0;
	
		TreeMap<String, Map<String, Map<String, Double>>> normalisedModels= new TreeMap<>();
		
		for (TopicModel model: models) {
			//Full topics dictionary
			
			Map<String, Map<String, Integer>> topics = model.getTopics();
			ArrayList<Double> listOfSums = new ArrayList<Double>();
			
			TreeMap<String, Map<String, Double>> normalisedTopics= new TreeMap<>();
			
			
			//going through the topics to get their sums
			for (Entry<String, Map<String, Integer>> topic: topics.entrySet()) {			
				Map<String, Integer> topicValues = topic.getValue();
				
				for(Entry<String,Integer> word : topicValues.entrySet()) {
					total = total + word.getValue();
				}
			
				listOfSums.add(total);
				total = 0.0;			
			}
		
			//going through the topics to change the values off the words from weight to word probabilities
			int counter = 0;
			for (Entry<String, Map<String, Integer>> topic: topics.entrySet()) {			
				Map<String, Integer> topicValues = topic.getValue();
				TreeMap<String, Double> normalisedTopicWords = new TreeMap<>();
				for(Entry<String,Integer> word : topicValues.entrySet()) {				
					normalisedTopicWords.put(word.getKey(), Double.valueOf(Double.valueOf(word.getValue())/listOfSums.get(counter)));					
				}			
				counter ++;
				normalisedTopics.put(topic.getKey(), normalisedTopicWords);	
			}		
			
			normalisedModels.put(model.getID(), normalisedTopics);
		}
		
		return normalisedModels;
	}
	
	//getting the topM words from each topic and adding each topic as a leaf node in a leaf node array
	private List<Map<String, Map<String, Map<String, Double>>>> topM(Map<String, Map<String, Map<String, Double>>> models, Map<String, ClusterNode> leafNodes) {
		
		List<Map<String, Map<String, Map<String, Double>>>> allModels = new ArrayList<Map<String, Map<String, Map<String, Double>>>>();
		
		for (Entry<String,Map<String,Map<String,Double>>> model: models.entrySet()) {
			//Full topics dictionary
			
			Map<String, Map<String, Double>> topics = model.getValue();
			Map<String, Map<String, Double>> normalisedTopics= new TreeMap<>();
			
			TreeMap<String, Map<String, Map<String, Double>>> normalisedModel= new TreeMap<>();
			
			
			//makes every topic a leaf node and adds it to a list of leaf nodes
			for (Entry<String, Map<String, Double>> topic: topics.entrySet()) {
				//word-probabilities
				Map<String, Double> value = topic.getValue();
				
				Map<String, Double> v = sortMap(value, M );
				ClusterNode leafNode = new ClusterNode();
				
				leafNode.left = null;
				leafNode.right = null;
				leafNode.modelID = model.getKey();
				leafNode.topicID = topic.getKey();
				
				leafNodes.put(model.getKey() + ":" + topic.getKey(), leafNode);
				
				normalisedTopics.put(topic.getKey(), v);	
			}
			
			normalisedModel.put(model.getKey(), normalisedTopics);
			allModels.add(normalisedModel);
		}
		
		return allModels;
	}
	
	//sorts a map in descending order to get the words with the highest probabilities
	private Map<String, Double> sortMap(Map<String, Double> value, int m) {
		TreeMap<String, Double> topWords = new TreeMap<>();
		 
		value.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		    .limit(m)
		    .forEachOrdered(x -> topWords.put(x.getKey(), x.getValue()));

		return topWords;
		
	}
	
	
	//creates a matrix which contains distance matrices between all topics of all models 
	private Map<String, Double[][]> distanceCalculation(List<Map<String, Map<String, Map<String, Double>>>> V) {
		Map<String, Double[][]> fullDistanceMatrix = new TreeMap<>();
		
		for (int i = 0; i < V.size(); i++) {
			for (int j = i+1; j < V.size(); j++) {
						
				String iKey = "";
				String jKey = "";
				for(String key: V.get(i).keySet()) {
					iKey = key;
				}
				
				for(String key: V.get(j).keySet()) {
					jKey = key;
				}
				
				Double[][] distanceMatrix = new Double[V.get(i).get(iKey).size()][V.get(j).get(jKey).size()];
					
				int iTopicCounter = 0;
				for ( Entry<String, Map<String, Double>> modelITopics : V.get(i).get(iKey).entrySet()) {
					int jTopicCounter = 0;
					
					for ( Entry<String, Map<String, Double>> modelJTopics : V.get(j).get(jKey).entrySet()) {
						Map<String, Double> topicsI = modelITopics.getValue();
						Map<String, Double> topicsJ = modelJTopics.getValue();

						Double sum = topicsI.values().stream().reduce((Double) 0.0, Double::sum);
						Double sum2 = topicsJ.values().stream().reduce((Double) 0.0, Double::sum);

						distanceMatrix[iTopicCounter][jTopicCounter] = Math.abs(sum-sum2);					
						jTopicCounter = jTopicCounter + 1;
					}
					iTopicCounter = iTopicCounter + 1;
				}
				fullDistanceMatrix.put(iKey + ":" + jKey , distanceMatrix);
			}
		}
		
		return fullDistanceMatrix;
	}
	
		
	
	//gets the shortest distance between two topics in the distance matrix of models
	private String[] shortestDistance(Map<String,Double[][]> distanceMatrices) {
		
		String[] smallestValues = new String[3];
		
		
		Double shortestDistance = 500.0;
		for(Entry<String, Double[][]> distance : distanceMatrices.entrySet()) {
			for (int i = 0; i < distance.getValue().length; i++) { 
				for (int j = 0; j < distance.getValue()[i].length; j++) { 
					Double currentDistance = distance.getValue()[i][j];
					if (currentDistance < shortestDistance && currentDistance != -1.0) {
						shortestDistance = currentDistance;
						
						smallestValues[0] = String.valueOf(i);
						smallestValues[1] = String.valueOf(j);
						smallestValues[2] = distance.getKey();
	        	 	}
	         	}
			}
		}
		
		return smallestValues;
	}
	
	//joins 2 nodes or 2 clusters into a new cluster
	private ClusterNode joinNodes(ClusterNode node1, ClusterNode node2, Integer clusterID) {
		ClusterNode parent = new ClusterNode();
		
		parent.modelID = null;
		parent.topicID = null;
		parent.clusterID = clusterID;
		
		parent.left = node1;
		parent.right = node2;

		return parent;
	}
	
	
	
	
	//goes through all the clustering cases and performs the clustering of nodes or cluster, and calls the relevant methods to update the distance matrix
	private void updateDistanceMatrix(Map<String,Double[][]> distanceMatrices, Map<String, ClusterNode> leafNodes, String[] shortestValues, Map<String, ClusterNode> clusterOfTrees) {
		
		Integer shortestI = Integer.parseInt(shortestValues[0]);
		Integer shortestJ = Integer.parseInt(shortestValues[1]);
		String shortestKey = shortestValues[2];
		
		String model1 = shortestKey.split(":", 2)[0];
		String model2 = shortestKey.split(":", 2)[1];
		
		Integer topicI = shortestI;
		Integer topicJ = shortestJ;

		
		String model1Topic = model1 + ":topic" + topicI;
		String model2Topic = model2 + ":topic" + topicJ;

		//System.out.println(model1Topic);
		//System.out.println(model2Topic);
		
		ClusterNode node1 = leafNodes.get(model1Topic);
		ClusterNode node2 = leafNodes.get(model2Topic);

		//both nodes are standalone topics, and do not belong to a cluster
		if (node1.clusterID == null && node2.clusterID == null) {
			clusterID = clusterID + 1;
			node1.clusterID = clusterID;
			node2.clusterID = clusterID;
			
			nullifyMatrixRow(distanceMatrices, shortestI, shortestJ, shortestKey);
			nullifyMatrixColumn(distanceMatrices, shortestI, shortestJ, shortestKey);
			
			ClusterNode parent = joinNodes(node1, node2, node1.clusterID);
			parent.usedModels = new ArrayList<String>();
			parent.usedModels.add(node1.modelID);
			parent.usedModels.add(node2.modelID);

			clusterOfTrees.put(Integer.toString(parent.clusterID), parent);
		
			//node1 is part of a cluster and node2 is a standalone topic, and does not belong to a cluster
		} else if (node1.clusterID != null && node2.clusterID == null) {
			ClusterNode root = clusterOfTrees.get(Integer.toString(node1.clusterID));
			
			if (!root.usedModels.contains(node2.modelID)) {
				inOrder(root, node2, distanceMatrices);
				node2.clusterID= root.clusterID;
				ClusterNode newRoot = joinNodes(root, node2, root.clusterID);
				newRoot.usedModels = new ArrayList<String>();
				newRoot.usedModels.add(node2.modelID);
				newRoot.usedModels.addAll(root.usedModels);
				
				clusterOfTrees.remove(Integer.toString(root.clusterID));
				clusterOfTrees.put(Integer.toString(newRoot.clusterID), newRoot);
			}
			
			//node2 is part of a cluster and node1 is a standalone topic, and does not belong to a cluster
		} else if (node2.clusterID != null && node1.clusterID == null) {
			ClusterNode root = clusterOfTrees.get(Integer.toString(node2.clusterID));
			if (!root.usedModels.contains(node1.modelID)) {
				inOrder(root, node1, distanceMatrices);
				
				node1.clusterID= root.clusterID;
				ClusterNode newRoot = joinNodes(root, node1, root.clusterID);
				newRoot.usedModels = new ArrayList<String>();
				newRoot.usedModels.add(node1.modelID);
				newRoot.usedModels.addAll(root.usedModels);
				
				clusterOfTrees.remove(Integer.toString(root.clusterID));
				clusterOfTrees.put(Integer.toString(newRoot.clusterID), newRoot);
			} 			
			//both nodes belong to a cluster
		} else if (node2.clusterID != null && node1.clusterID != null) {
			ClusterNode node1Root = clusterOfTrees.get(Integer.toString(node1.clusterID));
			ClusterNode node2Root = clusterOfTrees.get(Integer.toString(node2.clusterID));

			boolean isSame = false;
			//checks whether the 2 clusters have common models or not
			for (String node1Model : node1Root.usedModels) {
				for (String node2Model : node2Root.usedModels) {
					if (node1Model == node2Model) {
						inOrder(node1, node2, distanceMatrices);
						distanceMatrices.get(node1.modelID + ":" + node2.modelID)[shortestI][shortestJ] = -1.0;
						
						isSame = true;
						break;
					}
				}
			}
			
			//if the 2 clusters have common models then the intersection between every leaf of both cluster is nullified to avoid, having a potential 
			//match again. The clusters are not combined together.
			if (isSame) {
				ArrayList<ClusterNode> leafNodes1 = new ArrayList<>();
				ArrayList<ClusterNode> leafNodes2 = new ArrayList<>();
				leafNodeGrabber(node1Root, leafNodes1);
				leafNodeGrabber(node2Root, leafNodes2);
				singlePointMatrixNullify(leafNodes1, leafNodes2, distanceMatrices);
				return;
			}
			
			//if there are no similar models between the clusters, then they are added together
			if (!node1Root.usedModels.containsAll(node2Root.usedModels)) {
				ArrayList<ClusterNode> leafNodes1 = new ArrayList<>();
				ArrayList<ClusterNode> leafNodes2 = new ArrayList<>();
				leafNodeGrabber(node1Root, leafNodes1);
				leafNodeGrabber(node2Root, leafNodes2);
				
				//goes through every leaf within the clusters and nullifies the rows and columns between them, to avoid having nodes within the cluster
				//potentially matching
				for (ClusterNode leaf1 : leafNodes1) {
					for (ClusterNode leaf2 : leafNodes2) {
						leafNodes.get(leaf2.modelID + ":" + leaf2.topicID).clusterID = leaf1.clusterID;
						inOrder(leaf1, leaf2, distanceMatrices);
					}
				}
				
				ClusterNode newRoot = joinNodes(node1Root, node2Root, node1Root.clusterID);
				newRoot.usedModels = new ArrayList<String>();
				newRoot.usedModels.addAll(node1Root.usedModels);
				newRoot.usedModels.addAll(node2Root.usedModels);
				
				newRoot.clusterID = node1Root.clusterID;
				clusterOfTrees.remove(Integer.toString(node1Root.clusterID));
				clusterOfTrees.remove(Integer.toString(node2Root.clusterID));
				
				clusterOfTrees.put(Integer.toString(newRoot.clusterID), newRoot);
			}
		}
		
	}
	
	
	//goes through a cluster and nullifies the rows and columns of its leaves with the new node
	private void inOrder(ClusterNode cluster, ClusterNode node, Map<String,Double[][]> distanceMatrices) {
		if (cluster.modelID != null) {
			
			
			Integer i = Integer.parseInt(cluster.topicID.split("topic", 2)[1]);
			Integer j = Integer.parseInt(node.topicID.split("topic", 2)[1]);
			
			String key = cluster.modelID + ":" + node.modelID;
			
			if(distanceMatrices.get(key) == null) {
				
				key = node.modelID + ":" + cluster.modelID;
				Integer temp = i;
				i = j;
				j = temp;
			}
			
			nullifyMatrixRow(distanceMatrices, i, j, key);
			nullifyMatrixColumn(distanceMatrices, i, j, key);
			return;
		}
		
		inOrder(cluster.left, node, distanceMatrices);
		inOrder(cluster.right, node, distanceMatrices);
	}
	
	//gets the list of leaf nodes
	private void leafNodeGrabber(ClusterNode cluster, ArrayList<ClusterNode> leafNodes) {
		
		if (cluster.modelID != null) {
			leafNodes.add(cluster);
			
			return;
		}
		
		leafNodeGrabber(cluster.left, leafNodes);
		leafNodeGrabber(cluster.right, leafNodes);
	}
	
	//nullifies a row in a distance matrix
	private void nullifyMatrixRow(Map<String,Double[][]> distanceMatrices, int shortestI, int shortestJ, String shortestKey) {
		Double[][] distanceMatrix = distanceMatrices.get(shortestKey);
		for (int j = 0; j < distanceMatrix[shortestI].length; j++) {
			//System.out.println(distanceMatrix[shortestI][j]);
			distanceMatrix[shortestI][j] = -1.0;
		}
		
		
		
		
	}
	
	//nullifies a column in a distance matrix
	private void nullifyMatrixColumn(Map<String,Double[][]> distanceMatrices, int shortestI, int shortestJ, String shortestKey) {
		Double[][] distanceMatrix = distanceMatrices.get(shortestKey);
		for (int i = 0; i < distanceMatrix.length; i++) {
			//System.out.println(distanceMatrix[i][shortestJ]);
			distanceMatrix[i][shortestJ] = -1.0;
		}
	}
	
	//nullifies an intersection between 2 nodes in a distance matrix
	private void singlePointMatrixNullify(ArrayList<ClusterNode> leafNodes1, ArrayList<ClusterNode> leafNodes2, Map<String,Double[][]> distanceMatrices) {
		for (ClusterNode node1 : leafNodes1) {
			for (ClusterNode node2: leafNodes2) {
				if (node1.modelID != node2.modelID) {
					String key = node1.modelID + ":" + node2.modelID;
					Integer i = Integer.parseInt(node1.topicID.split("topic", 2)[1]);
					Integer j = Integer.parseInt(node2.topicID.split("topic", 2)[1]);
				
					if (distanceMatrices.get(key) == null) {
						key = node2.modelID + ":" + node1.modelID;
						Integer temp = i;
						i = j;
						j = temp;
					}
					distanceMatrices.get(key)[i][j] = -1.0;
				}
			}
		}
	}
	
	

	
}
