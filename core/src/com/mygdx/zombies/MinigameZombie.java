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
 * Generic enemy class. Has hearing and sight player detection mechanisms.
 * Has passive mode and an aggressive following mode for when player is detected
 */
public class MinigameZombie extends Entity {
	
	private float speed;
	private int health;
	protected Sprite sprite;
	protected double angleRadians;
	protected double angleDegrees;
	private double angleToPlayerRadians;
	private Player player;
	private SpriteBatch spriteBatch;	
	private boolean inLights;
	private int noiseTimer;
	private int wanderTimer;
	private int alertTimer;
	private int moveCounter;
	private double up, down, left, right;
	private float alertSpeed;
	private Minigame level;
	private double distanceToPlayer;
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
		
		//Initialise timer values
		noiseTimer = 300;
		wanderTimer = 100;
		alertTimer = -1;
		
		//Initialise movement values
		moveCounter = 0;
		up = (Math.PI * 1.5);
		down = (Math.PI / 2);
		left = 0;
		right = Math.PI;
		exit = false;
		map = level.path;
		
		//Initialise variable which affects speed depending on alert status
		alertSpeed = 0.2f;
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		box2dWorld.setContactListener(new CustomContactListener());
	}

	/**
	 * Updates position based on whether player has been detected
	 */
	private void move() {
		//System.out.println(angleRadians);
		//System.out.println(down);
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
		//System.out.println(moveCounter);
		
		switch(map) {
		
		case "World_One_Minigame" :
			switch(moveCounter) {
			case 0 :
				angleRadians = down;
				break;
			case 420 : 
				angleRadians = right;
				break;
			case 880 :
				angleRadians = up;
				break;
			case 950 :
				exit = true;
				break;
			}
		break;
		
		case "World_Two_Minigame" :
			switch(moveCounter) {
			case 0 :
				angleRadians = down;
				break;
			}
		break;

		case "World_Four_Minigame" :
			switch(moveCounter) {
			case 0 :
				angleRadians = down;
				break;
			}
		break;
		
		case "World_Five_Minigame" :
			switch(moveCounter) {
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
