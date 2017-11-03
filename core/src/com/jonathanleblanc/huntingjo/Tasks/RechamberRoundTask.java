package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Hunter;

public class RechamberRoundTask extends Task
{
	private Hunter shooter;
	
	public RechamberRoundTask(Hunter shooter)
	{
		this.shooter = shooter;
	}
	
	@Override
	public void run() 
	{
		shooter.chamberRound();
	}

}
