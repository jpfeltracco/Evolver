package util;

public class DoubleHolder {
	double d = 0.0;
	
	public DoubleHolder(double d){
		this.d = d;
	}
	
	public void setValue(double d){
		this.d = d;
	}
	
	public double getValue(){
		return this.d;
	}
	
	public String toString(){
		return "" + d;
	}
}
