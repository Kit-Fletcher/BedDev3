package com.mygdx.zombies;

/**
 * Class for holding data used in collision handling and flag deletion
 */
public class InfoContainer {
	
	// Body IDs for collision identification
	public static enum BodyID {
		ZOMBIE, PLAYER, LEVEL, PROJECTILE, PICKUP, WEAPON, NPC, WALL, GATE
	}
	
	private BodyID type;
	private Object obj;
	private boolean deletionFlagged;
	
	/** Constructor for the InfoContainer class
	 * @param type - the body id of this object, used to identify the type of object in collisions
	 * @param obj - the object pointer to store in this object
	 */
	public InfoContainer(BodyID type, Object obj) {
		this.type = type;
		this.obj = obj;
		deletionFlagged = false;
	}

	public BodyID getType() {
		return type;
	}

	public Object getObj() {
		return obj;
	}
	
	/**
	 * Flag object so that it can be deleted imminently
	 */
	public void flagForDeletion() {
		deletionFlagged = true;
	}

	public boolean isDeletionFlagged() {
		return deletionFlagged;
	}
}
