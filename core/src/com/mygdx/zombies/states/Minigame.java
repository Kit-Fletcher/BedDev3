package com.mygdx.zombies.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.zombies.CustomContactListener;
import com.mygdx.zombies.Entity;
import com.mygdx.zombies.Turret;
import com.mygdx.zombies.Zombies;
import com.mygdx.zombies.MinigameZombie;
import com.mygdx.zombies.items.Projectile;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Minigame extends State {

	private ArrayList<MinigameZombie> enemiesList;
	private ArrayList<Projectile> bulletsList;
	private ArrayList<Turret> turretType1List;
	private ArrayList<Turret> turretType2List;
	private ArrayList<Button> placeList;
	private World box2dWorld;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private RayHandler rayHandler;
	private ArrayList<PointLight> lightsList;
	public String path;
	private Box2DDebugRenderer box2DDebugRenderer;
	private int waveCount, spawnX, spawnY, spawnCount, spawnDelay;
	private Button turretType1Btn, turretType2Btn;
	boolean turret1BtnClicked = false;
	int turret1AmountAllowed = 1;
	int turret2AmountAllowed = -2; //starts at minus 2 so that it becomes available on wave 3 
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
		turretType1List = new ArrayList<Turret>();
		turretType2List = new ArrayList<Turret>();
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
		
		this.health = health;
		hud = new Sprite(new Texture(Gdx.files.internal("player/heart.png")));
		
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		box2dWorld.setContactListener(new CustomContactListener());
        turretType1Btn = new Button(worldBatch,"", (int)((( 437/4 * Zombies.InitialWindowWidth /  Gdx.graphics.getWidth()) *3.5)*Zombies.WorldScale), 0, "Turret lv:1");
        turretType2Btn = new Button(worldBatch,"", (int)((( 437*3/4 * Zombies.InitialWindowWidth /  Gdx.graphics.getWidth()) *3.5)*Zombies.WorldScale), 0, "Turret lv:2");
        turretType1Btn.changeHidden();
        turretType2Btn.changeHidden();
		
	}
	/**
	 * Method to parse map objects, such as power ups, weapons, enemies and NPCs
	 */
	private void loadObjects() {
		
		//Get objects layer
		MapObjects objects = map.getLayers().get("Objects").getObjects();
		
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
				case "placer":
                    placeList.add(new Button(worldBatch, "selecter.png", x, y, ""));
                break;

				default:
					System.err.println("Error importing stage: unrecognised object");
				break;
			}
		}
		
		for (Button button : placeList)
			button.changeHidden();
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
		turretType1Btn.render();
		turretType2Btn.render();
		
		for (int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).render();
		for (Projectile bullet : bulletsList)
			bullet.render();
		for (Turret turret : turretType1List)
			turret.render();
		for (Turret turret : turretType2List)
			turret.render();
		for (Button button : placeList)
			button.render();
		
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
	
	/**
	 * Method to update everything in the state
	 * updates buttons sprites and adds turrets if placed
	 * increments wave count and calculates spawn count
	 */
	@Override
	public void update() {
		//Buttons in screen add if it is hidden or not
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Gdx.input.justTouched()) {
			
			if(turretType1Btn.isHover() && turretType1List.size()< turret1AmountAllowed) {
				turret1BtnClicked = true;
				for (int i = 0; i < placeList.size(); i++)
					placeList.get(i).changeHidden();
			
			}
			if(turretType2Btn.isHover() && turretType2List.size()< turret2AmountAllowed && !turret1BtnClicked) {
				for (int i = 0; i < placeList.size(); i++)
					placeList.get(i).changeHidden();
			
			}
			for (int i = 0; i < placeList.size(); i++)
				if(placeList.get(i).isHover() & !placeList.get(i).getHidden()) {
					
					if(turret1BtnClicked) {
						turretType1List.add(new Turret(this, placeList.get(i).getX() +placeList.get(i).getWidth()/2, placeList.get(i).getY()+placeList.get(i).getWidth()/2, "minigame/turret1.png", 15, "bullet.png", 20, Zombies.soundShoot,1000));
						turret1BtnClicked= false;
					}else {
						turretType2List.add(new Turret(this, placeList.get(i).getX() +placeList.get(i).getWidth()/2, placeList.get(i).getY()+placeList.get(i).getWidth()/2, "minigame/turret2.png", 10, "laser.png", 50, Zombies.soundLaser,500));
					}
					placeList.remove(i);
					i --;
					for (int l = 0; l < placeList.size(); l++)
						placeList.get(l).changeHidden();
					break;
			}
			
		}
		if((turretType1List.size()< turret1AmountAllowed) && turretType1Btn.getHidden() ) {
			turretType1Btn.changeHidden();
		}else if(turretType1List.size() == turret1AmountAllowed && !turretType1Btn.getHidden()){
			turretType1Btn.changeHidden();
		}
		if((turretType2List.size()< turret2AmountAllowed) && turretType2Btn.getHidden() ) {
			turretType2Btn.changeHidden();
		}else if(turretType2List.size() == turret2AmountAllowed && !turretType2Btn.getHidden()){
			turretType2Btn.changeHidden();
		}
		if(health <= 0) {
			StateManager.loadState(returnStage, 0);
		}
		
		//Check if wave is finished
		if(enemiesList.size() == 0) {
			waveCount += 1;
			turret1AmountAllowed ++;
			turret2AmountAllowed ++;
			switch (this.path) {
			case "World_One_Minigame" :
				spawnCount = waveCount * 1;
				break;
			case "World_Two_Minigame" :
				spawnCount = waveCount * 2;
				break;
			case "World_Four_Minigame" :
				spawnCount = waveCount * 4;
				break;
			case "World_Five_Minigame" :
				spawnCount = waveCount * 5;
				break;
			}
			
		}
		
		spawnZombies();
		
		//Update the camera position map size times 3/4
		camera.position.set((float) ((int)map.getProperties().get("width") * 32 * 3/4) , (float) ((int)map.getProperties().get("height") * 32 * 3/4), (float)0);
		camera.update();
		
		//Update Box2D physics
		box2dWorld.step(1 / 60f, 6, 2);
		
		//Update mobs
		for(int i = 0; i < enemiesList.size(); i++)
			enemiesList.get(i).update(this.inLights());			
		for (Turret turret : turretType1List)
			turret.update();
		for (Turret turret : turretType2List)
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
	
	/**
	 * Method to spawn next zombie if there are more zombies to spawn
	 * and sets the zombies speed and health
	 */
	private void spawnZombies() {
		//Zombie health and speed both increase with wave count, speed is capped at 10, health isn't capped
		if(spawnCount > 0 && spawnDelay == 0) {

			if (spawnCount < 10) {
				switch (spawnCount % 3) {
				case 0 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie1.png", spawnCount + 2, 5 * ((waveCount + 1) / 4) ));
					break;
				case 1 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie2.png", spawnCount + 2, 10 * ((waveCount + 1) / 4)));
					break;
				case 2 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie3.png", spawnCount + 2, 15 * ((waveCount + 1) / 4)));
					break;
				}
			} else {
				switch (spawnCount % 3) {
				case 0 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie1.png", 10, 5 * ((waveCount + 1) / 4)));
					break;
				case 1 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie2.png", 10, 10 * ((waveCount + 1) / 4)));
					break;
				case 2 :
					enemiesList.add(new MinigameZombie(this, spawnX, spawnY, "zombie/zombie3.png", 10, 15 * ((waveCount + 1) / 4)));
					break;
				}	
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

