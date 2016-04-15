package util;

import java.io.Serializable;

public interface ChangeEvent extends Serializable{

	public void handle(Object val);
	
}
