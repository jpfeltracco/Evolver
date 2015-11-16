package ui.Builder;

import java.io.Serializable;
import java.util.ArrayList;
import util.*;

public class MenuItems implements Serializable{
	ArrayList<String> titles = new ArrayList<String>();
	ArrayList<EntryType> types = new ArrayList<EntryType>();
	ArrayList<Holder> variables = new ArrayList<Holder>();
	ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	ArrayList<Boolean> changableElements = new ArrayList<Boolean>();
	boolean isEmpty = true;
	/**
	 * Adds a menu entry to the MenuItems Object, which will eventually be added to the GUI. Follow this step process:
	 * 1) Set up your holders (StringHolder, BooleanHolder, etc. all in ui). Giving them initial values will set their
	 * menu entries to initial values as well.
	 * 2) Determine the type of menu element you want. This needs to correlate to the Holder you have set up! IE. 
	 * ComboBoxes need BooleanHolder, Sliders need IntegerHolder or DoubleHolder, Text needs StringHolder, Label
	 * need StringHolders, and ComboBox needs a ComboHolder.
	 * 3) Determine if you want a Constraint applied to your variable
	 * 
	 * NOTE: If you do not set this up correctly (give entries the wrong variables) it WILL NOT work.
	 * 
	 * @param title the Title of this menu entry
	 * @param type the EntryType of this menu entry
	 * @param variable the input variable associated with this menu entry
	 * @param constraint the Constraint object that will constrain this menu input
	 * @param changable whether this variable can be changed during RunTime
	 */
	public void add(String title, EntryType type, Holder variable, Constraint constraint, boolean changable){
		if(!(variable instanceof Holder))
			throw new RuntimeException("Variables added to an MenuItems Object must implement Holder. (3rd param)");
		if(!(constraint instanceof Constraint) && constraint != null)
			throw new RuntimeException("Constraints added to an MenuItems Object must extend Constraint. (4th param)");
		
		switch(type){
		case CHECKBOX:
			if(!(variable instanceof BooleanHolder))
				throw new RuntimeException("The EntryType CHECKBOX requires a BooleanHolder. Change your variable (3rd param) to conform to this.");
			break;
		case SLIDER:
			if(!(variable instanceof IntegerHolder) && !(variable instanceof DoubleHolder))
				throw new RuntimeException("The EntryType SLIDER requires an IntegerHolder or a DoubleHolder. Change your variable (3rd param) to conform to this.");
			break;
		case TEXT:
			if(!(variable instanceof StringHolder))
				throw new RuntimeException("The EntryType TEXT requires a StringHolder. Change your variable (3rd param) to conform to this.");
		case LABEL:
			if(!(variable instanceof StringHolder))
				throw new RuntimeException("The EntryType LABEL requires a StringHolder. Change your variable (3rd param) to conform to this.");
			break;
		case COMBOBOX:
			if(!(variable instanceof ComboHolder))
				throw new RuntimeException("The EntryType COMBOBOX requires a ComboHolder. Change your variable (3rd param) to conform to this.");
			break;
		}
		
		titles.add(title);
		types.add(type);
		variables.add(variable);
		constraints.add(constraint);
		changableElements.add(new Boolean(changable));
		isEmpty = false;
	}
	
	/**
	 * Checks to see if this MenuItems is empty
	 * @return whether or not this MenuItems is empty
	 */
	public boolean isEmpty(){
		return isEmpty;
	}
	
	
	/**
	 * Adds a menu entry to the MenuItems Object, which will eventually be added to the GUI. Follow this step process:
	 * 1) Set up your holders (StringHolder, BooleanHolder, etc. all in ui). Giving them initial values will set their
	 * menu entries to initial values as well.
	 * 2) Determine the type of menu element you want. This needs to correlate to the Holder you have set up! IE. 
	 * ComboBoxes need BooleanHolder, Sliders need IntegerHolder or DoubleHolder, Text needs StringHolder, Label
	 * needs just a simple String object, and ComboBox needs a ComboHolder.
	 * 3) Determine if you want a Constraint applied to your variable
	 * 
	 * NOTE: If you do not set this up correctly (give entries the wrong variables) it WILL NOT work.
	 * 
	 * @param title the Title of this menu entry
	 * @param type the EntryType of this menu entry
	 * @param variable the input variable associated with this menu entry
	 * @param changable whether this variable can be changed during RunTime
	 */
	public void add(String title, EntryType type, Holder variable, boolean changable){
		add(title,type,variable,null,changable);
	}
	
