package util;

public class DoubleHolder implements Holder{
	private double val;
	
	public DoubleHolder(){
		val = 0;
	}
	
	public DoubleHolder(double d){
		val = d;
	}
	
	public void setValue(double d){
		this.val = d;
	}
	
	public double getValue(){
		return this.val;
	}
	
	public String toString(){
		return "" + val;
	}

	@Override
	public void setRawVariable(Object o) {
		val = (Double)o;
	}

	@Override
	public Object getRawVariable() {
		return new Double(val);
	}
}
