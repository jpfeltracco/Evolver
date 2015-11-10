package simulations;

import com.badlogic.gdx.math.MathUtils;
import controllers.Controller;
import ui.Builder.MenuItems;

public class Through extends Simulation {
	
	@Override
	public void simulate(Controller[] c) {
		double in = MathUtils.random() * 2 - 1;//i;
		double out = c[0].calculate(in)[0];
		double error = Math.abs(out * .5 - in);
		c[0].addFitness(-error);
			
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
	public Simulation cloneSimulation() {
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
	public void start() {
		
	}

}
