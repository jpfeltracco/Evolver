package com.jeremyfeltracco.core;


import com.badlogic.gdx.ApplicationAdapter;
import com.jeremyfeltracco.core.controllers.MLP;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm;
import com.jeremyfeltracco.core.evolver.EvolutionAlgorithm.Type;
import com.jeremyfeltracco.core.simulations.Round;

public class Main extends ApplicationAdapter {

	public static boolean runThreads = true;

	@Override
	public void create () {
		try {
			EvolutionAlgorithm ea 
				= new EvolutionAlgorithm(Type.RANDOM, 10, 5, 0.05f, Round.class, MLP.class);
			new Thread(ea).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	



}


