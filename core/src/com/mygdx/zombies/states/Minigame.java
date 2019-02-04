package com.mygdx.zombies.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
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
import com.mygdx.zombies.CustomContactListener;
import com.mygdx.zombies.Enemy;
import com.mygdx.zombies.Entity;
import com.mygdx.zombies.Gate;
import com.mygdx.zombies.NPC;
import com.mygdx.zombies.PickUp;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.items.Projectile;
import com.mygdx.zombies.items.RangedWeapon;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Minigame extends State {

	private ArrayList<Enemy> enemiesList;
	private ArrayList<Projectile> bulletsList;
	private World box2dWorld;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	private ArrayList<PointLight> lightsList;
	private ArrayList<Gate> gatesList;
	private String path;
	private Box2DDebugRenderer box2DDebugRenderer;
	private int WaveCount = 1;
	
	/**
	 * Constructor for the level
	 * 
	 */
	public Minigame(String path) {
		super();
		
		this.path = path;
		
		bulletsList = new ArrayList<Projectile>();
		enemiesList = new ArrayList<Enemy>();
		
		String mapFile = String.format("stages/%s.tmx", path);
		map = new TmxMapLoader().load(mapFile);
		renderer = new OrthogonalTiledMapRenderer(map, Zombies.WorldScale);

		box2dWorld = new World(new Vector2(0, 0), true);
		box2DDebugRenderer = new Box2DDebugRenderer();

		MapBodyBuilder.buildShapes(map, Zombies.PhysicsDensity / Zombies.WorldScale, box2dWorld);
						
		initLights();			
								
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		box2dWorld.setContactListener(new CustomContactListener());
		
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
		camera.zoom  = 3;
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
		
		//Check if wave is finished
		if(enemiesList.size() == 0) {
			WaveCount += 1;
			spawnZombies();
		}
		
		//Update the camera position
		camera.position.set(Zombies.InitialViewportWidth / 2 + 400, Zombies.InitialViewportHeight / 2 + 700, 0);
		camera.update();
		
		//Update Box2D physics
		box2dWorld.step(1 / 60f, 6, 2);
		
		//Update mobs
		for(int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).update(this.inLights());			

		
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
	
	private void spawnZombies() {
		for(int i = 0; i < WaveCount; i++) {
			//spawn zombie
		}
	}
}

