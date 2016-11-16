package fi.iki.nop.novelinsight.predicta;

import java.io.File;

// predicta user interface model that contains data

public class PredictaModel {
	
	private String appName = "Novel Insight Scoring";
	private String appVersion = "0.91b (narya core)";
	
	private boolean isRunning = false;
	
	private String trainingCSVFile = "";
	private String scoredValuesCSVFile = "";
	private String resultsCSVFile = "";
	
	// how scoring is computed: model predicts N(mean, stdev) 
	// values for each case. Scoring value = mean + riskLevel*stdev
	// zero means no risk taking whereas -1.0 means uncertain cases 
	// are dropped (pessimistic model) and +1.0 means optimism where
	// uncertain cases are tried.
	// It is good idea to initially use pessimistic scoring and when
	// results starts declining, try more uncertain cases (new areas/customers)
	// where we want to also gain new information how uncertain cases respond 
	// to marketing
	private double riskLevel = -0.5;
	
	// optimization time in seconds
	private double optimizationTime = 300.0; // default is 5 minutes

	public String getAppName(){
		return appName;
	}
	
	public String getAppVersion(){
		return appVersion;
	}
	
	public boolean getRunning(){
		return isRunning;
	}
	
	public void setRunning(boolean running){
		isRunning = running;
	}
	
	
	public boolean setTrainingFile(String csvFile){
		if(csvFile == null) return false;
		if(new File(csvFile).exists() == false) return false;
		
		trainingCSVFile = csvFile;
		return true;
	}
	
	public String getTrainingFile(){
		return trainingCSVFile;
	}
	
	public boolean setScoredFile(String csvFile){
		if(csvFile == null) return false;
		if(new File(csvFile).exists() == false) return false;
		
		scoredValuesCSVFile = csvFile;
		return true;
	}
	
	public String getScoredFile(){
		return scoredValuesCSVFile;
	}

	
	public boolean setResultsFile(String csvFile){
		if(csvFile == null) return false;
		
		resultsCSVFile = csvFile;
		return true;
	}
	
	public String getResultsFile(){
		return resultsCSVFile;
	}
	
	public boolean setRisk(double risk){
		if(risk <= -3.0 || risk >= 3.0) return false;
		riskLevel = risk;
		return true;
	}
	
	public double getRisk(){
		return riskLevel;
	}

	public boolean setOptimizationTime(double secs){
		if(secs <= 0.0) return false;
		optimizationTime = secs;
		return true;
	}
	
	public double getOptimizationTime(){
		return optimizationTime;
	}
}
