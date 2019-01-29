package com.mygdx.zombies;

import java.util.Random;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.mygdx.zombies.states.StateManager;

/**
 * Base class for the game
 */
public class Zombies extends Game {

	public static int InitialWindowWidth = 1280;
	public static int InitialWindowHeight = 720;
	public static int InitialViewportWidth = 1280;
	public static int InitialViewportHeight = 720;
	public static String windowTitle = "Silence of the Lamberts";
	public static float WorldScale = 1.5f;
	public static float PhysicsDensity = 100;
	public static BitmapFont mainFont;
	public static BitmapFont titleFont;
	public static BitmapFont pointsFont;
	public static BitmapFont creditsFont;
	public static Sound soundShoot;
	public static Sound soundSelect;
	public static Sound soundLaser;
	public static Sound soundSwing;
	public static Sound soundAmmo;
	public static Sound soundPowerUp;
	public static Sound soundAmbientWind;
	public static Sound soundEndMusic;
	public static Sound[] soundArrayZombie;
	public static Random random;
	private StateManager stateManager;

	// Collision masks. Can OR these together to combine.
	// Use categoryBits (1 default) and mask bits (-1 default)
	// (maskBitsA & categoryBitsB) && (categoryBitsA & maskBitsB);
	public static short playerFilter = 2;
	public static short projectileFilter = 1;
	

	/** Generate a BitmapFont using the given parameters
	 * @param name - the filename of the true type font
	 * @param size - the font size
	 * @return - the generated BitmapFont
	 */
	public static BitmapFont generateFont(String name, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(name));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}
	
	/** Calculate the angle between two points
	 * @param p1 - the first point
	 * @param p2 - the second point
	 * @return - the angle in radians
	 */
	public static double angleBetweenRads(Vector2 p1, Vector2 p2) {
		double diffx = p1.x - p2.x;
		double diffy = p1.y - p2.y;
		return Math.atan2(diffy, diffx);
	}
	
	/** Pythagorean algorithm to calculate the distance between two points
	 * @param p1 - the first point
	 * @param p2 - the second point
	 * @return - the distance between the two points
	 */
	public static double distanceBetween(Vector2 p1, Vector2 p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}

	/*
	 * Method called on game start
	 */
	@Override
	public void create() {
		//Initialise Box2D physics enginw
		Box2D.init();
		//Create new random number generator
		random = new Random();
		
		//Create statemanager
		stateManager = new StateManager();
		
		//Generate fonts and store in memory
		mainFont = Zombies.generateFont("NESCyrillic.ttf", 55);
		titleFont = Zombies.generateFont("Amatic-Bold.ttf", 150);
		pointsFont = Zombies.generateFont("KaushanScript-Regular.otf", 50);
		creditsFont = Zombies.generateFont("SourceSansPro-Regular.otf", 50);
		
		//Load sounds into memory
		soundShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/gun.wav"));
		soundSwing = Gdx.audio.newSound(Gdx.files.internal("sounds/swing.wav"));
		soundSelect = Gdx.audio.newSound(Gdx.files.internal("sounds/select.wav"));
		soundLaser = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
		soundAmmo = Gdx.audio.newSound(Gdx.files.internal("sounds/ammo.wav"));
		soundPowerUp = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
		soundAmbientWind = Gdx.audio.newSound(Gdx.files.internal("sounds/wind.mp3"));
		soundEndMusic = Gdx.audio.newSound(Gdx.files.internal("sounds/alligator_crawl.mp3"));
		
		soundArrayZombie = new Sound[8];
		for(int i = 0; i<soundArrayZombie.length; i++)
			soundArrayZombie[i] = Gdx.audio.newSound(Gdx.files.internal(String.format("sounds/zombie%d.wav", i+1)));
	}

	/*
	 * Method to perform a single render call
	 */
	@Override
	public void render() {
		//Update current state of StateManager
		stateManager.gameLoop();
		//Set default background colour
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Render current state of StateManager
		stateManager.render();
	}

	/*
	 * Run when the game is closed, clearing the memory
	 */
	@Override
	public void dispose() {
		stateManager.dispose();
		mainFont.dispose();
		titleFont.dispose();
		pointsFont.dispose();
		creditsFont.dispose();
		soundShoot.dispose();
		soundSwing.dispose();
		soundSelect.dispose();
		soundLaser.dispose();
		soundAmmo.dispose();
		soundPowerUp.dispose();
		soundAmbientWind.dispose();
		soundEndMusic.dispose();
		for(Sound sound : soundArrayZombie)
			sound.dispose();
	}

	/*
	 * Run when the game menu is resized
	 */
	@Override
	public void resize(int width, int height) {
		stateManager.resize(width, height);
	}
}
