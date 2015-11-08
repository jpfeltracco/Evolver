package ui.controllers;

import java.io.IOException;
import java.util.Vector;

import controllers.Controller;
import evolver.EvolutionAlgorithm;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import ui.Builder.Builder;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;

public class EaTabController {
	boolean startClicked;
	boolean pauseClicked;
	boolean status[] = new boolean[3];
	EvolutionAlgorithm ea = new EvolutionAlgorithm();
	Simulation simulation;
	Controller controller;
	Vector<Float> avgFit;
	final String tabID;
	final FXController fxController;
	final Tab tab;
	FXMLLoader fxmlLoader = new FXMLLoader();
	Builder builder = new Builder(this);
	
	
	public EaTabController(String tabID, FXController fxController, Tab tab){
		this.tabID = tabID;
		this.fxController = fxController;
		this.tab = tab;
		
		//((HasMenu)ea).clearFramework();
		ea.frameworkInit();
		
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
		controllerType.getItems().addAll(Controller.getTypeOfControllers());
		
		startButtonEnable(false);
		pauseButton.setDisable(true);
		
		System.out.println("Created a new Tab Controller: " + tabID);
		System.out.println("\tFXController: " + fxController);
		
		
		GridPane grid = getNewGrid();
		
		evolutionPane.setContent(builder.build(ea, grid));
		simType.getSelectionModel().select(Simulation.getTypeOfSimulations()[0]);
		controllerType.getSelectionModel().select(Controller.getTypeOfControllers()[0]);
		
		builder.addNonChangable(simType);
		builder.addNonChangable(controllerType);
		
		accordion.setExpandedPane(evolutionPane);
		simPane.requestFocus();
	}
	
	public String getTabID(){
		return tabID;
	}
	
	public boolean close(){
		System.out.println("Closing tabID: " + tabID);
		ea.setRunning(false);
		return true;
	}
	
	//--------FXML--------//
	
	@FXML
	private Button startButton;
	
	@FXML
	private Button pauseButton;
	
	@FXML
	private Button renderButton;

	@FXML
	private ComboBox<String> simType;
	
	@FXML
	private ComboBox<String> controllerType;

	@FXML
	private LineChart<Number, Number> fitnessGraph;
	
	@FXML
	private TitledPane simPane;
	
	@FXML
	private TitledPane controllerPane;
	
	@FXML
	private TitledPane evolutionPane;
	
	@FXML
	public CheckBox simulationValid;
	
	@FXML
	public CheckBox controllerValid;
	
	@FXML
	public CheckBox evolutionValid;
	
	@FXML
	public Label simLabel;
	
	@FXML
	public Label controlLabel;
	
	@FXML
	public Accordion accordion;
	
	
	
	@FXML
	private StackPane SimComboBox;
	
	@FXML
	private void onControllerTypeChanged(Event t){
		String val = (String)((ComboBox)t.getSource()).getValue();
		controller = (Controller) Controller.getController(val);
		//System.out.println(s.getSettings());
		
		controlLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(controllerType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        
        if(controller instanceof HasMenu){
        	//((HasMenu)controller).clearFramework();
        	((HasMenu)controller).frameworkInit();
        	controllerPane.setContent(builder.build(controller, grid));
        }else{
        	controllerValid.setSelected(true);
        	status[1] = true;
        	controllerPane.setContent(grid);
        	checkValidity();
        }
        
        controllerPane.requestFocus();
	}
	
	@FXML
	private void onSimTypeChanged(Event t) {
		String val = (String)((ComboBox)t.getSource()).getValue();
		simulation = (Simulation) Simulation.getSimulation(val);
		
		simLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        if(simulation instanceof HasMenu){
        	//((HasMenu)simulation).clearFramework();
        	((HasMenu)simulation).frameworkInit();
        	simPane.setContent(builder.build(simulation, grid));
        }else{
        	simulationValid.setSelected(true);
        	status[0] = true;
        	simPane.setContent(grid);
        	checkValidity();
        }
		simPane.requestFocus();
	}
	
	public void startButtonEnable(boolean val){
		startButton.setDisable(!val);
	}
	
	@FXML
	private void onStartClicked() {
		
		// addEA();
		if (startClicked = !startClicked) {
			ea.setRunning(true);
			builder.setChangable(false);
			System.out.println("STARTING SIMULATION");
			System.out.println("SIM NUM IN: " + simulation.getNumInputs() + "\tNUM OUT: " + simulation.getNumOutputs());
			controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
			if(simulation instanceof HasMenu)
				((HasMenu)simulation).confirmMenu();
			if(controller instanceof HasMenu)
				((HasMenu)controller).confirmMenu();
			ea.setSimAndController(simulation,controller);
			((HasMenu)ea).confirmMenu();
			(new Thread(ea)).start();
			pauseButton.setDisable(false);
			startButton.setText("Stop");
		} else {
			ea.setRunning(false);
			builder.setChangable(true);
			InputFramework def = ea.getFramework();
			ea = new EvolutionAlgorithm();
			ea.frameworkInit();
			ea.getFramework().setDefaults(def);
			GridPane grid = getNewGrid();
			evolutionPane.setContent(builder.build(ea, grid));
			pauseButton.setDisable(true);
			startButton.setText("Start");
			pause = false;
			pauseButton.setText("Pause");
		}
	}
	
	boolean pause = false;
	@FXML
	private void onPauseClicked() {
		if (pause == false) {
			pause = true;
			ea.setRunning(false);
			
			pauseButton.setText("Resume");
		}else{
			pause = false;
			ea.setRunning(true);
			pauseButton.setText("Pause");
			(new Thread(ea)).start();
			
		}
	}
	
	
	public void setValidity(boolean val, Object section){
		
		if(section instanceof Simulation){
			simulationValid.setSelected(val);
			status[0] = val;
		}else if(section instanceof Controller){
			controllerValid.setSelected(val);
			status[1] = val;
		}else if(section instanceof EvolutionAlgorithm){
			evolutionValid.setSelected(val);
			status[2] = val;
		}
		
		
		checkValidity();
	}
	
	public void checkValidity(){
		boolean ready = true;
		for(boolean b : status){
			if(!b){
				ready = false;
				break;
			}
		}
		
		
		//System.out.println("READY: " + ready);
		startButtonEnable(ready);
	}
	
	private GridPane getNewGrid(){
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
		
		return grid;
	}

}
