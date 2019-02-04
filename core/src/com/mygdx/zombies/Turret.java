package com.mygdx.zombies;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.items.RangedWeapon;
import com.mygdx.zombies.states.Level;

public class Turret extends Entity{

	
	
	protected Sprite sprite;
	protected double angleRadians;
	protected double angleDegrees;
	private double angleToZombieRadians;
	private ArrayList<Enemy> enemiesList;
	private SpriteBatch spriteBatch;	
	private boolean inLights;
	private Level level;
	private double distanceToZombie;
	private RangedWeapon gun;
	private boolean shoot = true;

	/**
	 * Constructor for generic turret class
	 * @param level - the level instance to spawn the enemy mob in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 * @param spritePath - the file path of the sprite file to use
	 * @param speed - the speed that this enemy will move
	 * @param health - the amount of health that this enemy spawns with
	 */
	public Turret(Level level, int x, int y, String spritePath, int shootDelay, String projectileSpritePath, float bulletSpeed, Sound shootSound) {
		
		//Add sprite
		spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
		//add the turrets gun
		gun = new RangedWeapon(level,shootDelay,projectileSpritePath,bulletSpeed,shootSound);
		//Add box2d body
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 40;
				friction = 0.5f;
				restitution = 1f;
			}
		};
		
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.TURRET, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		
		body.setLinearDamping(4);
		body.setFixedRotation(true);
		body.setType(BodyDef.BodyType.StaticBody);

		this.level = level;
		this.enemiesList = level.getEnemiesList();
		
		//Initialise timer values
		//Initialise variable which affects speed depending on alert status
		sprite.setRotation((float) angleDegrees);		
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		
	}
	
	public void update() {
		if(enemiesList!=null && shoot == true) {
			angleToZombieRadians = getAngleToZombie(getClosestZombie());
			angleDegrees = (double)Math.toDegrees(angleToZombieRadians); 
			sprite.setRotation((float) angleDegrees);	
			gun.use(this);
			gun.update(getPositionX(),getPositionY(), (float) angleDegrees);
			sprite.setRotation((float) angleDegrees);
		}
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
	}
	
	private Enemy getClosestZombie() {
		Enemy closestZombie = null;
		double distance = 0;
		double newDistance;
		for (int i = 0; i < enemiesList.size(); i++) {
			newDistance =Zombies.distanceBetween(new Vector2(getPositionX(), getPositionY()),
					new Vector2(enemiesList.get(i).getPositionX(), enemiesList.get(i).getPositionY()));
			if( i ==0) {
				distance = newDistance;
				closestZombie = enemiesList.get(i);
			}else if(Math.abs(newDistance)<Math.abs(distance)) {
				closestZombie = enemiesList.get(i);
			}
		}
		distanceToZombie = distance;
		return closestZombie;
	}
	
	private double getAngleToZombie(Enemy zombie) {
		if(zombie != null) {
			shoot = true;
			return Zombies.angleBetweenRads(new Vector2(getPositionX(), getPositionY()),
				new Vector2(zombie.getPositionX(), zombie.getPositionY()));
			
		}
		shoot = false;
		return angleToZombieRadians;
	}
	public int getPositionX() {
		return (int) (body.getPosition().x * Zombies.PhysicsDensity);
	}

	public int getPositionY() {
		return (int) (body.getPosition().y * Zombies.PhysicsDensity);
	}

	public Body getBody() {
		return body;
	}

	public double getAngleRadians() {
		return angleToZombieRadians;
	}
	
	public double getAngleDegrees() {
		return angleDegrees;
	}
	
	public void render() {
		sprite.draw(spriteBatch);
	}

}
