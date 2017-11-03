package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.World;


import processing.core.PVector;

public class WindChangeTask extends Task
{
	private World world;
	
	public WindChangeTask(World world)
	{
		this.world = world;
	}
	
	@Override
	public void run() 
	{
		world.getWind().setDirection(PVector.random2D());
	}
}
