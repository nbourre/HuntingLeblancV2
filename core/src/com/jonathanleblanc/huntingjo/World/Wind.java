package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Tasks.WindChangeTask;

import java.util.Random;

import processing.core.PVector;

public class Wind extends Actor
{
	//Properties
	//----------
	
	final private float AIR_FLUID_DENSITY = 1.225f;
	
	private PVector acceleration;
	private PVector velocity;
	private PVector direction;
	
	private float maxforce;
	private float maxspeed;
	
	//Utility methods
	//---------------
	
	public PVector getDragForce(PVector v, float A, float CD)
	{
		float vel = v.mag();
		PVector force = v.get();
		force.normalize();
		
		//pix/m conversion
		force.div(100);
		
		//Drag Force equation
		force.mult(0.5f * (AIR_FLUID_DENSITY * (vel*vel) * CD * A));
		
		return force;
	}
	
	//Getter/Setter methods
	//---------------------
	
	public void setDirection(PVector direction)
	{
		this.direction = direction;
		
		Timer.schedule(new WindChangeTask(world), 1 + MathUtils.random(2));
	}
	
	public PVector getVelocity()
	{
		return velocity;
	}

	//Constructor
	//-----------
	
	public Wind(PVector position, World world) 
	{
		super(position, ActorType.WIND, world, false, false);
		
		acceleration = new PVector(0, 0, 0);
		velocity = PVector.random2D();
		direction = PVector.random2D();

		is_visible = false;
		maxspeed = 0.75f;
		maxforce = 0.001f;
		
		Timer.schedule(new WindChangeTask(world), 1 + MathUtils.random(2));
	}

	//Update methods
	//--------------
	
	public void update(float delta)
	{
		//The magnitude of the wind is a Gaussian noise
		Random random = new Random();
		acceleration.add(seek(direction.get()));
		acceleration.setMag((float) (random.nextGaussian() * maxforce));
		
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		acceleration.mult(0);
	}
	
	//Steer the acceleration of the wind toward the given direction
	protected PVector seek(PVector target) 
	{
		PVector desired = PVector.sub(target, position);
		desired.setMag(maxspeed);
		
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce);
	
		return steer;
	}	
}
