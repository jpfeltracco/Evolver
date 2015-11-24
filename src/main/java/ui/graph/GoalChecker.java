package ui.graph;

import evolver.ElementHolder;
import ui.controllers.VDriver;

public class GoalChecker implements Runnable{

	final VDriver vDriver;
	final ElementHolder elements;
	
	public GoalChecker(VDriver vDriver, ElementHolder elements) {
		this.vDriver = vDriver;
		this.elements = elements;
	}

	@Override
	public void run() {
		vDriver.check(elements);
	}

}
