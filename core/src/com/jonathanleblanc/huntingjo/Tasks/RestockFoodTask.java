package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Actor;


public class RestockFoodTask extends Task
{
	private Actor foodsource;

	public RestockFoodTask(Actor foodsource)
	{
		this.foodsource = foodsource;
	}
	
	@Override
	public void run() 
	{
		if (foodsource.isDisposed()) cancel();
		else foodsource.setFood(foodsource.getFood() + 1/30);
	}
}
