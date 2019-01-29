package com.mygdx.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.zombies.states.Level;
import com.mygdx.zombies.states.StateManager;
import com.mygdx.zombies.states.StateManager.StateID;

/**
 * Boss1 enemy class. This is a more powerful enemy which spawns 'minion' zombies
 */
public class Boss1 extends Enemy {
	
	private Sprite armLeft;
	private Sprite armRight;
	private SpriteBatch spriteBatch;
	private int attackStep;
	private int minionSpawnStep;
	private Level level;

	/**Constructor for the first boss mob
	 * @param level - the level instance to spawn the mob in
	 * @param x - the x spawn coordinate
	 * @param y - the y spawn coordinate
	 */
	public Boss1(Level level, int x, int y) {	
		super(level, x, y, "zombie/boss1_head.png", 2, 30);
		
		this.level = level;
		spriteBatch = level.getWorldBatch();
		
		//Initialise arm sprites
		armLeft = new Sprite(new Texture(Gdx.files.internal("zombie/boss1_armLeft.png")));
		armRight = new Sprite(new Texture(Gdx.files.internal("zombie/boss1_armRight.png")));
		
		//Initialise timer values
		attackStep = 0;
		minionSpawnStep = 0;
	}
	
	@Override
	public void update(boolean inLights) {
		super.update(inLights);
		
		//Looping hand animation timer 
		attackStep+=3;
		if(attackStep > 100)
			attackStep = 0;
		
		//Looping timer for spawning minion zombies
		minionSpawnStep++;
		if(minionSpawnStep > 160) {
			minionSpawnStep = 0;	
			if(level.getEnemiesList().size() < 20) {
				Enemy minion =
						new Enemy(level, getPositionX(), getPositionY(),"zombie/zombie1.png", 12, 5);
				level.getEnemiesList().add(minion);
			}
		}
		
		//Get the position of the left and right hands
		Vector2 leftHandPos = getLeftHandPosition();
		Vector2 rightHandPos = getRightHandPosition();
		//Set the sprites to the hand positions
		armLeft.setPosition(getPositionX() + leftHandPos.x, getPositionY() + leftHandPos.y);
		armLeft.setRotation((float) angleDegrees);
		armRight.setPosition(getPositionX() + rightHandPos.x, getPositionY() + rightHandPos.y);
		armRight.setRotation((float) angleDegrees);
	}
	
	/**
	 * @return the position of the left hand, dependent on the current attackStep
	 */
	private Vector2 getLeftHandPosition() {	
		//Angle is body angle minus an offset
		double leftHandAngle = angleRadians - 0.2f;
		float x = (float)Math.toDegrees(Math.cos(leftHandAngle))*-attackStep/60;
		float y = (float)Math.toDegrees(Math.sin(leftHandAngle))*-attackStep/60;
		
		return new Vector2(x-sprite.getWidth()/2, y);
	}
	
	/**
	 * @return the position of the right hand, dependent on the current attackStep
	 */
	private Vector2 getRightHandPosition() {	
		//Angle is body angle plus an offset
		double rightHandAngle = angleRadians + 0.2f;
		float x = (float)Math.toDegrees(Math.cos(rightHandAngle))*+(attackStep-60)/60;
		float y = (float)Math.toDegrees(Math.sin(rightHandAngle))*+(attackStep-60)/60;
		
		return new Vector2(x-sprite.getWidth()/2, y);
	}
	
	@Override
	public void dispose() {
		//Clean up memory and dispose of entity
		super.dispose();
		armLeft.getTexture().dispose();
		armRight.getTexture().dispose();
		//Load win screen now this enemy is killed
		StateManager.loadState(StateID.ENDSCREEN);
	}
		
	@Override
	public void render() {
		//Call parent render method to draw main sprite
		super.render();
		//Draw hands
		armLeft.draw(spriteBatch);
		armRight.draw(spriteBatch);
	}
}
