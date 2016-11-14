package fi.iki.nop.novelinsight.predicta;

public class PredictaOptimizerNative extends PredictaOptimizer {
	static {
		System.loadLibrary("predicta.dll");
	}
	
	// starts optimization, returns false if optimization cannot started or if it ended to a failure
	native public boolean startOptimization(String trainingFile, String scoringFile, String resultsFile, double risk);
	
	// starts returns true if optimization is running
	native public boolean getRunning();
	
	// stops optimization if it is running, return false if there is a failure (optimization was not running or something else)
	native public boolean stopOptimization();
	
	// gets the latest error if above functions indicated failure
	native public String getError();
	
	// returns current status of optimization process, this can be shown on the status line 
	native public String getStatus();
	
}
