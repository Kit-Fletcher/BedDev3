package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Screen which explains to the player the premise with text and artwork
 */
public class BriefingScreen extends State {

	private Sprite banner;
	
	/**
	 * The constructor for the screen
	 */
	public BriefingScreen() {
		super();
		//Load and set up artwork
		banner = new Sprite(new Texture("header.jpg"));
		banner.setScale(4.3f);		
		banner.setPosition(0, Zombies.InitialWindowHeight-banner.getHeight());
	}

	@Override
	public void update() {
		//If left mouse button pressed, go to the next screen
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			StateManager.loadState(StateID.PLAYERSELECTMENU);
		}
	}
	
	@Override
	public void render() {
		UIBatch.begin();
		//Draw artwork
		banner.draw(UIBatch);
		//Draw text which explains the premise
		Zombies.mainFont.draw(UIBatch, "The apocalyse has arrived, and The University of\n"
									 + "York is now swarming with zombies. There are few\n"
									 + "survivors, but the fight is not over yet.\n"
									 + "Find a way to end this disaster.\n"
									 + "Be careful!", 20, 340);
		Zombies.mainFont.draw(UIBatch, "Click to continue", 424, 50);
		UIBatch.end();
	}
	
	@Override
	public void dispose() {
		//Clean up memory
		banner.getTexture().dispose();
	}
}
