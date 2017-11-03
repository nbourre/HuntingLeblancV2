package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Animal;


public class CanMunchTask extends Task
{
	private Animal muncher;

	public CanMunchTask(Animal patient)
	{
		this.muncher = patient;
	}
	
	@Override
	public void run() 
	{
		if (muncher.isDisposed()) cancel();
		else muncher.canMunch();
	}

}
