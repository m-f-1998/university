import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class MetricLoader {
	// The name of the metric method to invoke
	final static String METRIC_INTERFACE_METHOD = "calculate";
	// The path of the configuration file
	final static String CONF_FILE_PATH = System.getProperty("user.dir") + "/metric_configuration.conf";
	
	// Config parameters - IDs
	final static String CONFIG_ID_METRICS_DIR = "metrics_directory";
	final static String CONFIG_ID_MODELS_IN_DIR = "models_in_directory";
	final static String CONFIG_ID_MODELS_OUT_DIR = "models_out_directory";

	
	// Config parameters - default values
	final static File METRICS_DIR_PATH_DEFAULT = new File(System.getProperty("user.dir") + "/metrics");
	final static File MODELS_IN_DIR_DEFAULT = new File(System.getProperty("user.dir") + "/models");
	final static String MODELS_OUT_DIR_DEFAULT = System.getProperty("user.dir") + "/outputModels/";

	public static void main(String[] args) {
		// Set parameter variables to defaults
		File metricsDir = METRICS_DIR_PATH_DEFAULT;
		File modelsInDir = MODELS_IN_DIR_DEFAULT;
		String modelsOutDir = MODELS_OUT_DIR_DEFAULT;
		
		System.out.println("---------LOADING CONFIG---------");

		// Load the config
		Configuration configuration = ConfigParser.parseConfigFile(CONF_FILE_PATH);
		Object configValue;
		String path;

		// Load the config value for metrics directory
		configValue = configuration.get(CONFIG_ID_METRICS_DIR);
		
		
		if (configValue != null) {
			metricsDir = new File(configValue.toString());
		}
		
		// Load the config value for models input directory
		configValue = configuration.get(CONFIG_ID_MODELS_IN_DIR);
		if (configValue != null) {
			modelsInDir = new File(configValue.toString());
		}
		
		// Load the config value for models output directory
		configValue = configuration.get(CONFIG_ID_MODELS_OUT_DIR);
		if (configValue != null) {
			path = configValue.toString();
			
			if (path.charAt(path.length() - 1) != '/') {
				path = path + "/";
			}
			
			modelsOutDir = path;
		}
		
		folderCreator(modelsOutDir);
		
		System.out.println("---------LOADING METRICS--------");
		System.out.println("Loading metrics from: " + metricsDir.getAbsolutePath());
		// Return the full path, URL, of all files in the directory
		URL[] urls = filesToUrls(metricsDir.listFiles());	
		
		// Load metric class files from directory
		List<Class<?>> metricClassList = new ArrayList<>();
		ClassLoader cl = new URLClassLoader(urls);

		for (File f : metricsDir.listFiles()) {
			// Find the name of the current file, having removed the ".class" extension from
			// the end
			if (f.isFile()) {
				String currentMetricName = f.getName().substring(0, f.getName().length() - 6);
				// Add the loaded generic class into an accessible list of all classes
				try {
					metricClassList.add(cl.loadClass(currentMetricName));
				} catch (ClassNotFoundException e) {
					System.err.println("Error loading file: " + f.getName() + ", as a Java class.");
				}
			}
		}
		
		System.out.println("Successfully loaded " + metricClassList.size() + " metrics.");
		System.out.println();

		System.out.println("---------LOADING MODELS---------");
		System.out.println("Loading topic models from: " + modelsInDir.getAbsolutePath());
		// Return list of jsonFiles of all the files in the models directory
		List<JsonStore> jsonList = filesToJSONList(modelsInDir.listFiles(), modelsInDir.getAbsolutePath() + "/", modelsOutDir + "/");
		
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();

		// Deserializer for topic model json strings
		Gson gson = builder.create();

		TopicModel[] topicModels = new TopicModel[jsonList.size()];

		for (int i = 0; i < jsonList.size(); i++) {
			topicModels[i] = gson.fromJson(jsonList.get(i).getJson(), TopicModel.class);
		}

		System.out.println("Successfully loaded " + topicModels.length + " topic models.");
		System.out.println();

		System.out.println("---------RUNNING METRICS--------");

//		Main loop
		try {
			// Iterate through every file in the metrics folder
			for (Class<?> c : metricClassList) {

				// Create an instance of the metric
				// c.getConstructor(parameterTypes)
				Object metricInstance = c.newInstance();

				// Get the methods
				Method getMetricName = c.getMethod("getMetricName");
				Method calculation = c.getMethod(METRIC_INTERFACE_METHOD,
						new Class[] { TopicModel[].class, Configuration.class });

				// Print some info about the metric
				String metricName = (String) getMetricName.invoke(metricInstance);

				System.out.println("Running metric: " + metricName + "...");

				// Execute metric's "calculation" method, and time it's execution
				long metricStartTime = System.nanoTime();
				topicModels = (TopicModel[]) calculation.invoke(metricInstance, (Object) topicModels, configuration);
				long metricEndTime = System.nanoTime();
				long metricDuration = (metricEndTime - metricStartTime) / 1000000;

				System.out.println("Finished metric: " + metricName + " in " + metricDuration + "ms");
				System.out.println();

				// System.out.println(topicModels[0].buildJSONString());
			}

			// Print the result of invoking this method
			// testMethod.invoke(null, new Object[] {});
		} catch (NoSuchMethodException e) {
			System.err.println("The method cannot be found for the specified class");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("The arguments are invalid for the specified method");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("The creation of an instance of the loaded class has failed");
			e.printStackTrace();
		}

		System.out.println("---------CALCULATE STATISTICS----------");
		Statistics statistics = new Statistics();
		
		long statisticsStartTime = System.nanoTime();
		
		for (TopicModel topicModel : topicModels) {
			Map<String, Object> metrics = topicModel.getMetrics();

			HashMap<String, Object> stats = new HashMap<>();
			for (String metricName : metrics.keySet()) {
				
				Object metric = metrics.get(metricName);

				try {
					stats.put(metricName + "_stats", statistics.calculateStatistics((HashMap<String, Double>) metric));
				} catch (ClassCastException e) {}
				
				try {
					stats.put(metricName + "_stats", statistics.calculateStatistics((LinkedTreeMap<String, Double>) metric));
				} catch (ClassCastException e) {}
					
			}
			if (stats.size() != 0) {
				topicModel.addMetrics(stats);
				System.out.println("Calculated " + stats.size() + " statistics for model " + topicModel.getID() + "...");
			}

		}
		long statisticsEndTime = System.nanoTime();
		long statisticsDuration = (statisticsEndTime - statisticsStartTime) / 1000000;

		System.out.println("Finished Statistics in " + statisticsDuration + "ms");
		System.out.println();
		
		
		System.out.println("---------OUTPUT MODELS----------");

//		Recreate list of json models to rewrite to the output folder
		for (int i = 0; i < topicModels.length; i++) {
			String modelJsonString = topicModels[i].buildJSONString();
			JsonStore modelJson = new JsonStore();
			modelJson.setJsonFileName(topicModels[i].getID() + ".json");
			modelJson.setJson(modelJsonString);
			jsonList.set(i, modelJson);
		}

		System.out.println("Writing output models to: " + modelsOutDir);
		// writing jsons to file after processing in metrics
		try {
			writeJSONFiles(jsonList, modelsOutDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("message.");
	}

	private static URL[] filesToUrls(File[] files) {
		/** Convert an array of File objects to an array of URL paths **/
		// Variable to store list of class URLs
		List<URL> urlList = new ArrayList<URL>();

		// Convert every file in the metrics folder into a full filepath and save to the
		// list "urlList"
		for (File file : files) {
			try {
				URL url = file.toURI().toURL();
				System.out.println("Loading metric: " + file.getName());
				urlList.add(url);
			} catch (MalformedURLException e) {
				// URL is invalid
				System.err.println(file.getName() + " is an invalid URL");
				e.printStackTrace();
			}
		}

		return urlList.toArray(new URL[0]);
	}

	// get the json string from a path
	private static String readJSONFile(String path) {
		String json = "";
		try {
			InputStream inStream = new URL(path).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));
			String currentline = "";
			while ((currentline = reader.readLine()) != null) {
				json = json + currentline;
			}
			inStream.close();
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return json;
	}
	
	private static void folderCreator(String path) {
		if (!Files.exists(Paths.get(path))) {
			try {
				Path folder = Paths.get(path);
				Files.createDirectory(folder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static List<JsonStore> filesToJSONList(File[] files, String models_in_dir, String models_out_dir) {
		/**
		 * Convert an array of File objects to an array of JsonStore composed of the
		 * file name and the json itself
		 **/
		// Variable to store the list of topic model jsons coming in from the file system
		List<JsonStore> jsonList = new ArrayList<JsonStore>();

		// Convert every file in the metrics folder into a full filepath.
		// It then gets the filename out of the filepath,
		// accesses the file through the filepath and gets the json.
		// At the end it stores the file name and json in a JsonStore object that is
		// added to the jsonList.
		for (File file : files) {
			try {
				URL url = file.toURI().toURL();
				String jsonsArray[] = url.toString().split(":");
				Path path = Paths.get(jsonsArray[2]);
				
				String extension = "";
				String filename = path.getFileName().toString();
				
				int i = filename.lastIndexOf('.');
				
				if (i > 0) {
				    extension = filename.substring(i+1);
				    
				    if (extension.equals("json")) {
				    	JsonStore storage = new JsonStore();
						storage.setJsonFileName(filename);
						String json = readJSONFile(url.toString());
						
						if (!json.equals("")) {
							storage.setJson(json);
							jsonList.add(storage);
						}
				    } else if(extension.equals("conf")) {
				    	Path src = Paths.get(models_in_dir + filename); 
				    	Path dest = Paths.get(models_out_dir + filename); 
				    	
				    	//if a conf exists, it's deleted and a new version is updated with the new models
				    	Files.deleteIfExists(dest);
				    	Files.copy(src, dest);
				    } else {
				    	System.err.println(file.getName() + " uses an invalid extension!");
				    }
				}
				
				// System.out.println("Json Strings " + json);
			} catch (MalformedURLException e) {
				// URL is invalid
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return jsonList;

	}

	// writes the topic models to files each with the same name as before the
	// processing through metrics
	private static void writeJSONFiles(List<JsonStore> ListOfJson, String outputPath) throws IOException {
		if (!Files.exists(Paths.get(outputPath))) {
			Path path = Paths.get(outputPath);
			Files.createDirectory(path);
		}
		for (JsonStore item : ListOfJson) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + item.getJsonFileName()));
			writer.write(item.getJson());
			writer.close();
		}

	}
}
