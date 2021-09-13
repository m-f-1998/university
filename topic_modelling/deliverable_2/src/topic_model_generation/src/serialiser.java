package malletWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class Serialiser {
    static final String DEFAULT_OUTPUT_DIR = "./output/";
    
    String outputDir;
    HashMap<String, Integer> paramIndexes;
    Properties paramRanges;
    String configFilePath;

    public Serialiser(HashMap<String, Integer> paramIndexes, Properties paramRanges, String configFilePath) {
        this.configFilePath = configFilePath;
        this.paramRanges = paramRanges;
        this.paramIndexes = paramIndexes;
        this.outputDir = DEFAULT_OUTPUT_DIR + getOutputDir() + "/";
    }

    public Serialiser(HashMap<String, Integer> paramIndexes, Properties paramRanges, String configFilePath, String outputDir) {
        this.configFilePath = configFilePath;
        this.paramRanges = paramRanges;
        this.paramIndexes = paramIndexes;
        this.outputDir = outputDir + getOutputDir() + "/";
    }

    /**
     * 
     * @return Output directory for File write
     * 
     */
    String getOutputDir() {
        String datetime;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy'T'HH_mm_ss");
        datetime = LocalDateTime.now().format(formatter);

        return datetime;
    }

    /**
     * 
     * @param hashedParams Relabelled parameters used for the individual topic and stores in a HashMap
     * @return ID for each TopicModel
     * 
     */
    String createId(HashMap<String, Object> hashedParams) {
        String concat = "";
        
        for (Entry<String, Object> param: hashedParams.entrySet()){
            concat += param.getKey() + String.valueOf(param.getValue());
        }
        
        return Long.toHexString(concat.hashCode());
    }

    /**
     * 
     * Serialise TopicModels into a predefined JSON format
     * 
     * @param topicModel Generated TopicModels
     * @param parametersList Parameters that were collated from configuration file
     * 
     */
    public void serialise(TopicModel topicModel, List<Object> parametersList){
        Gson gson = new Gson();
        HashMap<String, Object> jsonHashmap = new HashMap<>();

        // Check dir exists and create one if not
        // System.out.println(outputDir);
        Path path = Paths.get(outputDir);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.println("(writeFiles) IO exception:");
            e.printStackTrace();
        }

        // Copies config file from input location to output
        try(FileChannel sourceChannel = new FileInputStream(configFilePath).getChannel(); FileChannel destChannel = new FileOutputStream(outputDir + "configFile.conf").getChannel(); ){
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }catch (IOException e){
            System.out.println("(serialise) IO Exception:");
            e.printStackTrace();
        }
        
        // Gather the words and weights
        HashMap<String, HashMap<String, Integer>> weights = topicModel.wordWeight();

        // Gather metrics
        HashMap<String, Object> metrics = new HashMap<>();
        metrics.put("model_log_likelihood", topicModel.getLL());
        metrics.put("log_likelihood", topicModel.getLLPerNumToken());
        metrics.put("coherence", topicModel.getCoherence());
        
        // Relabels unlabelled parameters used for the individual topic and stores in a HashMap
        HashMap<String, Object> hashedParams = new HashMap<>();
        for (String param : paramIndexes.keySet()){
            hashedParams.put(param, parametersList.get(paramIndexes.get(param)));
        }

        // Get the topic model ID
        String topicModelID = createId(hashedParams);

        // Creates Hashmap to be GSON stringified
        jsonHashmap.put("Topics", weights);
        jsonHashmap.put("Parameters", hashedParams);
        jsonHashmap.put("Metrics", metrics);
        jsonHashmap.put("ID", topicModelID);

        // Stringifies
        String jsonString = gson.toJson(jsonHashmap);

        // Write to json file
        try (FileWriter file = new FileWriter(outputDir + topicModelID + ".json");) {
            file.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
