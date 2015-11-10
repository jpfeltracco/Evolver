package simulations;

import java.io.Serializable;

import controllers.Controller;
import evolver.Element;
import evolver.EvolutionAlgorithm;
import ui.Builder.TabMenu;
import ui.Builder.MenuItems;

public abstract class Simulation extends TabMenu implements Runnable, Serializable{
	
	public boolean verbose = false;
	protected EvolutionAlgorithm ea;
	protected Controller[] controllers;
	protected Element[] elements;
	
	public void run(){
		for(int i = 0; i < elements.length / getControlPerSim(); i++){
			for(int j = 0; j < getControlPerSim(); j++){
				controllers[j].setConfig(elements[j + i*getControlPerSim()]);
			}
			simulate(controllers);
		}
	}
	
	/**
	 * This is an iteration of the simulation. Take in the array of controllers supplied and
	 * process the simulation using the controllers. Set the new fitness weights and the rest
	 * will be done for you.
	 * @param c the Controller array to be used for this simulation. IE: The 'Players' in the game.
	 */
	public abstract void simulate(Controller[] c);
	
	/**
	 * Return the number of inputs the controller needs to take in for this particular simulation.
	 * IE: 2 if the controller needs to respond to ball x-position and ball x-velocity, but only 
	 * 1 if it responds to just position.
	 * @return the number of inputs that the neural net needs to take in
	 */
	public abstract int getNumInputs();
	
	/**
	 * Return the number of outputs the controller needs to give out for this particular simulation. 
	 * IE: 2 if the controller needs to control ball x-position and ball x-velocity, but only 1 if 
	 * it controls just ball x-position. 
	 * @return the number of outputs that the neural net needs to produce
	 */
	public abstract int getNumOutputs();
	
	/**
	 * Return the number of controllers this simulation needs to operate. IE: How many 'players' 
	 * are in this game? Access them via the array that is supplied to simulate(Controller[] c)
	 * @return the number of controllers that this simulation needs per game
	 */
	public abstract int getControlPerSim();
	
	/**
	 * Return a Simulation that operates in the exact same manor as this Simulation. This is used
	 * to make all of the simulations in this whole program, so ensure that all the proper variables
	 * are passed. NOTE: No need to pass variables that you didn't add. 
	 * @return A Simulation of this type that is identical but not the same instance
	 */
	public abstract Simulation cloneSimulation();
	
	public Simulation clone(){
		Simulation s = cloneSimulation();
		migrateVariablesTo(s);
		return s;
	}
	/**
	 * Produce a string that tells some general information about this Simulation.
	 * @return a String about this Simulation
	 */
	public abstract String toString();
	
	public String getName(){
		return this.getClass().toString();
	}
	
	public Controller[] getControllers() {
		return controllers;
	}
	
	public void setControllers(Controller[] controllers) {
		this.controllers = controllers;
	}

	public void setElements(Element[] elements) {
		this.elements = elements;
	}
	
	public void setEvolutionAlgorithm(EvolutionAlgorithm ea) {
		this.ea = ea;
	}
	
	
	//-------------------------------------------------------------------
	static String[] names = new String[] {"Memory","Pong","Round","Through","XOR"};
	public static String[] getTypeOfSimulations(){
		return names;
	}
	
	public static boolean checkExists(String name){
		for(String s : names){
			if(s.equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public static Simulation[] getAllSimulations(){
		
		Simulation[] sims = new Simulation[names.length];
		for(int i = 0; i < names.length; i++){
			sims[i] = getSimulation(names[i]);
		}
		return sims;
	}
	
	public static Simulation getSimulation(String sim){
		switch(sim){
		case "Memory":
			return (Simulation)(new Memory());
		case "Pong":
			return (Simulation)(new Pong());
		case "Round":
			return (Simulation)(new Round());
		case "Through":
			return (Simulation)(new Through());
		case "XOR":
			return (Simulation)(new XOR());
		}
		return null;
	}
}
