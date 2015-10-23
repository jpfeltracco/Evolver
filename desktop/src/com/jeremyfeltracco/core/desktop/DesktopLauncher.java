package com.jeremyfeltracco.core.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jeremyfeltracco.core.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Awesome";
		config.width = 500;
		config.height = 500;
		config.resizable = false;
		new LwjglApplication(new Main(), config);
	}
}

//Testing
