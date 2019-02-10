package com.mygdx.zombies.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Minigame extends State {

	private ArrayList<MinigameZombie> enemiesList;
	private ArrayList<Projectile> bulletsList;
	private ArrayList<Turret> turretList;
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
	private int health;
	private Sprite hud;
	private StateManager.StateID returnStage;
	
	/**
	 * Constructor for the level
	 * 
	 */
	public Minigame(String path, int spawnX, int spawnY, int health,StateManager.StateID returnStage) {
		super();
		box2dWorld = new World(new Vector2(0, 0), true);
		this.path = path;
		this.returnStage = returnStage;
		
		bulletsList = new ArrayList<Projectile>();
		enemiesList = new ArrayList<MinigameZombie>();
		turretList = new ArrayList<Turret>();
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
		
		this.health = health;
		hud = new Sprite(new Texture(Gdx.files.internal("player/heart.png")));
		
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		box2dWorld.setContactListener(new CustomContactListener());
		
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
					
					turretList.add(new Turret(this, x, y,"minigame/turret1.png", 10, "bullet.png", 20, Zombies.soundShoot,500));
				break;
				default:
					System.err.println("Error importing stage: unrecognised object");
				break;
			}
		}
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


		//Render world
		worldBatch.setProjectionMatrix(camera.combined);
		worldBatch.begin();							
		//Draw mobs and game objects
		
		for (int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).render();
		
		for (Projectile bullet : bulletsList)
			bullet.render();
		for (Turret turret : turretList)
			turret.render();
		worldBatch.end();
		
		//Render lighting
		rayHandler.render();
		
		//Render HUD
		UIBatch.begin();
		for (int i = 0; i < health; i++) {
			hud.setPosition(100 + i * 50, 620);
			hud.draw(UIBatch);
		}
		UIBatch.end();

		//Enable this line to show Box2D physics debug info
		//box2DDebugRenderer.render(box2dWorld, camera.combined.scl(Zombies.PhysicsDensity));
	}
	
	@Override
	public void update() {
		//Method to update everything in the state
		
		if(health <= 0) {
			StateManager.loadState(returnStage,-1);
		}
		
		//Check if wave is finished
		if(enemiesList.size() == 0) {
			waveCount += 1;
			spawnCount = waveCount * 3;
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
			switch (spawnCount % 3) {
				case 0 :
				enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie1.png", 5, 5));
				break;
			case 1 :
				enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie2.png", 3, 10));
				break;
			case 2 :
				enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie3.png", 4, 15));
				break;
			}
			spawnCount --;
			spawnDelay = 40;
		} else if(spawnDelay > 0) {
			spawnDelay --;
		}
	}
	
	public ArrayList<Projectile> getBulletsList() {
		return bulletsList;
	}
	
	public void loseHealth(int x) {
		health -= x;
	}
}

