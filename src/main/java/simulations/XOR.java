package simulations;

import controllers.Controller;

public class XOR extends Simulation {
	static int x = 0;
	@Override
	public void simulate(Controller[] c) {
			//for (int i = 0; i < 10; i++) {
			//int rand1 = (int) (2 * MathUtils.random());
			//int rand2 = (int) (2 * MathUtils.random());
//			if (x++ > 10000000)
//				System.out.println("-------");
			
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					int rand1 = i;
					int rand2 = j;
					
					
					double out = c[0].calculate(rand1, rand2)[0];
					
//					if (rand1 == 1 && rand2 == 1) {
//						System.out.println(out);
//					}
					double expected = 0;
					double error;
					if ((i == 1 && j == 1) || (i == 0 && j == 0)) {
						expected = 0;
					} else if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
						expected = 1;
					}
					error = Math.abs(expected - out);
					
//					if (x++ > 10000000) {
//						System.out.println(rand1 + " " + rand2 + "\t" + out);
//					}

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
	
	@Override
	public String toString() {
		return "XOR Simulation";
	}

}
