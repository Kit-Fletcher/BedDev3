package com.mygdx.zombies.items;

/**
 * Power up class for storing power up attributes
 */
public class PowerUp {
	
	private int speedBoost;
	private int healthBoost;
	private int stealthBoost;
	private int maxHealth;
	private static boolean shieldBoost;
	
	/**
	 * The constructor for the power up
	 * @param speedBoost - the extra speed to give to the player
	 * @param healthBoost - the amount of health to give to the player
	 * @param stealthBoost - the stealth boost to give to the player
	 * @param maxHealth - the amount of extra health the player can have compared to the starting max
	 * @param shieldBoost - Player is invincible when true
	 */
	public PowerUp(int speedBoost, int healthBoost, int stealthBoost, int maxHealth, boolean shieldBoost) {
		this.speedBoost = speedBoost+1;
		this.healthBoost = healthBoost;
		this.stealthBoost = stealthBoost;
		this.maxHealth = maxHealth;
		this.shieldBoost = shieldBoost;
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
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
	
	public static boolean getShieldBoost() {
		return shieldBoost;
	}
	
	public void setShieldBoost(boolean shieldBoost) {
		this.shieldBoost = shieldBoost;
	}
}
