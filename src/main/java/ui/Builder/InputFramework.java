package ui.Builder;

import java.util.ArrayList;

import javafx.scene.control.Control;
import util.*;

public class InputFramework {
	
	
	
	ArrayList<String> titles = new ArrayList<String>();
	ArrayList<EntryType> types = new ArrayList<EntryType>();
	ArrayList<Object> variables = new ArrayList<Object>();
	ArrayList<Object> constraints = new ArrayList<Object>();
	ArrayList<Boolean> changableElements = new ArrayList<Boolean>();
	
	public void addEntry(String title, EntryType type, Object variable, Object constraint, boolean changable){
		titles.add(title);
		types.add(type);
		variables.add(variable);
		constraints.add(constraint);
		changableElements.add(new Boolean(changable));
	}
	
	
	
	public void addEntry(String title, EntryType type, Object variable, boolean changable){
		addEntry(title,type,variable,null,changable);
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
		return (Constraint)constraints.get(i);
	}
	
	public ComboHolder getComboDetails(int i){
		return (ComboHolder)constraints.get(i);
	}
	
	public boolean getChangable(int i){
		return changableElements.get(i);
	}
	
	public static enum EntryType{
		CHECKBOX, SLIDER, TEXT, LABEL, COMBOBOX
	}
	
	public boolean checkAllInit(){
		for(int i = 0; i < types.size(); i++){
			switch(types.get(i)){
			case TEXT:
				StringHolder sh = (StringHolder)variables.get(i);
				System.out.println("String: " + sh.initialized());
				if(!sh.initialized())
					return false;
				break;
			case COMBOBOX:
				ComboHolder ch = (ComboHolder)variables.get(i);
				System.out.println("Combo: " + ch.initialized());
				if(!ch.initialized())
					return false;
				break;
			default:
				break;
			}
		}
		return true;
	}
	
	public ArrayList<Object> getVariables(){
		return variables;
	}
	
	public void setDefaults(InputFramework def){
		ArrayList<Object> defVariables = def.getVariables();
		if(variables.size() != defVariables.size()){
			throw new RuntimeException("Variable array sizes do not match.");
		}
		for(int i = 0; i < defVariables.size(); i++){
			((Holder)variables.get(i)).setRawVariable(((Holder)defVariables.get(i)).getRawVariable());
		}
	}
}