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
		

		Simulation s = new XOR();
		Controller c = new MLP(s.getNumInputs(), s.getNumOutputs(), TransferFunctionType.SIN, 4, 4);
		EvolutionAlgorithm ea = new EvolutionAlgorithm(s, c);
		s.setEvolutionAlgorithm(ea);
		ea.setReproductionType(Type.RANDOM);
		ea.setGenerationMultiplier(10);
		ea.setMutationAmt(0.13f);
		ea.setMutationRate(0.15f);
		ea.setFoundersPercent(0.5f);
		ea.setGamesPerElement(5);

		
		
		System.out.println("Starting Simulation: " + s);
		new Thread(ea).start();
	}
	



}