package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.items.MeleeWeapon;
import com.mygdx.zombies.items.PowerUp;
import com.mygdx.zombies.items.RangedWeapon;
import com.mygdx.zombies.items.Weapon;
import com.mygdx.zombies.states.Level;
import com.mygdx.zombies.states.StateManager;

/**
 * The main player class
 */
public class Player extends Entity {

	private static Float health;
	private static Float points;
	private static String pointDisplay;
	private Sprite sprite;
	private double angleRads;
	private float angleDegrees;
	private long timer;
	private long last;
	private Sprite hud;
	private SpriteBatch spriteBatch;
	private SpriteBatch UIBatch;
	private byte swingStep;
	private byte swingDirection;
	private PowerUp powerUp;
	private static Weapon weapon;
	private static int playerNumber;
	private int charStealth;
	private int charSpeed;
	private float charDamage;
	private static Texture equippedTexture;
	private static Texture unequippedTexture;
	private static Level level;

	/** Constructor for the player class
	 * @param level - the level instance to spawn the player in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 */
	public Player(Level level, int x, int y) {
		spriteBatch = level.getWorldBatch();
		UIBatch = level.getUIBatch();
		Player.level = level;

		//Initialise player attributes to default values
		charDamage = charSpeed = charStealth = 1;
		
		//Set player attributes based on the current playerNumber
		switch(playerNumber) {
			case 1:
				charDamage = 0.5f;
				break;
			case 2:
				charStealth = 2;
				break;
			case 3:
				charSpeed = 2;
				break;
		}
		
		//Initialise player health if not yet set in previous stage
		if(Player.health == null || Player.health <= 0) {
			Player.health = 10.f;
		}
		
		//Initialise points if not yet set in previous stage
		if(points == null) {
			points = 18110.f;
			pointDisplay = "18110";
		}

		//Load player textures
		loadTextures();
		hud = new Sprite(new Texture(Gdx.files.internal("player/heart.png")));
		sprite = new Sprite(unequippedTexture);
		
	
		//Update texture if set
		if(weapon != null) {
			weapon.setLevel(level);
			if(weapon instanceof RangedWeapon) {
				setEquippedTexture();
			}		
		}
	
		//Generate Box2D object
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 40;
				friction = 0.5f;
				restitution = 0f;
				filter.maskBits = Zombies.projectileFilter;
				filter.categoryBits = Zombies.playerFilter;
			}
		};		
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.PLAYER, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, 0);
		body.setLinearDamping(20);
		body.setFixedRotation(true);

		//Initialise variables for the arm swinging capability
		swingStep = 0;
		swingDirection = -1;	
	}
	
	/**
	 * Loads player textures based on playerNumber
	 */
	private static void loadTextures() {
		equippedTexture = new Texture(Gdx.files.internal("player/player" + String.valueOf(playerNumber) + "_equipped.png"));
		unequippedTexture = new Texture(Gdx.files.internal("player/player" + String.valueOf(playerNumber) + "_unequipped.png"));
	}
	
	/** Set the player type and update textures accordingly
	 * @param playerNumber - the number representing the player type
	 */
	public static void setPlayerType(int playerNumber) {
		Player.playerNumber = playerNumber;
		loadTextures();
	}
	
	/**
	 * Show the weapon equipped texture
	 */
	private void setEquippedTexture() {
		sprite.setTexture(equippedTexture);
	}
	
	/**
	 * Show the weapon unequipped texture
	 */
	private void setUnequippedTexture() {
		sprite.setTexture(unequippedTexture);
	}
	
	/**Set the current weapon, updating the player texture appropriately
	 * @param weapon - the weapon to equip
	 */
	public void setWeapon(Weapon weapon) {
		//If weapon is melee type, do not use equipped texture
		if(weapon instanceof MeleeWeapon)
			setUnequippedTexture();
		else
			setEquippedTexture();
		//Play equip sound
		Zombies.soundAmmo.play();
		Player.weapon = weapon;
	}

	/**
	 * @return true if player has a weapon, false otherwise
	 */
	public boolean hasWeapon() {
		return weapon != null;
	}
	
	/**
	 * @return the relative rotation of the player's hands in degrees
	 */
	public float getHandsRotation() {
		return swingStep*15;
	}
	
	/**
	 * @return the relative position of the player's hands
	 */
	public Vector2 getHandsPosition() {				
		double rot = angleRads + swingStep/3.f;
		float x = (float)Math.toDegrees(Math.cos(rot))*0.5f;
		float y = (float)Math.toDegrees(Math.sin(rot))*0.5f;
		
		return new Vector2(x, y);
	}
	
	/**
	 * Update the number of points and the display value
	 * The points should gradually decrease at a varying speed
	 */
	private void updatePoints() {		
		timer = System.nanoTime()/1000000000;	
		
		if (timer % 2 == 0 && timer != last) {							
			points -= Math.round(Math.random() * 100);		
			
			if(points <= 0) {
				points = (float) 0;
			}	
			
			pointDisplay = Integer.toString(Math.round(points));		
			last = timer;
		}
	}	

	/**
	 * @return the amount of noise generated by the player accounting for stealth and speed
	 */
	public double getNoise() {
		int stealth = powerUp==null ? 1 : powerUp.getStealthBoost()+1;
		double noise = body.getLinearVelocity().len() / (stealth*charStealth) * 250;
		
		//If current weapon is a ranged weapon and is firing, make a lot of noise
		if(weapon instanceof RangedWeapon) {
			RangedWeapon rweapon = (RangedWeapon)weapon;
			if(rweapon.isFiring()) {
				noise += 2000;
			}
		}
		
		return noise;
	}

	/**
	 * @param mouseCoords - the mouse coordinates to look towards
	 */
	private void look(Vector3 mouseCoords) {
		//Calculate angle between player and mouse
		angleRads = Zombies.angleBetweenRads(new Vector2(getPositionX(), getPositionY()),
				new Vector2(mouseCoords.x, mouseCoords.y)) + Math.PI/2;
		angleDegrees = (float) Math.toDegrees(angleRads);
		//Update sprite rotation
		sprite.setRotation(angleDegrees);
	}

	/**
	 * Poll keyboard and move player according to presses
	 * Takes into account player speed power up and speed attribute
	 */
	private void move() {
		int speedBoost = powerUp==null ? 1 : powerUp.getSpeedBoost();
		Vector2 playerPosition = body.getPosition();
		
		if (Gdx.input.isKeyPressed(Keys.W))
			body.applyLinearImpulse(new Vector2(0, 25*speedBoost*charSpeed), playerPosition, true);
		else if (Gdx.input.isKeyPressed(Keys.S))
			body.applyLinearImpulse(new Vector2(0, -25*speedBoost*charSpeed), playerPosition, true);

		if (Gdx.input.isKeyPressed(Keys.A))
			body.applyLinearImpulse(new Vector2(-25*speedBoost*charSpeed, 0), playerPosition, true);
		else if (Gdx.input.isKeyPressed(Keys.D))
			body.applyLinearImpulse(new Vector2(25*speedBoost*charSpeed, 0), playerPosition, true);
	}
	
	/**
	 * @return true if hands are moving
	 */
	public boolean isSwinging() {
		return weapon instanceof MeleeWeapon && swingStep > 0 && swingStep < 10;
	}

	/** Updates player position, rotation, score and weaponry
	 * @param mouseCoords - the mouse coordinates to rotate the player towards
	 */
	public void update(Vector3 mouseCoords) {
		
		if (weapon != null) {
			Vector2 h = getHandsPosition();
			Vector2 pos = new Vector2(getPositionX() + h.x, getPositionY() + h.y);
			if(weapon instanceof MeleeWeapon)
				swingUpdate();
			else
				swingStep=5;
			weapon.update((int)(pos.x),
					(int)(pos.y), angleDegrees+getHandsRotation());
			if (Gdx.input.isButtonPressed(Buttons.LEFT))
				weapon.use();
		}			
		
		move();
		look(mouseCoords);
		updatePoints();
		
		sprite.setPosition(getPositionX() - sprite.getWidth() / 2, getPositionY() - sprite.getHeight() / 2);
		updatePoints();
	}

	/**
	 * Method to deal with hand movement update
	 */
	private void swingUpdate() {
		if(isSwinging())
			swingStep += swingDirection;
		else {
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					Zombies.soundSwing.play();
					swingDirection *= -1;
					swingStep+=swingDirection;	
			}
		}		
	}

	/**
	 * Draw the player sprite and weapon
	 */
	public void render() {
		sprite.draw(spriteBatch);
		
		if(weapon != null) {
			weapon.render();
		}
	}

	/**
	 * Draw the player HUD
	 */
	public void hudRender() {
		Zombies.pointsFont.draw(UIBatch, pointDisplay, 100, 590);

		for (int i = 0; i < health; i++) {
			hud.setPosition(100 + i * 50, 620);
			hud.draw(UIBatch);
		}
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
		return angleRads;
	}
	
	public double getAngleDegrees() {
		return angleDegrees;
	}

	/** Give the player a power up, replacing the previous one
	 * @param powerUp - the power up to apply to the player
	 */
	public void setPowerUp(PowerUp powerUp) {
		Zombies.soundPowerUp.play();
		this.powerUp = powerUp;
		health += powerUp.getHealthBoost();
	}

	public float getHealth() {
		return health;
	}

	/** Sets the health to the given value, restarting the current level if depleted
	 * @param health - the value to set the health to
	 */
	public void setHealth(float health) {
		Player.health = health;
		//Restart current level from last entry point if health depleted
		if(health <= 0)
			StateManager.loadState(new Level(level.getPath(), level.getSpawnEntryID()));
	}
	
	public float getDamage() {
		return charDamage;
	}
	
	/*
	 * Dispose of the player instance, clearing the memory
	 */
	public void dispose() {
		super.dispose();
		hud.getTexture().dispose();
		equippedTexture.dispose();
		unequippedTexture.dispose();
	}
}
