package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Tasks.CanMunchTask;
import com.jonathanleblanc.huntingjo.Tasks.FootfallTask;
import com.jonathanleblanc.huntingjo.Tasks.HungerTask;

import java.util.ArrayList;

import processing.core.PVector;

//Animal
//------
//- A heavily modified implementation of the Reynolds "Boid"
//- Behaviour is determined by two systems: sensory awareness and herd/predation mentality

public class Animal extends Actor
{
	//Properties
	//----------
	
	//Animal States
	public static enum AnimalState
	{
		EAT,
		ROLLOVER,
		ROT
	}
	
	protected World world;
	
	//Vectors
	protected PVector acceleration;
	protected PVector velocity;
	protected PVector goal;
	protected PVector final_velocity;

	//Artificial Intelligence
	protected Actor nearest_predator;
	protected Actor nearest_prey;
	protected Herd herd;
	
	protected float r;
	protected float maxforce;
	protected float maxspeed;
	protected float noserange;
	protected float earrange;
	protected float state_time;
	protected float rotate_time;
	
	protected boolean panicked;
	protected boolean can_munch;
	protected boolean starving;
	protected boolean final_moment;
	protected int rollover_scale;
	protected float hunger;
	
	protected AnimalState animal_state;
	
	//Utility methods
	//---------------
	
	//Apply a force proportional to mass
	public void applyForce(PVector force)
	{
		force.div(mass);
		acceleration.add(force);
	}
	
