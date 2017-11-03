package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Renderers.GameRenderer;
import com.jonathanleblanc.huntingjo.Screens.GameScreen;

import java.util.ArrayList;

import processing.core.PVector;

//World
//- Spawns and manages all actors

public class World 
{
	//Properties
	//----------
	
	private PVector size;
	
	private ArrayList <Actor> actors = new ArrayList <Actor> ();
	private ArrayList <Actor> obstacles = new ArrayList <Actor> ();
	private ArrayList <Tree> trees = new ArrayList <Tree> ();
	private ArrayList <Alert> alerts = new ArrayList <Alert> ();
	private ArrayList <ParticleEmitter> particleEmitters = new ArrayList <ParticleEmitter> ();
	
	private GameScreen screen;
	private Hunter hunter;
	private Wind wind;
	private Herd herd;
	private Herd pack;
	
	private boolean show_help;
	
	//Debugging
	private static boolean debug_predationOn = true;
	private static boolean fov_on = true;
	private static boolean nose_on = true;
	private static boolean ear_on = true;

	//Getter/Setter methods
	//---------------------
	
	public ArrayList <Actor> getActors() 
	{
		return actors;
	}
	
	public Hunter getHunter() 
	{
		return hunter;
	}
	
	public ArrayList <Actor> getObstacles()
	{
		return obstacles;
	}
	
	public PVector getSize()
	{
		return size.get();
	}
	
	public Wind getWind()
	{
		return wind;
	}
	
	public ArrayList <Animal> getHerdAnimals()
	{
		return herd.getAnimals();
	}
	
	public ArrayList <Animal> getPackAnimals()
	{
		return pack.getAnimals();
	}
	
	public ArrayList <Alert> getAlerts()
	{
		return alerts;
	}
	
	public ArrayList <ParticleEmitter> getParticleEmitters()
	{
		return particleEmitters;
	}
	
	public static boolean isDebug_predationOn() 
	{
		return debug_predationOn;
	}
	
	public static void toggleDebug_predation() 
	{
		World.debug_predationOn = !World.debug_predationOn;
	}
	
	public static boolean fovOn()
	{
		return fov_on;
	}
	
	public static boolean noseOn()
	{
		return nose_on;
	}
	
	public static boolean earOn()
	{
		return ear_on;
	}
	
	public static void toggle_fov()
	{
		fov_on = !fov_on;
	}
	
	public static void toggle_nose()
	{
		nose_on = !nose_on;
	}
	
	public static void toggle_ear()
	{
		ear_on = !ear_on;
	}
	
	public ArrayList<Tree> getTrees()
	{
		return trees;
	}
	
	public GameRenderer getRenderer()
	{
		return screen.getRenderer();
	}
	
	public boolean getShowHelp()
	{
		return show_help;
	}
	
	//Constructor
	//-----------
	
	public World(GameScreen screen, PVector size)
	{
		this.screen = screen;
		this.size = size;
		show_help = false;
		
		spawnForest();
		toggle_help();
	}
	
	//Show/hide the help box
	//----------------------
	
	public void toggle_help()
	{
		show_help = !show_help;
	}
	
	//Spawning methods
	//----------------
	
	public void spawnForest()
	{
		//Wind
		wind = new Wind(new PVector(0, 0, 0), this);
		
		//Obstacles
		spawnObstacles(5, 10, 5);
		
		//Hunter
		PVector pos = new PVector(MathUtils.random(200, size.x - 200), MathUtils.random(200, size.y - 200));
		spawnHunter(pos);
		
		//Herd
		spawnHerd(new PVector(MathUtils.random(0, size.x), MathUtils.random(0, size.y)), 12);
		
		//Pack
		spawnPack(new PVector(MathUtils.random(0, size.x), MathUtils.random(0, size.y)), 4);
	}
	
	//Spawn the Hunter
	private void spawnHunter(PVector position)
	{
		hunter = (Hunter) spawnActor(new Hunter(position, this));
	}
	
