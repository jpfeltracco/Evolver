package ui.Builder;

/**
 * HasMenu enables objects to produce a menu that Builder will populate into a Pane. Use
 * MenuItems Objects to manage the specifics of the menu, and define Holder variables to pass
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
public abstract class TabMenu {
	
	protected MenuItems menuItems = new MenuItems();
	
	/**
	 * This method is for initializing the MenuItems Object "inputF" for this object. Define all variables
	 * and apply them to inputF by calling addEntry(). This function will get called
	 * automatically.
	 * 
	 * NOTE: The MenuItems Object has already been initialized as "inputF"
	 */
	public abstract void menuInit(MenuItems menu);
	
	/**
	 * Initializes this TabMenu. Do not override, use menuInit(MenuItems menu).
	 */
	public void menuInit(){
		menuInit(menuItems);
	}
	
	/**
	 * This method is for returning the MenuItems Object "inputF" made in the frameworkInit() method of 
	 * this interface.
	 * @return the MenuItems Object for this object
	 */
	public MenuItems getMenuItems(){
		return menuItems;
	}
	
	/**
	 * Migrates one object's variables to another instance of the same object. Useful for cloning and 
	 * other transfer methods. After this method is called, every variable that is dependent on menu
	 * choices should be initialized in the new object.
	 * @param menuItems the MenuItems Object of the original object.
	 * @param in the object to migrate this MenuItems Object to
	 */
	public void migrateVariablesTo(Object in){
		if(!(in instanceof TabMenu))
			throw new RuntimeException("Migration can only be applied to objects that implement HasMenu.");
		TabMenu r = (TabMenu)in;
		r.menuInit();
		r.getMenuItems().setDefaults(menuItems);
		if(!r.check())
			throw new RuntimeException("The provided variables are not valid for this object.");
		r.start();
	}
	
	/**
	 * This method is for checking whether the inputed values are valid for this object. It is
	 * called when any of the menu items are changed, and thus these changes are passed along to
	 * the respective Holders. 
	 * 
	 * NOTE: No need to check things that are constrained by Constraint. You should also call on
	 * MenuItems Object inputF.checkAllInit() to see if all the menu items have been initialize IE.
	 * a ComboBox is not initialized unless a value is selected, or a default value was originally
	 * provided.
	 * @return whether or not all menu values are valid.
	 */
	public abstract boolean check();
	
	/**
	 * This method is ran once the Start button has been pressed. Manage anything that needs to be
	 * done after the menu is fixed here, such as transferring variables to their non-holder counterparts.
	 */
	public abstract void start();
	
}
