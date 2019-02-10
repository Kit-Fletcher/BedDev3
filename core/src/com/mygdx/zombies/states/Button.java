package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.states.State;

/**
 * Button class with two variants: 1) standard button   2) updating mode button
 */
public class Button {

	private SpriteBatch spriteBatch;
	private Sprite mainSprite;
	private Sprite hoverSprite;
	private int positionX;
	private int positionY;
	private String text;
	private String path = null;
	private TiledMap map;
	private int mode;
	private boolean hide;
	private String[] modeTextArray;

	/**
	 * Constructor for the standard button variant
	 * @param spriteBatch -  the spriteBatch to draw the button to
	 * @param x - the x position of the button
	 * @param y - the y position of the button
	 * @param text - the button text
	 */
	public Button(SpriteBatch spriteBatch, int x, int y, String text) {
		this.text = text;	
		setup(spriteBatch, x, y);
	}
	
	public Button(SpriteBatch spriteBatch,String path,OrthogonalTiledMapRenderer renderer, int x, int y, String text) {
		this.text = text;	
		setup(spriteBatch,path, x, y);
		this.path = path;
		System.out.println("Renderer" + renderer.getViewBounds().x);
	}
	
	/**
	 * Constructor for the updating button variant
	 * @param spriteBatch -  the spriteBatch to draw the button to
	 * @param x - the x position of the button
	 * @param y - the y position of the button
	 * @param modeTextArray - the array of text strings to use
	 */
	public Button(SpriteBatch spriteBatch, int x, int y, String[] modeTextArray) {
		mode = 0;
		text = modeTextArray[mode];
		
		this.modeTextArray = modeTextArray;
		setup(spriteBatch, x, y);
	}
	
	/**
	 * Setup sprites
	 * @param spriteBatch -  the spriteBatch to draw the button to
	 * @param x - the x position of the button
	 * @param y - the y position of the button
	 */
	private void setup(SpriteBatch spriteBatch, int x, int y) {
		this.spriteBatch = spriteBatch;
		
		//Load textures and set up sprites
		mainSprite = new Sprite(new Texture(Gdx.files.internal("button.jpg")));
		mainSprite.setPosition(x, y);
		hoverSprite = new Sprite(new Texture(Gdx.files.internal("hover_button.jpg")));
		hoverSprite.setPosition(x, y);
		
		positionX = x;
		positionY = y;
	}
	
	private void setup(SpriteBatch spriteBatch,String path, int x, int y) {
		this.spriteBatch = spriteBatch;
		//Load textures and set up sprites
		mainSprite = new Sprite(new Texture(Gdx.files.internal("minigame/" + path)));
		mainSprite.setPosition(x, y);
		hoverSprite = new Sprite(new Texture(Gdx.files.internal("minigame/hover" + path)));
		hoverSprite.setPosition(x, y);
		
		positionX = x;
		positionY = y;
	}


	
	/**
	 * Go to the next mode and display the associated text string, only works if updating variant
	 */
	public void nextMode() {
		mode++;
		if(mode >= modeTextArray.length) {
			mode = 0;
		}
		text = modeTextArray[mode];
	}
	
	/**
	 * @return get the current mode
	 */
	public int getMode() {
		return mode;
	}
	
	
	/**
	 * @param mode - set the current mode, updating the display text accordingly
	 */
	public void setMode(int mode) {
		this.mode = mode;
		text = modeTextArray[mode];
	}

	/**
	 * @return true if the mouse is hovering over the button
	 */
	public boolean isHover() {
		if(!hide) {
			float adjustedMouseX;
			float adjustedMouseY;
			//Return if the mouse is in the button rectangle
			if(this.path != null){
				adjustedMouseX = (Gdx.input.getX()-342)*3.5f;
				adjustedMouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()-14)*3.5f;
//				System.out.println("hi");
//				adjustedMouseX *= Zombies.WorldScale;
//				adjustedMouseX +=
//				adjustedMouseY *= Zombies.WorldScale;
//				adjustedMouseY +=
			}else{
			// Adjust mouse coordinates in case the window is resized
				adjustedMouseX = Gdx.input.getX() * Zombies.InitialWindowWidth / (float) Gdx.graphics.getWidth();
				adjustedMouseY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * Zombies.InitialWindowHeight
						/ (float) Gdx.graphics.getHeight();
			}
			return mainSprite.getBoundingRectangle()
					.contains(adjustedMouseX, adjustedMouseY);
		}else {
			return false;
		}
	}

	/**
	 * Draw the button to the screen
	 */
	public void render() {	
		if(!hide) {
			//Draw sprite
			if (isHover())
				hoverSprite.draw(spriteBatch);
			else
				mainSprite.draw(spriteBatch);
			//Draw text
			Zombies.mainFont.draw(spriteBatch, text, (float) ((positionX + 148) - (text.length() * 14)), positionY + 69);
	
		}
	}
	
	/**
	 * Clean up the memory and dispose
	 */
	public void dispose() {
		mainSprite.getTexture().dispose();
		hoverSprite.getTexture().dispose();
	}
	public void change() {
		hide = !hide;
	}
	
	public boolean getHide() {
		return hide;
	}
	public int getX() {
		return (int) mainSprite.getX();
	}
	public int getY() {
		return (int) mainSprite.getY();
	}
	public int getWidth() {
		return (int) mainSprite.getWidth();
	}
}
