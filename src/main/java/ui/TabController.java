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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import simulations.Simulation;
import simulations.XOR;

public class TabController implements Initializable{
	boolean startClicked;
	EvolutionAlgorithm ea;
	Vector<Float> avgFit;
	
	
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
		// addEA();
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
		// TODO Auto-generated method stub
		
	}

}
