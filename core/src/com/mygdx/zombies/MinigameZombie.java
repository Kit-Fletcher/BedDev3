package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.states.Minigame;

/**
 * Generic enemy class. Has hearing and sight player detection mechanisms.
 * Has passive mode and an aggressive following mode for when player is detected
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
		//this.player = level.getPlayer();
		
		//Initialise movement values
		moveCounter = 0;
		up = (Math.PI * 1.5);
		down = (Math.PI / 2);
		left = 0;
		right = Math.PI;
		exit = false;
		map = level.path;
		
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		box2dWorld.setContactListener(new CustomContactListener());
	}

	/**
	 * Updates position based on whether player has been detected
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
					(temp >= 440  && temp < 1900) ? 1 : 
					(temp >= 1900  && temp < 2900) ? 2 :
					(temp >= 2900  && temp < 3200) ? 3 :
					(temp >= 3200  && temp < 3300) ? 4 :
					(temp >= 3300  && temp < 3400) ? 5 : 6) {
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
				angleRadians = right;
				break;
			case 5 :
				angleRadians = up;
				break;
			case 6 :
				exit = true;
				break;
			}
		break;

		case "World_Four_Minigame" :
			switch((temp < 275) ? 0 : 
				(temp >= 275  && temp < 875) ? 1 : 
				(temp >= 875  && temp < 1400) ? 2 :
				(temp >= 1400  && temp < 2600) ? 3 :
				(temp >= 2600  && temp < 3000) ? 4 : 5) {
			case 0 :
				angleRadians = down;
				break;
			case 1 : 
				angleRadians = right;
				break;
			case 2 :
				angleRadians = down;
				break;
			case 3 :
				angleRadians = left;
				break;
			case 4 :
				angleRadians = up;
				break;
			case 5 :
				exit = true;
				break;
			}
			
		break;
		
		case "World_Five_Minigame" :
			switch((temp < 1160) ? 0 : 
				(temp >= 1160  && temp < 2600) ? 1 : 
				(temp >= 2500  && temp < 3500) ? 2 : 3) {
			case 0 :
				angleRadians = down;
				break;
			case 1 : 
				angleRadians = left;
				break;
			case 2 :
				angleRadians = up;
				break;
			case 3 :
				exit = true;
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
