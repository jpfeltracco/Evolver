package controllers;

import java.io.File;
import java.io.Serializable;

import evolver.Element;
import ui.Builder.TabMenu;

public abstract class Controller extends TabMenu implements Serializable{
	protected int numIn;
	protected int numOut;
	double[] output;
	Element element;
	
	/**
	 * Sets the number of inputs and outputs for this controller in this context.
	 * @param numIn the number of inputs
	 * @param numOut the number of outputs
	 */
	public void setInOut(int numIn, int numOut){
		this.numIn = numIn;
		this.numOut = numOut;
		output = new double[numOut];
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
	
	/**
	 * Passes the fitness on to the element that this controller currently owns.
	 * @param amt the fitness to be added
	 */
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
	 * are passed. NOTE: No need to pass variables that you didn't add. NOTE: EVERY VARIABLE used
	 * in the menuInit(MenuItems menu) function HAVE ALREADY BEEN MOVED. NO NEED TO MOVE THOSE. 
	 * @return A Simulation of this type that is identical but not the same instance
	 */
	public abstract Controller copy();
	
	/**
	 * Clones this Controller and returns it.
	 * @return the cloned Controller
	 */
	public Controller clone(){
		Controller c = copy();
		c.setInOut(this.numIn, this.numOut);
		migrateVariablesTo(c);
		return c;
	}
	
	/**
	 * Determine if these two Elements are the same by this controller's standards. This can be
	 * via fitness values, but it should be based on actual element configuration.
	 * @param e1 the first Element to be compared
	 * @param e2 the second Element to be compared
	 * @return whether these two Elements are the same
	 */
	public abstract boolean isSame(Element e1, Element e2);
	
	/**
	 * Save the current configuration to the File specified. Do not add extensions or change the name,
	 * as this has already been set up.
	 * @param loc the File location of this save file
	 */
	public abstract void saveConfig(File loc);
	
	/**
	 * Gets the preferred extensions for outputting the configuration file. 
	 * IE. If a .txt is preferred, an output would be "txt".
	 * @return the preferred extensions for output configurations
	 */
	public abstract String[] getExtension();
	
	/**
	 * This method is ran once the Start button has been pressed. Manage anything that needs to be
	 * done after the menu is fixed here, such as transferring variables to their non-holder counterparts.
	 * DO NOT override.
	 */
	public void start(){
		start(this.numIn, this.numOut);
	}
	
	/**
	 * This method is ran once the Start button has been pressed. Manage anything that needs to be
	 * done after the menu is fixed here, such as transferring variables to their non-holder counterparts.
	 * @param numIn the number of inputs to the Controller
	 * @param numOut the number of expected outputs to the Controller
	 */
	public abstract void start(int numIn, int numOut);
	

	//-------------------------------------------------------------------
	// TODO Create a better way of doing this!
	//-------------------------------------------------------------------
	static String[] names = new String[] {"MLP","FPGA"};
	public static String[] getTypeOfControllers(){
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
