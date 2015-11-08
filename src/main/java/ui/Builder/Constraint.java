package ui.Builder;
/**
 * The Constraint class ensures that a Slider will keep its numerical values in the proper range.
 * Used with InputFrameworks.
 * @author Keenan Nicholson
 *
 */
public class Constraint{
	private final double min;
	private final double max;
	private Type type;
	private int digits = 0;
	
	/**
	 * Makes a new Constraint with a min, max, and the number of digits to round everything to. NOTE:
	 * stating digits ensures that the output will be a double, not an integer. It will need a 
	 * DoubleHolder as the variable type.
	 * @param min the min value in which the slider can output (inclusive)
	 * @param max the max value in which the slider can output (inclusive)
	 * @param digits the number of digits which are available for the output
	 */
	public Constraint(double min, double max, int digits){
		this.min = min;
		this.max = max;
		type = Type.DOUBLE;
		this.digits = digits;
	}
	
	/**
	 * Makes a new Constraint with a min, max, and the number of digits to round everything to. NOTE:
	 * the output will be an integer, and this needs an IntegerHolder as the variable type.
	 * @param min the min value in which the slider can output (inclusive)
	 * @param max the max value in which the slider can output (inclusive)
	 */
	public Constraint(int min, int max){
		this.min = min;
		this.max = max;
		type = Type.INT;
	}
	
	/**
	 * Gets the max value for this Constraint in double form.
	 * @return the max value
	 */
	public double getMaxDouble(){
		return max;
	}
	
	/**
	 * Gets the min value for this Constraint in double form.
	 * @return the min value
	 */
	public double getMinDouble(){
		return min;
	}
	
	/**
	 * Gets the max value for this Constraint in integer form.
	 * @return the max value
	 */
	public int getMaxInt(){
		return (int)max;
	}
	
	/**
	 * Gets the min value for this Constraint in integer form.
	 * @return the min value
	 */
	public int getMinInt(){
		return (int)min;
	}
	
	/**
	 * Gets the Type of digit, INT, or DOUBLE. NOTE: Enum as Constraint.Type.
	 * @return the Type of the digit
	 */
	public Type getDigitType(){
		return type;
	}
	
	/**
	 * Returns the number of available digits to work with.
	 * @return
	 */
	public int getDigitCount(){
		return digits;
	}
	
	/**
	 * Clones this Constraint for use in another context.
	 */
	public Constraint clone(){
		if(type == Type.DOUBLE)
			return new Constraint(min, max, digits);
		else
			return new Constraint((int)min, (int)max);
	}
	
	/**
	 * Enum for Constraint to tell what type of number it is working with.
	 * @author keenannicholson
	 *
	 */
	public enum Type{
		INT, DOUBLE
	}
}
