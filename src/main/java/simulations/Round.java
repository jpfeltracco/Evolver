package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;

public class Round extends Simulation {
	@Override
	public void simulate(Controller[] c) {
//		float[] d = {0.7f,0.1f,0.22f,0.43f,0.4f,0.51f,0.62f,0.99f};
		for (int i = 0; i < 5; i++) {
			float rand = MathUtils.random();
			double out = c[0].calculate(rand)[0];
			//System.out.println(out);
			double error = 0;
			if (rand >= 0.5)
				error = 1 - out;
			else
				error = out;
			
			c[0].addFitness(-error*10);
		}
		
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

	@Override
	public String toString() {
		return "Rounding Simulation";
	}

}