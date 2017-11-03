package com.jonathanleblanc.huntingjo.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.World.Actor;
import com.jonathanleblanc.huntingjo.World.Alert;
import com.jonathanleblanc.huntingjo.World.Animal;
import com.jonathanleblanc.huntingjo.World.BodySegment;
import com.jonathanleblanc.huntingjo.World.Bullet;
import com.jonathanleblanc.huntingjo.World.Particle;
import com.jonathanleblanc.huntingjo.World.ParticleEmitter;
import com.jonathanleblanc.huntingjo.World.Tree;
import com.jonathanleblanc.huntingjo.World.World;


import processing.core.PVector;

//Game Renderer
//-------------
//- Controls the orthographic camera
//- Adjusts the camera's projection according to window size
//- Displays visible actors on the screen
//- Draws visual representations of invisible systems in debug mode

public class GameRenderer
{
	//Properties
	//----------
	
	private World world;
	
	//Debug modes
	private static boolean debug = false;
	private static boolean debug_fov = false;
	private static boolean debug_wind = false;
	private static boolean debug_particles = false;
	private static boolean debug_collisions = true;
	
	//Drawing tools
	protected ShapeRenderer shapeRenderer;
	protected SpriteBatch batch;
	
	//Camera
	protected OrthographicCamera camera;
	protected Vector3 mouse;
	protected Vector3 unprojected_mouse;
	
	//Pixel-per-unit system
	protected final static float CAMERA_WIDTH = 640;
	protected final static float CAMERA_HEIGHT = 480;
	protected float ppuX;
	protected float ppuY;
	
	
	//Utility methods
	//---------------
	
	//Converts a vector3 to a Processing Vector
	//(Ex.: when the Hunter actor needs to access the unprojected mouse coordinates)
	public static PVector vector3toProcessing(Vector3 vector)
	{
		return new PVector(vector.x, vector.y);
	}
	
	//Resizes the camera and adjusts its projection according to pixels-per-unit
	public void setSize(int w, int h)
	{
		ppuX = (float) w / CAMERA_WIDTH;
		ppuY = (float) h / CAMERA_HEIGHT;
		
		camera.setToOrtho(false, (float) w, (float) h);
		camera.update();
	}
	
	//Initializes and positions the camera
	public void setupCamera(PVector position)
	{
		mouse = new Vector3(0, 0, 0);
		unprojected_mouse = new Vector3(0, 0, 0);
		
		camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
		camera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		setCameraPosition(position);
	}
	
	//Initializes the drawing tools
	public void setupDrawing()
	{
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
	}
	
	//Getter/Setter methods
	//---------------------
	
	public PVector getCameraSize()
	{
		return new PVector(CAMERA_WIDTH, CAMERA_HEIGHT);
	}
	
	public void setCameraPosition(PVector pos)
	{
		camera.position.set(pos.x, pos.y, 0);
		camera.update();
	}
	
	public PVector getCameraPosition()
	{
		return new PVector(camera.position.x, camera.position.y);
	}
	
	public Vector3 getMouse()
	{
		return mouse;
	}

	//Debugger methods
	//----------------
	
	public static void toggleDebug()
	{
		debug = !debug;
	}
	
	public static boolean getDebug()
	{
		return debug;
	}
	
	public static void toggleDebugFOV()
	{
		debug_fov = !debug_fov;
	}
	
	public static boolean getDebugFOV()
	{
		return debug_fov;
	}
	
	public static void toggleDebugWind()
	{
		debug_wind = !debug_wind;
	}
	
	public static boolean getDebugWind()
	{
		return debug_wind;
	}
	
	public static void toggleDebugParticles()
	{
		debug_particles = !debug_particles;
	}
	
	public static boolean getDebugParticles()
	{
		return debug_particles;
	}
	
	public static void toggleDebugCollisions()
	{
		debug_collisions = !debug_collisions;
	}
	
	public static boolean getDebugCollisions()
	{
		return debug_collisions;
	}

	//Constructor
	//-----------
	public GameRenderer(World world)
	{
		this.world = world;
		
		setupCamera(world.getHunter().getPosition().get());
		setupDrawing();
	}
	
	//Update methods
	//--------------
	
	//Update mouse coordinates, projected & unprojected
	//(Called by the GameScreen before the World updates)
	public void update_mouse(float x, float y)
	{
		mouse.set(x, y, 0);
		camera.unproject(mouse);
		unprojected_mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	}
	
	//Render the updated World
	public void render(float delta)
	{
		update(delta);
		draw();
	}
	
