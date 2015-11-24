package ui.controllers;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import goals.Goal;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.graph.DataBridge;

public class VDriver {
	final VTab vTab;
	final Goal goal;
	DataBridge dataBridge = new DataBridge(this);
	ElementHolder elements;

	public VDriver(Simulation simulation, Controller controller, MenuItems eaMenuItems, ElementHolder elements, Goal goal)  {
		System.out.println("Starting VDriver...");
		this.elements = elements;
		this.goal = goal;
		System.out.println("Creating VTab...");
		
		vTab = new VTab(simulation, controller, new EvolutionAlgorithm(), eaMenuItems, elements, dataBridge);
		vTab.activate();
		goal.activate(elements);
	}
	
	public void check(ElementHolder elements){		
		if(goal.check(elements)){
			vTab.stop();
			System.out.println("VDriver Complete. Shutting down VTab...");
			elements = vTab.getElements();
			System.out.println("Memo: " + goal.message());
			//TODO: Handle the result!
		}
	}
	
	public ElementHolder getOutput(){
		return elements;
	}


}
