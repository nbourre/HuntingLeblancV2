package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Actor;
import com.jonathanleblanc.huntingjo.World.World;

public class RespawnTask extends Task
{
	private World world;
	private Actor.ActorType type;
	
	public RespawnTask(World world, Actor.ActorType type)
	{
		this.world = world;
		this.type = type;
	}
	
	@Override
	public void run()
	{
		world.respawnActor(type);
	}
}
