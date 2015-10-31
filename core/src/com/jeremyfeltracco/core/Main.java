package com.jeremyfeltracco.core;


import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.ApplicationAdapter;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.controllers.MLP;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm.Type;
import com.jeremyfeltracco.core.simulations.Simulation;
import com.jeremyfeltracco.core.simulations.*;

public class Main extends ApplicationAdapter {

	public static boolean runThreads = true;

	@Override
	public void create () {
		Simulation s = new Memory();
		Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.TANH, 6, 6);
		EvolutionAlgorithm ea = new EvolutionAlgorithm(Type.RANDOM, 10, .13f, .15f, 0.5f, s, c);

		s.setEvolutionAlgorithm(ea);
		
		System.out.println("Starting Simulation: " + s);
		new Thread(ea).start();
	}
	



}