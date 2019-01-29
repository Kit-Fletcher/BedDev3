package com.mygdx.zombies.items;

/**
 * Power up class for storing power up attributes
 */
public class PowerUp {
	
	private int speedBoost;
	private int healthBoost;
	private int stealthBoost;
	
	/**
	 * The constructor for the power up
	 * @param speedBoost - the extra speed to give to the player
	 * @param healthBoost - the amount of health to give to the player
	 * @param stealthBoost - the stealth boost to give to the player
	 */
	public PowerUp(int speedBoost, int healthBoost, int stealthBoost) {
		this.speedBoost = speedBoost+1;
		this.healthBoost = healthBoost;
		this.stealthBoost = stealthBoost;
	}

	public int getSpeedBoost() {
		return speedBoost;
	}
	
	public int getHealthBoost() {
		return healthBoost;
	}
	
	public int getStealthBoost() {
		return stealthBoost;
	}
}
