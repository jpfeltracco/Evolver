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
}
