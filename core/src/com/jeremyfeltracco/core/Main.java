package com.jeremyfeltracco.core;


import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.ApplicationAdapter;
import com.jeremyfeltracco.core.controllers.Controller;
import com.jeremyfeltracco.core.controllers.MLP;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm.Type;
import com.jeremyfeltracco.core.simulations.Simulation;
import com.jeremyfeltracco.core.simulations.XOR;

public class Main extends ApplicationAdapter {

	public static boolean runThreads = true;

	@Override
	public void create () {
		
		Simulation s = new XOR();
		Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.TANH, 3);
		
		EvolutionAlgorithm ea = new EvolutionAlgorithm(Type.HALF, 10, 0.1f, 0.05f, s, c);
		s.setEvolutionAlgorithm(ea);
		new Thread(ea).start();
		
		
	}
	



}