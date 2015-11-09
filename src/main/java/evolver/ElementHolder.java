package evolver;

import java.io.Serializable;

public class ElementHolder implements Serializable{
	private final Element[] elements;
	private final int genNum;
	public ElementHolder(Element[] elements, int genNum){
		this.elements = elements;
		this.genNum = genNum;
	}
	
	public Element[] getElements(){
		return elements;
	}
	
	public int getGen(){
		return genNum;
	}
}
