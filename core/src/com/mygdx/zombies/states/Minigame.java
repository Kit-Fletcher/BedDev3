package com.mygdx.zombies.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.zombies.Boss1;
import com.mygdx.zombies.CustomContactListener;
import com.mygdx.zombies.Enemy;
import com.mygdx.zombies.Entity;
import com.mygdx.zombies.Gate;
import com.mygdx.zombies.InfoContainer;
import com.mygdx.zombies.NPC;
import com.mygdx.zombies.PickUp;
import com.mygdx.zombies.Player;
import com.mygdx.zombies.Turret;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.MinigameZombie;
import com.mygdx.zombies.items.MeleeWeapon;
import com.mygdx.zombies.items.PowerUp;
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.items.RangedWeapon;
import com.mygdx.zombies.states.StateManager.StateID;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Minigame extends State {

	private ArrayList<MinigameZombie> enemiesList;
	private ArrayList<Projectile> bulletsList;
	private ArrayList<Turret> turretList;
	private ArrayList<Button> placeList;
	private World box2dWorld;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	private ArrayList<PointLight> lightsList;
	private ArrayList<Gate> gatesList;
	public String path;
	private Box2DDebugRenderer box2DDebugRenderer;
	private int waveCount;
	private int spawnX, spawnY, spawnCount, spawnDelay;
	private Button turret1;
	private Button turret2;
	boolean tur1 = false;
	
	
	/**
	 * Constructor for the level
	 * 
	 */
	public Minigame(String path, int spawnX, int spawnY) {
		super();
		box2dWorld = new World(new Vector2(0, 0), true);
		this.path = path;
		
		bulletsList = new ArrayList<Projectile>();
		enemiesList = new ArrayList<MinigameZombie>();
		turretList = new ArrayList<Turret>();
		placeList = new ArrayList<Button>();
		String mapFile = String.format("stages/%s.tmx", path);
		map = new TmxMapLoader().load(mapFile);
		renderer = new OrthogonalTiledMapRenderer(map, Zombies.WorldScale);

		box2dWorld = new World(new Vector2(0, 0), true);
		box2DDebugRenderer = new Box2DDebugRenderer();

		MapBodyBuilder.buildShapes(map, Zombies.PhysicsDensity / Zombies.WorldScale, box2dWorld);
						
		initLights();			
		loadObjects();
		
		waveCount = 0;
		spawnCount = 0;
		spawnDelay = 0;		
		
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		box2dWorld.setContactListener(new CustomContactListener());
		turret1 = new Button(worldBatch,"turret1button.png", renderer, 1000, 1000, "");
		turret2 = new Button(worldBatch,"turret2button.png", renderer, 1000, 800, "");
		
		
	}
	/**
	 * Method to parse map objects, such as power ups, weapons, enemies and NPCs
	 */
	private void loadObjects() {
		
		//Get objects layer
		MapObjects objects = map.getLayers().get("Objects").getObjects();
		//turretList.add(new Turret(this,(int) (723* Zombies.WorldScale),(int)( 819*Zombies.WorldScale) ,"minigame/turret1.png", 15, "bullet.png", 20, Zombies.soundShoot));
		//Iterate objects
		for(MapObject object : objects) {
			
			//Retrieve properties
			MapProperties p = object.getProperties();
			int x = ((Float) p.get("x")).intValue();
			int y = ((Float) p.get("y")).intValue();
			
			//Scale coordinates
			x*= Zombies.WorldScale;
			y*= Zombies.WorldScale;

			//Added the object, using the name as an identifier
			switch(object.getName()) {
				case "zombie1":
					
					enemiesList.add(new MinigameZombie(this, x, y, "zombie/zombie1.png", 3, 5));
				break;
				
				case "zombie2":
					enemiesList.add(new MinigameZombie(this, x, y, "zombie/zombie2.png", 5, 15));
				break;
				
				case "zombie3":
					enemiesList.add(new MinigameZombie(this, x, y, "zombie/zombie3.png", 10, 5));
				break;
				case "turret1":
					
					turretList.add(new Turret(this, x, y,"minigame/turret1.png", 10, "bullet.png", 20, Zombies.soundShoot,700));
				break;
				case "placer":
					placeList.add(new Button(worldBatch, "selecter.png",renderer, x, y, ""));
				break;
				default:
					System.err.println("Error importing stage: unrecognised object");
				break;
			}
		
		}
		for (Button button : placeList)
			button.change();
	}
	private void initLights() {
		
		//Set up rayhandler
		rayHandler = new RayHandler(box2dWorld);
		rayHandler.setShadows(true);
		rayHandler.setAmbientLight(.4f);
		lightsList = new ArrayList<PointLight>();
		
		//Parse tiled map light objects
		MapObjects objects = map.getLayers().get("Lights").getObjects();
				
				for(MapObject object : objects) {
					
					//Get object properties
					MapProperties p = object.getProperties();
					int x = ((Float) p.get("x")).intValue();
					int y = ((Float) p.get("y")).intValue();
					
					x *=  Zombies.WorldScale;
					y *=  Zombies.WorldScale;
					
					
					Color color;
					int distance;
					
					//Set attributes based on light type
					switch(object.getName()) {
						case "street":
							color = Color.ORANGE;
							distance = 250;
							break;
						case "security":
							color = Color.CYAN;
							distance = 120;
							break;
						case "red":
							color = Color.FIREBRICK;
							distance = 80;
							break;
						case "torch":
							color = Color.GREEN;
							distance = 80;
							break;
						default:
							throw new IllegalArgumentException();
					}		
					
					//Add light to list
					lightsList.add(new PointLight(rayHandler, 20, color, distance, x, y));
				}
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width * Zombies.InitialViewportWidth / (float) Zombies.InitialViewportWidth;
		camera.viewportHeight = height;
		camera.zoom  = (float) 3.5;
		camera.update();
	}
	
	@Override
	public void render() {
		
		//Render map
		renderer.setView(camera);
		renderer.render();
		//System.out.println(Gdx.input.getX() + " " + Gdx.input.getY());

		//Render world
		worldBatch.setProjectionMatrix(camera.combined);
		worldBatch.begin();							
		//Draw mobs and game objects
		turret1.render();
		turret2.render();
		for (int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).render();
		
		for (Projectile bullet : bulletsList)
			bullet.render();
		for (Turret turret : turretList)
			turret.render();
		for (Button button : placeList)
			button.render();
		worldBatch.end();
		
		//Render lighting
		rayHandler.render();
		
		//Render HUD
		UIBatch.begin();
		//player.hudRender();
		UIBatch.end();

		//Enable this line to show Box2D physics debug info
		//box2DDebugRenderer.render(box2dWorld, camera.combined.scl(Zombies.PhysicsDensity));
	}
	
	@Override
	public void update() {
		
		//Method to update everything in the state
		//Buttons in screen add if it is hidden or not
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			if(turret1.isHover() && turretList.size()< waveCount) {
				tur1 = true;
				for (int i = 0; i < placeList.size(); i++)
					placeList.get(i).change();
			
			}
			if(turret2.isHover() && turretList.size()< waveCount-2 && !tur1) {
				for (int i = 0; i < placeList.size(); i++)
					placeList.get(i).change();
			
			}
			for (int i = 0; i < placeList.size(); i++)
				if(placeList.get(i).isHover() & !placeList.get(i).getHide()) {
					
					if(tur1) {
						turretList.add(new Turret(this, placeList.get(i).getX() +placeList.get(i).getWidth()/2, placeList.get(i).getY()+placeList.get(i).getWidth()/2, "minigame/turret1.png", 15, "bullet.png", 20, Zombies.soundShoot,700));
						tur1= false;
					}else {
						turretList.add(new Turret(this, placeList.get(i).getX() +placeList.get(i).getWidth()/2, placeList.get(i).getY()+placeList.get(i).getWidth()/2, "minigame/turret2.png", 10, "laser.png", 50, Zombies.soundLaser,500));
					}
					placeList.remove(i);
					i --;
					for (int l = 0; l < placeList.size(); l++)
						placeList.get(l).change();
					break;
			}
//			if (play.isHover()) {
//				Zombies.soundSelect.play();
//				//Start playing ambient sound
//				Zombies.soundAmbientWind.loop();
//				StateManager.loadState(StateID.BRIEFINGSCREEN);
//			}
//			else if (credits.isHover()) {
//				Zombies.soundSelect.play();
//				StateManager.loadState(StateID.CREDITSMENU);
//			}
//			else if (options.isHover()) {
//				Zombies.soundSelect.play();
//				StateManager.loadState(StateID.OPTIONSMENU);
//			}
//			else if (exit.isHover()) {
//				//Quit the game
//				Gdx.app.exit();
//			}
		}
		//Check if wave is finished
		if(enemiesList.size() == 0) {
			waveCount += 1;
			spawnCount = waveCount * 2;
		}
		
		spawnZombies();
		
		//Update the camera position
		camera.position.set(Zombies.InitialViewportWidth / 2 + 400, Zombies.InitialViewportHeight / 2 + 850, 0);
		camera.update();
		
		//Update Box2D physics
		box2dWorld.step(1 / 60f, 6, 2);
		
		//Update mobs
		for(int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).update(this.inLights());			
		for (Turret turret : turretList)
			turret.update();
		
		//Remove deletion flagged objects
		Entity.removeDeletionFlagged(enemiesList);
		Entity.removeDeletionFlagged(bulletsList);



		//Update Box2D lighting
		rayHandler.setCombinedMatrix(camera);
		rayHandler.update();

	}
	
	public boolean inLights() {
		//Iterate through lights
		for (PointLight light : lightsList)
			//Calculate distance between each light and player
			if (Zombies.distanceBetween(new Vector2(Zombies.InitialViewportWidth / 2, Zombies.InitialViewportHeight / 2), new Vector2(light.getX(), light.getY()))
					< light.getDistance())
				return true;
		return false;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		//Clean up memory
		rayHandler.dispose();
		renderer.dispose();
		map.dispose();
		//box2DDebugRenderer.dispose();
	}
	
	public World getBox2dWorld() {
		return box2dWorld;
	}
	
	public ArrayList<MinigameZombie> getEnemiesList() {
		return enemiesList;
	}
	
	private void spawnZombies() {
		
		if(spawnCount > 0 && spawnDelay == 0) {
			System.out.println("spawning");
			System.out.println(spawnX);
			System.out.println(spawnY);
			enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie1.png", 3, 5));
			spawnCount --;
			spawnDelay = 40;
		} else if(spawnDelay > 0) {
			spawnDelay --;
		}
	}
	
	public ArrayList<Projectile> getBulletsList() {
		return bulletsList;
	}
}

