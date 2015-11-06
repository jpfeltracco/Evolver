package simulations;

import com.badlogic.gdx.math.MathUtils;

import controllers.Controller;
import ui.Builder.Constraint;
import ui.Builder.HasMenu;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import util.*;

public class Round extends Simulation implements HasMenu {
	int trialCount;
	InputFramework inputF = new InputFramework();
	
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

	
	IntegerHolder numTrials = new IntegerHolder(5);
	@Override
	public void frameworkInit() {
		inputF.addEntry("Trial Count", EntryType.SLIDER, numTrials, new Constraint(1,20) ,true);
	}

	
	@Override
	public Simulation clone() {
		Round r = new Round();
		HasMenu.migrate(inputF,r);
		return r;
	}

	@Override
	public boolean check() {
		return true;
	}

	@Override
	public void confirmMenu() {
		trialCount = numTrials.getValue();
	}

	@Override
	public InputFramework getFramework() {
		return inputF;
	}
	

}
