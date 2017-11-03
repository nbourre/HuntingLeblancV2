package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Actor;


public class ExpirationTask extends Task
{
	private Actor victim;

	public ExpirationTask(Actor victim)
	{
		this.victim = victim;
	}
	
	@Override
	public void run() 
	{
		victim.dispose();
	}
}
