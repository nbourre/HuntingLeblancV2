package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Tasks.ExpirationTask;


import processing.core.PVector;

//Alert
//-----
//- Consists of a smell or a noise
//- Is emitted by smelly/noisy actors
//- Animals detect other significant Actors by colliding with their smells/noises

public class Alert extends Actor
{
	//Properties
	//----------
	private Actor source;
	private Color debug_code;

	private float magnitude;
	private float max_magnitude;
	
	private boolean is_heard;
	private int noise_index;
	
	//Getter/Setter methods
	//---------------------
	
	public float getMagnitude()
	{
		return magnitude;
	}
	public Actor getSource()
	{
		return source;
	}
	public Color getDebugCode()
	{
		return debug_code;
	}
	
	//Constructor
	//-----------
	
	public Alert(PVector position, ActorType type, World world, Actor source, float magnitude, float duration, int noise_index)
	{
		super(position, type, world, false, false);
		
		is_visible = false;
		is_heard = false;
		this.noise_index = noise_index;
		
		max_magnitude = magnitude;
		this.magnitude = magnitude;
		this.type = type;
		this.source = source;
		
		Timer.schedule(new ExpirationTask(this), duration);
		
		if (this.type == ActorType.SMELL) debug_code = new Color(0.3f, 0.3f, 1, 0.2f);
		else if (this.type == ActorType.NOISE)
		{
			debug_code = new Color(1f, 0.3f, 1, 0.2f);
			this.magnitude = max_magnitude / 10;
		}
		else debug_code = new Color(0.3f, 1, 1, 0.2f);
	}
	
	//Update method
	//-------------
	
	@Override
	public void update(float delta)
	{
		//Apply the wind force
		position.add(world.getWind().getVelocity());
		
		//If you are a noise
		if (type == ActorType.NOISE)
		{
			//Your "wave" travels throughout the world in addition to being carried by the wind
			if (magnitude < max_magnitude) magnitude += max_magnitude / 10;
			//Once you reach your maximum range you fade out
			else dispose();
			
			//Once you reach the Hunter's ears and haven't been heard yet
			if (world.getHunter().getPosition().dist(position) - magnitude <= 400 && noise_index != -1 && !is_heard)
			{
				//Adjust the volume
				//(CounterStrike 1.6 sounds are freaking loud...)
				//Get the distance between you and the Hunter's ears
				float volume = 0.2f, dist = world.getHunter().getPosition().dist(position) - magnitude;
				
				//Adjust the volume depending on how far away you are
				volume *= 1.5 - (dist / 400);
				
				//Play the sound
				AssetControler.sounds.get(noise_index).play(volume);
				//And only play it once
				is_heard = true;
			}
		}
	}
}
