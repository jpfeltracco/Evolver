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
	
	@Override
	public void setToHolder(Holder h) {
		if(!(h instanceof DoubleHolder))
			throw new RuntimeException("A DoubleHolder can only be set to values of other DoubleHolders.");
		System.out.println("BEFORE:\t" + getValue());
		System.out.println("SET:\t" + (Double)h.getRawVariable());
		setValue((Double)h.getRawVariable());
		System.out.println("AFTER:\t" + getValue());
		
	}
	
	@Override
	public Holder clone() {
		return new DoubleHolder(val);
	}
}
