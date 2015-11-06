package ui.Builder;

/**
 * HasMenu enables objects to produce a menu that Builder will populate into a Pane. Use
 * InputFrameworks to manage the specifics of the menu, and define Holder variables to pass
 * these menu results to your program.
 * 
 * 
 * 
 * NOTE: Remember to appropriately overload the clone() method if needed. IE. Pass the menu results
 * along to the cloned object if that is needed.
 * 
 * @author Keenan Nicholson
 *
 */
public interface HasMenu {
	
	/**
	 * This method is for initializing the InputFramework "inputF" for this object. Define all variables
	 * and apply them to inputF by calling addEntry(). This function will get called
	 * automatically.
	 * 
	 * NOTE: The InputFramework has already been initialized as "inputF"
	 */
	public void frameworkInit();

	/**
	 * Clears this InputFramework that is passed in. After this command, the InputFramework 
	 * will be completely empty of values and description.
	 */
	public default void clearFramework(InputFramework f){
		f.clear();
	}
	
	/**
	 * This method is for returning the InputFramework "inputF" made in the frameworkInit() method of 
	 * this interface.
	 * @return the InputFramework for this object
	 */
	public InputFramework getFramework();
	
	/*public default void claimVariables(ArrayList<Holder> vals){
		inputF.setVariablesFromList(vals);
	}
	
	public default ArrayList<Holder> prepCloneMenuItems(){
		return inputF.getVariableClones();
	}*/
	
	public static void migrate(InputFramework inputF, Object in){
		if(!(in instanceof HasMenu))
			throw new RuntimeException("Migration can only be applied to objects that implement HasMenu.");
		HasMenu r = (HasMenu)in;
		r.frameworkInit();
		r.getFramework().setDefaults(inputF);
		if(!r.check())
			throw new RuntimeException("The provided variables are not valid for this object.");
		r.confirmMenu();
	}
	
	/**
	 * This method is for checking whether the inputed values are valid for this object. It is
	 * called when any of the menu items are changed, and thus these changes are passed along to
	 * the respective Holders. 
	 * 
	 * NOTE: No need to check things that are constrained by Constraint. You should also call on
	 * InputFramework inputF.checkAllInit() to see if all the menu items have been initialize IE.
	 * a ComboBox is not initialized unless a value is selected, or a default value was originally
	 * provided.
	 * @return whether or not all menu values are valid.
	 */
	public boolean check();
	
	/**
	 * This method is ran once the Start button has been pressed. Manage anything that needs to be
	 * done after the menu is fixed here, such as transferring variables to their non-holder counterparts.
	 */
	public void confirmMenu();
	
}
