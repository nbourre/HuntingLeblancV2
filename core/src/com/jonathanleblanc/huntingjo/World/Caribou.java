package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;


import java.util.HashMap;
import java.util.Map;

import processing.core.PVector;

//Caribou
//-------
//- Wanders around and eats flowers
//- Runs away from Hunters and Wolves

public class Caribou extends Animal 
{
	//Properties
	//----------
	
	private Map <AnimalState, Integer> states = new HashMap <AnimalState, Integer>();
	{
		states.put(AnimalState.EAT, AssetControler.ANIM_CARIBOU_EAT);
		states.put(AnimalState.ROLLOVER, AssetControler.ANIM_CARIBOU_DEATH);
		states.put(AnimalState.ROT, AssetControler.ANIM_CARIBOU_ROT);
	};
	
	//Utility methods
	//---------------
	
	//Build your skeleton
	//(Thick skull, vulnerable chest cavity, fat arse)
	@Override
	protected void buildSkeleton()
	{
		body.clear();
		
		body.add(new BodySegment(this, position.get(), 8, 23, 0.8f));
		body.add(new BodySegment(this, position.get(), 11, 4, 1.2f));
		body.add(new BodySegment(this, position.get(), 13, -19, 0.7f));
	}
	
	//Constructor
	//-----------
	
	public Caribou(PVector position, World world, Herd herd) 
	{
		super(position, ActorType.CARIBOU, world, herd);

		animal_state = AnimalState.EAT;
		
		maxspeed = 2;
		maxforce = 0.6f;
		density_scale = 0.45f;
		mass = MathUtils.random(80, 125);
		is_cover = true;
		
		food = 1 + MathUtils.random();
		max_food = food;
		
		buildSkeleton();
	}

	//Draw method
	//-----------
	
	@Override
	public void draw(SpriteBatch batch)
	{
		Sprite sprite;
		drawSprite(batch, sprite = new Sprite((TextureRegion) AssetControler.animations.get(states.get(animal_state)).getKeyFrame(state_time, true)), position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2, 0.5f, sprite.getWidth() / 2, sprite.getHeight() / 2, rotate_angle);
	}
}
