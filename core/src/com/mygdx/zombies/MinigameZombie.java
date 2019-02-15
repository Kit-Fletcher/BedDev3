package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.states.Level;
import com.mygdx.zombies.states.Minigame;

/**
 * Generic minigame enemy, doesn't respond to player follows a set path through the map.
 */
public class MinigameZombie extends Entity {
	
	private float speed;
	private int health;
	protected Sprite sprite;
	protected double angleRadians;
	protected double angleDegrees;
	private boolean inLights;
	private SpriteBatch spriteBatch;	
	private int moveCounter;
	private double up, down, left, right;
	private Minigame level;
	private boolean exit;
	private String map;

	/**
	 * Constructor for generic enemy class
	 * @param level - the level instance to spawn the enemy mob in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 * @param spritePath - the file path of the sprite file to use
	 * @param speed - the speed that this enemy will move
	 * @param health - the amount of health that this enemy spawns with
	 */
	public MinigameZombie(Minigame level, int x, int y, String spritePath, float speed, int health) {
		
		//Add sprite
		spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));

		//Add box2d body
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 40;
				friction = 0.5f;
				restitution = 1f;
			}
		};
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.MINIZOMBIE, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		body.setLinearDamping(4);
		body.setFixedRotation(true);

		this.level = level;
		this.speed = speed;
		this.health = health;	
	
		//Initialise movement values
		moveCounter = 0;
		up = (Math.PI * 1.5);
		down = (Math.PI / 2);
		left = 0;
		right = Math.PI;
		exit = false;
		map = level.path;
		
		//Initialise variable which affects speed depending on alert status
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		box2dWorld.setContactListener(new CustomContactListener());
	}

	/**
	 * Updates position based on direction chosen and speed
	 */
	private void move() {
		setDirection();
			
		//Move Box2D body in angleRadians, accounting for speed attributes
		body.applyLinearImpulse(new Vector2((float) Math.cos(angleRadians) * -speed,
				(float) Math.sin(angleRadians) * -speed), body.getPosition(), true);
			
		//Update sprite transformation
		angleDegrees = Math.toDegrees(angleRadians);
		sprite.setRotation((float) angleDegrees);		
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		
		moveCounter ++;
	}


	/** Update all aspects of enemy
	 * @param inLights - whether the player is lit by light sources
	 */
	public void update(boolean inLights) {
		this.inLights = inLights;
		move();
		if(exit == true) {
			level.loseHealth(health / 5);
			getInfo().flagForDeletion();
		}
	}

	public int getPositionX() {
		return (int) (body.getPosition().x * Zombies.PhysicsDensity);
	}

	public int getPositionY() {
		return (int) (body.getPosition().y * Zombies.PhysicsDensity);
	}

	public void render() {
		sprite.draw(spriteBatch);
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		//Remove enemy if health below zero
		if(health <= 0)					
			getInfo().flagForDeletion();
	}
	
	/** Sets the direction of the zombie
	 *  Depending on how long the zombie has been moving for, its speed and the map
	 *  works out if the zombie needs to change direction to reach the end.
	 *  If the zombie has reached the end sets exit to be true to flag for removal.
	 */
	private void setDirection() {
		int temp = moveCounter * (int) speed;
		switch(map) {
		case "World_One_Minigame" :
			switch((temp < 1260) ? 0 : 
					(temp >= 1260  && temp < 2640) ? 1 : 
					(temp >= 2640  && temp < 2820) ? 2 : 3) {
			case 0 :
				angleRadians = down;
				break;
			case 1 : 
				angleRadians = right;
				break;
			case 2 :
				angleRadians = up;
				break;
			case 3 :
				exit = true;
				break;
			}
		break;
		
		case "World_Two_Minigame" :
			switch((temp < 440) ? 0 : 
					(temp >= 440  && temp < 1700) ? 1 : 
					(temp >= 1700  && temp < 2800) ? 2 :
					(temp >= 2800  && temp < 2970) ? 3 : 4) {
			case 0 :
				angleRadians = right;
				break;
			case 1 : 
				angleRadians = down;
				break;
			case 2 :
				angleRadians = right;
				break;
			case 3 :
				angleRadians = up;
				break;
			case 4 :
				exit = true;
				break;
			}
		break;

		case "World_Four_Minigame" :
			switch(moveCounter * (int) speed) {
			case 0 :
				angleRadians = down;
				break;
			}
		break;
		
		case "World_Five_Minigame" :
			switch(moveCounter * (int) speed) {
			case 0 :
				angleRadians = down;
				break;
			}
			break;
		}
	}
	
	public Vector2 getVelocity() {
		return body.getLinearVelocity();
	}
	
	public float getSpeed() {
		return speed;
	}
}
