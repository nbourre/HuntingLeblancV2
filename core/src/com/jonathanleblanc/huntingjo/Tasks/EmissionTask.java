package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Actor;

public class EmissionTask extends Task
{
	private Actor.ActorType type;
	private Actor emitter;
	private float magnitude;
	private float duration;
	private int noise_index;

	public EmissionTask(Actor.ActorType type, Actor emitter, float magnitude, float duration, int noise_index)
	{
		this.type = type;
		this.emitter = emitter;
		this.magnitude = magnitude;
		this.duration = duration;
		this.noise_index = noise_index;
	}
	
	@Override
	public void run() 
	{
		if (emitter.isDisposed()) cancel();
		else emitter.emitAlert(type, magnitude, duration, noise_index);
	}

}
