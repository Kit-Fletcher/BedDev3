package com.mygdx.zombies.items;

import com.mygdx.zombies.states.Level;

/**
 * Abstract weapon interface for both ranged and melee weapons
 */
public interface Weapon {
	

	public abstract void use();
	
	/**
	 * Method to update weapon transformation
	 * @param x - the x position to move to
	 * @param y - the y position to move to
	 * @param rotation - the rotation to set
	 */
	public abstract void update(int x, int y, float rotation);
	
	/**
	 * Set the weapon level reference
	 * @param level - the level reference to change to
	 */
	public abstract void setLevel(Level level);
	
	/**
	 * Draw the weapon to the screen
	 */
	public abstract void render();
	
	/**
	 * Remove weapon clearing up memory
	 */
	public abstract void dispose();
}
