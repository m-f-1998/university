package malletWrapper;

import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

// Used for CLI and guava cartesian list generation
import com.google.common.collect.*;
import org.apache.commons.cli.*;

// Used for logging
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


// Mallet classes used
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

public class TopicGenerator {
	private Properties parameters;
	private Properties dParameters;
	final String corpusFilePath;
	final String configFilePath;
	private HashMap<String, Integer> paramIndexs;
	private Boolean maxThreadsFlag = false;
	private int numThreads;
	private CommandLine cmd;

	private static final int DEFAULT_NUM_OF_TOPICS = 20;
	private static final String[] acceptedParams = {"seed_index", "alpha_sum", "symmetric_alpha", "beta", "iterations", "number_of_topics", "burn_in_interval", "optimise_interval"};
	private static final List<String> acceptedParamList = Arrays.asList(acceptedParams);

	private static final Logger logger = LogManager.getLogger(TopicGenerator.class);

	public TopicGenerator(String[] args) {

		cmd = parseArgs(args);
		corpusFilePath = cmd.getOptionValue("corpus");
		configFilePath = cmd.getOptionValue("parameters");
		parameters = new Properties();

		if (cmd.hasOption("parameters")){
			parameters = parseConfigFile(configFilePath);
			validateParams(parameters);
		}
		
		// if default params file provided then overwrite 
		if (cmd.hasOption("defaults")){
			dParameters = parseConfigFile(cmd.getOptionValue("defaults"));
			validateParams(dParameters);
			populateDefaultsFromFile();
		}

		// if max threads option is provided then set flag to true
		if (cmd.hasOption("maxthreads")){
			maxThreadsFlag =  true;
		}

		// if number of threads argument provided then set numThreads = value
		if (cmd.hasOption("numthreads")){
			numThreads = Integer.parseInt(cmd.getOptionValue("numthreads"));
		}

		populateDefaults();
	}

	/**
	 * 
	 * Parses arguments given through CLI. Uses apache.commons.cli.
	 * Returns a CommandLine object which holds all the options read from runtime
	 * 
	 * @param arguments
	 * @return Parsed CommandLine object, PraseException error if options flags are invalid
	 */
	private CommandLine parseArgs(String[] arguments) {
		// Parses arguments given through CLI. Uses apache.commons.cli.
		// Returns a CommandLine object which holds all the options read from runtime
		Options options = new Options();

		Option params = new Option("p", "parameters", true, "parameters.conf file path");
		params.setRequired(true);
		options.addOption(params);
	
		Option corpus = new Option("c", "corpus", true, "corpus file path");
		corpus.setRequired(true);
		options.addOption(corpus);

		Option defaults = new Option("d", "defaults", true, "default params file path");
		defaults.setRequired(false);
		options.addOption(defaults);

		Option maxthreads = new Option("m", "maxthreads", false, "if flag provided, will use max available processors as model 'num of threads' for faster execution");
		maxthreads.setRequired(false);
		options.addOption(maxthreads);

		Option numthreads = new Option("n", "numthreads", true, "number of threads to use per model");
		numthreads.setRequired(false);
		options.addOption(numthreads);

		Option outputPath = new Option("o", "output", true, "path for outputted topic models");
		outputPath.setRequired(false);
		options.addOption(outputPath);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		// Leaves help section unordered so therefore displayed by order added
		formatter.setOptionComparator(null);

		try {
			return parser.parse(options, arguments);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("options", options);

			System.exit(1);
		}

		return null;
	}

	/**
	 * 
	 * Validate the format of the parameters and their type before convertParams(String[] paramValues) is called
	 * 
	 * @param parameters
	 * 
	 */
	private void validateParams(Properties parameters){
		// Check beta and alpha both provided
		Boolean hasBeta = parameters.containsKey("beta");
		Boolean hasAlpha = parameters.containsKey("alpha_sum");
		if ((hasAlpha || hasBeta) && !( hasAlpha && hasBeta)){
			stopError("Both alpha_sum and beta sum should be provided by only one has been provided. Please edit config file.");
		}

		// Check values are valid
		parameters.forEach((key,value) -> {
			if(key.equals("symmetric_alpha") && !(value.equals("true") || value.equals("false"))){
				stopError("Symmetric alpha should be a boolean i.e. 'true' or 'false'. Provided value: " + value );
			} else if ((key.equals("alpha_sum") || key.equals("beta")) && !(String.valueOf(value).matches("(\\d+.\\d)([,-](\\d+.\\d))*"))){
				stopError(key + " should be a double. i.e. 1.0 not 1. Provided value: " + value);
			} else if(!(key.equals("symmetric_alpha") || key.equals("alpha_sum") || key.equals("beta")) && !(String.valueOf(value).matches("(\\d+)([,-](\\d+))*"))){
				stopError(key + " should be an integer. Provided value: " + value);
			}

			if(!(acceptedParamList.contains(key))){
				stopError(key + " is not a recognised parameter. Please use an accepted parameter from this list: " + Arrays.toString(acceptedParams));
			}
				
		});

		
	}

