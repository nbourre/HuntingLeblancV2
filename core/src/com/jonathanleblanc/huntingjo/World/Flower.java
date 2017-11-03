package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;
import com.jonathanleblanc.huntingjo.Tasks.RestockFoodTask;

import java.util.ArrayList;

import processing.core.PVector;

//Flower
//------
//- Emits a flowery smell
//- Is eaten by Caribous

public class Flower extends Tree
{
	//Constructor
	//-----------
	
	public Flower(PVector position, World world, int numStems) 
	{
		super(position, world, numStems);
		
		material_type = MaterialType.AIR;
		type = ActorType.FLOWER;
		texture_index = AssetControler.TEXT_FLOWERPETAL;
		
		limbs = new ArrayList<TreeLimb> ();
		
		for (int i = 0; i < numStems; i ++)
		{
			PVector pos = PVector.random2D();
			pos.mult(7 + MathUtils.random(4));
			pos.add(position);
			limbs.add(new FlowerPetals(pos, world));
		}
		
		//flowers don't really weigh 200 kg
		//they are given this value in order for them not to be swept away by animals and hunters
		mass = 200;
		food = 1;
		Timer.schedule(new RestockFoodTask(this), 1, 1);
		
		size_radius = 5;
		buildSkeleton();
	}
	
	//Draw method
	//-----------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		Sprite sprite;
		
		//Draw the "trunk"
		//	Individual stems connected to the base of the flower and to an individual petal
		for (TreeLimb petals : limbs)
		{
			sprite = new Sprite(AssetControler.textures.get(AssetControler.TEXT_FLOWERSTALK));
			
			//Find the distance between the base of the flower and the petal
			float drawscale = PVector.dist(position, petals.getPosition());
			
			//Divide by the length of the sprite to find the scale
			drawscale /= 16;
			
			sprite.setPosition(position.x - 2, position.y - 2);
			sprite.setOrigin(2, 2);
			
			//Find the angle between the position and the petal
			float angle = MathUtils.radiansToDegrees * (MathUtils.atan2(petals.getPosition().y - position.y, petals.getPosition().x - position.x));
			if (angle < 0) angle = 360 + angle;
			
			sprite.setScale(drawscale, 1);
			sprite.rotate(angle);
			
			//Draw the stem
			sprite.draw(batch);
		}
		
		//Draw the individual petals
		draw_limbs(batch);
	}
}
