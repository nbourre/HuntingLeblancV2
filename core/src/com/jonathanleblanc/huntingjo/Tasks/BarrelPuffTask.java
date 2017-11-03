package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Hunter;


public class BarrelPuffTask extends Task
{
	private Hunter shooter;

	public BarrelPuffTask(Hunter shooter)
	{
		this.shooter = shooter;
	}
	
	@Override
	public void run() 
	{
		if (shooter.isDisposed()) cancel();
		else shooter.puffBarrel();
	}

}
