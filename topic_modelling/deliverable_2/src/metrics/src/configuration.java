import java.util.HashMap;

public class Configuration {
	HashMap<String, Object> conf;
	   public Configuration(HashMap<String, Object> conf)
	   {
		   this.conf = conf;
	   }
	   
	   public Object get(String configKey)
	   {
		   Object res = conf.get(configKey);
		   if (res == null)
		   {
			   System.err.println("Could not find configuration for " + configKey);
		   }
		   
		   return res;
	   }
}
