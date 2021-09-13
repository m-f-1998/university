

public class parameters 
{
	public parameters(Integer seed, Integer iterations, Integer num_of_topics, Float alpha, Float beta, Integer burning_interval, Integer optim_interval)
	{
		this.seed = seed;
		this.iterations = iterations;
		this.num_of_topics = num_of_topics;
		this.alpha = alpha;
		this.beta = beta;
		this.burning_interval = burning_interval;
		this.optim_interval = optim_interval;
	}
	
	private Integer seed;
	private Integer iterations;
	private Integer num_of_topics;
	private Float alpha;
	private Float beta;
	private Integer burning_interval;
	private Integer optim_interval;
	
	
	public Integer getSeed() {
		return this.seed;
	}
	
	public void setSeed(Integer seed){
		this.seed = seed;
	}
	
	public Integer getIterations() {
		return this.iterations;
	}
	
	public void setIterations(Integer iterations){
		this.iterations = iterations;
	}
	
	public Integer getNumOfTopics() {
		return this.num_of_topics;
	}
	
	public void setNumOfTopics(Integer num_of_topics){
		this.num_of_topics = num_of_topics;
	}
	
	public Float getAlpha() {
		return this.alpha;
	}
	
	public void setAlpha(Float alpha){
		this.alpha = alpha;
	}
	
	public Float getBeta() {
		return this.beta;
	}
	
	public void setBeta(Float beta){
		this.beta = beta;
	}
	
	public Integer getBurningInterval() {
		return this.burning_interval;
	}
	
	public void setBurningInterval(Integer burning_interval){
		this.burning_interval = burning_interval;
	}
	
	public Integer getOptimInterval() {
		return this.optim_interval;
	}
	
	public void setOptimInterval(Integer optim_interval){
		this.optim_interval = optim_interval;
	}
}
