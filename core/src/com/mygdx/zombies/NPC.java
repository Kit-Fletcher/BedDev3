package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.states.Level;

/**
 * NPC character which follows the player and will die if damaged by enemy attacks
 */
public class NPC extends Entity {
	
	private SpriteBatch spriteBatch;
	private Sprite sprite;
	private Player player;
	private int health;
	
	/** Constructor for the NPC class
	 * @param level - the level to spawn the NPC in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 */
	public NPC(Level level, int x, int y) {
		
		//Add sprite
		spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal("NPC.png")));
		
		//Add box2d body
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 40;
				friction = 0.5f;
				restitution = 0f;
			}
		};
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.NPC, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		body.setLinearDamping(4);
		body.setFixedRotation(true);
				
		this.player = level.getPlayer();
		health = 10;
	}
	
	/** Set the health to the given value, removing the NPC if health is depleted
	 * @param health - the value to set the health to
	 */
	public void setHealth(int health) {
		this.health = health;
		if(health <= 0)					
			getInfo().flagForDeletion();
	}
	
	public int getHealth() {
		return health;
	}
	
	/**
	 * Method to update position and rotation
	 */
	public void update() {	
		
		Vector2 bodyPosition = body.getPosition().scl(Zombies.PhysicsDensity);
		Vector2 playerPosition = new Vector2(player.getPositionX(), player.getPositionY());
		double distance = Zombies.distanceBetween(bodyPosition, playerPosition);
		double angleRads = Zombies.angleBetweenRads(new Vector2(player.getPositionX(), player.getPositionY()), bodyPosition);
		float angleDegrees = (float) Math.toDegrees(angleRads);
		
		//Move towards the player until closeby
		if(distance > 150)
			body.applyLinearImpulse((float) Math.cos(angleDegrees)*0.8f, (float) Math.sin(angleDegrees)*0.8f,
					bodyPosition.x, bodyPosition.y, true);
		
		sprite.setPosition(bodyPosition.x - sprite.getWidth()/2, bodyPosition.y - sprite.getHeight()/2);		
		sprite.setRotation(angleDegrees);
	}
	
	public void render() {
		sprite.draw(spriteBatch);
	}
	
	public void dispose() {
		super.dispose();
		sprite.getTexture().dispose();
	}
}
