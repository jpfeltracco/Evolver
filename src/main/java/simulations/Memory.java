package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;

public class Memory extends Simulation {
	static int x = 0;
	@Override
	public void simulate(Controller[] c) {
		float testerVal = MathUtils.random(); // some new original value
		float mem = MathUtils.random(); // some random old value
		
		// First input is next val, second is mem val
		double[] outputs = c[0].calculate(testerVal, mem); // only use this to get a new mem
		
		float rand = MathUtils.random(); // New val to put in
		double[] newOutputs = c[0].calculate(rand, outputs[1]); // put in new val and mem from prev
		
		double error = Math.abs(testerVal - newOutputs[0]);
		
		if (verbose) {
			System.out.println(testerVal + "\t" + newOutputs[0]);
		} else {
			c[0].addFitness(-error);
		}
	}
	
	@Override
	public int getNumInputs() {
		return 2;
	}

	@Override
	public int getNumOutputs() {
		return 2;
	}

	@Override
	public int getControlPerSim() {
		return 1;
	}

	@Override
	public Simulation clone() {
		Memory r = new Memory();
		return r;
	}

	@Override
	public String toString() {
		return "Memory thing.";
	}

}
