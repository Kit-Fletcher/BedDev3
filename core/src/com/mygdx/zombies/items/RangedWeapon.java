package com.mygdx.zombies.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.states.Level;

/**
 * Ranged weapon class which fires projectiles
 */
public class RangedWeapon implements Weapon {

	private int shootDelay;
	private int timerTicks;
	private Level level;
	private String projectileSpritePath;
	private Sound shootSound;
	private float bulletSpeed;
	private static boolean firing;
	
	/**
	 * Constructor for the ranged weapon
	 * @param level - the level to create the weapon in
	 * @param shootDelay - the reload delay between firing projectiles
	 * @param projectileSpritePath - the file name of the fired projectile sprite
	 * @param bulletSpeed - the speed of the fired projectile
	 * @param shootSound - the shooting sound
	 */
	public RangedWeapon(Level level, int shootDelay, String projectileSpritePath, float bulletSpeed, Sound shootSound) {
		
		this.level = level;		
		this.shootDelay = shootDelay;
		this.projectileSpritePath = projectileSpritePath;
		this.shootSound = shootSound;
		this.bulletSpeed = bulletSpeed;
		
		//Initialise shoot timing values
		timerTicks = 0;
		firing = false;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	/**Method to check whether the weapon is firing
	 * @return true if the ranged weapon is firing
	 */
	public boolean isFiring() {
		return firing;
	}
	
	/**
	 * Fires a projectile, if the weapon is loaded
	 */
	@Override
	public void use() {
		if(timerTicks == 0) {
			timerTicks++;
			Vector2 pos = level.getPlayer().getHandsPosition();
			level.getBulletsList().add(new Projectile(level, (int)pos.x + level.getPlayer().getPositionX(), (int)pos.y + level.getPlayer().getPositionY(),
					(float)(level.getPlayer().getAngleRadians() + Math.PI/2), projectileSpritePath, bulletSpeed));
			firing = true;
			shootSound.play();
		}
	}
	
	/**
	 * Method to update shoot timer
	 */
	@Override
	public void update(int x, int y, float rotation) {
		
		if(timerTicks > 0)
			timerTicks++;
		if(timerTicks >= shootDelay) {
			timerTicks = 0;
			firing = false;
		}
	}

	@Override
	public void render() {
	}
	
	@Override
	public void dispose() {		
	}
}
