package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import ui.Builder.MenuItems;

public class Pong extends Simulation {

	
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
	public Simulation cloneSimulation() {
		return new Pong();
	}

	@Override
	public void simulate(Controller[] c) {

		for (int i = 0; i < 10; i++) {
		float ball = MathUtils.random();
		float pad = MathUtils.random();
				double out = c[0].calculate(ball, pad)[0];
				double error = 0;
				if (out > 0.5) {
					if (ball > pad) error = 0;
					else if (ball < pad) error = 10;
				} else {
					if (ball > pad) error = 10;
					else if (ball < pad) error = 0;
				}
				c[0].addFitness(-error);
		}
		
	}
	
	@Override
	public String toString() {
		return "Pong Simulation";
	}

	@Override
	public void menuInit(MenuItems inputF) { }

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void start() { }

}
