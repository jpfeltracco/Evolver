package util;

public class BooleanHolder implements Holder{
	private boolean val;
	
	public BooleanHolder(){
		val = false;
	}
	
	public BooleanHolder(boolean b){
		val = b;
	}
	
	public void setValue(boolean bol){
		this.val = bol;
	}
	
	public boolean getValue(){
		return this.val;
	}
	
	public String toString(){
		return ((val)?"true":"false");
	}

	@Override
	public void setRawVariable(Object o) {
		val = (Boolean)o;	
	}

	@Override
	public Object getRawVariable() {
		return new Boolean(val);
	}
}
