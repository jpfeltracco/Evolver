package com.jeremyfeltracco.core.simulations;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.controllers.Controller;

public class XOR extends Simulation {
	@Override
	public void simulate(Controller[] c) {
		
		int[] vals = {0, 1};
		
			//for (int i = 0; i < 10; i++) {
			//int rand1 = (int) (2 * MathUtils.random());
			//int rand2 = (int) (2 * MathUtils.random());
			
//			System.out.println(rand2);
			
			for (int i = 0; i < vals.length; i++) {
				for (int j = 0; j < vals.length; j++) {
					int rand1 = i;
					int rand2 = j;
					
					double out = c[0].calculate(rand1, rand2)[0];
					double error;
					if ((rand1 == 1 && rand2 == 1) || (rand1 == 0 && rand2 == 0)) {
						error = out;
					} else {
						error = 1 - out;
					}
					
					c[0].addFitness(-error);
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
		XOR r = new XOR();
		return r;
	}

}
