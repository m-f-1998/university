
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TopicModel
{ 
   private Map<String, Object> Parameters = new HashMap<String, Object>();
   private Map<String, Object> Metrics = new HashMap<String, Object>();
   private Map<String, Map<String, Integer>> Topics = new HashMap<String, Map<String, Integer>>();
   private String ID = "";
   
   public TopicModel(){}
   
   //---------------------------GETTER METHODS----------------------------------------------
//   TODO: Implement
   public String getID() {
	   return this.ID;
   }
   
   public Map<String, Object> getMetrics() {
	   return this.Metrics;
   }
   
   public Map<String, Object> getParameters() {
	   return this.Parameters;
   }
   
   public Map<String, Map<String, Integer>> getTopics() {
	   return this.Topics;
   }
   
   //Adds a key/value to the metrics map
   public void addMetrics(String key, Object value) {
	   this.Metrics.put(key, value);
   }
   
 //Adds a key/value to the metrics map
   public void addMetrics(Map<String, Object> value) {
	   this.Metrics.putAll(value);
   }
   
   //---------------------------JSON BUILDER----------------------------------------------
   public String buildJSONString()
   {
	   GsonBuilder builder = new GsonBuilder(); 
	   builder.setPrettyPrinting();
	   Gson gson = builder.create();
	   String jsonString = gson.toJson(this); 
	   return jsonString;
   }
}