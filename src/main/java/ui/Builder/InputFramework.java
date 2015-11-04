package ui.Builder;

import java.util.ArrayList;

public class InputFramework {
	ArrayList<String> titles = new ArrayList<String>();
	ArrayList<EntryType> types = new ArrayList<EntryType>();
	ArrayList<Object> variables = new ArrayList<Object>();
	ArrayList<Constraint> constraints = new ArrayList<Constraint>();
	
	public void addEntry(String title, EntryType type, Object variable, Constraint constraint){
		titles.add(title);
		types.add(type);
		variables.add(variable);
		constraints.add(constraint);
	}
	
	public void addEntry(String title, EntryType type, Object variable){
		addEntry(title,type,variable,null);
	}
	
	public int size(){
		return titles.size();
	}
	
	public String getTitle(int i){
		return titles.get(i);
	}
	
	public EntryType getType(int i){
		return types.get(i);
	}
	
	public Object getVariable(int i){
		return variables.get(i);
	}
	
	public Constraint getConstraint(int i){
		return constraints.get(i);
	}
	
	public static enum EntryType{
		CHECKBOX, SLIDER, TEXT, LABEL
	}
}