package com.jeremyfeltracco.core.evolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.neuroph.util.random.GaussianRandomizer;

import com.jeremyfeltracco.core.Main;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.simulations.Simulation;

public class EvolutionAlgorithm implements Runnable {

	enum Type {
		HALF, RANDOM
	}

	private final Type t;
	private final int numPerGen;
	private final float mutationAmt;
	private final Class<Simulation> sim;
	private final Class<Controller> controller;
	private final int controlInputs;
	private final int controlOutputs;
	private final int controlPerSim;
	private final int configSize;
	private int availableControllers;
	private GaussianRandomizer r;
	private Element[] elements;
	private final int numThreads;
	private Controller[] controllers;

	public EvolutionAlgorithm(Type t, int numPerGen, float mutationAmt, Class<Simulation> sim, Class<Controller> controller)
			throws InstantiationException, IllegalAccessException {
		this.t = t;
		this.numPerGen = numPerGen;
		this.mutationAmt = mutationAmt;
		this.sim = sim;
		this.controller = controller;
		Simulation s = sim.newInstance();
		Controller c = controller.newInstance();
		controlInputs = s.getNumInputs();
		controlOutputs = s.getNumOutputs();
		controlPerSim = s.getControlPerSim();
		availableControllers = c.getAvailableControllers();
		configSize = c.getConfigSize();
		r = new GaussianRandomizer(0, 1); // Normal curve with mean 0 and std dev of 1
		elements = new Element[numPerGen];
		
		numThreads = availableControllers / controlPerSim; 
		numPerGen = 10 * (numThreads * controlPerSim);
		
		
		controllers = new Controller[availableControllers];
		controllers[0] = null;
		for(int i = 0; i < availableControllers; i++){
			Constructor<Controller> con = null;
			try {
				con = controller.getConstructor(int.class, int.class);
			} catch (NoSuchMethodException e2) {
				e2.printStackTrace();
			} catch (SecurityException e2) {
				e2.printStackTrace();
			}
			try {
				controllers[i] = con.newInstance(controlInputs, controlOutputs);
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	private Simulation makeNewSim() throws InstantiationException, IllegalAccessException {
		return sim.newInstance();
	}

	@Override
	public void run() {
		while (Main.runThreads) {
			// Setup simulations
			for (Element e : elements) {
				e = new Element();
				e.config = new double[configSize];
				for (int i = 0; i < configSize; i++)
					e.config[i] = r.getRandomGenerator().nextDouble();
			}
			
			
			
			
			Simulation[] sims = new Simulation[numThreads];
			for(int i = 0; i < numThreads; i++){
				try {
					sims[i] = sim.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				Controller[] appliedControllers = new Controller[controlPerSim];
				for(int j = 0; j < controlPerSim; j++){
					appliedControllers[j] = controllers[i*controlPerSim + j];
				}
				sims[i].setControllers(appliedControllers);
				
				Element[] appliedElements = generateElements(numPerGen);
				sims[i].setElements(appliedElements);
				
				
				Simulation.simsRunning ++;
			}
			
			//ACTIVATE THREAD (finish this portion
				//For Loop to do so (IE. run thread.start()
			for(Simulation s : sims){
				new Thread(s).start();
			}
			waitForThreads();
			// Interpret output from elements
			// Setup next generation
		}
	}
	
	
	
	private Element[] generateElements(int num) {
		return null;
	}

	private synchronized void waitForThreads(){
		while(Simulation.simsRunning != 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
