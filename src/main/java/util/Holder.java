package util;

public interface Holder {
	public void setRawVariable(Object o);
	public Object getRawVariable();
	public void setToHolder(Holder h);
	public Holder clone();
}
