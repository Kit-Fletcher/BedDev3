package com.mygdx.zombies.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.Zombies;

/**
 * Generic state class, used for menus, screens and levels
 */
public class State {

	protected SpriteBatch worldBatch;
	protected SpriteBatch UIBatch;

	/**
	 * Constructor for the state class
	 */
	public State() {
		worldBatch = new SpriteBatch();
		UIBatch = new SpriteBatch();
		//Resize to account for window dimensions
		resize();
	}
	
	public SpriteBatch getWorldBatch() {
		return worldBatch;
	}
	
	public SpriteBatch getUIBatch() {
		return UIBatch;
	}

	/**
	 * Virtual render method
	 */
	public void render() {
	}

	
	/**
	 * Virtual update method
	 */
	public void update() {	
	}
	
	/**
	 * Method to scale the user interface based on the window size
	 */
	private void resize() {
		UIBatch.getProjectionMatrix()
			.setToOrtho2D(0, 0, Zombies.InitialWindowWidth, Zombies.InitialWindowHeight);
	}

	/**
	 * Method is run when window is resized
	 * @param width - the new window width
	 * @param height - the new window height
	 */
	public void resize(int width, int height) {
		resize();
	}

	public void dispose() {
		//Clean up memory
		worldBatch.dispose();
		UIBatch.dispose();
	}
}
