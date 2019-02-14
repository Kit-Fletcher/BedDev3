package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Screen to allow player selection
 */
public class PlayerSelectMenu extends State {
	
	private Button player1;
	private Button player2;
	private Button player3;
	private Sprite sprite1; 
	private Sprite sprite2; 
	private Sprite sprite3; 
	
	/**
	 * Constructor for the screen
	 */
	public PlayerSelectMenu()
	{
		super();
		
		//Set up buttons
		player1 = new Button(UIBatch, 50, 50, "Comp Sci");
		player2 = new Button(UIBatch, 500, 50, "Chemistry");
		player3 = new Button(UIBatch, 950, 50, "Footballer");
		//Load textures and set up sprites
		sprite1 = new Sprite(new Texture(Gdx.files.internal("player/player1_unequipped.png")));
		sprite1.setPosition(150, 400);
		sprite1.scale(3);
		sprite2 = new Sprite(new Texture(Gdx.files.internal("player/player2_unequipped.png")));
		sprite2.setPosition(600, 400);
		sprite2.scale(3);
		sprite3 = new Sprite(new Texture(Gdx.files.internal("player/player3_unequipped.png")));
		sprite3.setPosition(1000, 400);
		sprite3.scale(3);
	}
	
	@Override
	public void render() {
		UIBatch.begin();
		//Draw text prompt
		Zombies.mainFont.draw(UIBatch, "Select a Student", 450, 700);
		//Draw buttons
		player1.render();
		player2.render();
		player3.render();
		//Draw sprites
		sprite1.draw(UIBatch);
		sprite2.draw(UIBatch);
		sprite3.draw(UIBatch);
		UIBatch.end();
	}
	
	@Override
	public void update() {
		//Method to poll left mouse button and handle button click events
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
				if (player1.isHover()) {
					Zombies.soundSelect.play();
					Player.setPlayerType(1);
					//Load next screen
					StateManager.loadState(StateID.STAGE1, 0);
				}
				else if (player2.isHover()) {
					Zombies.soundSelect.play();
					Player.setPlayerType(2);
					//Load next screen
					StateManager.loadState(StateID.STAGE1, 0);
				}
				else if (player3.isHover()) {
					Zombies.soundSelect.play();
					Player.setPlayerType(3);
					//Load next screen
					StateManager.loadState(StateID.STAGE1, 0);
				}
		}
	}
	
	@Override
	public void dispose() {
		//Clean up memory
		player1.dispose();
		player2.dispose();
		player3.dispose();
		sprite1.getTexture().dispose();
		sprite2.getTexture().dispose();
		sprite3.getTexture().dispose();
	}
}
