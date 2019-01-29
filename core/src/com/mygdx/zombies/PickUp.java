package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.states.Level;

/**
 * Generic class for a pick up container object. Generates a Box2D area, which if touched
 * by the player is destroyed, releasing its stored item
 */
public class PickUp extends Entity {
	
	private SpriteBatch spriteBatch;
	private Sprite sprite;
	private Object containedItem;

	/** Constructor for the pick up object
	 * @param level - the level instance to spawn the pick up container object in
	 * @param x - the x position
	 * @param y - the y position
	 * @param pickUpSpritePath - the file path of the sprite to use
	 * @param containedItem - the item to contain in the pick up
	 * @param type - the type identifier, to determine type in collisions
	 */
	public PickUp(Level level, int x, int y, String pickUpSpritePath, Object containedItem, InfoContainer.BodyID type) {
		spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal(pickUpSpritePath)));
		sprite.setPosition(x, y);
	
		this.containedItem = containedItem;
		
		FixtureDef fixtureDef = new FixtureDef() {
			{
				isSensor = true;
			}
		};
		//Generate Box2D detection area from sprite
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, type, fixtureDef);
		
		body.setTransform((x+sprite.getWidth()/2) / Zombies.PhysicsDensity,
				(y+sprite.getHeight()/2) / Zombies.PhysicsDensity, 0);
	}	
	
	/**
	 * @return the object contained within the pick up container
	 */
	public Object getContainedItem() {
		return containedItem;
	}
	
	public void render() {
		sprite.draw(spriteBatch);
	}
	
	/**
	 * Dispose of the object, clearing the memory
	 */
	public void dispose() {
		super.dispose();
		sprite.getTexture().dispose();
	}
}
