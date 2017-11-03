package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;


import processing.core.PVector;

//Bullet
//- Shoots through the air at hypersonic speeds
//- Inflicts gruesome wounds to any Animal unlucky enough to get hit

public class Bullet extends Actor
{
	//Properties
	//----------
	
	private Hunter shooter;
	private World world;
	
	private PVector velocity;
	private PVector drag;
	
	private float attack_power;
	private float trajectory_angle;
	private float distance_travelled;
	private float muzzle_velocity;
	
	//Utility methods
	//---------------
	
	//Convert velocity in m/s to pixels/gameloop iterations
	private PVector convert_velocity(PVector deceleration, float delta)
	{
		PVector converted_velocity = velocity.get(), converted_deceleration = deceleration.get();
		
		//Conversion of vectors in meters/sec to pixels/sec (100 pixels = 1 m)
		//Conversion of vectors in pixels/sec to pixels/gameloop iteration
		converted_velocity.mult(delta * 100);
		converted_deceleration.mult(delta * 100);
		
		converted_velocity.sub(converted_deceleration);

		return converted_velocity;
	}
	
	//Getter/Setter methods
	//---------------------
	
	public PVector getPosition()
	{
		return position;
	}
	
	//Constructor
	//-----------
	
	public Bullet(Hunter shooter, World world, PVector position, PVector velocity, PVector drag, float attack_power)
	{
		super(position, ActorType.BULLET, world, false, false);
		
		is_solid = false;
		
		this.shooter = shooter;
		this.world = world;
		
		this.position = position;
		this.velocity = velocity;
		muzzle_velocity = velocity.mag();
		this.drag = drag;
		this.attack_power = attack_power;
		
		trajectory_angle = MathUtils.radiansToDegrees * (MathUtils.atan2((position.y + velocity.y) - position.y, (position.x + velocity.x) - position.x));
		if (trajectory_angle < 0) trajectory_angle = 360 + trajectory_angle;
		
		distance_travelled = 0;
	}
	
	//Update methods
	//--------------
	
	//Update movement
	@Override
	public void update(float delta)
	{
		//Initialize impact segment
		BodySegment impact = null;
		
		//Keep position to calculate mileage after movement
		PVector prev_pos = position.get();
		
		//Calculate the drag force 
		drag = world.getWind().getDragForce(velocity, shooter.BULLET_FRONTAL_AREA, shooter.BULLET_DRAG_COEFFICIENT);
		
		//Check if there's a collision in the future
		impact = discrete_collision_check(drag, delta);
		
		//If there aren't any future impacts
		if (impact == null)
		{
			//Apply wind force
			position.add(world.getWind().getVelocity());

			//Apply air friction (drag force)
			position.add(convert_velocity(drag, delta));
		}
		//Otherwise react accordingly
		else impactActor(impact);
		
		//Disappear if you go out of the world's bounds
		limits();
		
		//Update the mileage
		float step_dist = PVector.dist(prev_pos, position);
		distance_travelled += step_dist;
	}
	
	//Discrete collision checking algorithm
	private BodySegment discrete_collision_check(PVector deceleration, float delta)
	{
		//First we convert the velocity
		PVector test_vel = convert_velocity(deceleration, delta);
		
		//Travel in time, testing the positions ahead pixel-per-pixel
		for (int i = 0; i < test_vel.mag(); i ++)
		{
			PVector test_pos = position.get();
			PVector dir = velocity.get();
			dir.normalize();
			dir.setMag(i);
			test_pos.add(dir);

			//If the bullet meets an actor
			for (Actor actor : world.getActors())
			{
				//Check if the actor isn't the shooter and whether or not it can get hit
				if (!actor.equals(shooter) && actor.getMaterialType() != MaterialType.AIR)
				{
					//Go through the actor's body segments
					for (BodySegment segment : actor.getBody())
					{
						//If the test point is inside the segment
						if (segment.containsPoint(test_pos))
						{
							//We have an impact
							PVector test_dist = velocity.get();
							test_dist.normalize();
							test_dist.mult(i);
							position.add(test_dist);
							
							return segment;
						}
					}
				}
			}
		}
		
		//If there's nothing ahead, move on
		return null;
	}
	
