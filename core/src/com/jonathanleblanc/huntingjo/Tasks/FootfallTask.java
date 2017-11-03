package com.jonathanleblanc.huntingjo.Tasks;

import com.badlogic.gdx.utils.Timer.Task;
import com.jonathanleblanc.huntingjo.World.Animal;

public class FootfallTask extends Task
{
	private Animal walker;

	public FootfallTask(Animal walker)
	{
		this.walker = walker;
	}
	
	@Override
	public void run() 
	{
		if (walker.isDisposed()) cancel();
		else walker.setFootfall(false);
	}

}
