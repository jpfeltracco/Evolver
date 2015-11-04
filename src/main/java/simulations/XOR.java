package simulations;

import controllers.Controller;
import ui.Builder.Constraint;
import ui.Builder.InputFramework;
import ui.Builder.InputFramework.EntryType;
import util.BooleanHolder;
import util.DoubleHolder;
import util.IntegerHolder;
import util.StringHolder;

public class XOR extends Simulation {
	BooleanHolder setting1 = new BooleanHolder(false);
	BooleanHolder setting4 = new BooleanHolder(false);
	DoubleHolder setting2 = new DoubleHolder(0);
	IntegerHolder setting5 = new IntegerHolder(0);
	StringHolder setting3 = new StringHolder();
	
	
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
		
	public void check(){
		System.out.println("\n1: " + setting1);
		System.out.println("2: " + setting2);
		System.out.println("3: " + setting3);
		System.out.println("4: " + setting4);
		System.out.println("5: " + setting5);
	}
	
	public InputFramework getFramework(){
		InputFramework inputF = new InputFramework();
		inputF.addEntry("Check", EntryType.CHECKBOX, setting1);
		inputF.addEntry("Setting2", EntryType.SLIDER, setting2, new Constraint(0,8.5,3));
		inputF.addEntry("Setting3", EntryType.TEXT, setting3);
		inputF.addEntry("Check 2", EntryType.CHECKBOX, setting4);
		inputF.addEntry("Int", EntryType.SLIDER, setting5, new Constraint(3,17));
		inputF.addEntry("Info", EntryType.LABEL, "This is some system info! Way cool! Lol!");
		return inputF;
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
