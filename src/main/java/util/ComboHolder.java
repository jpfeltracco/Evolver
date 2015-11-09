package util;

import javafx.scene.control.ComboBox;

/**
 * ComboHolder holds the values for a ComboBox and assists in making them easier to use with Builder.
 * @author Keenan Nicholson
 *
 */
public class ComboHolder implements Holder{
	Object[] objects;
	String[] titles;
	transient ComboBox<Object> comboB;
	Object focus;
	
	/**
	 * Creates a new ComboHolder with the Object array that will be displayed in the drop-down menu.
	 * NOTE: The actual text that will be displayed will come from the Object's toString()
	 * @param objects the initial array of Objects to display in the drop down
	 */
	public ComboHolder(Object[] objects){
		this.objects = objects;
		titles = new String[objects.length];
		for(int i = 0; i < objects.length; i++){
			titles[i] = objects[i].toString();
		}
		
	}
	
	/**
	 * Creates a new ComboHolder with the Object array that will be displayed in the drop-down menu.
	 * NOTE: The actual text that will be displayed will come from the titles String[]
	 * @param objects the initial array of Objects to display in the drop down
	 * @param titles the initial array of titles to display in the drop down
	 */
	public ComboHolder(Object[] objects, String[] titles){
		this.objects = objects;
		this.titles = titles;
		
	}
	
	/**
	 * Creates a new ComboHolder with the Object array that will be displayed in the drop-down menu.
	 * NOTE: The actual text that will be displayed will come from the Object's toString()
	 * @param objects the initial array of Objects to display in the drop down
	 * @param selection the initial selection for this drop down
	 */
	public ComboHolder(Object[] objects, Object selection){
		this(objects);
		focus = selection;
	}
	
	/**
	 * Gets the array of titles.
	 * @return the titles in array form
	 */
	public String[] getTitles(){
		return titles;
	}
	
	/**
	 * Sets focus to the inputed object
	 * @param o the object to set focus to.
	 */
	public void setFocus(Object o){
		if(o instanceof String){
			for(int i = 0; i < titles.length; i++){
				String s = titles[i];
				if(s.equals((String)o)){
					focus = objects[i];
					break;
				}
			}
		}else{
			if(contains(o))
				focus = o;
			else
				throw new RuntimeException("The Object to set focus to is not contained in the drop down.");
		}
		
	}
	
	/**
	 * Check to see if the inputed object is contained in this menu.
	 * @param o the object to check
	 * @return whether the object is in the menu
	 */
	public boolean contains(Object o){
		for(Object obj : objects){
			if(obj == o){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the object that is currently focused on
	 * @return the current focus object
	 */
	public Object getFocusObject(){
		return focus;
	}
	
	/**
	 * Returns whether or not this Holder is initialized
	 * @return whether or not this Holder is initialized
	 */
	public boolean initialized(){
		return focus != null;
	}
	
	/**
	 * Sets this ComboHolder's ComboBox to the inputed value. NOTE: Needed for adding elements
	 * to the ComboHolder.
	 * @param comboB the ComboBox to set this Holder's ComboBox to
	 */
	public void setComboBox(ComboBox<Object> comboB){
		this.comboB = comboB;
	}

	/**
	 * Adds a new drop down item to this ComboHolder's ComboBox.
	 * @param o the object to add to the drop down
	 */
	public void add(Object o){
		if(comboB == null)
			throw new RuntimeException("Please set the ComboBox before adding new elements.");
		Object[] newObjects = new Object[objects.length+1];
		for(int i = 0; i < objects.length; i++){
			newObjects[i] = objects[i];
		}
		newObjects[objects.length] = o;
		comboB.getItems().add(o);	
	}
	
	/**
	 * Adds a new drop down item to this ComboHolder's ComboBox with the given title String.
	 * @param o the object to add to the drop down
	 * @param title the title to display the object with
	 */
	public void add(Object o, String title){
		if(comboB == null)
			throw new RuntimeException("Please set the ComboBox before adding new elements.");
		Object[] newObjects = new Object[objects.length+1];
		for(int i = 0; i < objects.length; i++){
			newObjects[i] = objects[i];
		}
		newObjects[objects.length] = o;
		String[] newTitles = new String[titles.length+1];
		for(int i = 0; i < titles.length; i++){
			newTitles[i] = titles[i];
		}
		newTitles[titles.length] = title;
		comboB.getItems().add(title);
	}
		
	@Override
	public void setRawVariable(Object o) {
		focus = o;
	}

	@Override
	public Object getRawVariable() {
		return focus;
	}
	
	@Override
	public void setToHolder(Holder h) {
		setFocus(h.getRawVariable());
	}
	
	@Override
	public Holder clone() {
		ComboHolder out = new ComboHolder(objects);
		out.setFocus(focus);
		return out;
	}
}
