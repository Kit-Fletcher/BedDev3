package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * End screen which is played when the game has been won
 */
public class EndScreen extends State {

	private Texture banner;
	
	/**
	 * Constructor for the screen
	 */
	public EndScreen() {
		super();
		//Load artwork texture
		banner = new Texture("win.png");
		Zombies.soundAmbientWind.stop();
		Zombies.soundEndMusic.loop();
	}
	
	@Override
	public void update() {
		//Check for left mouse button and go to next screen if pressed
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			StateManager.loadState(StateID.MAINMENU);
		}
	}
	
	@Override
	public void render() {
		UIBatch.begin();
		//Draw artwork
		UIBatch.draw(banner, Zombies.InitialWindowWidth/2-banner.getWidth()/2, Zombies.InitialWindowHeight-banner.getHeight()-20);
		//Draw win text message
		Zombies.mainFont.draw(UIBatch, "Click to continue", 424, 140);
		UIBatch.end();
	}
	
	@Override
	public void dispose() {
		//Stop playing the music when screen exited
		Zombies.soundEndMusic.stop();
		banner.dispose();
	}
}