	//Spawn all the obstacles
	private void spawnObstacles(int nrocks, int ntrees, int nflowers)
	{
		//Rocks are evenly distributed throughout the world
		for (int i = 0; i < nrocks; i ++)
		{
			for (int j = 0; j < nrocks; j ++)
			{
				float sector_w = size.x / nrocks;
				float sector_h = size.y / nrocks;
				
				obstacles.add(spawnActor(new Rock(new PVector((float) ((sector_w * i + (sector_w * 0.1)) + MathUtils.random(0, (float) (sector_w * 0.8))), (float) ((sector_h * j + (sector_h * 0.1)) + MathUtils.random(0, (float) (sector_h * 0.8)))), this)));
			}
		}
		
		//Trees are evenly distributed throughout the world
		for (int i = 0; i < ntrees; i ++)
		{
			for (int j = 0; j < ntrees; j ++)
			{
				float sector_w = size.x / ntrees;
				float sector_h = size.y / ntrees;
				
				Tree tree = new Tree(new PVector((float) ((sector_w * i + (sector_w * 0.1)) + MathUtils.random(0, (float) (sector_w * 0.8))), (float) ((sector_h * j + (sector_h * 0.1)) + MathUtils.random(0, (float) (sector_h * 0.8)))), this, 3 + MathUtils.random(2));
				
				trees.add(tree);
				obstacles.add(spawnActor(tree));
			}
		}

		//Flowers are randomly distributed throughout the world
		for (int i = 0; i < nflowers; i ++)
		{
			for (int j = 0; j < nflowers; j ++)
			{
				int numpetals = 3 + MathUtils.random(2);
				obstacles.add(spawnActor(new Flower(new PVector(MathUtils.random(size.x), MathUtils.random(size.y)), this, numpetals)));
			}
		}
	}
	
	//Spawn the Caribou herd
	private void spawnHerd(PVector location, int size)
	{
		herd = new Herd(this);
		
		for (int i = 0; i < size; i ++)
		{
			herd.addAnimal(new Caribou(new PVector(location.x + MathUtils.random(-10, 10), location.y + MathUtils.random(-10, 10)), this, herd));
		}
	}
	
	//Spawn the pack of Wolves
	private void spawnPack(PVector location, int size)
	{
		pack = new Herd(this);
		
		for (int i = 0; i < size; i ++)
		{
			pack.addAnimal(new Wolf(new PVector(location.x + MathUtils.random(-10, 10), location.y + MathUtils.random(-10, 10)), this, pack));
		}
	}
	
	//Spawn an actor
	public Actor spawnActor(Actor actor)
	{
		actors.add(actor);
		
		return actor;
	}
	
	//Respawn an animal
	public void respawnActor(Actor.ActorType type)
	{
		//Spawn the actor on the edges of the world
		PVector location = new PVector();
		int rand = MathUtils.random(3);
		
		switch (rand)
		{
			//East side
			case 0:
			{
				location.set(size.x, size.y * MathUtils.random());
				
				break;
			}
			
			//North side
			case 1:
			{
				location.set(size.x * MathUtils.random(), size.y);
				
				break;
			}
			
			//West side
			case 2:
			{
				location.set(0, size.y * MathUtils.random());
				
				break;
			}
			
			//South side
			case 3:
			{
				location.set(size.x * MathUtils.random(), 0);
				
				break;
			}
		}

		if (type == Actor.ActorType.CARIBOU) herd.addAnimal(new Caribou(location, this, pack));
		else pack.addAnimal(new Wolf(location, this, herd));
	}
	
	//Update methods
	//--------------
	
	public void update(float delta)
	{
		update_wind(delta);
		update_alerts(delta);
		update_particles(delta);
		update_actors(delta);
	}
	
	//Wind engine
	private void update_wind(float delta)
	{
		wind.update(delta);
	}
	
	//Alerts
	private void update_alerts(float delta)
	{
		ArrayList <Alert> expired_alerts = new ArrayList <Alert>();
		
		for (Alert alert : alerts)
		{
			alert.update(delta);
			if (alert.isDisposed() || (alert.getPosition().x < 0 || alert.getPosition().y < 0 || alert.getPosition().x > size.x || alert.getPosition().y > size.y)) expired_alerts.add(alert);
		}
		
		for (Alert alert : expired_alerts)
		{
			alerts.remove(alert);
		}
	}
	
	//Particle emitters
	private void update_particles(float delta)
	{
		ArrayList <ParticleEmitter> expired_emitters = new ArrayList <ParticleEmitter> ();
		
		for (ParticleEmitter emitter : particleEmitters)
		{
			emitter.update(delta);
			
			if (emitter.isExpired()) expired_emitters.add(emitter);
		}
		
		for (ParticleEmitter emitter : expired_emitters)
		{
			particleEmitters.remove(emitter);
		}
	}
	
	//Actors
	private void update_actors(float delta)
	{
		ArrayList<Actor> deadguys = new ArrayList<Actor> ();
		
		//Dead things
		for (Actor deadguy : actors)
		{
			if (deadguy.getLife() == 0) deadguys.add(deadguy);
		}
		
		for (Actor deadguy : deadguys)
		{
			actors.remove(deadguy);
			actors.add(0, deadguy);
		}
		
		//Herd
		for (Actor expired_caribou : herd.run(delta))
		{
			actors.remove(expired_caribou);
		}
		
		//Pack
		for (Actor expired_wolf : pack.run(delta))
		{
			actors.remove(expired_wolf);
		}
		
		//Hunter
		hunter.update(delta);
		
		//Obstacles
		for (Actor obstacle : obstacles)
		{
			obstacle.update(delta);
		}
	}
}