package util;

public class StringHolder implements Holder{
	private String val;
	
	public StringHolder(){}
	
	public StringHolder(String s){
		this.val = s;
	}
	
	
	public void setValue(String s){
		this.val = s;
	}
	
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
