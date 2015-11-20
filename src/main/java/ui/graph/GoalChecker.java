package ui.graph;

import ui.controllers.VDriver;

public class GoalChecker implements Runnable{

	final VDriver vDriver;
	
	public GoalChecker(VDriver vDriver) {
		this.vDriver = vDriver;
	}

	@Override
	public void run() {
		vDriver.check();
	}

}
