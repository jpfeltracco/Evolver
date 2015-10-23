package com.jeremyfeltracco.core.simulations;

import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.evolver.Element;

public abstract class Simulation implements Runnable {
	protected Controller[] controllers;
	protected Element[] elements;
	
	public abstract void run();
	
	public abstract int getNumInputs();
	public abstract int getNumOutputs();
	public abstract int getControlPerSim();
	
	public Controller[] getControllers() {
		return controllers;
	}
	
	public void setControllers(Controller[] controllers) {
		this.controllers = controllers;
	}

	public void setElements(Element[] elements) {
		this.elements = elements;
	}
	
	protected void cleanUp() {
		// Notify EvolutionAlgorithm that we have fitness ready
	}
}
