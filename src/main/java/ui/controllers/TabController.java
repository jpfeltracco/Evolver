package ui.controllers;

import java.io.IOException;
import java.util.Vector;

import evolver.EvolutionAlgorithm;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import simulations.Simulation;
import ui.Builder.SimBuilder;

public class TabController {
	boolean startClicked;
	EvolutionAlgorithm ea;
	Vector<Float> avgFit;
	final String tabID;
	final FXController fxController;
	final Tab tab;
	FXMLLoader fxmlLoader = new FXMLLoader();
	SimBuilder sb = new SimBuilder(this);
	
	
	public TabController(String tabID, FXController fxController, Tab tab){
		this.tabID = tabID;
		this.fxController = fxController;
		this.tab = tab;
		
		tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				if(close()){
					fxController.EATabsArray.set(fxController.EATabsArray.indexOf(t.getSource()), null);
					fxController.tabControllers.remove(this);
				}else
					t.consume();

			}
		});
		
		try {
			fxmlLoader.setController(this);
			Parent root = (Parent) fxmlLoader.load(this.getClass().getResource("eaTab.fxml").openStream());
			tab.setContent(root);
			fxController.tabControllers.add(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		simType.getItems().addAll(Simulation.getTypeOfSimulations());
		
		startButton.setDisable(true);
		
		System.out.println("Created a new Tab Controller: " + tabID);
		System.out.println("\tFXController: " + fxController);
	}
	
	public String getTabID(){
		return tabID;
	}
	
	public boolean close(){
		System.out.println("Closing tabID: " + tabID);
		return true;
	}
	
	//--------FXML--------//
	
	@FXML
	private Button startButton;
	
	@FXML
	private Button renderButton;

	@FXML
	private ComboBox<String> simType;

	@FXML
	private ComboBox<String> controllerCombo;

	@FXML
	private LineChart<Number, Number> fitnessGraph;
	
	@FXML
	private TitledPane simPane;
	
	@FXML
	private StackPane SimComboBox;
	
	@FXML
	private void onSimTypeChanged(Event t) {
		String val = (String)((ComboBox)t.getSource()).getValue();
		System.out.println("Sim changed: " + val);
		Simulation s = (Simulation) Simulation.getSimulation(val);
		//System.out.println(s.getSettings());
		
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(2);
		
		ColumnConstraints c = new ColumnConstraints();
		c.setHgrow(Priority.SOMETIMES);
		c.setMaxWidth(173.0);
		c.setMinWidth(10.0);
		c.setPrefWidth(124.0);
		grid.getColumnConstraints().add(c);
		
		c = new ColumnConstraints();
		c.setHalignment(HPos.RIGHT);
		c.setHgrow(Priority.NEVER);
		grid.getColumnConstraints().add(c);
		
		RowConstraints r = new RowConstraints();
		r.setVgrow(Priority.NEVER);
		grid.getRowConstraints().add(r);
		
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        
		simPane.setContent(sb.build(s, grid, r));
		simPane.requestFocus();
	}
	
	public void simulationEnable(boolean val){
		startButton.setDisable(!val);
	}
	
	@FXML
	private void onStartClicked() {
		// addEA();
		/*if (startClicked = !startClicked) {
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
		}*/
		System.out.println(tabID + "\t" + this + "\t" + fxController);
		fxController.EATabs.getSelectionModel().selectLast();
	}
}
