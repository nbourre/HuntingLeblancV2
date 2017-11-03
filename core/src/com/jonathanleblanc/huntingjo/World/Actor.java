package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Tasks.EmissionTask;
import com.jonathanleblanc.huntingjo.Tasks.RespawnTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import processing.core.PVector;

//Actor
//-----
//- The building block of the simulator's interactions
//- An object inherits from Actor if it does anything significant in the world

public class Actor 
{
	//Properties
	//----------
	
	//Types of actor
	public static enum ActorType
	{
		HUNTER,
		CARIBOU,
		WOLF,
		FLOWER,
		ROCK,
		TREE,
		WIND,
		SMELL,
		NOISE,
		BULLET,
		WHOCARES 
	}
	
	//Types of material
	public static enum MaterialType
	{
		FLESH,
		WOOD,
		STONE,
		AIR
	}
	
	//An Actor hit by a bullet emits a different kind of particle depending on its material
	public static Map <MaterialType, ParticleEmitter.ParticleType> impactTypes = new HashMap <MaterialType, ParticleEmitter.ParticleType>();
	{
		impactTypes.put(MaterialType.FLESH, ParticleEmitter.ParticleType.BLOOD);
		impactTypes.put(MaterialType.STONE, ParticleEmitter.ParticleType.SMOKE);
		impactTypes.put(MaterialType.WOOD, ParticleEmitter.ParticleType.SMOKE);
	};
	
	//An Actor can access the rest of the World's contents
	protected World world;
	
	//Vectors
	protected PVector position;
	protected PVector velocity;
	
	//Skeleton
	protected ArrayList<BodySegment> body = new ArrayList<BodySegment>();
	
	//Texture Index
	protected int texture_index;
	
	//Physical Properties
	protected float size_radius = 15;
	protected float rotate_angle = MathUtils.random(360);
	protected float fov_width = 45;
	protected float fov_range = 400;
	protected float mass = 100;
	protected float density_scale = 1;
	protected float bo_strength = 150;
	
	protected boolean disposed = false;
	protected boolean is_solid = true;
	protected boolean is_visible = true;
	protected boolean is_respawnable = false;
	protected boolean is_cover = false;
	protected boolean footfall = false;
	protected boolean noisy = false;
	
	protected ActorType type;
	protected MaterialType material_type;
	
	protected float life = 1;
	protected float attack_power = 0;
	protected float food = 0;
	protected float max_food = 1;
	
	//Utility methods
	//---------------
	
	//Build the skeleton with body segments
	//(By default, an Actor has one segment, but animals have more)
	protected void buildSkeleton()
	{
		body = new ArrayList<BodySegment>();
		body.add(new BodySegment(this, position, size_radius));
	}
	
	//Emit a smell or a noise
	public void emitAlert(ActorType type, float magnitude, float duration, int noise_index)
	{
		world.getAlerts().add(new Alert(position.get(), type, world, this, magnitude, duration, noise_index));
	}
	
	//For noisy actors with a movement animation,
	//Emit a noise when a foot hits the ground
	//The frequency of the emission is proportional to the Actor's current velocity
	public void movementNoise()
	{
		if (noisy)
		{
			int ran = MathUtils.random(3);
			emitAlert(ActorType.NOISE, 300, 3, AssetControler.SOUND_FOOTSTEP[ran]);
		}
	}
	
	//Getter/Setter methods
	//---------------------
	public void setFood(float food)
	{
		this.food = food;
		
		if (this.food > 1) this.food = 1;
	}
	
	public float getFood()
	{
		return food;
	}
	
	public boolean maxFood()
	{
		return (food == max_food);
	}
	
	public void setLife(float life)
	{
		this.life = life;
	}
	
	public float getLife()
	{
		return life;
	}
	
	public PVector getPosition() 
	{
		return position;
	}
	
	public ArrayList<BodySegment> getBody()
	{
		return this.body;
	}
	
	public ActorType getType()
	{
		return type;
	}
	
	public boolean isDisposed()
	{
		return disposed;
	}
	
	public boolean isCover()
	{
		return is_cover;
	}
	
	public MaterialType getMaterialType()
	{
		return material_type;
	}
	
	public float getDensityScale()
	{
		return density_scale;
	}
	