	/**
	 * Gets the number of menu items defined by this MenuItems Object
	 * @return the number of menu items
	 */
	public int size(){
		return titles.size();
	}
	
	/**
	 * Gets the String title at a specific index.
	 * @param i the index to check
	 * @return the String title
	 */
	public String getTitle(int i){
		return titles.get(i);
	}
	
	/**
	 * Gets the EntryType at a specific index.
	 * @param i the index to check
	 * @return the EntryType
	 */
	public EntryType getType(int i){
		return types.get(i);
	}
	
	/**
	 * Gets the variable (a Holder) at a specific index.
	 * @param i the index to check
	 * @return the variable
	 */
	public Holder getVariable(int i){
		return variables.get(i);
	}
	
	/**
	 * Gets the Constraint at a specific index. NOTE: This function casts Objects to Constraints.
	 * You MUST ensure that the inputed index contains a Constraint (perhaps using instanceof).
	 * @param i the index to check
	 * @return the Constraint
	 */
	public Constraint getConstraint(int i){
		return constraints.get(i);
	}
	
	/**
	 * Returns whether or not an element at the inputed index can be changed during RunTime
	 * @param i the index to check
	 * @return whether or not the element can change during RunTime
	 */
	public boolean getChangable(int i){
		return changableElements.get(i);
	}
	
	/**
	 * Checks to see if all of the variables associated with this MenuItems Object are initialized.
	 * This is useful for making sure that all ComboBoxes and Strings have been inputed.
	 * Recommended: Use in check() to ensure that your variables are valid from the user.
	 * @return whether or not all of the menu variables have been initialized
	 */
	public boolean checkAllInit(){
		for(int i = 0; i < types.size(); i++){
			switch(types.get(i)){
			case TEXT:
				StringHolder sh = (StringHolder)variables.get(i);
				if(!sh.initialized())
					return false;
				break;
			case COMBOBOX:
				ComboHolder ch = (ComboHolder)variables.get(i);
				if(!ch.initialized())
					return false;
				break;
			default:
				break;
			}
		}
		return true;
	}
	
	/**
	 * Gets the variables associated with this MenuItems Object
	 * @return the variables associated with this MenuItems Object
	 */
	public ArrayList<Holder> getVariables(){
		return variables;
	}
	
	/**
	 * Gets the variables associated with this MenuItems Object
	 * @return the variables associated with this MenuItems Object
	 */
	public ArrayList<Holder> getVariableClones(){
		ArrayList<Holder> out = new ArrayList<Holder>();
		for(Holder h : variables){
			out.add(h.clone());
		}
		return out;
	}
	
	/**
	 * Clears this MenuItems Object. After this command, the MenuItems Object will be completely empty of values 
	 * and description.
	 */
	public void clear(){
		titles.clear();
		types.clear();
		variables.clear();
		constraints.clear();
		changableElements.clear();
	}
	
	/**
	 * Sets the default variables in this MenuItems Object to the variables of the inputed MenuItems Object. Note:
	 * Ensure that these MenuItems Objects both describe the same object! The menus MUST match, as this
	 * is meant for menu persistence between objects of the same type.
	 * @param def the other MenuItems Object to get default values from
	 */
	public void setDefaults(MenuItems def){
		ArrayList<Holder> defVariables = def.getVariables();
		if(size() != def.size()){
			throw new RuntimeException("Variable array sizes do not match. Current size: " + size() + "  Other: " + def.size());
		}
		for(int i = 0; i < defVariables.size(); i++){
			variables.get(i).setRawVariable(defVariables.get(i).getRawVariable());
		}
	}
	
	/**
	 * Enum for telling the Builder which type of menu item to use
	 * @author Keenan Nicholson
	 */
	public static enum EntryType{
		CHECKBOX, SLIDER, TEXT, LABEL, COMBOBOX
	}
}