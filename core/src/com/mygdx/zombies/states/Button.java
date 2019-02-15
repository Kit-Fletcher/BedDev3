package com.mygdx.zombies.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.zombies.Zombies;


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
	/**
	 * Constructor for minigame button variants
	 * @param spriteBatch - SpriteBatch to draw the button to
	 * @param path - file path of button sprite to use 
	 * @param x - the x position of the button
	 * @param y - the y position of the button
	 * @param text - any text to be displayed on the button
	 */
	public Button(SpriteBatch spriteBatch,String path, int x, int y, String text) {
		this.text = text;	
		
		setup(spriteBatch,path, x, y);
		this.path = path;
		
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
	/**
	 * setup sprites
	 * @param spriteBatch -  the spriteBatch to draw the button to
	 * @param path - the file path of button sprite to use 
	 * @param x - the x position of the button
	 * @param y - the y position of the button
	 */
	private void setup(SpriteBatch spriteBatch,String path, int x, int y) {
		this.spriteBatch = spriteBatch;
		//Load textures and set up sprites
		if(path == "") {
			//if no path then use a regular type button
			mainSprite = new Sprite(new Texture(Gdx.files.internal("minigame/button.png")));
			mainSprite.setPosition(x- (mainSprite.getWidth()/2)* Zombies.InitialWindowWidth / (float) Gdx.graphics.getWidth(), y);
			hoverSprite = new Sprite(new Texture(Gdx.files.internal("minigame/hoverbutton.png")));
			hoverSprite.setPosition(x- (mainSprite.getWidth()/2)* Zombies.InitialWindowWidth / (float) Gdx.graphics.getWidth(), y);
			// Calculates x so the buttons appear in nice places on screen
			positionX = (int) (x- (mainSprite.getWidth()/2)* Zombies.InitialWindowWidth / (float) Gdx.graphics.getWidth());
		}else {
			mainSprite = new Sprite(new Texture(Gdx.files.internal("minigame/" + path)));
			mainSprite.setPosition(x, y);
			hoverSprite = new Sprite(new Texture(Gdx.files.internal("minigame/hover" + path)));
			hoverSprite.setPosition(x, y);
			positionX = x;
			
		}
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
				//Only called during minigame (Zoomed out map)
				//adjusts the mouse to fit zoom by making the mouse and maps coordinates sync up
				adjustedMouseX = (Gdx.input.getX()-(Gdx.graphics.getWidth()/2 -(686/2)))*3.5f;
				adjustedMouseY = (Gdx.graphics.getHeight()- Gdx.input.getY()-(Gdx.graphics.getHeight()/2 -(686/2)))*3.5f;
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
			if(path==null) {
				Zombies.mainFont.draw(spriteBatch, text, (float) ((positionX + 148) - (text.length() * 14)), positionY + 69);
			}else {
				//accounts for the bigger buttons in minigames
				Zombies.mainFont.draw(spriteBatch, text, (float) ((positionX + 175) - (text.length() * 14)), positionY + 69);
			}
		}
	}
	
	/**
	 * Clean up the memory and dispose
	 */
	public void dispose() {
		mainSprite.getTexture().dispose();
		hoverSprite.getTexture().dispose();
	}
	
	/**
	 * swaps the button from hidden to visible and vice versa
	 */
	public void change() {
		hide = !hide;
	}
	
	/**
	 * 
	 * @return hide - the value representing whether the button is hidden
	 */
	public boolean getHide() {
		return hide;
	}
	//return sprite values
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
