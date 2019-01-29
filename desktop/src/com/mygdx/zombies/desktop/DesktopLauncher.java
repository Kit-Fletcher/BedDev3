package com.mygdx.zombies.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.zombies.Zombies;

public class DesktopLauncher {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Zombies.InitialWindowWidth;
        config.height = Zombies.InitialWindowHeight;
        config.title = Zombies.windowTitle;
        config.vSyncEnabled = true;
        new LwjglApplication(new Zombies(), config);
	}
}
