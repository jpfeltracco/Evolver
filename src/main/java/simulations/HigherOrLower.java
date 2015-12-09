package simulations;

import java.util.Random;

import controllers.Controller;
import ui.Builder.Constraint;
import ui.Builder.MenuItems;
import ui.Builder.MenuItems.EntryType;
import util.IntegerHolder;

public class HigherOrLower extends Simulation {
	static Random randomGenerator = new Random();


	
	@Override
	public double[] simulate(Controller[] c) {

		int trialCount = 0;
		int rightCount = 0;
		//double scale = 2/((double)upperBound.getInt() - (double)lowerBound.getInt());
		for(int i = 0; i < trials.getInt(); i ++){
			int a = randomGenerator.nextInt(upperBound.getInt()-lowerBound.getInt()) + lowerBound.getInt();
			int b = randomGenerator.nextInt(upperBound.getInt()-lowerBound.getInt()) + lowerBound.getInt();
			int k = randomGenerator.nextInt(upperBound.getInt()-lowerBound.getInt()) + lowerBound.getInt();
			boolean aBiggest;
			
			if(k == a)
				continue;
			
			//System.out.println(a + "\t" + convert(a));
			
			aBiggest = c[0].calculate(convert(a), convert(k))[0] >= 0;


			if((aBiggest && a > b) || (!aBiggest && a < b)){
				rightCount += 1;
			}

			trialCount++;
		}
		
		//System.out.println((rightCount / (double)trialCount));
		
		return new double[] {(rightCount / (double)trialCount)};
	}
    
    private double convert(double x){
    	return (2*(x - lowerBound.getInt())) / (upperBound.getInt() - lowerBound.getInt()) -1;
    }

	@Override
	public int getNumInputs() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getNumOutputs() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getControlPerSim() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Simulation copy() {
		// TODO Auto-generated method stub
		return new HigherOrLower();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Higher or lower simulation";
	}

	IntegerHolder lowerBound = new IntegerHolder(0);
	IntegerHolder upperBound = new IntegerHolder(100);
	IntegerHolder trials = new IntegerHolder(1000);
	@Override
	public void menuInit(MenuItems menu) {
		menu.add("Upper Bound", EntryType.SLIDER, upperBound, new Constraint(-200,200),false);
		menu.add("Lower Bound", EntryType.SLIDER, lowerBound, new Constraint(-200,200),false);
		menu.add("Trials", EntryType.SLIDER, trials, new Constraint(10,1000),false);
	}

	@Override
	public boolean check() {
		return lowerBound.getInt() < upperBound.getInt() && Math.abs(lowerBound.getInt() - upperBound.getInt()) > 5;
	}

	@Override
	public boolean start() {
		return true;
	}

}
