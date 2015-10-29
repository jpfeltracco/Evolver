package com.jeremyfeltracco.core.simulations;

import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.evolver.Element;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm;

public abstract class Simulation implements Runnable {
	
	
	protected EvolutionAlgorithm ea;
	protected Controller[] controllers;
	protected Element[] elements;
	public static int simsRunning = 0;
	
	public abstract void run();
	
	public abstract int getNumInputs();
	public abstract int getNumOutputs();
	public abstract int getControlPerSim();
	public abstract Simulation clone();
	
	public Controller[] getControllers() {
		return controllers;
	}
	
	public void setControllers(Controller[] controllers) {
		this.controllers = controllers;
	}

	public void setElements(Element[] elements) {
		this.elements = elements;
	}
	
	public void setEvolutionAlgorithm(EvolutionAlgorithm ea) {
		this.ea = ea;
	}
	
	protected synchronized void cleanUp() {
		// Notify EvolutionAlgorithm that we have fitness ready
		Simulation.simsRunning--;
		synchronized(ea) {
			ea.notify();
		}
	}
}
