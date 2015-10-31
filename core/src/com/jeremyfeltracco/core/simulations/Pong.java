package com.jeremyfeltracco.core.simulations;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.evolver.Element;

public class Pong extends Simulation {
	@Override
	public void run() {
		
		int[] vals = {0, 1};
		
		for (Element e : elements){
			controllers[0].setConfig(e);

			for (int i = 0; i < 10; i++) {
			float ball = MathUtils.random();
			float pad = MathUtils.random();
					double out = controllers[0].calculate(ball, pad)[0];
					double error = 0;
					if (out > 0.5) {
						if (ball > pad) error = 0;
						else if (ball < pad) error = 10;
					} else {
						if (ball > pad) error = 10;
						else if (ball < pad) error = 0;
					}
					e.addFitness(-error);
			}
			
		}
	}
	
	@Override
	public int getNumInputs() {
		return 2;
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
		Pong r = new Pong();
		return r;
	}

}
