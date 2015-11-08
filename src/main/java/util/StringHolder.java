package util;
/**
 * StringHolder holds a String value and has some methods that are used with InputFramework, Builder,
 * and other menu-related classes.
 * @author Keenan Nicholson
 *
 */
public class StringHolder implements Holder{
	private String val;
	
	/**
	 * Create a new StringHolder with the default value of null.
	 */
	public StringHolder(){}
	
	/**
	 * Create a new StringHolder with the default value of s.
	 * @param s the initial value
	 */
	public StringHolder(String s){
		this.val = s;
	}
	
	
	/**
	 * Sets this StringHolder to a value of s
	 * @param s the value to set this StringHolder to
	 */
	public void setValue(String s){
		this.val = s;
	}
	
	/**
	 * Gets this StringHolder value
	 * @return this StringHolder value
	 */
	public String getValue(){
		return this.val;
	}
	
	public String toString(){
		return val;
	}
	
	public boolean initialized(){
		return val != null;
	}

	@Override
	public void setRawVariable(Object o) {
		val = (String)o;
	}

	@Override
	public Object getRawVariable() {
		return val;
	}
	
	@Override
	public void setToHolder(Holder h) {
		if(!(h instanceof StringHolder))
			throw new RuntimeException("A StringHolder can only be set to values of other StringHolders.");
		setValue((String)h.getRawVariable());
	}
	
	@Override
	public Holder clone() {
		return new StringHolder(val);
	}
}
