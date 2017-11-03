package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Animal;

public class ChilloutTask extends Task
{
	private Animal patient;

	public ChilloutTask(Animal patient)
	{
		this.patient = patient;
	}
	
	@Override
	public void run() 
	{
		if (patient.isDisposed()) cancel();
	}

}
