package simulations;

import controllers.Controller;
import ui.Builder.MenuItems;
import util.BooleanHolder;
import util.DoubleHolder;
import util.IntegerHolder;
import util.StringHolder;

public class XOR extends Simulation{
	BooleanHolder setting1 = new BooleanHolder();
	BooleanHolder setting4 = new BooleanHolder();
	DoubleHolder setting2 = new DoubleHolder();
	IntegerHolder setting5 = new IntegerHolder();
	StringHolder setting3 = new StringHolder();
	MenuItems inputF;
	
	@Override
	public void simulate(Controller[] c) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int rand1 = i * 2 - 1;
				int rand2 = j * 2 - 1;
				double out = c[0].calculate(rand1, rand2)[0];			
				double expected = 0;
				double error;
				if ((i == 1 && j == 1) || (i == 0 && j == 0)) {
					expected = -1;
				} else if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
					expected = 1;
				}
				error = Math.abs(expected - out);
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
	public Simulation copy() {
		return new XOR();
	}
	
	@Override
	public String toString() {
		return "XOR Simulation";
	}


	@Override
	public void menuInit(MenuItems inputF) { }


	@Override
	public boolean check() {
		return true;
	}


	@Override
	public boolean start() {
		return true;
	}

}