	//Check if a position is within your FOV
	private boolean inFOV(PVector pos) 
	{
		//Get the FOV cone
		float limit1 = rotate_angle - fov_width, limit2 = rotate_angle + fov_width;
		
		if (limit1 < 0) 
		{
			limit1 = 360f + limit1;
		}
		
		if (limit2 > 360f) 
		{
			limit2 = limit2 - 360f;
		}
		
		//Get the angle between this position and the other position
	    float angle = MathUtils.radiansToDegrees * (MathUtils.atan2(pos.y - position.y, pos.x - position.x));
		
		if (pos.y < (position.y))
		{
			angle = 360 - angle;
		}
		
		if (limit1 > limit2)
		{
			if (angle <= limit2 || angle >= limit1) 
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		else 
		{
			if (angle >= limit1 && angle <= limit2) 
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
	}
	
	//Getter/Setter methods
	//---------------------
	
	public void setPanicked(boolean panicked)
	{
		this.panicked = panicked;
	}
	
	public void setCanMunch(boolean can_munch)
	{
		this.can_munch = can_munch;
	}

	protected float getDirectionAngle()
	{
		PVector actual_vel;
		if (life > 0) actual_vel = velocity.get();
		else actual_vel = final_velocity.get();
			
	    float angle = MathUtils.radiansToDegrees * (MathUtils.atan2((position.y + actual_vel.y) - position.y, (position.x + actual_vel.x) - position.x));
		if (angle < 0) angle = 360 + angle;
		return angle;
	}
	
	public void canMunch()
	{
		can_munch = true;
	}
	
	public void getHungrier()
	{
		if (!starving) 
		{
			hunger += 0.1f;
		
			if (hunger >= 1)
			{
				starving = true;
			}
		}
		
	}
	
	@Override
	public void setLife(float life)
	{
		this.life = life;
		
		if (life == 0)
		{
			if (!final_moment)
			{
				animal_state = AnimalState.ROLLOVER;
				state_time = 0;
				
				final_velocity = velocity.get();
				velocity.mult(0);
				final_moment = true;
			}
			
			material_type = MaterialType.AIR;
			mass *= 2;
		}
	}
	
	//Constructor
	//-----------
	
	public Animal(PVector position, ActorType type, World world, Herd herd) 
	{
		super(position, type, world, true, true);
		
		animal_state = AnimalState.EAT;
		material_type = MaterialType.FLESH;
		
		is_respawnable = true;
		
		acceleration = new PVector(0, 0);
		velocity = PVector.random2D();
		
		this.position = position;
		r = 2.0f;
		maxspeed = 1.5f;
		maxforce = 0.06f;
		noserange = 200;
		earrange = 300;
		rotate_time = 0;
		
		this.herd = herd;
		this.world = world;
		goal = new PVector(world.getSize().x* MathUtils.random(), world.getSize().y* MathUtils.random());
		panicked = false;
		
		can_munch = true;
		starving = false;
		hunger = MathUtils.random();
		Timer.schedule(new HungerTask(this), 3, 3);
		
		rollover_scale = MathUtils.random(-1, 1);
	}
	
	//Update methods
	//--------------
	
	//Update your mental and physical states
	public void run(ArrayList<Animal> animals, ArrayList <Actor> actors, ArrayList <Alert> alerts, float delta)
	{
		//Mental state
		ArrayList<Actor> dpreys = sense(animals, actors, alerts);
		
		//Physical state
		update(dpreys);
		borders();
		actor_collisions(actors);
		
		//Manage state time for animations
		rotate_time += delta;
		
		switch (animal_state)
		{
			case EAT:
			{
				//Munching animation
				if (can_munch) state_time = 0;
				else state_time += delta;
				break;
			}

			case ROLLOVER:
			{
				//Transition between live animation and death
				if (state_time < 1.5f) state_time += delta;
				else animal_state = AnimalState.ROT;
				break;
			}

			case ROT:
			{
				//Decay animation unfolds as food is consumed
				state_time = 1 - (food / max_food);
				break;
			}
			
			default:
			{}
		}
	}
	
	//Detect any actors in your vicinity
	protected ArrayList<Actor> sense(ArrayList<Animal> animals, ArrayList<Actor> actors, ArrayList<Alert> alerts)
	{
		//Initialize lists of detected actors
		ArrayList<Actor> dpredators = new ArrayList<Actor>();
		ArrayList<Actor> dpreys = new ArrayList<Actor>();
		ArrayList<Actor> dactors = new ArrayList<Actor> ();
		
		nearest_prey = null;
		
		//Sight
		//Check if you are allowed to see
		if (World.fovOn())
		{
			//Check if there are actors within your FOV
			for (Actor actor : actors)
			{
				if (inFOV(actor.getPosition()))
				{
					dactors.add(actor);	
				}
			}
		}
		
		//Smell and hearing
		for (Alert alert : alerts)
		{
			//Determine the distance between you and the alert
			//Check if it is close enough for you to perceive it
			float dist = MathUtils.clamp(PVector.dist(alert.getPosition(), position) - alert.getMagnitude(), 0, PVector.dist(alert.getPosition(), position));
			
			//Check if you are allowed to smell
			//Check if the alert is a smell
			//Check if the alert is within smelling distance
			if (World.noseOn() && dist < noserange && alert.getType() == ActorType.SMELL)
			{
				//Detect the alert's source
				dactors.add(alert.getSource());
			}
			
			//Check if you are allowed to hear
			//Check if the alert is a noise
			//Check if the alert is within hearing distance
			if (World.earOn() && dist < earrange && alert.getType() == ActorType.NOISE)
			{
				//Detect the alert's source
				dactors.add(alert.getSource());
			}
		}
		
		//React to detected actors
		for (Actor actor : dactors)
		{
			//If the alert signals a Hunter
			//	OR it signals a Wolf and you are a Caribou
			if (actor.getType() == ActorType.HUNTER || (actor.getType() == ActorType.WOLF && type == ActorType.CARIBOU) && !dpredators.contains(actor))
			{
				//You have detected a predator!
				dpredators.add(actor);
			}
			//If you are a Wolf and the alert signals a Caribou
			//	OR you are a Caribou and the alert signals a Flower
			else if ((actor.getType() == ActorType.CARIBOU && type == ActorType.WOLF) || (actor.getType() == ActorType.FLOWER && type == ActorType.CARIBOU) && !dpreys.contains(actor) && starving && actor.getFood() > 0)
			{
				//You have detected a prey!
				dpreys.add(actor);
			}
		}
		
		//Hunt your prey
		//(If you are aware of any)
		//(And if you are hungry)
		if (dpreys.size() > 0 && starving) 
		{
			//If there is only one, it's obviously the nearest one
			nearest_prey = dpreys.get(0);
		
			//Otherwise go through the detected actors and find the nearest
			for (Actor actor : dpreys)
			{
				if (PVector.dist(position, nearest_prey.getPosition()) > PVector.dist(position, actor.getPosition()))
				{
					nearest_prey = actor;
				}
			}
			
			//The goal in the Reynolds Steering algorithm is your prey
			goal = nearest_prey.getPosition();
		}
		
		//Unless there is a reason to panic, you chill out
		panicked = false;
		
		//Run in the opposite direction than the predator if you're in danger
		if (dpredators.size() > 0) 
		{
			//If there is only one, it's obviously the nearest one
			nearest_predator = dpredators.get(0);

			//Otherwise go through the detected actors and find the nearest
			for (Actor actor : dpreys)
			{
				if (PVector.dist(position, nearest_predator.getPosition()) > PVector.dist(position, actor.getPosition()))
				{
					nearest_predator = actor;
				}
			}
			
			//If the nearest predator is close enough
			if (PVector.dist(position, nearest_predator.getPosition()) <= herd.getFlightRadius())
			{
				//Freak out
				panicked = true;
			}
		}
		
		//Once you have compiled lists of detected actors
		//Proceed with the herd/predation dynamics
		flock(animals, dpredators, dpreys);
		
		//The list of preys will be used in the update()
		//	when you check whether or not you can attack someone
		return dpreys;
	}

	//Herd/predation dynamics
	protected void flock(ArrayList<Animal> animals, ArrayList<Actor> predators, ArrayList<Actor> prey)
	{
		//Separation force
		PVector sep = separate(animals);
		
		//Alignment
		PVector ali = align(animals);
		
		//Cohesion
		PVector coh = cohesion(animals);
		
		//Flight force
		PVector flight = flight(predators);
		
		//Pursuit force
		PVector pursuit = pursuit(prey);
		
		//Seek a goal
		PVector objective = new PVector();
		if (goal != null) objective = seek(goal);
		
		// Arbitrarily weigh the forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(1.0f);
		flight.mult(10.0f);
		pursuit.mult(5.0f);
		objective.mult(1);
		
		//Add the force vectors to acceleration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);

		//Run away if you're panicking
		if (panicked) 
		{
			applyForce(flight);
		}
		//Or else run after the nearest prey if you're hungry
		//	Or just wander around if you're full
		else
		{
			applyForce(objective);
			if (starving) applyForce(pursuit);
		}
	}
	
	//Reynolds Goal-Seeking algorithm
	protected PVector seek(PVector target) 
	{
		PVector desired = PVector.sub(target, position);
		desired.setMag(maxspeed);
		
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce);
	
		return steer;
	}
	
	//Separation force
	protected PVector separate (ArrayList<Animal> animals)
	{
		float desiredseparation = herd.getSeparationRadius();
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		
		for (Animal other : animals) 
		{
			float d = PVector.dist(position, other.position);
			
			if ((d > 0) && (d < desiredseparation))
			{
				PVector diff = PVector.sub(position, other.position);
				diff.normalize();
				diff.div(d);
				steer.add(diff);
				count ++;
			}
		}
		
		if (count > 0)
		{
			steer.div((float)count);
		}
		
		if (steer.mag() > 0) 
		{	
			steer.setMag(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		
		return steer;
	}
	
	//Alignment force
	protected PVector align (ArrayList<Animal> animals)
	{
		float neighbordist =  herd.getAlignmentRadius();
		PVector sum = new PVector(0, 0);
		int count = 0;
		
		for (Animal other : animals) 
		{
			float d = PVector.dist(position, other.position);
			if ((d > 0) && (d < neighbordist))
			{
				sum.add(other.velocity);
				count ++;
			}
		}
		
		if (count > 0)
		{
			sum.div((float)count);
			sum.setMag(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxforce);
			
			return steer;
		}
		else 
		{
			return new PVector(0, 0);
		}
	}
	
	//Cohesion force
	protected PVector cohesion (ArrayList<Animal> animals) 
	{
		float neighbordist = herd.getCohesionRadius();
		PVector sum = new PVector(0, 0); // Start with empty vector to accumulate all positions
		int count = 0;
		
		for (Animal other : animals)
		{
			float d = PVector.dist(position, other.position);
			
			if ((d > 0) && (d < neighbordist)) 
			{
				sum.add(other.position);
				count ++;
			}
		}
		
		if (count > 0) 
		{
			sum.div(count);
			return seek(sum);
		} 
		else 
		{
			return new PVector(0, 0);
		}
	}
	
	//Flight force
	protected PVector flight (ArrayList<Actor> predators)
	{
		float desiredseparation = herd.getFlightRadius();
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		
		if (predators != null)
		{
			for (Actor other : predators) 
			{
				float d = PVector.dist(position, other.position);
				
				if ((d > 0) && (d < desiredseparation))
				{
					PVector diff = PVector.sub(position, other.position);
					diff.normalize();
					diff.div(d);
					steer.add(diff);
					count ++;
				}
			}
		}
		
		if (count > 0)
		{	
			steer.div((float)count);
		}
		
		if (steer.mag() > 0) 
		{
			steer.setMag(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		
		return steer;
	}
	
	//Pursuit force
	protected PVector pursuit(ArrayList<Actor> prey)
	{
		float desiredseparation = herd.getChaseRadius();
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		
		if (prey != null)
		{
			for (Actor other : prey) 
			{
				float d = PVector.dist(position, other.position);
				
				if ((d > 0) && (d < desiredseparation))
				{
					PVector diff = PVector.sub(other.position, position);
					diff.normalize();
					diff.div(d);
					steer.add(diff);
					count ++;
				}
			}
		}
		
		if (count > 0)
		{
			steer.div((float)count);
		}
		
		if (steer.mag() > 0) 
		{
			steer.setMag(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		
		return steer;
	}

	//Update physical aspects
	protected void update(ArrayList<Actor> preys)
	{
		//Your maximum speed and acceleration is contingent upon several factors
		float actual_maxspeed = maxspeed;
		PVector actual_acceleration = acceleration.get();
		
		//If you're wounded you will move slower
		//	Because the weak are usually the first ones to go in the forest...
		actual_maxspeed *= life;

		//Run faster if you're freaking out about predators
		if (panicked)
		{
			actual_maxspeed *= 1.02f;
			actual_acceleration.mult(1.01f);
		}
		//If you're starving and you have preys around
		//	It's feeding time!
		else if (preys.size() > 0 && starving)
		{
			//Attack!
			Circle attack = new Circle(body.get(0).getPosition().x, body.get(0).getPosition().y, size_radius);
			boolean has_attacked = false;
			
			//Check the preys you are aware of
			for (Actor pvictim : preys)
			{
				//As well as every one of their body segments
				for (BodySegment segment : pvictim.getBody())
				{
					//If your attack hitbox intersects with one of their segments
					if (attack.overlaps(segment.getCircle()))
					{
						//If your attack cooldown is finished
						if (can_munch)
						{
							//If the victim is alive, chomp off some of its life
							if (pvictim.getLife() > 0)
							{
								//Adjust the damage according to the segment's damage model
								pvictim.setLife(pvictim.getLife() - attack_power * segment.getDamageModel());
								if (pvictim.getLife() < 0) pvictim.setLife(0);
							}
							//Otherwise if you're hungry
							else if (hunger > 0)
							{
								//Empty the carcass of its edible parts...
								pvictim.setFood(pvictim.getFood() - 0.05f);
								if (pvictim.getFood() <= 0)
								{
									pvictim.setFood(0);
									pvictim.dispose();
								}
								
								//Once you're not hungry anymore
								//	You stop eating and you don't feel like chasing prey around for a while
								hunger -= 0.05;
								if (hunger < 0)
								{
									hunger = 0;
									starving = false;
								}
							}
							
							actual_acceleration.mult(0);
							
							//Provide visual feedback
							ParticleEmitter.ParticleType splash_type;
							
							//If you're a wolf eating a caribou, blood flies everywhere
							if (pvictim.getType() == ActorType.CARIBOU) splash_type = ParticleEmitter.ParticleType.BLOOD;
							//But if you're a caribou munching on a flower, 
							//	there are pieces of vegetation instead
							else splash_type = ParticleEmitter.ParticleType.LEAF;
							
							//Spawn the particle emitter
							world.getParticleEmitters().add(new ParticleEmitter(segment.getPosition().get(), world, 8, splash_type, 1));
							
							//Set the attack cooldown
							Timer.schedule(new CanMunchTask(this), 1);
							can_munch = false;
							has_attacked = true;
						}
					}
					
					//You can only attack one body segment at a time
					if (has_attacked) break;
				}
				
				//You can only attack one actor at a time
				if (has_attacked) break;
			}
		}
		
		//Movement
		velocity.add(actual_acceleration);
		velocity.limit(actual_maxspeed);
		
		if (life > 0) position.add(velocity);
		acceleration.mult(0);
		
		//Your footsteps make noise
		if (!footfall)
		{
			footfall = true;
			movementNoise();
			Timer.schedule(new FootfallTask(this), (maxspeed * 2/3f) - (velocity.mag() / maxspeed));
		}
		
		//Update angle
		if (rotate_time >= 0.1f && velocity.mag() > 0)
		{
			rotate_angle = getDirectionAngle();
			rotate_time = 0;
		}
		
		//Update body segment positions
		if (velocity.mag() > 0)
		{
			for (BodySegment segment : body)
			{
				PVector dir = velocity.get();
				dir.normalize();
				dir.mult(segment.getOffset());
				dir.add(position);
				segment.setPosition(dir.get());
			}
		}
	}
	
	//Wrap around
	//(If you go outside the world's limits, you respawn at the other end)
	protected void borders()
	{
		if (position.x < -r) position.x = world.getSize().x+r;
		if (position.y < -r) position.y = world.getSize().y+r;
		if (position.x > world.getSize().x+r) position.x = -r;
		if (position.y > world.getSize().y+r) position.y = -r;
	}
}
