package ui.controllers;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.graph.DataBridge;

public class VTab implements Runnable{
	
	final DataBridge grapher;
	boolean active;
	EvolutionAlgorithm ea;
	Controller controller;
	Simulation simulation;
	ElementHolder elements;

	/**
	 * Creates a new VTab with the provided inputs, and loads the elements
	 * from the provided ElementHolder
	 * @param simulation the Simulation to use in this VTab
	 * @param controller the Controller to use in this VTab
	 * @param ea the EvolutionAlgorithm to use in this VTab
	 * @param eaMenuItems the MenuItems to use in this VTab
	 * @param elements the Elements to load from
	 * @param grapher the DataBridge to use in this VTab
	 */
	public VTab(Simulation simulation, Controller controller, EvolutionAlgorithm ea, MenuItems eaMenuItems, ElementHolder elements, DataBridge grapher) {
		this.controller = controller;
		this.simulation = simulation;
		this.grapher = grapher;
		this.ea = ea;
		
		if(eaMenuItems != null){
			ea.getMenuItems().clear();
			ea.menuInit();
			ea.getMenuItems().setDefaults(eaMenuItems);
		}
		
		System.out.println("Initializing EA...");
		ea.menuInit();
		
		ea.setDataBridge(grapher);
		if(elements == null)
			grapher.setGeneration(0);
		else{
			ea.readElementHolder(elements);
			grapher.setGeneration(elements.getGen());
		}
	}
	
	/**
	 * Creates a new VTab with the provided inputs.
	 * @param simulation the Simulation to use in this VTab
	 * @param controller the Controller to use in this VTab
	 * @param ea the EvolutionAlgorithm to use in this VTab
	 * @param eaMenuItems the MenuItems to use in this VTab
	 * @param grapher the DataBridge to use in this VTab
	 */
	public VTab(Simulation simulation, Controller controller, EvolutionAlgorithm ea, MenuItems eaMenuItems, DataBridge grapher) {
		this(simulation, controller, ea, eaMenuItems, null, grapher);
	}
	
	//------Not Sure if this works-------
	/**
	 * Updates the components of this VTab. NOTE: UNTESTED
	 * @param simulation the Simulation to use in this VTab
	 * @param controller the Controller to use in this VTab
	 * @param ea the EvolutionAlgorithm to use in this VTab
	 */
	public void updateComponents(Simulation simulation, Controller controller, EvolutionAlgorithm ea){
		//TODO: Make sure this actually works
		this.simulation = simulation;
		this.controller = controller;
		this.ea = ea;
	}
	
	/**
	 * Starts the VTab. This function automatically makes a new thread that 
	 * initializes everything.
	 */
	public void activate() {
		(new Thread(this)).start();
	}
	
	/**
	 * Do not run this function: call activate() instead. This function is only
	 * public because it is inherited from Runnable.
	 */
	public void run(){
		active = true;
		System.out.println("Starting EA...");
		controller.setInOut(simulation.getNumInputs(), simulation.getNumOutputs());
		
		simulation.menuInit();
		simulation.start();
		controller.menuInit();
		controller.start();
		ea.readElementHolder(elements);
		ea.setSimAndController(simulation,controller);
		ea.setRunning(true);
		
		//Failed to start IE. The stop button was clicked before the Controllers could init.
		if(!ea.start()){
			ea.setRunning(false);
			return;
		}
		
		(new Thread(ea)).start();
	}
	
	/**
	 * Stops this VTab.
	 */
	public void stop() {
		active = false;
		System.out.println("Shutting down EA...");
		ea.setRunning(false);
		System.out.println("Gathering Elements...");
		elements = ea.getExportedElements();
	}
	
	/**
	 * Gets the elements produced by this VTab.
	 * @return the elements
	 */
	public ElementHolder getElements(){
		return elements;
	}
	
	/**
	 * Returns whether or not this VTab is active or not. IE. Has stop() been called yet?
	 * @return
	 */
	public boolean getActive(){
		return active;
	}

}
