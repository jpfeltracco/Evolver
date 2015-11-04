package ui;

import evolver.EvolutionAlgorithm;

public class Bridge {
	//private final EvolutionAlgorithm ea;
	private final FXController fx;
	private String tabID;
	public Bridge(String tabID, FXController fx){
		//this.ea = ea;
		this.fx = fx;
		this.tabID = tabID;
		System.out.println("Bridge made");
	}
	
	public boolean close(){
		System.out.println("Close");
		return false;
	}
	
	public String getTabID(){
		return tabID;
	}
}
