package ui.controllers;

import java.io.File;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import simulations.Simulation;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;

public class EATabController extends EATabHolder {
	
	boolean pauseClicked = false;
	boolean startClicked = false;
	
	ElementHolder elementHolder;
	
	
	public EATabController(String tabID, FXController fxController, Tab tab){
		super(tabID, tab, fxController);
		
		original = false;
		simType.getSelectionModel().select(Simulation.getTypeOfSimulations()[0]);
		controllerType.getSelectionModel().select(Controller.getTypeOfControllers()[0]);
		changeSim(simulation);
		changeController(controller);
		original = true;
		
	}
	
	public EATabController(String tabID, FXController fxController, Tab tab, Simulation simulation, Controller controller,  InputFramework evolveFrame, ElementHolder elements, String[] graphData){
		super(tabID, tab, fxController, simulation, controller, evolveFrame, elements, graphData);
		String simClassName = simulation.getClass().getName();
		String simName = simClassName.substring(simClassName.indexOf(".") + 1);
		String controllerClassName = controller.getClass().getName();
		String controllerName = controllerClassName.substring(controllerClassName.indexOf(".") + 1);
		
		if(!Simulation.check(simName)){
			simType.getItems().add(simName);
		}
		
		if(!Controller.check(controllerName)){
			controllerType.getItems().add(controllerName);
		}
		
		original = false;
		simType.getSelectionModel().select(simName);
		controllerType.getSelectionModel().select(controllerName);
		changeSim(simulation);
		changeController(controller);
		original = true;
		
		
	}
	
	
	
	
	
	@Override
	public boolean close(){
		System.out.println("Closing tabID: " + tabID);
		ea.setRunning(false);
		return true;
	}
	
	@Override
	protected void changeController(Controller control){
		String val = (String)controllerType.getValue();
		//controller = (Controller) Controller.getController(val);
		//System.out.println(s.getSettings());
		
		controlLabel.setText(val);
		
		GridPane grid = getNewGrid();
		
        grid.add(controllerType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        
        if(control instanceof HasMenu){
        	((HasMenu)control).getFramework().clear();
        	((HasMenu)control).frameworkInit();
        	System.out.println("Building Controller: " + control);
        	controllerScrollPane.setContent(builder.build(control, grid));
        }else{
        	controllerValid.setSelected(true);
        	status[1] = true;
        	controllerScrollPane.setContent(grid);
        	checkValidity();
        }
        
        controllerPane.requestFocus();
	}
	
	@Override
	protected void changeSim(Simulation sim) {
		String val = (String)simType.getValue();
		//simulation = (Simulation) Simulation.getSimulation(val);
		
		System.out.println("Making Sim: " + sim);
		
		//String val = simulation.getClass().getName().substring(simulation.getClass().getName().indexOf(".")+1);
		simLabel.setText(val);
		simType.getSelectionModel().select(val);
		GridPane grid = getNewGrid();
        grid.add(simType, 1, 0);
        grid.add(new Label("Type:"), 0, 0);
        
        //System.out.println("Has Menu: " + (sim instanceof HasMenu));
        if(sim instanceof HasMenu){
        	((HasMenu)sim).getFramework().clear();
        	((HasMenu)sim).frameworkInit();
//        	System.out.println("Variable: " + ((HasMenu)sim).getFramework().getVariable(0));
        	System.out.println("Building Simulation: " + sim);
        	simScrollPane.setContent(builder.build(sim, grid));
        }else{
        	simulationValid.setSelected(true);
        	status[0] = true;
        	simScrollPane.setContent(grid);
        	checkValidity();
        }
		simPane.requestFocus();

	}
	
	boolean stopped = true;
	
	@Override
	@FXML
	protected void onStartClicked() {
		if (startClicked = !startClicked) {
			stopped = false;
			
			clearButton.setDisable(true);
			graphClean.setDisable(true);
			builder.setChangable(false);
			//System.out.println("STARTING SIMULATION");
			//System.out.println("SIM NUM IN: " + simulation.getNumInputs() + "\tNUM OUT: " + simulation.getNumOutputs());
			controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
			if(simulation instanceof HasMenu)
				((HasMenu)simulation).confirmMenu();
			if(controller instanceof HasMenu)
				((HasMenu)controller).confirmMenu();
			ea.readElementHolder(elementHolder);
			ea.setSimAndController(simulation,controller);
			((HasMenu)ea).confirmMenu();
			ea.setRunning(true);
			(new Thread(ea)).start();
			startButton.setText("Stop");
		} else {
			stopped = true;
			ea.setRunning(false);
			clearButton.setDisable(false);
			graphClean.setDisable(false);
			//builder.setChangable(true);
			elementHolder = ea.getExportedElements();
			startButton.setText("Start");
		}
	}
	
	public ElementHolder getElementHolder(){
		return elementHolder;
	}
	
	
	@Override
	@FXML 
	protected void saveFitnessGraph(){
		saveChartAsPng(fitnessGraph);
	}
	
	@Override
	@FXML
	protected void onClearClicked() {
		InputFramework def = ea.getFramework();
		
		if(!fitnessGrapher.getLoaded()){
			fitnessGrapher.resetGraph();
		}else{
			fitnessGrapher.setLoaded(false);
		}
		builder.setChangable(true);
		ea = new EvolutionAlgorithm();
		ea.frameworkInit();
		ea.getFramework().setDefaults(def);
		ea.setGrapher(fitnessGrapher);
		GridPane grid = getNewGrid();
		evolutionScrollPane.setContent(builder.build(ea, grid));
		clearButton.setDisable(true);
		//startButton.setText("Start");
		//pauseClicked = false;
		//clearButton.setText("Pause");
		
		/*if (pauseClicked == false) {
			pauseClicked = true;
			ea.setRunning(false);
			clearButton.setText("Resume");
		}else{
			pauseClicked = false;
			ea.setRunning(true);
			clearButton.setText("Pause");
			(new Thread(ea)).start();
		}*/
		
	}
	
	
}
