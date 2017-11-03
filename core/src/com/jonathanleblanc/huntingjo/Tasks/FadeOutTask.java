package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Particle;


public class FadeOutTask extends Task
{
	private Particle particle;

	public FadeOutTask(Particle particle)
	{
		this.particle = particle;
	}
	
	@Override
	public void run() 
	{
		particle.fadeOut();
	}
}
