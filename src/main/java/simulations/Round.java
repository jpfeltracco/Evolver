package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import ui.Builder.Constraint;
import ui.Builder.TabMenu;
import ui.Builder.MenuItems;
import ui.Builder.MenuItems.EntryType;
import util.*;

public class Round extends Simulation {
	int trialCount;
	MenuItems inputF = new MenuItems();
	
	@Override
	public void simulate(Controller[] c) {
//		float[] d = {0.7f,0.1f,0.22f,0.43f,0.4f,0.51f,0.62f,0.99f};
		//System.out.println(trialCount);
		for (int i = 0; i < trialCount; i++) {
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
	public String toString() {
		return "Rounding Simulation";
	}

	
	

	
	@Override
	public Simulation copy() {
		return new Round();
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void start() {
		trialCount = numTrials.getValue();
	}

	IntegerHolder numTrials = new IntegerHolder(5);
	@Override
	public void menuInit(MenuItems inputF) {
		inputF.add("Trial Count", EntryType.SLIDER, numTrials, new Constraint(1,20) ,true);
	}
	

}
