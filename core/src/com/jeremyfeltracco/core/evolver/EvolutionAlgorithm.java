package com.jeremyfeltracco.core.evolver;

import org.neuroph.util.random.GaussianRandomizer;

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
		
	}

	private Simulation makeNewSim() throws InstantiationException, IllegalAccessException {
		return sim.newInstance();
	}

	@Override
	public void run() {
		while (true) {
			// Setup simulations
			for (Element e : elements) {
				e = new Element();
				e.config = new double[configSize];
				for (int i = 0; i < configSize; i++)
					e.config[i] = r.getRandomGenerator().nextDouble();
			}
			
			// Run simulations
			// Interpret output
			// Setup next generation
		}
	}

}
