package com.jonathanleblanc.huntingjo.World;

import java.util.ArrayList;

import processing.core.PVector;

public class Herd 
{
	//Properties
	//----------
	
	protected World world;
	
	protected PVector goal;
	
	protected float separation_radius = 100;
	protected float alignment_radius = 500;
	protected float cohesion_radius = 500;
	protected float flight_radius = 500;
	protected float chase_radius = 600;
	
	public ArrayList<Animal> animals;
	
	//Utility methods
	//---------------
	
	public void addAnimal(Animal a) 
	{
		world.spawnActor(a);
		animals.add(a);
	}

	//Getter/Setter methods
	//---------------------
	
	public float getSeparationRadius()
	{
		return separation_radius;
	}	
	
	public float getAlignmentRadius()
	{
		return alignment_radius;
	}
	
	public float getCohesionRadius()
	{
		return cohesion_radius;
	}

	public float getFlightRadius()
	{
		return flight_radius;
	}
	
	public float getChaseRadius()
	{
		return chase_radius;
	}
	
	public ArrayList<Animal> getAnimals()
	{
		return animals;
	}
	
	//Constructor
	//-----------
	
	public Herd(World world) 
	{
		animals = new ArrayList<Animal>(); // Initialize the ArrayList
		this.world = world;
	}
	
	//Update methods
	//--------------
	
	public ArrayList<Actor> run(float delta)
	{
		ArrayList<Actor> expired_boids = new ArrayList<Actor>();
		
		for (Animal b : animals)
		{
			b.run(animals, world.getActors(), world.getAlerts(), delta);  // Passing the entire list of boids to each boid individually
			if (b.isDisposed()) expired_boids.add(b);
		}
		
		for (Actor eb : expired_boids)
		{
			animals.remove(eb);
		}
		
		return expired_boids;
	}
}