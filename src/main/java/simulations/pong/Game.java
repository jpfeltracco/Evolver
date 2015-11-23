package simulations.pong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public abstract class Game implements ApplicationListener{
	public Screen screen;
	
	public abstract Screen getStartScreen();
	
	@Override
	public void create() {
		screen = getStartScreen();
	}

	@Override
	public void pause() {
		screen.pause();
	}

	@Override
	public void render() {
		screen.update(Gdx.graphics.getDeltaTime());
		screen.present(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int arg0, int arg1) {
	
	}

	@Override
	public void resume() {
		screen.resume();		
	}

}
