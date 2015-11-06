package util;

public class IntegerHolder implements Holder{
	private int val = 0;
	
	public IntegerHolder(){
		val = 0;
	}
	
	public IntegerHolder(int i){
		val = i;
	}
	
	public void setValue(int i){
		this.val = i;
	}
	
	public int getValue(){
		return this.val;
	}
	
	public String toString(){
		return "" + val;
	}

	@Override
	public void setRawVariable(Object o) {
		val = (Integer)o;
		
	}

	@Override
	public Object getRawVariable() {
		return new Integer(val);
	}
	
	@Override
	public void setToHolder(Holder h) {
		if(!(h instanceof IntegerHolder))
			throw new RuntimeException("A IntegerHolder can only be set to values of other IntegerHolders.");
		setValue((Integer)h.getRawVariable());
	}
	
	@Override
	public Holder clone() {
		return new IntegerHolder(val);
	}
}
