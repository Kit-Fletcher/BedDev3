package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.states.Level;

/**
 * Generic enemy class. Has hearing and sight player detection mechanisms.
 * Has passive mode and an aggressive following mode for when player is detected
 */
public class Enemy extends Entity {
	
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
	private float alertSpeed;
	private Level level;
	private double distanceToPlayer;

	/**
	 * Constructor for generic enemy class
	 * @param level - the level instance to spawn the enemy mob in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 * @param spritePath - the file path of the sprite file to use
	 * @param speed - the speed that this enemy will move
	 * @param health - the amount of health that this enemy spawns with
	 */
	public Enemy(Level level, int x, int y, String spritePath, float speed, int health) {
		
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
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.ZOMBIE, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		body.setLinearDamping(4);
		body.setFixedRotation(true);

		this.level = level;
		this.speed = speed;
		this.health = health;	
		this.player = level.getPlayer();
		
		//Initialise timer values
		noiseTimer = 300;
		wanderTimer = 100;
		alertTimer = -1;
		//Initialise variable which affects speed depending on alert status
		alertSpeed = 0.2f;
	}

	/**
	 * Updates position based on whether player has been detected
	 */
	private void move() {
					
		angleToPlayerRadians = Zombies.angleBetweenRads(new Vector2(getPositionX(), getPositionY()),
			     new Vector2(player.getPositionX(), player.getPositionY()));
		
		distanceToPlayer = Zombies.distanceBetween(new Vector2(getPositionX(), getPositionY()),
				new Vector2(player.getPositionX(), player.getPositionY()));	
		
		if(alertTimer <= 0) {
			//Wandering state
			wanderTimer--;
			if (wanderTimer == 0) {
				//Walk in random direction
				angleRadians = Math.random()*Math.PI*2;
				wanderTimer = 150;
				alertSpeed = 0.2f;
			}
		}
		else {
			//Alert state
			angleRadians = angleToPlayerRadians;			
			alertTimer --;
		}
		
		int noise = (int)((double) level.getPlayer().getNoise()/(distanceToPlayer+1));
		if(noise>=3||isPlayerInSight()) {
			//If player detected, increase movement speed and set time alerted for
			alertTimer = noise*100;
			alertSpeed = 1;
		}
		
		//Move Box2D body in angleRadians, accounting for speed attributes
		body.applyLinearImpulse(new Vector2((float) Math.cos(angleRadians) * -speed * alertSpeed,
				(float) Math.sin(angleRadians) * -speed * alertSpeed), body.getPosition(), true);
			
		//Update sprite transformation
		angleDegrees = Math.toDegrees(angleRadians);
		sprite.setRotation((float) angleDegrees);		
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
	}
	
	/**
	 * @return true if the player is within 40 degrees of the zombie's line of sight
	 * and close enough, considering how well lit player is
	 */
	private boolean isPlayerInSight() {				
		return (Math.abs(angleDegrees-Math.toDegrees(angleRadians))<40) &&
				(distanceToPlayer < 200 || (inLights && distanceToPlayer < 1000));
	}
	
	/**
	 * Method to update zombie sound effects timer
	 */
	private void noiseStep() {
		noiseTimer--;
		//If timer reaches zero...
		if(noiseTimer <= 0) {
			noiseTimer = Zombies.random.nextInt(1000) + 500;
			//Play a random sound, adjusting volume based on distance to player
			Zombies.soundArrayZombie[1+Zombies.random.nextInt(Zombies.soundArrayZombie.length-1)]
					.play(distanceToPlayer < 500 ? 500-(float)distanceToPlayer : 0);
		}
	}

	/** Update all aspects of enemy
	 * @param inLights - whether the player is lit by light sources
	 */
	public void update(boolean inLights) {
		this.inLights = inLights;
		move();
		noiseStep();
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
}
