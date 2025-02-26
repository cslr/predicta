package fi.iki.nop.novelinsight.predicta;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.text.Normalizer.Mode;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.swt.graphics.Image;



public class Predicta {
	
	static Predicta window;

	protected Shell shlPredicta;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text statusLine;
	
	// bad coding..
	public PredictaModel model;
	public PredictaOptimizer optimizer;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			window = new Predicta();
			
			Display.setAppName(window.model.getAppName());
			Display.setAppVersion(window.model.getAppVersion());			
			
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Predicta(){
		optimizer = new PredictaOptimizerNative();
		model = new PredictaModel();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPredicta.open();
		shlPredicta.layout();
		while (!shlPredicta.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPredicta = new Shell();
		shlPredicta.setSize(615, 412);
		shlPredicta.setText(model.getAppName());
		shlPredicta.setLayout(new FillLayout(SWT.HORIZONTAL));
		shlPredicta.setImage(new Image(Display.getDefault(), "novel-insight-predicta-icon.ico"));
		
		
		Composite composite = new Composite(shlPredicta, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblTrainingData = new Label(composite, SWT.NONE);
		lblTrainingData.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblTrainingData.setText("Scored data");
		
		text = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setToolTipText("CSV-file with D values per line. Each row's last column is treated as a predicted value/scoring.");
		text.setText(model.getTrainingFile());
		
		Button btnCsvFile = new Button(composite, SWT.NONE);
		btnCsvFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlPredicta, SWT.OPEN);
		        fd.setText("Select CSV-file");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.csv", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        
		        btnCsvFile.setEnabled(false);
		        
		        String selected = fd.open();
		        
		        btnCsvFile.setEnabled(true);
		        
		        if(selected != null){
		        	if(model.setTrainingFile(selected))
		        		text.setText(selected);
		        }
			}
		});
		btnCsvFile.setText("Select CSV file..");
		
		Label lblScoredData = new Label(composite, SWT.NONE);
		lblScoredData.setText("Input data");
		lblScoredData.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		text_1 = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		text_1.setEditable(false);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setToolTipText("CSV text file. Each row must have D-1 values (missing a predicted value/score).");
		text_1.setText(model.getScoredFile());
		
		Button btnSelectCsvFile = new Button(composite, SWT.NONE);
		btnSelectCsvFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlPredicta, SWT.OPEN);
		        fd.setText("Select CSV-file");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.csv", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        
		        btnSelectCsvFile.setEnabled(false);
		        
		        String selected = fd.open();
		        
		        btnSelectCsvFile.setEnabled(true);
		        
		        if(selected != null){
		        	if(model.setScoredFile(selected))
		        		text_1.setText(selected);
		        }
			}
		});
		btnSelectCsvFile.setText("Select CSV file..");
		
		Label lblRiskTaking = new Label(composite, SWT.NONE);
		lblRiskTaking.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblRiskTaking.setText("Risk taking");
		
		Scale scale = new Scale(composite, SWT.NONE);
		scale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double riskValue = 2.0*(scale.getSelection() - 50.0)/((double)(scale.getMaximum() - scale.getMinimum()));
				
				if(model.setRisk(riskValue)){
					String str = Double.toString(riskValue) + " (-1.0 = only certain cases, 0.0 = normal, +1.0 = try uncertain cases)";
					scale.setToolTipText(str);
				}
			}
		});
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		scale.setIncrement(10);
		
		double currentRisk = model.getRisk();
		if(currentRisk <= -1.0) currentRisk = -1.0;
		if(currentRisk >=  1.0) currentRisk =  1.0;
		currentRisk = 0.5*(currentRisk+1.0)*(scale.getMaximum() - scale.getMinimum()) + (double)scale.getMinimum();
		if(currentRisk < scale.getMinimum()) currentRisk = scale.getMinimum();
		if(currentRisk > scale.getMaximum()) currentRisk = scale.getMaximum();
		
		scale.setSelection((int)currentRisk);
		scale.setToolTipText("-1.0 = only certain cases, 0.0 = normal, 1.0 = try uncertain cases");
		new Label(composite, SWT.NONE);
		
		Label lblResults = new Label(composite, SWT.NONE);
		lblResults.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblResults.setText("Results");
		
		text_2 = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.CENTER);
		text_2.setEditable(false);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.setToolTipText("Results are stored to CSV text file. Each row contains a predicted value/score.");
		text_2.setText(model.getResultsFile());
		
		Button btnSelectCsvFile_1 = new Button(composite, SWT.NONE);
		btnSelectCsvFile_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlPredicta, SWT.SAVE);
		        fd.setText("Select Results CSV-file");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.csv", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        
		        btnSelectCsvFile_1.setEnabled(false);
		        
		        String selected = fd.open();
		        
		        btnSelectCsvFile_1.setEnabled(true);
		        
		        if(selected != null){
		        	if(model.setResultsFile(selected))
		        		text_2.setText(selected);
		        }
			}
		});
		btnSelectCsvFile_1.setText("Select CSV file..");
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 3, 1));
		
		Button btnPredict = new Button(composite_1, SWT.NONE);
		btnPredict.setSize(55, 25);
		
		Button btnStopComputation = new Button(composite_1, SWT.NONE);
		
		btnPredict.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(optimizer.startOptimization(model.getTrainingFile(), model.getScoredFile(), model.getResultsFile(), 
						model.getRisk(), model.getOptimizationTime(), model.demoVersion())){
					//btnPredict.setEnabled(false);
					//btnStopComputation.setEnabled(true);
				}
				else{
					// there was an failure
					String msg =  optimizer.getError();
					MessageBox mb = new MessageBox(shlPredicta, SWT.OK);
					mb.setMessage(msg);
					mb.open();
				}
			}
		});
		btnPredict.setText("Calculate scoring");
		
		btnStopComputation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(optimizer.stopOptimization()){
					//btnPredict.setEnabled(true);
					//btnStopComputation.setEnabled(false);
				}
				else{
					// there was an failure
					String msg =  optimizer.getError();
					MessageBox mb = new MessageBox(shlPredicta, SWT.OK);
					mb.setMessage(msg);
					mb.open();
				}
				
			}
		});
		btnStopComputation.setBounds(0, 0, 75, 25);
		btnStopComputation.setText("Stop computation");
		
		statusLine = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		statusLine.setEditable(false);
		GridData text_3_gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		text_3_gd.grabExcessHorizontalSpace = true;
		text_3_gd.horizontalSpan = 3;
		statusLine.setLayoutData(text_3_gd);
		
		Menu menu = new Menu(shlPredicta, SWT.BAR);
		shlPredicta.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				window.model = new PredictaModel();
				text.setText(model.getTrainingFile());
				text_1.setText(model.getScoredFile());
				text_2.setText(model.getResultsFile());
				
				double currentRisk = model.getRisk();
				if(currentRisk <= -1.0) currentRisk = -1.0;
				if(currentRisk >=  1.0) currentRisk =  1.0;
				currentRisk = 0.5*(currentRisk+1.0)*(scale.getMaximum() - scale.getMinimum()) + (double)scale.getMinimum();
				if(currentRisk < scale.getMinimum()) currentRisk = scale.getMinimum();
				if(currentRisk > scale.getMaximum()) currentRisk = scale.getMaximum();
				
				scale.setSelection((int)currentRisk);
				
				statusLine.setText("");
				
				optimizer.stopOptimization();
			}
		});
		mntmNewItem.setText("Reset");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setRunning(false);
				shlPredicta.dispose();
			}
		});
		mntmExit.setText("Exit");
		
		MenuItem mntmQuality = new MenuItem(menu, SWT.CASCADE);
		mntmQuality.setText("Quality");
		mntmQuality.setToolTipText("Set optimization time");
		
		Menu menu_3 = new Menu(mntmQuality);
		mntmQuality.setMenu(menu_3);
		
		MenuItem mntmOneMinute = new MenuItem(menu_3, SWT.RADIO);
		mntmOneMinute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmOneMinute.getSelection())
					model.setOptimizationTime(1*60.0);
			}
		});
		mntmOneMinute.setText("1 minute");
		
		MenuItem mntmMinutes = new MenuItem(menu_3, SWT.RADIO);
		mntmMinutes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmMinutes.getSelection())
					model.setOptimizationTime(5*60.0);
			}
		});
		mntmMinutes.setSelection(true);
		mntmMinutes.setText("5 minutes");
		
		MenuItem mntmMinutes_1 = new MenuItem(menu_3, SWT.RADIO);
		mntmMinutes_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmMinutes_1.getSelection())
					model.setOptimizationTime(10*60.0);
			}
		});
		mntmMinutes_1.setText("10 minutes");
		
		MenuItem mntmMinutes_2 = new MenuItem(menu_3, SWT.RADIO);
		mntmMinutes_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmMinutes_2.getSelection())
					model.setOptimizationTime(30*60.0);
			}
		});
		mntmMinutes_2.setText("30 minutes");
		
		MenuItem mntmHour = new MenuItem(menu_3, SWT.RADIO);
		mntmHour.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmHour.getSelection())
					model.setOptimizationTime(60*60.0);
			}
		});
		mntmHour.setText("1 hour");
		
		MenuItem mntmHours = new MenuItem(menu_3, SWT.RADIO);
		mntmHours.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmHours.getSelection())
					model.setOptimizationTime(6*60*60.0);
			}
		});
		mntmHours.setText("6 hours");
		
		MenuItem mntmHours_1 = new MenuItem(menu_3, SWT.RADIO);
		mntmHours_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mntmHours_1.getSelection())
					model.setOptimizationTime(24*60*60.0);
			}
		});
		mntmHours_1.setText("24 hours");
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_2 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_2);
		
		MenuItem mntmHelp_1 = new MenuItem(menu_2, SWT.NONE);
		mntmHelp_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = System.getProperty("user.dir");
				org.eclipse.swt.program.Program.launch(path + "/help/index.html");
			}
		});
		mntmHelp_1.setText("Help");
		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		MenuItem mntmAboutPredicta = new MenuItem(menu_2, SWT.NONE);
		mntmAboutPredicta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox mb = new MessageBox(shlPredicta, SWT.OK);
				
				String msg = model.getAppName() + " v. " + model.getAppVersion() + 
						"\n (C) Copyright Tomas Ukkonen 2016 <nop@iki.fi>";
				
				if(model.demoVersion())
					msg = msg + "\n\nDemo scores only the first 10 examples.\n" + 
								"Purchased version allows scoring unlimited amount of data.";
				else
					msg = msg + "\n\nCommercial version. Each purchase of this software allows \n"
							  + "installation to a SINGLE computer and use by a SINGLE user.";
							
				mb.setMessage(msg);
				
				mb.open();
				
			}
		});
		mntmAboutPredicta.setText("About..");
		
		///////////////////////////////////////////////////////////////////////////////
		// create polling thread that polls for the current status of optimization
		
		final Display display = Display.getDefault();
		model.setRunning(true);
		
		display.setAppName(model.getAppName());
		display.setAppVersion(model.getAppVersion());

        Thread pollingThread = new Thread(new Runnable() {

            @Override
            public void run() {
            	while(model.getRunning() == true && !shlPredicta.isDisposed()){
            		display.asyncExec(new Runnable() {
                    	@Override
                    	public void run() {
                    		String msg = optimizer.getStatus();
                    		if(msg != null) statusLine.setText(msg);
                    		
                    		if(optimizer.getRunning() == true){
                    			btnPredict.setEnabled(false);
                    			btnStopComputation.setEnabled(true);
                    		}
                    		else{
                    			btnPredict.setEnabled(true);
                    			btnStopComputation.setEnabled(false);
                    		}
                    			
                    	}

            		});
            		
            		try{ Thread.sleep(100); }
            		catch(InterruptedException ie){ }
            	}
            }

        });

        pollingThread.setDaemon(false);
        pollingThread.start();
	}
}
