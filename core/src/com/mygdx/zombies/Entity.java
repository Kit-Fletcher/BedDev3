package com.mygdx.zombies;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Generic Entity class for handling safe flag deletion system
 */
public abstract class Entity {

	protected World box2dWorld;
	protected Body body;
	
	/** Generic method to generate a Box2D physics rectangle object based on given parameters
	 * @param dimens - the dimensions of the rectangle
	 * @param box2dWorld - the world to create the rectangle in
	 * @param bodyID - defines the types of object, used in collision events
	 * @param fixtureDef - the Box2D fixture definition to use
	 */
	public void GenerateBodyRectangle(Vector2 dimens, World box2dWorld, InfoContainer.BodyID bodyID, FixtureDef fixtureDef) {
		this.box2dWorld = box2dWorld;
		body = box2dWorld.createBody(new BodyDef() {
			{
				type = BodyDef.BodyType.DynamicBody;
			}
		});
		final PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(dimens.x, dimens.y);
		fixtureDef.shape = polyShape;
		body.createFixture(fixtureDef);
		body.setUserData(new InfoContainer(bodyID, this));
		polyShape.dispose();
	}
	
	/** Method to generate Box2D physics rectangle from a sprite
	 * @param box2dWorld - the world to create the rectangle in
	 * @param sprite - the sprite to build the rectangle around
	 * @param bodyID - defines the types of object, used in collision events
	 * @param fixtureDef - the Box2D fixture definition to use
	 */
	protected void GenerateBodyFromSprite(World box2dWorld, Sprite sprite, InfoContainer.BodyID bodyID, FixtureDef fixtureDef) {
		
		Vector2 dimens = new Vector2(sprite.getWidth() / 2 / Zombies.PhysicsDensity,
				sprite.getHeight() / 2 / Zombies.PhysicsDensity);
		GenerateBodyRectangle(dimens, box2dWorld, bodyID, fixtureDef);
	}
	
	/**
	 * Delete the entity, clearing the memory
	 */
	public void dispose() {
		box2dWorld.destroyBody(body);
	};
	
	/**
	 * @return the associated custom user data for this entity
	 */
	public InfoContainer getInfo() {
		return (InfoContainer)body.getUserData();
	}
	
	/** Remove any objects that are deletion flagged from the given list
	 * @param lst - the list to remove flagged objects from
	 */
	public static <T extends Entity> void removeDeletionFlagged(ArrayList<T> lst) {
		//Iterate through list
		for(int i = 0; i< lst.size(); i++) {
			T entity = lst.get(i);
			if(entity.getInfo().isDeletionFlagged()) {
				entity.dispose();
				lst.remove(entity);
				//Step index back by one to account for removal from list
				i--;
			}
		}
	}
	
	/** Check and remove this object only if it is deletion flagged
	 * @param entity - the entity to check
	 */
	public static <T extends Entity> void removeDeletionFlagged(T entity) {
			if(entity.getInfo().isDeletionFlagged()) {
				entity.dispose();
				entity = null;
			}	
	}
}
