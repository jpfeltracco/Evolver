package util;

public class ComboHolder implements Holder{
	final Object[] objects;
	final String[] titles;
	Object focus;
	public ComboHolder(Object[] objects){
		this.objects = objects;
		
		titles = new String[objects.length];
		for(int i = 0; i < objects.length; i++){
			titles[i] = objects[i].toString();
		}
		
	}
	
	public ComboHolder(Object[] objects, Object selection){
		this(objects);
		focus = selection;
	}
	
	public Object[] getTitles(){
		return objects;
	}
	
	public void setFocus(Object o){
		focus = o;
	}
	
	public Object getFocusObject(){
		return focus;
	}
	
	public boolean initialized(){
		return focus != null;
	}

	@Override
	public void setRawVariable(Object o) {
		focus = o;
	}

	@Override
	public Object getRawVariable() {
		return focus;
	}
	
	@Override
	public void setToHolder(Holder h) {
		setFocus(h.getRawVariable());
	}
	
	@Override
	public Holder clone() {
		ComboHolder out = new ComboHolder(objects);
		out.setFocus(focus);
		return out;
	}
}