	//Process impacts
	private void impactActor(BodySegment impact)
	{
		switch (impact.getOwner().getMaterialType())
		{
			//Animal impact
			case FLESH:
			{
				//Adjust the attack power according to the velocity
				float adjusted_attack = attack_power;
				attack_power *= velocity.mag() / muzzle_velocity;
				
				if (impact.getOwner().getLife() > 0)
				{
					//Adjust the attack power according to the impacted segment's damage model
					//Deal the damage
					impact.getOwner().setLife(impact.getOwner().getLife() - adjusted_attack * impact.getDamageModel());
					if (impact.getOwner().getLife() < 0) impact.getOwner().setLife(0);
				}
				
				//Emit some blood particles
				world.getParticleEmitters().add(new ParticleEmitter(position.get(), world, 3 + MathUtils.random(3), Actor.impactTypes.get(MaterialType.FLESH), 1f));
				
				//The bullet penetrates the target
				penetrate(impact);
				break;
			}
			
			case STONE:
			{
				//Emit some smoke
				world.getParticleEmitters().add(new ParticleEmitter(position.get(), world, 3 + MathUtils.random(3), Actor.impactTypes.get(MaterialType.WOOD), 2/3f));
				
				//The bullet glances off
				ricochet(impact);
				break;
			}
			
			default:
			{
				world.getParticleEmitters().add(new ParticleEmitter(position.get(), world, 3 + MathUtils.random(3), Actor.impactTypes.get(impact.getOwner().getMaterialType()), 2/3f));
				penetrate(impact);
			}
		}
	}
	
	//Glance off rocks
	private void ricochet(BodySegment impact)
	{
		//Reduce the velocity
		float vel_mag = velocity.mag() * 1/3f;
		
		//If the bullet is still in flight
		if (vel_mag > 10)
		{
			PVector pos = impact.getPosition().get();
			PVector rpos = new PVector(MathUtils.random(-5f, 5f), MathUtils.random(-5f, 5f));
			pos.add(rpos);
			
			//Get the deflection vector
			PVector deflection = PVector.sub(position, pos);
			deflection.normalize();
			
			//Apply the deflection vector
			velocity.normalize();
			velocity.add(deflection);
			velocity.setMag(vel_mag);

			//Movement
			position.add(velocity);
			
			//Update the trajectory angle for drawing the tracer
			trajectory_angle = MathUtils.radiansToDegrees * (MathUtils.atan2((position.y + velocity.y) - position.y, (position.x + velocity.x) - position.x));
			if (trajectory_angle < 0) trajectory_angle = 360 + trajectory_angle;
		}
		//If the bullet is too slow, it disappears
		else dispose();
	}
	
	//Penetrate through wood and animal bodies
	private void penetrate(BodySegment impact)
	{
		//Find the exit point by going through the body pixel by pixel
		for (int i = 0; i < impact.getSizeRadius(); i ++)
		{
			PVector test_pos = position.get();
			PVector dir = velocity.get();
			dir.add(new PVector(MathUtils.random(-5, 5), MathUtils.random(-5, 5)));
			dir.normalize();
			dir.setMag(i);
			test_pos.add(dir);

			//If you've gone through the body
			if (!impact.containsPoint(test_pos))
			{
				//You've found the exit point
				PVector test_dist = velocity.get();
				test_dist.normalize();
				test_dist.mult(i);
				position.add(test_dist);

				velocity.mult(impact.getOwner().getDensityScale());
				break;
			}
		}
		
		//If the bullet is still in flight
		if (velocity.mag() > 10)
		{
			//Emit a puff of particles
			world.getParticleEmitters().add(new ParticleEmitter(position.get(), world, 3 + MathUtils.random(3), Actor.impactTypes.get(impact.getOwner().getMaterialType()), 2/3f));
			
			//Keep moving
			position.add(velocity);
		}
		else dispose();
	}
	
	//Check if you're out of the world bounds and disappear if you are
	public void limits()
	{
		if (position.x > world.getSize().x || position.x < 0 || position.y > world.getSize().y || position.y < 0) dispose();
	}
	
	//Draw method
	//-----------
	
	//Draw the tracer
	@Override
	public void draw(SpriteBatch batch)
	{
		float tracer_scale = 1;
		if (distance_travelled < 100) tracer_scale = distance_travelled / 100;
		else if (velocity.mag() < 100) tracer_scale = velocity.mag() / 100;
		
		drawSprite(batch, new Sprite(AssetControler.textures.get(AssetControler.TEXT_BULLET)), position.x - 100, position.y, tracer_scale, 100, 0, trajectory_angle);
	}
}
