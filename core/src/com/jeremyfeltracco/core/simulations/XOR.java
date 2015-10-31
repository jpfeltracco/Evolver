package com.jeremyfeltracco.core.simulations;

import com.badlogic.gdx.math.MathUtils;
import com.jeremyfeltracco.core.evolver.Element;

public class XOR extends Simulation {
	@Override
	public void run() {
		
		int[] vals = {0, 1};
		
		for (Element e : elements){
			controllers[0].setConfig(e);

//			for (int i = 0; i < 10; i++) {
//			int rand1 = (int) (2 * MathUtils.random());
//			int rand2 = (int) (2 * MathUtils.random());
			
//			System.out.println(rand2);
			
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
//					double out = controllers[0].calculate(rand1, rand2)[0];
					double out = controllers[0].calculate(i, j)[0];

					if (i == 1 && j == 1) {
//						System.out.println(out);
					}
					double error;
					if ((i == 1 && j == 1) || (i == 0 && j == 0)) {
						error = out;
					} else {
						error = 1 - out;
					}
					
					e.addFitness(-error);
				}
			}
		}
//			}
//			}
			
//		}
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
