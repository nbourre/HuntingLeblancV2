package com.jonathanleblanc.huntingjo.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.jonathanleblanc.huntingjo.Controlers.AssetControler;

import java.util.HashMap;
import java.util.Map;

import processing.core.PVector;

//Wolf
//----
//- Wanders around and chases Caribous
//- Runs away from the Hunter

public class Wolf extends Animal
{
	//Properties
	//----------
	
	private Map <AnimalState, Integer> states = new HashMap <AnimalState, Integer>();
	{
		states.put(AnimalState.EAT, AssetControler.ANIM_WOLF_WAGTAIL);
		states.put(AnimalState.ROLLOVER, AssetControler.ANIM_WOLF_WAGTAIL);
		states.put(AnimalState.ROT, AssetControler.ANIM_WOLF_WAGTAIL);
	}
	
	//Utility methods
	//---------------
	
	@Override
	protected void buildSkeleton()
	{
		body.clear();
		
		body.add(new BodySegment(this, position.get(), 6, 20, 1.4f));
		body.add(new BodySegment(this, position.get(), 8, 6, 1.3f));
		body.add(new BodySegment(this, position.get(), 9, -9, 1f));
	}
	
	//Constructor
	//-----------
	
	public Wolf(PVector position, World world, Herd pack) 
	{
		super(position, ActorType.WOLF, world, pack);
		
		type = ActorType.WOLF;
		
		size_radius = 10;
		body.get(0).setSizeRadius(size_radius);
		food = max_food;
		
		maxspeed = 3;
		maxforce = 0.25f;
		density_scale = 0.59f;
		mass = MathUtils.random(40, 50);
		
		attack_power = 0.2f;
		buildSkeleton();
	}
	
	//Draw methods
	//------------

	@Override
	public void draw(SpriteBatch batch)
	{
		drawSprite(batch, new Sprite((TextureRegion) AssetControler.animations.get(states.get(animal_state)).getKeyFrame(state_time, true)), position.x - 70, position.y - 21, 0.5f, 70, 21, rotate_angle);
	}
}