	//Update camera coordinates after World is updated
	private void update(float delta)
	{
		setCameraPosition(world.getHunter().getLookFocus().get());
		
		if (camera.position.x < CAMERA_WIDTH / 2 * ppuX) setCameraPosition(new PVector(CAMERA_WIDTH / 2 * ppuX, camera.position.y));
		if (camera.position.x > (world.getSize().x - CAMERA_WIDTH / 2) * ppuX) setCameraPosition(new PVector((world.getSize().x - CAMERA_WIDTH / 2) * ppuX, camera.position.y));
		if (camera.position.y < CAMERA_HEIGHT / 2 * ppuY) setCameraPosition(new PVector(camera.position.x, CAMERA_HEIGHT / 2 * ppuY));
		if (camera.position.y > (world.getSize().y - CAMERA_HEIGHT / 2) * ppuY) setCameraPosition(new PVector(camera.position.x, (world.getSize().y - CAMERA_HEIGHT / 2) * ppuY));
		
		camera.update();
	}
	
	//Drawing methods
	//---------------
	
	//Draw visible actors
	private void draw()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//Actors
		for (Actor actor : world.getActors())
		{
			actor.draw(batch);
		}
		
		//Particles
		for (ParticleEmitter pe : world.getParticleEmitters())
		{
			for (Particle p : pe.getParticles())
			{
				p.draw(batch);
			}
		}

		//Tree branches
		for (Tree tree : world.getTrees())
		{
			tree.draw_limbs(batch);
		}
		
		batch.draw(AssetControler.textures.get(AssetControler.TEXT_CAMERAEDGE), camera.position.x - ((CAMERA_WIDTH / 2) * ppuX), camera.position.y - ((CAMERA_HEIGHT / 2) * ppuY), 0, 0, 640, 480, ppuX, ppuY, 0, 0, 0, 640, 480, false, false);
		
		//Display the help box if it's visible
		if (world.getShowHelp())
		{
			Sprite help = new Sprite(AssetControler.textures.get(AssetControler.TEXT_HELP));
			help.setAlpha(0.8f);
			help.setPosition(camera.position.x - 200, camera.position.y - 150);
			help.draw(batch);
		}
		
		batch.end();
		
		//Draw the debugging information if debugging is activated
		if (debug) drawDebug();
	}
	
	//Draw debugging information
	public void drawDebug()
	{
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		
		//Mouse debugging
		//(Check if the hunter's look focus vector follows mouse coordinates)
		shapeRenderer.circle(mouse.x, mouse.y, 5);
		shapeRenderer.circle(world.getHunter().getLookFocus().x, world.getHunter().getLookFocus().y, 3);
		
		//Camera debugging
		//(Check if the camera follows the mouse properly)
		shapeRenderer.setColor(new Color(1, 0, 0, 1));
		shapeRenderer.circle(camera.position.x, camera.position.y, 3);
		
		//Draw the body segments to check the vectorial collisions
		if (debug_collisions) 
		{
			shapeRenderer.setColor(new Color(0, 1, 0, 1));
			
			//Actors
			for (Actor actor : world.getActors())
			{
				for (BodySegment segment : actor.getBody())
				{
					shapeRenderer.circle(segment.getPosition().x, segment.getPosition().y, segment.getSizeRadius());
				}
			}
		
			//Caribou flock
			for (Animal animal : world.getHerdAnimals())
			{
				for (BodySegment segment : animal.getBody())
				{
					shapeRenderer.circle(segment.getPosition().x, segment.getPosition().y, segment.getSizeRadius());
				}
			}
		
			//Wolf flock
			for (Animal animal : world.getPackAnimals())
			{
				for (BodySegment segment : animal.getBody())
				{
					shapeRenderer.circle(segment.getPosition().x, segment.getPosition().y, segment.getSizeRadius());
				}
			}
		}
		
		//Wind debugging
		//(Check if the wind force is properly applied to invisible alerts like smells and sounds)
		if (debug_wind)
		{
			int count = 0;
			for (Alert alert : world.getAlerts())
			{
				if (count++ > 20003) break;
				
				shapeRenderer.setColor(alert.getDebugCode());
				shapeRenderer.circle(alert.getPosition().x, alert.getPosition().y, alert.getMagnitude());
			}
		}
		
		//Bullet debugging
		for (Bullet bullet : world.getHunter().getBulletsInFlight())
		{
			shapeRenderer.circle(bullet.getPosition().x, bullet.getPosition().y, 5);
		}
		
		shapeRenderer.end();
	}
}
