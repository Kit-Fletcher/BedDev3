package com.mygdx.zombies;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ZombieLauncher {

	public static void main(String[] args) {

		new LwjglApplication(new Zombies(), new LwjglApplicationConfiguration());
	}
}
