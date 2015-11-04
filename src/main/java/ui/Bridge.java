package ui;

import evolver.EvolutionAlgorithm;

public class Bridge {
	//private final EvolutionAlgorithm ea;
	private final FXController fx;
	public Bridge(FXController fx){
		//this.ea = ea;
		this.fx = fx;
		System.out.println("Bridge made");
	}
}