	public void setFootfall(boolean footfall)
	{
		this.footfall = footfall;
	}
	
	public boolean getSolidity()
	{
		return is_solid;
	}
	
	public void setNoisiness(boolean noisy)
	{
		this.noisy = noisy;
	}
	
	//Constructor
	//-----------
	public Actor(PVector position, ActorType type, World world, boolean smelly, boolean noisy)
	{
		material_type = MaterialType.AIR;
		
		this.position = position;
		this.type = type;
		this.world = world;
		this.velocity = new PVector();

		if (smelly)
		{
			Timer timer = new Timer();
			timer.start();
			timer.scheduleTask(new EmissionTask(ActorType.SMELL, this, bo_strength, 5, -1), 3, 3);
		}
		
		this.noisy = noisy;
		
		buildSkeleton();
	}
	
	//Update methods
	//--------------
	
	//Basic update will check for collisions so that Actors don't overlap
	public void update(float delta)
	{
		actor_collisions(world.getActors());
	}
	
	//Collision check
	protected void actor_collisions(ArrayList <Actor> others)
	{
		//Since every body segment in the world is a circular shape...
		//Go through the list of other actors
		for (Actor candidate : others)
		{
			//Check if the other actor isn't you
			//Check if the other actor is in fact an obstacle
			if (!this.equals(candidate) && ((candidate.getSolidity() && is_solid) || (type == ActorType.CARIBOU && candidate.getType() == ActorType.FLOWER)))
			{
				//Go through the actor's segments
				for (BodySegment other_segment : candidate.getBody())
				{
					//And check with your owns segments
					for (BodySegment segment : body)
					{
						//If there is a collision with one of your segments and one of the other's segments
						if (other_segment.collidesWith(segment) && candidate.getSolidity())
						{
							//Determine in what direction and how far you must move to get out of the way
							PVector repulsion = new PVector(segment.getPosition().x - other_segment.getPosition().x, segment.getPosition().y - other_segment.getPosition().y, 0);
							repulsion.setMag((segment.getSizeRadius() + other_segment.getSizeRadius()) - PVector.dist(other_segment.getPosition(), segment.getPosition()));
							
							//If the other actor is bigger than you
							if (mass < candidate.mass)
							{
								//Get out of the way
								position.add(repulsion);
							}
							//If the opposite is true
							else
							{
								//Push the little bugger out of the way
								candidate.getPosition().sub(repulsion);
							}
						}
					}
				}
			}
		}
	}
	
	//If an actor is expired it is disposed:
	//Once the game loop iteration finishes, the World proceeds to get rid of expired actors
	public void dispose()
	{
		disposed = true;
		
		//If it can respawn (ex.: an animal), a respawn timer starts
		if (is_respawnable)
		{
			Timer.schedule(new RespawnTask(world, type), 10);
			is_respawnable = false;
		}
	}

	//Draw methods
	//------------
	
	//Each Actor draws itself using the GameRenderer's sprite batch
	public void draw(SpriteBatch batch)
	{
		if (is_visible)
		{
			drawSprite(batch, new Sprite(AssetControler.textures.get(texture_index)), position.x - AssetControler.textures.get(texture_index).getWidth() / 2, position.y - AssetControler.textures.get(texture_index).getHeight() / 2, 1, rotate_angle);
		}
	}
	
	//Quickly draw a sprite with certain modifications and translations
	//(Ex.: different origins, positions, rotations, etc.)
	public void drawSprite(SpriteBatch batch, Sprite sprite, float x, float y, float scale, float originx, float originy, float rotation)
	{
		sprite.setPosition(x, y);
		sprite.setScale(scale);
		sprite.setOrigin(originx, originy);
		sprite.setRotation(rotation);
		sprite.draw(batch);
	}
	
	//Quickly draw a sprite with certain modifications and translations
	//(Ex.: different origins, positions, rotations, etc.)
	//But automatically center the origin
	public void drawSprite(SpriteBatch batch, Sprite sprite, float x, float y, float scale, float rotation)
	{
		sprite.setPosition(x, y);
		sprite.setScale(scale);
		sprite.setOriginCenter();
		sprite.setRotation(rotation);
		sprite.draw(batch);
	}
}
