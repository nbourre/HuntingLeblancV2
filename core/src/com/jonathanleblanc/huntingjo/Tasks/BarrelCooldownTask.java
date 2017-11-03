package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Hunter;

public class BarrelCooldownTask extends Task
{
	private Hunter shooter;

	public BarrelCooldownTask(Hunter shooter)
	{
		this.shooter = shooter;
	}
	
	@Override
	public void run() 
	{
		if (shooter.isDisposed()) cancel();
		else shooter.cooldownBarrel();
	}

}
