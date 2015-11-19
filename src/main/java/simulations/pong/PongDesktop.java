package simulations.pong;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class PongDesktop {
	public static void main(String[] argv){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Awesome";
		config.width = 700;
		config.height = 500;
		config.resizable = false;
		new LwjglApplication(new Pong(null, null), config); // We don't know a way to get controllers in here yet
	}
}
