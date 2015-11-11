package ui.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import javafx.scene.layout.GridPane;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.Builder.TabMenu;
import ui.graph.DataBridge;

public class VTab {
	
	EvolutionAlgorithm ea;
	Controller controller;
	Simulation simulation;
	boolean active;
	ElementHolder elements;
	final DataBridge grapher;

	public VTab(Simulation simulation, Controller controller, EvolutionAlgorithm ea, MenuItems eaMenuItems, ElementHolder elements, DataBridge grapher) {
		this.controller = controller;
		this.simulation = simulation;
		this.grapher = grapher;
		this.ea = ea;
		
		if(eaMenuItems != null)
			ea.getMenuItems().setDefaults(eaMenuItems);
		
		System.out.println("Initializing EA...");
		ea.menuInit();
		
		ea.setGrapher(grapher);
		if(elements == null)
			grapher.setGeneration(0);
		else{
			ea.readElementHolder(elements);
			grapher.setGeneration(elements.getGen());
		}
		
		//------
	}
	
	public VTab(Simulation simulation, Controller controller, EvolutionAlgorithm ea, MenuItems eaMenuItems, DataBridge grapher) {
		this(simulation, controller, ea, eaMenuItems, null, grapher);
	}
	
	public void updateComponents(Simulation simulation, Controller controller, EvolutionAlgorithm ea){
		this.simulation = simulation;
		this.controller = controller;
		this.ea = ea;
	}
	
	public void start() {
		active = true;
		System.out.println("Starting EA...");
		controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
		
		simulation.menuInit();
		simulation.start();
		controller.menuInit();
		controller.start();
		ea.readElementHolder(elements);
		ea.setSimAndController(simulation,controller);
		ea.start();
		ea.setRunning(true);
		(new Thread(ea)).start();
	}
	
	public void stop() {
		active = false;
		System.out.println("Shutting down EA...");
		ea.setRunning(false);
		System.out.println("Gathering Elements...");
		elements = ea.getExportedElements();
	}
	
	public ElementHolder getElements(){
		return elements;
	}
	
	public boolean getActive(){
		return active;
	}

}
