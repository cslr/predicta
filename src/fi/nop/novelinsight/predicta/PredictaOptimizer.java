package fi.nop.novelinsight.predicta;

public class PredictaOptimizer {
	
	static {
		// System.loadLibrary("predicta.dll");
	}
	
	private boolean isRunning = false;
	private String errorMsg = "";
	
	// starts optimization, returns false if optimization cannot started or if it ended to a failure
	public boolean startOptimization(String trainingFile, String scoringFile, String resultsFile, double risk){
		if(Math.random() < 0.10){
			errorMsg = "Random Error";
			return false;
		}
		if(isRunning){
			errorMsg = "Already Running";
			return false;
		}
		
		isRunning = true;
		return true;
	}
	
	// starts returns true if optimization is running
	public boolean getRunning(){ return isRunning; }
	
	// stops optimization if it is running, return false if there is a failure (optimization was not running or something else)
	public boolean stopOptimization(){
		if(isRunning == true){ isRunning = false; return true; }
		else{
			errorMsg = "Optimization is not running";
			return false;
		}
	}
	
	// gets the latest error if above functions indicated failure
	public String getError(){
		return errorMsg;
	}
	
	// returns current status of optimization process, this can be shown on the status line 
	public String getStatus(){
		return new java.util.Date().toString();
	}
	
}
