package com.mygdx.zombies.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.states.Level;

/**
 * Class for a melee weapon
 */
public class MeleeWeapon implements Weapon {
	
	private SpriteBatch spriteBatch;
	private Sprite sprite;
	
	/** Constructor for the melee weapon
	 * @param spriteBatch - the spriteBatch to draw the weapon sprite to
	 */
	public MeleeWeapon(SpriteBatch spriteBatch) {
		this.spriteBatch = spriteBatch;
		sprite = new Sprite(new Texture(Gdx.files.internal("sword.png")));
	}
	
	public void setLevel(Level level) {
		spriteBatch = level.getWorldBatch();
		
	}

	@Override
	public void render() {
		sprite.draw(spriteBatch);
	}

	@Override
	public void use() {
	}

	/** 
	 * Update method to set the transformation of the weapon
	 * @param x - the x position of the player's hands
	 * @param y - the y position of the player's hands
	 * @param rotation - the rotation of the player's hands
	 */
	@Override
	public void update(int x, int y, float rotation) {
		//set position to front part of player sprite, sticking to player rotation and rotating with mouse.
		sprite.setPosition(x, y);
		sprite.setRotation(rotation);			
	}
	
	/**
	 * Dispose method to clear memory
	 */
	@Override
	public void dispose() {	
		sprite.getTexture().dispose();
	}
}
