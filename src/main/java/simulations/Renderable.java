package simulations;

import controllers.Controller;
import ui.controllers.EATab;

public interface Renderable {
	public void render(Controller[] c, EATab etab);
	
	public void exit();
	
}
