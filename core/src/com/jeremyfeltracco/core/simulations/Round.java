package com.jeremyfeltracco.core.simulations;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.evolver.Element;

public class Round extends Simulation {
	
	@Override
	public void run() {
		for (Element e : elements){
			controllers[0].setConfig(e);
			for (int i = 0; i < 5; i++) {
				
				float rand = MathUtils.random();
				double out = controllers[0].calculate(rand)[0];
				System.out.println(out);
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

}
