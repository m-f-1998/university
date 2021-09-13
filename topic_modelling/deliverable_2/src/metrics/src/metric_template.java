public class MetricTemplate extends AbstractMetric {
	final static String CONFIG_ID_PARAMETER = "config_id";
	
	String exampleParameter;

	@Override
	public TopicModel[] calculate(TopicModel[] models, Configuration configuration)  {
	
		// Load parameters from the config file into global variables
		loadParameters(configuration);
		
		// Run the metric on every model
		for (TopicModel model: models)
		{
			model.addMetrics(getMetricName(), "<metric output value(s)>");
		}
		
		// Return the new list of models that now contain this metric's values
		return models;
	}
	
	@Override
	public String getMetricName() {
		/** Return the name of the metric **/
		return null;
	}
	
	public void loadParameters(Configuration config) {
		/** Load the config values for use **/
		
		exampleParameter = loadParameterString(config,CONFIG_ID_PARAMETER,"default_value");
	}
}
