//class to store the name of the original JSON file name and the JSON string attached to it
public class JsonStore {
	private String jsonFileName;
	private String json;
	
	// Getter
	public String getJsonFileName() {
	    return jsonFileName;
	}

	// Setter
	public void setJsonFileName(String newName) {
	   this.jsonFileName = newName;
	}
	
	
	// Getter
	public String getJson() {
	    return json;
	}

	// Setter
	public void setJson(String jsonString) {
	   this.json = jsonString;
	}
}
