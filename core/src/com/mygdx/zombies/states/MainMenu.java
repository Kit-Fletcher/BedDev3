package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * The main menu class, with buttons linking to various functionality
 */
public class MainMenu extends State {

	private Button play;
	private Button exit;
	private Button options;
	private Button credits;
	private Texture background;
	private Texture logo;

	/**
	 * Constructor for the main menu
	 */
	public MainMenu() {
		super();
		//Load textures
		background = new Texture("background.jpg");
		logo = new Texture("logo.png");
		
		//Initialise buttons
		play = new Button(UIBatch, 325, 350, "Play");
		exit = new Button(UIBatch, 675, 150, "Exit");
		options = new Button(UIBatch, 675, 350, "Options");
		credits = new Button(UIBatch, 325, 150, "Credits");
	}

	@Override
	public void render() {
		UIBatch.begin();
		UIBatch.draw(background, 0, 0);
		//Render buttons
		play.render();
		exit.render();
		options.render();
		credits.render();
		//Render textures
		Zombies.titleFont.draw(UIBatch, "Silence Of The Lamberts", 225, 650);
		UIBatch.draw(logo, 1050, 10);
		UIBatch.end();		
	}

	@Override
	public void update() {
		//Code to handle button click events
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			if (play.isHover()) {
				Zombies.soundSelect.play();
				//Start playing ambient sound
				Zombies.soundAmbientWind.loop();
				StateManager.loadState(StateID.BRIEFINGSCREEN);
			}
			else if (credits.isHover()) {
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.CREDITSMENU);
			}
			else if (options.isHover()) {
				Zombies.soundSelect.play();
				StateManager.loadState(StateID.OPTIONSMENU);
			}
			else if (exit.isHover()) {
				//Quit the game
				Gdx.app.exit();
			}
		}
	}

	@Override
	public void dispose() {
		//Clear the memory
		play.dispose();
		exit.dispose();
		options.dispose();
		credits.dispose();
		background.dispose();
		logo.dispose();
	}
}
