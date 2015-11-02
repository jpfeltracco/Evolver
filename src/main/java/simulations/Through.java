package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;

public class Through extends Simulation {
	static int x = 0;
	@Override
	public void simulate(Controller[] c) {
//		if (x++ > 50000000)
//			System.out.println("--");
		
		//for (int i = 0; i < 20; i+= 1) {
			double in = MathUtils.random();//i;
			double out = c[0].calculate(in)[0];
			//System.out.print(i + ", ");
			double error = Math.abs(out - in);
//			if (x++ > 50000000)
//				System.out.println(in + "\t" + out);
			c[0].addFitness(-error);
		//}
		//System.out.println();
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
	
	@Override
	public String toString() {
		return "Through Simulation";
	}

}
