package util;

import java.io.Serializable;

public interface Holder extends Serializable{
	
	/**
	 * Sets the Holder's variable to that of another's. Object based.
	 * @param o the object to set the Holder's object to
	 */
	public void setRawVariable(Object o);
	
	/**
	 * Gets the Holder's variable in Object form.
	 * @return the variable in Object form
	 */
	public Object getRawVariable();
	
	/**
	 * Sets this Holder's values to another Holder's.
	 * @param h the other Holder to set values to
	 */
	public void setToHolder(Holder h);
	
	/**
	 * Clones a holder and returns one that has the same values, but is not
	 * dependent on the same values.
	 * @return a cloned Holder
	 */
	public Holder clone();
}
