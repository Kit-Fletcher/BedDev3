package com.mygdx.zombies.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.zombies.Entity;
import com.mygdx.zombies.InfoContainer;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.Level;

/**
 * Projectile class for moving bullets, missiles, arrows etc. which inflict damage on enemies
 */
public class Projectile extends Entity {

	private SpriteBatch spriteBatch;
	private Sprite sprite;

	/**
	 * Constructor for the projectile
	 * @param level - the level instance to spawn the bullet in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 * @param angle - the angle to move
	 * @param spritePath - the file name of the sprite to use
	 * @param speed - the speed that the projectile moves
	 */
	public Projectile(Level level, int x, int y, float angle, String spritePath, float speed) {
		
		//Apply bullet spray
		angle += Zombies.random.nextFloat()*0.2f-0.1f;
		
		//Add sprite
		this.spriteBatch = level.getWorldBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
		sprite.setRotation((float)Math.toDegrees(angle));

		//Build box2d body
		FixtureDef fixtureDef = new FixtureDef() {
			{
				density = 200;
				friction = 1;
				restitution = 1;
				filter.categoryBits = Zombies.playerFilter;
				isSensor = true;
			}
		};
		GenerateBodyFromSprite(level.getBox2dWorld(), sprite, InfoContainer.BodyID.PROJECTILE, fixtureDef);
		body.setTransform(x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, angle);
		body.setBullet(true);
		body.setFixedRotation(true);

		//Apply movement
		body.applyLinearImpulse((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed,
				x / Zombies.PhysicsDensity, y / Zombies.PhysicsDensity, true);
	}

	/**
	 * Render method to draw projectile sprite to screen
	 */
	public void render() {
		sprite.setPosition(body.getPosition().x * Zombies.PhysicsDensity - sprite.getWidth() / 2,
				body.getPosition().y * Zombies.PhysicsDensity - sprite.getWidth() / 2);
		sprite.draw(spriteBatch);
	}

	/**
	 * Dispose method to clear memory
	 */
	@Override
	public void dispose() {
		super.dispose();
		sprite.getTexture().dispose();
	}
}
