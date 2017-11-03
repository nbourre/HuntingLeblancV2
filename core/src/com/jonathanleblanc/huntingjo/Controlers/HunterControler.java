package com.jonathanleblanc.huntingjo.Controlers;


import com.jonathanleblanc.huntingjo.World.World;

import java.util.HashMap;
import java.util.Map;

//Hunter Controller
//-----------------
//- Processes input picked up by GameScreen
//- Calls the appropriate methods in Hunter actor

public class HunterControler
{
	//Properties
	//----------

	//Key States
	private enum Keys
	{
		RIGHT,
		UP,
		LEFT,
		DOWN,
		STEALTH,
		FIRE,
		RELOAD
	}

	static Map <Keys, Boolean> keys = new HashMap <HunterControler.Keys, Boolean>();
	{
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.LEFT, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.STEALTH, false);
		keys.put(Keys.FIRE, false);
		keys.put(Keys.RELOAD, false);
	};

	private World world;

	//Constructor
	public HunterControler(World world)
	{
		this.world = world;
	}

	//Pressed key events
	//Right
	public void rightPressed() 
	{
		keys.get(keys.put(Keys.RIGHT, true));
	}

	//Up
	public void upPressed() 
	{
		keys.get(keys.put(Keys.UP, true));
	}
	
	//Left
	public void leftPressed() 
	{
		keys.get(keys.put(Keys.LEFT, true));
	}

	//Down
	public void downPressed()
	{
		keys.get(keys.put(Keys.DOWN, true));
	}
	
	//Stealth
	public void stealthPressed()
	{
		keys.get(keys.put(Keys.STEALTH, true));
	}
	
	//Fire
	public void firePressed() 
	{
		keys.get(keys.put(Keys.FIRE, true));
	}
	
	//Reload
	public void reloadPressed() 
	{
		keys.get(keys.put(Keys.RELOAD, true));
	}
	
	//Released key events
	//Right
	public void rightReleased() 
	{
		keys.get(keys.put(Keys.RIGHT, false));
	}

	//Up
	public void upReleased() 
	{
		keys.get(keys.put(Keys.UP, false));
	}
	
	//Left
	public void leftReleased() 
	{
		keys.get(keys.put(Keys.LEFT, false));
	}

	//Down
	public void downReleased()
	{
		keys.get(keys.put(Keys.DOWN, false));
	}
	
	//Stealth
	public void stealthReleased()
	{
		keys.get(keys.put(Keys.STEALTH, false));
	}
	
	//Fire
	public void fireReleased() 
	{
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	//Reload
	public void reloadReleased() 
	{
		keys.get(keys.put(Keys.RELOAD, false));
	}
	
	//Process the input from the player
	public void update(float delta) 
	{
		processInput();
	}

	//Call the methods in Hunter actor
	private void processInput() 
	{
		if (keys.get(Keys.RIGHT))
		{
			world.getHunter().move_right();
		}
		
		if (keys.get(Keys.UP))
		{
			world.getHunter().move_up();
		}
		
		if (keys.get(Keys.LEFT))
		{
			world.getHunter().move_left();
		}
		
		if (keys.get(Keys.DOWN))
		{
			world.getHunter().move_down();
		}
		
		if (keys.get(Keys.STEALTH))
		{
			world.getHunter().setNoisiness(false);
		}
		else
		{
			world.getHunter().setNoisiness(true);
		}
		
		if (keys.get(Keys.RELOAD))
		{
			world.getHunter().do_reload();
		}
		
		if (keys.get(Keys.FIRE))
		{
			world.getHunter().do_fire();
		}
	}
}