package ui.Builder;

public class Constraint{
	private final double min;
	private final double max;
	private Type type;
	private int digits = 0;
	public Constraint(double min, double max, int digits){
		this.min = min;
		this.max = max;
		type = Type.DOUBLE;
		this.digits = digits;
	}
	
	public Constraint(int min, int max){
		this.min = min;
		this.max = max;
		type = Type.INT;
	}
	
	public double getMaxDouble(){
		return max;
	}
	
	public double getMinDouble(){
		return min;
	}
	
	public int getMaxInt(){
		return (int)max;
	}
	
	public int getMinInt(){
		return (int)min;
	}
	
	public Type getDigitType(){
		return type;
	}
	
	public int getDigitCount(){
		return digits;
	}
	
	public Constraint clone(){
		if(type == Type.DOUBLE)
			return new Constraint(min, max, digits);
		else
			return new Constraint((int)min, (int)max);
	}
	
	public enum Type{
		INT, DOUBLE
	}
}
