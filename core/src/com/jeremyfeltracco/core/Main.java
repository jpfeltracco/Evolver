package com.jeremyfeltracco.core;


import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.ApplicationAdapter;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.controllers.MLP;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm.Type;
import com.jeremyfeltracco.core.simulations.Round;
import com.jeremyfeltracco.core.simulations.Simulation;

public class Main extends ApplicationAdapter {

	public static boolean runThreads = true;

	@Override
	public void create () {
		
		Simulation s = new Round();
		Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.TANH, new int[] {3,3});
		
		EvolutionAlgorithm ea = new EvolutionAlgorithm(Type.RANDOM, 10, 5, 0.05f, s, c);
		new Thread(ea).start();
		
		
	}
	



}


