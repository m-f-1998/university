import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

// Adapted from code by Cailean Welsh
public class ConfigParser {
	
	public static  Configuration parseConfigFile(String configFileName) {
		Properties parameters = new Properties();
		FileInputStream paramFileStream = null;
		try {
			paramFileStream = new FileInputStream(configFileName);
			try {
				parameters.load(paramFileStream);
			} catch (IOException e) {
				System.out.println("(parseConfigFile): Error when reading from input stream:");
				e.printStackTrace();
			}
			try {
				paramFileStream.close();
			} catch (IOException e) {
				System.out.println("(parseConfigFile): Error when closing input stream:");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.out.println("(parseConfigFile): File not found. Error message:");
			e.printStackTrace();
		}
		
		HashMap<String, Object> paramMap = new HashMap<>();

		// For each parameter split the parameter string by "-" and pass to be converted into lists by convertParam
		parameters.forEach((key, value) -> {
			String paramValues = parameters.getProperty(key.toString());
			paramMap.put((String) key, convertParam(paramValues));
		});
		
		return new Configuration(paramMap);
	}
	
	private static Object convertParam(String paramValue) {
		Object result;
		String intRegex = "-?\\d+";
		String stringRegex = "[A-Za-z][A-Za-z0-9-\\.:\\/ \\_\\\\]*";
		String nullRegex = "";
		String doubleRegex = "[0-9]+\\.[0-9]+";

		if (paramValue.equals("true") || paramValue.equals("false")) {
			result = Boolean.parseBoolean(paramValue);
		} else if (paramValue.matches(intRegex)) {
			result = Integer.valueOf(paramValue);
		} else if (paramValue.matches(stringRegex)) {
			result = String.valueOf(paramValue);
		} else if (paramValue.matches(nullRegex)) {
			result = null;
		}
		else if (paramValue.matches(doubleRegex)){
			result = Double.valueOf(paramValue);
		}
		else {
			result = null;
		}
		
		return result;
	}

}
