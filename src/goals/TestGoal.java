package goals;

import evolver.ElementHolder;

public class TestGoal extends Goal{

	String memo;
	
	@Override
	public boolean checkComplete(ElementHolder elements) {
		double fit = elements.getElements()[elements.getElements().length-1].getFitness();
		memo = "Last Fitness:\t" + fit + "\nGen:\t" + elements.getGen();
		return fit > -0.5 || elements.getGen() >= 1000;
	}

	@Override
	public int maxEvolveTime() {
		// TODO Auto-generated method stub
		return 10000; //10 seconds
	}

	@Override
	public String memo() {
		return memo;
	}

	@Override
	public void initialize(ElementHolder elements) {
		// TODO Auto-generated method stub
	}

}
