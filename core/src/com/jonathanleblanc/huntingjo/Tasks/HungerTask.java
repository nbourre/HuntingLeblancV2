package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Animal;

public class HungerTask extends Task
{
	private Animal victim;

	public HungerTask(Animal victim)
	{
		this.victim = victim;
	}
	
	@Override
	public void run() 
	{
		if (victim.isDisposed()) cancel();
		else victim.getHungrier();
	}
}
