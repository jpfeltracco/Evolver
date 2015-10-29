package com.jeremyfeltracco.core.simulations;

import com.jeremyfeltracco.core.evolver.Element;

public class Round extends Simulation {
	
	@Override
	public void run() {
		float[] d = {0.7f,0.1f,0.22f,0.43f,0.4f,0.51f,0.62f,0.99f};
		for (Element e : elements){
			controllers[0].setConfig(e);
			for (int i = 0; i < d.length; i++) {
				float rand = d[i];//MathUtils.random();
				double out = controllers[0].calculate(rand)[0];
				double error = 0;
				if (rand >= 0.5)
					error = 1 - out;
				else
					error = out;
				
				e.addFitness(-error);
			}
		}
		cleanUp();
	}
	
	@Override
	public int getNumInputs() {
		return 1;
	}

	@Override
	public int getNumOutputs() {
		return 1;
	}

	@Override
	public int getControlPerSim() {
		return 1;
	}

	@Override
	public Simulation clone() {
		Round r = new Round();
		return r;
	}

}