	/**
	 * 
	 * Helper used to stop program and print error message
	 * @param message Error message
	 * 
	 */
	private void stopError(String message){
		logger.error(message);
		System.exit(1);
	}

	/**
	 * 
	 * @param configFileName Configuration file address
	 * @return Parameters in the configuration file
	 * 
	 */
	private Properties parseConfigFile(String configFileName) {
		Properties params = new Properties();
		FileInputStream paramFileStream = null;
		try {
			paramFileStream = new FileInputStream(configFileName);
			try {
				params.load(paramFileStream);
			} catch (IOException e) {
				logger.error("(parseConfigFile): Error when reading from input stream:");
				e.printStackTrace();
			}
			try {
				paramFileStream.close();
			} catch (IOException e) {
				logger.error("(parseConfigFile): Error when closing input stream:");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			logger.error("(parseConfigFile): File not found. Error message:");
			e.printStackTrace();
		}

		return params;
	}

	/**
	 * 
	 * Use a default parameter if none is set in the user supplied configuration file
	 * 
	 */
	private void populateDefaults(){
		if(!(parameters.containsKey("number_of_topics"))){
			logger.error("WARNING: No number of topics provided in param file. Using default: " + DEFAULT_NUM_OF_TOPICS);
			parameters.setProperty("number_of_topics", String.valueOf(DEFAULT_NUM_OF_TOPICS));
		}
	}

	/**
	 * 
	 * Use a default from a file if none is set in the user supplied configuration file
	 * 
	 */
	private void populateDefaultsFromFile() {
		dParameters.forEach((key, value) -> {
			if (!parameters.containsKey(key)) {
				parameters.setProperty(key.toString(), value.toString());
				logger.error("Warning: using default parameter: " + key.toString() + " = " + value.toString());
			}
		});
	}

	/**
	 * 
	 * Helper for converting parameters to Mallet's strict types
	 * 
	 * @param paramValues Parameters directly from configuration file 
	 * @return Parameters list with strict types
	 */
	private List<Object> convertParam(String[] paramValues) {
		List<Object> valueList = new ArrayList<>();
		String intRegex = "-?\\d+";
		if(paramValues.length > 3){
			stopError("Parameter invalid. Using invalid range expression.");
		}else if (paramValues.length == 3) {
			if (paramValues[0].matches(intRegex)) {
				valueList = IntStream.iterate(Integer.parseInt(paramValues[0]),
						i -> i <= Integer.parseInt(paramValues[1]), i -> i + Integer.parseInt(paramValues[2])).boxed()
						.collect(Collectors.toList());
			} else {
				valueList = DoubleStream.iterate(Double.parseDouble(paramValues[0]),
						i -> i <= Double.parseDouble(paramValues[1]), i -> i + Double.parseDouble(paramValues[2]))
						.boxed().collect(Collectors.toList());
			}
		} else if (paramValues.length == 2) {
			if (paramValues[0].matches(intRegex)) {
				valueList = IntStream.rangeClosed(Integer.parseInt(paramValues[0]), Integer.parseInt(paramValues[1]))
						.boxed().collect(Collectors.toList());
			} else {
				valueList = DoubleStream.iterate(Double.parseDouble(paramValues[0]),
						i -> i <= Double.parseDouble(paramValues[1]), i -> i + 1).boxed().collect(Collectors.toList());
			}
		} else {
			// If the parameters are a list of desired values (separated by a comma) then parse to doubles or ints
			if (paramValues[0].contains(",")){
				List<Object> tempValueList = new ArrayList<>();
				if(paramValues[0].contains(".")){
					tempValueList.addAll(Arrays.asList(paramValues[0].split(",")).stream().mapToDouble(Double::parseDouble).boxed().collect(Collectors.toList()));					
				}else{
					tempValueList.addAll(Arrays.asList(paramValues[0].split(",")).stream().mapToInt(Integer::parseInt).boxed().collect(Collectors.toList()));
				}
				for (Object i : tempValueList){
					valueList.add(i);
				}
			}else if (paramValues[0].equals("true") || paramValues[0].equals("false")) {
				valueList.add(Boolean.parseBoolean(paramValues[0]));
			} else if (paramValues[0].matches(intRegex)) {
				valueList.add(Integer.valueOf(paramValues[0]));
			} else {
				valueList.add(Double.valueOf(paramValues[0]));
			}
		}
		return valueList;
	}

	/**
	 * 
	 * Creates an InstanceList to be used by Mallet when generating the topic models
	 * 
	 * @return TopicModel's corpus
	 * 
	 */
	private InstanceList createCorpusInstance() {
		ArrayList<Pipe> pipeList = new ArrayList<>();
		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+")) );
		pipeList.add( new TokenSequence2FeatureSequence() );
		
		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		try {
			File corpus = new File(corpusFilePath);

			// If corpus empty, exit
			if(corpus.length() <= 0){
				stopError("Corpus is empty. Exiting.");
			}

			Reader corpusReader = new InputStreamReader(new FileInputStream(corpus));
			
			
			instances.addThruPipe(new CsvIterator (corpusReader, Pattern.compile("^(\\S*)[\\s]*(\\S*)[\\s]*(.*)"), 3,2,1));

		
		} catch (FileNotFoundException e) {
			logger.error("(createCorpusInstance) Corpus File Not Found: ");
			e.printStackTrace();
			System.exit(1);
		}
		return instances;
	}
	
	/**
	 * 
	 * For each parameter in configuration file, configured passed TopicModel
	 * 
	 * @param tm TopicModel to be configured
	 * @param param Key of parameter to be set
	 * @param variation Parameter variation from configuration file
	 * 
	 */
	private void initialiseHelper(TopicModel tm, String param, List<Object> variation){
		switch(param){
			case "symmetric_alpha":
				tm.setSymmetricAlpha((Boolean) variation.get(paramIndexs.get(param)));
				return;
			case "seed_index":
				tm.setSeed((int) variation.get(paramIndexs.get(param)));
				return;
			case "iterations":
				tm.setIterations((int) variation.get(paramIndexs.get(param)));
				return;
			case "burn_in_interval":
				tm.setBurninPeriod((int) variation.get(paramIndexs.get(param)));
				return;
			case "optimise_interval":
				tm.setOptimiseInterval((int) variation.get(paramIndexs.get(param)));
				return;
			default:
				return;
		}
	}

	/**
	 * 
	 * Create model objects and pass to serialiser
	 * 
	 */
	public void generateModels() {
		ArrayList<List<Object>> paramList = new ArrayList<>();
		List<Set<Object>> convertedSet = new ArrayList<>();

		// For each parameter split the parameter string by "-" and pass to be converted into lists by convertParam
		parameters.forEach((key, value) -> {
			String[] paramValues = parameters.getProperty(key.toString()).split("\\s*-\\s*");
			paramList.add(convertParam(paramValues));
		});

		// Converts paramList into a set
		for (List<Object> subList : paramList) {
			convertedSet.add(new HashSet<>(subList));
		}

		// Creates cartesian list i.e. all possible unique variations of the parameters
		Set<List<Object>> cartesianList = Sets.cartesianProduct(convertedSet);
		InstanceList corpus = createCorpusInstance();

		// Gets indices for parameters Properties object
		paramIndexs = new HashMap<>();
		int index = 0;
		for(Object param : parameters.keySet()){
			paramIndexs.put((String) param, index);
			index++;
		}

		Serialiser serialiser;
		if (cmd.hasOption("output")){
			serialiser = new Serialiser(paramIndexs, parameters, configFilePath, cmd.getOptionValue("output"));
		}else{
			serialiser = new Serialiser(paramIndexs, parameters, configFilePath);
		}
		

		// For each possible combination of parameters, create a  pic model, set parameters and serialise
		for (List<Object> variation : cartesianList) {
			TopicModel tm;

			if(parameters.containsKey("alpha_sum") && parameters.containsKey("beta")){
				tm = new TopicModel((int) variation.get(paramIndexs.get("number_of_topics")),
					(double) variation.get(paramIndexs.get("alpha_sum")),
					(double) variation.get(paramIndexs.get("beta"))
					);
			} else {
				tm = new TopicModel((int) variation.get(paramIndexs.get("number_of_topics")));
			}
			
			// if numthreads has a value then set number threads equal to this
			// Checks if maxThreadsFlag is set to true, and if so then calls setMaxThreads
			if (numThreads > 0 && Boolean.TRUE.equals(maxThreadsFlag)){
				logger.error("Requested max threads and provided value or number of threads. Using number of threads provided and disregarding max threads flag.");
				tm.setThreads(numThreads);
			} else if (numThreads > 0){
				tm.setThreads(numThreads);
			} else if (Boolean.TRUE.equals(maxThreadsFlag)){
				tm.setMaxThreads();
			}

			for (Object param : parameters.keySet()){
				initialiseHelper(tm, (String) param, variation);
			}

			// Set instances after seed is set to avoid seedless initialisation 
			tm.setInstances(corpus);

			// Run iterations
			tm.runModel();
			
			// Serialise single topic model, calling one of two constructors if output dir is specified
			serialiser.serialise(tm, variation);
		 }
	}

	public static void main (String[] args){
		TopicGenerator tg = new TopicGenerator(args);
		tg.generateModels();	
	}
	
}
