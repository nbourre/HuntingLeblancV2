package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.ParticleEmitter;

public class ParticleEmissionTask extends Task
{
	private ParticleEmitter emitter;
	private float duration;
	
	public ParticleEmissionTask(ParticleEmitter emitter, float duration) 
	{
		this.emitter = emitter;
		this.duration = duration;
	}

	@Override
	public void run() 
	{
		if (emitter.isExpired()) cancel();
		else emitter.spawnParticle(emitter.getPosition().get(), emitter.getWorld(), duration);
	}
	
}
