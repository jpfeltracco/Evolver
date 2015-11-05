package controllers;

import evolver.Element;
import simulations.Memory;
import simulations.Pong;
import simulations.Round;
import simulations.Simulation;
import simulations.Through;
import simulations.XOR;

public abstract class Controller {
	protected int numIn;
	protected int numOut;
	double[] output;
	Element element;
	
	public void setInOut(int numIn, int numOut){
		this.numIn = numIn;
		this.numOut = numOut;
		output = new double[numOut];
		//System.out.println("Setting In Out: " + numIn + "\t" + numOut);
	}
	
	/**
	 * Calculates an answer based on a variable number of inputs. This is 
	 * a blocking statement currently, and as such, the program will wait 
	 * for an answer before moving on.
	 * @param in the inputs into this controller
	 * @return the outputs in a double array
	 */
	public abstract double[] calculate(double... in);
	
	/**
	 * Sets the config (whatever type this requires) from an Element. The 
	 * Element contains the config to be set via element.config.
	 * NOTE: ADD 'element = e' TO THIS METHOD (verbatim if e is the Element
	 * supplied by the method).
	 * @param e the Element to get the config from
	 */
	public abstract void setConfig(Element e);
	
	public void addFitness(double amt) {
		element.addFitness(amt);
	}
	
	/**
	 * Gets the size of the config array needed.
	 * @return the size of the needed config array
	 */
	public abstract int getConfigSize();
	
	/**
	 * Generate a 'random' config array, and then applies it to an Element 
	 * and returns the Element
	 * @return an Element with a random config, suited for this type of Controller
	 */
	public abstract Element generateRandomConfig();
	
	/**
	 * Mutates (randomizes) an Element e's config at a given number of places. (Calculated 
	 * from mutateAmt, which is the percentage of the total config that needs to be mutated.)
	 * @param e the Element to be mutated
	 * @param mutateAmt the percentage of Element e's config array to be mutated
	 */
	public abstract void mutateElement(Element e, float mutateAmt);
	
	/**
	 * Return a Controller that operates in the exact same manor as this Controller. This is used
	 * to make all of the controllers in this whole program, so ensure that all the proper variables
	 * are passed. NOTE: No need to pass variables that you didn't add. 
	 * @return A Simulation of this type that is identical but not the same instance
	 */
	public abstract Controller clone();
	
	
	public abstract boolean isSame(Element e1, Element e2);
	
	//-------------------------------------------------------------------
	public static String[] getTypeOfControllers(){
		return new String[] {"MLP","FPGA"};
	}
	
	public static Controller getController(String sim){
		switch(sim){
		case "MLP":
			return (Controller)(new MLP());
		case "FPGA":
			return (Controller)(new FPGA());
		}
		return null;
	}
}
