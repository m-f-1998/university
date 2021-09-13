import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

abstract class AbstractMetric {
	final static String CONF_SEPERATOR = "-";
	final static String MODELS_OUT_DIR_DEFAULT = System.getProperty("user.dir") + "/outputModels/";

	public abstract String getMetricName();
	
	public String loadParameterString(Configuration config, String configID, String defaultValue) {
		/**
		 * loadParameterString(configuration,parameter_ID,default_value)
		 * 
		 * Loads a String parameter from Configuration with the specified config ID.
		 * If the parameter is invalid, returns the default value.
		 **/
		Object configValue = config.get(buildFullConfID(configID));
		if (configValue != null) {
			try {
				return (String) configValue;
			} catch (ClassCastException e) {
				// Parameter is of incorrect type, using default value
				return defaultValue;
			}
		} else {
			// Parameter empty, using default value
			return defaultValue;
		}
	}
	
	public double loadParameterDouble(Configuration config, String configID, Double defaultValue) {
		/**
		 * loadParameterString(configuration,parameter_ID,default_value)
		 * 
		 * Loads a double parameter from Configuration with the specified config ID.
		 * If the parameter is invalid, returns the default value.
		 **/
		Object configValue = config.get(buildFullConfID(configID));
		if (configValue != null) {
			try {
				return (double) configValue;
			} catch (ClassCastException e) {
				// Parameter is of incorrect type, using default value
				return defaultValue;
			}
		} else {
			// Parameter empty, using default value
			return defaultValue;
		}
	}
	
	public int loadParameterInteger(Configuration config, String configID, int defaultValue) {
		/**
		 * loadParameterString(configuration,parameter_ID,default_value)
		 * 
		 * Loads an integer parameter from Configuration with the specified config ID.
		 * If the parameter is invalid, returns the default value.
		 **/
		Object configValue = config.get(buildFullConfID(configID));
		if (configValue != null) {
			try {
				return (int) configValue;
			} catch (ClassCastException e) {
				// Parameter is of incorrect type, using default value
				return defaultValue;
			}
		} else {
			// Parameter empty, using default value
			return defaultValue;
		}
	}
	
	public boolean loadParameterBoolean(Configuration config, String configID, boolean defaultValue) {
		/**
		 * loadParameterString(configuration,parameter_ID,default_value)
		 * 
		 * Loads a boolean parameter from Configuration with the specified config ID.
		 * If the parameter is invalid, returns the default value.
		 **/
		Object configValue = config.get(buildFullConfID(configID));
		if (configValue != null) {
			try {
				return (boolean) configValue;
			} catch (ClassCastException e) {
				// Parameter is of incorrect type, using default value
				return defaultValue;
			}
		} else {
			// Parameter empty, using default value
			return defaultValue;
		}
	}

	public abstract TopicModel[] calculate(TopicModel[] models, Configuration configuration);
	
//	Method to build the config full ID given a the metric details and a config id
	protected String buildFullConfID(String configName)
	{
		return getMetricName() + CONF_SEPERATOR + configName;
	}
	
	protected void writeFile(Map<String,?> value, Configuration configuration)
	{
		GsonBuilder builder = new GsonBuilder(); 
		builder.setPrettyPrinting();
		
		Gson gson = builder.create();
		String jsonString = gson.toJson(value); 

		Object configValue = configuration.get("models_out_directory");
		String modelsOutDir = MODELS_OUT_DIR_DEFAULT;
		
		if (configValue != null) {
			modelsOutDir = (String) configValue;
		}
		
		try {
			Path path = Paths.get(modelsOutDir); 
			Path fileName;
			if (path.endsWith("/")) {
				fileName = Path.of(modelsOutDir + getMetricName() + ".json");
			} else {
				fileName = Path.of(modelsOutDir + "/" + getMetricName() + ".json");
			}
			
			if (!Files.exists(Paths.get(modelsOutDir))) {
				Path folder = Paths.get(modelsOutDir);
				Files.createDirectory(folder);
			}
			
			Files.writeString(fileName, jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}