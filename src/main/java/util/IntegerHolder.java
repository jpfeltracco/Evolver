package util;

public class IntegerHolder {
	int i = 0;
	
	public IntegerHolder(int i){
		this.i = i;
	}
	
	public void setValue(int i){
		this.i = i;
	}
	
	public int getValue(){
		return this.i;
	}
	
	public String toString(){
		return "" + i;
	}
}
