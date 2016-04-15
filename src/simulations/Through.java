package simulations;

import controllers.Controller;
import ui.Builder.MenuItems;
import util.Rand;

public class Through extends Simulation {

	@Override
	public double[] simulate(Controller[] c) {
		double in = Rand.r.nextFloat() * 2 - 1;//i;
		double out = c[0].calculate(in)[0];
		double error = Math.abs(out * .5 - in);
		return new double[] {-error};

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
	public Simulation copy() {
		return new Through();
	}

	@Override
	public String toString() {
		return "Through Simulation";
	}

	@Override
	public void menuInit(MenuItems inputF) {
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public boolean start() {
		return true;
	}

}
