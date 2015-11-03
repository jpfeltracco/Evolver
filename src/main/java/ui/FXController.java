package ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import org.neuroph.util.TransferFunctionType;

import controllers.Controller;
import controllers.MLP;
import evolver.EvolutionAlgorithm;
import evolver.EvolutionAlgorithm.Type;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import simulations.Simulation;
import simulations.XOR;

public class FXController implements Initializable {
	boolean startClicked;
	
//	ArrayList<EvolutionAlgorithm> eas = new ArrayList<EvolutionAlgorithm>();
	EvolutionAlgorithm ea;
	Vector<Float> avgFit;
	
	// Yo Keenan! These are populated by FXML voodoo magic from the FXMLLoader in Main!
    @FXML
    private Button startButton;

    @FXML
    private Button renderButton;
    
    @FXML
    private ComboBox<?> simType;

    @FXML
    private Slider mutRateSlider;

    @FXML
    private Slider mutAmtSlider;

    @FXML
    private ComboBox<String> controllerCombo;

    @FXML
    private LineChart<Number, Number> fitnessGraph;
    
    @FXML 
    private void onSimTypeChanged() {
    	System.out.println("sim changed");
    }
    
    @FXML
    private void onStartClicked() {
    	if (startClicked = !startClicked) {
    		if (ea != null) {
    			ea.setRunning(true);
    			new Thread(ea).start();
    		} else {
    			Simulation s = new XOR();
    	        Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.SIN, 4, 4);
    	        ea = new EvolutionAlgorithm(s, c);
    	        s.setEvolutionAlgorithm(ea);
    	        ea.setReproductionType(Type.RANDOM);
    	        ea.setGenerationMultiplier(10);
    	        ea.setMutationAmt(0.13f);
    	        ea.setMutationRate(0.15f);
    	        ea.setFoundersPercent(0.5f);
    	        ea.setGamesPerElement(5);
    	        avgFit = ea.getAvgFit();
    	        new Thread(ea).start();
    		}
    		startButton.setText("Stop");
    		
    		// Do stuff assuming we are running and configured.
    	} else {
    		startButton.setText("Start");
    		ea.setRunning(false);
    	}
    	
    	
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("glass.accessible.force", "false"); // Windows 10 crashes unless this is here :(
		
		
		// Demo of way to populate combo box programmatically
//		ObservableList<String> options = 
//			    FXCollections.observableArrayList(
//			        "MLP 3x3",
//			        "FPGA",
//			        "Norris's brain"
//			    );
//		controllerCombo.setItems(options);
//		
//		// Demo of way to populate graph programmatically
//		 series = new Series<Number, Number>();
//        //populating the series with data
//        series.getData().add(new XYChart.Data<Number, Number>(1, 23));
//        series.getData().add(new XYChart.Data<Number, Number>(2, 14));
//        series.getData().add(new XYChart.Data<Number, Number>(3, 15));
//        series.getData().add(new XYChart.Data<Number, Number>(4, 24));
//        series.getData().add(new XYChart.Data<Number, Number>(5, 34));
//        series.getData().add(new XYChart.Data<Number, Number>(6, 36));
//        series.getData().add(new XYChart.Data<Number, Number>(7, 22));
//        series.getData().add(new XYChart.Data<Number, Number>(8, 45));
//        series.getData().add(new XYChart.Data<Number, Number>(9, 43));
//        series.getData().add(new XYChart.Data<Number, Number>(10, 17));
//        series.getData().add(new XYChart.Data<Number, Number>(11, 29));
//        series.getData().add(new XYChart.Data<Number, Number>(12, 25));
//        
        
	}
	
	

}