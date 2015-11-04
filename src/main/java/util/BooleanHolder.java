package util;

public class BooleanHolder {
	boolean bol = false;
	
	public BooleanHolder(boolean bol){
		this.bol = bol;
	}
	
	public void setValue(boolean bol){
		this.bol = bol;
	}
	
	public boolean getValue(){
		return this.bol;
	}
	
	public String toString(){
		return ((bol)?"true":"false");
	}
}
