package ui.controllers;

import controllers.Controller;
import evolver.ElementHolder;
import evolver.EvolutionAlgorithm;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import simulations.Simulation;
import ui.Builder.MenuItems;
import ui.graph.DataBridge;

public class VDriver {
	
	DataBridge dataBridge = new DataBridge(this);
	final VTab vTab;
	ElementHolder elements;
	boolean running;
	public VDriver(Simulation simulation, Controller controller, MenuItems eaMenuItems, ElementHolder elements)  {
		System.out.println("Starting VDriver...");
		this.elements = elements;
		running = true;
		System.out.println("Creating VTab...");

		vTab = new VTab(simulation, controller, new EvolutionAlgorithm(), eaMenuItems, elements, dataBridge);
		vTab.start();
	}
	
	public  void check(){
		if(!running)
			return;
		ObservableList<Data<Number,Number>> dat = dataBridge.getSeries(0).getData();
		if(Math.abs((double)dat.get(dat.size()-1).getYValue()) < 0.5){
			System.out.println("VDriver Complete. Shutting down VTab...");
			elements = vTab.getElements();
			System.out.println("Last Fitness: " + dat.get(dat.size()-1).getYValue());
			running = false;
		}
	}
	
	public ElementHolder getOutput(){
		return elements;
	}


}
