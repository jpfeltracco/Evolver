package com.jeremyfeltracco.core.simulations;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.controllers.Controller;

public class Through extends Simulation {
	static int x = 0;
	@Override
	public void simulate(Controller[] c) {
		
//		for (int i = 0; i < 50; i++) {
			double in = 1;
			double out = c[0].calculate(in)[0];
			
			double error = Math.abs(out - in);
			if (x++ > 1000000)
			System.out.println(out);
						
			c[0].addFitness(-out);
//		}
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
		Through r = new Through();
		return r;
	}

}
