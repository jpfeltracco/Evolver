package simulations.pong;

import controllers.Controller;
import ui.controllers.EATab;

public class Pong extends Game{
	boolean firstTimeCreate = true;
	public GameScreen s;
	public EATab etab;
	Controller c1;
	Controller c2;
	
	public Pong(Controller c1, Controller c2, EATab etab) {
		super();
		// Super janky way to control for rendering
		this.c1 = c1;
		this.c2 = c2;
		this.etab = etab;
	}

	@Override
	public Screen getStartScreen() {
		if (s == null) {
			s = new GameScreen(this, c1, c2);
		}
		return s;
	}

	@Override
	public void create() {
		Assets.load();
		super.create();
	}
	
	@Override
	public void dispose() {
		screen.dispose();
		etab.renderButton.fire();
	}
	
	
}
