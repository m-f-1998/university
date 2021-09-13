import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.internal.LinkedTreeMap;

public class Statistics {	
	public HashMap<String, Double> calculateStatistics(double[] inputArray){
		return getStatisticsObject(inputArray);
	}
	
	public HashMap<String, Double> calculateStatistics(HashMap<String, Double> inputMap){
//		Make a double list out of the values from the map
		Iterator<Double> objArray = inputMap.values().iterator();
		double[] inputArray = new double[inputMap.size()];
		
		int counter = 0;
		while (objArray.hasNext())
		{
			inputArray[counter++] = objArray.next().doubleValue();
		}
		
//		Get the statistics map
		return getStatisticsObject(inputArray);
	}
	
	public HashMap<String, Double> calculateStatistics(ArrayList<Double> inputArrayList) {
		double[] inputArray = new double[inputArrayList.size()];
		for (int i=0; i<inputArrayList.size(); i++)
		{
			inputArray[i] = inputArrayList.get(i);
		}
		
		return getStatisticsObject(inputArray);
	}
	
	public Object calculateStatistics(LinkedTreeMap<String, Double> inputMap) {
//		Make a double list out of the values from the map
		Iterator<Double> objArray = inputMap.values().iterator();
		double[] inputArray = new double[inputMap.size()];
		
		int counter = 0;
		while (objArray.hasNext())
		{
			inputArray[counter++] = objArray.next().doubleValue();
		}
		
//		Get the statistics map
		return getStatisticsObject(inputArray);
	}
	
	private HashMap<String, Double> getStatisticsObject(double[] inputArray){
		
		HashMap<String, Double> statsObject = new HashMap<>();
		
		statsObject.put("mean", calculateMean(inputArray));
		statsObject.put("median", calculateMedian(inputArray));
		statsObject.put("min", calculateMin(inputArray));
		statsObject.put("max", calculateMax(inputArray));
		statsObject.put("range", calculateRange(inputArray));
		statsObject.put("standard_deviation", calculateStandardDeviation(inputArray));
		statsObject.put("variance", calculateVariance(inputArray));
		
		return statsObject;
		
	}
	
	private double calculateMean(double[] inputArray)
	{
		double mean;
		
		if (inputArray.length == 0)
		{
			mean = -1;
		}
		else
		{
			double total = 0;
			for (double v: inputArray)
			{
				total += v;
			}
			
			mean = total / inputArray.length;
		}
		
		return mean;
	}
	
	private double calculateMedian(double[] inputArray)
	{
//		TODO: Test that this doesn't reorder things it shouldn't
		Arrays.sort(inputArray);
		
		double median;
		
		if (inputArray.length == 0) {
			median = -1;
		}
//		Take the mean of the middle two values if list length is even
		else if (inputArray.length % 2 == 0)
		{
			int bottomIndex = (inputArray.length / 2) - 1;
			int topIndex = bottomIndex + 1;
			
			double medianBottom = inputArray[bottomIndex];
			double medianTop = inputArray[topIndex];
			
			median = (medianTop + medianBottom) / 2;
		}
//		Take the mean of the middle value
		else
		{
			int index = (int) Math.ceil(inputArray.length / (double) 2) - 1;
			median = inputArray[index];
			
		}
		
		return median;
	}
	
	private double calculateMin(double[] inputArray)
	{
//		TODO: Test
		double min = Double.MAX_EXPONENT;
		for (double d: inputArray)
		{
			if (d < min)
			{
				min = d;
			}
		}
		return min;
	}
	
	private double calculateMax(double[] inputArray)
	{
//		TODO: Test
		double max = Double.MIN_EXPONENT;
		for (double d: inputArray)
		{
			if (d > max)
			{
				max = d;
			}
		}
		return max;
	}
	
	private double calculateRange(double[] inputArray)
	{
		return calculateMax(inputArray) - calculateMin(inputArray);
	}
	
	private double calculateVariance(double[] inputArray)
	{
		double variance;
		
		if (inputArray.length == 0)
		{
			variance = -1;
		}
		else if (inputArray.length > 1)
		{
			double mean = calculateMean(inputArray);
			double square_diff = 0;
			
			for (double i: inputArray)
			{
				square_diff += Math.pow(i - mean, 2);
			}
			
			variance = square_diff / (inputArray.length - 1);
			
		}
		else
		{
			variance = 0;
		}
		
		return variance;
	}

	private double calculateStandardDeviation(double[] inputArray)
	{
		double std;
		if (inputArray.length == 0)
		{
			std = -1;
		}
		else
		{
			double variance = calculateVariance(inputArray);
			std = Math.sqrt(variance);
		}
		
		
		return std;
	}


}
