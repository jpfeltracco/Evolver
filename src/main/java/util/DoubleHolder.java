package util;
/**
 * DoubleHolder holds a double value and has some methods that are used with MenuItems Object, Builder,
 * and other menu-related classes.
 * @author Keenan Nicholson
 *
 */
public class DoubleHolder extends Holder{
	private double val;
	
	/**
	 * Create a new DoubleHolder with the default value of 0.
	 */
	public DoubleHolder(){
		val = 0;
	}
	
	/**
	 * Create a new DoubleHolder with the default value of d.
	 * @param d the initial value
	 */
	public DoubleHolder(double d){
		val = d;
	}
	
	/**
	 * Sets this DoubleHolder to a value of d
	 * @param d the value to set this DoubleHolder to
	 */
	public void setValue(double d){
		this.val = d;
		changed();
	}
	
	/**
	 * Gets this DoubleHolder's value
	 * @return this DoubleHolder's value
	 */
	public double getValue(){
		return this.val;
	}
	
	public String toString(){
		return "" + val;
	}
	
	/**
	 * Gets this DoubleHolder's value
	 * @return this DoubleHolder's value
	 */
	public double getDouble(){
		return val;
	}
	
	/**
	 * Gets this DoubleHolder's value in float form
	 * @return this DoubleHolder's value in float form
	 */
	public float getFloat(){
		return (float)val;
	}

	@Override
	public void setRawVariable(Object o) {
		val = (Double)o;
		changed();
	}

	@Override
	public Object getRawVariable() {
		return new Double(val);
	}
	
	@Override
	public void setToHolder(Holder h) {
		if(!(h instanceof DoubleHolder))
			throw new RuntimeException("A DoubleHolder can only be set to values of other DoubleHolders.");
		changed();
		
	}
	
	@Override
	public Holder clone() {
		return new DoubleHolder(val);
	}
}
