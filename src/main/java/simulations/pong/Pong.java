package simulations.pong;

import controllers.Controller;

public class Pong extends Game{
	boolean firstTimeCreate = true;
	public GameScreen s;
	Controller c1;
	Controller c2;
	
	public Pong(Controller c1, Controller c2) {
		super();
		// Super janky way to control for rendering
		this.c1 = c1;
		this.c2 = c2;
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
	
	
}
