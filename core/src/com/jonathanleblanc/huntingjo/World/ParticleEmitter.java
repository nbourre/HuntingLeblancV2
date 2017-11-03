package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PVector;

//Particle Emitter
//----------------
//- Spawns and manages a group of particles

public class ParticleEmitter 
{
	//Properties
	//----------
	
	public static enum ParticleType
	{
		SMOKE,
		WATER,
		BLOOD,
		LEAF,
		WHOCARES
	}
	
	private World world;
	
	private ArrayList <Particle> particles;
	
	private PVector position;

	private ParticleType type;
	
	private int size;
	private float scale;
	
	private boolean is_expired;
	
	//Utility methods
	//---------------

	public void spawnParticle(PVector velocity, World world, float duration)
	{
		float rscale = scale * MathUtils.random(0.8f, 1.2f);
		PVector varyPos = PVector.random2D();
		varyPos.mult(5);
		varyPos.add(position);
		
		if (type != null) particles.add(new Particle(varyPos, type, world, velocity, duration, rscale));
	}
	
	//Getter/Setter methods
	//---------------------
	
	public ArrayList <Particle> getParticles()
	{
		return particles;
	}
	
	public boolean isExpired()
	{
		return is_expired;
	}
	
	public PVector getPosition()
	{
		return position;
	}
	
	public World getWorld() 
	{
		return world;
	}
	
	//Constructor
	//-----------
	
	public ParticleEmitter(PVector position, World world, int size, ParticleType type,  float scale)
	{
		particles = new ArrayList<Particle> ();
		
		this.position = position;
		this.world = world;
		this.size = size;
		this.scale = scale;
		this.type = type;
		
		is_expired = false;
		
		for (int i = 0; i < size; i ++)
		{
			Random random = new Random();
			PVector vel = new PVector((float) random.nextGaussian(), (float) random.nextGaussian());
			vel.normalize();
			vel.mult(0.1f);
			spawnParticle(vel, world, 1);
		}
	}
	
	//Update methods
	//--------------
	
	public void update(float delta)
	{
		ArrayList <Particle> expired_particles = new ArrayList <Particle> ();
		
		for (Particle particle : particles)
		{
			particle.update(delta);
			if (particle.isDisposed()) expired_particles.add(particle);
		}
		
		//Get rid of expired particles
		for (Particle particle : expired_particles)
		{
			particles.remove(particle);
		}
		
		//If none of your particles are left, the World gets rid of you
		if (particles.size() == 0 && size == 0) is_expired = true;
	}
}
