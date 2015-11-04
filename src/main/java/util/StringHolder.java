package util;

public class StringHolder {
	String s;
	
	public StringHolder(String s){
		this.s = s;
	}
	
	public StringHolder(){
		this("");
	}
	
	public void setValue(String s){
		this.s = s;
	}
	
	public String getValue(){
		return this.s;
	}
	
	public String toString(){
		return s;
	}
}
