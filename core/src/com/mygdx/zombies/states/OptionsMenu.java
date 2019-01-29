package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Options menu state class
 */
public class OptionsMenu extends State {
	private Texture background;
	private Button back;
	private Button fullscreen;

	/**
	 * Constructor for the options menu
	 */
	public OptionsMenu() {
		super();
		//Load textures
		background = new Texture("background.jpg");
		//Set up buttons
		back = new Button(UIBatch, 500, 10, "Back");
		fullscreen = new Button(UIBatch, 500, 450, new String[] { "Fullscreen", "Windowed" });
		//Change button text if already fullscreen
		if(Gdx.graphics.isFullscreen()) {
			fullscreen.setMode(1);
		}
	}

	@Override
	public void render() {
		//Draw buttons and background
		UIBatch.begin();
		UIBatch.draw(background, 0, 0);
		back.render();
		fullscreen.render();
		//Draw options text label
		Zombies.titleFont.draw(UIBatch, "Options", 500, 700);
		UIBatch.end();
	}

	@Override
	public void update() {
		//Method to poll left mouse button and handle button click events
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			if (fullscreen.isHover()) {
				
				Zombies.soundSelect.play();	
				//Toggle fullscreen mode
				fullscreen.nextMode();			
				if(fullscreen.getMode() == 0) {
					//Set windowed
					Gdx.graphics.setWindowedMode(Zombies.InitialWindowWidth, Zombies.InitialWindowHeight);
				}
				else {
					//Set fullscreen
					DisplayMode mode = Gdx.graphics.getDisplayMode();
					Gdx.graphics.setFullscreenMode(mode);
				}
			}
			else if (back.isHover()) {
				Zombies.soundSelect.play();
				//Load main menu state
				StateManager.loadState(StateID.MAINMENU);
			}
		}
	}
	
	@Override
	public void dispose() {
		//Clean up memory
		background.dispose();
		back.dispose();
		fullscreen.dispose();
	}
}
