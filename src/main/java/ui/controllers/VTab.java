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
	
	final EvolutionAlgorithm ea;
	final Controller controller;
	final Simulation simulation;
	
	ElementHolder elements;
	final DataBridge grapher;

	public VTab(Simulation simulation, Controller controller, MenuItems eaMenuItems, ElementHolder elements, DataBridge grapher) {
		this.controller = controller;
		this.simulation = simulation;
		this.grapher = grapher;
		
		System.out.println("Creating EA...");
		ea = new EvolutionAlgorithm();
		ea.menuInit();
		ea.getMenuItems().setDefaults(eaMenuItems);
		ea.readElementHolder(elements);
		
		//Set up grapher
		ea.setGrapher(grapher);
		if(elements == null)
			grapher.setGeneration(0);
		else
			grapher.setGeneration(elements.getGen());
		
		//------
		
	}
	
	public void start() {

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
		System.out.println("Shutting down EA...");
		ea.setRunning(false);
		System.out.println("Gathering Elements...");
		elements = ea.getExportedElements();
	}
	
	public ElementHolder getElements(){
		return elements;
	}

}
